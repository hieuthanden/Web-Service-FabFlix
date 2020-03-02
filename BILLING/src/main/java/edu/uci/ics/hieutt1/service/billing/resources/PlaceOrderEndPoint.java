package edu.uci.ics.hieutt1.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.billing.BillingService;
import edu.uci.ics.hieutt1.service.billing.core.CheckEmailFormat;
import edu.uci.ics.hieutt1.service.billing.core.CreatePayPalOrder;
import edu.uci.ics.hieutt1.service.billing.core.InputMovieToData;
import edu.uci.ics.hieutt1.service.billing.core.PayPalOrderClient;
import edu.uci.ics.hieutt1.service.billing.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.billing.model.PlaceOrderResponseModel;
import edu.uci.ics.hieutt1.service.billing.model.RetrieveRequestModel;
import edu.uci.ics.hieutt1.service.billing.model.data.ItemModel;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("order/place")
public class PlaceOrderEndPoint {

    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response PlaceOrder(@Context HttpHeaders headers, String jsonText) {
        ArrayList<ItemModel> list = new ArrayList();
        DecimalFormat df = new DecimalFormat("0.00");
        ServiceLogger.LOGGER.warning("Placing order....");
        ObjectMapper mapper = new ObjectMapper();

        String email_header = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        RetrieveRequestModel requestModel;
        PlaceOrderResponseModel responseModel;
        try {
            requestModel = (RetrieveRequestModel)mapper.readValue(jsonText, RetrieveRequestModel.class);
        } catch (IOException var18) {
            var18.printStackTrace();
            byte resultCode;
            if (var18 instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new PlaceOrderResponseModel(resultCode, "JSON Parse Exception", (String)null, (String)null);
                ServiceLogger.LOGGER.warning("Unable to parse JSON to POJO");
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }

            if (var18 instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new PlaceOrderResponseModel(resultCode, "JSON Mapping Exception", (String)null, (String)null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }

            resultCode = -1;
            responseModel = new PlaceOrderResponseModel(resultCode, "Internal Server Error", (String)null, (String)null);
            ServiceLogger.LOGGER.severe("Internal Server Error");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }

        ServiceLogger.LOGGER.info("Received email for place order");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);
        String email = requestModel.getEmail();
        CheckEmailFormat checkEmailFormat = new CheckEmailFormat(email);
        if (checkEmailFormat.checkLength() && checkEmailFormat.checkFormat()) {
            Float total_price = 0.0F;

            try {
                String query = "SELECT email, movie_id, quantity, unit_price, discount FROM cart INNER JOIN movie_price USING (movie_id) WHERE email  = '";
                query = query + email + "';";
                PreparedStatement ps = BillingService.getCon().prepareStatement(query);
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
                    total_price = (rs.getFloat("unit_price") - (1.0F - rs.getFloat("discount"))) * (float)rs.getInt("quantity");
                    list.add(itemModel);
                }
            } catch (SQLException var19) {
                ServiceLogger.LOGGER.warning("Query failed: Unable to check item is on the cart.");
                var19.printStackTrace();
            }

            if (list.isEmpty()) {
                responseModel = new PlaceOrderResponseModel(312, "Shopping cart item does not exist.", (String)null, (String)null);
                ServiceLogger.LOGGER.warning("Shopping cart item does not exist on cart.");
                Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseModel);
                builder.header("email", email_header);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();
            } else {
                ServiceLogger.LOGGER.warning("total_price = " + df.format(total_price));
                PayPalOrderClient ppOrderclient = new PayPalOrderClient();
                CreatePayPalOrder createPayPalOrder = new CreatePayPalOrder(ppOrderclient, df.format(total_price));
                String[] payPalreturn = CreatePayPalOrder.create();
                String token = payPalreturn[0];
                String approve_url = payPalreturn[1];
                InputMovieToData inputMovieToData = new InputMovieToData((ItemModel[])list.toArray(new ItemModel[0]));
                int sale_id = inputMovieToData.take_auto_inc();
                inputMovieToData.send_data();
                inputMovieToData.record_transaction(sale_id, token);
                responseModel = new PlaceOrderResponseModel(3400, "Order placed successfully.", approve_url, token);
                ServiceLogger.LOGGER.warning("Order placed successfully.");
                Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseModel);
                builder.header("email", email_header);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();
            }
        } else {
            responseModel = new PlaceOrderResponseModel(342, "Order creation failed.", (String)null, (String)null);
            ServiceLogger.LOGGER.warning("Email has invalid lenght or format!");
            Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseModel);
            builder.header("email", email_header);
            builder.header("session_id", session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();
        }
    }
}