package edu.uci.ics.hieutt1.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.billing.BillingService;
import edu.uci.ics.hieutt1.service.billing.core.CheckEmailFormat;
import edu.uci.ics.hieutt1.service.billing.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.billing.model.RetrieveRequestModel;
import edu.uci.ics.hieutt1.service.billing.model.RetrieveResponseModel;
import edu.uci.ics.hieutt1.service.billing.model.data.ItemModel;
import edu.uci.ics.hieutt1.service.billing.core.GetMovieThumbnail;
import edu.uci.ics.hieutt1.service.billing.model.data.ThumbnailModel;

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
import java.util.ArrayList;

@Path("cart/retrieve") // Outer path
public class RetrieveEndPoint {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // Example endpoint to demonstrate salting & hashing
    public Response DeleteItemCart(@Context HttpHeaders headers, String jsonText) {
        RetrieveRequestModel requestModel;
        RetrieveResponseModel responseModel;
        ArrayList<ItemModel> list = new ArrayList<>();
        ArrayList<String> movie_id_list = new ArrayList<>();

        ServiceLogger.LOGGER.warning("Retrieve items in the cart....");

        String email_header = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        ObjectMapper mapper = new ObjectMapper();

        // Validate model & map JSON to POJO
        try {
            requestModel = mapper.readValue(jsonText, RetrieveRequestModel.class);
        } catch (IOException e) {
            // Catch other exceptions here
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new RetrieveResponseModel(resultCode, "JSON Parse Exception", null);
                ServiceLogger.LOGGER.warning("Unable to parse JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new RetrieveResponseModel(resultCode, "JSON Mapping Exception", null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new RetrieveResponseModel(resultCode, "Internal Server Error", null);
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        ServiceLogger.LOGGER.info("Received email");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        // Get email and password
        String email = requestModel.getEmail();

        //check email format
        CheckEmailFormat checkEmailFormat= new CheckEmailFormat(email);
        if (checkEmailFormat.checkLength() == false) {
            responseModel = new RetrieveResponseModel(-10, "Email address has invalid length", null);
            ServiceLogger.LOGGER.warning("Email has invalid lenght!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        } else if (checkEmailFormat.checkFormat() == false) {
            responseModel = new RetrieveResponseModel(-11, "Email address has invalid format", null);
            ServiceLogger.LOGGER.warning("Email has invalid format!");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        // retrieve user data from database
        try {
            String query = "SELECT email, movie_id, quantity, unit_price, discount FROM cart INNER JOIN movie_price USING (movie_id) WHERE email  = '";
            query += email + "';";

            // Create the prepared statement
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            while(rs.next()) {
                ItemModel itemModel = new ItemModel();
                itemModel.setEmail(rs.getString("email"));
                itemModel.setUnit_price(rs.getFloat("unit_price"));
                itemModel.setDiscount(rs.getFloat("discount"));
                itemModel.setQuantity(rs.getInt("quantity"));
                itemModel.setMovie_id(rs.getString("movie_id"));
                list.add(itemModel);
                movie_id_list.add(rs.getString("movie_id"));
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to check item is on the cart.");
            e.printStackTrace();
        }

        if (list.isEmpty())
        {
            responseModel = new RetrieveResponseModel(312, "Shopping cart item does not exist.", null);
            ServiceLogger.LOGGER.warning("Shopping cart item does not exist on cart.");
            Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseModel);
            builder.header("email", email_header);
            builder.header("session_id", session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();
        }
        else {
            //find thumbnail for movie
            ServiceLogger.LOGGER.info("Asking for thumbails");
            GetMovieThumbnail thumbs_request = new GetMovieThumbnail();
            ThumbnailModel[] thumbs = thumbs_request.getThumbnail(movie_id_list.toArray(new String[0]));
            for (int i = 0; i < list.size(); i++){
                list.get(i).setMovie_title(thumbs[i].getTitle());
                list.get(i).setBackdrop_path(thumbs[i].getBackdrop_path());
                list.get(i).setPoster_path(thumbs[i].getPoster_path());
            }
            ServiceLogger.LOGGER.info("GOT all thumbnails for movies.");
            responseModel = new RetrieveResponseModel(3130, "Shopping cart retrieved successfully.", list.toArray(new ItemModel[0]));
            ServiceLogger.LOGGER.warning("Shopping cart retrieved successfully.");
            Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseModel);
            builder.header("email", email_header);
            builder.header("session_id", session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();
        }
    }
}
