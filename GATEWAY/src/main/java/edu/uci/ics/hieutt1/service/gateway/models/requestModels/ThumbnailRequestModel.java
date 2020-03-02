package edu.uci.ics.hieutt1.service.gateway.models.requestModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ThumbnailRequestModel extends BaseRequestModel{
    @JsonProperty(value = "movie_ids", required = true) private String[] movies_ids;

    @JsonCreator
    public ThumbnailRequestModel() {
    }

    @JsonCreator
    public ThumbnailRequestModel(@JsonProperty(value = "movie_ids", required = true) String[] movies_ids) {
        this.movies_ids = movies_ids ;
    }
    @JsonProperty(value = "movie_ids", required = true)  public String[] getMoive_ids() {
        return movies_ids;
    }
}
