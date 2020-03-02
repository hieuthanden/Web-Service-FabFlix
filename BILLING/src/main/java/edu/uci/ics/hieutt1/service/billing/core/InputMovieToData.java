package edu.uci.ics.hieutt1.service.billing.core;

import edu.uci.ics.hieutt1.service.billing.BillingService;
import edu.uci.ics.hieutt1.service.billing.logger.ServiceLogger;
import edu.uci.ics.hieutt1.service.billing.model.data.ItemModel;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class InputMovieToData {
    ItemModel[] itemModels;

    public InputMovieToData(ItemModel[] itemModels) {
        this.itemModels = itemModels;
    }

    public void send_data() {
        Timestamp current_time = new Timestamp(System.currentTimeMillis());
        Date date = new Date(current_time.getTime());
        String query = "INSERT INTO sale(email, movie_id, quantity, sale_date) VALUE";
        ItemModel[] var4 = this.itemModels;
        int rs = var4.length;

        for(int var6 = 0; var6 < rs; ++var6) {
            ItemModel item = var4[var6];
            query = query + " ('" + item.getEmail() + "','" + item.getMovie_id() + "'," + item.getQuantity() + ",'" + date.toString() + "'),";
        }

        query = query.substring(0, query.length() - 1) + ";";

        try {
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            rs = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeeded.");
        } catch (SQLException var8) {
            ServiceLogger.LOGGER.warning("Query failed: dd movies to sale database.");
            var8.printStackTrace();
        }

        ServiceLogger.LOGGER.info("Add movies to sale database successfully");
    }

    public int take_auto_inc() {
        String query = "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_NAME = 'sale';";

        try {
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            if (rs.next()) {
                return rs.getInt("AUTO_INCREMENT");
            }
        } catch (SQLException var4) {
            ServiceLogger.LOGGER.warning("Query failed: dd movies to sale database.");
            var4.printStackTrace();
        }

        return 0;
    }

    public void record_transaction(int sale_id, String token) {
        String query = "INSERT INTO transaction (sale_id, token) VALUE";
        ItemModel[] var4 = this.itemModels;
        int rs = var4.length;

        for(int var6 = 0; var6 < rs; ++var6) {
            ItemModel var10000 = var4[var6];
            query = query + " ('" + sale_id + "','" + token + "'),";
            ++sale_id;
        }

        query = query.substring(0, query.length() - 1) + ";";

        try {
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            rs = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeeded.");
        } catch (SQLException var8) {
            ServiceLogger.LOGGER.warning("Query failed: add sale to transaction.");
            var8.printStackTrace();
        }

        ServiceLogger.LOGGER.info("Add sale to transaction successfully");
    }
}