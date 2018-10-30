package com.taj.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Created by User on 8/27/2018.
 */
public class TakatfTenderCategoryPOJO {
    @NotNull
    @NotEmpty
    private String category_name;

    public TakatfTenderCategoryPOJO(String category_name) {
        this.category_name = category_name;
    }

    public TakatfTenderCategoryPOJO() {
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
