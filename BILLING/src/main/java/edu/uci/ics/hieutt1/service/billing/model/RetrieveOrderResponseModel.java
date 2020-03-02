package edu.uci.ics.hieutt1.service.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import edu.uci.ics.hieutt1.service.billing.model.data.TransactionModel;

@JsonInclude(Include.NON_NULL)
public class RetrieveOrderResponseModel {
    @JsonProperty(
            value = "resultCode",
            required = true
    )
    private int resultCode;
    @JsonProperty(
            value = "message",
            required = true
    )
    private String message;
    @JsonProperty("transaction")
    private TransactionModel[] items;

    @JsonCreator
    public RetrieveOrderResponseModel(@JsonProperty(value = "resultCode",required = true) int resultCode, @JsonProperty(value = "message",required = true) String message, @JsonProperty("transaction") TransactionModel[] items) {
        this.resultCode = resultCode;
        this.message = message;
        this.items = items;
    }

    public int getResultCode() {
        return this.resultCode;
    }

    public String getMessage() {
        return this.message;
    }

    public TransactionModel[] getItems() {
        return this.items;
    }
}