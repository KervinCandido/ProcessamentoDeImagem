package br.unip.cc.pi.dao;

import br.unip.cc.pi.model.Person;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PersonDAO {

    private static final List<Person> people = new ArrayList<>();
    private final Gson gson;
    private final File RECONHECIMENTO_FACIAL_DB = new File("./reconhecimento-facial.json");

    public PersonDAO() {
        gson = new GsonBuilder().create();
    }

    public void save(Person person) {
        if (person.getId() != null) {
            Person p = findById(person.getId());
            p.setName(person.getName());
            p.setNivelDeAcesso(person.getNivelDeAcesso());
            p.setFaces(person.getFaces());
        } else {
            person.setId(getLastId());
            people.add(person);
        }
        updateGson();
    }

    public List<Person> findAll() {
        try {
            if (!people.isEmpty()) return people;

            BufferedReader br = new BufferedReader(new FileReader(RECONHECIMENTO_FACIAL_DB));
            Type listType = new TypeToken<ArrayList<Person>>() {}.getType();
            people.addAll(gson.fromJson(br, listType));
            return new ArrayList<>(people);
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private Long getLastId() {
        if (people.isEmpty()) return 1L;
        Person person = people.get(people.size() - 1);
        return person.getId()+1;
    }

    private void updateGson() {
        try {
            String json = gson.toJson(people);
            try(FileWriter fileWriter = new FileWriter(RECONHECIMENTO_FACIAL_DB)){
                fileWriter.write(json);
                fileWriter.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Person findById(Long recognize) throws RuntimeException{
        return people.stream().filter(person -> person.getId().equals(recognize))
                .findFirst().orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));
    }
}
