package com.taj.repository;

import com.taj.model.AreaModelEnglish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by User on 9/16/2018.
 */
@Repository
public class AreaRepo {


    @Autowired
    JdbcTemplate jdbcTemplate;

    public int addArea(String areaName, String areNameAr) {
        return jdbcTemplate.update("INSERT INTO area VALUES (?,?,?);", null, areaName, areNameAr);
    }

    public List<AreaModelEnglish> getAreas(int flag_ar) {
        String sql = "";
        if (flag_ar == 0){
            sql = "SELECT area_id, area_name  FROM area;";
        }else {
            sql = "SELECT area_id, area_name_ar  FROM area;";
        }
        return jdbcTemplate.query(sql,
                (resultSet, i) -> new AreaModelEnglish(resultSet.getInt(1), resultSet.getString(2)));
    }

    public AreaModelEnglish getareaById(int areaId, int flag_ar) {
        String sql = "";
        if (flag_ar == 0){
            sql = "SELECT area_id, area_name FROM area WHERE area_id=?;";
        }else {
            sql = "SELECT area_id, area_name_ar FROM area WHERE area_id=?;";
        }
        return jdbcTemplate.queryForObject(sql, new Object[]{areaId},
                (resultSet, i) -> new AreaModelEnglish(resultSet.getInt(1), resultSet.getString(2)));
    }

    public int updateArea(int areaId, String areaName) {
        return jdbcTemplate.update("UPDATE area SET area_name=? WHERE area_id=?;", areaName, areaId);
    }

    public int deleteArea(int areaId) {
        return jdbcTemplate.update("DELETE FROM area WHERE area_id=?", areaId);
    }

    public boolean checkIfExist(int areaId) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM  area WHERE area_id=?;",
                Integer.class, areaId);
        return cnt != null && cnt > 0;
    }
}
