package com.taj.model;

import javax.validation.constraints.*;

/**
 * Created by User on 9/30/2018.
 */
public class NewSchoolProfileModelDTOImage {


    @Min(1)
    private int school_id;
    @NotNull
    @NotBlank
    @NotEmpty
    @Size(max = 450, min = 1, message = "name should have at least 1 characters")
    private String school_name;
    @NotNull
    @NotBlank
    @NotEmpty
    @Size(max = 450, min = 1, message = "address should have at least 1 characters")
    private String school_address;

    private String school_service_desc;
    //    @NotNull
//    @NotBlank
//    @NotEmpty
//    @Size(max = 450, min = 1, message="company_name should have at least 1 characters")
    private String school_link_youtube;
    private String school_website_url;
    private float school_lng, school_lat;
    @NotNull
    private String school_phone_number;
    private String area;
    private String city;
    private float lng;
    private float lat;

    public NewSchoolProfileModelDTOImage(@Min(1) int school_id, @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "name should have at least 1 characters")
    String school_name, @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "address should have at least 1 characters")
                                    String school_address, String school_service_desc, String school_link_youtube, String school_website_url,
                                    float school_lng, float school_lat, String school_phone_number, String city, String area, float lng, float lat) {
        this.school_id = school_id;
        this.school_name = school_name;
        this.school_address = school_address;
        this.school_service_desc = school_service_desc;
        this.school_link_youtube = school_link_youtube;
        this.school_website_url = school_website_url;
        this.school_lng = school_lng;
        this.school_lat = school_lat;
        this.school_phone_number = school_phone_number;
        this.area = area;
        this.city = city;
        this.lng = lng;
        this.lat = lat;
    }

    public NewSchoolProfileModelDTOImage() {
    }

    public int getSchool_id() {
        return school_id;
    }

    public void setSchool_id(int school_id) {
        this.school_id = school_id;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getSchool_address() {
        return school_address;
    }

    public void setSchool_address(String school_address) {
        this.school_address = school_address;
    }

    public String getSchool_service_desc() {
        return school_service_desc;
    }

    public void setSchool_service_desc(String school_service_desc) {
        this.school_service_desc = school_service_desc;
    }

    public String getSchool_link_youtube() {
        return school_link_youtube;
    }

    public void setSchool_link_youtube(String school_link_youtube) {
        this.school_link_youtube = school_link_youtube;
    }

    public String getSchool_website_url() {
        return school_website_url;
    }

    public void setSchool_website_url(String school_website_url) {
        this.school_website_url = school_website_url;
    }

    public float getSchool_lng() {
        return school_lng;
    }

    public void setSchool_lng(float school_lng) {
        this.school_lng = school_lng;
    }

    public float getSchool_lat() {
        return school_lat;
    }

    public void setSchool_lat(float school_lat) {
        this.school_lat = school_lat;
    }

    public String getSchool_phone_number() {
        return school_phone_number;
    }

    public void setSchool_phone_number(String school_phone_number) {
        this.school_phone_number = school_phone_number;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }
}
