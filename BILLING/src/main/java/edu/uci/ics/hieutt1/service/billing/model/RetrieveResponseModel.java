package edu.uci.ics.hieutt1.service.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.hieutt1.service.billing.model.data.ItemModel;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class RetrieveResponseModel {

    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "items")
    private ItemModel[] items;

    @JsonCreator
    public RetrieveResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                               @JsonProperty(value = "message", required = true) String message,
                                 @JsonProperty(value = "items") ItemModel[] items) {

        this.resultCode = resultCode;
        this.message = message;
        this.items = items;
    }

    @JsonProperty("resultCode")
    public int getResultCode() { return resultCode; }

    @JsonProperty("message")
    public String getMessage() { return message; }

    @JsonProperty("items")
    public ItemModel[] getItems() {return items;}
}
