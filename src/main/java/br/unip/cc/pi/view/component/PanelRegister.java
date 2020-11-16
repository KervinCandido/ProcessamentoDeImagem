package br.unip.cc.pi.view.component;

import br.unip.cc.pi.model.NivelDeAcesso;

import javax.swing.*;
import java.awt.*;

public class PanelRegister extends JPanel {

    private final JTextField txtNome;
    private final JButton btnCapturaRosto;
    private final JComboBox<NivelDeAcesso> cmbNivelAcesso;
    private final JButton btnSalvar;

    public PanelRegister() {
        setLayout(null);
        setBackground(new Color(44, 62, 80));

        txtNome = new JTextField();
        txtNome.setBounds(65, 25, 300, 30);
        txtNome.setBackground(new Color(255, 255, 255, 0));
        txtNome.setForeground(new Color(236, 240, 241));
        txtNome.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(46, 204, 113)));

        JLabel lblNome = new JLabel("Nome");
        lblNome.setLabelFor(txtNome);
        lblNome.setBounds(25, 25, 40, 30);
        lblNome.setForeground(new Color(236, 240, 241));

        JLabel lblNivelAcesso = new JLabel("Nível Acesso");
        lblNivelAcesso.setLabelFor(txtNome);
        lblNivelAcesso.setBounds(25, 65, 80, 30);
        lblNivelAcesso.setForeground(new Color(236, 240, 241));

        cmbNivelAcesso = new JComboBox<>();
        cmbNivelAcesso.setBounds(105, 65, 200, 30);
        cmbNivelAcesso.addItem(NivelDeAcesso.PUBLICO);
        cmbNivelAcesso.addItem(NivelDeAcesso.DIRETOR_DE_DIVISAO);
        cmbNivelAcesso.addItem(NivelDeAcesso.MINISTRO_MEIO_AMBIENTE);

        btnCapturaRosto = new JButton("Captura Rosto");
        btnCapturaRosto.setBounds(315, 65, 200, 30);
        btnCapturaRosto.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(46, 204, 113)));
        btnCapturaRosto.setBackground(new Color(52, 152, 219, 0));
        btnCapturaRosto.setForeground(new Color(236, 240, 241));
        btnCapturaRosto.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(25, 110, 200, 30);
        btnSalvar.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(46, 204, 113)));
        btnSalvar.setBackground(new Color(52, 152, 219, 0));
        btnSalvar.setForeground(new Color(236, 240, 241));
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        add(lblNome);
        add(txtNome);
        add(lblNivelAcesso);
        add(cmbNivelAcesso);
        add(btnCapturaRosto);
        add(btnSalvar);

    }

    public void clean() {
        txtNome.setText("");
        cmbNivelAcesso.setSelectedIndex(0);
    }

    public JTextField getTxtNome() {
        return txtNome;
    }

    public JButton getBtnCapturaRosto() {
        return btnCapturaRosto;
    }

    public JComboBox<NivelDeAcesso> getCmbNivelAcesso() {
        return cmbNivelAcesso;
    }

    public JButton getBtnSalvar() {
        return btnSalvar;
    }
}
