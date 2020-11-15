package br.unip.cc.pi.recognizer.face;

public class FaceRecognerFactory {

    public static FaceRecognizer createLBHFFaceRecognizer() {
        return new LBHFFaceRecognizer();
    }
}
