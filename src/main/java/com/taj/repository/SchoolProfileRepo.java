package com.taj.repository;

import com.taj.model.CustomSchoolProfileModel;
import com.taj.model.SchoolProfileModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by MahmoudAhmed on 6/3/2018.
 */
@Repository
public class SchoolProfileRepo {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean isExist(int school_id) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM efaz_school_profile WHERE school_id=?;",
                Integer.class, school_id);
        return cnt != null && cnt > 0;
    }

    public boolean isExistInLogin(int school_id, String login_role) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM efaz_login WHERE login_id=? AND login_role=?;",
                Integer.class, school_id, login_role);
        return cnt != null && cnt > 0;
    }

    public int addSchoolProfile(int school_id, String school_name, String school_logo_image, String school_address,
                                String school_service_desc, String school_link_youtube, String school_website_url, float school_lng,
                                float school_lat, String school_cover_image, String school_phone_number) {
        return jdbcTemplate.update("INSERT INTO efaz_school_profile VALUES (?,?,?,?,?,?,?,?,?,?,?)", school_id, school_name,
                school_logo_image, school_address, school_service_desc, school_link_youtube, school_website_url, school_lng, school_lat, school_cover_image, school_phone_number);
    }


    public List<SchoolProfileModel> getSchoolSProfiles() {
        return jdbcTemplate.query("SELECT * FROM efaz_school_profile;",
                (resultSet, i) -> new SchoolProfileModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
                        resultSet.getString(6), resultSet.getString(7), resultSet.getFloat(8),
                        resultSet.getFloat(9), resultSet.getString(10), resultSet.getString(11)));
    }

    public String getSchoolSProfilesLogo(int id) {
        return jdbcTemplate.queryForObject("SELECT school_logo_image FROM efaz_school_profile where school_id=?;",
                String.class, id);
    }

    public SchoolProfileModel getSchoolProfile(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM efaz_school_profile WHERE  school_id=?;",
                new Object[]{id}, (resultSet, i) -> new SchoolProfileModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
                        resultSet.getString(6), resultSet.getString(7), resultSet.getFloat(8), resultSet.getFloat(9),resultSet.getString(10), resultSet.getString(11)));
    }

    public int updateProfile(int id, String school_name, String school_logo_image, String school_address,
                             String school_service_desc, String school_link_youtube, String school_website_url, float school_lng, float school_lat,
                             String school_cover_image, String school_phone_number) {

        return jdbcTemplate.update("update efaz_school_profile set school_name=?," +
                        "school_logo_image=?, school_address=?," +
                        "school_service_desc=?, school_link_youtube=?, school_website_url=?, school_lng=?, school_lat=?, school_cover_image=?, school_phone_number=? " +
                        " where school_id=?", school_name, school_logo_image, school_address, school_service_desc
                , school_link_youtube, school_website_url, school_lng, school_lat, school_cover_image, school_phone_number, id);

    }

    public int checkSchoolProfile(int id) {
        int count = jdbcTemplate.queryForObject("SELECT Count(*) FROM efaz_school_profile WHERE  school_id=?;",
                Integer.class, id);
        return count;
    }

    public int deleteSchoolProfile(int id) {
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=0;");
        int response = jdbcTemplate.update("DELETE FROM efaz_school_profile WHERE school_id=?", id);
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=1;");
        return response;
    }


    public CustomSchoolProfileModel getSchoolProfileForAdmin(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM efaz_school_profile WHERE  school_id=?;",
                new Object[]{id}, (resultSet, i) -> new CustomSchoolProfileModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                         resultSet.getString(7), resultSet.getString(10), resultSet.getString(11)));
    }

    public int updateProfileForAdmin(int id, String school_name, String school_logo_image, String school_address, String school_service_desc,
                                     String school_link_youtube, String school_website_url,
                             String school_cover_image, String school_phone_number) {

        float lat = jdbcTemplate.queryForObject("SELECT school_lat FROM efaz_school_profile WHERE  school_id=?;",
                Float.class, id);
        float lng = jdbcTemplate.queryForObject("SELECT school_lng FROM efaz_school_profile WHERE  school_id=?;",
                Float.class, id);

        return jdbcTemplate.update("update efaz_school_profile set school_name=?," +
                        "school_logo_image=?, school_address=?," +
                        "school_service_desc=?, school_link_youtube=?, school_website_url=?, school_lng=?, school_lat=?, school_cover_image=?, school_phone_number=? " +
                        " where school_id=?", school_name, school_logo_image, school_address, school_service_desc
                , school_link_youtube, school_website_url, lng, lat, school_cover_image, school_phone_number, id);

    }

}
