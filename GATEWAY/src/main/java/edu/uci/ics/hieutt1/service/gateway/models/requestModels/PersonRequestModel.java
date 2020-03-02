package edu.uci.ics.hieutt1.service.gateway.models.requestModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonRequestModel extends BaseRequestModel {
    @JsonProperty(value = "name", required = true)
    private String name;
    @JsonProperty(value = "limit", required = false)
    private Integer limit;
    @JsonProperty(value = "offset", required = false)
    private Integer offset;
    @JsonProperty(value = "orderby", required = false)
    private String orderby;
    @JsonProperty(value = "direction", required = false)
    private String direction;
    @JsonProperty(value = "hidden", required = false)
    private Boolean hidden;

    @JsonCreator
    public PersonRequestModel(
            @JsonProperty(value = "name", required = false) String name,
            @JsonProperty(value = "limit", required = false) Integer limit,
            @JsonProperty(value = "offset", required = false) Integer offset,
            @JsonProperty(value = "orderby", required = false) String orderby,
            @JsonProperty(value = "direction", required = false) String direction) {
        this.name = name;
        this.limit = limit;
        this.offset = offset;
        this.orderby = orderby;
        this.direction = direction;
    }
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("limit")
    public Integer getLimit() {
        return limit;
    }
    @JsonProperty("offset")
    public Integer getOffset() {
        return offset;
    }
    @JsonProperty("orderby")
    public String getOrderby() {
        return orderby;
    }
    @JsonProperty("direction")
    public String getDirection() {
        return direction;
    }

    @JsonProperty("hidden")
    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden( @JsonProperty(value = "hidden") Boolean hidden) {
        this.hidden = hidden;
    }


}
