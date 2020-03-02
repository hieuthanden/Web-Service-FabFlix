package edu.uci.ics.hieutt1.service.gateway.models.requestModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchRequestModel extends BaseRequestModel{
    @JsonProperty(value = "title", required = false)
    private String title;
    @JsonProperty(value = "year", required = false)
    private Integer year;
    @JsonProperty(value = "director", required = false)
    private String director;
    @JsonProperty(value = "genre", required = false)
    private String genre;
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
    public SearchRequestModel(@JsonProperty(value = "title", required = false) String title,
                              @JsonProperty(value = "year", required = false) Integer year,
                              @JsonProperty(value = "director", required = false) String director,
                              @JsonProperty(value = "genre", required = false) String genre,
                              @JsonProperty(value = "limit", required = false) Integer limit,
                              @JsonProperty(value = "offset", required = false) Integer offset,
                              @JsonProperty(value = "orderby", required = false) String orderby,
                              @JsonProperty(value = "direction", required = false) String direction) {
        this.title = title;
        this.year = year;
        this.director = director;
        this.genre = genre;
        this.hidden = null;
        this.limit = limit;
        this.offset = offset;
        this.orderby = orderby;
        this.direction = direction;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }
    @JsonProperty("year")
    public Integer getYear() {
        return year;
    }
    @JsonProperty("director")
    public String getDirector() {
        return director;
    }
    @JsonProperty("genre")
    public String getGenre() {
        return genre;
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

    @JsonCreator
    public void setHidden(@JsonProperty(value = "hidden", required = false) Boolean hidden) {
        this.hidden = hidden;
    }
}
