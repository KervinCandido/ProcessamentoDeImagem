package br.unip.cc.pi;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class App {
    public static void main(String[] args) {
        try {
            final BufferedImage image = ImageIO.read(new File("./imagens/dean.jpg"));
            final int height = image.getHeight();
            final int width = image.getWidth();

            int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    final Color pixelColor = new Color(image.getRGB(x, y));
                    int vermelho = pixelColor.getRed();
                    int verde = pixelColor.getGreen();
                    int azul = pixelColor.getBlue();
                    int alpha = pixelColor.getAlpha();

                    if (
                            vermelho > 95 && verde > 40 && azul > 26 &&
                            ((Math.max(Math.max(vermelho, verde), azul) -
                                    Math.min(Math.min(vermelho, verde), azul)) > 15) &&
                            vermelho > verde &&
                            (vermelho - verde) > 15 &&
                            vermelho > azul
                    ) {
                        pixels[y * width + x] = Color.white.getRGB();
                    } else {
                        pixels[y * width + x] = Color.black.getRGB();
                    }

//                    pixels[y * width + x] = new Color(vermelho, verde, azul, alpha).getRGB();
                }
            }

            image.setRGB(0, 0, width, height, pixels, 0, width);
            ImageIO.write(image, "jpg", new File("./imagens/saida/dean2.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
