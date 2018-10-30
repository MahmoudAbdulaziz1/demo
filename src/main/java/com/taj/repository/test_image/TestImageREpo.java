package com.taj.repository.test_image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.FileOutputStream;

/**
 * Created by MahmoudAhmed on 10/14/2018.
 */
@Repository
public class TestImageREpo {

    @Autowired
    JdbcTemplate template;
    private FileOutputStream fileOutputStream;

    public int insertData(String url, String name, String second) {
        String sql = "INSERT INTO imagetest VALUES(?,?,?,?);";
        return template.update(sql, null, url, name, second);
    }

    public int insertData2(byte[] url, String name) {
        String sql = "INSERT INTO imagetest VALUES(?,?,?);";
        return template.update(sql, null, url, name);
    }


}