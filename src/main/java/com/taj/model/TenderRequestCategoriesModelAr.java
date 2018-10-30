package com.taj.model;

/**
 * Created by User on 10/30/2018.
 */
public class TenderRequestCategoriesModelAr {


    private long id;
    private String category_name_ar;
    private long count;

    public TenderRequestCategoriesModelAr(long id, String category_name_ar, long count) {
        this.id = id;
        this.category_name_ar = category_name_ar;
        this.count = count;
    }

    public TenderRequestCategoriesModelAr() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory_name_ar() {
        return category_name_ar;
    }

    public void setCategory_name_ar(String category_name_ar) {
        this.category_name_ar = category_name_ar;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
