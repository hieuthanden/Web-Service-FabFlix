package edu.uci.ics.hieutt1.service.movies.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.movies.MoviesService;
import edu.uci.ics.hieutt1.service.movies.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.movies.models.PersonRequestModel;
import edu.uci.ics.hieutt1.service.movies.models.SearchResponseModel;
import edu.uci.ics.hieutt1.service.movies.core.PersonQueryBuilder;
import edu.uci.ics.hieutt1.service.movies.models.data.MovieModel;
import edu.uci.ics.hieutt1.service.movies.core.FindingPrivilege;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("people") // Outer path
public class PeopleEndPoint {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response PeopleRestCall(@Context HttpHeaders headers,
                                   @QueryParam("name") String name,
                                   @QueryParam("limit") Integer limit,
                                   @QueryParam("offset") Integer offset,
                                   @QueryParam("orderby") String orderby,
                                   @QueryParam("direction") String direction){

        ServiceLogger.LOGGER.warning("Path: /api/movies/people");
        PersonRequestModel requestModel = new PersonRequestModel(name, limit, offset, orderby, direction);
        SearchResponseModel responseModel;
        ArrayList<MovieModel> list = new ArrayList<>();


        // Get header strings
        // If there is no header with given key, it will be null
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        // check if the user is privilege
        ServiceLogger.LOGGER.info("Asking for privilege");
        FindingPrivilege findPri = new FindingPrivilege();
        Boolean is_privilege = findPri.findPrivilege(email);
        ServiceLogger.LOGGER.info("GOT privilege for user: " + email);
        if (!is_privilege)
            requestModel.setHidden(Boolean.FALSE);

        //check if person is in the database
        String query = "SELECT person_id FROM person WHERE name LIKE '%" + name + "%';";
        try {
            // Create the prepared statement
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            if (!rs.next()) {
                responseModel = new SearchResponseModel(213, " No people found with search parameters.", null);
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve person from database.");
            e.printStackTrace();
        }

        PersonQueryBuilder queryBuilder = new PersonQueryBuilder();
        query = queryBuilder.BuildPersonQueryBuilder(name, requestModel);
        try {
            // Create the prepared statement
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            while (rs.next()) {
                MovieModel movieModel = new MovieModel();
                movieModel.setMovie_id(rs.getString("movie_id"));
                movieModel.setTitle(rs.getString("title"));
                movieModel.setYear(rs.getInt("year"));
                movieModel.setDirector(rs.getString("director"));
                movieModel.setRating(rs.getFloat("rating"));
                movieModel.setBackdrop_path(rs.getString("backdrop_path"));
                movieModel.setPoster_path(rs.getString("poster_path"));
                if(is_privilege)
                    movieModel.setHidden(rs.getBoolean("hidden"));
                list.add(movieModel);
            }
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve movies from database.");
            e.printStackTrace();
        }

        Response.ResponseBuilder builder;
        if (list.isEmpty()) {
            responseModel = new SearchResponseModel(211, "No movies found with search paramete", null);
            builder = Response.status(Response.Status.OK).entity(responseModel);
            ServiceLogger.LOGGER.info("return array with no movie");
        }
        else {
            responseModel = new SearchResponseModel(210, "Found movie(s) with search parameters", list.toArray(new MovieModel[0]));
            builder = Response.status(Response.Status.OK).entity(responseModel);
            ServiceLogger.LOGGER.info("return array with movies");

        }
        // Pass along headers
        builder.header("email", email);
        builder.header("session_id", session_id);
        builder.header("transaction_id", transaction_id);
        // Return the response
        return builder.build();
    }
}