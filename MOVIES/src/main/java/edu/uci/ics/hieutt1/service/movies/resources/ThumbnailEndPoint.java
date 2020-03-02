package edu.uci.ics.hieutt1.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.movies.MoviesService;
import edu.uci.ics.hieutt1.service.movies.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.movies.models.ThumbnailRequestModel;
import edu.uci.ics.hieutt1.service.movies.models.ThumbnailResponseModel;

import edu.uci.ics.hieutt1.service.movies.core.SearchQueryBuilder;
import edu.uci.ics.hieutt1.service.movies.models.data.MovieModel;
import edu.uci.ics.hieutt1.service.movies.core.FindingPrivilege;
import edu.uci.ics.hieutt1.service.movies.models.data.ThumbnailModel;
import org.apache.commons.codec.DecoderException;
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
import java.util.ArrayList;

@Path("/thumbnail") // Outer path
public class ThumbnailEndPoint {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // Example endpoint to demonstrate salting & hashing
    public Response login(@Context HttpHeaders headers, String jsonText) {
        ThumbnailRequestModel requestModel;
        ThumbnailResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        // Get header strings
        // If there is no header with given key, it will be null
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        // Validate model & map JSON to POJO
        try {
            requestModel = mapper.readValue(jsonText, ThumbnailRequestModel.class);
            if (requestModel.getMoive_ids().length == 0) {
                ServiceLogger.LOGGER.info("Movie_id list for request is empty");
                responseModel = new ThumbnailResponseModel(211, "No movies found with search paramete", null);
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
        } catch (IOException e) {
            // Catch other exceptions here
            e.printStackTrace();
            responseModel = new ThumbnailResponseModel(-1, "Internal Server Error", null);
            ServiceLogger.LOGGER.severe("False to map jsontext with thumbnail request model");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }

        ServiceLogger.LOGGER.info("Received list of movie_id");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);


        String[] moive_ids = requestModel.getMoive_ids();
        ServiceLogger.LOGGER.info("GOT all movie ids: " + moive_ids);
        String keys = "";
        for (String m:moive_ids){
            keys += "'" + m +"',";}
        keys = keys.substring(0, keys.length() -1);

        String query = "SELECT movie_id, title, backdrop_path, poster_path FROM movie WHERE movie_id IN (";
        query += keys + ")";
        query += " ORDER BY FIELD(movie_id," + keys +");";
        System.out.println(query);

        ArrayList<ThumbnailModel> list = new ArrayList<>();
        try {
            // Create the prepared statement
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            while (rs.next()) {
                ThumbnailModel thumbnailModel= new ThumbnailModel();
                thumbnailModel.setMovie_id(rs.getString("movie_id"));
                thumbnailModel.setTitle(rs.getString("title"));
                thumbnailModel.setBackdrop_path(rs.getString("backdrop_path"));
                thumbnailModel.setPoster_path(rs.getString("poster_path"));
                list.add(thumbnailModel);
            }
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve movies from database.");
            e.printStackTrace();
            responseModel = new ThumbnailResponseModel(-1, "Internal Server Error", null);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }

        Response.ResponseBuilder builder;
        if (list.isEmpty() || list == null) {
            responseModel = new ThumbnailResponseModel(211, "No movies found with search paramete");
            builder = Response.status(Response.Status.OK).entity(responseModel);
            ServiceLogger.LOGGER.info("return array with no movie");
        }
        else {
            responseModel = new ThumbnailResponseModel(210, "Found movie(s) with search parameters", list.toArray(new ThumbnailModel[0]));
            builder = Response.status(Response.Status.OK).entity(responseModel);
            ServiceLogger.LOGGER.info("return array with movies");

        }

        return Response.status(Response.Status.OK).entity(responseModel).build();
    }
}
