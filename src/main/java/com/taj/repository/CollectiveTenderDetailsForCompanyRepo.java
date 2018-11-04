package com.taj.repository;

import com.taj.model.CollectiveTenderDetailsForCompanyModel;
import com.taj.model.Pagination.CollectiveTenderDetailsForCompanyModelPagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by User on 9/3/2018.
 */
@Repository
public class CollectiveTenderDetailsForCompanyRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;


    public int getTenderDetailsPaginationCount(int id) {
        String sql = "SELECT\n" +
                "\tCOUNT(DISTINCT tender_id ) AS count \n" +
                "FROM\n" +
                "\ttakatf_tender AS t\n" +
                "\tLEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id\n" +
                "\tLEFT JOIN tkatf_tender_catgory_request AS c ON c.t_tender_id = t.tender_id\n" +
                "\tLEFT JOIN takataf_request_cat_count AS tr ON t.tender_id = tr.tend_id\n" +
                "\tLEFT JOIN efaz_company_category AS ca ON c.t_category_id = category_id\n" +
                "\tLEFT JOIN takataf_request_cat_count AS tr2 ON tr.cat_id = tr2.cat_id \n" +
                "\tAND tr2.tend_id = ? \n" +
                "WHERE\n" +
                "\tt.tender_id = ?;";
        return jdbcTemplate.queryForObject(sql, Integer.class, new Object[]{id, id});
    }


    public CollectiveTenderDetailsForCompanyModelPagination getTenderDetailsPAgination(int id, int page, int pageSize, int flag_ar) {

        int pages = (int) Math.ceil(((float) getTenderDetailsPaginationCount(id)) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;

        if (flag_ar == 0) {


            String sql = "SELECT\n" +
                    "\ttender_id,\n" +
                    "\ttender_logo,\n" +
                    "\ttender_title,\n" +
                    "\ttender_explain,\n" +
                    "\tIFNULL( tender_company_display_date, '1970-01-01 02:00:00' ) AS tender_company_display_date,\n" +
                    "\tIFNULL( tender_company_expired_date, '1970-01-01 02:00:00' ) AS tender_company_expired_date,\n" +
                    "\tCOUNT( DISTINCT request_id ) AS response_count,\n" +
                    "\tt_category_id AS cat_id,\n" +
                    "\tIFNULL( category_name, 0 ) AS category_name,\n" +
                    "\tIFNULL( SUM( DISTINCT tr2.count ), 0 ) AS sum \n" +
                    "FROM\n" +
                    "\ttakatf_tender AS t\n" +
                    "\tLEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id\n" +
                    "\tLEFT JOIN tkatf_tender_catgory_request AS c ON c.t_tender_id = t.tender_id\n" +
                    "\tLEFT JOIN takataf_request_cat_count AS tr ON t.tender_id = tr.tend_id\n" +
                    "\tLEFT JOIN efaz_company_category AS ca ON c.t_category_id = category_id\n" +
                    "\tLEFT JOIN takataf_request_cat_count AS tr2 ON tr.cat_id = tr2.cat_id \n" +
                    "\tAND tr2.tend_id = ? \n" +
                    "WHERE\n" +
                    "\tt.tender_id = ? \n" +
                    "GROUP BY\n" +
                    "\tt_category_id,\n" +
                    "\ttender_id,\n" +
                    "\ttender_title,\n" +
                    "\ttender_explain,\n" +
                    "\ttender_company_display_date,\n" +
                    "\ttender_company_expired_date,\n" +
                    "\tcategory_name " +
                    " LIMIT ?,?;";

            List<CollectiveTenderDetailsForCompanyModel> tenders = jdbcTemplate.query(sql, new Object[]{id, id, limitOffset, pageSize},
                    (resultSet, i) -> new CollectiveTenderDetailsForCompanyModel(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                            resultSet.getString(4), resultSet.getTimestamp(5).getTime(), resultSet.getTimestamp(6).getTime(),
                            resultSet.getInt(7), resultSet.getInt(8), resultSet.getString(9), resultSet.getInt(10)));
            return new CollectiveTenderDetailsForCompanyModelPagination(getTenderDetailsPaginationCount(id), pages, tenders);

        }else {


            String sql = "SELECT\n" +
                    "\ttender_id,\n" +
                    "\ttender_logo,\n" +
                    "\ttender_title,\n" +
                    "\ttender_explain,\n" +
                    "\tIFNULL( tender_company_display_date, '1970-01-01 02:00:00' ) AS tender_company_display_date,\n" +
                    "\tIFNULL( tender_company_expired_date, '1970-01-01 02:00:00' ) AS tender_company_expired_date,\n" +
                    "\tCOUNT( DISTINCT request_id ) AS response_count,\n" +
                    "\tt_category_id AS cat_id,\n" +
                    "\tIFNULL( category_name_ar, 0 ) AS category_name,\n" +
                    "\tIFNULL( SUM( DISTINCT tr2.count ), 0 ) AS sum \n" +
                    "FROM\n" +
                    "\ttakatf_tender AS t\n" +
                    "\tLEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id\n" +
                    "\tLEFT JOIN tkatf_tender_catgory_request AS c ON c.t_tender_id = t.tender_id\n" +
                    "\tLEFT JOIN takataf_request_cat_count AS tr ON t.tender_id = tr.tend_id\n" +
                    "\tLEFT JOIN efaz_company_category AS ca ON c.t_category_id = category_id\n" +
                    "\tLEFT JOIN takataf_request_cat_count AS tr2 ON tr.cat_id = tr2.cat_id \n" +
                    "\tAND tr2.tend_id = ? \n" +
                    "WHERE\n" +
                    "\tt.tender_id = ? \n" +
                    "GROUP BY\n" +
                    "\tt_category_id,\n" +
                    "\ttender_id,\n" +
                    "\ttender_title,\n" +
                    "\ttender_explain,\n" +
                    "\ttender_company_display_date,\n" +
                    "\ttender_company_expired_date,\n" +
                    "\tcategory_name " +
                    " LIMIT ?,?;";

            List<CollectiveTenderDetailsForCompanyModel> tenders = jdbcTemplate.query(sql, new Object[]{id, id, limitOffset, pageSize},
                    (resultSet, i) -> new CollectiveTenderDetailsForCompanyModel(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                            resultSet.getString(4), resultSet.getTimestamp(5).getTime(), resultSet.getTimestamp(6).getTime(),
                            resultSet.getInt(7), resultSet.getInt(8), resultSet.getString(9), resultSet.getInt(10)));
            return new CollectiveTenderDetailsForCompanyModelPagination(getTenderDetailsPaginationCount(id), pages, tenders);

        }
    }


    public List<CollectiveTenderDetailsForCompanyModel> getTenderDetails(int id, int flag_ar) {
        if (flag_ar == 0) {
            String sql = "SELECT\n" +
                    "\ttender_id,\n" +
                    "\ttender_logo,\n" +
                    "\ttender_title,\n" +
                    "\ttender_explain,\n" +
                    "\tIFNULL( tender_company_display_date, '1970-01-01 02:00:00' ) AS tender_company_display_date,\n" +
                    "\tIFNULL( tender_company_expired_date, '1970-01-01 02:00:00' ) AS tender_company_expired_date,\n" +
                    "\tCOUNT( DISTINCT request_id ) AS response_count,\n" +
                    "\tt_category_id AS cat_id,\n" +
                    "\tIFNULL( category_name, '' ) AS category_name,\n" +
                    "\tIFNULL( SUM( DISTINCT tr2.count ), 0 ) AS sum \n" +
                    "FROM\n" +
                    "\ttakatf_tender AS t\n" +
                    "\tLEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id\n" +
                    "\tLEFT JOIN tkatf_tender_catgory_request AS c ON c.t_tender_id = t.tender_id\n" +
                    "\tLEFT JOIN takataf_request_cat_count AS tr ON t.tender_id = tr.tend_id\n" +
                    "\tLEFT JOIN efaz_company_category AS ca ON c.t_category_id = category_id\n" +
                    "\tLEFT JOIN takataf_request_cat_count AS tr2 ON tr.cat_id = tr2.cat_id \n" +
                    "\tAND tr2.tend_id = ? \n" +
                    "WHERE\n" +
                    "\tt.tender_id = ? \n" +
                    "GROUP BY\n" +
                    "\tt_category_id,\n" +
                    "\ttender_id,\n" +
                    "\ttender_title,\n" +
                    "\ttender_explain,\n" +
                    "\ttender_company_display_date,\n" +
                    "\ttender_company_expired_date,\n" +
                    "\tcategory_name;";

            return jdbcTemplate.query(sql, new Object[]{id, id},
                    (resultSet, i) -> new CollectiveTenderDetailsForCompanyModel(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                            resultSet.getString(4), resultSet.getTimestamp(5).getTime(), resultSet.getTimestamp(6).getTime(),
                            resultSet.getInt(7), resultSet.getInt(8), resultSet.getString(9), resultSet.getInt(10)));
        }else {



            String sql = "SELECT\n" +
                    "\ttender_id,\n" +
                    "\ttender_logo,\n" +
                    "\ttender_title,\n" +
                    "\ttender_explain,\n" +
                    "\tIFNULL( tender_company_display_date, '1970-01-01 02:00:00' ) AS tender_company_display_date,\n" +
                    "\tIFNULL( tender_company_expired_date, '1970-01-01 02:00:00' ) AS tender_company_expired_date,\n" +
                    "\tCOUNT( DISTINCT request_id ) AS response_count,\n" +
                    "\tt_category_id AS cat_id,\n" +
                    "\tIFNULL( category_name_ar, '' ) AS category_name,\n" +
                    "\tIFNULL( SUM( DISTINCT tr2.count ), 0 ) AS sum \n" +
                    "FROM\n" +
                    "\ttakatf_tender AS t\n" +
                    "\tLEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id\n" +
                    "\tLEFT JOIN tkatf_tender_catgory_request AS c ON c.t_tender_id = t.tender_id\n" +
                    "\tLEFT JOIN takataf_request_cat_count AS tr ON t.tender_id = tr.tend_id\n" +
                    "\tLEFT JOIN efaz_company_category AS ca ON c.t_category_id = category_id\n" +
                    "\tLEFT JOIN takataf_request_cat_count AS tr2 ON tr.cat_id = tr2.cat_id \n" +
                    "\tAND tr2.tend_id = ? \n" +
                    "WHERE\n" +
                    "\tt.tender_id = ? \n" +
                    "GROUP BY\n" +
                    "\tt_category_id,\n" +
                    "\ttender_id,\n" +
                    "\ttender_title,\n" +
                    "\ttender_explain,\n" +
                    "\ttender_company_display_date,\n" +
                    "\ttender_company_expired_date,\n" +
                    "\tcategory_name;";

            return jdbcTemplate.query(sql, new Object[]{id, id},
                    (resultSet, i) -> new CollectiveTenderDetailsForCompanyModel(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                            resultSet.getString(4), resultSet.getTimestamp(5).getTime(), resultSet.getTimestamp(6).getTime(),
                            resultSet.getInt(7), resultSet.getInt(8), resultSet.getString(9), resultSet.getInt(10)));



        }
    }
}
