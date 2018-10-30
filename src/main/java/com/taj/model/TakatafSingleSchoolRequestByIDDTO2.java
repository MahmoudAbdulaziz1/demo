package com.taj.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by User on 9/12/2018.
 */
public class TakatafSingleSchoolRequestByIDDTO2 {


    private int tender_id;
    @NotNull
    @NotBlank
    @NotEmpty
    @Size(max = 450, min = 1, message = "title should have at least 1 characters")
    private String tender_title;
    @NotNull
    @NotBlank
    @NotEmpty
    @Size(max = 450, min = 1, message = "explain should have at least 1 characters")
    private String tender_explain;

    private @NotNull long tender_display_date;
    @NotNull
    private long tender_expire_date;
    private int response_count;
    private int id;
    private String category_name;
    private int count;
    private String tender_logo;

    public TakatafSingleSchoolRequestByIDDTO2(int tender_id, @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "title should have at least 1 characters") String tender_title, @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "explain should have at least 1 characters") String tender_explain,
                                              @NotNull long tender_display_date, @NotNull long tender_expire_date, int response_count,
                                              int id, String category_name, int count, String tender_logo) {
        this.tender_id = tender_id;
        this.tender_title = tender_title;
        this.tender_explain = tender_explain;
        this.tender_display_date = tender_display_date;
        this.tender_expire_date = tender_expire_date;
        this.response_count = response_count;
        this.id = id;
        this.category_name = category_name;
        this.count = count;
        this.tender_logo = tender_logo;
    }

    public TakatafSingleSchoolRequestByIDDTO2(@NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "title should have at least 1 characters") String tender_title, @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "explain should have at least 1 characters") String tender_explain, @NotNull long tender_display_date,
                                              @NotNull long tender_expire_date, int response_count, int id, String category_name, int count, String tender_logo) {
        this.tender_title = tender_title;
        this.tender_explain = tender_explain;
        this.tender_display_date = tender_display_date;
        this.tender_expire_date = tender_expire_date;
        this.response_count = response_count;
        this.id = id;
        this.category_name = category_name;
        this.count = count;
        this.tender_logo = tender_logo;
    }

    public TakatafSingleSchoolRequestByIDDTO2() {
    }

    public int getTender_id() {
        return tender_id;
    }

    public void setTender_id(int tender_id) {
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

    public int getResponse_count() {
        return response_count;
    }

    public void setResponse_count(int response_count) {
        this.response_count = response_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTender_logo() {
        return tender_logo;
    }

    public void setTender_logo(String tender_logo) {
        this.tender_logo = tender_logo;
    }
}
