package edu.uci.ics.hieutt1.service.gateway.models.requestModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class RegisterRequestModel extends  BaseRequestModel{
    @JsonProperty(value = "email", required = true) private String email;
    @JsonProperty(value = "password", required = true) private char[] password;

    @JsonCreator
    public RegisterRequestModel(@JsonProperty(value = "email", required = true) String email,
                         @JsonProperty(value = "password", required = true) char[] password) {
        this.email = email;
        this.password = password;
    }
    @JsonProperty(value = "email", required = true) public String getEmail(){
        return email;
    }
    @JsonProperty(value = "password", required = true)  public char[] getPassword() {
        return password;
    }
}
