package com.taj.model.Pagination;

import com.taj.model.CategoryNameDto;
import com.taj.model.TenderRequestCategoriesModel;
import com.taj.model.TenderRequestSchoolModel;

import java.util.List;
import java.util.Map;

/**
 * Created by User on 10/21/2018.
 */
public class TenderRequestTenderModel2DtoPagination {

    Map<TenderRequestSchoolModel, List<TenderRequestCategoriesModel>> res;
    private long tender_id;
    private String tender_logo;
    private String tender_title;
    private String tender_explain;
    private long tender_display_date;
    private long tender_expire_date;
    private long tender_company_display_date;
    private long tender_company_expired_date;
    private long response_count;
    private List<CategoryNameDto> category;
    private List<TenderRequestSchoolModel> schools;
    private int itemsNum;
    private int pageNum;


    public TenderRequestTenderModel2DtoPagination(long tender_id, String tender_logo, String tender_title, String tender_explain,
                                                  long tender_display_date, long tender_expire_date, long tender_company_display_date,
                                                  long tender_company_expired_date, long response_count, List<CategoryNameDto> category,
                                                  List<TenderRequestSchoolModel> schools, int itemsNum, int pageNum) {
        this.tender_id = tender_id;
        this.tender_logo = tender_logo;
        this.tender_title = tender_title;
        this.tender_explain = tender_explain;
        this.tender_display_date = tender_display_date;
        this.tender_expire_date = tender_expire_date;
        this.tender_company_display_date = tender_company_display_date;
        this.tender_company_expired_date = tender_company_expired_date;
        this.response_count = response_count;
//        this.res = res;
        this.schools = schools;
        this.category = category;
        this.pageNum = pageNum;
        this.itemsNum = itemsNum;
    }

    public TenderRequestTenderModel2DtoPagination() {
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

    public Map<TenderRequestSchoolModel, List<TenderRequestCategoriesModel>> getRes() {
        return res;
    }

    public void setRes(Map<TenderRequestSchoolModel, List<TenderRequestCategoriesModel>> res) {
        this.res = res;
    }

    public List<TenderRequestSchoolModel> getSchools() {
        return schools;
    }

    public void setSchools(List<TenderRequestSchoolModel> schools) {
        this.schools = schools;
    }

    public List<CategoryNameDto> getCategory() {
        return category;
    }

    public void setCategory(List<CategoryNameDto> category) {
        this.category = category;
    }

    public long getTender_company_display_date() {
        return tender_company_display_date;
    }

    public void setTender_company_display_date(long tender_company_display_date) {
        this.tender_company_display_date = tender_company_display_date;
    }

    public long getTender_company_expired_date() {
        return tender_company_expired_date;
    }

    public void setTender_company_expired_date(long tender_company_expired_date) {
        this.tender_company_expired_date = tender_company_expired_date;
    }

    public String getTender_logo() {
        return tender_logo;
    }

    public void setTender_logo(String tender_logo) {
        this.tender_logo = tender_logo;
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
