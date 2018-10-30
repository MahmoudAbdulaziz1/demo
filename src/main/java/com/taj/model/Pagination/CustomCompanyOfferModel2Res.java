package com.taj.model.Pagination;

import com.taj.model.CustomeCompanyOfferModel2;

import java.util.List;

/**
 * Created by User on 10/18/2018.
 */
public class CustomCompanyOfferModel2Res {

    private String status;
    private int pageNum;
    private List<CustomeCompanyOfferModel2> offers;

    public CustomCompanyOfferModel2Res(String status, int pageNum, List<CustomeCompanyOfferModel2> offers) {
        this.status = status;
        this.pageNum = pageNum;
        this.offers = offers;
    }

    public CustomCompanyOfferModel2Res() {
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<CustomeCompanyOfferModel2> getOffers() {
        return offers;
    }

    public void setOffers(List<CustomeCompanyOfferModel2> offers) {
        this.offers = offers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
