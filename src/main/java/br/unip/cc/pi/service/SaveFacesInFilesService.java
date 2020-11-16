package br.unip.cc.pi.service;

import br.unip.cc.pi.view.form.PersonForm;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SaveFacesInFilesService {

    public List<File> saveFaces(PersonForm person) throws IOException {
        String name = person.getName();
        List<BufferedImage> faces = person.getFaces();
        createDirIfNotExists();
        List<File> files = new ArrayList<>();

        LocalDateTime localDateTime = LocalDateTime.now();
        String timeStamp = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        for (int i = 0; i < faces.size(); i++) {
            String fileName = name + timeStamp + i;
            File file = new File("./faces/"+Base64.getEncoder().encodeToString(fileName.getBytes())+".jpg");
            ImageIO.write(resize(faces.get(i), 210, 210), "jpg", file);
            files.add(file);
        }

        return files;
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    private void createDirIfNotExists() {
        File dir = new File("./faces");
        if (!dir.exists()) dir.mkdir();
    }
}
