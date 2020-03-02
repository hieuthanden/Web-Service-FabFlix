package edu.uci.ics.hieutt1.service.movies.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.movies.configs.IdmConfigs;
import edu.uci.ics.hieutt1.service.movies.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.movies.MoviesService;
import edu.uci.ics.hieutt1.service.movies.models.SessionRequestModel;
import edu.uci.ics.hieutt1.service.movies.models.SessionResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class CheckingAccount {
    public CheckingAccount() {
    }

    public static int Check(String email, String session_id) {
        // Set the path of the endpoint we want to communicate with
        IdmConfigs idmConfigs = MoviesService.getIdmConfigs();
        String servicePath = idmConfigs.getScheme() + idmConfigs.getHostName() + ":" + idmConfigs.getPort() + idmConfigs.getPath();
        String endpointPath = idmConfigs.getSessionPath();
        ServiceLogger.LOGGER.info("servicePath: " + servicePath);
        ServiceLogger.LOGGER.info("endpointPath: " + endpointPath);

        SessionRequestModel requestModel = new SessionRequestModel(email, session_id);
        SessionResponseModel responseModel;

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
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Request sent.");

        ServiceLogger.LOGGER.info("Received status " + response.getStatus());

        Integer code = 0;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonText = response.readEntity(String.class);
            responseModel = mapper.readValue(jsonText, SessionResponseModel.class);
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO.");
            code = responseModel.getResultCode();

        } catch (IOException e) {
            ServiceLogger.LOGGER.warning("Unable to map response to POJO.");
        }
        // Do work with data contained in response model

        return code;
    }
}

