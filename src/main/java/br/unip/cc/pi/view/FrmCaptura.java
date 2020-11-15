package br.unip.cc.pi.view;

import org.opencv.core.*;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.FisherFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FrmCaptura extends JFrame {

    private final JLabel imagemCamera;
    private final FisherFaceRecognizer fisherFaceRecognizer;
    private ScheduledExecutorService timer;
    private final VideoCapture capture;
    private boolean cameraActive;
    private final CascadeClassifier faceCascade;
    private int absoluteFaceSize;

    public FrmCaptura() throws HeadlessException {
        this.capture = new VideoCapture();
        this.faceCascade = new CascadeClassifier();
        this.absoluteFaceSize = 0;
        this.faceCascade.load("classificadores\\lbpcascades\\lbpcascade_frontalface.xml");

        fisherFaceRecognizer = FisherFaceRecognizer.create();

        imagemCamera = new JLabel("");
        imagemCamera.setBounds(0, 0, 800, 600);
        add(imagemCamera);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
        pack();
        setLocationRelativeTo(null);
    }

    public void startCamera() {
        this.capture.open(0);

        Runnable frameGrabber = new Runnable() {
            @Override
            public void run()
            {
                // effectively grab and process a single frame
                Mat frame = grabFrame();
                // convert and show the frame
                BufferedImage imageToShow = mat2Image(frame);
                updateImageView(imageToShow);
            }
        };

        this.timer = Executors.newSingleThreadScheduledExecutor();
        this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
    }

    public BufferedImage mat2Image(Mat frame)
    {
        return matToBufferedImage(frame);
    }

    private void updateImageView(BufferedImage image){
        EventQueue.invokeLater(()-> imagemCamera.setIcon(new ImageIcon(image)));
    }

    private BufferedImage matToBufferedImage(Mat original) {
        // init
        BufferedImage image = null;
        int width = original.width(), height = original.height(), channels = original.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        if (original.channels() > 1)
        {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        }
        else
        {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }


    private Mat grabFrame() {
        Mat frame = new Mat();
        try
        {
            this.capture.read(frame);

            if (!frame.empty())
            {
                this.detectAndDisplay(frame);
            }

        }
        catch (Exception e)
        {
            System.err.println("Exception during the image elaboration: " + e);
            e.printStackTrace();
        }

        return frame;
    }

    private void detectAndDisplay(Mat frame)
    {

        if (frame.empty()) return;

        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height, in our case)
        if (this.absoluteFaceSize == 0)
        {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0)
            {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        // detect faces
        this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());

        // each rectangle in faces is a face: draw them!
        Rect[] facesArray = faces.toArray();

        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
            if (0 <= facesArray[i].x
                    && 0 <= facesArray[i].width
                    && facesArray[i].x + facesArray[i].width <= frame.cols()
                    && 0 <= facesArray[i].y
                    && 0 <= facesArray[i].height
                    && facesArray[i].y + facesArray[i].height <= frame.rows()) {
                Imgcodecs.imwrite( i + ".jpg", frame.submat(facesArray[i]));
                System.out.println("ENTRO");
            }
        }

    }
}
