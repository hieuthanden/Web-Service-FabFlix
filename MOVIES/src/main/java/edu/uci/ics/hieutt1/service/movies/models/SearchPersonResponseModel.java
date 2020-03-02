package edu.uci.ics.hieutt1.service.movies.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.hieutt1.service.movies.models.data.DetailPersonModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchPersonResponseModel {
    @JsonProperty(value = "resultCode")
    private int resultCode;
    @JsonProperty(value = "message")
    private String message;
    @JsonProperty(value = "people")
    private DetailPersonModel[] movieModel;

    @JsonCreator
    public SearchPersonResponseModel(@JsonProperty(value = "resultCode") int resultCode,
                                           @JsonProperty(value = "message") String message,
                                           @JsonProperty(value = "people") DetailPersonModel[] movieModel) {
        this.resultCode = resultCode;
        this.message = message;
        this.movieModel = movieModel;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DetailPersonModel[] getMovieModel() {
        return movieModel;
    }

    public void setMovieModel(DetailPersonModel[] movieModel) {
        this.movieModel = movieModel;
    }
}
