package edu.uci.ics.hieutt1.service.movies.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.hieutt1.service.movies.models.data.FullPersonModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetPersonResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "person", required = true)

    private FullPersonModel personModel;

    @JsonCreator
    public GetPersonResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                                     @JsonProperty(value = "message", required = true) String message,
                                     @JsonProperty(value = "person", required = true) FullPersonModel personModel) {

        this.resultCode = resultCode;
        this.message = message;
        this.personModel = personModel;
    }
    @JsonProperty(value = "resultCode", required = true)
    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
    @JsonProperty(value = "message", required = true)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    @JsonProperty(value = "person", required = true)

    public FullPersonModel getPersonModel() {
        return personModel;
    }

    public void setPersonModel(FullPersonModel personModel) {

        this.personModel = personModel;
    }
}
