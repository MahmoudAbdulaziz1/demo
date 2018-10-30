package com.taj.model.Pagination;

import com.taj.model.TakatafMyTenderPageDTO;

import java.util.List;

/**
 * Created by User on 10/21/2018.
 */
public class TakatafMyTenderPageDTOPagination {

    private int itemsNum;
    private int pageNum;
    private List<TakatafMyTenderPageDTO> tenders;

    public TakatafMyTenderPageDTOPagination(int itemsNum, int pageNum, List<TakatafMyTenderPageDTO> tenders) {
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
        this.tenders = tenders;
    }

    public TakatafMyTenderPageDTOPagination() {
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

    public List<TakatafMyTenderPageDTO> getTenders() {
        return tenders;
    }

    public void setTenders(List<TakatafMyTenderPageDTO> tenders) {
        this.tenders = tenders;
    }
}
