package com.taj.model.Pagination;

import com.taj.model.CategoryNameDto;
import com.taj.model.TenderRequestCategories;
import com.taj.model.TenderRequestSchools;

import java.util.List;
import java.util.Map;

/**
 * Created by User on 10/21/2018.
 */
public class TenderRequestTendersPagination {

    List<CategoryNameDto> category;
    Map<TenderRequestSchools, List<TenderRequestCategories>> res;
    private long tender_id;
    private String tender_title;
    private String tender_explain;
    private long tender_display_date;
    private long tender_expire_date;
    private long response_count;
    private List<TenderRequestSchools> schools;
    private int itemsNum;
    private int pageNum;

    public TenderRequestTendersPagination(long tender_id, String tender_title, String tender_explain, long tender_display_date, long tender_expire_date,
                                long response_count, List<CategoryNameDto> category, Map<TenderRequestSchools, List<TenderRequestCategories>> res,
                                List<TenderRequestSchools> schools, int itemsNum, int pageNum) {
        this.tender_id = tender_id;
        this.tender_title = tender_title;
        this.tender_explain = tender_explain;
        this.tender_display_date = tender_display_date;
        this.tender_expire_date = tender_expire_date;
        this.response_count = response_count;
        this.category = category;
        this.res = res;
        this.schools = schools;
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
    }

    public TenderRequestTendersPagination(long tender_id, String tender_title, String tender_explain,
                                long tender_display_date, long tender_expire_date, long response_count,
                                List<CategoryNameDto> category, List<TenderRequestSchools> schools, int itemsNum, int pageNum) {
        this.tender_id = tender_id;
        this.tender_title = tender_title;
        this.tender_explain = tender_explain;
        this.tender_display_date = tender_display_date;
        this.tender_expire_date = tender_expire_date;
        this.response_count = response_count;
        this.category = category;
        this.schools = schools;
        this.itemsNum = itemsNum;
        this.pageNum = pageNum;
    }

    public TenderRequestTendersPagination() {
    }

    public List<CategoryNameDto> getCategory() {
        return category;
    }

    public void setCategory(List<CategoryNameDto> category) {
        this.category = category;
    }

    public Map<TenderRequestSchools, List<TenderRequestCategories>> getRes() {
        return res;
    }

    public void setRes(Map<TenderRequestSchools, List<TenderRequestCategories>> res) {
        this.res = res;
    }

    public long getTender_id() {
        return tender_id;
    }

    public void setTender_id(long tender_id) {
        this.tender_id = tender_id;
    }

    public String getTender_title() {
        return tender_title;
    }

    public void setTender_title(String tender_title) {
        this.tender_title = tender_title;
    }

    public String getTender_explain() {
        return tender_explain;
    }

    public void setTender_explain(String tender_explain) {
        this.tender_explain = tender_explain;
    }

    public long getTender_display_date() {
        return tender_display_date;
    }

    public void setTender_display_date(long tender_display_date) {
        this.tender_display_date = tender_display_date;
    }

    public long getTender_expire_date() {
        return tender_expire_date;
    }

    public void setTender_expire_date(long tender_expire_date) {
        this.tender_expire_date = tender_expire_date;
    }

    public long getResponse_count() {
        return response_count;
    }

    public void setResponse_count(long response_count) {
        this.response_count = response_count;
    }

    public List<TenderRequestSchools> getSchools() {
        return schools;
    }

    public void setSchools(List<TenderRequestSchools> schools) {
        this.schools = schools;
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
