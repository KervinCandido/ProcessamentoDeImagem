package br.unip.cc.pi.capture.face;

import br.unip.cc.pi.capture.face.listener.CapturedFacesLimitListener;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FaceCapture {

    private final List<BufferedImage> faces;
    private final List<CapturedFacesLimitListener> limitListeners;

    public FaceCapture() {
        this.faces = new ArrayList<>();
        this.limitListeners = new ArrayList<>();
    }

    public void addCapturedFacesLimitListener(CapturedFacesLimitListener capturedFacesLimitListener) {
        this.limitListeners.add(capturedFacesLimitListener);
    }

    public void addFace(BufferedImage bufferedImage) {
        this.faces.add(bufferedImage);
        if (this.faces.size() >= 10) {
            limitListeners.forEach(CapturedFacesLimitListener::onCapturedFaces);
        }
    }

    public void clear() {
        this.faces.clear();
    }

    public void clearListeners() {
        this.limitListeners.clear();
    }

    public List<BufferedImage> getFaces() {
        return new ArrayList<>(this.faces);
    }
}
