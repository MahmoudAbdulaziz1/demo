package com.taj.model.Pagination;

import com.taj.model.AdminOrdersModel;

import java.util.List;

/**
 * Created by User on 10/21/2018.
 */
public class AdminOrdersModelPagination {
    private int pageNum;
    private int itemsNum;
    private List<AdminOrdersModel> orders;

    public AdminOrdersModelPagination() {
    }

    public AdminOrdersModelPagination(int pageNum, int itemsNum, List<AdminOrdersModel> orders) {
        this.pageNum = pageNum;
        this.itemsNum = itemsNum;
        this.orders = orders;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getItemsNum() {
        return itemsNum;
    }

    public void setItemsNum(int itemsNum) {
        this.itemsNum = itemsNum;
    }

    public List<AdminOrdersModel> getOrders() {
        return orders;
    }

    public void setOrders(List<AdminOrdersModel> orders) {
        this.orders = orders;
    }
}
