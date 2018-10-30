package com.taj.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taj.model.*;
import com.taj.model.Pagination.TakatafMyTenderPageDTOPagination;
import com.taj.model.Pagination.TakatafTenderPOJOPagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

/**
 * Created by User on 8/19/2018.
 */
@Repository
public class TakatafTenderNewRepo {


    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ObjectMapper mapper;

    public int addTender(String tender_logo, String tender_title, String tender_explain, long tender_display_date, long tender_expire_date,
                         long tender_company_display_date, long tender_company_expired_date, List<TakatfTenderCategoryPOJO> cats, int flag_ar) {


        if (flag_ar == 0) {
            for (int i = 0; i < cats.size(); i++) {
                int categorys = jdbcTemplate.queryForObject("SELECT category_id  FROM  efaz_company_category WHERE  category_name LIKE ?;",
                        Integer.class, "%" + cats.get(i).getCategory_name().trim() + "%");
                String sql = "SELECT\n" +
                        "\tcount(*) \n" +
                        "FROM\n" +
                        "\ttakatf_tender AS tend\n" +
                        "\tLEFT JOIN tkatf_tender_catgory_request AS cat ON tend.tender_id = cat.t_tender_id \n" +
                        "WHERE\n" +
                        " tend.tender_company_expired_date >= now() " +
                        " AND cat.t_category_id = ?;";
                int num = jdbcTemplate.queryForObject(sql, Integer.class, categorys);
                if (num > 0) {

                    return -100;
                }
            }
        }else {
            for (int i = 0; i < cats.size(); i++) {
                int categorys = jdbcTemplate.queryForObject("SELECT category_id  FROM  efaz_company_category WHERE  category_name_ar LIKE ?;",
                        Integer.class, "%" + cats.get(i).getCategory_name().trim() + "%");
                String sql = "SELECT\n" +
                        "\tcount(*) \n" +
                        "FROM\n" +
                        "\ttakatf_tender AS tend\n" +
                        "\tLEFT JOIN tkatf_tender_catgory_request AS cat ON tend.tender_id = cat.t_tender_id \n" +
                        "WHERE\n" +
                        " tend.tender_company_expired_date >= now() " +
                        " AND cat.t_category_id = ?;";
                int num = jdbcTemplate.queryForObject(sql, Integer.class, categorys);
                if (num > 0) {

                    return -100;
                }
            }
        }

        KeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                final PreparedStatement ps = connection.prepareStatement("INSERT INTO takatf_tender VALUES (?,?,?,?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, null);
                ps.setString(2, tender_logo);
                ps.setString(3, tender_title);
                ps.setString(4, tender_explain);/////////
                ps.setTimestamp(5, new Timestamp(tender_display_date));
                ps.setTimestamp(6, new Timestamp(tender_expire_date));
                ps.setInt(7, 0);
                ps.setInt(8, 1);
                if (tender_company_display_date == 0) {
                    ps.setTimestamp(10, null);

                } else {
                    ps.setTimestamp(10, new Timestamp(tender_company_display_date));
                }

                if (tender_company_expired_date == 0) {
                    ps.setTimestamp(9, null);
                } else {
                    ps.setTimestamp(9, new Timestamp(tender_company_expired_date));
                }
                return ps;
            }

        }, key);
        //int log_id = jdbcTemplate.update("INSERT INTO efaz_login VALUES (?,?,?,?,?)", null, user_email,
        //        encodedPassword, is_active, login_type);
        int tend_id = key.getKey().intValue();


//        int a = jdbcTemplate.update("INSERT INTO takatf_tender VALUES (?,?,?,?,?,?,?,?,?,?)", null, tender_logo, tender_title, tender_explain,
//                new Timestamp(tender_display_date), new Timestamp(tender_expire_date), 0, 1, new Timestamp(tender_company_display_date),
//                new Timestamp(tender_company_expired_date));
        if (flag_ar == 0) {
            for (int i = 0; i < cats.size(); i++) {
                int categorys = jdbcTemplate.queryForObject("SELECT category_id  FROM  efaz_company_category WHERE  category_name LIKE ?;",
                        Integer.class, "%" + cats.get(i).getCategory_name().trim() + "%");

                jdbcTemplate.update("INSERT INTO tkatf_tender_catgory_request VALUES  (?,?,?)", null, tend_id, categorys);
            }
        }else {
            for (int i = 0; i < cats.size(); i++) {
                int categorys = jdbcTemplate.queryForObject("SELECT category_id  FROM  efaz_company_category WHERE  category_name_ar LIKE ?;",
                        Integer.class, "%" + cats.get(i).getCategory_name().trim() + "%");

                jdbcTemplate.update("INSERT INTO tkatf_tender_catgory_request VALUES  (?,?,?)", null, tend_id, categorys);
            }
        }


        return tend_id;
    }


    public List<CategoriesInUse> getCategoriesInUse() {

        String sql2 = "SELECT DISTINCT\n" +
                "\tcategory_name \n" +
                "FROM\n" +
                "\ttkatf_tender_catgory_request AS re\n" +
                "\tLEFT JOIN efaz_company_category AS cat ON re.t_category_id = cat.category_id\n" +
                "\tLEFT JOIN takatf_tender as t ON re.t_tender_id = t.tender_id\n" +
                "\tWHERE (tender_expire_date>= NOW() OR tender_company_expired_date>=NOW());";
        List<CategoriesInUse> data = jdbcTemplate.query(sql2,
                (resultSet, i1) -> new CategoriesInUse(resultSet.getString(1)));
        return data;
    }



    public List<CategoriesInUseArabic> getCategoriesInUseArabic() {

        String sql2 = "SELECT DISTINCT\n" +
                "\tcategory_name_ar \n" +
                "FROM\n" +
                "\ttkatf_tender_catgory_request AS re\n" +
                "\tLEFT JOIN efaz_company_category AS cat ON re.t_category_id = cat.category_id\n" +
                "\tLEFT JOIN takatf_tender as t ON re.t_tender_id = t.tender_id\n" +
                "\tWHERE (tender_expire_date>= NOW() OR tender_company_expired_date>=NOW());";
        List<CategoriesInUseArabic> data = jdbcTemplate.query(sql2,
                (resultSet, i1) -> new CategoriesInUseArabic(resultSet.getString(1)));
        return data;
    }



    public List<TakatafTenderPOJO> getTenders() {
        String sql = "SELECT tender_id, tender_logo, tender_title, tender_explain, tender_display_date, tender_expire_date , " +
                "                 IFNULL(tender_company_display_date,'1970-01-01 02:00:00'), IFNULL(tender_company_expired_date,'1970-01-01 02:00:00')," +
                "                 count(distinct request_id) AS response_count " +
                "                                FROM takatf_tender AS t  " +
                "                                LEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id GROUP BY tender_id;";
        return jdbcTemplate.query(sql,
                (resultSet, i) -> new TakatafTenderPOJO(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4),
                        resultSet.getTimestamp(5).getTime(), resultSet.getTimestamp(6).getTime(), resultSet.getTimestamp(7).getTime(),
                        resultSet.getTimestamp(8).getTime(), resultSet.getInt(9)));
    }


    public int getTendersPaginationCount() {
        String sql = "SELECT\n" +
                "\tCOUNT( tender_id ) AS count \n" +
                "FROM\n" +
                "\ttakatf_tender;";
        return jdbcTemplate.queryForObject(sql, Integer.class);

    }

    public TakatafTenderPOJOPagination getTendersPagination(int page, int pageSize) {

        int pages = (int) Math.ceil(((float) getTendersPaginationCount()) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;

        String sql = "SELECT\n" +
                "\ttender_id,\n" +
                "\ttender_logo,\n" +
                "\ttender_title,\n" +
                "\ttender_explain,\n" +
                "\ttender_display_date,\n" +
                "\ttender_expire_date,\n" +
                "\tIFNULL( tender_company_display_date, '1970-01-01 02:00:00' ),\n" +
                "\tIFNULL( tender_company_expired_date, '1970-01-01 02:00:00' ),\n" +
                "\tcount( DISTINCT request_id ) AS response_count \n" +
                "FROM\n" +
                "\ttakatf_tender AS t\n" +
                "\tLEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id \n" +
                "GROUP BY\n" +
                "\ttender_id\n" +
                "\tLIMIT ?,?;";
        List<TakatafTenderPOJO> tenders = jdbcTemplate.query(sql, new Object[]{limitOffset, pageSize},
                (resultSet, i) -> new TakatafTenderPOJO(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4),
                        resultSet.getTimestamp(5).getTime(), resultSet.getTimestamp(6).getTime(), resultSet.getTimestamp(7).getTime(),
                        resultSet.getTimestamp(8).getTime(), resultSet.getInt(9)));

        return new TakatafTenderPOJOPagination(getTendersPaginationCount(), tenders);

    }

    public TakatafTenderPOJO getTender(int id) {

        String sql = "SELECT tender_id, tender_logo, tender_title, tender_explain, tender_display_date, tender_expire_date , " +
                " IFNULL(tender_company_display_date,'1970-01-01 02:00:00'), IFNULL(tender_company_expired_date,'1970-01-01 02:00:00'), " +
                " count(distinct request_id) AS response_count " +
                "                FROM takatf_tender AS t  " +
                "                LEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id WHERE tender_id=? GROUP BY tender_id; ";
        return jdbcTemplate.queryForObject(sql, new Object[]{id},
                (resultSet, i) -> new TakatafTenderPOJO(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4),
                        resultSet.getTimestamp(5).getTime(), resultSet.getTimestamp(6).getTime(), resultSet.getTimestamp(7).getTime(),
                        resultSet.getTimestamp(8).getTime(), resultSet.getInt(9)));
    }

    public List<TakatafMyTenderPageDTO> getAdminTenders() {
        String sql = "SELECT\n" +
                "\ttender_id,\n" +
                "tender_logo, " +
                "\ttender_title,\n" +
                "\ttender_explain,\n" +
                "\ttender_display_date,\n" +
                "\ttender_expire_date,\n" +
                "\tIFNULL( tender_company_display_date, '1970-01-01 02:00:00' ) AS tender_company_display_date,\n" +
                "\tIFNULL( tender_company_expired_date, '1970-01-01 02:00:00' ) AS tender_company_expired_date,\n" +
                "\tcount( DISTINCT request_id ) AS response_count,\n" +
                "\tcount( DISTINCT ten.t_category_id ) AS cat_count  \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\ttakatf_tender AS t\n" +
                "\t\tLEFT JOIN tkatf_tender_catgory_request AS ten ON t.tender_id = ten.t_tender_id\n" +
                "\t\tLEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id \n" +
                "\t) \n" +
                "WHERE\n" +
                "\ttender_company_expired_date = '1970-01-01 02:00:00' \n" +
                "\tOR tender_company_expired_date >= NOW( ) \n" +
                "\tOR ISNULL( tender_company_expired_date ) \n" +
                "GROUP BY\n" +
                "\ttender_id,\n" +
                "\ttender_title,\n" +
                "\ttender_explain,\n" +
                "\ttender_display_date,\n" +
                "\ttender_expire_date,\n" +
                "\ttender_company_display_date,\n" +
                "\ttender_company_expired_date;";
        return jdbcTemplate.query(sql,
                (resultSet, i) -> new com.taj.model.TakatafMyTenderPageDTO(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4),
                        resultSet.getTimestamp(5).getTime(), resultSet.getTimestamp(6).getTime(), resultSet.getTimestamp(7).getTime()
                        , resultSet.getTimestamp(8).getTime(),
                        resultSet.getInt(9), resultSet.getInt(10)));
    }


    public int getAdminTendersPaginationCount() {
        String sql = "SELECT\n" +
                "\tcount( DISTINCT tender_id ) \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\ttakatf_tender AS t\n" +
                "\t\tLEFT JOIN tkatf_tender_catgory_request AS ten ON t.tender_id = ten.t_tender_id\n" +
                "\t\tLEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id \n" +
                "\t) \n" +
                "WHERE\n" +
                "\ttender_company_expired_date = '1970-01-01 02:00:00' \n" +
                "\tOR tender_company_expired_date >= NOW( ) \n" +
                "\tOR ISNULL( tender_company_expired_date );";

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }


    public TakatafMyTenderPageDTOPagination getAdminTendersPagination(int page, int pageSize) {

        int pages = (int) Math.ceil(((float) getAdminTendersPaginationCount()) / ((float) pageSize));
        System.out.println(getAdminTendersPaginationCount()+"  Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;

        String sql = "SELECT\n" +
                "\ttender_id,\n" +
                "tender_logo, " +
                "\ttender_title,\n" +
                "\ttender_explain,\n" +
                "\ttender_display_date,\n" +
                "\ttender_expire_date,\n" +
                "\tIFNULL( tender_company_display_date, '1970-01-01 02:00:00' ) AS tender_company_display_date,\n" +
                "\tIFNULL( tender_company_expired_date, '1970-01-01 02:00:00' ) AS tender_company_expired_date,\n" +
                "\tcount( DISTINCT request_id ) AS response_count,\n" +
                "\tcount( DISTINCT ten.t_category_id ) AS cat_count  \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\ttakatf_tender AS t\n" +
                "\t\tLEFT JOIN tkatf_tender_catgory_request AS ten ON t.tender_id = ten.t_tender_id\n" +
                "\t\tLEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id \n" +
                "\t) \n" +
                "WHERE " +
                "\ttender_company_expired_date = '1970-01-01 02:00:00' \n" +
                "\tOR tender_company_expired_date >= NOW( ) \n" +
                "\tOR ISNULL( tender_company_expired_date ) \n" +
                "GROUP BY\n" +
                "\ttender_id,\n" +
                "\ttender_title,\n" +
                "\ttender_explain,\n" +
                "\ttender_display_date,\n" +
                "\ttender_expire_date,\n" +
                "\ttender_company_display_date,\n" +
                "\ttender_company_expired_date " +
                " LIMIT ?,?;";
        List<TakatafMyTenderPageDTO> tenders = jdbcTemplate.query(sql, new Object[]{limitOffset, pageSize},
                (resultSet, i) -> new com.taj.model.TakatafMyTenderPageDTO(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4),
                        resultSet.getTimestamp(5).getTime(), resultSet.getTimestamp(6).getTime(), resultSet.getTimestamp(7).getTime()
                        , resultSet.getTimestamp(8).getTime(),
                        resultSet.getInt(9), resultSet.getInt(10)));

        return new TakatafMyTenderPageDTOPagination(getAdminTendersPaginationCount(), pages, tenders);
    }

    public int getAdminTendersPaginationCountSchool() {
        String sql = "SELECT\n" +
                "\tCOUNT( DISTINCT tender_id ) \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\ttakatf_tender AS t\n" +
                "\t\tLEFT JOIN tkatf_tender_catgory_request AS ten ON t.tender_id = ten.t_tender_id\n" +
                "\t\tLEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id \n" +
                "\t) \n" +
                "WHERE\n" +
                " tender_expire_date >= NOW( ) ;";

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }


    public TakatafMyTenderPageDTOPagination getAdminTendersPaginationSchool(int page, int pageSize) {

        int pages = (int) Math.ceil(((float) getAdminTendersPaginationCountSchool()) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;

        String sql = "SELECT\n" +
                "\ttender_id,\n" +
                "tender_logo, " +
                "\ttender_title,\n" +
                "\ttender_explain,\n" +
                "\ttender_display_date,\n" +
                "\ttender_expire_date,\n" +
                "\tIFNULL( tender_company_display_date, '1970-01-01 02:00:00' ) AS tender_company_display_date,\n" +
                "\tIFNULL( tender_company_expired_date, '1970-01-01 02:00:00' ) AS tender_company_expired_date,\n" +
                "\tcount( DISTINCT request_id ) AS response_count,\n" +
                "\tcount( DISTINCT ten.t_category_id ) AS cat_count  \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\ttakatf_tender AS t\n" +
                "\t\tLEFT JOIN tkatf_tender_catgory_request AS ten ON t.tender_id = ten.t_tender_id\n" +
                "\t\tLEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id \n" +
                "\t) \n" +
                "WHERE " +
                " tender_expire_date >= NOW( ) " +
                "GROUP BY\n" +
                "\ttender_id,\n" +
                "\ttender_title,\n" +
                "\ttender_explain,\n" +
                "\ttender_display_date,\n" +
                "\ttender_expire_date,\n" +
                "\ttender_company_display_date,\n" +
                "\ttender_company_expired_date " +
                " LIMIT ?,?;";
        List<TakatafMyTenderPageDTO> tenders = jdbcTemplate.query(sql, new Object[]{limitOffset, pageSize},
                (resultSet, i) -> new com.taj.model.TakatafMyTenderPageDTO(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4),
                        resultSet.getTimestamp(5).getTime(), resultSet.getTimestamp(6).getTime(), resultSet.getTimestamp(7).getTime()
                        , resultSet.getTimestamp(8).getTime(),
                        resultSet.getInt(9), resultSet.getInt(10)));

        return new TakatafMyTenderPageDTOPagination(getAdminTendersPaginationCountSchool(), pages, tenders);
    }



    public List<TakatafMyTenderPageDTO> getAdminTendersHistory() {
        String sql = "SELECT\n" +
                "\ttender_id,\n" +
                "tender_logo," +
                "\ttender_title,\n" +
                "\ttender_explain,\n" +
                "\ttender_display_date,\n" +
                "\ttender_expire_date,\n" +
                "\tIFNULL( tender_company_display_date, '1970-01-01 02:00:00' ) AS tender_company_display_date,\n" +
                "\tIFNULL( tender_company_expired_date, '1970-01-01 02:00:00' ) AS tender_company_expired_date,\n" +
                "\tcount( DISTINCT request_id ) AS response_count,\n" +
                "\tcount( DISTINCT ten.t_category_id ) AS cat_count \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\ttakatf_tender AS t\n" +
                "\t\tLEFT JOIN tkatf_tender_catgory_request AS ten ON t.tender_id = ten.t_tender_id\n" +
                "\t\tLEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id \n" +
                "\t) \n" +
                "WHERE\n" +
                "\ttender_company_expired_date < NOW( ) \n" +
                "\tAND ! ( tender_company_expired_date = '1970-01-01 02:00:00' OR ISNULL(tender_company_expired_date) OR  tender_company_expired_date > NOW( )) \n" +
                "GROUP BY\n" +
                "\ttender_id,\n" +
                "\ttender_title,\n" +
                "\ttender_explain,\n" +
                "\ttender_display_date,\n" +
                "\ttender_expire_date,\n" +
                "\ttender_company_display_date,\n" +
                "\ttender_company_expired_date;";
        return jdbcTemplate.query(sql,
                (resultSet, i) -> new com.taj.model.TakatafMyTenderPageDTO(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4),
                        resultSet.getTimestamp(5).getTime(), resultSet.getTimestamp(6).getTime(), resultSet.getTimestamp(7).getTime()
                        , resultSet.getTimestamp(8).getTime(),
                        resultSet.getInt(9), resultSet.getInt(10)));
    }


    private int getAdminTendersHistoryPaginationCount() {
        String sql = "SELECT\n" +
                "\tCOUNT( DISTINCT tender_id ) \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\ttakatf_tender AS t\n" +
                "\t\tLEFT JOIN tkatf_tender_catgory_request AS ten ON t.tender_id = ten.t_tender_id\n" +
                "\t\tLEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id \n" +
                "\t) \n" +
                "WHERE\n" +
                "\ttender_company_expired_date < NOW( ) AND ! ( tender_company_expired_date = '1970-01-01 02:00:00' OR ISNULL( tender_company_expired_date ) OR tender_company_expired_date > NOW( ) \n" +
                "\t);";

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }


    public TakatafMyTenderPageDTOPagination getAdminTendersHistoryPagination(int page, int pageSize) {
        int pages = (int) Math.ceil(((float) getAdminTendersHistoryPaginationCount()) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;

        String sql = "SELECT\n" +
                "\ttender_id,\n" +
                "tender_logo," +
                "\ttender_title,\n" +
                "\ttender_explain,\n" +
                "\ttender_display_date,\n" +
                "\ttender_expire_date,\n" +
                "\tIFNULL( tender_company_display_date, '1970-01-01 02:00:00' ) AS tender_company_display_date,\n" +
                "\tIFNULL( tender_company_expired_date, '1970-01-01 02:00:00' ) AS tender_company_expired_date,\n" +
                "\tcount( DISTINCT request_id ) AS response_count,\n" +
                "\tcount( DISTINCT ten.t_category_id ) AS cat_count \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\ttakatf_tender AS t\n" +
                "\t\tLEFT JOIN tkatf_tender_catgory_request AS ten ON t.tender_id = ten.t_tender_id\n" +
                "\t\tLEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id \n" +
                "\t) \n" +
                "WHERE\n" +
                "\ttender_company_expired_date < NOW( ) \n" +
                "\tAND ! ( tender_company_expired_date = '1970-01-01 02:00:00' OR ISNULL(tender_company_expired_date) OR  tender_company_expired_date > NOW( )) \n" +
                "GROUP BY\n" +
                "\ttender_id,\n" +
                "\ttender_title,\n" +
                "\ttender_explain,\n" +
                "\ttender_display_date,\n" +
                "\ttender_expire_date,\n" +
                "\ttender_company_display_date,\n" +
                "\ttender_company_expired_date " +
                " LIMIT ?,?;";
        List<TakatafMyTenderPageDTO> historyTenders = jdbcTemplate.query(sql, new Object[]{limitOffset, pageSize},
                (resultSet, i) -> new com.taj.model.TakatafMyTenderPageDTO(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4),
                        resultSet.getTimestamp(5).getTime(), resultSet.getTimestamp(6).getTime(), resultSet.getTimestamp(7).getTime()
                        , resultSet.getTimestamp(8).getTime(),
                        resultSet.getInt(9), resultSet.getInt(10)));
        return new TakatafMyTenderPageDTOPagination(getAdminTendersHistoryPaginationCount(), pages, historyTenders);
    }


    public int updateTender(int tender_id, String tender_logo,
                            String tender_title,
                            String tender_explain,
                            long tender_display_date, long tender_expire_date,
                            long tender_company_display_date, long tender_company_expired_date,
                            List<TakatfTenderCategoryPOJO> cats, int flag_ar) {

        jdbcTemplate.update("DELETE FROM tkatf_tender_catgory_request WHERE t_tender_id= ?;", tender_id);

        if (flag_ar == 0) {
            for (int i = 0; i < cats.size(); i++) {
                int categorys = jdbcTemplate.queryForObject("SELECT category_id  FROM  efaz_company_category WHERE  category_name LIKE ?;",
                        Integer.class, "%" + cats.get(i).getCategory_name().trim() + "%");
                String sql = "SELECT\n" +
                        "\tcount( * ) \n" +
                        "FROM\n" +
                        "\ttakatf_tender AS tend\n" +
                        "\tINNER JOIN tkatf_tender_catgory_request AS cat ON tend.tender_id = cat.t_tender_id \n" +
                        "WHERE\n" +
                        " tend.tender_expire_date >= now( ) " +
                        "\tAND cat.t_category_id = ?;";
                int num = jdbcTemplate.queryForObject(sql, Integer.class, categorys);
                if (num > 0) {
                    return -100;
                }
            }
        }else {

            for (int i = 0; i < cats.size(); i++) {
                int categorys = jdbcTemplate.queryForObject("SELECT category_id  FROM  efaz_company_category WHERE  category_name_ar LIKE ?;",
                        Integer.class, "%" + cats.get(i).getCategory_name().trim() + "%");
                String sql = "SELECT\n" +
                        "\tcount( * ) \n" +
                        "FROM\n" +
                        "\ttakatf_tender AS tend\n" +
                        "\tINNER JOIN tkatf_tender_catgory_request AS cat ON tend.tender_id = cat.t_tender_id \n" +
                        "WHERE\n" +
                        " tend.tender_expire_date >= now( ) " +
                        "\tAND cat.t_category_id = ?;";
                int num = jdbcTemplate.queryForObject(sql, Integer.class, categorys);
                if (num > 0) {
                    return -100;
                }
            }

        }


        if (cats != null) {
            if (isExistTenders(tender_id)) {
                int x = delete(tender_id);
            }
            if (flag_ar == 0){
                for (int i = 0; i < cats.size(); i++) {
                    int categorys = jdbcTemplate.queryForObject("SELECT category_id  FROM  efaz_company_category WHERE  category_name LIKE ?;",
                            Integer.class, "%" + cats.get(i).getCategory_name().trim() + "%");
                    System.out.println(categorys);

                    jdbcTemplate.update("INSERT INTO tkatf_tender_catgory_request VALUES  (?,?,?)", null, tender_id, categorys);
                }
            }else {
                for (int i = 0; i < cats.size(); i++) {
                    int categorys = jdbcTemplate.queryForObject("SELECT category_id  FROM  efaz_company_category WHERE  category_name_ar LIKE ?;",
                            Integer.class, "%" + cats.get(i).getCategory_name().trim() + "%");
                    System.out.println(categorys);

                    jdbcTemplate.update("INSERT INTO tkatf_tender_catgory_request VALUES  (?,?,?)", null, tender_id, categorys);
                }
            }


        }

        int ten = jdbcTemplate.update("UPDATE takatf_tender SET tender_logo=?, tender_title=?, tender_explain=?," +
                        " tender_display_date=?, tender_expire_date=?, tender_is_confirmed=?, tender_is_available=?, tender_company_expired_date=?," +
                        " tender_company_display_date=? WHERE tender_id=?", tender_logo, tender_title, tender_explain,
                new Timestamp(tender_display_date), new Timestamp(tender_expire_date), 0, 1,
                new Timestamp(tender_company_expired_date), new Timestamp(tender_company_display_date), tender_id);
        System.out.println(cats.size());


        return ten;

    }

    public int delete(int tender_id) {
        return jdbcTemplate.update("Delete from tkatf_tender_catgory_request where t_tender_id = ?;", tender_id);
    }

    public boolean isExistTenders(int id) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM tkatf_tender_catgory_request WHERE t_tender_id=?;",
                Integer.class, id);
        return cnt != null && cnt > 0;
    }

    public int deleteTenderWithItsResponse(int id) {
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=0;");
        jdbcTemplate.update("DELETE FROM takatf_tender WHERE tender_id = ?;", id);
        int x = delete(id);
        jdbcTemplate.update("Delete FROM takataf_request_cat_count WHERE tend_id=?;", id);
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=1;");

        return 0;
    }

    public int getCategoryId(String name, int flag_ar) {
        if (flag_ar == 0) {
            int categorys = jdbcTemplate.queryForObject("SELECT category_id  FROM  efaz_company_category WHERE  category_name LIKE ?;",
                    Integer.class, "%" + name.trim() + "%");
            return categorys;
        }else {
            int categorys = jdbcTemplate.queryForObject("SELECT category_id  FROM  efaz_company_category WHERE  category_name_ar LIKE ?;",
                    Integer.class, "%" + name.trim() + "%");
            return categorys;
        }
    }

//    public TakatafTenderWithCompanies getSingleTenderDetails(int id) {
//
//
//        String sql = "SELECT tender_id, tender_logo, tender_title, tender_explain, category_name, tender_display_date, tender_expire_date ," +
//                " tender_company_display_date, tender_company_expired_date, " +
//                "count(distinct request_id) AS response_count,school_name, school_logo_image,school_service_desc" +
//                "                FROM takatf_tender AS t INNER JOIN efaz_company_category as cat ON t.tender_cat_id = cat.category_id" +
//                "                LEFT JOIN takatf_request_tender AS req ON t.tender_id = req.request_tender_id " +
//                "                LEFT JOIN efaz_school_profile AS res ON req.request_school_id = res.school_id where t.tender_id=? GROUP BY tender_id;";
//
//
//        return jdbcTemplate.queryForObject(sql,
//                new Object[]{id}, (resultSet, i) -> new TakatafTenderWithCompanies(resultSet.getInt(1), resultSet.getBytes(2),
//                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
//                        resultSet.getTimestamp(6).getTime(), resultSet.getTimestamp(7).getTime(),
//                        resultSet.getTimestamp(8).getTime(), resultSet.getTimestamp(9).getTime(),resultSet.getInt(10),
//                        resultSet.getString(11), resultSet.getBytes(12), resultSet.getString(13)));
//
//
//    }
//
//    public int acceptOffer(int request_id){
//        return jdbcTemplate.update("UPDATE takatf_request_tender SET is_aproved=1 WHERE request_id=?;", request_id);
//    }
//    public int cancelOffer(int request_id){
//        return jdbcTemplate.update("UPDATE takatf_request_tender SET is_aproved=0 WHERE request_id=?;", request_id);
//    }
//
//
//
//
//    public int updateRequest(int tender_id, byte[] tender_logo, String tender_title, String tender_explain, long tender_display_date, long tender_expire_date,
//                             long tender_company_display_date, long tender_company_expired_date, String tender_cat_id) {
//
//
//
//        int category = jdbcTemplate.queryForObject("SELECT category_id  FROM  efaz_company_category WHERE  category_name LIKE ?;",
//                Integer.class, "%" + tender_cat_id + "%");
//
//        return jdbcTemplate.update("update takatf_tender set tender_logo=?," + " tender_title=?, tender_explain=?," +
//                        "tender_display_date=?," + " tender_display_date=?, tender_is_confirmed=?, tender_is_available=?, " +
//                        "tender_company_display_date=?, tender_company_expired_date=?, " +
//                        "tender_cat_id=? "+
//                        " where tender_id=?;", tender_logo,  tender_title, tender_explain, new Timestamp(tender_display_date)
//                , new Timestamp(tender_expire_date), 0, 1, new Timestamp(tender_company_display_date), new Timestamp(tender_company_expired_date),category, tender_id);
//
//    }
//
//    public int deleteSchoolRequest(int id) {
//        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=0;");
//        int res = jdbcTemplate.update("DELETE FROM takatf_tender WHERE tender_id=?", id);
//        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=1;");
//        return res;
//    }
//
//
//

    public boolean isExist(int id) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM takatf_tender WHERE tender_id=?;",
                Integer.class, id);
        return cnt != null && cnt > 0;
    }


}
