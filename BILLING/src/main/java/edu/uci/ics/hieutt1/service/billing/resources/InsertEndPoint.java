package edu.uci.ics.hieutt1.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.billing.BillingService;
import edu.uci.ics.hieutt1.service.billing.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.billing.model.InsertRequestModel;
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
import java.util.Locale;

@Path("cart/insert") // Outer path
public class InsertEndPoint {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // Example endpoint to demonstrate salting & hashing
    public Response InsertCart(@Context HttpHeaders headers, String jsonText) {
        InsertRequestModel requestModel;
        InsertResponseModel responseModel;
        ServiceLogger.LOGGER.warning("Inerting movie to cart....");

        String email_header = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        ObjectMapper mapper = new ObjectMapper();

        // Validate model & map JSON to POJO
        try {
            requestModel = mapper.readValue(jsonText, InsertRequestModel.class);
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
        String movie_id = requestModel.getMovie_id();
        Integer quanlity = requestModel.getQuantity();

        //check if the privilege is qualified for hidden movies
        ServiceLogger.LOGGER.info("Asking for privilege");
        FindingPrivilege findPri = new FindingPrivilege();
        Boolean is_privilege = findPri.findPrivilege(email);
        ServiceLogger.LOGGER.info("GOT privilege for user: " + email);
        if (!is_privilege){
            responseModel = new InsertResponseModel(14, "User not found.");
            ServiceLogger.LOGGER.warning("User not found.");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
        if(quanlity < 1){
            responseModel = new InsertResponseModel(33, "Quantity has invalid value.");
            ServiceLogger.LOGGER.warning("Quantity has invalid value.");
            Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseModel);
            builder.header("email", email_header);
            builder.header("session_id", session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();
        }
        if(movie_id.length() > 10){
            responseModel = new InsertResponseModel(3150, "Shopping cart operation failed.");
            ServiceLogger.LOGGER.warning(" Shopping cart operation failed.");
            Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseModel);
            builder.header("email", email_header);
            builder.header("session_id", session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();
        }

        // retrieve user data from database
        try {
            String query = "SELECT email FROM cart WHERE email = '";
            query += email + "'";
            query += " AND movie_id = '";
            query += movie_id + "';";

            // Create the prepared statement
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            if (rs.next())
            {
                responseModel = new InsertResponseModel(311, "Duplicate insertion.");
                ServiceLogger.LOGGER.warning("Duplicate insertion on cart.");
                Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseModel);
                builder.header("email", email_header);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to check if duplicate in cart.");
            e.printStackTrace();
        }

        try {
            String query = "INSERT INTO cart(email, movie_id, quantity)" +
                    " VALUES(?, ?, ?);";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);

            ps.setString(1, email);
            ps.setString(2, movie_id);
            ps.setInt(3, quanlity);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int rs = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeeded.");
        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable insert movie to cart");
            e.printStackTrace();
        }

        responseModel = new InsertResponseModel(3100, "Shopping cart item inserted successfully.");
        ServiceLogger.LOGGER.warning("Shopping cart item inserted successfully.");
        Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseModel);
        builder.header("email", email_header);
        builder.header("session_id", session_id);
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }
}
