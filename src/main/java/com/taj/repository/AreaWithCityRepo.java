package com.taj.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by User on 9/17/2018.
 */
@Repository
public class AreaWithCityRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;


    public List<Map<String, Object>> getTestObject(int flag_ar) {
        String sql = "";
        if (flag_ar == 0){
            sql = "SELECT area_id, area_name, city_id, city_name " +
                    "FROM  area AS a " +
                    "LEFT JOIN city AS c ON a.area_id = c.city_area_id;";
        }else {
            sql = "SELECT area_id, area_name_ar AS area_name, city_id, city_name_ar  AS city_name " +
                    "FROM  area AS a " +
                    "LEFT JOIN city AS c ON a.area_id = c.city_area_id;";
        }



        return jdbcTemplate.queryForList(sql);
    }


}
