package br.unip.cc.pi.view.component;

import javax.swing.*;
import java.awt.*;

public class PanelHome extends JPanel {

    private final JButton btnCadastro;
    private final JButton btnEntrar;

    public PanelHome() {
        setLayout(null);
        setBackground(new Color(44, 62, 80));

        btnCadastro = new JButton("Cadastrar");
        btnCadastro.setBounds(300, 235, 200, 30);
        btnCadastro.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(46, 204, 113)));
        btnCadastro.setBackground(new Color(52, 152, 219, 0));
        btnCadastro.setForeground(new Color(236, 240, 241));
        btnCadastro.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEntrar = new JButton("Entrar");
        btnEntrar.setBounds(300, 275, 200, 30);
        btnEntrar.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(230, 126, 34)));
        btnEntrar.setBackground(new Color(52, 152, 219, 0));
        btnEntrar.setForeground(new Color(236, 240, 241));
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        add(btnCadastro);
        add(btnEntrar);
    }

    public JButton getBtnCadastro() {
        return btnCadastro;
    }

    public JButton getBtnEntrar() {
        return btnEntrar;
    }
}
