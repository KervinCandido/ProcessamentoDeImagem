package br.unip.cc.pi.recognizer.face;

import br.unip.cc.pi.model.Person;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;

import java.io.File;
import java.nio.IntBuffer;

import static org.bytedeco.opencv.global.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

public class LBHFFaceRecognizer implements FaceRecognizer {

    private final org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer recognizer;

    public LBHFFaceRecognizer() {
        recognizer = org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer.create();
    }

    @Override
    public void train(Person person) {
        int qtdFaces = person.getFaces().size();
        MatVector images = new MatVector(qtdFaces);
        Mat labels = new Mat(qtdFaces, 1, opencv_core.CV_32SC1);
        IntBuffer labelsBuf = labels.createBuffer();

        for (int i = 0; i < person.getFaces().size(); i++) {
            Mat img = opencv_imgcodecs.imread(person.getFaces().get(i).getAbsolutePath());
            cvtColor(img, img, CV_BGR2GRAY);

            images.put(i, img);
            labelsBuf.put(i, person.getId().intValue());
        }
        recognizer.train(images, labels);
        recognizer.save("train.yml");
    }

    @Override
    public int recognize(Mat faces) {
        recognizer.read("train.yml");
        cvtColor(faces, faces, CV_BGR2GRAY);

        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(0);
        recognizer.predict(faces, label, confidence);

        int predictedLabel = label.get(0);
        System.out.println("PREDICTED LABEL " + predictedLabel);
        System.out.println("CONFIDENCE " + confidence.get(0));
        //acima de 60 desconhecido
        if(confidence.get(0) > 60) {
            return -1;
        }
        return predictedLabel;
    }
}
