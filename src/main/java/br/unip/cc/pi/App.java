package br.unip.cc.pi;

import br.unip.cc.pi.view.FrmCaptura;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;

public class App {

    static {
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(()->{
            FrmCaptura frmCaptura = new FrmCaptura();
            frmCaptura.setVisible(true);
            frmCaptura.startCamera();
        });
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
