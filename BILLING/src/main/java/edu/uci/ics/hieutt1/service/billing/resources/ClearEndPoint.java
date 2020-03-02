package edu.uci.ics.hieutt1.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.billing.BillingService;
import edu.uci.ics.hieutt1.service.billing.core.CheckEmailFormat;
import edu.uci.ics.hieutt1.service.billing.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.billing.model.RetrieveRequestModel;
import edu.uci.ics.hieutt1.service.billing.model.InsertResponseModel;
import edu.uci.ics.hieutt1.service.billing.core.FindingPrivilege;


import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("cart/clear") // Outer path
public class ClearEndPoint {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // Example endpoint to demonstrate salting & hashing
    public Response DeleteItemCart(@Context HttpHeaders headers, String jsonText) {
        RetrieveRequestModel requestModel;
        InsertResponseModel responseModel;
        ServiceLogger.LOGGER.warning("Clearing item in the cart....");

        String email_header = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        ObjectMapper mapper = new ObjectMapper();

        // Validate model & map JSON to POJO
        try {
            requestModel = mapper.readValue(jsonText, RetrieveRequestModel.class);
        } catch (IOException e) {
            // Catch other exceptions here
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new InsertResponseModel(resultCode, "JSON Parse Exception");
                ServiceLogger.LOGGER.warning("Unable to parse JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new InsertResponseModel(resultCode, "JSON Mapping Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new InsertResponseModel(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        ServiceLogger.LOGGER.info("Received email, movie_id and quantity");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        // Get email and password
        String email = requestModel.getEmail();

        //check email format
        CheckEmailFormat checkEmailFormat= new CheckEmailFormat(email);
        if (checkEmailFormat.checkLength() == false || checkEmailFormat.checkFormat() == false) {
            responseModel = new InsertResponseModel(3150, "Shopping cart operation failed.");
            ServiceLogger.LOGGER.warning("Shopping cart operation failed.");
            Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseModel);
            builder.header("email", email_header);
            builder.header("session_id", session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();
        }


        // retrieve user data from database
        try {
            String query = "SELECT email FROM cart WHERE email = '";
            query += email + "';";

            // Create the prepared statement
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            if (!rs.next())
            {
                responseModel = new InsertResponseModel(312, "Shopping cart item does not exist.");
                ServiceLogger.LOGGER.warning("Shopping cart item does not exist on cart.");
                Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseModel);
                builder.header("email", email_header);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to check item is on the cart.");
            e.printStackTrace();
        }

        try {
            String query = "DELETE FROM cart WHERE email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int rs = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeeded.");
        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to clear the cart");
            e.printStackTrace();
        }
        responseModel = new InsertResponseModel(3140, "Shopping cart cleared successfully.");
        ServiceLogger.LOGGER.warning("Shopping cart cleared successfully.");
        Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseModel);
        builder.header("email", email_header);
        builder.header("session_id", session_id);
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }
}
