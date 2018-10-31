package com.taj.repository;

import com.taj.model.SchoolRequestCategoryModelEnglish;
import com.taj.model.schoolCategoriesToWEBSITE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by User on 7/3/2018.
 */
@Repository
public class SchoolRequestCategoryRepo {


    @Autowired
    JdbcTemplate jdbcTemplate;

    public int addSchoolRequestCategories(String categoryName, String categoryNameAr) {
        return jdbcTemplate.update("INSERT INTO efaz_school_request_category VALUES (?,?,?)", null, categoryName, categoryNameAr);
    }

    public List<SchoolRequestCategoryModelEnglish> getSchoolRequestCategories(int flag_ar) {
        String sql = "";
        if (flag_ar == 0){
            sql = "SELECT request_category_id, request_category_name FROM efaz_school_request_category;";
        }else {
            sql = "SELECT request_category_id, request_category_name_ar AS request_category_name FROM efaz_school_request_category;";
        }
        return jdbcTemplate.query(sql,
                ((resultSet, i) -> new SchoolRequestCategoryModelEnglish(resultSet.getInt(1), resultSet.getString(2))));
    }

    public SchoolRequestCategoryModelEnglish getSchoolRequestCategory(int id, int flag_ar) {

        String sql = "";
        if (flag_ar == 0){
            sql = "SELECT request_category_id, request_category_name FROM efaz_school_request_category WHERE request_category_id=?;";
        }else {
            sql = "SELECT request_category_id, request_category_name_ar AS request_category_name FROM efaz_school_request_category WHERE request_category_id=?;";
        }

        return jdbcTemplate.queryForObject(sql, new Object[]{id},
                ((resultSet, i) -> new SchoolRequestCategoryModelEnglish(resultSet.getInt(1), resultSet.getString(2))));
    }

    public int updateSchoolRequestCategory(int id, String name, String name_ar) {
        return jdbcTemplate.update("UPDATE efaz_school_request_category SET request_category_name=?, request_category_name_ar=? WHERE request_category_id=?",
                name, name_ar, id);
    }

    public int deleteSchoolRequestCategory(int id) {
        return jdbcTemplate.update("DELETE FROM efaz_school_request_category WHERE request_category_id=?", id);
    }


    public List<schoolCategoriesToWEBSITE> getSchoolRequestCategoriesForWeb(int flag_ar) {
        String sql = "";
        if ( flag_ar == 0){
            sql = "SELECT\n" +
                    "\trequest_category_id,\n" +
                    "\trequest_category_name,\n" +
                    "\tCount( DISTINCT t.request_id ) AS request_count,\n" +
                    "\tCount( DISTINCT school_id ) AS school_count,\n" +
                    "\tCount( of.response_id ) AS offer_count \n" +
                    "FROM\n" +
                    "\tefaz_school_request_category AS sc\n" +
                    "\tLEFT JOIN efaz_school_tender AS t ON t.requests_category_id = sc.request_category_id\n" +
                    "\tLEFT JOIN efaz_company_response_school_request AS of ON t.request_id = of.responsed_request_id \n" +
                    "GROUP BY\n" +
                    "\trequest_category_id,\n" +
                    "\trequest_category_name;";
        }else {

            sql = "SELECT\n" +
                    "\trequest_category_id,\n" +
                    "\trequest_category_name_ar AS request_category_name,\n" +
                    "\tCount( DISTINCT t.request_id ) AS request_count,\n" +
                    "\tCount( DISTINCT school_id ) AS school_count,\n" +
                    "\tCount( of.response_id ) AS offer_count \n" +
                    "FROM\n" +
                    "\tefaz_school_request_category AS sc\n" +
                    "\tLEFT JOIN efaz_school_tender AS t ON t.requests_category_id = sc.request_category_id\n" +
                    "\tLEFT JOIN efaz_company_response_school_request AS of ON t.request_id = of.responsed_request_id \n" +
                    "GROUP BY\n" +
                    "\trequest_category_id,\n" +
                    "\trequest_category_name;";

        }


        return jdbcTemplate.query(sql,
                ((resultSet, i) -> new schoolCategoriesToWEBSITE(resultSet.getInt(1), resultSet.getString(2),resultSet.getInt(3),resultSet.getInt(4),resultSet.getInt(5))));
    }


    public List<schoolCategoriesToWEBSITE> getSchoolRequestCategoriesForWebById(int id,  int flag_ar) {
        String sql = "";
        if (flag_ar == 0){
            sql = "SELECT " +
                    "request_category_id, request_category_name, Count(DISTINCT t.request_id) as request_count, Count(Distinct school_id) as school_count, Count(of.response_id) as offer_count FROM efaz_school_request_category as sc " +
                    "LEFT JOIN " +
                    "    efaz_school_tender as t  ON sc.request_category_id = t.requests_category_id " +
                    "LEFT JOIN " +
                    "     efaz_company_response_school_request as of  ON t.request_id = of.responsed_request_id " +
                    " WHERE request_category_id=? " +
                    "    Group by request_category_id;";
        }else {
            sql = "SELECT " +
                    "request_category_id, request_category_name_ar AS request_category_name, Count(DISTINCT t.request_id) as request_count, Count(Distinct school_id) as school_count, Count(of.response_id) as offer_count FROM efaz_school_request_category as sc " +
                    "LEFT JOIN " +
                    "    efaz_school_tender as t  ON sc.request_category_id = t.requests_category_id " +
                    "LEFT JOIN " +
                    "     efaz_company_response_school_request as of  ON t.request_id = of.responsed_request_id " +
                    " WHERE request_category_id=? " +
                    "    Group by request_category_id;";
        }

        return jdbcTemplate.query(sql,new Object[]{id},
                ((resultSet, i) -> new schoolCategoriesToWEBSITE(resultSet.getInt(1),
                        resultSet.getString(2),resultSet.getInt(3),resultSet.getInt(4),resultSet.getInt(5))));
    }


}
