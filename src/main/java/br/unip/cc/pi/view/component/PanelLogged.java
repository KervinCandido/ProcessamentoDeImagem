package br.unip.cc.pi.view.component;

import br.unip.cc.pi.model.Person;

import javax.swing.*;
import java.awt.*;

public class PanelLogged extends JPanel {

    private final JLabel lblIdPerson;
    private final JLabel lblNamePerson;
    private final JLabel lblNivelDeAcessoPerson;

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
        lblIdPerson.setBounds(50, 20, 100, 30);
        lblIdPerson.setForeground(new Color(26, 188, 156));

        lblNamePerson = new JLabel("");
        lblNamePerson.setBounds(70, 50, 100, 30);
        lblNamePerson.setForeground(new Color(26, 188, 156));

        lblNivelDeAcessoPerson = new JLabel("");
        lblNivelDeAcessoPerson.setBounds(120, 80, 100, 30);
        lblNivelDeAcessoPerson.setForeground(new Color(26, 188, 156));

        add(lblId);
        add(lblName);
        add(lblNivelAcesso);
        add(lblIdPerson);
        add(lblNamePerson);
        add(lblNivelDeAcessoPerson);
    }

    public void updatePerson(Person person) {
        lblIdPerson.setText(person.getId().toString());
        lblNamePerson.setText(person.getName());
        lblNivelDeAcessoPerson.setText(person.getNivelDeAcesso().toString());
    }
}
