package edu.uci.ics.hieutt1.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.idm.IDMService;
import edu.uci.ics.hieutt1.service.idm.core.CheckEmailFormat;
import edu.uci.ics.hieutt1.service.idm.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.idm.models.RegisterResponseModel;
import edu.uci.ics.hieutt1.service.idm.models.ResetRequestModel;
import edu.uci.ics.hieutt1.service.idm.models.LogoutResponseModel;
import edu.uci.ics.hieutt1.service.idm.security.Crypto;
import edu.uci.ics.hieutt1.service.idm.security.Session;
import org.apache.commons.codec.binary.Hex;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

@Path("/") // Outer path
public class ResetEndPoint {
    @Path("pword/reset")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response Update(@Context HttpHeaders headers, String jsonText) {
        ResetRequestModel requestModel;
        LogoutResponseModel responseModel;

        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, ResetRequestModel.class);
        } catch (IOException e) {
            // Catch other exceptions here
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new LogoutResponseModel(resultCode, "JSON Parse Exception");
                ServiceLogger.LOGGER.warning("Unable to parse JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new LogoutResponseModel(resultCode, "JSON Mapping Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new LogoutResponseModel(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        ServiceLogger.LOGGER.info("Received session request with email and session_id");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        String email = requestModel.getEmail();
        String reset_token = requestModel.getReset_token();
        char[] password = requestModel.getPassword();

        CheckEmailFormat checkemailformat;
        checkemailformat = new CheckEmailFormat(email);
        if (checkemailformat.checkLength() == false) {
            responseModel = new LogoutResponseModel(-10, "Email address has invalid length");
            ServiceLogger.LOGGER.warning("Email has invalid lenght!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        } else if (checkemailformat.checkFormat() == false) {
            responseModel = new LogoutResponseModel(-11, "Email address has invalid format");
            ServiceLogger.LOGGER.warning("Email has invalid format!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        } else if (password == null || password.length < 7) {
            responseModel = new LogoutResponseModel(-12, "Password has invalid length");
            ServiceLogger.LOGGER.warning("Token has invalid length!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        } else if (password.length > 16) {
            responseModel = new LogoutResponseModel(12, "Password does not meet length requirements");
            ServiceLogger.LOGGER.warning("Token has invalid length!");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        } else if (password.length > 16) {
            responseModel = new LogoutResponseModel(12, "Password does not meet length requirements");
            ServiceLogger.LOGGER.warning("Token has invalid length!");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasNum = false;
        boolean is_illegal = false;
        for (char c : password) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasNum = true;
            } else {
                is_illegal = true;
            }
        }
        //check if the password is legal
        if (is_illegal || !hasLower || !hasUpper || !hasNum) {
            responseModel = new LogoutResponseModel(13, "Password does not meet character requirement!");
            ServiceLogger.LOGGER.warning("Password does not meet character requirement!");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }

        boolean email_in_data = false;
        try {
            String query = "SELECT email FROM user;";

            // Create the prepared statement
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            while (rs.next()) {
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
            responseModel = new LogoutResponseModel(14, "User not found");
            ServiceLogger.LOGGER.warning("User not found in the database");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }

        //retrieve reset_token from database
        String db_reset_token = "";
        try {
            String query = "SELECT reset_token" +
                    " FROM user" +
                    " WHERE email LIKE ?;";

            // Create the prepared statement
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            ps.setString(1, email);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            if (rs.next()) {
                db_reset_token = rs.getString("reset_token");
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve session from database.");
            e.printStackTrace();
        }
        if (!db_reset_token.equals(reset_token)) {
            responseModel = new LogoutResponseModel(152, "Invalid reset token");
            ServiceLogger.LOGGER.warning("Invalid reset token");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }

        byte[] salt = Crypto.genSalt();
        // uSe salt to hash password
        byte[] hashedPassword = Crypto.hashPassword(password, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);

        // Encode salt & password
        String encodedSalt = Hex.encodeHexString(salt);
        String encodedPassword = Hex.encodeHexString(hashedPassword);

        try {
            String query = "UPDATE user SET salt = ?, pword = ? WHERE email LIKE ?;";
            // Create the prepared statement
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            ps.setString(1, encodedSalt);
            ps.setString(2, encodedPassword);
            ps.setString(3, email);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int rs = ps.executeUpdate();

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to insert user data.");
            e.printStackTrace();
        }

        responseModel = new LogoutResponseModel(150, "Password updated successfully");
        ServiceLogger.LOGGER.info("Password updated successfully");
        return Response.status(Response.Status.OK).entity(responseModel).build();
    }
}
