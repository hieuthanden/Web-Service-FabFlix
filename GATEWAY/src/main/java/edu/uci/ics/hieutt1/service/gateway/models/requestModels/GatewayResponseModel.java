package edu.uci.ics.hieutt1.service.gateway.models.requestModels;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatewayResponseModel {
    @JsonProperty(value = "transaction_id", required = true)
    private String transaction_id;

    @JsonProperty(value = "time_interval", required = true)
    private long time_interval;

    @JsonCreator
    public GatewayResponseModel(String transaction_id, long time_interval) {
        this.transaction_id = transaction_id;
        this.time_interval = time_interval;
    }
}
