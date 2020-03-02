package edu.uci.ics.hieutt1.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.idm.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.idm.models.ForgetRequestModel;
import edu.uci.ics.hieutt1.service.idm.models.LogoutResponseModel;
import edu.uci.ics.hieutt1.service.idm.security.Crypto;
import edu.uci.ics.hieutt1.service.idm.security.Session;

import edu.uci.ics.hieutt1.service.idm.security.Token;

import edu.uci.ics.hieutt1.service.idm.IDMService;
import edu.uci.ics.hieutt1.service.idm.core.CheckEmailFormat;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



@Path("/") // Outer path
public class ForgetEndPoint {
    @Path("pword/forget")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // Example endpoint to demonstrate salting & hashing
    public Response login(@Context HttpHeaders headers, String jsonText) {
        ForgetRequestModel requestModel;
        LogoutResponseModel responseModel;
        CheckEmailFormat checkemailformat;

        ObjectMapper mapper = new ObjectMapper();

        // Validate model & map JSON to POJO
        try {
            requestModel = mapper.readValue(jsonText, ForgetRequestModel.class);
        } catch (IOException e) {
            // Catch other exceptions here
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new LogoutResponseModel(resultCode, "JSON parse exception");
                ServiceLogger.LOGGER.warning("Unable to parse JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new LogoutResponseModel(resultCode, "JSON mapping exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new LogoutResponseModel(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        ServiceLogger.LOGGER.info("Received login request with email and password");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        // Get email and password
        String email = requestModel.getEmail();
        checkemailformat = new CheckEmailFormat(email);
        if (checkemailformat.checkLength() == false) {
            responseModel = new LogoutResponseModel(-10, "Email address has invalid length");
            ServiceLogger.LOGGER.warning("Email has invalid lenght!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        } else if (checkemailformat.checkFormat() == false) {
            responseModel = new LogoutResponseModel(-11, "Email address has invalid format");
            ServiceLogger.LOGGER.warning("Email has invalid format!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

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

        byte[] reset_token = Crypto.genSalt();
        String encoded_reset_token = Hex.encodeHexString(reset_token);

        Email send_email = EmailBuilder.startingBlank()
                .from("Hieu Tran", "hieuthanden1@gmail.com")
                .to(email, email)
                .withSubject("DoNotRePly: FabFlix Reset password")
                .withPlainText("The reset password token is: " + encoded_reset_token)
                .buildEmail();
        Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "hieuthanden1@gmail.com", "thuylinh2910")
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer();

        mailer.sendMail(send_email);

        try {
            String re_query = "UPDATE user SET reset_token=?" +
                    " WHERE email=?;";
            PreparedStatement re_ps = IDMService.getCon().prepareStatement(re_query);
            re_ps.setString(1, encoded_reset_token);
            re_ps.setString(2, email);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + re_ps.toString());
            int rs = re_ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeeded.");
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to update session to database.");
            e.printStackTrace();
        }

        responseModel = new LogoutResponseModel(151, "Reset token emailed successfully");
        ServiceLogger.LOGGER.info("Reset token emailed successfully");
        return Response.status(Response.Status.OK).entity(responseModel).build();
    }
}
