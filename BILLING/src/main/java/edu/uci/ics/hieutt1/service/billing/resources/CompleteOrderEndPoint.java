package edu.uci.ics.hieutt1.service.billing.resources;

import edu.uci.ics.hieutt1.service.billing.BillingService;
import edu.uci.ics.hieutt1.service.billing.core.CapturePayPalOrder;
import edu.uci.ics.hieutt1.service.billing.core.PayPalOrderClient;
import edu.uci.ics.hieutt1.service.billing.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.billing.model.InsertResponseModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("order/complete")
public class CompleteOrderEndPoint {
    @GET
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response DeleteItemCart(@Context HttpHeaders headers, @QueryParam("token") String token, @QueryParam("payer_id") String payer_id) {
        String query = "SELECT token FROM transaction WHERE token LIKE '";
        query = query + token + "';";

        InsertResponseModel responseModel;
        try {
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            if (!rs.next()) {
                responseModel = new InsertResponseModel(3421, "Token not found.");
                ServiceLogger.LOGGER.warning("Token not found.");
                return Response.status(Status.OK).entity(responseModel).build();
            }
        } catch (SQLException var15) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to find token in database.");
            var15.printStackTrace();
        }

        PayPalOrderClient ppOrderclient = new PayPalOrderClient();
        CapturePayPalOrder capturePayPalOrder = new CapturePayPalOrder(ppOrderclient, token);
        String capture_id = capturePayPalOrder.capture();
        if (capture_id == null) {
            responseModel = new InsertResponseModel(3422, "Order can not be completed.");
            ServiceLogger.LOGGER.warning("Order can not be completed.");
            return Response.status(Status.OK).entity(responseModel).build();
        } else {
            query = "UPDATE transaction SET capture_id = '" + capture_id + "' WHERE token LIKE '" + token + "';";

            try {
                PreparedStatement ps = BillingService.getCon().prepareStatement(query);
                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                int rs = ps.executeUpdate();
                ServiceLogger.LOGGER.info("Query succeeded.");
            } catch (SQLException var14) {
                ServiceLogger.LOGGER.warning("Query failed: Unable to find token in database.");
                var14.printStackTrace();
            }

            String email = null;
            query = "SELECT email FROM sale INNER JOIN transaction t on sale.sale_id = t.sale_id WHERE token LIKE '";
            query = query + token + "';";

            PreparedStatement ps;
            try {
                ps = BillingService.getCon().prepareStatement(query);
                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                ResultSet rs = ps.executeQuery();
                ServiceLogger.LOGGER.info("Query succeeded.");
                if (rs.next()) {
                    email = rs.getString("email");
                }
            } catch (SQLException var13) {
                ServiceLogger.LOGGER.warning("Query failed: Unable to find token in database.");
                var13.printStackTrace();
            }

            try {
                query = "DELETE FROM cart WHERE email = ?;";
                ps = BillingService.getCon().prepareStatement(query);
                ps.setString(1, email);
                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                int rs = ps.executeUpdate();
                ServiceLogger.LOGGER.info("Query succeeded.");
            } catch (SQLException var12) {
                ServiceLogger.LOGGER.warning("Query failed: Unable to clear the cart");
                var12.printStackTrace();
            }

            responseModel = new InsertResponseModel(3420, "Order is completed successfully.");
            ServiceLogger.LOGGER.warning("Order is completed successfully.");
            return Response.status(Status.OK).entity(responseModel).build();
        }
    }
}