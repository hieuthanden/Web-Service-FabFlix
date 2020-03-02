package edu.uci.ics.hieutt1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrowseRequestModel {
    @JsonProperty(value = "hidden", required = false)
    private Boolean hidden;
    @JsonProperty(value = "limit", required = false)
    private Integer limit;
    @JsonProperty(value = "offset", required = false)
    private Integer offset;
    @JsonProperty(value = "orderby", required = false)
    private String orderby;
    @JsonProperty(value = "direction", required = false)
    private String direction;

    @JsonCreator
    public BrowseRequestModel(
                              @JsonProperty(value = "limit", required = false) Integer limit,
                              @JsonProperty(value = "offset", required = false) Integer offset,
                              @JsonProperty(value = "orderby", required = false) String orderby,
                              @JsonProperty(value = "direction", required = false) String direction) {
        this.hidden = null;
        this.limit = limit;
        this.offset = offset;
        this.orderby = orderby;
        this.direction = direction;
    }

    @JsonProperty("hidden")
    public Boolean getHidden() {
        return hidden;
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

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}
