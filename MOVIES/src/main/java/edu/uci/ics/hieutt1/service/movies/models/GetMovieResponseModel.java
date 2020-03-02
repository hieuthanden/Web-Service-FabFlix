package edu.uci.ics.hieutt1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.hieutt1.service.movies.models.data.FullMovieModel;



// Example response model

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMovieResponseModel{

    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "movie", required = true)
    private FullMovieModel movieModel;

    @JsonCreator
    public GetMovieResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                               @JsonProperty(value = "message", required = true) String message,
                               @JsonProperty(value = "movie", required =  true) FullMovieModel movieModel) {
        this.resultCode = resultCode;
        this.message = message;
        this.movieModel = movieModel;
    }

    @JsonProperty("resultCode")
    public int getResultCode(){
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage(){
        return message;
    }

    @JsonProperty("movie")
    public FullMovieModel getSum() {
        return movieModel;
    }

}