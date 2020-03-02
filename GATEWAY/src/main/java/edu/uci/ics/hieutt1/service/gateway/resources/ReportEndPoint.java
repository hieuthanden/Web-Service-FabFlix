package edu.uci.ics.hieutt1.service.gateway.resources;

import edu.uci.ics.hieutt1.service.gateway.GatewayService;
import edu.uci.ics.hieutt1.service.gateway.connectionpool.ConnectionPoolManager;
import edu.uci.ics.hieutt1.service.gateway.core.DeleteTransaction;
import edu.uci.ics.hieutt1.service.gateway.core.MapQuerry;
import edu.uci.ics.hieutt1.service.gateway.core.MapRequestModel;
import edu.uci.ics.hieutt1.service.gateway.core.SessionChecking;
import edu.uci.ics.hieutt1.service.gateway.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.gateway.models.requestModels.*;
import edu.uci.ics.hieutt1.service.gateway.threadpool.ClientRequest;
import edu.uci.ics.hieutt1.service.gateway.threadpool.ThreadPool;
import edu.uci.ics.hieutt1.service.gateway.transaction.TransactionGenerator;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

@Path("report") // Outer path
public class ReportEndPoint {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response ReportRestCall(@Context HttpHeaders headers){

        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        if( email != null && session_id != null) {
            String session_json = SessionChecking.check(email, session_id);
            if (session_json != null) {
                Response.ResponseBuilder builder = Response.status(Response.Status.OK);
                builder.header("email", email);
                builder.header("session_id", session_id);
                return builder.entity(session_json).build();
            }
        }

        String query = "SELECT * FROM responses WHERE transaction_id LIKE '";
        query += transaction_id + "';";

        try {
            // Create the prepared statement
            Connection con = GatewayService.getConnectionPoolManager().requestCon();
            PreparedStatement ps = con.prepareStatement(query);
            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            if (rs.next()) {
                int statusCode = rs.getInt("http_status");
                String response = rs.getString("response");
                ServiceLogger.LOGGER.info("http_status = " + statusCode + "; response: " + response);
                GatewayService.getConnectionPoolManager().releaseCon(con);
                boolean is_delete = DeleteTransaction.delete(transaction_id);
                if (is_delete == true)
                    return Response.status(Response.Status.fromStatusCode(statusCode)).entity(response).build();
                else
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            else
            {
                GatewayService.getConnectionPoolManager().releaseCon(con);
                Response.ResponseBuilder builder;
                builder = Response.status(Response.Status.NO_CONTENT);
                builder.header("massage", "waiting for content...");
                if (email != null)
                    builder.header("email", email);
                if (session_id != null)
                    builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                builder.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());
                return builder.build();
            }
        }catch (SQLException e) {
                ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve movies from database.");
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }
}
