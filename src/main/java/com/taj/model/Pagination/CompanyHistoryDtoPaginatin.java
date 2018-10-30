package com.taj.model.Pagination;

import com.taj.model.new_company_history.CompanyHistoryDto;

import java.util.List;

/**
 * Created by User on 10/22/2018.
 */
public class CompanyHistoryDtoPaginatin {

    private int itemsNum;
    private int pageNum;
    private List<CompanyHistoryDto> offers;

    public CompanyHistoryDtoPaginatin(int itemsNum, int pageNum, List<CompanyHistoryDto> offers) {
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
        this.offers = offers;
    }

    public CompanyHistoryDtoPaginatin() {
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

    public List<CompanyHistoryDto> getOffers() {
        return offers;
    }

    public void setOffers(List<CompanyHistoryDto> offers) {
        this.offers = offers;
    }
}
