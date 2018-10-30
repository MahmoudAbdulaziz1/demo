package com.taj.model.Pagination;

import com.taj.model.AdminHistoryOrdersModel;

import java.util.List;

/**
 * Created by User on 10/21/2018.
 */
public class AdminHistoryOrdersModelPagination {
    private int itemsNum;
    private int pageNum;
    private List<AdminHistoryOrdersModel> historyOrders;

    public AdminHistoryOrdersModelPagination(int itemsNum, int pageNum, List<AdminHistoryOrdersModel> historyOrders) {
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
        this.historyOrders = historyOrders;
    }

    public AdminHistoryOrdersModelPagination() {
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

    public List<AdminHistoryOrdersModel> getHistoryOrders() {
        return historyOrders;
    }

    public void setHistoryOrders(List<AdminHistoryOrdersModel> historyOrders) {
        this.historyOrders = historyOrders;
    }
}
