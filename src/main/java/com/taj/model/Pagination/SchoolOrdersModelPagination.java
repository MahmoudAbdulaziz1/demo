package com.taj.model.Pagination;

import com.taj.model.school_history_admin_dashboard.SchoolOrdersModel;

import java.util.List;

/**
 * Created by User on 10/22/2018.
 */
public class SchoolOrdersModelPagination {

    private int itemsNum;
    private int pageNum;
    private List<SchoolOrdersModel> orders;

    public SchoolOrdersModelPagination(int itemsNum, int pageNum, List<SchoolOrdersModel> orders) {
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
        this.orders = orders;
    }

    public SchoolOrdersModelPagination() {
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

    public List<SchoolOrdersModel> getOrders() {
        return orders;
    }

    public void setOrders(List<SchoolOrdersModel> orders) {
        this.orders = orders;
    }
}
