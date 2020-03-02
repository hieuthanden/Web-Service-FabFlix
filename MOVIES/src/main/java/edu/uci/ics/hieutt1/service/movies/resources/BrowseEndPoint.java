package edu.uci.ics.hieutt1.service.movies.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.movies.MoviesService;
import edu.uci.ics.hieutt1.service.movies.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.movies.models.BrowseRequestModel;
import edu.uci.ics.hieutt1.service.movies.models.SearchResponseModel;
import edu.uci.ics.hieutt1.service.movies.core.BrowseQueryBuilder;
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

@Path("browse/{phase}") // Outer path
public class BrowseEndPoint {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response BrowseRestCall(@Context HttpHeaders headers, @PathParam("phase") String keys,
                                   @QueryParam("limit") Integer limit,
                                   @QueryParam("offset") Integer offset,
                                   @QueryParam("orderby") String orderby,
                                   @QueryParam("direction") String direction){

        // Declare models
        BrowseRequestModel requestModel = new BrowseRequestModel(limit, offset, orderby, direction);
        SearchResponseModel responseModel;

        if(keys.isEmpty() || keys == null){
            responseModel = new SearchResponseModel(211, "No movies found with search parameters.", null);
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }

        String[] key_list = keys.split(",");
        ArrayList<String> key_array = new ArrayList<>();
        for (String a : key_list){
            key_array.add(a);
        }

        // Get header strings
        // If there is no header with given key, it will be null
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        //check if the privilege is qualified for hidden movies
        ServiceLogger.LOGGER.info("Asking for privilege");
        FindingPrivilege findPri = new FindingPrivilege();
        Boolean is_privilege = findPri.findPrivilege(email);
        ServiceLogger.LOGGER.info("GOT privilege for user: " + email);
        if (!is_privilege)
            requestModel.setHidden(Boolean.FALSE);

        //Build query
        ArrayList<String> request_list = new ArrayList<String>();
        request_list.add("title");
        request_list.add("year");
        request_list.add("rating");
        request_list.add("p.name AS director");
        request_list.add("rating");
        request_list.add("backdrop_path");
        request_list.add("poster_path");
        request_list.add("hidden");

        BrowseQueryBuilder queryBuilder = new BrowseQueryBuilder();
        String query = queryBuilder.BuildBrowseQuerry(request_list, requestModel, key_array);
        System.out.println(query);

        ArrayList<MovieModel> list = new ArrayList<>();
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
            responseModel = new SearchResponseModel(-1, "Internal Server Error", null);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }

        // Return a response with same headers
        Response.ResponseBuilder builder;
        if (list.isEmpty()) {
            responseModel = new SearchResponseModel(211, "No movies found with search parameters.", null);
            builder = Response.status(Response.Status.OK).entity(responseModel);
            ServiceLogger.LOGGER.info("return array with no movie");
        }
        else {
            responseModel = new SearchResponseModel(210, "Found movie(s) with search parameters.", list.toArray(new MovieModel[0]));
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
