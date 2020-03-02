package edu.uci.ics.hieutt1.service.gateway.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.gateway.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.gateway.models.requestModels.*;

import javax.ws.rs.core.Response;
import java.io.IOException;

public class MapRequestModel {
    public static BaseRequestModel mapModel(String jsonText, String requestAPI) {
        BaseRequestModel requestModel = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (requestAPI.contains("movies/thumbnail")) {
                requestModel = mapper.readValue(jsonText, ThumbnailRequestModel.class);
                ServiceLogger.LOGGER.severe("Successfully map jsontext to ThumbnailRequestmodel");
            }
            else if (requestAPI.contains("idm/register")){
                requestModel = mapper.readValue(jsonText, RegisterRequestModel.class);
                ServiceLogger.LOGGER.severe("Successfully map jsontext to RegisterRequestmodel");
            }
            else if (requestAPI.contains("idm/login")){
                requestModel = mapper.readValue(jsonText, LoginRequestModel.class);
                ServiceLogger.LOGGER.severe("Successfully map jsontext to LoginRequestmodel");
            }
            else if (requestAPI.contains("idm/session")){
                requestModel = mapper.readValue(jsonText, SessionRequestModel.class);
                ServiceLogger.LOGGER.severe("Successfully map jsontext to SessionRequestmodel");
            }
            else if (requestAPI.contains("idm/privilege")){
                requestModel = mapper.readValue(jsonText, PrivilegeRequestModel.class);
                ServiceLogger.LOGGER.severe("Successfully map jsontext to PrivilegeRequestmodel");
            }
            else if (requestAPI.contains("billing/cart/insert")){
                requestModel = mapper.readValue(jsonText, InsertRequestModel.class);
                ServiceLogger.LOGGER.severe("Successfully map jsontext to CartInsertRequestmodel");
            }
            else if (requestAPI.contains("billing/cart/update")){
                requestModel = mapper.readValue(jsonText, InsertRequestModel.class);
                ServiceLogger.LOGGER.severe("Successfully map jsontext to CartUpdateRequestmodel");
            }
            else if (requestAPI.contains("billing/cart/delete")){
                requestModel = mapper.readValue(jsonText, DeleteRequestModel.class);
                ServiceLogger.LOGGER.severe("Successfully map jsontext to CartDeleteRequestmodel");
            }
            else if (requestAPI.contains("billing/cart/retrieve")){
                requestModel = mapper.readValue(jsonText, RetrieveRequestModel.class);
                ServiceLogger.LOGGER.severe("Successfully map jsontext to CartRetrieveRequestmodel");
            }
            else if (requestAPI.contains("billing/cart/clear")){
                requestModel = mapper.readValue(jsonText, RetrieveRequestModel.class);
                ServiceLogger.LOGGER.severe("Successfully map jsontext to CartClearRequestmodel");
            }
            else if (requestAPI.contains("billing/order/place")){
                requestModel = mapper.readValue(jsonText, RetrieveRequestModel.class);
                ServiceLogger.LOGGER.severe("Successfully map jsontext to OrderPlaceRequestmodel");
            }
            else if (requestAPI.contains("billing/order/retrieve")){
                requestModel = mapper.readValue(jsonText, RetrieveRequestModel.class);
                ServiceLogger.LOGGER.severe("Successfully map jsontext to OrderRetriveRequestmodel");
            }

        } catch (IOException e) {
            // Catch other exceptions here
            e.printStackTrace();
            ServiceLogger.LOGGER.severe("False to map jsontext with thumbnail request model");
        }
        return requestModel;
    }
}
