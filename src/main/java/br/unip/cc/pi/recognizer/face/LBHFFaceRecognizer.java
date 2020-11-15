package br.unip.cc.pi.recognizer.face;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;

import java.nio.IntBuffer;

import static org.bytedeco.opencv.global.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

public class LBHFFaceRecognizer implements FaceRecognizer {

    private final org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer recognizer;

    public LBHFFaceRecognizer() {
        recognizer = org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer.create();
        init();
    }

    private void init() {
        MatVector images = new MatVector(2);
        Mat labels = new Mat(2, 1, opencv_core.CV_32SC1);
        IntBuffer labelsBuf = labels.createBuffer();

        Mat img = opencv_imgcodecs.imread("./face.jpg");
        Mat img2 = opencv_imgcodecs.imread("./face2.jpg");
        cvtColor(img, img, CV_BGR2GRAY);
        cvtColor(img2, img2, CV_BGR2GRAY);

        images.put(0, img);
        labelsBuf.put(0, 1000);

        images.put(1, img2);
        labelsBuf.put(1, 5000);

        recognizer.train(images, labels);
    }

    @Override
    public int recognize(Mat faces) {
        cvtColor(faces, faces, CV_BGR2GRAY);
        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(0);
        recognizer.predict(faces, label, confidence);

        int predictedLabel = label.get(0);

        //System.out.println(confidence.get(0));

        //Confidence value less than 60 means face is known
        //Confidence value greater than 60 means face is unknown
        if(confidence.get(0) > 60) {
            //System.out.println("-1");
            return -1;
        }
        return predictedLabel;
    }
}
