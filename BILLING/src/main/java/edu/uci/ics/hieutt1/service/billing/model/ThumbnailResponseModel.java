package edu.uci.ics.hieutt1.service.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import edu.uci.ics.hieutt1.service.billing.model.data.ThumbnailModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ThumbnailResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "thumbnails")
    private ThumbnailModel[] thumbnailModels;

    @JsonCreator
    public ThumbnailResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                                  @JsonProperty(value = "message", required = true) String message,
                                  @JsonProperty(value = "thumbnails") ThumbnailModel[] thumbnailModels) {
        this.resultCode = resultCode;
        this.message = message;
        this.thumbnailModels = thumbnailModels;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }

    public ThumbnailModel[] getThumbnailModels() {
        return thumbnailModels;
    }
}
