package edu.uci.ics.hieutt1.service.billing.model.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemModel {

    @JsonProperty(value = "email", required = true) private String email;
    @JsonProperty(value = "unit_price", required = true) private Float unit_price;
    @JsonProperty(value = "discount", required = true) private Float discount;
    @JsonProperty(value = "quantity", required = true) private Integer quantity;
    @JsonProperty(value = "movie_id", required = true) private String movie_id;
    @JsonProperty(value = "movie_title", required = true) private String movie_title;
    @JsonProperty(value = "backdrop_path", required = true) private String backdrop_path;
    @JsonProperty(value = "poster_path", required = true) private String poster_path;

    @JsonCreator
    public ItemModel() {};

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Float getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(Float unit_price) {
        this.unit_price = unit_price;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }
}

