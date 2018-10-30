package com.taj.model;

/**
 * Created by User on 9/3/2018.
 */
public class CollectiveTenderCompaniesRequestForCompanyModel {

    private int response_id;
    private double responsed_cost;
    private int company_id;
    private String company_name;
    private String company_desc;
    private String company_logo_image;
    private long response_date;
    private String response_desc;
    private int is_aproved;


    public CollectiveTenderCompaniesRequestForCompanyModel(int response_id, double responsed_cost, int company_id,
                                                           String company_name, String company_desc, String company_logo_image,
                                                           long response_date, String response_desc, int is_aproved) {
        this.response_id = response_id;
        this.responsed_cost = responsed_cost;
        this.company_id = company_id;
        this.company_name = company_name;
        this.company_desc = company_desc;
        this.company_logo_image = company_logo_image;
        this.response_date = response_date;
        this.response_desc = response_desc;
        this.is_aproved = is_aproved;
    }

    public CollectiveTenderCompaniesRequestForCompanyModel(double responsed_cost, int company_id, String company_name,
                                                           String company_desc, String company_logo_image, long response_date,
                                                           String response_desc, int is_aproved) {
        this.responsed_cost = responsed_cost;
        this.company_id = company_id;
        this.company_name = company_name;
        this.company_desc = company_desc;
        this.company_logo_image = company_logo_image;
        this.response_date = response_date;
        this.response_desc = response_desc;
        this.is_aproved = is_aproved;
    }

    public CollectiveTenderCompaniesRequestForCompanyModel() {
    }

    public int getResponse_id() {
        return response_id;
    }

    public void setResponse_id(int response_id) {
        this.response_id = response_id;
    }

    public double getResponsed_cost() {
        return responsed_cost;
    }

    public void setResponsed_cost(double responsed_cost) {
        this.responsed_cost = responsed_cost;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_desc() {
        return company_desc;
    }

    public void setCompany_desc(String company_desc) {
        this.company_desc = company_desc;
    }

    public String getCompany_logo_image() {
        return company_logo_image;
    }

    public void setCompany_logo_image(String company_logo_image) {
        this.company_logo_image = company_logo_image;
    }

    public long getResponse_date() {
        return response_date;
    }

    public void setResponse_date(long response_date) {
        this.response_date = response_date;
    }

    public String getResponse_desc() {
        return response_desc;
    }

    public void setResponse_desc(String response_desc) {
        this.response_desc = response_desc;
    }

    public int getIs_aproved() {
        return is_aproved;
    }

    public void setIs_aproved(int is_aproved) {
        this.is_aproved = is_aproved;
    }
}
