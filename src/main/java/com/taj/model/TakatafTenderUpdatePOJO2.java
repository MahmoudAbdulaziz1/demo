package com.taj.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by MahmoudAhmed on 10/25/2018.
 */
public class TakatafTenderUpdatePOJO2 {


    private int tender_id;
    private String tender_logo;
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
    private int tender_is_confirmed;
    private int tender_is_available;
    private long tender_company_display_date;
    private long tender_company_expired_date;
    private @NotNull @NotEmpty @NotBlank List<TakatfTenderCategoryPOJO> cats;
    private int flag_ar;

    public TakatafTenderUpdatePOJO2() {
    }

    public TakatafTenderUpdatePOJO2(int tender_id, String tender_logo, @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "title should have at least 1 characters") String tender_title, @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "explain should have at least 1 characters") String tender_explain, @NotNull long tender_display_date, @NotNull long tender_expire_date, int tender_is_confirmed, int tender_is_available,
                                    long tender_company_display_date, long tender_company_expired_date,
                                    @NotNull @NotEmpty @NotBlank List<TakatfTenderCategoryPOJO> cats, int flag_ar) {
        this.tender_id = tender_id;
        this.tender_logo = tender_logo;
        this.tender_title = tender_title;
        this.tender_explain = tender_explain;
        this.tender_display_date = tender_display_date;
        this.tender_expire_date = tender_expire_date;
        this.tender_is_confirmed = tender_is_confirmed;
        this.tender_is_available = tender_is_available;
        this.tender_company_display_date = tender_company_display_date;
        this.tender_company_expired_date = tender_company_expired_date;
        this.flag_ar = flag_ar;
        this.cats = cats;
    }

    public int getTender_id() {
        return tender_id;
    }

    public void setTender_id(int tender_id) {
        this.tender_id = tender_id;
    }

    public String getTender_logo() {
        return tender_logo;
    }

    public void setTender_logo(String tender_logo) {
        this.tender_logo = tender_logo;
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

    public int getTender_is_confirmed() {
        return tender_is_confirmed;
    }

    public void setTender_is_confirmed(int tender_is_confirmed) {
        this.tender_is_confirmed = tender_is_confirmed;
    }

    public int getTender_is_available() {
        return tender_is_available;
    }

    public void setTender_is_available(int tender_is_available) {
        this.tender_is_available = tender_is_available;
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

    public int getFlag_ar() {
        return flag_ar;
    }

    public void setFlag_ar(int flag_ar) {
        this.flag_ar = flag_ar;
    }

    public List<TakatfTenderCategoryPOJO> getCats() {
        return cats;
    }

    public void setCats(List<TakatfTenderCategoryPOJO> cats) {
        this.cats = cats;
    }
}
