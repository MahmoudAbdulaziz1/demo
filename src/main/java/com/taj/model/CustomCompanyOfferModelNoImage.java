package com.taj.model;

import javax.validation.constraints.*;

/**
 * Created by User on 10/24/2018.
 */
public class CustomCompanyOfferModelNoImage {

    private int offer_id;

    @NotNull
    @NotBlank
    @NotEmpty
    @Size(max = 450, min = 1, message = "title should have at least 1 characters")
    private String offer_title;
    @NotNull
    @NotBlank
    @NotEmpty
    @Size(max = 450, min = 1, message = "explian should have at least 1 characters")
    private String offer_explaination;
    @Min(1)
    private double offer_cost;
    private @NotNull Long offer_display_date;
    @NotNull
    private Long offer_expired_date;
    @NotNull
    private Long offer_deliver_date;
    @Min(1)
    private int company_id;
    @Min(1)
    private int offer_count;
    private String city;
    private String area;
    private float lng;
    private float lat;

    public CustomCompanyOfferModelNoImage(int offer_id, @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "title should have at least 1 characters") String offer_title, @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "explian should have at least 1 characters") String offer_explaination, @Min(1) double offer_cost, @NotNull Long offer_display_date,
                                          @NotNull Long offer_expired_date, @NotNull Long offer_deliver_date, @Min(1) int company_id, @Min(1) int offer_count, String city, String area, float lng, float lat) {
        this.offer_id = offer_id;
        this.offer_title = offer_title;
        this.offer_explaination = offer_explaination;
        this.offer_cost = offer_cost;
        this.offer_display_date = offer_display_date;
        this.offer_expired_date = offer_expired_date;
        this.offer_deliver_date = offer_deliver_date;
        this.company_id = company_id;
        this.offer_count = offer_count;
        this.city = city;
        this.area = area;
        this.lng = lng;
        this.lat = lat;
    }

    public CustomCompanyOfferModelNoImage() {
    }

    public int getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(int offer_id) {
        this.offer_id = offer_id;
    }

    public String getOffer_title() {
        return offer_title;
    }

    public void setOffer_title(String offer_title) {
        this.offer_title = offer_title;
    }

    public String getOffer_explaination() {
        return offer_explaination;
    }

    public void setOffer_explaination(String offer_explaination) {
        this.offer_explaination = offer_explaination;
    }

    public double getOffer_cost() {
        return offer_cost;
    }

    public void setOffer_cost(double offer_cost) {
        this.offer_cost = offer_cost;
    }

    public Long getOffer_display_date() {
        return offer_display_date;
    }

    public void setOffer_display_date(Long offer_display_date) {
        this.offer_display_date = offer_display_date;
    }

    public Long getOffer_expired_date() {
        return offer_expired_date;
    }

    public void setOffer_expired_date(Long offer_expired_date) {
        this.offer_expired_date = offer_expired_date;
    }

    public Long getOffer_deliver_date() {
        return offer_deliver_date;
    }

    public void setOffer_deliver_date(Long offer_deliver_date) {
        this.offer_deliver_date = offer_deliver_date;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public int getOffer_count() {
        return offer_count;
    }

    public void setOffer_count(int offer_count) {
        this.offer_count = offer_count;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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
