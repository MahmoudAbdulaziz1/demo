package com.taj.model;

import com.taj.controller.test.NotEmptyFields;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by User on 8/19/2018.
 */
public class TakatafTenderNewModel {

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
    private long tender_company_display_date;
    private long tender_company_expired_date;
    private int response_count;
    @NotNull
    @NotEmpty
    @NotEmptyFields
    private List<TakatfTenderCategoryPOJO> cats;
    private int flag_ar;

    public TakatafTenderNewModel(int tender_id,
                                 @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "title should have at least 1 characters") String tender_title,
                                 @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "explain should have at least 1 characters") String tender_explain,
                                 @NotNull long tender_display_date, @NotNull long tender_expire_date,
                                 long tender_company_display_date, long tender_company_expired_date, int response_count,
                                 @NotNull @NotEmpty List<TakatfTenderCategoryPOJO> cats, int flag_ar) {
        this.tender_id = tender_id;
        this.tender_title = tender_title;
        this.tender_explain = tender_explain;
        this.tender_display_date = tender_display_date;
        this.tender_expire_date = tender_expire_date;
        this.tender_company_display_date = tender_company_display_date;
        this.tender_company_expired_date = tender_company_expired_date;
        this.response_count = response_count;
        this.cats = cats;
        this.flag_ar = flag_ar;
    }

    public TakatafTenderNewModel(
            @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "title should have at least 1 characters") String tender_title,
            @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "explain should have at least 1 characters") String tender_explain,
            @NotNull long tender_display_date, @NotNull long tender_expire_date,
            long tender_company_display_date, long tender_company_expired_date, int response_count,
            @NotNull @NotEmpty List<TakatfTenderCategoryPOJO> cats, int flag_ar) {
        this.tender_title = tender_title;
        this.tender_explain = tender_explain;

        this.tender_display_date = tender_display_date;
        this.tender_expire_date = tender_expire_date;
        this.tender_company_display_date = tender_company_display_date;
        this.tender_company_expired_date = tender_company_expired_date;
        this.response_count = response_count;
        this.cats = cats;
        this.flag_ar = flag_ar;
    }

    public TakatafTenderNewModel() {
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

    public int getResponse_count() {
        return response_count;
    }

    public void setResponse_count(int response_count) {
        this.response_count = response_count;
    }

    public List<TakatfTenderCategoryPOJO> getCats() {
        return cats;
    }

    public void setCats(List<TakatfTenderCategoryPOJO> cats) {
        this.cats = cats;
    }

    public int getFlag_ar() {
        return flag_ar;
    }

    public void setFlag_ar(int flag_ar) {
        this.flag_ar = flag_ar;
    }
}
