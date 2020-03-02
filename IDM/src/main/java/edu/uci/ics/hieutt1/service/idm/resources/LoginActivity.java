package edu.uci.ics.hieutt1.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.idm.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.idm.models.LoginRequestModel;
import edu.uci.ics.hieutt1.service.idm.models.LoginResponseModel;
import edu.uci.ics.hieutt1.service.idm.security.Crypto;
import edu.uci.ics.hieutt1.service.idm.security.Session;

import edu.uci.ics.hieutt1.service.idm.security.Token;

import edu.uci.ics.hieutt1.service.idm.IDMService;
import edu.uci.ics.hieutt1.service.idm.core.CheckEmailFormat;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



@Path("/") // Outer path

public class LoginActivity {
    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // Example endpoint to demonstrate salting & hashing
    public Response login(@Context HttpHeaders headers, String jsonText) {
        LoginRequestModel requestModel;
        LoginResponseModel responseModel;
        CheckEmailFormat checkemailformat;

        ObjectMapper mapper = new ObjectMapper();

        // Validate model & map JSON to POJO
        try {
            requestModel = mapper.readValue(jsonText, LoginRequestModel.class);
        } catch (IOException e) {
            // Catch other exceptions here
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new LoginResponseModel(resultCode, "JSON parse exception", null);
                ServiceLogger.LOGGER.warning("Unable to parse JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new LoginResponseModel(resultCode, "JSON mapping exception", null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new LoginResponseModel(resultCode, "Internal Server Error", null);
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        ServiceLogger.LOGGER.info("Received login request with email and password");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        // Get email and password
        String email = requestModel.getEmail();
        checkemailformat = new CheckEmailFormat(email);
        char[] password = requestModel.getPassword();

        //check if the password and emal is legal
        if (password == null || password.length < 1) {
            responseModel = new LoginResponseModel(-12, "Password has invalid length", null);
            ServiceLogger.LOGGER.warning("Password has invalid lenght!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
        else if (checkemailformat.checkLength() == false) {
            responseModel = new LoginResponseModel(-10, "Email address has invalid length", null);
            ServiceLogger.LOGGER.warning("Email has invalid lenght!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
        else if (checkemailformat.checkFormat() == false) {
            responseModel = new LoginResponseModel(-11, "Email address has invalid format", null);
            ServiceLogger.LOGGER.warning("Email has invalid format!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        // Check if the email is on the database
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
            responseModel = new LoginResponseModel(14, "User not found", null);
            ServiceLogger.LOGGER.warning("User not found in the database");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }

        // retrieve salt and password of user from database
        String data_salt = "";
        String data_pword = "";
        try {
            String query = "SELECT salt, pword FROM user WHERE email LIKE ?;";

            // Create the prepared statement
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            ps.setString(1, email);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            while (rs.next()){
                data_salt = rs.getString("salt");
                data_pword = rs.getString("pword");
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve user email from database.");
            e.printStackTrace();
        }
        try {
            byte[] salt = Hex.decodeHex(data_salt);
            byte[] hashedPassword = Crypto.hashPassword(password, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);
            String encodedPassword = Hex.encodeHexString(hashedPassword);
            // check if passwords matchjava -jar build/libs/edu.uci.ics.hieutt1.service.idm.jar -c config.yaml
            if (!encodedPassword.equals(data_pword)) {
                responseModel = new LoginResponseModel(11, "Passwords do not match", null);
                ServiceLogger.LOGGER.warning("Passwords do not match");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
        }
        catch (DecoderException e){
            ServiceLogger.LOGGER.warning("Decoder: false to decode salt from database");
        }
        // Generate session
        Session session = Session.createSession(requestModel.getEmail());

        // check if user already has active session
        try {
            String query = "SELECT status FROM session WHERE email like ?;";

            // Create the prepared statement
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            ps.setString(1, email);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            while (rs.next()){
                int status = rs.getInt("status");
                if (status == 1) {
                    String del_query = "DELETE FROM session WHERE email = ?;";
                    PreparedStatement del_ps = IDMService.getCon().prepareStatement(del_query);
                    del_ps.setString(1, email);
                    ServiceLogger.LOGGER.info("Trying query: " + del_ps.toString());
                    int del_rs = del_ps.executeUpdate();
                    ServiceLogger.LOGGER.info("Query succeeded.");
                }
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve user email from database.");
            e.printStackTrace();
        }
        try {
            String query = "INSERT INTO session(session_id, email, status, time_created, last_used, expr_time)" +
                    " VALUES(?, ?, 1, ?, ?, ?);";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            ps.setString(1, session.getSessionID().toString());
            ps.setString(2, session.getEmail());
            ps.setString(3, session.getTimeCreated().toString());
            ps.setString(4, session.getLastUsed().toString());
            ps.setString(5, session.getExprTime().toString());

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int rs = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeeded.");
        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve user email from database.");
            e.printStackTrace();
        }
        responseModel = new LoginResponseModel(120, "User logged in successfully", session.getSessionID().toString());
        return Response.status(Response.Status.OK).entity(responseModel).build();
    }
}


