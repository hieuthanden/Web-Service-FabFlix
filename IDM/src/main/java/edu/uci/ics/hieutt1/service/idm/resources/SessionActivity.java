package edu.uci.ics.hieutt1.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.idm.IDMService;
import edu.uci.ics.hieutt1.service.idm.core.CheckEmailFormat;
import edu.uci.ics.hieutt1.service.idm.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.idm.models.SessionRequestModel;
import edu.uci.ics.hieutt1.service.idm.models.SessionResponseModel;
import edu.uci.ics.hieutt1.service.idm.security.Session;

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
public class SessionActivity {
    @Path("session")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // Example endpoint to demonstrate salting & hashing
    public Response session(@Context HttpHeaders headers, String jsonText) {
        SessionRequestModel requestModel;
        SessionResponseModel responseModel;
        CheckEmailFormat checkemailformat;

        ObjectMapper mapper = new ObjectMapper();


        // Validate model & map JSON to POJO
        try {
            requestModel = mapper.readValue(jsonText, SessionRequestModel.class);
        } catch (IOException e) {
            // Catch other exceptions here
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new SessionResponseModel(resultCode, "JSON Parse Exception", null);
                ServiceLogger.LOGGER.warning("Unable to parse JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new SessionResponseModel(resultCode, "JSON Mapping Exception", null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new SessionResponseModel(resultCode, "Internal Server Error", null);
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        ServiceLogger.LOGGER.info("Received session request with email and session_id");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        // Get email and password
        String email = requestModel.getEmail();
        String session_id = requestModel.getSession_id();


        checkemailformat = new CheckEmailFormat(email);
        if (checkemailformat.checkLength() == false) {
            responseModel = new SessionResponseModel(-10, "Email address has invalid length", null);
            ServiceLogger.LOGGER.warning("Email has invalid lenght!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        } else if (checkemailformat.checkFormat() == false) {
            responseModel = new SessionResponseModel(-11, "Email address has invalid format", null);
            ServiceLogger.LOGGER.warning("Email has invalid format!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        } else if ( session_id == null ||session_id.length() != 128) {
            responseModel = new SessionResponseModel(-13, "Token has invalid length", null);
            ServiceLogger.LOGGER.warning("Token has invalid length!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        //check if user is in database
        boolean email_in_data = false;
        try {
            String query = "SELECT email, status FROM user;";

            // Create the prepared statement
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            while (rs.next()) {
                String data_email = rs.getString("email");
                int data_status = rs.getInt("status");
                if (data_email.equals(email) && data_status == 1) {
                    email_in_data = true;
                }
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve user email from database.");
            e.printStackTrace();
        }
        if (email_in_data == false) {
            responseModel = new SessionResponseModel(14, "User not found");
            ServiceLogger.LOGGER.warning("User not found in the database");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }

        // retrieve session
        int data_status = 0;
        long data_time_created = 0;
        long data_last_used = 0;
        long data_expr_time = 0;
        try {
            String query = "SELECT status, time_created, last_used, expr_time" +
                    " FROM session" +
                    " WHERE email LIKE ? AND session_id LIKE ?;";

            // Create the prepared statement
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            ps.setString(1, email);
            ps.setString(2, session_id);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            if (rs.next()) {
                data_status = rs.getInt("status");
                data_time_created = rs.getTimestamp("time_created").getTime();
                data_last_used = rs.getTimestamp("last_used").getTime();
                data_expr_time = rs.getTimestamp("expr_time").getTime();
            } else {
                responseModel = new SessionResponseModel(134, "Session not found");
                ServiceLogger.LOGGER.warning("Session not found in the database");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve session from database.");
            e.printStackTrace();
        }

        if (data_status == 2) {
            responseModel = new SessionResponseModel(132, "Session is closed");
            ServiceLogger.LOGGER.warning("Session is closedin the database");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        } else if (data_status == 3) {
            responseModel = new SessionResponseModel(131, "Session is expired");
            ServiceLogger.LOGGER.warning("Session is expired in the database");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        } else if (data_status == 4) {
            responseModel = new SessionResponseModel(133, "Session is revoked");
            ServiceLogger.LOGGER.warning("Session is revoked in the database");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        } else {
            Date current_date = new Date();
            long current_time = current_date.getTime();
            int new_status;
            ServiceLogger.LOGGER.warning("expr time: " + data_expr_time);
            ServiceLogger.LOGGER.warning("current time: " + current_time);
            if (current_time > data_expr_time) {
                new_status = 3;
                responseModel = new SessionResponseModel(131, "Session is expired");
                ServiceLogger.LOGGER.warning("Session is expired in the database");
            } else if ((current_time - data_last_used) > Session.SESSION_TIMEOUT) {
                new_status = 4;
                responseModel = new SessionResponseModel(133, "Session is revoked");
                ServiceLogger.LOGGER.warning("Session is revoked in the database");
            } else {
                new_status = 1;
                responseModel = new SessionResponseModel(130, "Session is active", session_id);
                ServiceLogger.LOGGER.warning("Session is active in the database");
            }
            try {
                String re_query = "UPDATE session SET status=?, last_used=?" +
                        " WHERE session_id=? AND email=?;";
                PreparedStatement re_ps = IDMService.getCon().prepareStatement(re_query);
                re_ps.setInt(1, new_status);
                re_ps.setTimestamp(2, new Timestamp(current_time));
                re_ps.setString(3, session_id);
                re_ps.setString(4, email);

                // Save the query result to a ResultSet so records may be retrieved
                ServiceLogger.LOGGER.info("Trying query: " + re_ps.toString());
                int rs = re_ps.executeUpdate();
                ServiceLogger.LOGGER.info("Query succeeded.");
            } catch (SQLException e) {
                ServiceLogger.LOGGER.warning("Query failed: Unable to update session to database.");
                e.printStackTrace();
            }
            if ( (data_expr_time - current_time) < Session.SESSION_TIMEOUT && (data_expr_time - current_time) > 0){
                // revoke session from database
                try {
                    String del_query = "DELETE FROM session WHERE session_id = ?;";
                    PreparedStatement del_ps = IDMService.getCon().prepareStatement(del_query);
                    del_ps.setString(1, session_id);
                    ServiceLogger.LOGGER.info("Trying query: " + del_ps.toString());
                    int del_rs = del_ps.executeUpdate();
                    ServiceLogger.LOGGER.info("Query succeeded.");
                } catch (SQLException e) {
                    ServiceLogger.LOGGER.warning("Query failed: Unable to update session to database.");
                    e.printStackTrace();
                }
                // create and insert new session to database
                Session session = Session.createSession(email);
                responseModel = new SessionResponseModel(130, "Session is active!", session_id);
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
            }

            //retrieve user counter
            int counter = 0;
            try {
                String query = "SELECT counter FROM user WHERE email LIKE ?;";
                PreparedStatement ps = IDMService.getCon().prepareStatement(query);
                ps.setString(1, email);
                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                ResultSet rs = ps.executeQuery();
                ServiceLogger.LOGGER.info("Query succeeded.");
                if (rs.next()) {
                    counter = rs.getInt("counter");
                }
            } catch (SQLException e) {
                ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve session from database.");
                e.printStackTrace();
            }

            long requestInterval = IDMService.getRequestInterval();
            int maxRapidRequest = IDMService.getMaxRapidRequest();
            int status = 1;
            if (counter >= maxRapidRequest) {
                status = 3;
                counter = 0;
                ServiceLogger.LOGGER.info("User is locked");
            }
            else {
                long interval_time = current_time - (data_last_used + requestInterval);
                ServiceLogger.LOGGER.info("current_time: " + current_time);
                ServiceLogger.LOGGER.info("interval time: " + interval_time);
                if (current_time < (data_last_used + requestInterval)) {
                    counter++;
                    ServiceLogger.LOGGER.info("Counter incresing");
                } else {
                    counter = 0;
                    ServiceLogger.LOGGER.info("Counter reset");
                }
            }

            try {
                String re_query = "UPDATE user SET status=?, counter=? WHERE email=?;";
                PreparedStatement re_ps = IDMService.getCon().prepareStatement(re_query);
                re_ps.setInt(1, status);
                re_ps.setInt(2, counter);
                re_ps.setString(3, email);
                ServiceLogger.LOGGER.info("Trying query: " + re_ps.toString());
                int rs = re_ps.executeUpdate();
                ServiceLogger.LOGGER.info("Query succeeded.");
            } catch (SQLException e) {
                ServiceLogger.LOGGER.warning("Query failed: Unable to update session to database.");
                e.printStackTrace();
            }

            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
    }
}