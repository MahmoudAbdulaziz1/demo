package com.taj.model.Pagination;

import com.taj.model.CollectiveTenderDetailsForCompanyModel;

import java.util.List;

/**
 * Created by User on 10/21/2018.
 */
public class CollectiveTenderDetailsForCompanyModelPagination {

    private int itemsNum;
    private int pageNum;
    private List<CollectiveTenderDetailsForCompanyModel> tenders;

    public CollectiveTenderDetailsForCompanyModelPagination(int itemsNum, int pageNum, List<CollectiveTenderDetailsForCompanyModel> tenders) {
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
        this.tenders = tenders;
    }

    public CollectiveTenderDetailsForCompanyModelPagination() {
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

    public List<CollectiveTenderDetailsForCompanyModel> getTenders() {
        return tenders;
    }

    public void setTenders(List<CollectiveTenderDetailsForCompanyModel> tenders) {
        this.tenders = tenders;
    }
}
