package edu.uci.ics.hieutt1.service.billing.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.billing.BillingService;
import edu.uci.ics.hieutt1.service.billing.configs.IdmConfigs;
import edu.uci.ics.hieutt1.service.billing.configs.MoviesConfigs;
import edu.uci.ics.hieutt1.service.billing.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.billing.model.ThumbnailRequestModel;
import edu.uci.ics.hieutt1.service.billing.model.ThumbnailResponseModel;
import edu.uci.ics.hieutt1.service.billing.model.data.ThumbnailModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class GetMovieThumbnail {

    public GetMovieThumbnail() {
    }

    public ThumbnailModel[] getThumbnail(String[] movie_list){
        // Set the path of the endpoint we want to communicate with
        MoviesConfigs moviesConfigs = BillingService.getMoviesConfigs();
        String servicePath = moviesConfigs.getScheme() + moviesConfigs.getHostName() + ":" + moviesConfigs.getPort() + moviesConfigs.getPath();
        String endpointPath = moviesConfigs.getThumbnailPath();
        System.out.println(servicePath);
        System.out.println(endpointPath);

        ThumbnailRequestModel requestModel = new ThumbnailRequestModel(movie_list);
        ThumbnailResponseModel responseModel = null;

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
            responseModel = mapper.readValue(jsonText, ThumbnailResponseModel.class);
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO.");

        } catch (IOException e) {
            ServiceLogger.LOGGER.warning("Unable to map response to POJO.");
        }
        // Do work with data contained in response model
        ServiceLogger.LOGGER.info("ResultCode: " + responseModel.getResultCode());

        if(responseModel.getResultCode() == 210)
        {
            ThumbnailModel[] thumbnailModels = responseModel.getThumbnailModels();
            ServiceLogger.LOGGER.info("successfully return movies thumbnail");
            return thumbnailModels;
        }
        else {
            ServiceLogger.LOGGER.info("Unsuccessfully return movies thumbnail");
            return null;
        }
    }

}
