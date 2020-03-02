package edu.uci.ics.hieutt1.service.gateway.core;

import edu.uci.ics.hieutt1.service.gateway.GatewayService;
import edu.uci.ics.hieutt1.service.gateway.logger.ServiceLogger;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteTransaction {
    public static boolean delete(String transaction_id) {
        String query = "DELETE FROM responses WHERE transaction_id LIKE '";
        query += transaction_id + "';";
        Connection con = GatewayService.getConnectionPoolManager().requestCon();
        try {
            // Create the prepared statement

            PreparedStatement ps = con.prepareStatement(query);
            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int rs = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeeded.");
            if (rs != 0) {
                GatewayService.getConnectionPoolManager().releaseCon(con);
                return true;
            }
            else
            {
                GatewayService.getConnectionPoolManager().releaseCon(con);
                return false;
            }
        }catch (SQLException e) {
            GatewayService.getConnectionPoolManager().releaseCon(con);
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve movies from database.");
            e.printStackTrace();
            return false;
        }
    }
}
