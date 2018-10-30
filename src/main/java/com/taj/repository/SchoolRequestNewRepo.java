package com.taj.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.model.Pagination.SchoolRequestNewDto2ModelPagination;
import com.taj.model.Pagination.SchoolRequestNewDtoModelPagination;
import com.taj.model.*;
import com.taj.model.new_school_request.SchoolRequestNewDto2Model;
import com.taj.model.new_school_request.SchoolRequestNewDtoModel;
import com.taj.model.new_school_request.getSchoolCustomNewRequestByIdModel;
import com.taj.model.school_request_image_web.SchoolRequestWithImageByIdDto;
import com.taj.model.school_request_image_web.SchoolRequestWithImageByIdDto2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

/**
 * Created by User on 8/15/2018.
 */
@Repository
public class SchoolRequestNewRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    ObjectMapper mapper;

    public SchoolRequestNewDtoModelPagination getRequestsAllPagination(int page, int pageSize, int flag_ar) {

        int pages = (int) Math.ceil(((float) getRequestsAllPaginationCount()) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;

        if (flag_ar ==0 ){

            String sql = "SELECT " +
                    "request_id, request_title, request_explaination, request_display_date, " +
                    "    request_expired_date," +
                    " IFNULL(request_count,0) AS request_count" +
                    ", school_id," +
                    "    request_category_name, " +
                    "    count(distinct responsed_request_id) AS response_count" +
                    " FROM efaz_school_tender AS tender INNER JOIN" +
                    "                        efaz_school_request_category AS cat" +
                    "                         ON" +
                    "                         tender.requests_category_id = cat.request_category_id" +
                    "                         LEFT JOIN efaz_company_response_school_request AS req" +
                    "                         ON tender.request_id = req.responsed_request_id" +
                    "                         GROUP BY request_id " +
                    " LIMIT ?,?;";

            List<SchoolRequestNewDtoModel> requests = jdbcTemplate.query(sql, new Object[]{limitOffset, pageSize},
                    (resultSet, i) -> new SchoolRequestNewDtoModel(resultSet.getInt(1),
                            resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime()
                            , resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getInt(9)));

            return new SchoolRequestNewDtoModelPagination(getRequestsAllPaginationCount(), pages, requests);

        }else {


            String sql = "SELECT " +
                    "request_id, request_title, request_explaination, request_display_date, " +
                    "    request_expired_date," +
                    " IFNULL(request_count,0) AS request_count" +
                    ", school_id," +
                    "    request_category_name_ar AS request_category_name, " +
                    "    count(distinct responsed_request_id) AS response_count" +
                    " FROM efaz_school_tender AS tender INNER JOIN" +
                    "                        efaz_school_request_category AS cat" +
                    "                         ON" +
                    "                         tender.requests_category_id = cat.request_category_id" +
                    "                         LEFT JOIN efaz_company_response_school_request AS req" +
                    "                         ON tender.request_id = req.responsed_request_id" +
                    "                         GROUP BY request_id " +
                    " LIMIT ?,?;";

            List<SchoolRequestNewDtoModel> requests = jdbcTemplate.query(sql, new Object[]{limitOffset, pageSize},
                    (resultSet, i) -> new SchoolRequestNewDtoModel(resultSet.getInt(1),
                            resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime()
                            , resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getInt(9)));

            return new SchoolRequestNewDtoModelPagination(getRequestsAllPaginationCount(), pages, requests);

        }


    }

    public int addRequest(String request_title, String request_explaination,
                          long request_display_date, long request_expired_date, int school_id,
                          String request_category_id, String image_one, int request_count,  int flag_ar) {

        KeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                final PreparedStatement ps = connection.prepareStatement("INSERT INTO school_requst_images VALUES (?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, null);
                try {
                    ps.setString(2, image_one);
                } catch (NullPointerException e) {
                    ps.setBytes(2, null);
                }
                ps.setBytes(3, null);/////////
                ps.setBytes(4, null);
                ps.setBytes(5, null);
                return ps;
            }

        }, key);

        int image_id = key.getKey().intValue();

        if (flag_ar == 0){

            int category = jdbcTemplate.queryForObject("SELECT request_category_id  FROM efaz_school_request_category WHERE  request_category_name LIKE ?;",
                    Integer.class, "%" + request_category_id + "%");

            return jdbcTemplate.update("INSERT INTO efaz_school_tender VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", null, null, image_id, request_title,
                    request_explaination, new Timestamp(request_display_date), new Timestamp(request_expired_date), null, null, 0,
                    null, school_id, category, null, null, request_count);

        }else {


            int category = jdbcTemplate.queryForObject("SELECT request_category_id  FROM efaz_school_request_category WHERE  request_category_name_ar LIKE ?;",
                    Integer.class, "%" + request_category_id + "%");

            return jdbcTemplate.update("INSERT INTO efaz_school_tender VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", null, null, image_id, request_title,
                    request_explaination, new Timestamp(request_display_date), new Timestamp(request_expired_date), null, null, 0,
                    null, school_id, category, null, null, request_count);

        }


    }

    public int updateSchoolRequest(int request_id, String request_title, String request_explaination,
                                   long request_display_date, long request_expired_date, int school_id,
                                   String request_category_id, String image_one, int request_count, int request_is_conformied, int flag_ar) {

        int imageId = jdbcTemplate.queryForObject("SELECT images_id FROM efaz_school_tender Where request_id=?", Integer.class, request_id);
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=0;");
        jdbcTemplate.update("DELETE FROM school_requst_images WHERE image_id=? ;", imageId);

        KeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                final PreparedStatement ps = connection.prepareStatement("INSERT INTO school_requst_images VALUES (?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, null);
                try {
                    ps.setString(2, image_one);
                } catch (NullPointerException e) {
                    ps.setBytes(2, null);
                }
                ps.setBytes(3, null);/////////
                ps.setBytes(4, null);
                ps.setBytes(5, null);
                return ps;
            }

        }, key);

        int image_id = key.getKey().intValue();

        if (flag_ar == 0){

            int category = jdbcTemplate.queryForObject("SELECT request_category_id  FROM efaz_school_request_category WHERE  request_category_name LIKE ?;",
                    Integer.class, "%" + request_category_id + "%");
            jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=0;");
            int res = jdbcTemplate.update("update efaz_school_tender set request_details_file=?," + " images_id=?, request_title=?," +
                            "request_explaination=?," + " request_display_date=?, request_expired_date=?, request_deliver_date=?," +
                            "request_payment_date=?, request_is_available=?, request_is_conformied=?, school_id=?, requests_category_id=?," +
                            " receive_palce_id=?, extended_payment=?, request_count=? " +
                            " where request_id=?;", null, image_id, request_title, request_explaination, new Timestamp(request_display_date)
                    , new Timestamp(request_expired_date), null, null, null, request_is_conformied, school_id, category,
                    null, null, request_count, request_id);
            jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=1;");
            return res;

        }else {

            int category = jdbcTemplate.queryForObject("SELECT request_category_id  FROM efaz_school_request_category WHERE  request_category_name_ar LIKE ?;",
                    Integer.class, "%" + request_category_id + "%");
            jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=0;");
            int res = jdbcTemplate.update("update efaz_school_tender set request_details_file=?," + " images_id=?, request_title=?," +
                            "request_explaination=?," + " request_display_date=?, request_expired_date=?, request_deliver_date=?," +
                            "request_payment_date=?, request_is_available=?, request_is_conformied=?, school_id=?, requests_category_id=?," +
                            " receive_palce_id=?, extended_payment=?, request_count=? " +
                            " where request_id=?;", null, image_id, request_title, request_explaination, new Timestamp(request_display_date)
                    , new Timestamp(request_expired_date), null, null, null, request_is_conformied, school_id, category,
                    null, null, request_count, request_id);
            jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=1;");
            return res;

        }


    }

    public ResponseEntity<ObjectNode> getSchoolRequest(int request_id, int flag_ar) {
        ObjectNode node = mapper.createObjectNode();
        String sql = "";
        if (flag_ar == 0){
            sql = "SELECT\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date,\n" +
                    "\trequest_is_conformied,\n" +
                    "\tschool_id,\n" +
                    "\trequest_category_name,\n" +
                    "\timage_one,\n" +
                    "\trequest_count \n" +
                    "FROM\n" +
                    "\tefaz_school_tender AS st\n" +
                    "\tLEFT JOIN school_requst_images AS img ON st.images_id = img.image_id\n" +
                    "\tLEFT JOIN efaz_school_request_category AS cat ON cat.request_category_id = st.requests_category_id\n" +
                    "WHERE\n" +
                    "\trequest_id = ?;";
        }else {
            sql = "SELECT\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date,\n" +
                    "\trequest_is_conformied,\n" +
                    "\tschool_id,\n" +
                    "\trequest_category_name_ar AS request_category_name,\n" +
                    "\timage_one,\n" +
                    "\trequest_count \n" +
                    "FROM\n" +
                    "\tefaz_school_tender AS st\n" +
                    "\tLEFT JOIN school_requst_images AS img ON st.images_id = img.image_id\n" +
                    "\tLEFT JOIN efaz_school_request_category AS cat ON cat.request_category_id = st.requests_category_id\n" +
                    "WHERE\n" +
                    "\trequest_id = ?;";
        }


        if (isExist(request_id)) {
            SchoolRequestModelUpdate model = jdbcTemplate.queryForObject(sql, new Object[]{request_id},
                    (resultSet, i) -> new SchoolRequestModelUpdate(resultSet.getInt(1),
                            resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime(),
                            resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getString(9), resultSet.getInt(10)));


            node.put("request_id", model.getRequest_id());
            node.put("request_title", model.getRequest_title());
            node.put("request_explaination", model.getRequest_explaination());
            node.put("request_display_date", model.getRequest_display_date());
            node.put("request_expired_date", model.getRequest_expired_date());
            node.put("request_is_conformied", model.getRequest_is_conformied());
            node.put("school_id", model.getSchool_id());
            node.put("request_category_name", model.getRequest_category_name());
            node.put("image_one", model.getImage_one());
            node.put("request_count", model.getRequest_count());
            node.put("flag_ar", flag_ar);
            return ResponseEntity.status(HttpStatus.OK).body(node);
        } else {
            node.put("status", 400);
            node.put("message", "tender with this id not founr");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(node);
        }


    }

    public List<SchoolRequestsDTO> getRequests() {

        String sql = "SELECT " +
                "request_id, request_title, request_explaination, request_display_date, " +
                "    request_expired_date, school_id," +
                "    request_category_name, request_count" +
                " FROM efaz_school_tender AS tender INNER JOIN" +
                "                        efaz_school_request_category AS cat" +
                "                         ON" +
                "                         tender.requests_category_id = cat.request_category_id;";

        return jdbcTemplate.query(sql,
                (resultSet, i) -> new SchoolRequestsDTO(resultSet.getInt(1),
                        resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime()
                        , resultSet.getInt(6), resultSet.getString(7), resultSet.getInt(8)));
    }

    public int getRequestsAllPaginationCount() {
        String sql = "SELECT\n" +
                "\tCOUNT( DISTINCT request_id ) count \n" +
                "FROM\n" +
                "\tefaz_school_tender AS tender\n" +
                "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id;";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<SchoolRequestNewDtoModel> getRequestsAll(int flag_ar) {

        if (flag_ar == 0){


            String sql = "SELECT " +
                    "request_id, request_title, request_explaination, request_display_date, " +
                    "    request_expired_date," +
                    " IFNULL(request_count,0) AS request_count" +
                    ", school_id," +
                    "    request_category_name, " +
                    "    count(distinct responsed_request_id) AS response_count" +
                    " FROM efaz_school_tender AS tender INNER JOIN" +
                    "                        efaz_school_request_category AS cat" +
                    "                         ON" +
                    "                         tender.requests_category_id = cat.request_category_id" +
                    "                         LEFT JOIN efaz_company_response_school_request AS req" +
                    "                         ON tender.request_id = req.responsed_request_id" +
                    "                         GROUP BY request_id;";

            return jdbcTemplate.query(sql,
                    (resultSet, i) -> new SchoolRequestNewDtoModel(resultSet.getInt(1),
                            resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime()
                            , resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getInt(9)));


        }else {


            String sql = "SELECT " +
                    "request_id, request_title, request_explaination, request_display_date, " +
                    "    request_expired_date," +
                    " IFNULL(request_count,0) AS request_count" +
                    ", school_id," +
                    "    request_category_name_ar AS request_category_name, " +
                    "    count(distinct responsed_request_id) AS response_count" +
                    " FROM efaz_school_tender AS tender INNER JOIN" +
                    "                        efaz_school_request_category AS cat" +
                    "                         ON" +
                    "                         tender.requests_category_id = cat.request_category_id" +
                    "                         LEFT JOIN efaz_company_response_school_request AS req" +
                    "                         ON tender.request_id = req.responsed_request_id" +
                    "                         GROUP BY request_id;";

            return jdbcTemplate.query(sql,
                    (resultSet, i) -> new SchoolRequestNewDtoModel(resultSet.getInt(1),
                            resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime()
                            , resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getInt(9)));


        }

    }


    public SchoolRequestNewDtoById getRequestByID(int id) {


        String sql = "SELECT \n" +
                "                tender.request_id, request_title, request_explaination, request_display_date, \n" +
                "                    request_expired_date, tender.school_id, school_name, school_logo_image, \n" +
                "                    request_category_name, \n" +
                "                    count( responsed_request_id) AS response_count, count(seen_id)AS views_count\n" +
                "                 FROM efaz_school_tender AS tender INNER JOIN\n" +
                "                                        efaz_school_request_category AS cat\n" +
                "                                         ON\n" +
                "                                         tender.requests_category_id = cat.request_category_id\n" +
                "                                        LEFT JOIN efaz_company_response_school_request AS req\n" +
                "                                         ON tender.request_id = req.responsed_request_id\n" +
                "                                         LEFT JOIN efaz_school_profile AS sp ON tender.school_id = sp.school_id\n" +
                "                                         Left Join efaz_company_see_request AS reqst ON  tender.request_id = reqst.request_id \n" +
                "                                       WHERE  tender.request_id=? \n" +
                "                 GROUP BY tender.request_id, request_title, request_explaination, request_display_date, \n" +
                "                    request_expired_date, tender.school_id, school_name, school_logo_image, \n" +
                "                    request_category_name;";


//        String sql = "SELECT " +
//                "request_id, request_title, request_explaination, request_display_date, " +
//                "    request_expired_date, school_id," +
//                "    request_category_name" +
//                " FROM efaz_school_tender AS tender INNER JOIN" +
//                "                        efaz_school_request_category AS cat" +
//                "                         ON" +
//                "                         tender.request_category_id = cat.request_category_id"+
//                " WHERE  request_id=?;";
        return jdbcTemplate.queryForObject(sql,
                new Object[]{id}, (resultSet, i) -> new SchoolRequestNewDtoById(resultSet.getInt(1),
                        resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime()
                        , resultSet.getInt(6), resultSet.getString(7), resultSet.getBytes(8), resultSet.getString(9),
                        resultSet.getInt(10), resultSet.getInt(11)));
    }


    public SchoolRequestWithImageByIdDto getRequestByIDWithImage(int id) {


        String sql = "SELECT\n" +
                "\ttender.request_id,\n" +
                "\trequest_title,\n" +
                "\trequest_explaination,\n" +
                "\trequest_display_date,\n" +
                "\trequest_expired_date,\n" +
                "\ttender.school_id,\n" +
                "\tschool_name,\n" +
                "\tschool_logo_image,\n" +
                "\trequest_category_name,\n" +
                "\tcount( responsed_request_id ) AS response_count,\n" +
                "\tcount( seen_id ) AS views_count,\n" +
                "\timage_one \n" +
                "FROM\n" +
                "\tefaz_school_tender AS tender\n" +
                "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                "\tLEFT JOIN efaz_school_profile AS sp ON tender.school_id = sp.school_id\n" +
                "\tLEFT JOIN efaz_company_see_request AS reqst ON tender.request_id = reqst.request_id\n" +
                "\tLEFT JOIN school_requst_images AS img ON tender.images_id = img.image_id \n" +
                "WHERE\n" +
                "\ttender.request_id = ? \n" +
                "GROUP BY\n" +
                "\ttender.request_id,\n" +
                "\trequest_title,\n" +
                "\trequest_explaination,\n" +
                "\trequest_display_date,\n" +
                "\trequest_expired_date,\n" +
                "\ttender.school_id,\n" +
                "\tschool_name,\n" +
                "\tschool_logo_image,\n" +
                "\trequest_category_name,\n" +
                "\timage_one;";


        return jdbcTemplate.queryForObject(sql,
                new Object[]{id}, (resultSet, i) -> new SchoolRequestWithImageByIdDto(resultSet.getInt(1),
                        resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime()
                        , resultSet.getInt(6), resultSet.getString(7), resultSet.getString(8), resultSet.getString(9),
                        resultSet.getInt(10), resultSet.getInt(11), resultSet.getString(12)));
    }


    public SchoolRequestWithImageByIdDto2 getRequestByIDWithImageAndCompanyRequest(int id, int companyId) {


        String sql = "SELECT\n" +
                "\ttender.request_id,\n" +
                "\trequest_title,\n" +
                "\trequest_explaination,\n" +
                "\trequest_display_date,\n" +
                "\trequest_expired_date,\n" +
                "\ttender.school_id,\n" +
                "\tschool_name,\n" +
                "\tschool_logo_image,\n" +
                "\trequest_category_name,\n" +
                "\tcount( req.responsed_request_id ) AS response_count,\n" +
                "\tcount( seen_id ) AS views_count,\n" +
                "\timage_one,\n" +
                "\tIFNULL( cs.responsed_cost, 0 ) AS responsed_cost,\n" +
                "\tIFNULL( cs.response_desc, '' ) AS response_desc \n" +
                "FROM\n" +
                "\tefaz_school_tender AS tender\n" +
                "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                "\tLEFT JOIN efaz_school_profile AS sp ON tender.school_id = sp.school_id\n" +
                "\tLEFT JOIN efaz_company_see_request AS reqst ON tender.request_id = reqst.request_id\n" +
                "\tLEFT JOIN school_requst_images AS img ON tender.images_id = img.image_id\n" +
                "\tLEFT JOIN efaz_company_response_school_request AS cs ON cs.responsed_request_id = ? \n" +
                "\tAND cs.responsed_company_id = ? \n" +
                "WHERE\n" +
                "\ttender.request_id = ? \n" +
                "GROUP BY\n" +
                "\ttender.request_id,\n" +
                "\trequest_title,\n" +
                "\trequest_explaination,\n" +
                "\trequest_display_date,\n" +
                "\trequest_expired_date,\n" +
                "\ttender.school_id,\n" +
                "\tschool_name,\n" +
                "\tschool_logo_image,\n" +
                "\trequest_category_name,\n" +
                "\timage_one,\n" +
                "\tcs.responsed_cost,\n" +
                "\tcs.response_desc;";


        return jdbcTemplate.queryForObject(sql,
                new Object[]{id, companyId, id}, (resultSet, i) -> new SchoolRequestWithImageByIdDto2(resultSet.getInt(1),
                        resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime()
                        , resultSet.getInt(6), resultSet.getString(7), resultSet.getString(8), resultSet.getString(9),
                        resultSet.getInt(10), resultSet.getInt(11), resultSet.getString(12), resultSet.getDouble(13), resultSet.getString(14)));
    }


    public int getRequestsBySchoolIDPaginationCount(int id) {
        String sql = "SELECT\n" +
                "\tCount(DISTINCT request_id ) \n" +
                "FROM\n" +
                "\tefaz_school_tender AS tender\n" +
                "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                "\tLEFT JOIN efaz_company_profile AS p ON req.responsed_company_id = p.company_id \n" +
                "WHERE\n" +
                "\ttender.school_id = ? \n" +
                "\tAND request_expired_date >= NOW( ) \n" +
                "\tAND request_is_conformied = 0 ;";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }


    public SchoolRequestNewDto2ModelPagination getRequestsBySchoolIDPagination(int id, int page, int pageSize, int flag_ar) {

        int pages = (int) Math.ceil(((float) getRequestsBySchoolIDPaginationCount(id)) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;

        if (flag_ar == 0) {


            String sql = "SELECT\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date," +
                    " IFNULL(request_count,0) AS request_count, " +
                    "\tschool_id,\n" +
                    "\trequest_category_name,\n" +
                    "\tcount( responsed_request_id ) AS response_count,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcompany_category_id,\n" +
                    "\tresponsed_cost,\n" +
                    "\tresponse_date,\n" +
                    "\tis_aproved \n" +
                    "FROM\n" +
                    "\tefaz_school_tender AS tender\n" +
                    "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                    "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                    "\tLEFT JOIN efaz_company_profile AS p ON req.responsed_company_id = p.company_id \n" +
                    "WHERE\n" +
                    "\ttender.school_id = ?  AND request_expired_date>= NOW() AND request_is_conformied = 0 " +
                    "GROUP BY\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date, request_count, \n" +
                    "\tschool_id,\n" +
                    "\trequest_category_name,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcompany_category_id,\n" +
                    "\tresponsed_cost,\n" +
                    "\tresponse_date, is_aproved " +
                    " LIMIT ?,?;";


            List<SchoolRequestNewDto2Model> requests = jdbcTemplate.query(sql,
                    new Object[]{id, limitOffset, pageSize}, (resultSet, i) -> new SchoolRequestNewDto2Model(resultSet.getInt(1),
                            resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime(),
                            resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getInt(15)));

            return new SchoolRequestNewDto2ModelPagination(getRequestsBySchoolIDPaginationCount(id), pages, requests);


        } else {
            String sql = "SELECT\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date," +
                    " IFNULL(request_count,0) AS request_count, " +
                    "\tschool_id,\n" +
                    "\trequest_category_name_ar AS request_category_name,\n" +
                    "\tcount( responsed_request_id ) AS response_count,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcompany_category_id,\n" +
                    "\tresponsed_cost,\n" +
                    "\tresponse_date,\n" +
                    "\tis_aproved \n" +
                    "FROM\n" +
                    "\tefaz_school_tender AS tender\n" +
                    "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                    "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                    "\tLEFT JOIN efaz_company_profile AS p ON req.responsed_company_id = p.company_id \n" +
                    "WHERE\n" +
                    "\ttender.school_id = ?  AND request_expired_date>= NOW() AND request_is_conformied = 0 " +
                    "GROUP BY\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date, request_count, \n" +
                    "\tschool_id,\n" +
                    "\trequest_category_name,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcompany_category_id,\n" +
                    "\tresponsed_cost,\n" +
                    "\tresponse_date, is_aproved " +
                    " LIMIT ?,?;";


            List<SchoolRequestNewDto2Model> requests = jdbcTemplate.query(sql,
                    new Object[]{id, limitOffset, pageSize}, (resultSet, i) -> new SchoolRequestNewDto2Model(resultSet.getInt(1),
                            resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime(),
                            resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getInt(15)));

            return new SchoolRequestNewDto2ModelPagination(getRequestsBySchoolIDPaginationCount(id), pages, requests);
        }


    }

    public int getRequestsBySchoolIDPaginationCountByname(int id, String name) {
        String sql = "SELECT\n" +
                "\tCount(DISTINCT request_id ) \n" +
                "FROM\n" +
                "\tefaz_school_tender AS tender\n" +
                "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                "\tLEFT JOIN efaz_company_profile AS p ON req.responsed_company_id = p.company_id \n" +
                "WHERE\n" +
                "\ttender.school_id = ? \n" +
                "\tAND request_expired_date >= NOW( ) \n" +
                "\tAND request_is_conformied = 0 AND request_title LIKE ?;";
        return jdbcTemplate.queryForObject(sql, Integer.class, id, "%" + name + "%");
    }

    public SchoolRequestNewDto2ModelPagination getRequestsBySchoolIDPaginationByName(int id, int page, int pageSize, String name, int flag_ar) {

        int pages = (int) Math.ceil(((float) getRequestsBySchoolIDPaginationCountByname(id, name)) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;

        if (flag_ar == 0) {

            String sql = "SELECT\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date," +
                    " IFNULL(request_count,0) AS request_count, " +
                    "\tschool_id,\n" +
                    "\trequest_category_name,\n" +
                    "\tcount( responsed_request_id ) AS response_count,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcompany_category_id,\n" +
                    "\tresponsed_cost,\n" +
                    "\tresponse_date,\n" +
                    "\tis_aproved \n" +
                    "FROM\n" +
                    "\tefaz_school_tender AS tender\n" +
                    "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                    "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                    "\tLEFT JOIN efaz_company_profile AS p ON req.responsed_company_id = p.company_id \n" +
                    "WHERE\n" +
                    "\ttender.school_id = ?  AND request_expired_date>= NOW() AND request_is_conformied = 0 AND request_title LIKE ? " +
                    "GROUP BY\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date, request_count, \n" +
                    "\tschool_id,\n" +
                    "\trequest_category_name,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcompany_category_id,\n" +
                    "\tresponsed_cost,\n" +
                    "\tresponse_date, is_aproved " +
                    " LIMIT ?,?;";


            List<SchoolRequestNewDto2Model> requests = jdbcTemplate.query(sql,
                    new Object[]{id, "%" + name + "%", limitOffset, pageSize}, (resultSet, i) -> new SchoolRequestNewDto2Model(resultSet.getInt(1),
                            resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime(),
                            resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getInt(15)));

            return new SchoolRequestNewDto2ModelPagination(getRequestsBySchoolIDPaginationCountByname(id, name), pages, requests);
        } else {


            String sql = "SELECT\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date," +
                    " IFNULL(request_count,0) AS request_count, " +
                    "\tschool_id,\n" +
                    "\trequest_category_name AS request_category_name,\n" +
                    "\tcount( responsed_request_id ) AS response_count,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcompany_category_id,\n" +
                    "\tresponsed_cost,\n" +
                    "\tresponse_date,\n" +
                    "\tis_aproved \n" +
                    "FROM\n" +
                    "\tefaz_school_tender AS tender\n" +
                    "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                    "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                    "\tLEFT JOIN efaz_company_profile AS p ON req.responsed_company_id = p.company_id \n" +
                    "WHERE\n" +
                    "\ttender.school_id = ?  AND request_expired_date>= NOW() AND request_is_conformied = 0 AND request_title LIKE ? " +
                    "GROUP BY\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date, request_count, \n" +
                    "\tschool_id,\n" +
                    "\trequest_category_name,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcompany_category_id,\n" +
                    "\tresponsed_cost,\n" +
                    "\tresponse_date, is_aproved " +
                    " LIMIT ?,?;";


            List<SchoolRequestNewDto2Model> requests = jdbcTemplate.query(sql,
                    new Object[]{id, "%" + name + "%", limitOffset, pageSize}, (resultSet, i) -> new SchoolRequestNewDto2Model(resultSet.getInt(1),
                            resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime(),
                            resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getInt(15)));

            return new SchoolRequestNewDto2ModelPagination(getRequestsBySchoolIDPaginationCountByname(id, name), pages, requests);


        }
    }

    public SchoolRequestNewDto2ModelPagination getRequestsBySchoolIDPaginationByMore(int id, int page, int pageSize) {

        int pages = (int) Math.ceil(((float) getRequestsBySchoolIDPaginationCount(id)) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;


        String sql = "SELECT\n" +
                "\trequest_id,\n" +
                "\trequest_title,\n" +
                "\trequest_explaination,\n" +
                "\trequest_display_date,\n" +
                "\trequest_expired_date," +
                " IFNULL(request_count,0) AS request_count, " +
                "\tschool_id,\n" +
                "\trequest_category_name,\n" +
                "\tcount( responsed_request_id ) AS response_count,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_category_id,\n" +
                "\tresponsed_cost,\n" +
                "\tresponse_date,\n" +
                "\tis_aproved \n" +
                "FROM\n" +
                "\tefaz_school_tender AS tender\n" +
                "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                "\tLEFT JOIN efaz_company_profile AS p ON req.responsed_company_id = p.company_id \n" +
                "WHERE\n" +
                "\ttender.school_id = ?  AND request_expired_date>= NOW() AND request_is_conformied = 0 " +
                "GROUP BY\n" +
                "\trequest_id,\n" +
                "\trequest_title,\n" +
                "\trequest_explaination,\n" +
                "\trequest_display_date,\n" +
                "\trequest_expired_date, request_count, \n" +
                "\tschool_id,\n" +
                "\trequest_category_name,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_category_id,\n" +
                "\tresponsed_cost,\n" +
                "\tresponse_date, is_aproved  ORDER BY  request_expired_date DESC  " +
                " LIMIT ?,?;";


        List<SchoolRequestNewDto2Model> requests = jdbcTemplate.query(sql,
                new Object[]{id, limitOffset, pageSize}, (resultSet, i) -> new SchoolRequestNewDto2Model(resultSet.getInt(1),
                        resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime(),
                        resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getInt(15)));

        return new SchoolRequestNewDto2ModelPagination(getRequestsBySchoolIDPaginationCount(id), pages, requests);
    }

    public SchoolRequestNewDto2ModelPagination getRequestsBySchoolIDPaginationByLess(int id, int page, int pageSize) {

        int pages = (int) Math.ceil(((float) getRequestsBySchoolIDPaginationCount(id)) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;


        String sql = "SELECT\n" +
                "\trequest_id,\n" +
                "\trequest_title,\n" +
                "\trequest_explaination,\n" +
                "\trequest_display_date,\n" +
                "\trequest_expired_date," +
                " IFNULL(request_count,0) AS request_count, " +
                "\tschool_id,\n" +
                "\trequest_category_name,\n" +
                "\tcount( responsed_request_id ) AS response_count,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_category_id,\n" +
                "\tresponsed_cost,\n" +
                "\tresponse_date,\n" +
                "\tis_aproved \n" +
                "FROM\n" +
                "\tefaz_school_tender AS tender\n" +
                "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                "\tLEFT JOIN efaz_company_profile AS p ON req.responsed_company_id = p.company_id \n" +
                "WHERE\n" +
                "\ttender.school_id = ?  AND request_expired_date>= NOW() AND request_is_conformied = 0 " +
                "GROUP BY\n" +
                "\trequest_id,\n" +
                "\trequest_title,\n" +
                "\trequest_explaination,\n" +
                "\trequest_display_date,\n" +
                "\trequest_expired_date, request_count, \n" +
                "\tschool_id,\n" +
                "\trequest_category_name,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_category_id,\n" +
                "\tresponsed_cost,\n" +
                "\tresponse_date, is_aproved ORDER BY  request_expired_date ASC   " +
                " LIMIT ?,?;";


        List<SchoolRequestNewDto2Model> requests = jdbcTemplate.query(sql,
                new Object[]{id, limitOffset, pageSize}, (resultSet, i) -> new SchoolRequestNewDto2Model(resultSet.getInt(1),
                        resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime(),
                        resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getInt(15)));

        return new SchoolRequestNewDto2ModelPagination(getRequestsBySchoolIDPaginationCount(id), pages, requests);
    }

    public int getRequestsBySchoolIDPaginationCountByBothMore(int id, String name) {
        String sql = "SELECT\n" +
                "\tCount(DISTINCT request_id ) \n" +
                "FROM\n" +
                "\tefaz_school_tender AS tender\n" +
                "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                "\tLEFT JOIN efaz_company_profile AS p ON req.responsed_company_id = p.company_id \n" +
                "WHERE\n" +
                "\ttender.school_id = ? \n" +
                "\tAND request_expired_date >= NOW( ) \n" +
                "\tAND request_is_conformied = 0 AND request_title LIKE ?  ;";
        return jdbcTemplate.queryForObject(sql, Integer.class, id, "%" + name + "%");
    }

    public SchoolRequestNewDto2ModelPagination getRequestsBySchoolIDPaginationByBothMore(int id, int page, int pageSize, String name) {

        int pages = (int) Math.ceil(((float) getRequestsBySchoolIDPaginationCountByBothMore(id, name)) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;


        String sql = "SELECT\n" +
                "\trequest_id,\n" +
                "\trequest_title,\n" +
                "\trequest_explaination,\n" +
                "\trequest_display_date,\n" +
                "\trequest_expired_date," +
                " IFNULL(request_count,0) AS request_count, " +
                "\tschool_id,\n" +
                "\trequest_category_name,\n" +
                "\tcount( responsed_request_id ) AS response_count,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_category_id,\n" +
                "\tresponsed_cost,\n" +
                "\tresponse_date,\n" +
                "\tis_aproved \n" +
                "FROM\n" +
                "\tefaz_school_tender AS tender\n" +
                "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                "\tLEFT JOIN efaz_company_profile AS p ON req.responsed_company_id = p.company_id \n" +
                "WHERE\n" +
                "\ttender.school_id = ?  AND request_expired_date>= NOW() AND request_is_conformied = 0 AND request_title LIKE ? " +
                "GROUP BY\n" +
                "\trequest_id,\n" +
                "\trequest_title,\n" +
                "\trequest_explaination,\n" +
                "\trequest_display_date,\n" +
                "\trequest_expired_date, request_count, \n" +
                "\tschool_id,\n" +
                "\trequest_category_name,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_category_id,\n" +
                "\tresponsed_cost,\n" +
                "\tresponse_date, is_aproved ORDER BY  request_expired_date DESC  " +
                " LIMIT ?,?;";


        List<SchoolRequestNewDto2Model> requests = jdbcTemplate.query(sql,
                new Object[]{id, "%" + name + "%", limitOffset, pageSize}, (resultSet, i) -> new SchoolRequestNewDto2Model(resultSet.getInt(1),
                        resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime(),
                        resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getInt(15)));

        return new SchoolRequestNewDto2ModelPagination(getRequestsBySchoolIDPaginationCountByBothMore(id, name), pages, requests);
    }

    public int getRequestsBySchoolIDPaginationCountByBothLess(int id, String name) {
        String sql = "SELECT\n" +
                "\tCount(DISTINCT request_id ) \n" +
                "FROM\n" +
                "\tefaz_school_tender AS tender\n" +
                "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                "\tLEFT JOIN efaz_company_profile AS p ON req.responsed_company_id = p.company_id \n" +
                "WHERE\n" +
                "\ttender.school_id = ? \n" +
                "\tAND request_expired_date >= NOW( ) \n" +
                "\tAND request_is_conformied = 0 AND request_title LIKE ?;";
        return jdbcTemplate.queryForObject(sql, Integer.class, id, "%" + name + "%");
    }

    public SchoolRequestNewDto2ModelPagination getRequestsBySchoolIDPaginationByBothLess(int id, int page, int pageSize, String name) {

        int pages = (int) Math.ceil(((float) getRequestsBySchoolIDPaginationCountByBothLess(id, name)) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;


        String sql = "SELECT\n" +
                "\trequest_id,\n" +
                "\trequest_title,\n" +
                "\trequest_explaination,\n" +
                "\trequest_display_date,\n" +
                "\trequest_expired_date," +
                " IFNULL(request_count,0) AS request_count, " +
                "\tschool_id,\n" +
                "\trequest_category_name,\n" +
                "\tcount( responsed_request_id ) AS response_count,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_category_id,\n" +
                "\tresponsed_cost,\n" +
                "\tresponse_date,\n" +
                "\tis_aproved \n" +
                "FROM\n" +
                "\tefaz_school_tender AS tender\n" +
                "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                "\tLEFT JOIN efaz_company_profile AS p ON req.responsed_company_id = p.company_id \n" +
                "WHERE\n" +
                "\ttender.school_id = ?  AND request_expired_date>= NOW() AND request_is_conformied = 0 AND request_title LIKE ? " +
                "GROUP BY\n" +
                "\trequest_id,\n" +
                "\trequest_title,\n" +
                "\trequest_explaination,\n" +
                "\trequest_display_date,\n" +
                "\trequest_expired_date, request_count, \n" +
                "\tschool_id,\n" +
                "\trequest_category_name,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_category_id,\n" +
                "\tresponsed_cost,\n" +
                "\tresponse_date, is_aproved " +
                " ORDER BY  request_expired_date ASC " +
                " LIMIT ?,?;";


        List<SchoolRequestNewDto2Model> requests = jdbcTemplate.query(sql,
                new Object[]{id, "%" + name + "%", limitOffset, pageSize}, (resultSet, i) -> new SchoolRequestNewDto2Model(resultSet.getInt(1),
                        resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime(),
                        resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getInt(15)));

        return new SchoolRequestNewDto2ModelPagination(getRequestsBySchoolIDPaginationCountByBothLess(id, name), pages, requests);
    }


    public List<SchoolRequestNewDto2Model> getRequestsBySchoolID(int id, int flag_ar) {


        if (flag_ar == 0) {

            String sql = "SELECT\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date," +
                    " IFNULL(request_count,0) AS request_count, " +
                    "\tschool_id,\n" +
                    "\trequest_category_name,\n" +
                    "\tcount( responsed_request_id ) AS response_count,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcompany_category_id,\n" +
                    "\tresponsed_cost,\n" +
                    "\tresponse_date,\n" +
                    "\tis_aproved \n" +
                    "FROM\n" +
                    "\tefaz_school_tender AS tender\n" +
                    "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                    "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                    "\tLEFT JOIN efaz_company_profile AS p ON req.responsed_company_id = p.company_id \n" +
                    "WHERE\n" +
                    "\ttender.school_id = ?  AND request_expired_date>= NOW() AND request_is_conformied = 0 " +
                    "GROUP BY\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date, request_count, \n" +
                    "\tschool_id,\n" +
                    "\trequest_category_name_ar,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcompany_category_id,\n" +
                    "\tresponsed_cost,\n" +
                    "\tresponse_date, is_aproved;";


            return jdbcTemplate.query(sql,
                    new Object[]{id}, (resultSet, i) -> new SchoolRequestNewDto2Model(resultSet.getInt(1),
                            resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime(),
                            resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getInt(15)));
        } else {
            String sql = "SELECT\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date," +
                    " IFNULL(request_count,0) AS request_count, " +
                    "\tschool_id,\n" +
                    "\trequest_category_name_ar AS request_category_name,\n" +
                    "\tcount( responsed_request_id ) AS response_count,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcompany_category_id,\n" +
                    "\tresponsed_cost,\n" +
                    "\tresponse_date,\n" +
                    "\tis_aproved \n" +
                    "FROM\n" +
                    "\tefaz_school_tender AS tender\n" +
                    "\tINNER JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                    "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                    "\tLEFT JOIN efaz_company_profile AS p ON req.responsed_company_id = p.company_id \n" +
                    "WHERE\n" +
                    "\ttender.school_id = ?  AND request_expired_date>= NOW() AND request_is_conformied = 0 " +
                    "GROUP BY\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date, request_count, \n" +
                    "\tschool_id,\n" +
                    "\trequest_category_name_ar,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcompany_category_id,\n" +
                    "\tresponsed_cost,\n" +
                    "\tresponse_date, is_aproved;";


            return jdbcTemplate.query(sql,
                    new Object[]{id}, (resultSet, i) -> new SchoolRequestNewDto2Model(resultSet.getInt(1),
                            resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime(),
                            resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getInt(15)));
        }
    }

    public List<getSchoolCustomNewRequestByIdModel> getRequestOfSchoolByID(int id ,int flag_ar) {

        String sql = "";
        if (flag_ar == 0 ){


            sql = "SELECT\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date," +
                    " IFNULL(request_count, 0)AS request_count," +
                    "\tschool_id,\n" +
                    "\trequest_category_name,\n" +
                    "\tcount( responsed_request_id ) AS response_count,\n" +
                    "\tifnull( company_name, 0 ) AS company_name,\n" +
                    "\tifnull( company_logo_image, '' ) AS company_logo,\n" +
                    "\tifnull( category_name, 0 ) AS category_name,\n" +
                    "\tifnull( responsed_cost, 0 ) AS responsed_cost,\n" +
                    "\tifnull( response_date, NOW( ) ) AS response_date,\n" +
                    "\tifnull( response_id, 0 ) AS response_id,\n" +
                    "\tifnull( responsed_company_id, 0 ) AS responsed_company_id,\n" +
                    "\tis_aproved,\n" +
                    "\timage_one,\n" +
                    "\tresponse_desc \n" +
                    "FROM\n" +
                    "\tefaz_school_tender AS tender\n" +
                    "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                    "\tLEFT JOIN efaz_company_profile AS cp ON req.responsed_company_id = cp.company_id\n" +
                    "\tLEFT JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                    "\tLEFT JOIN efaz_company_category AS cc ON cp.company_category_id = cc.category_id\n" +
                    "\tLEFT JOIN school_requst_images AS sri ON tender.images_id = sri.image_id \n" +
                    "WHERE\n" +
                    "\trequest_id = ? \n" +
                    "GROUP BY\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date,\n" +
                    "\tschool_id,\n" +
                    "\trequest_category_name,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcategory_name,\n" +
                    "\tresponsed_cost,\n" +
                    "\tresponse_date,\n" +
                    "\tresponse_id,\n" +
                    "\tresponsed_company_id,\n" +
                    "\tis_aproved,\n" +
                    "\timage_one,\n" +
                    "\tresponse_desc;";


        }else {


            sql = "SELECT\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date," +
                    " IFNULL(request_count, 0)AS request_count," +
                    "\tschool_id,\n" +
                    "\trequest_category_name_ar AS request_category_name,\n" +
                    "\tcount( responsed_request_id ) AS response_count,\n" +
                    "\tifnull( company_name, 0 ) AS company_name,\n" +
                    "\tifnull( company_logo_image, '' ) AS company_logo,\n" +
                    "\tifnull( category_name, 0 ) AS category_name,\n" +
                    "\tifnull( responsed_cost, 0 ) AS responsed_cost,\n" +
                    "\tifnull( response_date, NOW( ) ) AS response_date,\n" +
                    "\tifnull( response_id, 0 ) AS response_id,\n" +
                    "\tifnull( responsed_company_id, 0 ) AS responsed_company_id,\n" +
                    "\tis_aproved,\n" +
                    "\timage_one,\n" +
                    "\tresponse_desc \n" +
                    "FROM\n" +
                    "\tefaz_school_tender AS tender\n" +
                    "\tLEFT JOIN efaz_company_response_school_request AS req ON tender.request_id = req.responsed_request_id\n" +
                    "\tLEFT JOIN efaz_company_profile AS cp ON req.responsed_company_id = cp.company_id\n" +
                    "\tLEFT JOIN efaz_school_request_category AS cat ON tender.requests_category_id = cat.request_category_id\n" +
                    "\tLEFT JOIN efaz_company_category AS cc ON cp.company_category_id = cc.category_id\n" +
                    "\tLEFT JOIN school_requst_images AS sri ON tender.images_id = sri.image_id \n" +
                    "WHERE\n" +
                    "\trequest_id = ? \n" +
                    "GROUP BY\n" +
                    "\trequest_id,\n" +
                    "\trequest_title,\n" +
                    "\trequest_explaination,\n" +
                    "\trequest_display_date,\n" +
                    "\trequest_expired_date,\n" +
                    "\tschool_id,\n" +
                    "\trequest_category_name,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcategory_name,\n" +
                    "\tresponsed_cost,\n" +
                    "\tresponse_date,\n" +
                    "\tresponse_id,\n" +
                    "\tresponsed_company_id,\n" +
                    "\tis_aproved,\n" +
                    "\timage_one,\n" +
                    "\tresponse_desc;";


        }



        return jdbcTemplate.query(sql,
                new Object[]{id}, (resultSet, i) -> new getSchoolCustomNewRequestByIdModel(resultSet.getInt(1),
                        resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime()
                        , resultSet.getInt(6), resultSet.getInt(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10),
                        resultSet.getString(11), resultSet.getString(12), resultSet.getDouble(13), resultSet.getTimestamp(14).getTime(),
                        resultSet.getInt(15), resultSet.getInt(16), resultSet.getInt(17), resultSet.getString(18), resultSet.getString(19)));
    }


    public List<SchoolRequestNewDto> getRequestsByCategoryID(String id) {


        int category = jdbcTemplate.queryForObject("SELECT request_category_id  FROM efaz_school_request_category WHERE  request_category_name LIKE ?;",
                Integer.class, "%" + id + "%");


        String sql = "SELECT " +
                "request_id, request_title, request_explaination, request_display_date, " +
                "    request_expired_date, school_id," +
                "    request_category_name, " +
                "    count( responsed_request_id) AS response_count" +
                " FROM efaz_school_tender AS tender INNER JOIN" +
                "                        efaz_school_request_category AS cat" +
                "                         ON" +
                "                         tender.requests_category_id = cat.request_category_id" +
                "                         LEFT JOIN efaz_company_response_school_request AS req" +
                "                         ON tender.request_id = req.responsed_request_id" +
                "                      WHERE  tender.requests_category_id =?   " +
                "  GROUP BY request_id;";


//        String sql = "SELECT " +
//                "request_id, request_title, request_explaination, request_display_date, " +
//                "    request_expired_date, school_id," +
//                "    request_category_name" +
//                " FROM efaz_school_tender AS tender INNER JOIN" +
//                "                        efaz_school_request_category AS cat" +
//                "                         ON" +
//                "                         tender.request_category_id = cat.request_category_id"+
//                " WHERE  tender.request_category_id = ?;";

        return jdbcTemplate.query(sql,
                new Object[]{category}, (resultSet, i) -> new SchoolRequestNewDto(resultSet.getInt(1),
                        resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime()
                        , resultSet.getInt(6), resultSet.getString(7), resultSet.getInt(8)));
    }


    public int updateRequest(int request_id, String request_title, String request_explaination,
                             long request_display_date, long request_expired_date, int school_id,
                             String request_category_id, int request_count, int flag_ar) {

        if (flag_ar == 0){

            int category = jdbcTemplate.queryForObject("SELECT request_category_id  FROM efaz_school_request_category WHERE  request_category_name LIKE ?;",
                    Integer.class, "%" + request_category_id + "%");

            return jdbcTemplate.update("update efaz_school_tender set request_details_file=?," + " images_id=?, request_title=?," +
                            "request_explaination=?," + " request_display_date=?, request_expired_date=?, request_deliver_date=?," +
                            "request_payment_date=?, request_is_available=?, request_is_conformied=?, school_id=?, requests_category_id=?," +
                            " receive_palce_id=?, extended_payment=?, request_count=? " +
                            " where request_id=?;", null, null, request_title, request_explaination, new Timestamp(request_display_date)
                    , new Timestamp(request_expired_date), null, null, null, null, school_id, category,
                    null, null, request_count, request_id);

        }else {

            int category = jdbcTemplate.queryForObject("SELECT request_category_id  FROM efaz_school_request_category WHERE  request_category_name_ar LIKE ?;",
                    Integer.class, "%" + request_category_id + "%");

            return jdbcTemplate.update("update efaz_school_tender set request_details_file=?," + " images_id=?, request_title=?," +
                            "request_explaination=?," + " request_display_date=?, request_expired_date=?, request_deliver_date=?," +
                            "request_payment_date=?, request_is_available=?, request_is_conformied=?, school_id=?, requests_category_id=?," +
                            " receive_palce_id=?, extended_payment=?, request_count=? " +
                            " where request_id=?;", null, null, request_title, request_explaination, new Timestamp(request_display_date)
                    , new Timestamp(request_expired_date), null, null, null, null, school_id, category,
                    null, null, request_count, request_id);

        }



    }

    public int deleteSchoolRequest(int id) {
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=0;");
        int res = jdbcTemplate.update("DELETE FROM efaz_school_tender WHERE request_id=?", id);
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=1;");
        return res;
    }


    public boolean isExist(int id) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM efaz_school_tender WHERE request_id=?;",
                Integer.class, id);
        return cnt != null && cnt > 0;
    }

    public boolean isExistMine(int requestId, int schoolId) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM efaz_school_tender WHERE request_id=? AND school_id=?;",
                Integer.class, requestId, schoolId);
        return cnt != null && cnt > 0;
    }

    public boolean isExistOrganizationAndOffer(int school_id) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM efaz_school_profile WHERE school_id=? ;",
                Integer.class, school_id);
        return cnt != null && cnt > 0;
    }


    public List<SchoolRequestNewDtoWitCompany> getSingleTenderDetails(int request_id) {


//
        String sql = "SELECT " +
                "request_id, request_title, request_explaination, request_display_date, " +
                "    request_expired_date, " +
                "    count( req.responsed_request_id) AS response_count, res.response_date, res.responsed_cost, company_name, company_logo_image, category_name " +
                " FROM (efaz_school_tender AS tender " +
                "                         LEFT JOIN efaz_company_response_school_request AS req " +
                "                         ON tender.request_id = req.responsed_request_id)" +
                "                         LEFT JOIN efaz_company_response_school_request AS res " +
                "                         ON tender.request_id = res.responsed_request_id " +
                "                         LEFT JOIN efaz_company_profile AS com " +
                "                         ON res.responsed_company_id = com.company_id" +
                "                         INNER JOIN efaz_company_category AS cat " +
                "                         ON com.company_category_id = cat.category_id " +
                "where request_id =?" +
                "                         GROUP BY company_name;";

        String sql1 = "SELECT " +
                "                request_id, request_title, request_explaination, request_display_date, " +
                "                    request_expired_date, res.response_date, res.responsed_cost, company_name, company_logo_image, category_name" +
                "                 FROM efaz_school_tender AS tender " +
                "  LEFT JOIN efaz_company_response_school_request AS res " +
                "     ON tender.request_id = res.responsed_request_id" +
                "                  LEFT JOIN efaz_company_profile AS com " +
                "                                        ON res.responsed_company_id = com.company_id" +
                " INNER JOIN efaz_company_category AS cat " +
                " ON com.company_category_id = cat.category_id " +
                "  where request_id =?;";


        return jdbcTemplate.query(sql1,
                new Object[]{request_id}, (resultSet, i) -> new SchoolRequestNewDtoWitCompany(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getTimestamp(4).getTime(), resultSet.getTimestamp(5).getTime(),
                        resultSet.getTimestamp(6).getTime(), resultSet.getDouble(7),
                        resultSet.getString(8), resultSet.getBytes(9), resultSet.getString(10)));


    }

    public int acceptOffer(int request_id) {
        return jdbcTemplate.update("UPDATE efaz_school_tender SET request_is_conformied=1 WHERE request_id=?;", request_id);
    }

    public int cancelOffer(int request_id) {
        return jdbcTemplate.update("UPDATE efaz_school_tender SET request_is_conformied=0 WHERE request_id=?;", request_id);
    }


}
