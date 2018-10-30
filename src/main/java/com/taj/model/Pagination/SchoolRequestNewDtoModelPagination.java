package com.taj.model.Pagination;

import com.taj.model.new_school_request.SchoolRequestNewDtoModel;

import java.util.List;

/**
 * Created by User on 10/21/2018.
 */
public class SchoolRequestNewDtoModelPagination {

    private int itemsNum;
    private int pageNum;
    private List<SchoolRequestNewDtoModel> requests;

    public SchoolRequestNewDtoModelPagination(int itemsNum, int pageNum, List<SchoolRequestNewDtoModel> requests) {
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
        this.requests = requests;
    }

    public SchoolRequestNewDtoModelPagination() {
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

    public List<SchoolRequestNewDtoModel> getRequests() {
        return requests;
    }

    public void setRequests(List<SchoolRequestNewDtoModel> requests) {
        this.requests = requests;
    }
}
