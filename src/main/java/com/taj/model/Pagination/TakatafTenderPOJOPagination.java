package com.taj.model.Pagination;

import com.taj.model.TakatafTenderPOJO;

import java.util.List;

/**
 * Created by User on 10/21/2018.
 */
public class TakatafTenderPOJOPagination {

    private int itemsNum;
    private List<TakatafTenderPOJO> tenders;

    public TakatafTenderPOJOPagination() {
    }

    public TakatafTenderPOJOPagination(int itemsNum, List<TakatafTenderPOJO> tenders) {
        this.itemsNum = itemsNum;
        this.tenders = tenders;
    }

    public int getItemsNum() {
        return itemsNum;
    }

    public void setItemsNum(int pageNum) {
        this.itemsNum = pageNum;
    }

    public List<TakatafTenderPOJO> getTenders() {
        return tenders;
    }

    public void setTenders(List<TakatafTenderPOJO> tenders) {
        this.tenders = tenders;
    }
}
