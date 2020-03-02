package edu.uci.ics.hieutt1.service.gateway.models.requestModels;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PrivilegeRequestModel extends BaseRequestModel {
    @JsonProperty(value = "email", required = true) private String email;
    @JsonProperty(value = "plevel", required = true) private int plevel;

    @JsonCreator
    public PrivilegeRequestModel(@JsonProperty(value = "email", required = true) String email,
                             @JsonProperty(value = "plevel", required = true) int plevel) {
        this.email = email;
        this.plevel = plevel;
    }
    @JsonProperty(value = "email", required = true) public String getEmail(){
        return email;
    }
    @JsonProperty(value = "plevel", required = true)  public int getPlevel() {
        return plevel;
    }
}