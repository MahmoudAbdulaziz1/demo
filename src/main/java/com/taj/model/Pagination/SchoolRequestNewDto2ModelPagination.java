package com.taj.model.Pagination;

import com.taj.model.new_school_request.SchoolRequestNewDto2Model;

import java.util.List;

/**
 * Created by User on 10/21/2018.
 */
public class SchoolRequestNewDto2ModelPagination {

    private int itemsNum;
    private int pageNum;
    private List<SchoolRequestNewDto2Model> requests;

    public SchoolRequestNewDto2ModelPagination(int itemsNum, int pageNum, List<SchoolRequestNewDto2Model> requests) {
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
        this.requests = requests;
    }

    public SchoolRequestNewDto2ModelPagination() {
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

    public List<SchoolRequestNewDto2Model> getRequests() {
        return requests;
    }

    public void setRequests(List<SchoolRequestNewDto2Model> requests) {
        this.requests = requests;
    }
}
