package edu.uci.ics.hieutt1.service.movies.models.data;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class FullPersonModel {
    @JsonProperty(value = "person_id", required = true)
    private Integer person_id;
    @JsonProperty(value = "name", required = true)
    private String name;
    @JsonProperty(value = "gender")
    private String gender;
    @JsonProperty(value = "birthday")
    private String birthday;
    @JsonProperty(value = "deathday")
    private String deathday;
    @JsonProperty(value = "biography")
    private String biography;
    @JsonProperty(value = "birthplace")
    private String birthplace;
    @JsonProperty(value = "popularity")
    private Float popularity;
    @JsonProperty(value = "profile_path")
    private String profile_path;

    @JsonCreator
    public FullPersonModel() {
    }

    public Integer getPerson_id() {
        return person_id;
    }

    public void setPerson_id(Integer person_id) {
        this.person_id = person_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDeathday() {
        return deathday;
    }

    public void setDeathday(String deathday) {
        this.deathday = deathday;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public Float getPopularity() {
        return popularity;
    }

    public void setPopularity(Float popularity) {
        this.popularity = popularity;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }
}