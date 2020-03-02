package edu.uci.ics.hieutt1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.hieutt1.service.movies.models.data.MovieModel;

// Example response model

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponseModel{

    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "movies", required = true)
    private MovieModel[] movieModel;

    @JsonCreator
    public SearchResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                                @JsonProperty(value = "message", required = true) String message,
                                @JsonProperty(value = "movies", required =  true) MovieModel[] movieModel) {
        this.resultCode = resultCode;
        this.message = message;
        this.movieModel = movieModel;
    }
    @JsonCreator
    public SearchResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                               @JsonProperty(value = "message", required = true) String message) {
        this.resultCode = resultCode;
        this.message = message;

    }

    @JsonProperty("resultCode")
    public int getResultCode(){
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage(){
        return message;
    }

    @JsonProperty("movies")
    public MovieModel[] getSum() {
        return movieModel;
    }

}