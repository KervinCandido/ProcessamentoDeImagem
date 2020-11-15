package br.unip.cc.pi.service;

import br.unip.cc.pi.view.form.PersonForm;

import java.awt.image.BufferedImage;
import java.util.List;

public class ValidateRegisterService {

    public ValidateRegisterService() {
    }

    public void isPersonValid(PersonForm personForm) throws RuntimeException {
        String name = personForm.getName();

        if (name.length() < 1) {
            throw new RuntimeException("Nome não pode ser em branco");
        }

        if (!name.matches("^[a-zA-Z]+([ ]*[a-zA-Z]*)*")) {
            throw new RuntimeException("Nome só pode ser composto por letra e espaço");
        }

        List<BufferedImage> faces = personForm.getFaces();
        if (faces.size() != 10) {
            throw new RuntimeException("Leitura facial não concluida ou não realizada");
        }
    }
}
