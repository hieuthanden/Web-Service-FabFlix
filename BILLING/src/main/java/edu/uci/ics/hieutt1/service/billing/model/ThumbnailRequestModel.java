package edu.uci.ics.hieutt1.service.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ThumbnailRequestModel {
    @JsonProperty(value = "movie_ids", required = true) String[] movie_ids;

    @JsonCreator
    public ThumbnailRequestModel(@JsonProperty(value = "movie_ids", required = true) String[] movie_ids) {
        this.movie_ids = movie_ids;
    }

    @JsonProperty(value = "movie_ids")
    public String[] getMovie_ids() {
        return movie_ids;
    }

    public void setMovie_ids(@JsonProperty(value = "movie_ids", required = true) String[] movie_ids) {
        this.movie_ids = movie_ids;
    }
}
