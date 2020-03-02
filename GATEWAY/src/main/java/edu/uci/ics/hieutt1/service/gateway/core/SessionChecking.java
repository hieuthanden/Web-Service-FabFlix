package edu.uci.ics.hieutt1.service.gateway.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.gateway.GatewayService;
import edu.uci.ics.hieutt1.service.gateway.configs.IdmConfigs;
import edu.uci.ics.hieutt1.service.gateway.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.gateway.models.requestModels.SessionRequestModel;
import edu.uci.ics.hieutt1.service.gateway.models.requestModels.SessionResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.Response;
import java.io.IOException;

public class SessionChecking {
    public static String check(String email, String session_id) {
        IdmConfigs idmConfigs = GatewayService.getIdmConfigs();
        String servicePath = idmConfigs.getScheme() + idmConfigs.getHostName() + ":" + idmConfigs.getPort() + idmConfigs.getPath();
        String endpointPath = idmConfigs.getSessionPath();

        SessionRequestModel requestModel = new SessionRequestModel(email, session_id);
        SessionResponseModel responseModel = null;

        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(servicePath).path(endpointPath);

        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Send the request and save it to a Response
        ServiceLogger.LOGGER.info("Sending request...");
        javax.ws.rs.core.Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Request sent.");

        ServiceLogger.LOGGER.info("Received status " + response.getStatus());
        String jsonText = null;
        try {

            ObjectMapper mapper = new ObjectMapper();
            jsonText = response.readEntity(String.class);
            responseModel = mapper.readValue(jsonText, SessionResponseModel.class);
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO.");

        } catch (IOException e) {
            ServiceLogger.LOGGER.warning("Unable to map response to POJO.");
        }
        // Do work with data contained in response model

        Integer code = responseModel.getResultCode();
        ServiceLogger.LOGGER.info("Geting code for checking session: " + code);
        if (code == 130)
            return null;
        else
            return jsonText;
    }
}
