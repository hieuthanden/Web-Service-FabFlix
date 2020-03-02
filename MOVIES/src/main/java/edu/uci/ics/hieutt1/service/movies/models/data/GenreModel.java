package edu.uci.ics.hieutt1.service.movies.models.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.TransferQueue;

public class GenreModel {
    @JsonProperty(value = "genre_id", required = true)
    private Integer genre_id;
    @JsonProperty(value = "name", required = true)
    private String name;

    public GenreModel() {
    }

    public Integer getGenre_id() {
        return genre_id;
    }

    public void setGenre_id(Integer genre_id) {
        this.genre_id = genre_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
