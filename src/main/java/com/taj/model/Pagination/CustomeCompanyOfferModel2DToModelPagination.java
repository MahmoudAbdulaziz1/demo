package com.taj.model.Pagination;

import com.taj.model.company_offer_response_count.CustomeCompanyOfferModel2DToModel;

import java.util.List;

/**
 * Created by User on 10/22/2018.
 */
public class CustomeCompanyOfferModel2DToModelPagination {

    private String status;
    private int itemsNum;
    private int pageNum;
    private List<CustomeCompanyOfferModel2DToModel> offers;

    public CustomeCompanyOfferModel2DToModelPagination(String status, int itemsNum, int pageNum, List<CustomeCompanyOfferModel2DToModel> offers) {
        this.status = status;
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
        this.offers = offers;
    }

    public CustomeCompanyOfferModel2DToModelPagination() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CustomeCompanyOfferModel2DToModel> getOffers() {
        return offers;
    }

    public void setOffers(List<CustomeCompanyOfferModel2DToModel> offers) {
        this.offers = offers;
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

}
