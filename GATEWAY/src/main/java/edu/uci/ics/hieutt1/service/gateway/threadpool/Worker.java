package edu.uci.ics.hieutt1.service.gateway.threadpool;

import edu.uci.ics.hieutt1.service.gateway.GatewayService;
import edu.uci.ics.hieutt1.service.gateway.connectionpool.ConnectionPoolManager;
import edu.uci.ics.hieutt1.service.gateway.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.gateway.models.requestModels.BaseRequestModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.container.ConnectionCallback;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class Worker extends Thread {
    int id;
    ThreadPool threadPool;

    private Worker(int id, ThreadPool threadPool) {
        this.id = id;
        this.threadPool = threadPool;
    }

    public static Worker CreateWorker(int id, ThreadPool threadPool) {
        return new Worker(id, threadPool);
    }

    public void process() {
        ClientRequest clientRequest = threadPool.takeRequest();
        ConnectionPoolManager connectionPoolManager = GatewayService.getConnectionPoolManager();
        String email = clientRequest.getEmail();
        String session_id = clientRequest.getSession_id();
        String transaction_id = clientRequest.getTransaction_id();
        String uri = clientRequest.getURI();
        String endPoint = clientRequest.getEndpoint();
        Map<String, String> query = clientRequest.getQuery();
        String requestType = clientRequest.getRequestType();
        BaseRequestModel requestModel = clientRequest.getRequestModel();

        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(uri).path(endPoint);
        ServiceLogger.LOGGER.info("Built target with uri: " + webTarget.getUri().toString());

        // ADD query params
        if (null != query && !query.isEmpty()) {
            for (String key : query.keySet()) {
                webTarget = webTarget.queryParam(key, query.get(key));
            }
        }
        ServiceLogger.LOGGER.info("Final uri: " + webTarget.getUri().toString());

        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).header("email", email)
                .header("session_id", session_id).header("transaction_id", transaction_id);

        // Send the request and save it to a Response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response;
        if (requestType.contains("POST"))
            response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        else
            response = invocationBuilder.get();
        ServiceLogger.LOGGER.info("Request sent.");

        //get status and jsontext from the respone
        int status = response.getStatus();
        String email_header = response.getHeaderString("email");
        String session_id_header = response.getHeaderString("session_id");
        String transaction_id_header = response.getHeaderString("transaction_id");
        String jsonText = response.readEntity(String.class);

        //get connection and add data to dataase
        Connection con = connectionPoolManager.requestCon();

        String query_string = "INSERT INTO responses(transaction_id, email, session_id, response, http_status) value (";
        if (transaction_id_header == null || transaction_id_header.isEmpty())
            query_string += "'" + transaction_id + "',";
        else
            query_string += "'" + transaction_id_header + "',";
        query_string += "'" + email_header + "',";
        query_string += "'" + session_id_header + "',";
        query_string += "'" + jsonText.replace("'", "''") + "',";
        query_string += "'" + status + "');";
        try {
            // Create the prepared statement
            PreparedStatement ps = con.prepareStatement(query_string);
            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int rs = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeeded.");
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve movies from database.");
            e.printStackTrace();
        }
        connectionPoolManager.releaseCon(con);
    }

    @Override
    public void run() {
        while (true) {
            try {
                process();
                Thread.sleep(GatewayService.getThreadConfigs().getRequestDelay());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
