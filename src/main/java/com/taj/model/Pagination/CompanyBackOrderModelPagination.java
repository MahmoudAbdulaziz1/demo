package com.taj.model.Pagination;

import com.taj.model.CompanyBackOrderModel;

import java.util.List;

/**
 * Created by User on 10/22/2018.
 */
public class CompanyBackOrderModelPagination {

    private int itemsNum;
    private int pageNum;
    private List<CompanyBackOrderModel> orders;

    public CompanyBackOrderModelPagination(int itemsNum, int pageNum, List<CompanyBackOrderModel> orders) {
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
        this.orders = orders;
    }

    public CompanyBackOrderModelPagination() {
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

    public List<CompanyBackOrderModel> getOrders() {
        return orders;
    }

    public void setOrders(List<CompanyBackOrderModel> orders) {
        this.orders = orders;
    }
}
