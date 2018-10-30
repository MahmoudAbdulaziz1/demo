package com.taj.repository;

import com.taj.model.*;
import com.taj.model.Pagination.FollowCompanyProfilesDtoPagination;
import com.taj.model.Pagination.FollowSchoolProfilesDtoPAgination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 7/19/2018.
 */
@Repository
public class SchoolFollowCompanyRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    SchoolProfileRepo repo;

    public boolean isExist(int id) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM efaz_organization_following WHERE follow_id=?;",
                Integer.class, id);
        return cnt != null && cnt > 0;
    }

    public boolean isRecordExist(int o_id, int f_id) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM efaz_organization_following WHERE organization_id=? AND follower_id=?;",
                Integer.class, o_id, f_id);
        return cnt != null && cnt > 0;
    }

    public boolean isExistFollwing(int id) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM efaz_login WHERE login_id=?;",
                Integer.class, id);
        return cnt != null && cnt > 0;
    }

    public boolean isExistFollwer(int id) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM efaz_login WHERE login_id=?;",
                Integer.class, id);
        return cnt != null && cnt > 0;
    }

    public int addFollower(int organization_id, int follower_id) {
        if (isRecordExist(organization_id, follower_id)) {
            return 2;
        } else {

            return jdbcTemplate.update("INSERT INTO efaz_organization_following VALUES (?,?,?)", null, organization_id, follower_id);
        }


    }

    public List<SchoolFollowCompany> getAllFollowers() {
        return jdbcTemplate.query("SELECT * FROM efaz_organization_following;",
                ((resultSet, i) -> new SchoolFollowCompany(resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3))));
    }

    public SchoolFollowCompany getById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM efaz_organization_following WHERE follow_id=?;", new Object[]{id},
                ((resultSet, i) -> new SchoolFollowCompany(resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3))));
    }

    public List<SchoolFollowCompany> getAllSchoolFollowing(int follower_id) {
        return jdbcTemplate.query("SELECT * FROM efaz_organization_following WHERE follower_id=?;", new Object[]{follower_id},
                ((resultSet, i) -> new SchoolFollowCompany(resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3))));
    }

    public List<SchoolProfileModel> getCompanyAllFollowers(int organization_id) {
        List<SchoolFollowCompany> list = jdbcTemplate.query("SELECT * FROM efaz_organization_following WHERE organization_id=?;", new Object[]{organization_id},
                ((resultSet, i) -> new SchoolFollowCompany(resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3))));
        List<SchoolProfileModel> school_data = new ArrayList<>();
        for (SchoolFollowCompany model : list) {
            int id = model.getFollower_id();
            school_data.add(repo.getSchoolProfile(id));
        }

        return school_data;
    }


    public List<SchoolProfileModel> getCompanyAllFollowersNew(int organization_id) {
        String sql = "SELECT school_id, school_name, school_logo_image, school_address, school_service_desc, school_link_youtube, school_website_url," +
                " school_lng, school_lat, school_cover_image, school_phone_number FROM (efaz_school_profile AS profile INNER JOIN " +
                " efaz_organization_following AS follow ON profile.school_id = follow.follower_id) Where follow.organization_id = ?;";

        return jdbcTemplate.query(sql, new Object[]{organization_id}, (resultSet, i) -> new SchoolProfileModel(resultSet.getInt(1), resultSet.getString(2),
                resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
                resultSet.getString(6), resultSet.getString(7), resultSet.getFloat(8), resultSet.getFloat(9), resultSet.getString(10), resultSet.getString(11)));
    }


//, company_address, company_category_id, company_link_youtube, company_website_url, company_lng, company_lat, company_cover_image, " +
    //"company_phone_number

    public List<CompanyFollowSch0oolDto> getSchoolAllFollowersNew(int organization_id) {

        String sql = "SELECT  company_name, company_logo_image FROM (efaz_company_profile AS profile INNER JOIN " +
                " efaz_organization_following AS follow ON profile.company_id = follow.follower_id) Where follow.organization_id = ?;";

        return jdbcTemplate.query(sql, new Object[]{organization_id}, (resultSet, i) -> new CompanyFollowSch0oolDto(resultSet.getString(1), resultSet.getBytes(2)));

    }


    public int getFollowersCount(int organization_id) {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM efaz_organization_following WHERE organization_id=?;",
                Integer.class, organization_id);

    }


    public int updateSchoolFollowCompany(int follow_id, int organization_id, int follower_id) {
        return jdbcTemplate.update("UPDATE efaz_organization_following SET follower_id=?, organization_id=? WHERE follow_id=?", follower_id, organization_id, follow_id);
    }

    public int deleteSchoolFollowCompany(int follow_id) {
        return jdbcTemplate.update("DELETE FROM efaz_organization_following WHERE follow_id=?", follow_id);
    }


    public int deleteSchoolFollowCompanyByIds(int organization_id, int follower_id) {
        return jdbcTemplate.update("DELETE FROM efaz_organization_following WHERE organization_id=? AND follower_id=?", organization_id, follower_id);
    }


    public int getId(int id1, int id2) {
        return jdbcTemplate.queryForObject("SELECT follow_id FROM efaz_organization_following WHERE organization_id=? AND follower_id=?;",
                Integer.class, id1, id2);

    }

    public List<FollowSchoolProfilesDto> getSchoolsWithFollow(int companyId) {
        String sql = "SELECT school_id, school_name, school_logo_image, IF (profile.school_id = follow.organization_id AND follow.follower_id=?, true, false) " +
                "AS is_follow FROM efaz_school_profile AS profile Left JOIN efaz_organization_following " +
                "AS follow ON profile.school_id = follow.organization_id AND follow.follower_id=?;";


        List<FollowSchoolProfilesDto> list = jdbcTemplate.query(sql, new Object[]{companyId, companyId},
                ((resultSet, i) -> new FollowSchoolProfilesDto(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getBoolean(4))));

        return list;

    }

    public int getSchoolsFollowCompanyPaginationCount(int companyId) {
        String sql = "SELECT\n" +
                "\tcount(DISTINCT school_id)AS count \n" +
                "FROM\n" +
                "\tefaz_school_profile\n" +
                "\tAS PROFILE INNER JOIN efaz_organization_following AS follow ON PROFILE.school_id = follow.follower_id \n" +
                "\tAND follow.organization_id = ?;";

        return jdbcTemplate.queryForObject(sql, Integer.class, companyId);
    }

    public FollowSchoolProfilesDtoPAgination getSchoolsFollowCompanyPagination(int companyId, int page , int pageSize) {

        int pages = (int) Math.ceil(((float) getSchoolsFollowCompanyPaginationCount(companyId)) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;


        String sql = "SELECT\n" +
                "\tschool_id,\n" +
                "\tschool_name,\n" +
                "\tschool_logo_image,\n" +
                "IF\n" +
                "\t( PROFILE.school_id = follow.follower_id AND follow.organization_id = ?, TRUE, FALSE ) AS is_follow \n" +
                "FROM\n" +
                "\tefaz_school_profile\n" +
                "\tAS PROFILE INNER JOIN efaz_organization_following AS follow ON PROFILE.school_id = follow.follower_id \n" +
                "\tAND follow.organization_id = ? " +
                "  LIMIT ?,?;";

        List<FollowSchoolProfilesDto> list = jdbcTemplate.query(sql, new Object[]{companyId, companyId, limitOffset, pageSize},
                ((resultSet, i) -> new FollowSchoolProfilesDto(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getBoolean(4))));

        return new FollowSchoolProfilesDtoPAgination(getSchoolsFollowCompanyPaginationCount(companyId), pages, list);
    }

    public List<FollowSchoolProfilesDto> getSchoolsFollowCompany(int companyId) {
        String sql = "SELECT\n" +
                "\tschool_id,\n" +
                "\tschool_name,\n" +
                "\tschool_logo_image,\n" +
                "IF\n" +
                "\t( PROFILE.school_id = follow.follower_id AND follow.organization_id = ?, TRUE, FALSE ) AS is_follow \n" +
                "FROM\n" +
                "\tefaz_school_profile\n" +
                "\tAS PROFILE INNER JOIN efaz_organization_following AS follow ON PROFILE.school_id = follow.follower_id \n" +
                "\tAND follow.organization_id = ?;";

        List<FollowSchoolProfilesDto> list = jdbcTemplate.query(sql, new Object[]{companyId, companyId},
                ((resultSet, i) -> new FollowSchoolProfilesDto(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getBoolean(4))));

        return list;
    }


    public int getCompaniesFollowedBySchoolPaginationCount(int schoolId) {
        String sql = "SELECT\n" +
                "\tcount( company_id ) AS count \n" +
                "FROM\n" +
                "\tefaz_company_profile\n" +
                "\tAS PROFILE INNER JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.organization_id \n" +
                "\tAND follow.follower_id = ?;";

        return  jdbcTemplate.queryForObject(sql, Integer.class, schoolId);
    }



    public FollowCompanyProfilesDtoPagination getCompaniesFollowedBySchoolPagination(int schoolId, int page, int pageSize) {

        int pages = (int) Math.ceil(((float) getCompaniesFollowedBySchoolPaginationCount(schoolId)) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;


        String sql = "SELECT\n" +
                "\tcompany_id,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "IF\n" +
                "\t( PROFILE.company_id = follow.organization_id AND follow.follower_id = ?, TRUE, FALSE ) AS is_follow \n" +
                "FROM\n" +
                "\tefaz_company_profile\n" +
                "\tAS PROFILE INNER JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.organization_id \n" +
                "\tAND follow.follower_id = ? LIMIT ?,?;";

        List<FollowCompanyProfilesDto> list = jdbcTemplate.query(sql, new Object[]{schoolId, schoolId, limitOffset, pageSize},
                ((resultSet, i) -> new FollowCompanyProfilesDto(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getBoolean(4))));

        return new FollowCompanyProfilesDtoPagination(getCompaniesFollowedBySchoolPaginationCount(schoolId), page,list);
    }



    public List<FollowCompanyProfilesDto> getCompainesFollowedBySchool(int schoolId) {
        String sql = "SELECT\n" +
                "\tcompany_id,\n" +
                "\tcompany_name,\n" +
                "\tcompany_logo_image,\n" +
                "IF\n" +
                "\t( PROFILE.company_id = follow.organization_id AND follow.follower_id = ?, TRUE, FALSE ) AS is_follow \n" +
                "FROM\n" +
                "\tefaz_company_profile\n" +
                "\tAS PROFILE INNER JOIN efaz_organization_following AS follow ON PROFILE.company_id = follow.organization_id \n" +
                "\tAND follow.follower_id = ?;";

        List<FollowCompanyProfilesDto> list = jdbcTemplate.query(sql, new Object[]{schoolId, schoolId},
                ((resultSet, i) -> new FollowCompanyProfilesDto(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getBoolean(4))));

        return list;
    }







    public List<getCompaniesWithFollowDTo> getCompaniesWithFollow(int schoolId) {
        String sql = "SELECT company_id, company_name, company_logo_image, company_address, category_name, company_link_youtube, company_website_url," +
                " company_lng, company_lat, company_cover_image, company_phone_number, IF (profile.company_id = follow.organization_id AND follow.follower_id=?, true, false) " +
                "AS is_follow FROM ((efaz_company_profile AS profile Left JOIN efaz_organization_following " +
                "AS follow ON profile.company_id = follow.organization_id AND follow.follower_id=?) INNER JOIN efaz_company_category AS cat" +
                " ON profile.company_category_id = cat.category_id) GROUP BY profile.company_id;";


        List<getCompaniesWithFollowDTo> list = jdbcTemplate.query(sql, new Object[]{schoolId, schoolId},
                ((resultSet, i) -> new getCompaniesWithFollowDTo(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getBytes(3), resultSet.getString(4), resultSet.getString(5),
                        resultSet.getString(6), resultSet.getString(7), resultSet.getFloat(8), resultSet.getFloat(9),
                        resultSet.getBytes(10), resultSet.getString(11), resultSet.getBoolean(12))));

        return list;

    }

    public List<GetCompaniesWithFollowANDOffers> getCompaniesWithFollowAndOffers(int schoolId) {
        String sql = "SELECT company_id, company_name, company_logo_image, company_address, category_name, company_link_youtube, company_website_url," +
                "                 company_lng, company_lat, company_cover_image, company_phone_number, " +
                "                 count(distinct fol.follow_id) AS follower_count," +
                "                 count(distinct offer.offer_id) AS offer_count," +
                "                 IF (profile.company_id = follow.organization_id " +
                "                 AND follow.follower_id=?, true, false) AS is_follow" +
                "                 FROM ((efaz_company_profile AS profile" +
                "                 Left JOIN efaz_organization_following" +
                "                AS follow ON profile.company_id = follow.organization_id AND follow.follower_id=?" +
                "                 INNER JOIN efaz_company_category AS cat" +
                "                 ON profile.company_category_id = cat.category_id" +
                "                 LEFT JOIN efaz_company_offer AS offer ON profile.company_id = offer.offer_company_id)" +
                "                 LEFT JOIN efaz_organization_following AS fol ON profile.company_id = fol.organization_id)" +
                "                 GROUP BY profile.company_id;";


        List<GetCompaniesWithFollowANDOffers> list = jdbcTemplate.query(sql, new Object[]{schoolId, schoolId},
                ((resultSet, i) -> new GetCompaniesWithFollowANDOffers(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getBytes(3), resultSet.getString(4), resultSet.getString(5),
                        resultSet.getString(6), resultSet.getString(7), resultSet.getFloat(8), resultSet.getFloat(9),
                        resultSet.getBytes(10), resultSet.getString(11), resultSet.getBoolean(12), resultSet.getInt(13),
                        resultSet.getInt(14))));

        return list;

    }


}
