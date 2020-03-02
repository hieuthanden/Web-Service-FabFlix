package edu.uci.ics.hieutt1.service.billing.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.billing.BillingService;
import edu.uci.ics.hieutt1.service.billing.configs.IdmConfigs;
import edu.uci.ics.hieutt1.service.billing.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.billing.model.PrivilegeRequestModel;
import edu.uci.ics.hieutt1.service.billing.model.PrivilegeResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class FindingPrivilege {
    public FindingPrivilege() {
    }

    public Boolean findPrivilege(String email) {
        // Set the path of the endpoint we want to communicate with
        IdmConfigs idmConfigs = BillingService.getIdmConfigs();
        String servicePath = idmConfigs.getScheme() + idmConfigs.getHostName() + ":" + idmConfigs.getPort() + idmConfigs.getPath();
        String endpointPath = idmConfigs.getPrivilegePath();

        PrivilegeRequestModel requestModel = new PrivilegeRequestModel(email, 5);
        PrivilegeResponseModel responseModel = null;

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
        try {

            ObjectMapper mapper = new ObjectMapper();
            String jsonText = response.readEntity(String.class);
            responseModel = mapper.readValue(jsonText, PrivilegeResponseModel.class);
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO.");

        } catch (IOException e) {
            ServiceLogger.LOGGER.warning("Unable to map response to POJO.");
        }
        // Do work with data contained in response model

        Integer code = responseModel.getResultCode();
        if (code == 140)
            return true;
        else
            return false;
    }
}
