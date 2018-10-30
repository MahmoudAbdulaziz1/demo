package com.taj.repository;

import com.taj.model.NewModelDto;
import com.taj.model.NewRegisterModel;
import com.taj.model.Pagination.NewRegisterModelPagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Repository;

import javax.mail.internet.MimeMessage;
import java.sql.*;
import java.util.List;

/**
 * Created by User on 9/10/2018.
 */
@Repository
public class NewRegisterRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private JavaMailSender sender;


    public int addUser(String email, String password, String userName, String phoneNumber, String companyName
            , String address, String website, String registrationRole, long registerationDate, String city, String area
            , float lng, float lat) {

        if (IfOrgNameExist(companyName)) {
            return -100;
        }

        KeyHolder holder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection connection)
                    throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO efaz_registration VALUES (?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, null);
                ps.setString(2, email);
                ps.setString(3, password);
                ps.setString(4, userName);
                ps.setString(5, phoneNumber);
                ps.setString(6, companyName);
                ps.setString(7, address);
                ps.setString(8, website);
                ps.setInt(9, 0);
                ps.setString(10, registrationRole);
                ps.setTimestamp(11, new Timestamp(registerationDate));

                return ps;
            }
        }, holder);

        int id = holder.getKey().intValue();

        return jdbcTemplate.update("INSERT INTO  complete_register_data VALUES (?,?,?,?,?,?,?)", id, city, area, 0, 0, lng, lat);
//        String sql = "SELECT " +
//                "\tregistration_id, " +
//                "\tregisteration_email, " +
//                "\tregisteration_password, " +
//                "\tregisteration_username, " +
//                "\tregisteration_phone_number, " +
//                "\tregistration_organization_name, " +
//                "\tregistration_address_desc, " +
//                "\tregistration_website_url, " +
//                "\tregistration_isActive, " +
//                "\tregistration_role, " +
//                "\tregisteration_date, " +
//                "\tcity, " +
//                "\tarea, " +
//                "\tarchive, " +
//                "\tconsider  " +
//                "FROM " +
//                " efaz_registration AS org" +
//                " LEFT JOIN complete_register_data AS DATA ON org.registration_id = DATA.id  " +
//                "WHERE " +
//                " registration_id = ?";
//        return jdbcTemplate.queryForObject(sql, new Object[]{id},
//                ((resultSet, i) -> new NewRegisterModel(resultSet.getInt(1), resultSet.getString(2),
//                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
//                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10),
//                        resultSet.getTimestamp(11).getTime(), resultSet.getString(12), resultSet.getString(13), resultSet.getInt(14), resultSet.getInt(15))));


    }


    public boolean checkIfEmailExist(String email, String role) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM efaz_registration WHERE registeration_email=? AND registration_role=?;",
                Integer.class, email, role);
        return cnt != null && cnt > 0;
    }


    public boolean IfEmailExist(int id) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM efaz_registration WHERE registration_id=?;",
                Integer.class, id);
        return cnt != null && cnt > 0;
    }


    public boolean IfOrgNameExist(String id) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM efaz_registration WHERE registration_organization_name=?;",
                Integer.class, id);
        return cnt != null && cnt > 0;
    }


    public int getInActiveCompaniesPaginationCount() {
        String sql = "SELECT\n" +
                "\tCOUNT(DISTINCT registration_id) \n" +
                "FROM\n" +
                "\tefaz_registration AS org\n" +
                "\tLEFT JOIN complete_register_data AS dta ON org.registration_id = dta.id \n" +
                "WHERE\n" +
                "\tregistration_isActive = 0 \n" +
                "\tAND archive = 0 \n" +
                "\tAND consider = 0;";

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }


    public NewRegisterModelPagination getInActiveCompaniesPagination(int page, int pageSize) {
        int pages = (int) Math.ceil(((float) getInActiveCompaniesPaginationCount()) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;
        String sql = " SELECT " +
                " registration_id, " +
                " registeration_email, " +
                " registeration_password, " +
                " registeration_username, " +
                " registeration_phone_number, " +
                " registration_organization_name, " +
                " registration_address_desc, " +
                " registration_website_url, " +
                " registration_isActive, " +
                " registration_role, " +
                " registeration_date, " +
                " city, " +
                " area, " +
                " archive, " +
                " consider ," +
                " lng," +
                " lat " +
                " FROM " +
                " efaz_registration AS org " +
                " LEFT JOIN complete_register_data AS dta ON org.registration_id = dta.id  " +
                " WHERE " +
                " registration_isActive = 0   " +
                " AND archive = 0  " +
                " AND consider = 0 " +
                " LIMIT ?,?;";

        List<NewRegisterModel> userData = jdbcTemplate.query(sql, new Object[]{limitOffset, pageSize},
                (resultSet, i) -> new NewRegisterModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10),
                        resultSet.getTimestamp(11).getTime(), resultSet.getString(12), resultSet.getString(13),
                        resultSet.getInt(14), resultSet.getInt(15), resultSet.getFloat(16), resultSet.getFloat(17)));
        return new NewRegisterModelPagination(getInActiveCompaniesPaginationCount(), pages, userData);

    }


    public List<NewRegisterModel> getInActiveCompanies() {
        String sql = " SELECT " +
                " registration_id, " +
                " registeration_email, " +
                " registeration_password, " +
                " registeration_username, " +
                " registeration_phone_number, " +
                " registration_organization_name, " +
                " registration_address_desc, " +
                " registration_website_url, " +
                " registration_isActive, " +
                " registration_role, " +
                " registeration_date, " +
                " city, " +
                " area, " +
                " archive, " +
                " consider ," +
                " lng," +
                " lat " +
                " FROM " +
                " efaz_registration AS org " +
                " LEFT JOIN complete_register_data AS dta ON org.registration_id = dta.id  " +
                " WHERE " +
                " registration_isActive = 0   " +
                " AND archive = 0  " +
                " AND consider = 0;";

        return jdbcTemplate.query(sql,
                (resultSet, i) -> new NewRegisterModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10),
                        resultSet.getTimestamp(11).getTime(), resultSet.getString(12), resultSet.getString(13),
                        resultSet.getInt(14), resultSet.getInt(15), resultSet.getFloat(16), resultSet.getFloat(17)));


    }

    public int archiveCompanyRequest(int id) {
        String sql = "UPDATE complete_register_data  " +
                " SET archive = 1,  consider=0 " +
                " WHERE " +
                " id = ?;";
        return jdbcTemplate.update(sql, id);
    }

    public int unArchiveCompanyRequest(int id) {
        String sql = "UPDATE complete_register_data  " +
                " SET archive = 0 " +
                " WHERE " +
                " id = ?;";
        return jdbcTemplate.update(sql, id);
    }


    public int considrateCompanyRequest(int id) {
        String sql = "UPDATE complete_register_data  " +
                " SET consider = 1, archive=0 " +
                " WHERE " +
                " id = ?;";
        return jdbcTemplate.update(sql, id);
    }

    public int unCosidrateCompanyRequest(int id) {
        String sql = "UPDATE complete_register_data  " +
                " SET consider = 0 " +
                " WHERE " +
                " id = ?;";
        return jdbcTemplate.update(sql, id);
    }

    public int getInActiveCompaniesArchivedPaginationCount() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT COUNT( DISTINCT registration_id ) AS count ");
        sqlBuilder.append(" FROM efaz_registration AS org ");
        sqlBuilder.append(" LEFT JOIN complete_register_data AS dta ON org.registration_id = dta.id  ");
        sqlBuilder.append(" WHERE registration_isActive = 0 AND archive = 1;");
        String sql = sqlBuilder.toString();
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public NewRegisterModelPagination getInActiveCompaniesArchived(int page, int pageSize) {
        int pages = (int) Math.ceil(((float) getInActiveCompaniesArchivedPaginationCount()) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;


        String sql = " SELECT  " +
                "\tregistration_id,\n" +
                "\tregisteration_email,\n" +
                "\tregisteration_password,\n" +
                "\tregisteration_username,\n" +
                "\tregisteration_phone_number,\n" +
                "\tregistration_organization_name,\n" +
                "\tregistration_address_desc,\n" +
                "\tregistration_website_url,\n" +
                "\tregistration_isActive,\n" +
                "\tregistration_role,\n" +
                "\tregisteration_date,\n" +
                "\tcity,\n" +
                "\tarea,\n" +
                "\tarchive,\n" +
                "\tconsider ," +
                " lng, lat " +
                "FROM\n" +
                "\tefaz_registration AS org\n" +
                "\tLEFT JOIN complete_register_data AS dta ON org.registration_id = dta.id \n" +
                "WHERE\n" +
                "\tregistration_isActive = 0 \n" +
                "\tAND archive = 1" +
                " LIMIT ?,?;";

        List<NewRegisterModel> userData = jdbcTemplate.query(sql, new Object[]{limitOffset, pageSize},
                (resultSet, i) -> new NewRegisterModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10),
                        resultSet.getTimestamp(11).getTime(), resultSet.getString(12), resultSet.getString(13),
                        resultSet.getInt(14), resultSet.getInt(15), resultSet.getFloat(16), resultSet.getFloat(17)));

        return new NewRegisterModelPagination(getInActiveCompaniesArchivedPaginationCount(), pages, userData);

    }


    public List<NewRegisterModel> getInActiveCompaniesArchived() {
        String sql = " SELECT  " +
                "\tregistration_id,\n" +
                "\tregisteration_email,\n" +
                "\tregisteration_password,\n" +
                "\tregisteration_username,\n" +
                "\tregisteration_phone_number,\n" +
                "\tregistration_organization_name,\n" +
                "\tregistration_address_desc,\n" +
                "\tregistration_website_url,\n" +
                "\tregistration_isActive,\n" +
                "\tregistration_role,\n" +
                "\tregisteration_date,\n" +
                "\tcity,\n" +
                "\tarea,\n" +
                "\tarchive,\n" +
                "\tconsider ," +
                " lng, lat " +
                "FROM\n" +
                "\tefaz_registration AS org\n" +
                "\tLEFT JOIN complete_register_data AS dta ON org.registration_id = dta.id \n" +
                "WHERE\n" +
                "\tregistration_isActive = 0 \n" +
                "\tAND archive = 1;";

        return jdbcTemplate.query(sql,
                (resultSet, i) -> new NewRegisterModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10),
                        resultSet.getTimestamp(11).getTime(), resultSet.getString(12), resultSet.getString(13),
                        resultSet.getInt(14), resultSet.getInt(15), resultSet.getFloat(16), resultSet.getFloat(17)));


    }


    public List<NewModelDto> getUsers() {


        String sql = " SELECT  " +
                "\tregistration_id,\n" +
                "\tregisteration_email,\n" +
                "\tregisteration_password,\n" +
                "\tregisteration_username,\n" +
                "\tregisteration_phone_number,\n" +
                "\tregistration_organization_name,\n" +
                "\tregistration_address_desc,\n" +
                "\tregistration_website_url,\n" +
                "\tregistration_isActive,\n" +
                "\tregistration_role,\n" +
                "\tregisteration_date,\n" +
                "\tcity,\n" +
                "\tarea,\n" +
                "\tarchive,\n" +
                "\tconsider ," +
                " lng, lat " +
                "FROM\n" +
                "\tefaz_registration AS org\n" +
                "\tLEFT JOIN complete_register_data AS dta ON org.registration_id = dta.id ;";


        return jdbcTemplate.query(sql,
                (resultSet, i) -> new NewModelDto(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10),
                        resultSet.getTimestamp(11).getTime(), resultSet.getString(12), resultSet.getString(13),
                        resultSet.getFloat(16), resultSet.getFloat(17)));
    }


    public int getInActiveCompaniesConsideratePaginationCount() {
        String sql = "SELECT\n" +
                "\tCOUNT( DISTINCT registration_id ) \n" +
                "FROM\n" +
                "\tefaz_registration AS org\n" +
                "\tLEFT JOIN complete_register_data AS dta ON org.registration_id = dta.id \n" +
                "WHERE\n" +
                "\tregistration_isActive = 0 \n" +
                "\tAND consider = 1;";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }


    public NewRegisterModelPagination getInActiveCompaniesConsideratePaginations(int page, int pageSize) {
        int pages = (int) Math.ceil(((float) getInActiveCompaniesConsideratePaginationCount()) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;
        String sql = "SELECT " +
                " registration_id," +
                "registeration_email, " +
                "registeration_password, " +
                "registeration_username, " +
                "registeration_phone_number, " +
                "registration_organization_name, " +
                "registration_address_desc, " +
                "registration_website_url, " +
                "registration_isActive, " +
                "registration_role, " +
                "registeration_date, " +
                "city, " +
                "area, " +
                "archive, " +
                "consider ," +
                " lng, lat " +
                "FROM " +
                "efaz_registration AS org " +
                "LEFT JOIN complete_register_data AS dta ON org.registration_id = dta.id  " +
                "WHERE " +
                "registration_isActive = 0  " +
                "AND consider = 1 " +
                " LIMIT ?,?;";

        List<NewRegisterModel> userData = jdbcTemplate.query(sql, new Object[]{limitOffset, pageSize},
                (resultSet, i) -> new NewRegisterModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10),
                        resultSet.getTimestamp(11).getTime(), resultSet.getString(12), resultSet.getString(13),
                        resultSet.getInt(14), resultSet.getInt(15), resultSet.getFloat(16), resultSet.getFloat(17)));

        return new NewRegisterModelPagination(getInActiveCompaniesConsideratePaginationCount(), pages, userData);
    }


    public List<NewRegisterModel> getInActiveCompaniesConsideratePagination() {
        String sql = "SELECT " +
                " registration_id," +
                "registeration_email, " +
                "registeration_password, " +
                "registeration_username, " +
                "registeration_phone_number, " +
                "registration_organization_name, " +
                "registration_address_desc, " +
                "registration_website_url, " +
                "registration_isActive, " +
                "registration_role, " +
                "registeration_date, " +
                "city, " +
                "area, " +
                "archive, " +
                "consider ," +
                " lng, lat " +
                "FROM " +
                "efaz_registration AS org " +
                "LEFT JOIN complete_register_data AS dta ON org.registration_id = dta.id  " +
                "WHERE " +
                "registration_isActive = 0  " +
                "AND consider = 1;";

        return jdbcTemplate.query(sql,
                (resultSet, i) -> new NewRegisterModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10),
                        resultSet.getTimestamp(11).getTime(), resultSet.getString(12), resultSet.getString(13),
                        resultSet.getInt(14), resultSet.getInt(15), resultSet.getFloat(16), resultSet.getFloat(17)));


    }


    public List<NewRegisterModel> getInActiveCompaniesConsiderate() {
        String sql = "SELECT " +
                " registration_id," +
                "registeration_email, " +
                "registeration_password, " +
                "registeration_username, " +
                "registeration_phone_number, " +
                "registration_organization_name, " +
                "registration_address_desc, " +
                "registration_website_url, " +
                "registration_isActive, " +
                "registration_role, " +
                "registeration_date, " +
                "city, " +
                "area, " +
                "archive, " +
                "consider ," +
                " lng, lat " +
                "FROM " +
                "efaz_registration AS org " +
                "LEFT JOIN complete_register_data AS dta ON org.registration_id = dta.id  " +
                "WHERE " +
                "registration_isActive = 0  " +
                "AND consider = 1;";

        return jdbcTemplate.query(sql,
                (resultSet, i) -> new NewRegisterModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10),
                        resultSet.getTimestamp(11).getTime(), resultSet.getString(12), resultSet.getString(13),
                        resultSet.getInt(14), resultSet.getInt(15), resultSet.getFloat(16), resultSet.getFloat(17)));


    }


    public List<NewRegisterModel> getInActiveCompaniesBoth() {
        String sql = "SELECT\n" +
                "\tregistration_id,\n" +
                "\tregisteration_email,\n" +
                "\tregisteration_password,\n" +
                "\tregisteration_username,\n" +
                "\tregisteration_phone_number,\n" +
                "\tregistration_organization_name,\n" +
                "\tregistration_address_desc,\n" +
                "\tregistration_website_url,\n" +
                "\tregistration_isActive,\n" +
                "\tregistration_role,\n" +
                "\tregisteration_date,\n" +
                "\tcity,\n" +
                "\tarea,\n" +
                "\tarchive,\n" +
                "\tconsider ," +
                " lng, lat" +
                "FROM\n" +
                "\tefaz_registration AS org\n" +
                "\tLEFT JOIN complete_register_data AS dta ON org.registration_id = dta.id \n" +
                "WHERE\n" +
                "\tregistration_isActive = 0 \n" +
                "\tAND archive = 1 \n" +
                "\tAND consider = 1;";

        return jdbcTemplate.query(sql,
                (resultSet, i) -> new NewRegisterModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10),
                        resultSet.getTimestamp(11).getTime(), resultSet.getString(12), resultSet.getString(13),
                        resultSet.getInt(14), resultSet.getInt(15), resultSet.getFloat(16), resultSet.getFloat(17)));


    }

    public NewRegisterModel getUser(int id) {

        String sql = "SELECT\n" +
                "\tregistration_id,\n" +
                "\tregisteration_email,\n" +
                "\tregisteration_password,\n" +
                "\tregisteration_username,\n" +
                "\tregisteration_phone_number,\n" +
                "\tregistration_organization_name,\n" +
                "\tregistration_address_desc,\n" +
                "\tregistration_website_url,\n" +
                "\tregistration_isActive,\n" +
                "\tregistration_role,\n" +
                "\tregisteration_date,\n" +
                "\tcity,\n" +
                "\tarea,\n" +
                "\tarchive,\n" +
                "\tconsider ," +
                " lng, lat " +
                "FROM\n" +
                "\tefaz_registration AS org " +
                "\tLEFT JOIN complete_register_data AS dta ON org.registration_id = dta.id " +
                " WHERE\n" +
                " registration_id=?; ";

        return jdbcTemplate.queryForObject(sql, new Object[]{id},
                ((resultSet, i) -> new NewRegisterModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10),
                        resultSet.getTimestamp(11).getTime(), resultSet.getString(12), resultSet.getString(13),
                        resultSet.getInt(14), resultSet.getInt(15), resultSet.getFloat(16), resultSet.getFloat(17))));
    }


    public int confirmEmail(int id) {
        NewRegisterModel model = getUser(id);
        if (isExistILogin(model.getRegisterationEmail(), model.getRegistrationRole())) {
            return -100;
        } else {
            jdbcTemplate.update("update efaz_registration set registration_isActive=1 WHERE registration_id=?", id);
            try {
                sendEmail(model.getRegisterationEmail(), id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jdbcTemplate.update("INSERT INTO efaz_login VALUES (?,?,?,?,?,?,?,?,?,?,?)", null, model.getRegisterationEmail(),
                    model.getRegisterationPassword(), 0, model.getRegistrationRole(), "Token=", new Timestamp(System.currentTimeMillis())
                    , model.getCity(), model.getArea(), model.getLng(), model.getLat());
        }
    }


    public boolean isExistILogin(String email, String login_role) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM efaz_login WHERE user_email=? AND login_role=?;",
                Integer.class, email, login_role);
        return cnt != null && cnt > 0;
    }

    /**
     * @param email
     * @param createdId
     * @throws Exception
     */
    private void sendEmail(String email, int createdId) throws Exception {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(email);
        helper.setText("Just Few Moments to complete Regsiter");
        helper.setText(
                "<table>\n" +
                        "    <tr>" +
                        "        <td style=\"background-color: #4ecdc4;border-color: #4c5764;border: 2px solid #45b7af;padding: 10px;text-align: center;\">" +
                        "            <P style=\"display: block;color: #ffffff;font-size: 12px;text-decoration: none;text-transform: uppercase;\">" +
                        "                Your Email has been Confirmed" +
                        "            </p>" +
                        "        </td>" +
                        "    </tr>" +
                        "</table>"
                , true);
        helper.setSubject("Complete Registration with id " + createdId);
        sender.send(message);
    }


}
