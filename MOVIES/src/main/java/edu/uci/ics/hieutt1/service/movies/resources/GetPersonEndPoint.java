package edu.uci.ics.hieutt1.service.movies.resources;

import edu.uci.ics.hieutt1.service.movies.MoviesService;
import edu.uci.ics.hieutt1.service.movies.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.movies.models.GetPersonResponseModel;
import edu.uci.ics.hieutt1.service.movies.models.data.FullPersonModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("people/get/{person_id}") // Outer path
public class GetPersonEndPoint {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response SearchPersonRestCall(@Context HttpHeaders headers, @PathParam("person_id") String person_id) {

        // Declare models
        GetPersonResponseModel responseModel;

        // Get header strings
        // If there is no header with given key, it will be null
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        String query = "SELECT p.person_id, p.name, g.gender_name AS gender, p.birthday, p.deathday, p.biography, p.birthplace, p.popularity, p.profile_path";
        query += " FROM person p, gender g WHERE p.gender_id = g.gender_id AND p.person_id = ";
        query += person_id;
        query += " GROUP BY p.person_id;";
        System.out.println(query);

        FullPersonModel fullPersonModel = new FullPersonModel();

        try {
            // Create the prepared statement
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            if (rs.next()) {
                fullPersonModel.setPerson_id(rs.getInt("person_id"));
                fullPersonModel.setName(rs.getString("name"));
                fullPersonModel.setGender(rs.getString("gender"));
                fullPersonModel.setBirthday(rs.getString("birthday"));
                fullPersonModel.setDeathday(rs.getString("deathday"));
                fullPersonModel.setBiography(rs.getString("biography"));
                fullPersonModel.setBirthplace(rs.getString("birthplace"));
                fullPersonModel.setPopularity(rs.getFloat("popularity"));
                fullPersonModel.setProfile_path(rs.getString("profile_path"));

            }
            else {
                ServiceLogger.LOGGER.info("return array with no people");
                responseModel = new GetPersonResponseModel(213, "No people found with search parameters.", null);
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve people from database.");
            e.printStackTrace();
            responseModel = new GetPersonResponseModel(-1, "Internal Server Error", null);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }

        // Return a response with same headers
        Response.ResponseBuilder builder;
        responseModel = new GetPersonResponseModel(212, "Found people with search parameters.", fullPersonModel);
        builder = Response.status(Response.Status.OK).entity(responseModel);
        ServiceLogger.LOGGER.info("return array with people");

        // Pass along headers
        builder.header("email", email);
        builder.header("session_id", session_id);
        builder.header("transaction_id", transaction_id);
        ServiceLogger.LOGGER.info(builder.toString());
        // Return the response
        return builder.build();
    }
}
