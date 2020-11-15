package br.unip.cc.pi.service;

import br.unip.cc.pi.dao.PersonDAO;
import br.unip.cc.pi.view.form.PersonForm;

import java.io.IOException;

public class PersonService {

    private final PersonDAO personDAO;

    public PersonService() {
        this.personDAO = new PersonDAO();
    }

    public void create(PersonForm personForm) throws IOException, RuntimeException {
        ValidateRegisterService validateRegisterService = new ValidateRegisterService();
        validateRegisterService.isPersonValid(personForm);
        personDAO.create(personForm.toPerson());
    }
}
