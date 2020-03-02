package edu.uci.ics.hieutt1.service.gateway.models.requestModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InsertRequestModel extends BaseRequestModel{

    @JsonProperty(value = "email", required = true) private String email;
    @JsonProperty(value = "movie_id", required = true)private String movie_id;
    @JsonProperty(value = "quantity", required = true)private Integer quantity;

    @JsonCreator
    public InsertRequestModel(@JsonProperty(value = "email", required = true) String email,
                              @JsonProperty(value = "movie_id", required = true) String movie_id,
                              @JsonProperty(value = "quantity", required = true) Integer quantity) {
        this.email = email;
        this.movie_id = movie_id;
        this.quantity = quantity;
    }


    public String getEmail() {
        return email;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
