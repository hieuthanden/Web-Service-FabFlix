package edu.uci.ics.hieutt1.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ForgetRequestModel {
    @JsonProperty(value = "email", required = true) private String email;

    @JsonCreator
    public ForgetRequestModel(@JsonProperty(value = "email", required = true) String email) {
        this.email = email;
    }
    @JsonProperty(value = "email", required = true) public String getEmail(){
        return email;
    }

}

