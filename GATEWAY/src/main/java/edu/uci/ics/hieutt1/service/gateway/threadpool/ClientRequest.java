package edu.uci.ics.hieutt1.service.gateway.threadpool;

import edu.uci.ics.hieutt1.service.gateway.models.requestModels.BaseRequestModel;

import java.util.Map;

public class ClientRequest {
    private String email;
    private String session_id;
    private String transaction_id;
    private String URI;
    private String endpoint;
    private Map<String, String> query;
    private String requestType;

    //TODO Add request model data members
    BaseRequestModel requestModel;

    public ClientRequest() {

    }

    public ClientRequest(String email, String session_id, String transaction_id,
                         String URI, String endpoint, Map<String, String>  query, BaseRequestModel requestModel, String requestType) {
        this.email = email;
        this.session_id = session_id;
        this.transaction_id = transaction_id;
        this.URI = URI;
        this.endpoint = endpoint;
        this.requestModel = requestModel;
        this.query = query;
        this.requestType = requestType;
    }

    public String getEmail() {
        return email;
    }

    public String getSession_id() {
        return session_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public String getURI() {
        return URI;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public BaseRequestModel getRequestModel() {
        return requestModel;
    }

    public Map<String, String> getQuery() {
        return query;
    }
    public String getRequestType(){
        return requestType;
    }
}
