package br.unip.cc.pi.view;

import br.unip.cc.pi.capture.face.FaceCapture;
import br.unip.cc.pi.capture.face.listener.CapturedFacesLimitListener;
import br.unip.cc.pi.model.NivelDeAcesso;
import br.unip.cc.pi.service.PersonService;
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
    private final FaceCapture faceCapture;
    private final PersonService personService;

    public FrmHome() throws HeadlessException {
        this.faceCapture = new FaceCapture();
        this.personService = new PersonService();
    }

    public void createAndShow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(getPanelHome());
        getPanelHome().getBtnCadastro().addActionListener(this::btnAbrirCadastro);
        getPanelRegister().getBtnCapturaRosto().addActionListener(this::btnCapturaRosto);
        getPanelRegister().getBtnCadastar().addActionListener(this::btnCadastrar);

        setVisible(true);
    }

    public void btnAbrirCadastro(ActionEvent event) {
        setContentPane(getPanelRegister());
        revalidate();
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

    public void btnCadastrar(ActionEvent event) {
        String nome = getPanelRegister().getTxtNome().getText().trim();
        NivelDeAcesso nivelDeAcesso = (NivelDeAcesso) getPanelRegister().getCmbNivelAcesso().getSelectedItem();
        List<BufferedImage> faces = faceCapture.getFaces();

        PersonForm personForm = new PersonForm();
        personForm.setName(nome);
        personForm.setNivelDeAcesso(nivelDeAcesso);
        personForm.setFaces(faces);

        try {
            personService.create(personForm);
            JOptionPane.showMessageDialog(this, nome + " cadastrado com sucesso!!");
            setContentPane(getPanelHome());
            revalidate();
        } catch (RuntimeException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Não foi possível salvar a pessoa", "", JOptionPane.INFORMATION_MESSAGE);
        }
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
}
