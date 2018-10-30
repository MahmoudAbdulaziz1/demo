package com.taj.model.Pagination;

import com.taj.model.NewRegisterModel;

import java.util.List;

/**
 * Created by User on 10/21/2018.
 */
public class NewRegisterModelPagination {

    private int itemsNum;
    private int pageNum;
    private List<NewRegisterModel> usrDate;

    public NewRegisterModelPagination() {
    }

    public NewRegisterModelPagination(int itemsNum, int pageNum, List<NewRegisterModel> usrDate) {
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
        this.usrDate = usrDate;
    }

    public int getItemsNum() {
        return itemsNum;
    }

    public void setItemsNum(int itemsNum) {
        this.itemsNum = itemsNum;
    }

    public List<NewRegisterModel> getUsrDate() {
        return usrDate;
    }

    public void setUsrDate(List<NewRegisterModel> usrDate) {
        this.usrDate = usrDate;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}
