package com.taj.repository;

import com.taj.model.CategoryModelEnglish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Taj 51 on 6/10/2018.
 */
@Repository
public class CategoryRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<CategoryModelEnglish> getCategories(int flag_ar) {
        String sql = "";
        if (flag_ar == 0){
            sql = "SELECT category_id, category_name FROM efaz_company_category;";
        }else {
            sql = "SELECT category_id, category_name_ar AS category_name FROM efaz_company_category;";
        }
        return jdbcTemplate.query(sql,
                (resultSet, i) -> new CategoryModelEnglish(resultSet.getInt(1), resultSet.getString(2)));
    }


    public CategoryModelEnglish getCategory(int id, int flag_ar) {
        String sql = "";
        if (flag_ar == 0){
            sql = "SELECT category_id, category_name FROM efaz_company_category WHERE category_id = ?;";
        }else {
            sql = "SELECT category_id, category_name_ar AS category_naem FROM efaz_company_category  WHERE category_id = ?;";
        }
        return jdbcTemplate.queryForObject(sql,
                new Object[]{id}, (resultSet, i) -> new CategoryModelEnglish(resultSet.getInt(1), resultSet.getString(2)));
    }

    public int addCategory(String categoryName, String categoryNameAr) {

        return jdbcTemplate.update("INSERT INTO efaz_company_category VALUES (?,?,?)", null, categoryName, categoryNameAr);
    }

    public int updateCategory(int id, String categoryName, String categoryNameAr) {
        return jdbcTemplate.update("update efaz_company_category set category_name=?, category_name_ar=? " +
                " where category_id=?;", categoryName, id);
    }

    public int deleteCategory(int id) {
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=0;");
        int res = jdbcTemplate.update("DELETE FROM efaz_company_category WHERE category_id=?", id);
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=1;");
        return res;

    }

}
