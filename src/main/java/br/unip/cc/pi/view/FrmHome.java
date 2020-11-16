package br.unip.cc.pi.view;

import br.unip.cc.pi.capture.face.FaceCapture;
import br.unip.cc.pi.capture.face.listener.CapturedFacesLimitListener;
import br.unip.cc.pi.model.NivelDeAcesso;
import br.unip.cc.pi.model.Person;
import br.unip.cc.pi.service.FaceRecognizerService;
import br.unip.cc.pi.service.PersonService;
import br.unip.cc.pi.view.component.PanelLogged;
import br.unip.cc.pi.view.component.PanelRegister;
import br.unip.cc.pi.view.component.PanelHome;
import br.unip.cc.pi.view.form.PersonForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class FrmHome extends JFrame {

    private PanelHome panelHome;
    private PanelRegister panelRegister;
    private PanelLogged panelLogged;
    private final FaceCapture faceCapture;
    private final PersonService personService;
    private final FaceRecognizerService faceRecognizerService;

    public FrmHome() throws HeadlessException {
        this.faceCapture = new FaceCapture();
        this.personService = new PersonService();
        this.faceRecognizerService = new FaceRecognizerService();
    }

    public void createAndShow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(getPanelHome());
        getPanelHome().getBtnCadastro().addActionListener(this::btnAbrirCadastro);
        getPanelHome().getBtnEntrar().addActionListener(this::btnEntrar);
        getPanelRegister().getBtnCapturaRosto().addActionListener(this::btnCapturaRosto);
        getPanelRegister().getBtnSalvar().addActionListener(this::btnSalvar);

        setVisible(true);
    }

    private void btnEntrar(ActionEvent event) {
        JDialogCaptureFace dialogCaptureFace = new JDialogCaptureFace();

        dialogCaptureFace.addFaceCaptureListener(faces -> {
            if (faces.size() > 1) {
                dialogCaptureFace.stop();
                JOptionPane.showMessageDialog(dialogCaptureFace,
                        "Somente a pessoa que irá se autenticar deve fica na câmera", "",
                        JOptionPane.WARNING_MESSAGE);
                dialogCaptureFace.retart();
                return;
            }
            try {
                BufferedImage bufferedImage = faces.get(0);
                Person person = faceRecognizerService.recognize(bufferedImage);
                dialogCaptureFace.stop();
                EventQueue.invokeLater(dialogCaptureFace::dispose);

                EventQueue.invokeLater(() -> getPanelLogged().updatePerson(person));
                getPanelLogged().updatePerson(person);
                switchPanel(getPanelLogged());

                JOptionPane.showMessageDialog(this, "Bem Vindo " + person.getName());
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        });
        dialogCaptureFace.createAndShow();
    }

    public void btnAbrirCadastro(ActionEvent event) {
        getPanelRegister().clean();
        switchPanel(getPanelRegister());
    }

    public void btnCapturaRosto(ActionEvent event) {
        JDialogCaptureFace dialogCaptureFace = new JDialogCaptureFace();

        CapturedFacesLimitListener capturedFacesLimitListener = () -> {
            dialogCaptureFace.stop();
            EventQueue.invokeLater(dialogCaptureFace::dispose);
        };

        faceCapture.clearListeners();
        faceCapture.addCapturedFacesLimitListener(capturedFacesLimitListener);
        faceCapture.clear();

        dialogCaptureFace.addFaceCaptureListener(faces -> {
            if (faces.size() > 1) {
                dialogCaptureFace.stop();
                JOptionPane.showMessageDialog(dialogCaptureFace,
                        "Só é possível cadastar uma pessoa por vez", "",
                        JOptionPane.WARNING_MESSAGE);
                dialogCaptureFace.retart();
            }
            faceCapture.addFace(faces.get(0));
        });
        dialogCaptureFace.createAndShow();
    }

    public void btnSalvar(ActionEvent event) {
        String nome = getPanelRegister().getTxtNome().getText().trim();
        NivelDeAcesso nivelDeAcesso = (NivelDeAcesso) getPanelRegister().getCmbNivelAcesso().getSelectedItem();
        List<BufferedImage> faces = faceCapture.getFaces();
        PersonForm personForm = new PersonForm();

        System.out.println("Verificando se a pessoa já não é cadastrada");
        for (BufferedImage face : faces) {
            try {
                Person person = faceRecognizerService.recognize(face);

                if (person != null) {
                    JOptionPane.showMessageDialog(this,
                            "Pessoa já foi cadastrada anteriormente como " +
                                    person.getName() + " - " + person.getNivelDeAcesso() +
                                    "\nA função de salvar atualizará os dados"
                            , "", JOptionPane.INFORMATION_MESSAGE);
                    personForm.setId(person.getId());
                    break;
                }
            } catch (RuntimeException e) {
                System.out.println("Não é cadastrada");
            }
        }


        personForm.setName(nome);
        personForm.setNivelDeAcesso(nivelDeAcesso);
        personForm.setFaces(faces);

        try {
            personService.save(personForm);
            JOptionPane.showMessageDialog(this, nome + " salvo(a) com sucesso!!");
            switchPanel(getPanelHome());
        } catch (RuntimeException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Não foi possível salvar a pessoa", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void switchPanel(final JPanel panel) {
        EventQueue.invokeLater(() -> {
            setContentPane(panel);
            revalidate();
        });
    }

    private PanelHome getPanelHome() {
        if (panelHome == null) {
            panelHome = new PanelHome();
        }
        return panelHome;
    }

    private PanelRegister getPanelRegister() {
        if (panelRegister == null ){
            panelRegister = new PanelRegister();
        }
        return panelRegister;
    }

    private PanelLogged getPanelLogged() {
        if (panelLogged == null ){
            panelLogged = new PanelLogged();
        }
        return panelLogged;
    }
}
