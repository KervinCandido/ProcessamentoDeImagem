package br.unip.cc.pi;

import br.unip.cc.pi.model.Person;
import br.unip.cc.pi.service.FaceRecognizerService;
import br.unip.cc.pi.service.PersonService;
import br.unip.cc.pi.view.FrmHome;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.indexer.DoubleIndexer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.*;

import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_calib3d.*;

import static org.bytedeco.opencv.global.opencv_core.CV_64FC1;

public class App {


    public static void main(String[] args) throws Exception {
        Runnable treinamento = () -> {
            List<Person> people = new PersonService().findAll();
            FaceRecognizerService faceRecognizerService = new FaceRecognizerService();
            faceRecognizerService.train(people);
            System.out.println("TREINO COMPLETO");
        };
        new Thread(treinamento).start();

        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());

            Runnable homeRunnable = () -> {
                FrmHome home = new FrmHome();
                EventQueue.invokeLater(home::createAndShow);
            };
            new Thread(homeRunnable, "home").start();
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void teste02(String[] args) throws IOException {
        String classifierName = null;
        if (args.length > 0) {
            classifierName = args[0];
        } else {
            URL url = new URL("https://raw.github.com/opencv/opencv/master/data/haarcascades/haarcascade_frontalface_alt.xml");
            File file = Loader.cacheResource(url);
            classifierName = file.getAbsolutePath();
        }

        // We can "cast" Pointer objects by instantiating a new object of the desired class.
        CascadeClassifier classifier = new CascadeClassifier(classifierName);
        if (classifier == null) {
            System.err.println("Error loading classifier file \"" + classifierName + "\".");
            System.exit(1);
        }

        // The available FrameGrabber classes include OpenCVFrameGrabber (opencv_videoio),
        // DC1394FrameGrabber, FlyCapture2FrameGrabber, OpenKinectFrameGrabber, OpenKinect2FrameGrabber,
        // RealSenseFrameGrabber, RealSense2FrameGrabber, PS3EyeFrameGrabber, VideoInputFrameGrabber, and FFmpegFrameGrabber.
        FrameGrabber grabber = VideoInputFrameGrabber.createDefault(0);
        grabber.start();

        // CanvasFrame, FrameGrabber, and FrameRecorder use Frame objects to communicate image data.
        // We need a FrameConverter to interface with other APIs (Android, Java 2D, JavaFX, Tesseract, OpenCV, etc).
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

        // FAQ about IplImage and Mat objects from OpenCV:
        // - For custom raw processing of data, createBuffer() returns an NIO direct
        //   buffer wrapped around the memory pointed by imageData, and under Android we can
        //   also use that Buffer with Bitmap.copyPixelsFromBuffer() and copyPixelsToBuffer().
        // - To get a BufferedImage from an IplImage, or vice versa, we can chain calls to
        //   Java2DFrameConverter and OpenCVFrameConverter, one after the other.
        // - Java2DFrameConverter also has static copy() methods that we can use to transfer
        //   data more directly between BufferedImage and IplImage or Mat via Frame objects.
        Mat grabbedImage = converter.convert(grabber.grab());
        int height = grabbedImage.rows();
        int width = grabbedImage.cols();

        // Objects allocated with `new`, clone(), or a create*() factory method are automatically released
        // by the garbage collector, but may still be explicitly released by calling deallocate().
        // You shall NOT call cvReleaseImage(), cvReleaseMemStorage(), etc. on objects allocated this way.
        Mat grayImage = new Mat(height, width, opencv_core.CV_8UC1);
        Mat rotatedImage = grabbedImage.clone();

        // The OpenCVFrameRecorder class simply uses the VideoWriter of opencv_videoio,
        // but FFmpegFrameRecorder also exists as a more versatile alternative.
//        FrameRecorder recorder = FrameRecorder.createDefault("output.avi", width, height);
//        recorder.start();

        // CanvasFrame is a JFrame containing a Canvas component, which is hardware accelerated.
        // It can also switch into full-screen mode when called with a screenNumber.
        // We should also specify the relative monitor/camera response for proper gamma correction.
        CanvasFrame frame = new CanvasFrame("Some Title", CanvasFrame.getDefaultGamma()/grabber.getGamma());

        // Let's create some random 3D rotation...
        Mat randomR    = new Mat(3, 3, CV_64FC1),
                randomAxis = new Mat(3, 1, CV_64FC1);
        // We can easily and efficiently access the elements of matrices and images
        // through an Indexer object with the set of get() and put() methods.
        DoubleIndexer Ridx = randomR.createIndexer(),
                axisIdx = randomAxis.createIndexer();
        axisIdx.put(0, (Math.random() - 0.5) / 4,
                (Math.random() - 0.5) / 4,
                (Math.random() - 0.5) / 4);
        Rodrigues(randomAxis, randomR);
        double f = (width + height) / 2.0;
        Ridx.put(0, 2, Ridx.get(0, 2) * f);
        Ridx.put(1, 2, Ridx.get(1, 2) * f);
        Ridx.put(2, 0, Ridx.get(2, 0) / f);
        Ridx.put(2, 1, Ridx.get(2, 1) / f);
        System.out.println(Ridx);

        // We can allocate native arrays using constructors taking an integer as argument.
        Point hatPoints = new Point(3);

        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
            // Let's try to detect some faces! but we need a grayscale image...
            cvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            RectVector faces = new RectVector();
            classifier.detectMultiScale(grayImage, faces);
            long total = faces.size();
            for (long i = 0; i < total; i++) {
                Rect r = faces.get(i);
                int x = r.x(), y = r.y(), w = r.width(), h = r.height();
                rectangle(grabbedImage, new Point(x, y), new Point(x + w, y + h), Scalar.RED, 1, CV_AA, 0);

                // To access or pass as argument the elements of a native array, call position() before.
                hatPoints.position(0).x(x - w / 10     ).y(y - h / 10);
                hatPoints.position(1).x(x + w * 11 / 10).y(y - h / 10);
                hatPoints.position(2).x(x + w / 2      ).y(y - h / 2 );
                fillConvexPoly(grabbedImage, hatPoints.position(0), 3, Scalar.GREEN, CV_AA, 0);
            }

            // Let's find some contours! but first some thresholding...
            threshold(grayImage, grayImage, 64, 255, CV_THRESH_BINARY);

            // To check if an output argument is null we may call either isNull() or equals(null).
            MatVector contours = new MatVector();
            findContours(grayImage, contours, CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
            long n = contours.size();
            for (long i = 0; i < n; i++) {
                Mat contour = contours.get(i);
                Mat points = new Mat();
                approxPolyDP(contour, points, arcLength(contour, true) * 0.02, true);
                drawContours(grabbedImage, new MatVector(points), -1, Scalar.BLUE);
            }

            warpPerspective(grabbedImage, rotatedImage, randomR, rotatedImage.size());

            Frame rotatedFrame = converter.convert(rotatedImage);
            frame.showImage(rotatedFrame);
//            recorder.record(rotatedFrame);
        }
        frame.dispose();
//        recorder.stop();
        grabber.stop();
    }

    private static void teste1() {
        try {
            final BufferedImage image = ImageIO.read(new File("./imagens/0001.jpg"));
            algoritmo1(image);

            final File pastaSaida = new File("./imagens/saida/");
            if (!pastaSaida.exists()) pastaSaida.mkdirs();
            ImageIO.write(image, "png", new File("./imagens/saida/0010_1.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void algoritmo1(BufferedImage image) {
        limiarizacao(image);
        dilatacao(image);
        encontraRosto(image);
    }

    private static void encontraRosto(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int tamMaiorLargura = 0;
        int tamMaiorAltura = 0;
        int xMaiorLargura = -1;
        int yMaiorLargura = -1;
        int xMaiorAltura = -1;
        int yMaiorAltura = -1;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color cor = new Color(image.getRGB(x, y));

                int tempYMaiorAltura = y;
                int tempTamanho = 0;

                while (cor.getRed() == 255 && tempYMaiorAltura+1 < height) {
                    cor = new Color(image.getRGB(x, ++tempYMaiorAltura));
                    tempTamanho++;
                }

                if (tempTamanho > tamMaiorAltura) {
                    tamMaiorAltura = tempTamanho;
                    xMaiorAltura = x;
                    yMaiorAltura = y;
                }

            }
        }

        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        for (int y = yMaiorAltura; y < yMaiorAltura + tamMaiorAltura; y++) {
            pixels[y * width + xMaiorAltura] = Color.red.getRGB();
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color cor = new Color(image.getRGB(x, y));

                int tempXMaiorLargura = x;
                int tempTamanho = 0;

                while (cor.getRed() == 255 && tempXMaiorLargura+1 < width) {
                    cor = new Color(image.getRGB(++tempXMaiorLargura, y));
                    tempTamanho++;
                }

                if (tempTamanho > tamMaiorLargura) {
                    tamMaiorLargura = tempTamanho;
                    xMaiorLargura = x;
                    yMaiorLargura = y;
                }

            }
        }

        for (int x = xMaiorLargura; x < xMaiorLargura + tamMaiorLargura; x++) {
            pixels[yMaiorLargura * width + x] = Color.BLUE.getRGB();
        }


        image.setRGB(0, 0, width, height, pixels, 0, width);

        final Graphics graphics = image.getGraphics();

        graphics.setColor(Color.ORANGE);
        graphics.drawRect(xMaiorLargura, yMaiorAltura, tamMaiorLargura, tamMaiorAltura);
    }

    private static void dilatacao(BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        int tamanhoFiltro = 9;

        for (int x = 5; x + tamanhoFiltro < width; x++) {
            for (int y = 5; y + tamanhoFiltro < height; y++) {

                int maiorVermelho = Collections.max(Arrays.asList(
                        new Color(image.getRGB(x - 1, y - 1)).getRed(),
                        new Color(image.getRGB(x - 1, y)).getRed(),
                        new Color(image.getRGB(x - 1, y + 1)).getRed(),

                        new Color(image.getRGB(x, y - 1)).getRed(),
                        new Color(image.getRGB(x, y)).getRed(),
                        new Color(image.getRGB(x, y + 1)).getRed(),

                        new Color(image.getRGB(x + 1, y - 1)).getRed(),
                        new Color(image.getRGB(x + 1, y)).getRed(),
                        new Color(image.getRGB(x + 1, y + 1)).getRed(),

                        new Color(image.getRGB(x - 2, y - 2)).getRed(),
                        new Color(image.getRGB(x - 2, y)).getRed(),
                        new Color(image.getRGB(x - 2, y + 2)).getRed(),

                        new Color(image.getRGB(x, y - 2)).getRed(),
                        new Color(image.getRGB(x, y)).getRed(),
                        new Color(image.getRGB(x, y + 2)).getRed(),

                        new Color(image.getRGB(x + 2, y - 2)).getRed(),
                        new Color(image.getRGB(x + 2, y)).getRed(),
                        new Color(image.getRGB(x + 2, y + 2)).getRed(),

                        new Color(image.getRGB(x - 3, y - 3)).getRed(),
                        new Color(image.getRGB(x - 3, y)).getRed(),
                        new Color(image.getRGB(x - 3, y + 3)).getRed(),

                        new Color(image.getRGB(x, y - 3)).getRed(),
                        new Color(image.getRGB(x, y)).getRed(),
                        new Color(image.getRGB(x, y + 3)).getRed(),

                        new Color(image.getRGB(x + 3, y - 3)).getRed(),
                        new Color(image.getRGB(x + 3, y)).getRed(),
                        new Color(image.getRGB(x + 3, y + 3)).getRed()
                ));

//                    int maiorVerde = Collections.max(Arrays.asList(
//                            new Color(image.getRGB(x - 1, y - 1)).getGreen(),
//                            new Color(image.getRGB(x - 1, y)).getGreen(),
//                            new Color(image.getRGB(x - 1, y + 1)).getGreen(),
//
//                            new Color(image.getRGB(x, y - 1)).getGreen(),
//                            new Color(image.getRGB(x, y)).getGreen(),
//                            new Color(image.getRGB(x, y + 1)).getGreen(),
//
//                            new Color(image.getRGB(x + 1, y - 1)).getGreen(),
//                            new Color(image.getRGB(x + 1, y)).getGreen(),
//                            new Color(image.getRGB(x + 1, y + 1)).getGreen(),
//
//                            new Color(image.getRGB(x - 2, y - 2)).getGreen(),
//                            new Color(image.getRGB(x - 2, y)).getGreen(),
//                            new Color(image.getRGB(x - 2, y + 2)).getGreen(),
//
//                            new Color(image.getRGB(x, y - 2)).getGreen(),
//                            new Color(image.getRGB(x, y)).getGreen(),
//                            new Color(image.getRGB(x, y + 2)).getGreen(),
//
//                            new Color(image.getRGB(x + 2, y - 2)).getGreen(),
//                            new Color(image.getRGB(x + 2, y)).getGreen(),
//                            new Color(image.getRGB(x + 2, y + 2)).getGreen(),
//
//                            new Color(image.getRGB(x - 3, y - 3)).getGreen(),
//                            new Color(image.getRGB(x - 3, y)).getGreen(),
//                            new Color(image.getRGB(x - 3, y + 3)).getGreen(),
//
//                            new Color(image.getRGB(x, y - 3)).getGreen(),
//                            new Color(image.getRGB(x, y)).getGreen(),
//                            new Color(image.getRGB(x, y + 3)).getGreen(),
//
//                            new Color(image.getRGB(x + 3, y - 3)).getGreen(),
//                            new Color(image.getRGB(x + 3, y)).getGreen(),
//                            new Color(image.getRGB(x + 3, y + 3)).getGreen()
//                    ));
//                    int maiorAzul = Collections.max(Arrays.asList(
//                            new Color(image.getRGB(x - 1, y - 1)).getBlue(),
//                            new Color(image.getRGB(x - 1, y)).getBlue(),
//                            new Color(image.getRGB(x - 1, y + 1)).getBlue(),
//
//                            new Color(image.getRGB(x, y - 1)).getBlue(),
//                            new Color(image.getRGB(x, y)).getBlue(),
//                            new Color(image.getRGB(x, y + 1)).getBlue(),
//
//                            new Color(image.getRGB(x + 1, y - 1)).getBlue(),
//                            new Color(image.getRGB(x + 1, y)).getBlue(),
//                            new Color(image.getRGB(x + 1, y + 1)).getBlue(),
//
//                            new Color(image.getRGB(x - 2, y - 2)).getBlue(),
//                            new Color(image.getRGB(x - 2, y)).getBlue(),
//                            new Color(image.getRGB(x - 2, y + 2)).getBlue(),
//
//                            new Color(image.getRGB(x, y - 2)).getBlue(),
//                            new Color(image.getRGB(x, y)).getBlue(),
//                            new Color(image.getRGB(x, y + 2)).getBlue(),
//
//                            new Color(image.getRGB(x + 2, y - 2)).getBlue(),
//                            new Color(image.getRGB(x + 2, y)).getBlue(),
//                            new Color(image.getRGB(x + 2, y + 2)).getBlue(),
//
//                            new Color(image.getRGB(x - 3, y - 3)).getBlue(),
//                            new Color(image.getRGB(x - 3, y)).getBlue(),
//                            new Color(image.getRGB(x - 3, y + 3)).getBlue(),
//
//                            new Color(image.getRGB(x, y - 3)).getBlue(),
//                            new Color(image.getRGB(x, y)).getBlue(),
//                            new Color(image.getRGB(x, y + 3)).getBlue(),
//
//                            new Color(image.getRGB(x + 3, y - 3)).getBlue(),
//                            new Color(image.getRGB(x + 3, y)).getBlue(),
//                            new Color(image.getRGB(x + 3, y + 3)).getBlue()
//                    ));

                final Color maiorCor = new Color(maiorVermelho, maiorVermelho, maiorVermelho);

                pixels[(y - 1) * width + (x - 1)] = maiorCor.getRGB();
                pixels[y * width + (x - 1)] = maiorCor.getRGB();
                pixels[(y + 1) * width + (x - 1)] = maiorCor.getRGB();

                pixels[(y - 1) * width + x] = maiorCor.getRGB();
                pixels[y * width + x] = maiorCor.getRGB();
                pixels[(y + 1) * width + x] = maiorCor.getRGB();

                pixels[(y - 1) * width + (x + 1)] = maiorCor.getRGB();
                pixels[y * width + (x + 1)] = maiorCor.getRGB();
                pixels[(y + 1) * width + (x + 1)] = maiorCor.getRGB();

                pixels[(y - 2) * width + (x - 2)] = maiorCor.getRGB();
                pixels[y * width + (x - 2)] = maiorCor.getRGB();
                pixels[(y + 2) * width + (x - 2)] = maiorCor.getRGB();

                pixels[(y - 2) * width + x] = maiorCor.getRGB();
                pixels[y * width + x] = maiorCor.getRGB();
                pixels[(y + 2) * width + x] = maiorCor.getRGB();

                pixels[(y - 2) * width + (x + 2)] = maiorCor.getRGB();
                pixels[y * width + (x + 2)] = maiorCor.getRGB();
                pixels[(y + 2) * width + (x + 2)] = maiorCor.getRGB();

                pixels[(y - 3) * width + (x - 3)] = maiorCor.getRGB();
                pixels[y * width + (x - 3)] = maiorCor.getRGB();
                pixels[(y + 3) * width + (x - 3)] = maiorCor.getRGB();

                pixels[(y - 2) * width + x] = maiorCor.getRGB();
                pixels[y * width + x] = maiorCor.getRGB();
                pixels[(y + 2) * width + x] = maiorCor.getRGB();

                pixels[(y - 3) * width + (x + 3)] = maiorCor.getRGB();
                pixels[y * width + (x + 3)] = maiorCor.getRGB();
                pixels[(y + 3) * width + (x + 3)] = maiorCor.getRGB();

//                    pixels[y * width + x] = Color.black.getRGB();
            }
        }


        image.setRGB(0, 0, width, height, pixels, 0, width);
    }

    private static void limiarizacao(BufferedImage image) {
        final int height = image.getHeight();
        final int width = image.getWidth();

        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final Color pixelColor = new Color(image.getRGB(x, y));
                int vermelho = pixelColor.getRed();
                int verde = pixelColor.getGreen();
                int azul = pixelColor.getBlue();
//                int alpha = pixelColor.getAlpha();

                if (
                        vermelho > 95 && verde > 40 && azul > 20 &&
                        ((Math.max(Math.max(vermelho, verde), azul) -
                                Math.min(Math.min(vermelho, verde), azul)) > 15) &&
                        vermelho > verde &&
                        (vermelho - verde) > 15 &&
                        vermelho > azul
                ) {
                    pixels[y * width + x] = Color.WHITE.getRGB();
                } else {
                    pixels[y * width + x] = Color.BLACK.getRGB();
                }

            }
        }

        image.setRGB(0, 0, width, height, pixels, 0, width);
    }
}
