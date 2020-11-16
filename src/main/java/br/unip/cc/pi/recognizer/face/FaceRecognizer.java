package br.unip.cc.pi.recognizer.face;

import br.unip.cc.pi.model.Person;
import org.bytedeco.opencv.opencv_core.Mat;

public interface FaceRecognizer {
    void train(Person person);
    int recognize(Mat faceData);
}
