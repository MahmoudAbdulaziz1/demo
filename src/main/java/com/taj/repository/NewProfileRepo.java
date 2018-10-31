package com.taj.repository;

import com.taj.model.NewProfileDto;
import com.taj.model.NewProfileDto2;
import com.taj.model.NewProfileModel;
import com.taj.model.TakatfTenderCategoryPOJO;
import com.taj.model.new_profile_map.NewProfileDto2DTO;
import com.taj.model.new_profile_map.NewProfileDtoDTO;
import com.taj.model.new_profile_map.NewProfileModelDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 9/11/2018.
 */
@Repository
public class NewProfileRepo {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public boolean isExist(int companyId) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM efaz_company_profile WHERE company_id=?;",
                Integer.class, companyId);
        return cnt != null && cnt > 0;
    }


    public int addProfileWithCategories(int companyId, String companyName, String companyLogoImage, String companyAddress,
                                        String companyLinkYoutube, String companyWebsiteUrl, float schoolLng,
                                        float schoolLat, String companyCoverImage, String companyPhoneNumber, String companyDesc,
                                        List<TakatfTenderCategoryPOJO> category) {
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=0;");

        KeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                final PreparedStatement ps = connection.prepareStatement("INSERT INTO efaz_company_profile VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, companyId);
                ps.setString(2, companyName);
                ps.setString(3, companyLogoImage);
                ps.setString(4, companyAddress);
                ps.setInt(5, 1);
                ps.setString(6, companyLinkYoutube);
                ps.setString(7, companyWebsiteUrl);
                ps.setFloat(8, schoolLng);
                ps.setFloat(9, schoolLat);
                ps.setString(10, companyCoverImage);
                ps.setString(11, companyPhoneNumber);
                ps.setString(12, companyDesc);
                return ps;
            }

        }, key);

        int profileID = key.getKey().intValue();


        for (int i = 0; i < category.size(); i++) {
            int categorys = jdbcTemplate.queryForObject("SELECT category_id  FROM  efaz_company_category WHERE  category_name LIKE ?;",
                    Integer.class, "%" + category.get(i).getCategory_name().trim() + "%");

            jdbcTemplate.update("INSERT INTO efaz_company_profile_cats VALUES  (?,?,?)", null, profileID, categorys);
        }


        return profileID;
    }

    public List<NewProfileModel> getProfiles() {

        String sql = "SELECT\n" +
                "\tcompany_id,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_address,\n" +
                "\tcompany_link_youtube,\n" +
                "\tcompany_website_url,\n" +
                "\tcompany_lng,\n" +
                "\tcompany_lat,\n" +
                "\tcompany_cover_image,\n" +
                "\tcompany_phone_number,\n" +
                "\tcount( DISTINCT follow_id ) AS follower_count,\n" +
                "\tcount( DISTINCT offer_id ) AS order_count,\n" +
                "\tcompany_desc,\n" +
                "\tCOUNT( DISTINCT id ) AS category_num,\n" +
                "\tcity,\n" +
                "\tarea \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\t(\n" +
                "\t\t\t( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.follower_id )\n" +
                "\t\t\tLEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id \n" +
                "\t\t)\n" +
                "\t\tLEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id \n" +
                "\t)\n" +
                "\tLEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id \n" +
                "GROUP BY\n" +
                "\tPROFILE.company_id;";

        return jdbcTemplate.query(sql,
                (resultSet, i) -> new NewProfileModel(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4),
                        resultSet.getString(5), resultSet.getString(6), resultSet.getFloat(7), resultSet.getFloat(8),
                        resultSet.getString(9), resultSet.getString(10), resultSet.getInt(11), resultSet.getInt(12),
                        resultSet.getString(13), resultSet.getInt(14), resultSet.getString(15), resultSet.getString(16)));
    }


    public List<NewProfileModelDTO> getProfiles2() {

        String sql = "SELECT\n" +
                "\tcompany_id,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_address,\n" +
                "\tcompany_link_youtube,\n" +
                "\tcompany_website_url,\n" +
                "\tcompany_lng,\n" +
                "\tcompany_lat,\n" +
                "\tcompany_cover_image,\n" +
                "\tcompany_phone_number,\n" +
                "\tcount( DISTINCT follow_id ) AS follower_count,\n" +
                "\tcount( DISTINCT offer_id ) AS order_count,\n" +
                "\tcompany_desc,\n" +
                "\tCOUNT( DISTINCT id ) AS category_num,\n" +
                "\tcity,\n" +
                "\tarea, lng, lat \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\t(\n" +
                "\t\t\t( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.follower_id )\n" +
                "\t\t\tLEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id \n" +
                "\t\t)\n" +
                "\t\tLEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id \n" +
                "\t)\n" +
                "\tLEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id \n" +
                "GROUP BY\n" +
                "\tPROFILE.company_id;";

        return jdbcTemplate.query(sql,
                (resultSet, i) -> new NewProfileModelDTO(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4),
                        resultSet.getString(5), resultSet.getString(6), resultSet.getFloat(7), resultSet.getFloat(8),
                        resultSet.getString(9), resultSet.getString(10), resultSet.getInt(11), resultSet.getInt(12),
                        resultSet.getString(13), resultSet.getInt(14), resultSet.getString(15), resultSet.getString(16),
                        resultSet.getFloat(17), resultSet.getFloat(18)));
    }


    public List<Map<String, Object>> getCompaniesProfilesObject() {
        String sql = "SELECT\n" +
                "\tcompany_id,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_address,\n" +
                "\tcompany_link_youtube,\n" +
                "\tcompany_website_url,\n" +
                "\tcompany_lng,\n" +
                "\tcompany_lat,\n" +
                "\tcompany_cover_image,\n" +
                "\tcompany_phone_number,\n" +
                "\tcount( DISTINCT follow_id ) AS follower_count,\n" +
                "\tcount( DISTINCT offer_id ) AS order_count,\n" +
                "\tcompany_desc,\n" +
                "\tCOUNT( DISTINCT id ) AS category_num,\n" +
                "\tcity,\n" +
                "\tarea,\n" +
                "\tcategory_id,\n" +
                "\tcategory_name \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\t(\n" +
                "\t\t\t( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.follower_id )\n" +
                "\t\t\tLEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id \n" +
                "\t\t)\n" +
                "\t\tLEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id \n" +
                "\t)\n" +
                "\tLEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id\n" +
                "\tLEFT JOIN efaz_company_category AS ccat ON cats.company_cat_id = ccat.category_id \n" +
                "GROUP BY\n" +
                "\tPROFILE.company_id,\n" +
                "\tccat.category_id;";


        return jdbcTemplate.queryForList(sql);
//        List<Map<String,Object>> maps=jdbcTemplate.queryForList(sql, new Object[]{id});//, new Object[]{id}, (resultSet, i) -> new Object());

    }


    public List<Map<String, Object>> getCompaniesProfilesObjectForAll(int school_id) {
        String sql = "SELECT\n" +
                "\n" +
                "                company_id,\n" +
                "                company_name,\n" +
                "                company_logo_image,\n" +
                "                company_address,\n" +
                "                company_link_youtube,\n" +
                "                company_website_url,\n" +
                "                company_lng,\n" +
                "                company_lat,\n" +
                "                company_cover_image,\n" +
                "                company_phone_number,\n" +
                "                count( DISTINCT follow.follow_id ) AS follower_count,\n" +
                "                count( DISTINCT offer_id ) AS order_count,\n" +
                "                company_desc,\n" +
                "                COUNT( id ) AS category_num,\n" +
                "                city,\n" +
                "                area,\n" +
                "                 category_id,\n" +
                "                category_name,\n" +
                "                IF\n" +
                "                ( PROFILE.company_id = follow2.organization_id AND follow2.follower_id = ?,  1, 0 ) AS is_follow \n" +
                "                FROM\n" +
                "                (\n" +
                "                (\n" +
                "                ( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.organization_id )\n" +
                "                LEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id \n" +
                "                )\n" +
                "                LEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id \n" +
                "                )\n" +
                "                LEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id\n" +
                "                LEFT JOIN efaz_company_category AS ccat ON cats.company_cat_id = ccat.category_id\n" +
                "                LEFT JOIN efaz_organization_following AS follow2 ON PROFILE.company_id = follow2.organization_id \n" +
                "                AND follow2.follower_id = ? \n" +
                "                GROUP BY\n" +
                "                company_id,\n" +
                "                ccat.category_id,\n" +
                "                \tfollow2.follower_id;";


        return jdbcTemplate.queryForList(sql, new Object[]{school_id, school_id});
//        List<Map<String,Object>> maps=jdbcTemplate.queryForList(sql, new Object[]{id});//, new Object[]{id}, (resultSet, i) -> new Object());

    }


    public int getCompaniesProfilesObjectForAll2PaginateCount(int school_id) {
        String sql = "SELECT\n" +
                "\tCOUNT(DISTINCT company_id) AS count\n" +
                "FROM\n" +
                "\t(\n" +
                "\t\t(\n" +
                "\t\t\t( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.organization_id )\n" +
                "\t\t\tLEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id \n" +
                "\t\t)\n" +
                "\t\tLEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id \n" +
                "\t)\n" +
                "\tLEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id\n" +
                "\tLEFT JOIN efaz_company_category AS ccat ON cats.company_cat_id = ccat.category_id\n" +
                "\tLEFT JOIN efaz_organization_following AS follow2 ON PROFILE.company_id = follow2.organization_id \n" +
                "\tAND follow2.follower_id = ? ;";

        return jdbcTemplate.queryForObject(sql, Integer.class, school_id);
    }

    public List<Map<String, Object>> getCompaniesProfilesObjectForAll2(int school_id) {
        String sql = "SELECT\n" +

                "                company_id,\n" +
                "                company_name,\n" +
                "                company_logo_image,\n" +
                "                company_address,\n" +
                "                company_link_youtube,\n" +
                "                company_website_url,\n" +
                "                company_lng,\n" +
                "                company_lat,\n" +
                "                company_cover_image,\n" +
                "                company_phone_number,\n" +
                "                count( DISTINCT follow.follow_id ) AS follower_count,\n" +
                "                count( DISTINCT offer_id ) AS order_count,\n" +
                "                company_desc,\n" +
                "                COUNT( id ) AS category_num,\n" +
                "                city,\n" +
                "                area,lng,lat,\n" +
                "                 category_id,\n" +
                "                category_name,\n" +
                "                IF\n" +
                "                ( PROFILE.company_id = follow2.organization_id AND follow2.follower_id = ?,  1, 0 ) AS is_follow \n" +
                "                FROM\n" +
                "                (\n" +
                "                (\n" +
                "                ( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.organization_id )\n" +
                "                LEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id \n" +
                "                )\n" +
                "                LEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id \n" +
                "                )\n" +
                "                LEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id\n" +
                "                LEFT JOIN efaz_company_category AS ccat ON cats.company_cat_id = ccat.category_id\n" +
                "                LEFT JOIN efaz_organization_following AS follow2 ON PROFILE.company_id = follow2.organization_id \n" +
                "                AND follow2.follower_id = ? \n" +
                "                GROUP BY\n" +
                "                company_id,\n" +
                "                ccat.category_id,\n" +
                "                \tfollow2.follower_id;";


        return jdbcTemplate.queryForList(sql, new Object[]{school_id, school_id});
//        List<Map<String,Object>> maps=jdbcTemplate.queryForList(sql, new Object[]{id});//, new Object[]{id}, (resultSet, i) -> new Object());

    }


    public List<Map<String, Object>> getCompaniesProfilesObjectForAll2WithFilter(int school_id, String name, int cat,
                                                                                 String area, String city, int view, int followed) {


        int nameFlag = 0;
        int catFlag = 0;
        int areaFlag = 0;
        int cityFlag = 0;
        int viewFlag = 0;
        int followedFlag = 0;

        String cols = "SELECT \n" +
                "    company_id,\n" +
                "    company_name,\n" +
                "    company_logo_image,\n" +
                "    company_address,\n" +
                "    company_link_youtube,\n" +
                "    company_website_url,\n" +
                "    company_lng,\n" +
                "    company_lat,\n" +
                "    company_cover_image,\n" +
                "    company_phone_number,\n" +
                "    COUNT(DISTINCT follow.follow_id) AS follower_count,\n" +
                "    COUNT(DISTINCT offer_id) AS order_count,\n" +
                "    company_desc,\n" +
                "    COUNT(id) AS category_num,\n" +
                "    city,\n" +
                "    area,\n" +
                "    lng,\n" +
                "    lat,\n" +
                "    category_id,\n" +
                "    category_name,\n" +
                "    IF(PROFILE.company_id = follow2.organization_id\n" +
                "            AND follow2.follower_id = ?,\n" +
                "        1,\n" +
                "        0) AS is_follow  ";


        String from = "FROM\n" +
                "    (((efaz_company_profile AS PROFILE ";
        String join1 = " LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.organization_id ";
        String close1 = ")";
        String join2 = " LEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id ";
        String close2 = ")";
        String join3 = " LEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id ";
        String close3 = ")";
        String join4 = " LEFT JOIN\n" +
                "    efaz_login AS pc ON PROFILE.company_id = pc.login_id ";
        String join5 = " LEFT JOIN\n" +
                "    efaz_company_category AS ccat ON cats.company_cat_id = ccat.category_id ";
        String join6 = "LEFT JOIN\n" +
                "    efaz_organization_following AS follow2 ON PROFILE.company_id = follow2.organization_id\n" +
                "        AND follow2.follower_id = ?";

        String where = " WHERE  ";

        String groupBy = " GROUP BY company_id , ccat.category_id , follow2.follower_id; ";


        if (!(name.isEmpty() || name.equals(null) || name.equals(""))) {
            nameFlag = 1;
            where = where + " company_name LIKE ? ";
        }

        if (cat != 0) {
            if (nameFlag != 0) {
                where = where + " AND category_id = ? ";
            } else {
                where = where + " category_id = ? ";
            }
            catFlag = 1;
        }

        if (!(area.isEmpty() || area.equals(null) || area.equals(""))) {
            if (nameFlag != 0 || catFlag != 0) {
                where = where + " AND area LIKE ? ";
            } else {
                where = where + " area LIKE ? ";
            }

            areaFlag = 1;
        }

        if (!(city.isEmpty() || city.equals(null) || city.equals(""))) {
            if (nameFlag != 0 || catFlag != 0 || areaFlag != 0) {
                where = where + " AND city LIKE ? ";
            } else {
                where = where + " city LIKE ? ";
            }

            cityFlag = 1;
        }

        if (view != 0) {


            if (nameFlag != 0 || catFlag != 0 || areaFlag != 0 || cityFlag != 0) {
                where = where + " AND PROFILE.company_id = offer.offer_company_id ";
            } else {
                where = where + " PROFILE.company_id = offer.offer_company_id ";
            }

            viewFlag = 1;
        }


        if (followed != 0) {


            if (nameFlag != 0 || catFlag != 0 || areaFlag != 0 || cityFlag != 0 || viewFlag != 0) {
                where = where + " AND PROFILE.company_id = follow2.organization_id AND follow2.follower_id = ? ";
            } else {
                where = where + " PROFILE.company_id = follow2.organization_id AND follow2.follower_id = ? ";
            }

            followedFlag = 1;
        }

        String sql = "";
        if (nameFlag == 1 || catFlag == 1 || areaFlag == 1 || cityFlag == 1 || viewFlag == 1 || followedFlag == 1) {
            sql = sql + cols + from + join1 + close1 + join2 + close2 + join3 + close3 + join4 + join5 + join6 + where + groupBy;
        } else {
            sql = sql + cols + from + join1 + close1 + join2 + close2 + join3 + close3 + join4 + join5 + join6 + groupBy;
        }

        List<Object> list = new ArrayList<>();
        list.add(school_id);
        list.add(school_id);
        if (nameFlag == 1) {
            list.add("%"+name+"%");
        }
        if (catFlag == 1) {
            list.add(cat);
        }
        if (areaFlag == 1) {
            list.add(area);
        }
        if (cityFlag == 1) {
            list.add(city);
        }
        if (followedFlag == 1) {
            list.add(school_id);
        }
        Object[] obj = list.toArray();


        System.out.println(sql + "  " + obj.length);



        return jdbcTemplate.queryForList(sql, obj);
//        List<Map<String,Object>> maps=jdbcTemplate.queryForList(sql, new Object[]{id});//, new Object[]{id}, (resultSet, i) -> new Object());

    }


    public List<Map<String, Object>> getCompaniesProfilesObjectForAll2Pagination(int school_id, int page, int pageSize) {
        String sql = "SELECT\n" +
                "                company_id,\n" +
                "                company_name,\n" +
                "                company_logo_image,\n" +
                "                company_address,\n" +
                "                company_link_youtube,\n" +
                "                company_website_url,\n" +
                "                company_lng,\n" +
                "                company_lat,\n" +
                "                company_cover_image,\n" +
                "                company_phone_number,\n" +
                "                count( DISTINCT follow.follow_id ) AS follower_count,\n" +
                "                count( DISTINCT offer_id ) AS order_count,\n" +
                "                company_desc,\n" +
                "                COUNT( id ) AS category_num,\n" +
                "                city,\n" +
                "                area,lng,lat,\n" +
                "                 category_id,\n" +
                "                category_name,\n" +
                "                IF\n" +
                "                ( PROFILE.company_id = follow2.organization_id AND follow2.follower_id = ?,  1, 0 ) AS is_follow \n" +
                "                FROM\n" +
                "                (\n" +
                "                (\n" +
                "                ( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.organization_id )\n" +
                "                LEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id \n" +
                "                )\n" +
                "                LEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id \n" +
                "                )\n" +
                "                LEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id\n" +
                "                LEFT JOIN efaz_company_category AS ccat ON cats.company_cat_id = ccat.category_id\n" +
                "                LEFT JOIN efaz_organization_following AS follow2 ON PROFILE.company_id = follow2.organization_id \n" +
                "                AND follow2.follower_id = ? \n" +
                "                GROUP BY\n" +
                "                company_id,\n" +
                "                ccat.category_id,\n" +
                "                \tfollow2.follower_id;";


        return jdbcTemplate.queryForList(sql, new Object[]{school_id, school_id});
//        List<Map<String,Object>> maps=jdbcTemplate.queryForList(sql, new Object[]{id});//, new Object[]{id}, (resultSet, i) -> new Object());

    }

    public List<NewProfileDto> getProfile(int profile) {
        String sql = "SELECT\n" +
                "\tcompany_id,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_address,\n" +
                "\tcompany_link_youtube,\n" +
                "\tcompany_website_url,\n" +
                "\tcompany_lng,\n" +
                "\tcompany_lat,\n" +
                "\tcompany_cover_image,\n" +
                "\tcompany_phone_number,\n" +
                "\tcount( DISTINCT follow_id ) AS follower_count,\n" +
                "\tcount( DISTINCT offer_id ) AS order_count,\n" +
                "\tcompany_desc,\n" +
                "\tcompany_cat_id,\n" +
                "\tcategory.category_name,\n" +
                "\tcity,\n" +
                "\tarea \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\t(\n" +
                "\t\t\t( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.follower_id )\n" +
                "\t\t\tLEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id \n" +
                "\t\t)\n" +
                "\t\tLEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id\n" +
                "\t\tLEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id\n" +
                "\t\tINNER JOIN efaz_company_category AS category ON cats.company_cat_id = category.category_id \n" +
                "\t) \n" +
                "WHERE\n" +
                "\tPROFILE.company_id = ? \n" +
                "GROUP BY\n" +
                "\tcats.company_cat_id;";
        return jdbcTemplate.query(sql, new Object[]{profile},
                (resultSet, i) -> new NewProfileDto(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                        resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getFloat(7), resultSet.getFloat(8), resultSet.getString(9), resultSet.getString(10),
                        resultSet.getInt(11), resultSet.getInt(12), resultSet.getString(13), resultSet.getInt(14),
                        resultSet.getString(15), resultSet.getString(16), resultSet.getString(17)));
    }


    public List<NewProfileDtoDTO> getProfile2(int profile) {
        String sql = "SELECT\n" +
                "\tcompany_id,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_address,\n" +
                "\tcompany_link_youtube,\n" +
                "\tcompany_website_url,\n" +
                "\tcompany_lng,\n" +
                "\tcompany_lat,\n" +
                "\tcompany_cover_image,\n" +
                "\tcompany_phone_number,\n" +
                "\tcount( DISTINCT follow_id ) AS follower_count,\n" +
                "\tcount( DISTINCT offer_id ) AS order_count,\n" +
                "\tcompany_desc,\n" +
                "\tcompany_cat_id,\n" +
                "\tcategory.category_name,\n" +
                "\tcity,\n" +
                "\tarea, pc.lng, pc.lat \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\t(\n" +
                "\t\t\t( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.follower_id )\n" +
                "\t\t\tLEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id \n" +
                "\t\t)\n" +
                "\t\tLEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id\n" +
                "\t\tLEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id\n" +
                "\t\tINNER JOIN efaz_company_category AS category ON cats.company_cat_id = category.category_id \n" +
                "\t) \n" +
                "WHERE\n" +
                "\tPROFILE.company_id = ? \n" +
                "GROUP BY\n" +
                "\tcats.company_cat_id;";
        return jdbcTemplate.query(sql, new Object[]{profile},
                (resultSet, i) -> new NewProfileDtoDTO(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                        resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getFloat(7), resultSet.getFloat(8), resultSet.getString(9), resultSet.getString(10),
                        resultSet.getInt(11), resultSet.getInt(12), resultSet.getString(13), resultSet.getInt(14),
                        resultSet.getString(15), resultSet.getString(16), resultSet.getString(17), resultSet.getFloat(18), resultSet.getFloat(19)));
    }


    public int updateProfile(int companyId, String companyName, String companyLogoImage, String companyAddress,
                             String companyLinkYoutube, String companyWebsiteUrl, float schoolLng,
                             float schoolLat, String companyCoverImage, String companyPhoneNumber, String companyDesc,
                             String city, String area, List<TakatfTenderCategoryPOJO> category) {
        jdbcTemplate.update("DELETE FROM efaz_company_profile_cats WHERE company_profile_id=?;", companyId);

        for (int i = 0; i < category.size(); i++) {
            int categorys = jdbcTemplate.queryForObject("SELECT category_id  FROM  efaz_company_category WHERE  category_name LIKE ?;",
                    Integer.class, "%" + category.get(i).getCategory_name().trim() + "%");

            jdbcTemplate.update("INSERT INTO efaz_company_profile_cats VALUES  (?,?,?)", null, companyId, categorys);
        }

        jdbcTemplate.update("update efaz_login set city=?," +
                "area=?  " +
                " where login_id=?;", city, area, companyId);

        return jdbcTemplate.update("update efaz_company_profile set company_name=?," +
                        "company_logo_image=?, company_address=?," +
                        "company_category_id=?, company_link_youtube=?, company_website_url=?, company_lng=?, company_lat=?," +
                        " company_cover_image=?, company_phone_number=?, company_desc=? " +
                        " where company_id=?;", companyName, companyLogoImage, companyAddress, 2
                , companyLinkYoutube, companyWebsiteUrl, schoolLng, schoolLat, companyCoverImage, companyPhoneNumber, companyDesc, companyId);


    }


    public int updateProfile2(int companyId, String companyName, String companyLogoImage, String companyAddress,
                              String companyLinkYoutube, String companyWebsiteUrl, float schoolLng,
                              float schoolLat, String companyCoverImage, String companyPhoneNumber, String companyDesc,
                              String city, String area, float lng, float lat, List<TakatfTenderCategoryPOJO> category) {
        jdbcTemplate.update("DELETE FROM efaz_company_profile_cats WHERE company_profile_id=?;", companyId);

        for (int i = 0; i < category.size(); i++) {
            int categorys = jdbcTemplate.queryForObject("SELECT category_id  FROM  efaz_company_category WHERE  category_name LIKE ?;",
                    Integer.class, "%" + category.get(i).getCategory_name().trim() + "%");

            jdbcTemplate.update("INSERT INTO efaz_company_profile_cats VALUES  (?,?,?)", null, companyId, categorys);
        }

        jdbcTemplate.update("update efaz_login set city=?," +
                "area=?, lng=?, lat=?  " +
                " where login_id=?;", city, area, lng, lat, companyId);

        return jdbcTemplate.update("update efaz_company_profile set company_name=?," +
                        "company_logo_image=?, company_address=?," +
                        "company_category_id=?, company_link_youtube=?, company_website_url=?, company_lng=?, company_lat=?," +
                        " company_cover_image=?, company_phone_number=?, company_desc=? " +
                        " where company_id=?;", companyName, companyLogoImage, companyAddress, 2
                , companyLinkYoutube, companyWebsiteUrl, schoolLng, schoolLat, companyCoverImage, companyPhoneNumber, companyDesc, companyId);


    }


    public List<NewProfileDto2> getProfileByCategory(String id) {

        String sql = "SELECT\n" +
                "\tcompany_id,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_address,\n" +
                "\tcompany_link_youtube,\n" +
                "\tcompany_website_url,\n" +
                "\tcompany_lng,\n" +
                "\tcompany_lat,\n" +
                "\tcompany_cover_image,\n" +
                "\tcompany_phone_number,\n" +
                "\tcount( DISTINCT follow_id ) AS follower_count,\n" +
                "\tcount( DISTINCT offer_id ) AS order_count,\n" +
                "\tcompany_desc,\n" +
                "\tcity,\n" +
                "\tarea \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\t(\n" +
                "\t\t\t( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.follower_id )\n" +
                "\t\t\tLEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id\n" +
                "\t\t\tLEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id\n" +
                "\t\t\tLEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id \n" +
                "\t\t) \n" +
                "\t) \n" +
                "WHERE\n" +
                "\tcats.company_cat_id = ? \n" +
                "GROUP BY\n" +
                "\tPROFILE.company_id;";
        int category = jdbcTemplate.queryForObject("SELECT category_id FROM efaz_company_category WHERE  category_name LIKE ?;",
                Integer.class, "%" + id + "%");
        return jdbcTemplate.query(sql, new Object[]{category},
                (resultSet, i) -> new NewProfileDto2(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
                        resultSet.getString(6), resultSet.getFloat(7), resultSet.getFloat(8),
                        resultSet.getString(9), resultSet.getString(10), resultSet.getInt(11), resultSet.getInt(12),
                        resultSet.getString(13), resultSet.getString(14), resultSet.getString(15)));
    }

    public List<NewProfileDto2DTO> getProfileByCategory2(String id) {

        String sql = "SELECT\n" +
                "\tcompany_id,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_address,\n" +
                "\tcompany_link_youtube,\n" +
                "\tcompany_website_url,\n" +
                "\tcompany_lng,\n" +
                "\tcompany_lat,\n" +
                "\tcompany_cover_image,\n" +
                "\tcompany_phone_number,\n" +
                "\tcount( DISTINCT follow_id ) AS follower_count,\n" +
                "\tcount( DISTINCT offer_id ) AS order_count,\n" +
                "\tcompany_desc,\n" +
                "\tcity,\n" +
                "\tarea, lng, lat \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\t(\n" +
                "\t\t\t( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.follower_id )\n" +
                "\t\t\tLEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id\n" +
                "\t\t\tLEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id\n" +
                "\t\t\tLEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id \n" +
                "\t\t) \n" +
                "\t) \n" +
                "WHERE\n" +
                "\tcats.company_cat_id = ? \n" +
                "GROUP BY\n" +
                "\tPROFILE.company_id;";
        int category = jdbcTemplate.queryForObject("SELECT category_id FROM efaz_company_category WHERE  category_name LIKE ?;",
                Integer.class, "%" + id + "%");
        return jdbcTemplate.query(sql, new Object[]{category},
                (resultSet, i) -> new NewProfileDto2DTO(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
                        resultSet.getString(6), resultSet.getFloat(7), resultSet.getFloat(8),
                        resultSet.getString(9), resultSet.getString(10), resultSet.getInt(11), resultSet.getInt(12),
                        resultSet.getString(13), resultSet.getString(14), resultSet.getString(15), resultSet.getFloat(16), resultSet.getFloat(17)));
    }


    public List<Map<String, Object>> getCompaniesProfilesObject2() {
        String sql = "SELECT\n" +
                "\tcompany_id,\n" +
                "\tcompany_name,\n" +
                "\tIFNULL(company_logo_image,'') AS company_logo_image,\n" +
                "\tcompany_address,\n" +
                "\tcompany_link_youtube,\n" +
                "\tcompany_website_url,\n" +
                "\tIFNULL(company_lng,0) AS company_lng ,\n" +
                "\tIFNULL(company_lat,0)AS company_lat,\n" +
                "\tIFNULL(company_cover_image,'') AS company_cover_image,\n" +
                "\tcompany_phone_number,\n" +
                "\tcount( DISTINCT follow_id ) AS follower_count,\n" +
                "\tcount( DISTINCT offer_id ) AS order_count,\n" +
                "\tcompany_desc,\n" +
                "\tCOUNT( DISTINCT id ) AS category_num,\n" +
                "\tcity,\n" +
                "\tarea, IFNULL(lng,0)AS lng, IFNULL(lat,0)AS lat, \n" +
                "\tcategory_id,\n" +
                "\tcategory_name \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\t(\n" +
                "\t\t\t( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.follower_id )\n" +
                "\t\t\tLEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id \n" +
                "\t\t)\n" +
                "\t\tLEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id \n" +
                "\t)\n" +
                "\tLEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id\n" +
                "\tLEFT JOIN efaz_company_category AS ccat ON cats.company_cat_id = ccat.category_id \n" +
                "GROUP BY\n" +
                "\tPROFILE.company_id,\n" +
                "\tccat.category_id;";


        return jdbcTemplate.queryForList(sql);
//        List<Map<String,Object>> maps=jdbcTemplate.queryForList(sql, new Object[]{id});//, new Object[]{id}, (resultSet, i) -> new Object());

    }


    public int getCompaniesProfilesObject2PaginationCount() {
        String sql = "SELECT\n" +
                "\tCount( DISTINCT company_id ) \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\t(\n" +
                "\t\t\t( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.follower_id )\n" +
                "\t\t\tLEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id \n" +
                "\t\t)\n" +
                "\t\tLEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id \n" +
                "\t)\n" +
                "\tLEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id\n" +
                "\tLEFT JOIN efaz_company_category AS ccat ON cats.company_cat_id = ccat.category_id";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<Map<String, Object>> getCompaniesProfilesObject2Pagination() {

        String sql = "SELECT\n" +
                "\tcompany_id,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_address,\n" +
                "\tcompany_link_youtube,\n" +
                "\tcompany_website_url,\n" +
                "\tcompany_lng,\n" +
                "\tcompany_lat,\n" +
                "\tcompany_cover_image,\n" +
                "\tcompany_phone_number,\n" +
                "\tcount( DISTINCT follow_id ) AS follower_count,\n" +
                "\tcount( DISTINCT offer_id ) AS order_count,\n" +
                "\tcompany_desc,\n" +
                "\tCOUNT( DISTINCT id ) AS category_num,\n" +
                "\tcity,\n" +
                "\tarea, lng, lat, \n" +
                "\tcategory_id,\n" +
                "\tcategory_name \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\t(\n" +
                "\t\t\t( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.follower_id )\n" +
                "\t\t\tLEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id \n" +
                "\t\t)\n" +
                "\t\tLEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id \n" +
                "\t)\n" +
                "\tLEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id\n" +
                "\tLEFT JOIN efaz_company_category AS ccat ON cats.company_cat_id = ccat.category_id  " +
                "GROUP BY\n" +
                "\tPROFILE.company_id,\n" +
                "\tccat.category_id;";


        return jdbcTemplate.queryForList(sql);
//        List<Map<String,Object>> maps=jdbcTemplate.queryForList(sql, new Object[]{id});//, new Object[]{id}, (resultSet, i) -> new Object());

    }

    //List<Map<String, Object>>
    public List<Map<String, Object>> getCompaniesProfilesObject2PaginationWithFilter(String name, int cat,
                                                                                     String area, String city, int view) {

        int nameFlag = 0;
        int catFlag = 0;
        int areaFlag = 0;
        int cityFlag = 0;
        int viewFlag = 0;

        String cols = "SELECT\n" +
                "\tcompany_id,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "\tcompany_address,\n" +
                "\tcompany_link_youtube,\n" +
                "\tcompany_website_url,\n" +
                "\tcompany_lng,\n" +
                "\tcompany_lat,\n" +
                "\tcompany_cover_image,\n" +
                "\tcompany_phone_number,\n" +
                "\tcount( DISTINCT follow_id ) AS follower_count,\n" +
                "\tcount( DISTINCT offer_id ) AS order_count,\n" +
                "\tcompany_desc,\n" +
                "\tCOUNT( DISTINCT id ) AS category_num,\n" +
                "\tcity,\n" +
                "\tarea, lng, lat, \n" +
                "\tcategory_id,\n" +
                "\tcategory_name \n";

        String from = "FROM ((( efaz_company_profile AS PROFILE  ";

        String join1 = "LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.follower_id  ";
        String close1 = " )";
        String join2 = " LEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id ";
        String close2 = " ) ";
        String join3 = " LEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id ";
        String close3 = " ) ";
        String join4 = " LEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id ";
        String join5 = " LEFT JOIN efaz_company_category AS ccat ON cats.company_cat_id = ccat.category_id ";

        String groupBy = "GROUP BY" +
                "                PROFILE.company_id," +
                "                ccat.category_id;";


        String where = " WHERE ";
        if (!(name.isEmpty() || name.equals(null) || name.equals(""))) {
            nameFlag = 1;
            where = where + " company_name LIKE ? ";
        }

        if (cat != 0) {
            if (nameFlag != 0) {
                where = where + " AND category_id = ? ";
            } else {
                where = where + " category_id = ? ";
            }
            catFlag = 1;
        }

        if (!(area.isEmpty() || area.equals(null) || area.equals(""))) {
            if (nameFlag != 0 || catFlag != 0) {
                where = where + " AND area LIKE ? ";
            } else {
                where = where + " area LIKE ? ";
            }

            areaFlag = 1;
        }

        if (!(city.isEmpty() || city.equals(null) || city.equals(""))) {
            if (nameFlag != 0 || catFlag != 0 || areaFlag != 0) {
                where = where + " AND city LIKE ? ";
            } else {
                where = where + " city LIKE ? ";
            }

            cityFlag = 1;
        }

        if (view != 0) {


            if (nameFlag != 0 || catFlag != 0 || areaFlag != 0 || cityFlag != 0) {
                where = where + " AND PROFILE.company_id = offer.offer_company_id ";
            } else {
                where = where + " PROFILE.company_id = offer.offer_company_id ";
            }

            viewFlag = 1;
        }

        String sql = "";
        if (nameFlag == 1 || catFlag == 1 || areaFlag == 1 || cityFlag == 1 || viewFlag == 1) {
            sql = sql + cols + from + join1 + close1 + join2 + close2 + join3 + close3 + join4 + join5 + where + groupBy;
        } else {
            sql = sql + cols + from + join1 + close1 + join2 + close2 + join3 + close3 + join4 + join5 + groupBy;
        }

        List<Object> list = new ArrayList<>();
        if (nameFlag == 1) {
            list.add("%"+name+"%");
        }
        if (catFlag == 1) {
            list.add(cat);
        }
        if (areaFlag == 1) {
            list.add(area);
        }
        if (cityFlag == 1) {
            list.add(city);
        }
//        if (viewFlag == 1) {
//            list.add(view);
//        }
        Object[] obj = list.toArray();


        System.out.println(sql + "  " + obj.length);

        return jdbcTemplate.queryForList(sql, obj);
//        List<Map<String,Object>> maps=jdbcTemplate.queryForList(sql, new Object[]{id});//, new Object[]{id}, (resultSet, i) -> new Object());

    }


    public List<Map<String, Object>> getCompaniesProfilesObject2Pagination2WithFilter(int id, String name, int cat,
                                                                                      String area, String city, int view) {

        int nameFlag = 0;
        int catFlag = 0;
        int areaFlag = 0;
        int cityFlag = 0;
        int viewFlag = 0;

        String cols = "SELECT \n" +
                "    company_id,\n" +
                "    company_name,\n" +
                "    company_logo_image,\n" +
                "    company_address,\n" +
                "    company_link_youtube,\n" +
                "    company_website_url,\n" +
                "    company_lng,\n" +
                "    company_lat,\n" +
                "    company_cover_image,\n" +
                "    company_phone_number,\n" +
                "    COUNT(DISTINCT follow_id) AS follower_count,\n" +
                "    COUNT(DISTINCT offer_id) AS order_count,\n" +
                "    company_desc,\n" +
                "    COUNT(DISTINCT id) AS category_num,\n" +
                "    city,\n" +
                "    area,\n" +
                "    lng,\n" +
                "    lat,\n" +
                "    category_id,\n" +
                "    category_name ";

        String from = "  FROM\n " +
                "    (((efaz_company_profile AS PROFILE ";

        String join1 = " LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.follower_id  ";
        String close1 = " )";
        String join2 = " LEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id ";
        String close2 = " ) ";
        String join3 = " LEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id ";
        String close3 = " ) ";
        String join4 = " LEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id ";
        String join5 = " LEFT JOIN efaz_company_category AS ccat ON cats.company_cat_id = ccat.category_id ";

        String groupBy = " GROUP BY" +
                "                PROFILE.company_id, " +
                "                ccat.category_id; ";


        String where = " WHERE\n" +
                "    company_id != ? ";
        if (!(name.isEmpty() || name.equals(null) || name.equals(""))) {
            nameFlag = 1;
            where = where + " AND company_name LIKE ? ";
        }

        if (cat != 0) {
            if (nameFlag != 0) {
                where = where + " AND category_id = ? ";
            } else {
                where = where + " AND category_id = ? ";
            }
            catFlag = 1;
        }

        if (!(area.isEmpty() || area.equals(null) || area.equals(""))) {
            if (nameFlag != 0 || catFlag != 0) {
                where = where + " AND area LIKE ? ";
            } else {
                where = where + " AND area LIKE ? ";
            }

            areaFlag = 1;
        }

        if (!(city.isEmpty() || city.equals(null) || city.equals(""))) {
            if (nameFlag != 0 || catFlag != 0 || areaFlag != 0) {
                where = where + " AND city LIKE ? ";
            } else {
                where = where + " AND city LIKE ? ";
            }

            cityFlag = 1;
        }

        if (view != 0) {


            if (nameFlag != 0 || catFlag != 0 || areaFlag != 0 || cityFlag != 0) {
                where = where + " AND PROFILE.company_id = offer.offer_company_id ";
            } else {
                where = where + " AND PROFILE.company_id = offer.offer_company_id ";
            }

            viewFlag = 1;
        }

        String sql = "";
        if (nameFlag == 1 || catFlag == 1 || areaFlag == 1 || cityFlag == 1 || viewFlag == 1) {
            sql = sql + cols + from + join1 + close1 + join2 + close2 + join3 + close3 + join4 + join5 + where + groupBy;
        } else {
            sql = sql + cols + from + join1 + close1 + join2 + close2 + join3 + close3 + join4 + join5 + where + groupBy;
        }

        List<Object> list = new ArrayList<>();
        list.add(id);
        if (nameFlag == 1) {
            list.add("%"+name+"%");
        }
        if (catFlag == 1) {
            list.add(cat);
        }
        if (areaFlag == 1) {
            list.add(area);
        }
        if (cityFlag == 1) {
            list.add(city);
        }
//        if (viewFlag == 1) {
//            list.add(view);
//        }
        Object[] obj = list.toArray();


        System.out.println(sql + "  " + obj.length);

        return jdbcTemplate.queryForList(sql, obj);
    }


    public List<Map<String, Object>> getCompaniesProfilesObject2Pagination2(int id, int flag_ar) {

        String sql = "";
        if ( flag_ar == 0){
            sql = "SELECT\n" +
                    "\tcompany_id,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcompany_address,\n" +
                    "\tcompany_link_youtube,\n" +
                    "\tcompany_website_url,\n" +
                    "\tcompany_lng,\n" +
                    "\tcompany_lat,\n" +
                    "\tcompany_cover_image,\n" +
                    "\tcompany_phone_number,\n" +
                    "\tcount( DISTINCT follow_id ) AS follower_count,\n" +
                    "\tcount( DISTINCT offer_id ) AS order_count,\n" +
                    "\tcompany_desc,\n" +
                    "\tCOUNT( DISTINCT id ) AS category_num,\n" +
                    "\tcity_name,\n" +
                    "\tarea_name, lng, lat, \n" +
                    "\tcategory_id,\n" +
                    "\tcategory_name \n" +
                    "FROM\n" +
                    "\t(\n" +
                    "\t\t(\n" +
                    "\t\t\t( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.follower_id )\n" +
                    "\t\t\tLEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id \n" +
                    "\t\t)\n" +
                    "\t\tLEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id \n" +
                    "\t)\n" +
                    "\tLEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id\n" +
                    "\tLEFT JOIN efaz_company_category AS ccat ON cats.company_cat_id = ccat.category_id  " +
                    " LEFT JOIN area AS a ON pc.area = a.area_id " +
                    " LEFT JOIN city AS c ON pc.city = c.city_id " +
                    " WHERE company_id != ? " +
                    "GROUP BY\n" +
                    "\tPROFILE.company_id,\n" +
                    "\tccat.category_id;";
        }else {
            sql = "SELECT\n" +
                    "\tcompany_id,\n" +
                    "\tcompany_name,\n" +
                    "\tcompany_logo_image,\n" +
                    "\tcompany_address,\n" +
                    "\tcompany_link_youtube,\n" +
                    "\tcompany_website_url,\n" +
                    "\tcompany_lng,\n" +
                    "\tcompany_lat,\n" +
                    "\tcompany_cover_image,\n" +
                    "\tcompany_phone_number,\n" +
                    "\tcount( DISTINCT follow_id ) AS follower_count,\n" +
                    "\tcount( DISTINCT offer_id ) AS order_count,\n" +
                    "\tcompany_desc,\n" +
                    "\tCOUNT( DISTINCT id ) AS category_num,\n" +
                    "\tcity_name_ar AS city_name,\n" +
                    "\tarea_name_ar AS area_name, lng, lat, \n" +
                    "\tcategory_id,\n" +
                    "\tcategory_name \n" +
                    "FROM\n" +
                    "\t(\n" +
                    "\t\t(\n" +
                    "\t\t\t( efaz_company_profile AS PROFILE LEFT JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.follower_id )\n" +
                    "\t\t\tLEFT JOIN efaz_company_offer AS offer ON PROFILE.company_id = offer.offer_company_id \n" +
                    "\t\t)\n" +
                    "\t\tLEFT JOIN efaz_company_profile_cats AS cats ON PROFILE.company_id = cats.company_profile_id \n" +
                    "\t)\n" +
                    "\tLEFT JOIN efaz_login AS pc ON PROFILE.company_id = pc.login_id\n" +
                    "\tLEFT JOIN efaz_company_category AS ccat ON cats.company_cat_id = ccat.category_id  " +
                    " LEFT JOIN area AS a ON pc.area = a.area_id " +
                    " LEFT JOIN city AS c ON pc.city = c.city_id " +
                    " WHERE company_id != ? " +
                    "GROUP BY\n" +
                    "\tPROFILE.company_id,\n" +
                    "\tccat.category_id;";
        }



        return jdbcTemplate.queryForList(sql, id);
//        List<Map<String,Object>> maps=jdbcTemplate.queryForList(sql, new Object[]{id});//, new Object[]{id}, (resultSet, i) -> new Object());

    }


    public int isCategoryExist(String catName) {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM efaz_company_category WHERE  category_name=?;",
                Integer.class, catName);
    }


}
