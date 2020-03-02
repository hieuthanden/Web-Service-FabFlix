package edu.uci.ics.hieutt1.service.movies.models.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonModel {
    @JsonProperty(value = "person_id", required = true)
    private Integer person_id;
    @JsonProperty(value = "name", required = true)
    private String name;

    public PersonModel() {
    }

    @JsonProperty(value = "person_id", required = true)
    public Integer getPerson_id() {
        return person_id;
    }

    @JsonProperty(value = "name", required = true)
    public void setPerson_id(Integer person_id) {
        this.person_id = person_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
