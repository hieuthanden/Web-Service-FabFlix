package edu.uci.ics.hieutt1.service.movies.core;

import java.util.ArrayList;
import edu.uci.ics.hieutt1.service.movies.models.SearchRequestModel;

public class SearchQueryBuilder {
    public static String BuildSearchQuerry(ArrayList<String> cols, SearchRequestModel requestModel){
        String query = "SELECT m.movie_id";
        for (String c:cols) {
            query += ", " + c;
        }
        query += " FROM movie m, person p, genre_in_movie gim, genre g";
        query += " WHERE m.director_id = p.person_id AND m.movie_id = gim.movie_id AND gim.genre_id = g.genre_id";
        if(requestModel.getTitle() != null) {
            query += " AND title LIKE '%" + requestModel.getTitle() + "%'";
        }
        if(requestModel.getYear() != null) {
            query += " AND year = " + requestModel.getYear();
        }
        if(requestModel.getDirector() != null) {
            query += " AND p.name LIKE '%" + requestModel.getDirector() + "%'";
        }
        if(requestModel.getGenre() != null) {
            query += " AND g.name LIKE '%" + requestModel.getGenre() + "%'";
        }
        if(requestModel.getHidden() == Boolean.FALSE) {
            query += " AND hidden = " + requestModel.getHidden();
        }
        query += " GROUP BY m.movie_id";

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
        if(requestModel.getOffset() != null && requestModel.getOffset()%5 == 0) {

            query += " OFFSET " + requestModel.getOffset();
        }
        else {
            query += " OFFSET 0";
        }
        query += ";";
        return query;
    }
}
