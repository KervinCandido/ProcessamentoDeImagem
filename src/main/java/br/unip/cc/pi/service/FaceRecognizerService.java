package br.unip.cc.pi.service;

import br.unip.cc.pi.dao.PersonDAO;
import br.unip.cc.pi.model.Person;
import br.unip.cc.pi.recognizer.face.FaceRecognerFactory;
import br.unip.cc.pi.recognizer.face.FaceRecognizer;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

import java.awt.image.BufferedImage;
import java.util.List;

public class FaceRecognizerService {

    private final FaceRecognizer lbhfFaceRecognizer;
    private final PersonDAO personDAO;

    public FaceRecognizerService() {
        lbhfFaceRecognizer = FaceRecognerFactory.createLBHFFaceRecognizer();
        personDAO = new PersonDAO();
    }

    public void train(Person person) {
        lbhfFaceRecognizer.train(person);
    }

    public void train(List<Person> people) {
        for (Person person : people) {
            train(person);
        }
    }

    public Person recognize(BufferedImage bufferedImage) throws RuntimeException {
        OpenCVFrameConverter.ToIplImage cv = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter jcv = new Java2DFrameConverter();
        Mat mat = cv.convertToMat(jcv.convert(bufferedImage));
        int recognize = lbhfFaceRecognizer.recognize(mat);
        if (recognize < 0) {
            throw new RuntimeException("Desconhecido");
        }

        return personDAO.findById((long) recognize);
    }
}
