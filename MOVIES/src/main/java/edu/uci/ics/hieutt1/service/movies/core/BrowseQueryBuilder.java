package edu.uci.ics.hieutt1.service.movies.core;

import edu.uci.ics.hieutt1.service.movies.models.BrowseRequestModel;

import java.util.ArrayList;

public class BrowseQueryBuilder {
    public static String BuildBrowseQuerry(ArrayList<String> cols, BrowseRequestModel requestModel, ArrayList<String> keys){
        String query = "SELECT m.movie_id";
        for (String c:cols) {
            query += ", " + c;
        }
        query += " FROM movie m, keyword k, keyword_in_movie kim, person p";
        query += " WHERE m.director_id = p.person_id AND m.movie_id = kim.movie_id AND k.keyword_id = kim.keyword_id";
        if(requestModel.getHidden() == Boolean.FALSE) {
            query += " AND hidden = " + requestModel.getHidden();
        }
        query += " AND k.name IN (";
        for (String k:keys) {
            query += "'" + k + "'" + ",";
        }
        query = query.substring(0, query.length() - 1);
        query += ")";
        query += " GROUP BY m.movie_id HAVING COUNT(*) = " + keys.size();

        if (requestModel.getOrderby() != null) {
            if (requestModel.getOrderby().equals("title")|| requestModel.getOrderby().equals("year") || requestModel.getOrderby().equals("rating")) {
                query += " ORDER BY " + requestModel.getOrderby();
            }
            else {
                query += " ORDER BY title";
            }
        }
        else {
            query += " ORDER BY title";
        }

        if(requestModel.getDirection() != null) {
            query += " " + requestModel.getDirection();
        }
        else {
            query += " ASC";
        }
        // secondary sort
        if(requestModel.getOrderby() != null) {
            if(requestModel.getOrderby().equals("title")) {
                query += " , rating DESC";
            }
            else if (requestModel.getOrderby().equals("rating")) {
                query += " , title ASC";
            }
            else if (requestModel.getOrderby().equals("year")) {
                query += " , rating DESC";
            }
        }

        if (requestModel.getLimit() != null) {
            if (requestModel.getLimit() == 10 || requestModel.getLimit() == 25 || requestModel.getLimit() == 50 || requestModel.getLimit() == 100) {
                query += " LIMIT " + requestModel.getLimit();
            }
            else {
                query += " LIMIT 10";
            }
        }
        else {
            query += " LIMIT 10";
        }
        if(requestModel.getOffset() != null && requestModel.getOffset()%requestModel.getLimit() == 0) {
            query += " OFFSET " + requestModel.getOffset();
        }
        else {
            query += " OFFSET 0";
        }
        query += ";";
        return query;
    }
}
