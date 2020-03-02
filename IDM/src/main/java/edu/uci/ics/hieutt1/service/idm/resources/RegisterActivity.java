package edu.uci.ics.hieutt1.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.idm.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.idm.models.RegisterRequestModel;
import edu.uci.ics.hieutt1.service.idm.models.RegisterResponseModel;
import edu.uci.ics.hieutt1.service.idm.security.Crypto;
import edu.uci.ics.hieutt1.service.idm.security.Session;
import edu.uci.ics.hieutt1.service.idm.IDMService;

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
public class RegisterActivity {
    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // Example endpoint to demonstrate salting & hashing
    public Response register(@Context HttpHeaders headers, String jsonText) {
        RegisterRequestModel requestModel;
        RegisterResponseModel responseModel;

        ObjectMapper mapper = new ObjectMapper();

        // Validate model & map JSON to POJO
        try {
            requestModel = mapper.readValue(jsonText, RegisterRequestModel.class);
        } catch (IOException e) {
            // Catch other exceptions here
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new RegisterResponseModel(resultCode, "JSON Parse Exception");
                ServiceLogger.LOGGER.warning("Unable to parse JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new RegisterResponseModel(resultCode, "JSON Mapping Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new RegisterResponseModel(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        ServiceLogger.LOGGER.info("Received register request with email and password");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);
        // Get email

        String email = requestModel.getEmail();

        if (email == null || email.length() < 5) {
            responseModel = new RegisterResponseModel(-10, "Email has invalid length!");
            ServiceLogger.LOGGER.warning("Email has invalid lenght!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        Pattern email_p = Pattern.compile("^\\p{Alnum}+@\\p{Alnum}+\\.\\p{Alnum}+$");
        Matcher m = email_p.matcher(email);
        if (m.matches() == false) {
            responseModel = new RegisterResponseModel(-11, "Email has invalid format!");
            ServiceLogger.LOGGER.warning("Email has invalid format!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        char[] password = requestModel.getPassword();

        if (password == null || password.length < 1) {
            responseModel = new RegisterResponseModel(-12, "Password has invalid length");
            ServiceLogger.LOGGER.warning("Password has invalid lenght!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
        else if (password.length < 7 || password.length > 16) {
            responseModel = new RegisterResponseModel(12, "Password does not meet length requirement");
            ServiceLogger.LOGGER.warning("Password does not meet length requirement!");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasNum = false;
        boolean is_illegal = false;
        for (char c:password) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            }
            else if (Character.isLowerCase(c)) {
                hasLower = true;
            }
            else if (Character.isDigit(c)) {
                hasNum = true;
            }
            else {
                is_illegal = true;
            }
        }
        //check if the password is legal

        if (is_illegal || !hasLower || !hasUpper || !hasNum) {
            responseModel = new RegisterResponseModel(13, "Password does not meet character requirement!");
            ServiceLogger.LOGGER.warning("Password does not meet character requirement!");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }


        // Check if the email is on the database
        Boolean email_in_database = false;
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
                    email_in_database = true;
                }
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve user email from database.");
            e.printStackTrace();
        }

        // Salt & Hash
        // Generate a random salt
        byte[] salt = Crypto.genSalt();
        // uSe salt to hash password
        byte[] hashedPassword = Crypto.hashPassword(password, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);

        // Encode salt & password
        String encodedSalt = Hex.encodeHexString(salt);
        String encodedPassword = Hex.encodeHexString(hashedPassword);

        ServiceLogger.LOGGER.info("email is already in database: " + email_in_database);
        if (email_in_database == true) {
            responseModel = new RegisterResponseModel(16, "Email already in use");
            ServiceLogger.LOGGER.warning("Email already in use!");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
        else {
            try {
                String query = "INSERT INTO user(email, status, plevel, salt, pword)" +
                               " VALUES(?, 1, 5, ?, ?);";
                // Create the prepared statement
                PreparedStatement ps = IDMService.getCon().prepareStatement(query);

                ps.setString(1, email);
                ps.setString(2, encodedSalt);
                ps.setString(3, encodedPassword);

                // Save the query result to a ResultSet so records may be retrieved
                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                int rs = ps.executeUpdate();

            } catch (SQLException e) {
                ServiceLogger.LOGGER.warning("Query failed: Unable to insert user data.");
                e.printStackTrace();
            }
            responseModel = new RegisterResponseModel(110, "User registered successfully.");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
    }
}


