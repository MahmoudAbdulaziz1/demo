package com.taj.model.Pagination;

import com.taj.model.FollowCompanyProfilesDto;

import java.util.List;

/**
 * Created by User on 10/22/2018.
 */
public class FollowCompanyProfilesDtoPagination {

    private int itemsNum;
    private int pageNum;
    private List<FollowCompanyProfilesDto> companies;

    public FollowCompanyProfilesDtoPagination(int itemsNum, int pageNum, List<FollowCompanyProfilesDto> companies) {
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
        this.companies = companies;
    }

    public FollowCompanyProfilesDtoPagination() {
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

    public List<FollowCompanyProfilesDto> getCompanies() {
        return companies;
    }

    public void setCompanies(List<FollowCompanyProfilesDto> companies) {
        this.companies = companies;
    }
}
