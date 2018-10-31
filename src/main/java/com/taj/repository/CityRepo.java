package com.taj.repository;

import com.taj.model.CityModelEnglish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by User on 9/16/2018.
 */
@Repository
public class CityRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int addCity(String cityName, int cityAreaId, String cityNameAr) {
        return jdbcTemplate.update("INSERT INTO city VALUES (?,?,?,?);", null, cityName, cityAreaId, cityNameAr);
    }

    public List<CityModelEnglish> getCities(int flag_ar) {
        String sql = "";
        if (flag_ar == 0){
            sql = "SELECT city_id, city_name, city_area_id FROM city;";
        }else {
            sql = "SELECT city_id, city_name_ar AS city_name, city_area_id FROM city;";
        }
        return jdbcTemplate.query(sql,
                (resultSet, i) -> new CityModelEnglish(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3)));
    }

    public CityModelEnglish getCityById(int cityId, int flag_ar) {

        String sql = "";
        if (flag_ar == 0){
            sql = "SELECT city_id, city_name, city_area_id FROM city WHERE city_id=?;";
        }else {
            sql = "SELECT city_id, city_name_ar AS city_name, city_area_id FROM city WHERE city_id=?;";
        }
        return jdbcTemplate.queryForObject(sql, new Object[]{cityId},
                (resultSet, i) -> new CityModelEnglish(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3)));
    }
    public int updateCity(int cityId, String cityName, int city_area_id){
        return jdbcTemplate.update("UPDATE city SET city_name=? , set city_area_id=? WHERE city_id=?;", cityName, city_area_id, cityId);
    }
    public int deleteCity(int cityId){
        return jdbcTemplate.update("DELETE FROM city WHERE city_id=?", cityId);
    }

    public boolean checkIfExist(int cityId) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM  city WHERE city_id=?;",
                Integer.class, cityId);
        return cnt != null && cnt > 0;
    }


}
