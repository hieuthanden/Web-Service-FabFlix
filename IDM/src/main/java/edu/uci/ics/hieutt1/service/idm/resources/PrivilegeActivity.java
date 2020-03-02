package edu.uci.ics.hieutt1.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.idm.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.idm.models.PrivilegeRequestModel;
import edu.uci.ics.hieutt1.service.idm.models.PrivilegeResponseModel;

import edu.uci.ics.hieutt1.service.idm.IDMService;
import edu.uci.ics.hieutt1.service.idm.core.CheckEmailFormat;


import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



@Path("/") // Outer path

public class PrivilegeActivity {
    @Path("privilege")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // Example endpoint to demonstrate salting & hashing
    public Response privilege(@Context HttpHeaders headers, String jsonText) {
        PrivilegeRequestModel requestModel;
        PrivilegeResponseModel responseModel;
        CheckEmailFormat checkemailformat;

        ObjectMapper mapper = new ObjectMapper();

        // Validate model & map JSON to POJO
        try {
            requestModel = mapper.readValue(jsonText, PrivilegeRequestModel.class);
        } catch (IOException e) {
            // Catch other exceptions here
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new PrivilegeResponseModel(resultCode, "JSON Parse Exception");
                ServiceLogger.LOGGER.warning("Unable to parse JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new PrivilegeResponseModel(resultCode, "JSON Mapping Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new PrivilegeResponseModel(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        ServiceLogger.LOGGER.info("Received login request with email and password");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        // Get email and password
        String email = requestModel.getEmail();
        checkemailformat = new CheckEmailFormat(email);
        int plevel = requestModel.getPlevel();

        if (plevel < 1 || plevel > 5) {
            responseModel = new PrivilegeResponseModel(-14, "Privilege level out of valid range");
            ServiceLogger.LOGGER.warning("Privilege level out of valid range!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
        else if (checkemailformat.checkLength() == false) {
            responseModel = new PrivilegeResponseModel(-10, "Email address has invalid length");
            ServiceLogger.LOGGER.warning("Email has invalid lenght!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
        else if (checkemailformat.checkFormat() == false) {
            responseModel = new PrivilegeResponseModel(-11, "Email address has invalid format");
            ServiceLogger.LOGGER.warning("Email has invalid format!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        // check if use is on the database
        boolean email_in_data = false;
        try {
            String query = "SELECT email FROM user;";

            // Create the prepared statement
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            while (rs.next()){
                String data_email = rs.getString("email");
                if (data_email.equals(email)) {
                    email_in_data = true;
                }
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve user email from database.");
            e.printStackTrace();
        }
        if (email_in_data == false) {
            responseModel = new PrivilegeResponseModel(14, "User not found");
            ServiceLogger.LOGGER.warning("User not found in the database");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }

        // retrieve user data from database
        int data_plevel = 0;
        try {
            String query = "SELECT plevel FROM user WHERE email LIKE ?;";

            // Create the prepared statement
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            ps.setString(1, email);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            rs.next();
            data_plevel = rs.getInt("plevel");
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve user email from database.");
            e.printStackTrace();
        }

        if (data_plevel <= plevel) {
            responseModel = new PrivilegeResponseModel(140, "User has sufficient privilege level");
            ServiceLogger.LOGGER.warning("User has sufficient privilege level");
        }
        else {
            responseModel = new PrivilegeResponseModel(141, "User has insufficient privilege level");
            ServiceLogger.LOGGER.warning("User has insufficient privilege level");
        }
        return Response.status(Response.Status.OK).entity(responseModel).build();
    }
}
