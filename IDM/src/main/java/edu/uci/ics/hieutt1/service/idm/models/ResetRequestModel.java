package edu.uci.ics.hieutt1.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ResetRequestModel {
    @JsonProperty(value = "email", required = true) private String email;
    @JsonProperty(value = "reset_token", required = true) private String reset_token;
    @JsonProperty(value = "password", required = true) private char[] password;

    @JsonCreator
    public ResetRequestModel(@JsonProperty(value = "email", required = true) String email,
                                @JsonProperty(value = "reset_token", required = true) String reset_token,
                                @JsonProperty(value = "password", required = true) char[] password) {
        this.email = email;
        this.reset_token = reset_token;
        this.password = password;
    }
    @JsonProperty(value = "email", required = true) public String getEmail(){
        return email;
    }
    @JsonProperty(value = "password", required = true)  public char[] getPassword() {
        return password;
    }
    @JsonProperty(value = "reset_token", required = true) public String getReset_token() {return reset_token;}
}
