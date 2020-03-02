package edu.uci.ics.hieutt1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchPersonRequestModel {
    @JsonProperty(value = "name", required = false)
    private String name;
    @JsonProperty(value = "birthday", required = false)
    private String birthday;
    @JsonProperty(value = "movie_title", required = false)
    private String movie_title;
    @JsonProperty(value = "limit", required = false)
    private Integer limit;
    @JsonProperty(value = "offset", required = false)
    private Integer offset;
    @JsonProperty(value = "orderby", required = false)
    private String orderby;
    @JsonProperty(value = "direction", required = false)
    private String direction;

    @JsonCreator
    public SearchPersonRequestModel(String name, String birthday, String movie_title, Integer limit, Integer offset, String orderby, String direction) {
        this.name = name;
        this.birthday = birthday;
        this.movie_title = movie_title;
        this.limit = limit;
        this.offset = offset;
        this.orderby = orderby;
        this.direction = direction;
    }
    @JsonProperty(value = "name")
    public String getName() {
        return name;
    }

    @JsonProperty(value = "birthday")
    public String getBirthday() {
        return birthday;
    }

    @JsonProperty(value = "movie_title")
    public String getMovie_title() {
        return movie_title;
    }

    @JsonProperty(value = "limit")
    public Integer getLimit() {
        return limit;
    }

    @JsonProperty(value = "offset")
    public Integer getOffset() {
        return offset;
    }

    @JsonProperty(value = "orderby")
    public String getOrderby() {
        return orderby;
    }

    @JsonProperty(value = "direction")
    public String getDirection() {
        return direction;
    }
}
