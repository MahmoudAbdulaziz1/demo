package com.taj.repository;

import com.taj.model.RegisterationModel2;
import com.taj.model.RegistrationModel;
import com.taj.security.JwtGenerator;
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
 * Created by MahmoudAhmed on 5/30/2018.
 */
@Repository
public class RegistrationRepo {


    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    LoginRepo repo;
    @Autowired
    private JavaMailSender sender;

    @Autowired
    JwtGenerator generator;
    @Autowired
    LoginRepo loginRepo;
//    RegistrationModel model;


    public boolean checkIfEmailExist(String email, String role){
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM efaz_registration WHERE registeration_email=? AND registration_role=?;",
                Integer.class, email, role);
        return cnt != null && cnt > 0;
    }


    public RegistrationModel addUser(String email, String password, String userName, String phoneNumber, String companyName
            , String address, String website, int isActive, String registration_role, long registeration_date) {

//        return jdbcTemplate.update("INSERT INTO efaz_registration VALUES (?,?,?,?,?,?,?,?,?,?,?)", null, email, password, userName, phoneNumber
//                , companyName, address, website, isSchool, isActive, registration_role);

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
                ps.setString(10, registration_role);
                ps.setTimestamp(11, new Timestamp(registeration_date));
                return ps;
            }
        }, holder);

        int id =  holder.getKey().intValue();

        return jdbcTemplate.queryForObject("SELECT * From efaz_registration WHERE registration_id=?", new Object[]{id},
                ((resultSet, i) -> new RegistrationModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10), resultSet.getTimestamp(11).getTime())));





    }




    public List<RegistrationModel> getUsers() {
        return jdbcTemplate.query("select * from efaz_registration;",
                (resultSet, i) -> new RegistrationModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10) , resultSet.getTimestamp(11).getTime()));
    }

    public RegistrationModel getUser(int id) {
        return jdbcTemplate.queryForObject("SELECT * From efaz_registration WHERE registration_id=?", new Object[]{id},
                ((resultSet, i) -> new RegistrationModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10), resultSet.getTimestamp(11).getTime())));
    }

    public RegisterationModel2 getUser2(int id) {
        String sql = "SELECT \n" +
                "    *\n" +
                "FROM\n" +
                "    efaz_registration AS r\n" +
                "        LEFT JOIN\n" +
                "    complete_register_data AS d ON r.registration_id = d.id  WHERE registration_id=?;";
        return jdbcTemplate.queryForObject(sql, new Object[]{id},
                ((resultSet, i) -> new RegisterationModel2(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10),
                        resultSet.getTimestamp(11).getTime(), resultSet.getString(13), resultSet.getString(14),
                        resultSet.getFloat(17), resultSet.getFloat(18))));
    }

    public int updateUser(int id, String email, String password, String userName, String phoneNumber, String companyName
            , String address, String website, int isActive, String registration_role) {
        return jdbcTemplate.update("update efaz_registration set registeration_email=?," +
                        "registeration_password=?, registeration_username=?, registeration_phone_number=?," +
                        "registration_organization_name=?, registration_address_desc=?, registration_website_url=?," +
                        "registration_is_school=?, registration_isActive=?, registration_role=?" +
                        " where registration_id=?", email, password, userName, phoneNumber
                , companyName, address, website, isActive, registration_role, id);
    }

    public int deleteUser(int id) {
        RegistrationModel model = getUser(id);
        try {
            sendNotConfirmEmail(model.getRegisteration_email(), id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jdbcTemplate.update("DELETE FROM efaz_registration WHERE registration_id=?", id);
    }


    public List<RegistrationModel> getInActiveCompanies() {
        return jdbcTemplate.query("SELECT * FROM efaz_registration WHERE registration_isActive=0;",
                (resultSet, i) -> new RegistrationModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10), resultSet.getTimestamp(11).getTime()));
    }

    public int activeCompanyAccount(int id) {
        jdbcTemplate.update("update efaz_registration set registration_isActive=1 WHERE registration_id=?", id);
        RegistrationModel model = getUser(id);
        RegistrationModel models = new RegistrationModel();

        return jdbcTemplate.update("INSERT INTO efaz_login VALUES (?,?,?,?,?,?,?)",  null, model.getRegisteration_email(),
                model.getRegisteration_password(), 0, model.getRegistration_role(), "Token=", new Timestamp(System.currentTimeMillis()));

    }

    public List<RegistrationModel> getActiveCompanies() {
        return jdbcTemplate.query("SELECT * FROM efaz_registration WHERE registration_isActive=1;",
                (resultSet, i) -> new RegistrationModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10), resultSet.getTimestamp(11).getTime()));
    }

    public int inActiveCompanyAccount(int id) {
        return jdbcTemplate.update("update efaz_registration set registration_isActive=0 WHERE registration_id=?", id);
    }

    public int  confirmEmail(int id) {
        RegistrationModel model = getUser(id);
        if (loginRepo.isExistILogin(model.getRegisteration_email(), model.getRegistration_role())){
            return -100;
        }else {
            RegistrationModel models = new RegistrationModel();

            try {
                sendEmail(model.getRegisteration_email(), id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            jdbcTemplate.update("update efaz_registration set registration_isActive=1 WHERE registration_id=?", id);
            return jdbcTemplate.update("INSERT INTO efaz_login VALUES (?,?,?,?,?,?,?,?,?)", null, model.getRegisteration_email(),
                    model.getRegisteration_password(), 0, model.getRegistration_role(), "Token=", new Timestamp(System.currentTimeMillis()),"","");

        }



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


    /**
     * @param email
     * @param createdId
     * @throws Exception
     */
    private void sendNotConfirmEmail(String email, int createdId) throws Exception {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(email);
        helper.setText("Just Few Moments to complete Regsiter");
        helper.setText(
                "<table>\n" +
                        "    <tr>" +
                        "        <td style=\"background-color: #4ecdc4;border-color: #4c5764;border: 2px solid #45b7af;padding: 10px;text-align: center;\">" +
                        "            <P style=\"display: block;color: #ffffff;font-size: 12px;text-decoration: none;text-transform: uppercase;\">" +
                        "                Your Email has not been Confirmed" +
                        "            </p>" +
                        "        </td>" +
                        "    </tr>" +
                        "</table>"
                , true);
        helper.setSubject("Complete Registration with id " + createdId);
        sender.send(message);
    }

}
