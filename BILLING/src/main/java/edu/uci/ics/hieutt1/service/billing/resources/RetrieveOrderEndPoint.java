package edu.uci.ics.hieutt1.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hieutt1.service.billing.BillingService;
import edu.uci.ics.hieutt1.service.billing.core.PayPalOrderClient;
import edu.uci.ics.hieutt1.service.billing.core.RetrievePayPalOrder;
import edu.uci.ics.hieutt1.service.billing.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.billing.model.RetrieveOrderResponseModel;
import edu.uci.ics.hieutt1.service.billing.model.RetrieveRequestModel;
import edu.uci.ics.hieutt1.service.billing.model.data.TransactionModel;
import edu.uci.ics.hieutt1.service.billing.model.data.TransactionModel.ItemModel;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("order/retrieve")
public class RetrieveOrderEndPoint {
    public RetrieveOrderEndPoint() {
    }

    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response RetrieveOrder(@Context HttpHeaders headers, String jsonText) {
        ArrayList<TransactionModel> list = new ArrayList();
        ServiceLogger.LOGGER.warning("Placing order....");
        ObjectMapper mapper = new ObjectMapper();

        RetrieveRequestModel requestModel;
        RetrieveOrderResponseModel responseModel;
        try {
            requestModel = (RetrieveRequestModel)mapper.readValue(jsonText, RetrieveRequestModel.class);
        } catch (IOException var16) {
            var16.printStackTrace();
            byte resultCode;
            if (var16 instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new RetrieveOrderResponseModel(resultCode, "JSON Parse Exception", (TransactionModel[])null);
                ServiceLogger.LOGGER.warning("Unable to parse JSON to POJO");
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }

            if (var16 instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new RetrieveOrderResponseModel(resultCode, "JSON Mapping Exception", (TransactionModel[])null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }

            resultCode = -1;
            responseModel = new RetrieveOrderResponseModel(resultCode, "Internal Server Error", (TransactionModel[])null);
            ServiceLogger.LOGGER.severe("Internal Server Error");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }

        ServiceLogger.LOGGER.info("Received email for place order");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);
        String email = requestModel.getEmail();

        try {
            String query = "SELECT token, sale_id, email, movie_id, quantity, unit_price, discount, sale_date FROM sale INNER JOIN movie_price USING (movie_id) INNER JOIN transaction using (sale_id) WHERE email LIKE '";
            query = query + email + "' AND transaction.capture_id IS NOT NULL ORDER BY token;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            String curent_token = null;
            TransactionModel transactionModel = null;
            ArrayList<TransactionModel.ItemModel> item_list = new ArrayList();

            while(rs.next()) {
                System.out.println("token: " + rs.getString("token"));
                if (!rs.getString("token").equals(curent_token)) {
                    curent_token = rs.getString("token");

                    if (transactionModel != null) {
                        transactionModel.setItems(item_list.toArray(new TransactionModel.ItemModel[0]));
                        list.add(transactionModel);
                    }
                    item_list = new ArrayList(); //reset item list
                    PayPalOrderClient ppOrderclient = new PayPalOrderClient();
                    RetrievePayPalOrder retrievePayPalOrder = new RetrievePayPalOrder(ppOrderclient, curent_token);
                    transactionModel = retrievePayPalOrder.retrieve();
                }

                TransactionModel.ItemModel itemModel = new ItemModel();
                itemModel.setEmail(rs.getString("email"));
                itemModel.setMovie_id(rs.getString("movie_id"));
                itemModel.setQuantity(rs.getInt("quantity"));
                itemModel.setUnit_price(rs.getFloat("unit_price"));
                itemModel.setDiscount(rs.getFloat("discount"));
                itemModel.setSale_date(rs.getString("sale_date"));
                item_list.add(itemModel);
                System.out.println("list size: " + list.size());
            }
            transactionModel.setItems(item_list.toArray(new TransactionModel.ItemModel[0]));
            list.add(transactionModel);
            System.out.println("Final list size: " + list.size());


        } catch (SQLException var17) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to check item is on the cart.");
            var17.printStackTrace();
        }

        responseModel = new RetrieveOrderResponseModel(3410, "Orders retrieved successfully", list.toArray(new TransactionModel[0]));
        ServiceLogger.LOGGER.warning("Orders retrieved successfully.");
        return Response.status(Status.OK).entity(responseModel).build();
    }
}