package edu.uci.ics.hieutt1.service.movies.core;

import edu.uci.ics.hieutt1.service.movies.models.SearchPersonRequestModel;

import java.sql.PreparedStatement;
import java.util.ArrayList;

public class SearchPersonQueryBuilder {
    public static String BuildSearchPersonQuery(SearchPersonRequestModel requestModel){
        String query = "SELECT p.person_id, p.name, p.birthday, p.popularity, p.profile_path";
        query += " FROM person p, movie m, person_in_movie pim";
        query += " WHERE p.person_id = pim.person_id AND pim.movie_id = m.movie_id";
        if(requestModel.getName() != null) {
            query += " AND p.name LIKE '%" + requestModel.getName() + "%'";
        }
        if(requestModel.getBirthday() != null) {
            query += " AND p.birthday LIKE '%" + requestModel.getBirthday() + "%'";
        }
        if(requestModel.getMovie_title() != null) {
            query += " AND m.title LIKE '%" + requestModel.getMovie_title() + "%'";
        }
        query += " GROUP BY p.person_id";

        if (requestModel.getOrderby() != null) {
            if (requestModel.getOrderby().equals("name")|| requestModel.getOrderby().equals("birthday") || requestModel.getOrderby().equals("popularity")) {
                query += " ORDER BY p." + requestModel.getOrderby();
            }
            else {
                query += " ORDER BY p.name";
            }
        }
        else {
            query += " ORDER BY p.name";
        }

        if(requestModel.getDirection() != null) {
            query += " " + requestModel.getDirection();
        }
        else {
            query += " ASC";
        }
        // secondary sort
        if(requestModel.getOrderby() != null) {
            if(requestModel.getOrderby().equals("name")) {
                query += " , p.popularity DESC";
            }
            else if (requestModel.getOrderby().equals("birthday")) {
                query += " , p.popularity DESC";
            }
            else if (requestModel.getOrderby().equals("popularity")) {
                query += " , p.name ASC";
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
