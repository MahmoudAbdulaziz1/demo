package com.taj.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by User on 10/30/2018.
 */
public class CategoryModelEnglish {


    private int category_id;
    @NotNull
    @NotBlank
    @NotEmpty
    @Size(max = 450, min = 1, message = "category_name should have at least 1 characters")
    private String category_name;

    public CategoryModelEnglish(int category_id, @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "category_name should have at least 1 characters") String category_name) {
        this.category_id = category_id;
        this.category_name = category_name;
    }

    public CategoryModelEnglish() {
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
