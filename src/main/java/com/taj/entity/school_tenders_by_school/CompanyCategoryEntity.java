package com.taj.entity.school_tenders_by_school;

import javax.persistence.*;

/**
 * Created by User on 10/16/2018.
 */
@Entity
@Table(name = "efaz_company_category")
public class CompanyCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int category_id;
    private String category_name;

    public CompanyCategoryEntity(int category_id, String category_name) {
        this.category_id = category_id;
        this.category_name = category_name;
    }

    public CompanyCategoryEntity(String category_name) {
        this.category_name = category_name;
    }

    public CompanyCategoryEntity() {
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
