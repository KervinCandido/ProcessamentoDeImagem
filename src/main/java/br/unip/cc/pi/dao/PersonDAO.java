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

    private final List<Person> people;
    private final Gson gson;
    private final File RECONHECIMENTO_FACIAL_DB = new File("./reconhecimento-facial.json");

    public PersonDAO() {
        gson = new GsonBuilder().create();
        people = new ArrayList<>();
    }

    public void create(Person person) {
        person.setId(getLastId());
        people.add(person);
        updateGson();
    }

    public List<Person> findAll() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(RECONHECIMENTO_FACIAL_DB));
        Type listType = new TypeToken<ArrayList<Person>>(){}.getType();
        return gson.fromJson(br, listType);
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
}
