package br.unip.cc.pi.view;

import br.unip.cc.pi.view.listener.FaceCaptureListener;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_imgproc.*;


public class JDialogCaptureFace extends JDialog {

    public static final int WINDOW_WIDTH = 600;
    public static final int WINDOW_HEIGHT = 400;

    private CascadeClassifier classifier;
    private FrameGrabber grabber;
    private boolean grabberIsStart = false;
    private boolean isCapturing = false;

    private final List<FaceCaptureListener> listeners;

    public JDialogCaptureFace() {
        this.listeners = new ArrayList<>();
    }

    private final SwingWorker<Void, String> captureFacesSwingWorker = new MySwingWorker();

    private final class MySwingWorker extends SwingWorker<Void, String> {

        private final Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 16);

        @Override
        protected Void doInBackground() throws Exception {
            try {
                publish("Carregando classificador");
                carregaClassificador();

                publish("Buscando câmera");
                grabber = VideoInputFrameGrabber.createDefault(0);
                captureFaces();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
            return null;
        }

        private void captureFaces() {
            try {
                publish("Iniciando câmera");
                grabber.start();
                grabberIsStart = true;
                OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
                Mat grabbedImage = converter.convert(grabber.grab());
                isCapturing = true;
                int height = grabbedImage.rows();
                int width = grabbedImage.cols();

                Mat grayImage = new Mat(height, width, opencv_core.CV_8UC1);

                while (isVisible() && isCapturing && (grabbedImage = converter.convert(grabber.grab())) != null) {

                    Size scaleSize = new Size(WINDOW_WIDTH, WINDOW_HEIGHT);
                    Mat resizeImage = new Mat();
                    opencv_imgproc.resize(grabbedImage, resizeImage, scaleSize , 0, 0, INTER_AREA);

                    cvtColor(resizeImage, grayImage, CV_BGR2GRAY);

                    Frame frame = converter.convert(resizeImage);
                    Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();

                    RectVector faces = new RectVector();
                    classifier.detectMultiScale(grayImage, faces);

                    if (faces.size() < 1) continue;

                    BufferedImage bufferedImage = java2DFrameConverter.getBufferedImage(frame);
                    Graphics graphics = getGraphics();
                    if (graphics == null) continue;
                    graphics.drawImage(bufferedImage, 0, 0, 600, 400, null);
                    graphics.setColor(Color.GREEN);

                    List<BufferedImage> facesList = new ArrayList<>();
                    for (Rect rect : faces.get()) {
                        graphics.drawRect(rect.x(), rect.y(), rect.width(), rect.height());
                        BufferedImage bufferedFace = bufferedImage.getSubimage(rect.x(), rect.y(), rect.width(), rect.height());
                        facesList.add(bufferedFace);
                    }
                    listeners.forEach( l -> l.onFaceCapture(facesList));
                    graphics.dispose();
                }

                publish("");
            } catch (IOException e) {
                //FAÇA ALGO
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }

        @Override
        protected void process(List<String> chunks) {
            Graphics graphics = getGraphics();
            if (graphics == null || chunks.size() < 1) return;

            FontMetrics fontMetrics = getFontMetrics(font);

            int stringWidth = fontMetrics.stringWidth(chunks.get(0));

            graphics.setColor(new Color(46, 204, 113));
            graphics.setFont(font);
            graphics.clearRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
            graphics.drawString(chunks.get(0), WINDOW_WIDTH/2 - stringWidth/2, WINDOW_HEIGHT/2 - 8);
            graphics.dispose();
        }

        @Override
        protected void done() {
            super.done();
            closeVideoCapture();
        }
    }

    private void closeVideoCapture() {
        try {
            if (grabberIsStart) {
                grabber.close();
                grabberIsStart = false;
            }
        } catch (FrameGrabber.Exception exception) {
            exception.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Não possível desligar a câmera", "", JOptionPane.WARNING_MESSAGE);
        }
    }


    private void carregaClassificador() throws IOException {
        //Busca Classificador
        URL url = new URL("https://raw.github.com/opencv/opencv/master/data/haarcascades/haarcascade_frontalface_alt.xml");
        File file = Loader.cacheResource(url);
        String classifierName = file.getAbsolutePath();
        classifier = new CascadeClassifier(classifierName);
    }

    public void createAndShow() {
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        setBackground(new Color(52, 73, 94));

        this.addWindowListener(windowClosed);
        this.setVisible(true);

        captureFacesSwingWorker.execute();
    }

    public void addFaceCaptureListener(FaceCaptureListener faceCaptureListener) {
        this.listeners.add(faceCaptureListener);
    }

    private final WindowAdapter windowClosed = new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
            super.windowClosed(e);
            closeVideoCapture();
        }
    };

    public void stop() {
        isCapturing = false;
    }

    public void retart() {
        new MySwingWorker().execute();
    }
}
