package edu.uci.ics.hieutt1.service.movies.resources;

import edu.uci.ics.hieutt1.service.movies.MoviesService;
import edu.uci.ics.hieutt1.service.movies.core.SearchPersonQueryBuilder;
import edu.uci.ics.hieutt1.service.movies.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.movies.models.SearchPersonRequestModel;
import edu.uci.ics.hieutt1.service.movies.models.SearchPersonResponseModel;
import edu.uci.ics.hieutt1.service.movies.models.data.DetailPersonModel;
import edu.uci.ics.hieutt1.service.movies.models.data.MovieModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("people/search") // Outer path
public class SearchPersonEndPoint {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response SearchPersonRestCall(@Context HttpHeaders headers,
                                   @QueryParam("name") String name,
                                   @QueryParam("birthday") String birthday,
                                   @QueryParam("movie_title") String movie_title,
                                   @QueryParam("limit") Integer limit,
                                   @QueryParam("offset") Integer offset,
                                   @QueryParam("orderby") String orderby,
                                   @QueryParam("direction") String direction) {

        // Declare models
        SearchPersonRequestModel requestModel = new SearchPersonRequestModel(name, birthday, movie_title, limit, offset, orderby, direction);
        SearchPersonResponseModel responseModel;

        // Get header strings
        // If there is no header with given key, it will be null
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        SearchPersonQueryBuilder queryBuilder = new SearchPersonQueryBuilder();
        String query = queryBuilder.BuildSearchPersonQuery(requestModel);
        System.out.println(query);

        ArrayList<DetailPersonModel> list = new ArrayList<>();
        try {
            // Create the prepared statement
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            while (rs.next()) {
                DetailPersonModel detailPersonModel = new DetailPersonModel();
                detailPersonModel.setPerson_id(rs.getInt("person_id"));
                detailPersonModel.setName(rs.getString("name"));
                detailPersonModel.setBirthday(rs.getString("birthday"));
                detailPersonModel.setPopularity(rs.getFloat("popularity"));
                detailPersonModel.setProfile_path(rs.getString("profile_path"));
                list.add(detailPersonModel);
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve people from database.");
            e.printStackTrace();
            responseModel = new SearchPersonResponseModel(-1, "Internal Server Error", null);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }

        // Return a response with same headers
        Response.ResponseBuilder builder;
        if (list.isEmpty()) {
            responseModel = new SearchPersonResponseModel(213, "No people found with search parameters.", null);
            builder = Response.status(Response.Status.OK).entity(responseModel);
            ServiceLogger.LOGGER.info("return array with no movie");
        } else {
            responseModel = new SearchPersonResponseModel(212, "Found people with search parameters.", list.toArray(new DetailPersonModel[0]));
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
