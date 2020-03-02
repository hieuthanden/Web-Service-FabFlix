package edu.uci.ics.hieutt1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.hieutt1.service.movies.models.data.ThumbnailModel;

@JsonInclude(JsonInclude.Include.NON_NULL) // Tells Jackson to ignore all fields with value of NULL

public class ThumbnailResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "thumbnails")
    private ThumbnailModel[] thumbnailModel;

    @JsonCreator
    public ThumbnailResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                               @JsonProperty(value = "message", required = true) String message,
                               @JsonProperty(value = "thumbnails") ThumbnailModel[] thumbnailModel) {
        this.resultCode = resultCode;
        this.message = message;
        this.thumbnailModel = thumbnailModel;
    }

    @JsonCreator
    public ThumbnailResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
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

    @JsonProperty("thumbnails")
    public ThumbnailModel[] getThumbnailModel() {
        return thumbnailModel ;
    }
}
