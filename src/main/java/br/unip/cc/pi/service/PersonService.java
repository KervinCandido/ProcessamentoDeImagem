package br.unip.cc.pi.service;

import br.unip.cc.pi.dao.PersonDAO;
import br.unip.cc.pi.model.Person;
import br.unip.cc.pi.view.form.PersonForm;

import java.io.IOException;
import java.util.List;

public class PersonService {

    private final PersonDAO personDAO;
    private final FaceRecognizerService faceRecognizerService;

    public PersonService() {
        this.personDAO = new PersonDAO();
        this.faceRecognizerService = new FaceRecognizerService();
    }

    public void save(PersonForm personForm) throws IOException, RuntimeException {
        ValidateRegisterService validateRegisterService = new ValidateRegisterService();
        validateRegisterService.isPersonValid(personForm);
        Person person = personForm.toPerson();
        personDAO.save(person);
        faceRecognizerService.train(person);
    }

    public List<Person> findAll() {
        return personDAO.findAll();
    }

    public Person findById(Long recognize) {
        return personDAO.findById(recognize);
    }

    public int count() {
        return personDAO.findAll().size();
    }
}
