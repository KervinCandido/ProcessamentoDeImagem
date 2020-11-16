package br.unip.cc.pi.view.component;

import br.unip.cc.pi.model.Person;

import javax.swing.*;
import java.awt.*;

public class PanelLogged extends JPanel {

    private final JLabel lblIdPerson;
    private final JLabel lblNamePerson;
    private final JLabel lblNivelDeAcessoPerson;
    private final JButton btnSair;

    public PanelLogged() {
        setLayout(null);
        setBackground(new Color(44, 62, 80));

        JLabel lblId = new JLabel("ID");
        lblId.setBounds(20, 20, 30, 30);
        lblId.setForeground(new Color(236, 240, 241));

        JLabel lblName = new JLabel("Nome");
        lblName.setBounds(20, 50, 50, 30);
        lblName.setForeground(new Color(236, 240, 241));

        JLabel lblNivelAcesso = new JLabel("Nivel de Acesso");
        lblNivelAcesso.setBounds(20, 80, 100, 30);
        lblNivelAcesso.setForeground(new Color(236, 240, 241));

        lblIdPerson = new JLabel("");
        lblIdPerson.setBounds(50, 20, 300, 30);
        lblIdPerson.setForeground(new Color(26, 188, 156));

        lblNamePerson = new JLabel("");
        lblNamePerson.setBounds(70, 50, 300, 30);
        lblNamePerson.setForeground(new Color(26, 188, 156));

        lblNivelDeAcessoPerson = new JLabel("");
        lblNivelDeAcessoPerson.setBounds(120, 80, 300, 30);
        lblNivelDeAcessoPerson.setForeground(new Color(26, 188, 156));

        btnSair = new JButton("Sair");
        btnSair.setBounds(20, 120, 200, 30);
        btnSair.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(230, 126, 34)));
        btnSair.setBackground(new Color(52, 152, 219, 0));
        btnSair.setForeground(new Color(236, 240, 241));
        btnSair.setCursor(new Cursor(Cursor.HAND_CURSOR));

        add(lblId);
        add(lblName);
        add(lblNivelAcesso);
        add(lblIdPerson);
        add(lblNamePerson);
        add(lblNivelDeAcessoPerson);
        add(btnSair);
    }

    public void updatePerson(Person person) {
        lblIdPerson.setText(person.getId().toString());
        lblNamePerson.setText(person.getName());
        lblNivelDeAcessoPerson.setText(person.getNivelDeAcesso().toString());
    }

    public JButton getBtnSair() {
        return btnSair;
    }
}
