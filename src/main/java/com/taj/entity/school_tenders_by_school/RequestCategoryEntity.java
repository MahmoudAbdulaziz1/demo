package com.taj.entity.school_tenders_by_school;

import javax.persistence.*;

/**
 * Created by User on 10/16/2018.
 */
@Entity
@Table(name = "efaz_school_request_category")
public class RequestCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int request_category_id;
    private String request_category_name;

    public RequestCategoryEntity(int request_category_id, String request_category_name) {
        this.request_category_id = request_category_id;
        this.request_category_name = request_category_name;
    }

    public RequestCategoryEntity(String request_category_name) {
        this.request_category_name = request_category_name;
    }

    public RequestCategoryEntity() {
    }

    public int getRequest_category_id() {
        return request_category_id;
    }

    public void setRequest_category_id(int request_category_id) {
        this.request_category_id = request_category_id;
    }

    public String getRequest_category_name() {
        return request_category_name;
    }

    public void setRequest_category_name(String request_category_name) {
        this.request_category_name = request_category_name;
    }
}
