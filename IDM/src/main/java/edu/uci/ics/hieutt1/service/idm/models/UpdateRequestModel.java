package edu.uci.ics.hieutt1.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class UpdateRequestModel {
    @JsonProperty(value = "email", required = true) private String email;
    @JsonProperty(value = "password", required = true) private char[] password;
    @JsonProperty(value = "session_id", required = true) private String session_id;

    @JsonCreator
    public UpdateRequestModel(@JsonProperty(value = "email", required = true) String email,
                             @JsonProperty(value = "password", required = true) char[] password,
                              @JsonProperty(value = "session_id", required = true) String session_id) {
        this.email = email;
        this.password = password;
        this.session_id = session_id;
    }
    @JsonProperty(value = "email", required = true) public String getEmail(){
        return email;
    }
    @JsonProperty(value = "password", required = true)  public char[] getPassword() {
        return password;
    }
    @JsonProperty(value = "session_id", required = true) public String getSession_id() {return session_id;}
}

