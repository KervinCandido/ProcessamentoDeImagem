package br.unip.cc.pi.view.listener;

import java.awt.image.BufferedImage;
import java.util.List;

@FunctionalInterface
public interface FaceCaptureListener {
    void onFaceCapture(List<BufferedImage> faces);
}
