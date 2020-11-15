package br.unip.cc.pi.recognizer.face;

import org.bytedeco.opencv.opencv_core.Mat;

public interface FaceRecognizer {
    int recognize(Mat faceData);
}
