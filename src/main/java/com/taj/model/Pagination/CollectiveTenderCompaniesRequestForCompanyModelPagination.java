package com.taj.model.Pagination;

import com.taj.model.CollectiveTenderCompaniesRequestForCompanyModel;

import java.util.List;

/**
 * Created by User on 10/21/2018.
 */
public class CollectiveTenderCompaniesRequestForCompanyModelPagination {


    private int itemsNum;
    private int pageNum;
    private List<CollectiveTenderCompaniesRequestForCompanyModel> tenders;


    public CollectiveTenderCompaniesRequestForCompanyModelPagination() {
    }

    public CollectiveTenderCompaniesRequestForCompanyModelPagination(int itemsNum, int pageNum, List<CollectiveTenderCompaniesRequestForCompanyModel> tenders) {
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
        this.tenders = tenders;
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

    public List<CollectiveTenderCompaniesRequestForCompanyModel> getTenders() {
        return tenders;
    }

    public void setTenders(List<CollectiveTenderCompaniesRequestForCompanyModel> tenders) {
        this.tenders = tenders;
    }
}
