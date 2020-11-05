package br.unip.cc.pi;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;

public class App {
    public static void main(String[] args) {
        try {
            final BufferedImage image = ImageIO.read(new File("./imagens/0006.jpg"));
            limiarizacao(image);
//            ImageIO.write(image, "png", new File("./imagens/saida/0010_0.png"));
            dilatacao(image);

            //encontra maior linha horizontal e vertical

            final File pastaSaida = new File("./imagens/saida/");
            if (!pastaSaida.exists()) pastaSaida.mkdirs();
            ImageIO.write(image, "png", new File("./imagens/saida/0010_1.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                int alpha = pixelColor.getAlpha();

                if (
                        vermelho > 95 && verde > 40 && azul > 20 &&
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

            }
        }

        image.setRGB(0, 0, width, height, pixels, 0, width);
    }
}
