package edu.uci.ics.hieutt1.service.gateway.resources;

import edu.uci.ics.hieutt1.service.gateway.GatewayService;
import edu.uci.ics.hieutt1.service.gateway.connectionpool.ConnectionPoolManager;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

@Path("{path : .+}") // Outer path

public class GatewayEndPoint {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response GatewayPostCall(@Context HttpHeaders headers, @PathParam("path") String path
            , @Context UriInfo uriinfo, String jsonText) throws UnsupportedEncodingException {
        System.out.println("Path: " + uriinfo.getPath());
        System.out.println("AsolutePath: " + uriinfo.getAbsolutePath());
        System.out.println("Base URI " + uriinfo.getBaseUri());
        System.out.println("Request URI " + uriinfo.getRequestUri());
        System.out.println("What we want " + uriinfo.getAbsolutePath().relativize(uriinfo.getRequestUri()).toString());

        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = TransactionGenerator.generate();
        // checking if session is valid
        if (uriinfo.getPath().contains("movies") || uriinfo.getPath().contains("billing")) {
            String session_json = SessionChecking.check(email, session_id);
            if (session_json != null) {
                Response.ResponseBuilder builder = Response.status(Response.Status.OK);
                builder.header("email", email);
                builder.header("session_id", session_id);
                return builder.entity(session_json).build();
            }
        }

        String uri = null;
        String endpoint = null;
        String requestType = null;
        URL url = null;
        try {
            url = uriinfo.getRequestUri().toURL();
        } catch (MalformedURLException e){
            ServiceLogger.LOGGER.info("Cannot convert form URI to URL");
        }
        Map<String, String> queries = null;
        if (uriinfo.getRequestUri().toString().contains("?"))
            queries = MapQuerry.splitQuery(url);

        ThreadPool threadPool =  GatewayService.getThreadPool();
        ConnectionPoolManager connectionPoolManager = GatewayService.getConnectionPoolManager();
        ClientRequest clientRequest;
        BaseRequestModel requestModel = null;

        if (uriinfo.getPath().contains("idm/register")){
            requestModel = MapRequestModel.mapModel(jsonText, "idm/register");
            uri = GatewayService.getIdmConfigs().getScheme() + GatewayService.getIdmConfigs().getHostName()  + ":" +
                    GatewayService.getIdmConfigs().getPort() + GatewayService.getIdmConfigs().getPath();
            endpoint = GatewayService.getIdmConfigs().getRegisterPath();
            requestType = "POST";
        }
        else if (uriinfo.getPath().contains("idm/login")){
            requestModel = MapRequestModel.mapModel(jsonText, "idm/login");
            uri = GatewayService.getIdmConfigs().getScheme() + GatewayService.getIdmConfigs().getHostName()  + ":" +
                    GatewayService.getIdmConfigs().getPort() + GatewayService.getIdmConfigs().getPath();
            endpoint = GatewayService.getIdmConfigs().getLoginPath();
            requestType = "POST";
        }
        else if (uriinfo.getPath().contains("idm/session")){
            requestModel = MapRequestModel.mapModel(jsonText, "idm/session");
            uri = GatewayService.getIdmConfigs().getScheme() + GatewayService.getIdmConfigs().getHostName()  + ":" +
                    GatewayService.getIdmConfigs().getPort() + GatewayService.getIdmConfigs().getPath();
            endpoint = GatewayService.getIdmConfigs().getSessionPath();
            requestType = "POST";
        }
        else if (uriinfo.getPath().contains("idm/privilege")){
            requestModel = MapRequestModel.mapModel(jsonText, "idm/privilege");
            uri = GatewayService.getIdmConfigs().getScheme() + GatewayService.getIdmConfigs().getHostName()  + ":" +
                    GatewayService.getIdmConfigs().getPort() + GatewayService.getIdmConfigs().getPath();
            endpoint = GatewayService.getIdmConfigs().getPrivilegePath();
            requestType = "POST";
        }
        else if (uriinfo.getPath().contains("movies/thumbnail")) {
            ServiceLogger.LOGGER.info("Building requestModel for movies/thumbnails");
            requestModel = MapRequestModel.mapModel(jsonText, "movies/thumbnail");
            uri = GatewayService.getMoviesConfigs().getScheme() + GatewayService.getMoviesConfigs().getHostName() +  ":" +
                    GatewayService.getMoviesConfigs().getPort() + GatewayService.getMoviesConfigs().getPath();
            endpoint = GatewayService.getMoviesConfigs().getThumbnailPath();
            requestType = "POST";
        }
        else if (uriinfo.getPath().contains("billing/cart/insert")) {
            ServiceLogger.LOGGER.info("Building requestModel for billing/cart/insert");
            requestModel = MapRequestModel.mapModel(jsonText, "billing/cart/insert");
            uri = GatewayService.getBillingConfigs().getScheme() + GatewayService.getBillingConfigs().getHostName() +  ":" +
                    GatewayService.getBillingConfigs().getPort() + GatewayService.getBillingConfigs().getPath();
            endpoint = GatewayService.getBillingConfigs().getCartInsertPath();
            requestType = "POST";
        }
        else if (uriinfo.getPath().contains("billing/cart/update")) {
            ServiceLogger.LOGGER.info("Building requestModel for billing/cart/update");
            requestModel = MapRequestModel.mapModel(jsonText, "billing/cart/update");
            uri = GatewayService.getBillingConfigs().getScheme() + GatewayService.getBillingConfigs().getHostName() + ":" +
                    GatewayService.getBillingConfigs().getPort() + GatewayService.getBillingConfigs().getPath();
            endpoint = GatewayService.getBillingConfigs().getCartUpdatePath();
            requestType = "POST";
        }
        else if (uriinfo.getPath().contains("billing/cart/delete")) {
            ServiceLogger.LOGGER.info("Building requestModel for billing/cart/delete");
            requestModel = MapRequestModel.mapModel(jsonText, "billing/cart/delete");
            uri = GatewayService.getBillingConfigs().getScheme() + GatewayService.getBillingConfigs().getHostName() + ":" +
                    GatewayService.getBillingConfigs().getPort() + GatewayService.getBillingConfigs().getPath();
            endpoint = GatewayService.getBillingConfigs().getCartDeletePath();
            requestType = "POST";
        }
        else if (uriinfo.getPath().contains("billing/cart/retrieve")) {
            ServiceLogger.LOGGER.info("Building requestModel for billing/cart/retrieve");
            requestModel = MapRequestModel.mapModel(jsonText, "billing/cart/retrieve");
            uri = GatewayService.getBillingConfigs().getScheme() + GatewayService.getBillingConfigs().getHostName() + ":" +
                    GatewayService.getBillingConfigs().getPort() + GatewayService.getBillingConfigs().getPath();
            endpoint = GatewayService.getBillingConfigs().getCartRetrievePath();
            requestType = "POST";
        }
        else if (uriinfo.getPath().contains("billing/cart/clear")) {
            ServiceLogger.LOGGER.info("Building requestModel for billing/cart/clear");
            requestModel = MapRequestModel.mapModel(jsonText, "billing/cart/clear");
            uri = GatewayService.getBillingConfigs().getScheme() + GatewayService.getBillingConfigs().getHostName() + ":" +
                    GatewayService.getBillingConfigs().getPort() + GatewayService.getBillingConfigs().getPath();
            endpoint = GatewayService.getBillingConfigs().getCartClearPath();
            requestType = "POST";
        }
        else if (uriinfo.getPath().contains("billing/order/place")) {
            ServiceLogger.LOGGER.info("Building requestModel for billing/order/place");
            requestModel = MapRequestModel.mapModel(jsonText, "billing/order/place");
            uri = GatewayService.getBillingConfigs().getScheme() + GatewayService.getBillingConfigs().getHostName() + ":" +
                    GatewayService.getBillingConfigs().getPort() + GatewayService.getBillingConfigs().getPath();
            endpoint = GatewayService.getBillingConfigs().getOrderPlacePath();
            requestType = "POST";
        }
        else if (uriinfo.getPath().contains("billing/order/retrieve")) {
            ServiceLogger.LOGGER.info("Building requestModel for billing/order/retrieve");
            requestModel = MapRequestModel.mapModel(jsonText, "billing/order/retrieve");
            uri = GatewayService.getBillingConfigs().getScheme() + GatewayService.getBillingConfigs().getHostName() + ":" +
                    GatewayService.getBillingConfigs().getPort() + GatewayService.getBillingConfigs().getPath();
            endpoint = GatewayService.getBillingConfigs().getOrderRetrievePath();
            requestType = "POST";
        }


        clientRequest = new ClientRequest(email, session_id, transaction_id, uri, endpoint, queries, requestModel, requestType);
        threadPool.putRequest(clientRequest);
        Response.ResponseBuilder builder;
        builder = Response.status(Response.Status.NO_CONTENT);
        if (email != null)
            builder.header("email", email);
        if (session_id != null)
            builder.header("session_id", session_id);
        builder.header("transaction_id", transaction_id);
        builder.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());
        return builder.build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response GatewayGetCall(@Context HttpHeaders headers, @PathParam("path") String path
            , @Context UriInfo uriinfo, String jsonText) throws UnsupportedEncodingException {
        System.out.println("Path: " + uriinfo.getPath());
        System.out.println("AsolutePath: " + uriinfo.getAbsolutePath());
        System.out.println("Base URI " + uriinfo.getBaseUri());
        System.out.println("Request URI " + uriinfo.getRequestUri());
        System.out.println("What we want " + uriinfo.getAbsolutePath().relativize(uriinfo.getRequestUri()).toString());

        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = TransactionGenerator.generate();
        // checking if session is valid
        if (uriinfo.getPath().contains("movies") || uriinfo.getPath().contains("billing")) {
            String session_json = SessionChecking.check(email, session_id);
            if (session_json != null) {
                Response.ResponseBuilder builder = Response.status(Response.Status.OK);
                builder.header("email", email);
                builder.header("session_id", session_id);
                return builder.entity(session_json).build();
            }
        }

        String uri = null;
        String endpoint = null;
        String requestType = null;
        URL url = null;
        try {
            url = uriinfo.getRequestUri().toURL();
        } catch (MalformedURLException e){
            ServiceLogger.LOGGER.info("Cannot convert form URI to URL");
        }
        Map<String, String> queries = null;
        if (uriinfo.getRequestUri().toString().contains("?"))
            queries = MapQuerry.splitQuery(url);

        ThreadPool threadPool =  GatewayService.getThreadPool();
        ConnectionPoolManager connectionPoolManager = GatewayService.getConnectionPoolManager();
        ClientRequest clientRequest;
        BaseRequestModel requestModel = null;

        if (uriinfo.getPath().contains("movies/search")) {
            uri = GatewayService.getMoviesConfigs().getScheme() + GatewayService.getMoviesConfigs().getHostName() + ":" +
                    GatewayService.getMoviesConfigs().getPort() + GatewayService.getMoviesConfigs().getPath();
            endpoint = GatewayService.getMoviesConfigs().getSearchPath();
            requestType = "GET";
        }
        else if (uriinfo.getPath().contains("movies/browse/")) {
            uri = GatewayService.getMoviesConfigs().getScheme() + GatewayService.getMoviesConfigs().getHostName() +  ":" +
                    GatewayService.getMoviesConfigs().getPort() + GatewayService.getMoviesConfigs().getPath();
            endpoint = GatewayService.getMoviesConfigs().getBrowsePath() + uriinfo.getPath().toString().substring("movies/browse/".length());
            requestType = "GET";
        }
        else if (uriinfo.getPath().contains("movies/get/")) {
            uri = GatewayService.getMoviesConfigs().getScheme() + GatewayService.getMoviesConfigs().getHostName() +  ":" +
                    GatewayService.getMoviesConfigs().getPort() + GatewayService.getMoviesConfigs().getPath();
            endpoint = GatewayService.getMoviesConfigs().getGetPath() + uriinfo.getPath().toString().substring("movies/get/".length());
            requestType = "GET";
        }
        else if (uriinfo.getPath().contains("movies/people/get/")) {
            uri = GatewayService.getMoviesConfigs().getScheme() + GatewayService.getMoviesConfigs().getHostName() +  ":" +
                    GatewayService.getMoviesConfigs().getPort() + GatewayService.getMoviesConfigs().getPath();
            endpoint = GatewayService.getMoviesConfigs().getPeopleGetPath() + uriinfo.getPath().toString().substring("movies/people/get/".length());
            requestType = "GET";
        }
        else if (uriinfo.getPath().contains("movies/people/search")) {
            uri = GatewayService.getMoviesConfigs().getScheme() + GatewayService.getMoviesConfigs().getHostName() +  ":" +
                    GatewayService.getMoviesConfigs().getPort() + GatewayService.getMoviesConfigs().getPath();
            endpoint = GatewayService.getMoviesConfigs().getPeopleSearchPath();
            requestType = "GET";
        }
        else if (uriinfo.getPath().contains("movies/people")){
            uri = GatewayService.getMoviesConfigs().getScheme() + GatewayService.getMoviesConfigs().getHostName() +  ":" +
                    GatewayService.getMoviesConfigs().getPort() + GatewayService.getMoviesConfigs().getPath();
            endpoint = GatewayService.getMoviesConfigs().getPeoplePath();
            requestType = "GET";
        }

        clientRequest = new ClientRequest(email, session_id, transaction_id, uri, endpoint, queries, requestModel, requestType);
        threadPool.putRequest(clientRequest);
        Response.ResponseBuilder builder;
        builder = Response.status(Response.Status.NO_CONTENT);
        if (email != null)
            builder.header("email", email);
        if (session_id != null)
            builder.header("session_id", session_id);
        builder.header("transaction_id", transaction_id);
        builder.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());
        return builder.build();
    }
}
