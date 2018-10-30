package com.taj.model.Pagination;

import com.taj.model.FollowSchoolProfilesDto;

import java.util.List;

/**
 * Created by User on 10/22/2018.
 */
public class FollowSchoolProfilesDtoPAgination {

    private int itemsNum;
    private int pageNum;
    List<FollowSchoolProfilesDto> schools;

    public FollowSchoolProfilesDtoPAgination(int itemsNum, int pageNum, List<FollowSchoolProfilesDto> schools) {
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
        this.schools = schools;
    }

    public FollowSchoolProfilesDtoPAgination() {
    }

    public int getItemsNum() {
        return itemsNum;
    }

    public void setItemsNum(int itemsNum) {
        this.itemsNum = itemsNum;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<FollowSchoolProfilesDto> getSchools() {
        return schools;
    }

    public void setSchools(List<FollowSchoolProfilesDto> schools) {
        this.schools = schools;
    }
}
