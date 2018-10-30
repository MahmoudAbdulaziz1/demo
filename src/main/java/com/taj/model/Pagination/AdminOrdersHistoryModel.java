package com.taj.model.Pagination;

import com.taj.model.AdminHistoryOrdersModel;

import java.util.List;

/**
 * Created by User on 10/17/2018.
 */
public class AdminOrdersHistoryModel {

    private List<AdminHistoryOrdersModel> orders;
    private int allResultCount;

    public AdminOrdersHistoryModel(List<AdminHistoryOrdersModel> orders, int allResultCount) {
        this.orders = orders;
        this.allResultCount = allResultCount;
    }

    public AdminOrdersHistoryModel() {
    }

    public List<AdminHistoryOrdersModel> getOrders() {
        return orders;
    }

    public void setOrders(List<AdminHistoryOrdersModel> orders) {
        this.orders = orders;
    }

    public int getAllResultCount() {
        return allResultCount;
    }

    public void setAllResultCount(int allResultCount) {
        this.allResultCount = allResultCount;
    }
}
