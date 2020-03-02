package edu.uci.ics.hieutt1.service.movies.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.movies.MoviesService;
import edu.uci.ics.hieutt1.service.movies.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.movies.models.GetMovieResponseModel;
import edu.uci.ics.hieutt1.service.movies.models.SearchResponseModel;
import edu.uci.ics.hieutt1.service.movies.models.data.FullMovieModel;
import edu.uci.ics.hieutt1.service.movies.models.data.GenreModel;
import edu.uci.ics.hieutt1.service.movies.models.data.MovieModel;
import edu.uci.ics.hieutt1.service.movies.core.FindingPrivilege;
import edu.uci.ics.hieutt1.service.movies.models.data.PersonModel;

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

@Path("get/{movie_id}") // Outer path
public class GetMovieEndPoint {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response BrowseRestCall(@Context HttpHeaders headers, @PathParam("movie_id") String movie_id){

        // Declare models
        GetMovieResponseModel responseModel;

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
        Boolean is_priv = false;
        if (is_privilege)
            is_priv = true;

        //Build query
        String query ="SELECT movie_id, title, year, p.name AS director, rating, num_votes, budget, revenue, overview, backdrop_path, poster_path, hidden ";
        query += " FROM movie m, person p WHERE m.director_id = p.person_id AND movie_id LIKE '";
        query += movie_id + "'";
        if (!is_priv)
            query += " AND m.hidden = FALSE";
        query += ";";
        System.out.println(query);

        Response.ResponseBuilder builder;
        FullMovieModel movieModel = new FullMovieModel();
        try {
            // Create the prepared statement
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            if (rs.next()) {
                movieModel.setMovie_id(rs.getString("movie_id"));
                movieModel.setTitle(rs.getString("title"));
                movieModel.setYear(rs.getInt("year"));
                movieModel.setDirector(rs.getString("director"));
                movieModel.setRating(rs.getFloat("rating"));
                movieModel.setNum_votes(rs.getInt("num_votes"));
                movieModel.setBudget(rs.getString("budget"));
                movieModel.setRevenue(rs.getString("revenue"));
                movieModel.setOverview(rs.getString("overview"));
                movieModel.setBackdrop_path(rs.getString("backdrop_path"));
                movieModel.setPoster_path(rs.getString("poster_path"));
                if (is_privilege)
                    movieModel.setHidden(rs.getBoolean("hidden"));
                movieModel.setGenres(getGenres(rs.getString("movie_id")));
                movieModel.setPeople(getPeople(rs.getString("movie_id")));

                responseModel = new GetMovieResponseModel(210, "Found movie(s) with search parameters", movieModel);
                builder = Response.status(Response.Status.OK).entity(responseModel);
                ServiceLogger.LOGGER.info("return array with movies");
            }
            else {
                responseModel = new GetMovieResponseModel(211, "No movies found with search paramete", null);
                builder = Response.status(Response.Status.OK).entity(responseModel);
                ServiceLogger.LOGGER.info("return array with no movie");
            }
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve movies from database.");
            e.printStackTrace();
            responseModel = new GetMovieResponseModel(-1, "Internal Server Error", null);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }

        // Return a response with same headers
        // Pass along headers
        builder.header("email", email);
        builder.header("session_id", session_id);
        builder.header("transaction_id", transaction_id);
        // Return the response
        return builder.build();
    }

    private GenreModel[] getGenres(String movie_id){
        String query = "SELECT g.genre_id, g.name FROM genre g, genre_in_movie gim WHERE g.genre_id = gim.genre_id AND gim.movie_id = '";
        query += movie_id + "';";
        ArrayList<GenreModel> genres = new ArrayList<>();
        try {
            // Create the prepared statement
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            while(rs.next()){
                GenreModel genreModel = new GenreModel();
                genreModel.setGenre_id(rs.getInt("genre_id"));
                genreModel.setName(rs.getString("name"));
                genres.add(genreModel);
            }
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve genre from database.");
            e.printStackTrace();
        }
        return genres.toArray(new GenreModel[0]);
    }

    private PersonModel[] getPeople(String movie_id){
        String query = "SELECT p.person_id, p.name FROM person p, person_in_movie pim WHERE p.person_id = pim.person_id AND pim.movie_id = '";
        query += movie_id + "';";
        ArrayList<PersonModel> people = new ArrayList<>();
        try {
            // Create the prepared statement
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            while(rs.next()){
                PersonModel personModel = new PersonModel();
                personModel.setPerson_id(rs.getInt("person_id"));
                personModel.setName(rs.getString("name"));
                people.add(personModel);
            }
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve PEOPLE from database.");
            e.printStackTrace();
        }
        return people.toArray(new PersonModel[0]);
    }
}
