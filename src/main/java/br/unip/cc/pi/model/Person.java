package br.unip.cc.pi.model;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class Person {

    private Long id;
    private String name;
    private NivelDeAcesso nivelDeAcesso;
    private List<File> faces;

    public Person() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NivelDeAcesso getNivelDeAcesso() {
        return nivelDeAcesso;
    }

    public void setNivelDeAcesso(NivelDeAcesso nivelDeAcesso) {
        this.nivelDeAcesso = nivelDeAcesso;
    }

    public List<File> getFaces() {
        return faces;
    }

    public void setFaces(List<File> faces) {
        this.faces = faces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) &&
                Objects.equals(name, person.name) &&
                nivelDeAcesso == person.nivelDeAcesso &&
                Objects.equals(faces, person.faces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, nivelDeAcesso, faces);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nivelDeAcesso=" + nivelDeAcesso +
                ", faces=" + faces +
                '}';
    }
}
