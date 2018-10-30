package com.taj.model;

import org.hibernate.annotations.ManyToAny;

import javax.validation.constraints.*;

/**
 * Created by Taj 51 on 6/11/2018.
 */
public class LoginDetailsModel {

    @Min(1)
    private int details_id;
    @Min(1)
    private int login_id;
    @NotNull
    @Min(0)
    private int is_school;
    @NotNull
    @NotBlank
    @NotEmpty
    @Size(max = 450, min = 1, message="category_name should have at least 1 characters")
    private String lgoin_time;
    @NotNull
    @NotBlank
    @NotEmpty
    @Size(max = 450, min = 1, message="category_name should have at least 1 characters")
    private String ip_address;
    @Min(0)
    private int is_mobill;

    public LoginDetailsModel(int details_id, int login_id, int is_school, String lgoin_time, String ip_address, int is_mobill) {
        this.details_id = details_id;
        this.login_id = login_id;
        this.is_school = is_school;
        this.lgoin_time = lgoin_time;
        this.ip_address = ip_address;
        this.is_mobill = is_mobill;
    }


    public LoginDetailsModel(int login_id, int is_school, String lgoin_time, String ip_address, int is_mobill) {
        this.login_id = login_id;
        this.is_school = is_school;
        this.lgoin_time = lgoin_time;
        this.ip_address = ip_address;
        this.is_mobill = is_mobill;
    }

    public LoginDetailsModel() {
    }

    public int getDetails_id() {
        return details_id;
    }

    public void setDetails_id(int details_id) {
        this.details_id = details_id;
    }

    public int getLogin_id() {
        return login_id;
    }

    public void setLogin_id(int login_id) {
        this.login_id = login_id;
    }

    public int getIs_school() {
        return is_school;
    }

    public void setIs_school(int is_school) {
        this.is_school = is_school;
    }

    public String getLgoin_time() {
        return lgoin_time;
    }

    public void setLgoin_time(String lgoin_time) {
        this.lgoin_time = lgoin_time;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public int getIs_mobill() {
        return is_mobill;
    }

    public void setIs_mobill(int is_mobill) {
        this.is_mobill = is_mobill;
    }
}
