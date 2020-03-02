package edu.uci.ics.hieutt1.service.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PlaceOrderResponseModel {
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
    @JsonProperty("approve_url")
    private String approve_url;
    @JsonProperty("token")
    private String token;

    @JsonCreator
    public PlaceOrderResponseModel(int resultCode, String message, String approve_url, String token) {
        this.resultCode = resultCode;
        this.message = message;
        this.approve_url = approve_url;
        this.token = token;
    }

    public int getResultCode() {
        return this.resultCode;
    }

    public String getMessage() {
        return this.message;
    }

    public String getApprove_url() {
        return this.approve_url;
    }

    public String getToken() {
        return this.token;
    }
}