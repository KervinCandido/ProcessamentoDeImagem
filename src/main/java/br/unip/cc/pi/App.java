package br.unip.cc.pi;

import br.unip.cc.pi.model.Person;
import br.unip.cc.pi.service.FaceRecognizerService;
import br.unip.cc.pi.service.PersonService;
import br.unip.cc.pi.view.FrmHome;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.util.List;

public class App {


    public static void main(String[] args) {
        Runnable treinamento = () -> {
            List<Person> people = new PersonService().findAll();
            FaceRecognizerService faceRecognizerService = new FaceRecognizerService();
            faceRecognizerService.train(people);
            System.out.println("TREINO COMPLETO");
        };
        new Thread(treinamento).start();

        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());

            Runnable homeRunnable = () -> {
                FrmHome home = new FrmHome();
                EventQueue.invokeLater(home::createAndShow);
            };
            new Thread(homeRunnable, "home").start();
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}
