package br.unip.cc.pi.view.form;

import br.unip.cc.pi.model.NivelDeAcesso;
import br.unip.cc.pi.model.Person;
import br.unip.cc.pi.service.SaveFacesInFilesService;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class PersonForm {

    private String name;
    private NivelDeAcesso nivelDeAcesso;
    private List<BufferedImage> faces;

    private final SaveFacesInFilesService saveFacesInFilesService;

    public PersonForm() {
        saveFacesInFilesService = new SaveFacesInFilesService();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BufferedImage> getFaces() {
        return faces;
    }

    public void setFaces(List<BufferedImage> faces) {
        this.faces = faces;
    }

    public NivelDeAcesso getNivelDeAcesso() {
        return nivelDeAcesso;
    }

    public void setNivelDeAcesso(NivelDeAcesso nivelDeAcesso) {
        this.nivelDeAcesso = nivelDeAcesso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonForm that = (PersonForm) o;
        return Objects.equals(name, that.name) &&
                nivelDeAcesso == that.nivelDeAcesso &&
                Objects.equals(faces, that.faces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nivelDeAcesso, faces);
    }

    public Person toPerson() throws IOException {
        Person person = new Person();
        person.setName(name);
        person.setNivelDeAcesso(nivelDeAcesso);

        List<File> files = saveFacesInFilesService.saveFaces(this);
        person.setFaces(files);

        return person;
    }
}
