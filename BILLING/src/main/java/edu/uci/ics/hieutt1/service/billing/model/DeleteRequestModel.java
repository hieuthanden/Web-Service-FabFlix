package edu.uci.ics.hieutt1.service.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteRequestModel {

    @JsonProperty(value = "email", required = true) private String email;
    @JsonProperty(value = "movie_id", required = true)private String movie_id;



    @JsonCreator
    public DeleteRequestModel(@JsonProperty(value = "email", required = true) String email,
                              @JsonProperty(value = "movie_id", required = true) String movie_id)
    {
        this.email = email;
        this.movie_id = movie_id;
    }

    public String getEmail() {
        return email;
    }

    public String getMovie_id() {
        return movie_id;
    }


}
