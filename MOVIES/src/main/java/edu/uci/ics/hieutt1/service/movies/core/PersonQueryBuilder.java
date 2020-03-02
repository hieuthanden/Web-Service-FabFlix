package edu.uci.ics.hieutt1.service.movies.core;

import edu.uci.ics.hieutt1.service.movies.models.PersonRequestModel;

public class PersonQueryBuilder {

    public static String BuildPersonQueryBuilder(String person_name, PersonRequestModel requestModel) {


        String query = "SELECT m.movie_id, m.title, m.year, p.name AS director, m.rating, m.backdrop_path, m.poster_path, m.hidden FROM movie m, person p WHERE m.movie_id IN (SELECT movie_id FROM person_in_movie pim, person p WHERE pim.person_id = p.person_id AND p.name LIKE '%";

        query += person_name;
        query += "%') AND m.director_id = p.person_id";

        if(requestModel.getHidden() == Boolean.FALSE) {
            query += " AND hidden = " + requestModel.getHidden();
        }

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
