package com.taj.model.Pagination;

import com.taj.model.new_school_history.SchoolRequestHistoryDtoDTO;

import java.util.List;

/**
 * Created by User on 10/22/2018.
 */
public class SchoolRequestHistoryDtoDTOPagination {

    private int itemsNum;
    private int pageNum;
    private List<SchoolRequestHistoryDtoDTO> requests;

    public SchoolRequestHistoryDtoDTOPagination(int itemsNum, int pageNum, List<SchoolRequestHistoryDtoDTO> requests) {
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
        this.requests = requests;
    }

    public SchoolRequestHistoryDtoDTOPagination() {
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

    public List<SchoolRequestHistoryDtoDTO> getRequests() {
        return requests;
    }

    public void setRequests(List<SchoolRequestHistoryDtoDTO> requests) {
        this.requests = requests;
    }
}
