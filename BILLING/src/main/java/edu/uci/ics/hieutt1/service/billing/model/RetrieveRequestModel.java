package edu.uci.ics.hieutt1.service.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RetrieveRequestModel {

    @JsonProperty(value = "email", required = true) private String email;

    @JsonCreator
    public RetrieveRequestModel(@JsonProperty(value = "email", required = true) String email)
    {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

}
