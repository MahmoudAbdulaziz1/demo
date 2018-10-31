package com.taj.repository;

import com.taj.model.CollectiveTenderCompaniesRequestForCompanyModel;
import com.taj.model.Pagination.CollectiveTenderCompaniesRequestForCompanyModelPagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by User on 9/3/2018.
 */
@Repository
public class CollectiveTenderCompaniesRequestForCompanyRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int getTenderCompaniesPaginationCount(int id) {
        String sql = "SELECT\n" +
                "\tCOUNT( DISTINCT response_id ) \n" +
                "FROM\n" +
                "\ttakataf_company_request_tender AS req\n" +
                "\tLEFT JOIN efaz_company_profile AS PROFILE ON req.response_takataf_company_id = PROFILE.company_id \n" +
                "WHERE\n" +
                "\tresponse_takataf_request_id = ?;";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }




    public CollectiveTenderCompaniesRequestForCompanyModelPagination getTenderCompaniesPagination(int id, int page, int pageSize) {

        int pages = (int) Math.ceil(((float) getTenderCompaniesPaginationCount(id)) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;

        String sql = "SELECT\n" +
                "\tresponse_id,\n" +
                "\tresponsed_cost,\n" +
                "\tcompany_id,\n" +
                "\tcompany_name,\n" +
                "\tcompany_desc,\n" +
                "\tcompany_logo_image,\n" +
                "\tresponse_date,\n" +
                "\tresponse_desc,\n" +
                "\tis_aproved \n" +
                "FROM\n" +
                "\ttakataf_company_request_tender AS req\n" +
                "\tLEFT JOIN efaz_company_profile AS PROFILE ON req.response_takataf_company_id = PROFILE.company_id \n" +
                "WHERE\n" +
                "\tresponse_takataf_request_id = ? " +
                " LIMIT ?,?;";
        List<CollectiveTenderCompaniesRequestForCompanyModel> tenders =  jdbcTemplate.query(sql, new Object[]{id, limitOffset, pageSize},
                (resultSet, i) -> new CollectiveTenderCompaniesRequestForCompanyModel(resultSet.getInt(1), resultSet.getDouble(2),
                        resultSet.getInt(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getTimestamp(7).getTime(), resultSet.getString(8), resultSet.getInt(9)));

        return new CollectiveTenderCompaniesRequestForCompanyModelPagination(getTenderCompaniesPaginationCount(id), pages, tenders);
    }




    public List<CollectiveTenderCompaniesRequestForCompanyModel> getTenderCompanies(int id) {
        String sql = "SELECT\n" +
                "\tresponse_id,\n" +
                "\tresponsed_cost,\n" +
                "\tcompany_id,\n" +
                "\tcompany_name,\n" +
                "\tcompany_desc,\n" +
                "\tcompany_logo_image,\n" +
                "\tresponse_date,\n" +
                "\tresponse_desc,\n" +
                "\tis_aproved \n" +
                "FROM\n" +
                "\ttakataf_company_request_tender AS req\n" +
                "\tLEFT JOIN efaz_company_profile AS PROFILE ON req.response_takataf_company_id = PROFILE.company_id \n" +
                "WHERE\n" +
                "\tresponse_takataf_request_id = ?;";
        return jdbcTemplate.query(sql, new Object[]{id},
                (resultSet, i) -> new CollectiveTenderCompaniesRequestForCompanyModel(resultSet.getInt(1), resultSet.getDouble(2),
                        resultSet.getInt(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getTimestamp(7).getTime(), resultSet.getString(8), resultSet.getInt(9)));
    }

    public int approveRequest(int response_takataf_company_id, int response_takataf_request_id) {
        return jdbcTemplate.update("UPDATE takataf_company_request_tender SET is_aproved=1 WHERE response_takataf_company_id=?" +
                " AND response_takataf_request_id=?", response_takataf_company_id, response_takataf_request_id);
    }

    public int cancelRequest(int response_takataf_company_id, int response_takataf_request_id) {
        return jdbcTemplate.update("UPDATE takataf_company_request_tender SET is_aproved=0 WHERE response_takataf_company_id=?" +
                " AND response_takataf_request_id=?", response_takataf_company_id, response_takataf_request_id);
    }
}
