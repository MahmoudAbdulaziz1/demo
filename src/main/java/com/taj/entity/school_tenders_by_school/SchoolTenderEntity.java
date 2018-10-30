package com.taj.entity.school_tenders_by_school;

import javax.persistence.*;

/**
 * Created by User on 10/16/2018.
 */
@Entity
@Table(name = "efaz_school_tender")
public class SchoolTenderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int request_id;
    private byte[] request_details_file;
    @ManyToOne
    @JoinColumn(name="images_id", nullable=false)
    private SchoolRequestImageEntity schoolRequestImageEntity;
    private String request_title;
    private String request_explaination;
    private long request_display_date;
    private long request_expired_date;
    private long request_deliver_date;
    private int request_is_available;
    private int request_is_conformied;
    private long request_payment_date;
    @ManyToOne
    @JoinColumn(name="school_id", nullable=false)
    private SchoolProfileEntity schoolProfileEntity;
    @ManyToOne
    @JoinColumn(name="requests_category_id", nullable=false)
    private RequestCategoryEntity requestCategoryEntity;
    @ManyToOne
    @JoinColumn(name="receive_palce_id", nullable=true)
    private SchoolRecievePlacesEntity receive_palce_id;
    private int extended_payment;
    private int request_count;

    public SchoolTenderEntity(int request_id, byte[] request_details_file, int schoolRequestImageEntity, String request_title, String request_explaination,
                              long request_display_date, long request_expired_date, long request_deliver_date, int request_is_available, int request_is_conformied,
                              long request_payment_date, int schoolProfileEntity,
                              int requestCategoryEntity, int receive_palce_id, int extended_payment, int request_count) {
        this.request_id = request_id;
        this.request_details_file = request_details_file;
        this.schoolRequestImageEntity = new SchoolRequestImageEntity(schoolRequestImageEntity, null, null, null, null);
        this.request_title = request_title;
        this.request_explaination = request_explaination;
        this.request_display_date = request_display_date;
        this.request_expired_date = request_expired_date;
        this.request_deliver_date = request_deliver_date;
        this.request_is_available = request_is_available;
        this.request_is_conformied = request_is_conformied;
        this.request_payment_date = request_payment_date;
        this.schoolProfileEntity = new SchoolProfileEntity(schoolProfileEntity, "", null, "", "", "", "", 0, 0, null, "");
        this.requestCategoryEntity = new RequestCategoryEntity(requestCategoryEntity, "");
        this.receive_palce_id = new SchoolRecievePlacesEntity(receive_palce_id, "");
        this.extended_payment = extended_payment;
        this.request_count = request_count;
    }

    public SchoolTenderEntity(byte[] request_details_file, int schoolRequestImageEntity, String request_title, String request_explaination,
                              long request_display_date, long request_expired_date, long request_deliver_date, int request_is_available, int request_is_conformied,
                              long request_payment_date, int schoolProfileEntity,
                              int requestCategoryEntity, int receive_palce_id, int extended_payment, int request_count) {
        this.request_details_file = request_details_file;
        this.schoolRequestImageEntity = new SchoolRequestImageEntity(schoolRequestImageEntity, null, null, null, null);
        this.request_title = request_title;
        this.request_explaination = request_explaination;
        this.request_display_date = request_display_date;
        this.request_expired_date = request_expired_date;
        this.request_deliver_date = request_deliver_date;
        this.request_is_available = request_is_available;
        this.request_is_conformied = request_is_conformied;
        this.request_payment_date = request_payment_date;
        this.schoolProfileEntity = new SchoolProfileEntity(schoolProfileEntity, "", null, "", "", "", "", 0, 0, null, "");
        this.requestCategoryEntity = new RequestCategoryEntity(requestCategoryEntity, "");
        this.receive_palce_id = new SchoolRecievePlacesEntity(request_id, "");
        this.extended_payment = extended_payment;
        this.request_count = request_count;
    }

    public SchoolTenderEntity() {
    }

    public int getRequest_id() {
        return request_id;
    }

    public void setRequest_id(int request_id) {
        this.request_id = request_id;
    }

    public byte[] getRequest_details_file() {
        return request_details_file;
    }

    public void setRequest_details_file(byte[] request_details_file) {
        this.request_details_file = request_details_file;
    }

    public SchoolRequestImageEntity getSchoolRequestImageEntity() {
        return schoolRequestImageEntity;
    }

    public void setSchoolRequestImageEntity(SchoolRequestImageEntity schoolRequestImageEntity) {
        this.schoolRequestImageEntity = schoolRequestImageEntity;
    }

    public String getRequest_title() {
        return request_title;
    }

    public void setRequest_title(String request_title) {
        this.request_title = request_title;
    }

    public String getRequest_explaination() {
        return request_explaination;
    }

    public void setRequest_explaination(String request_explaination) {
        this.request_explaination = request_explaination;
    }

    public long getRequest_display_date() {
        return request_display_date;
    }

    public void setRequest_display_date(long request_display_date) {
        this.request_display_date = request_display_date;
    }

    public long getRequest_expired_date() {
        return request_expired_date;
    }

    public void setRequest_expired_date(long request_expired_date) {
        this.request_expired_date = request_expired_date;
    }

    public long getRequest_deliver_date() {
        return request_deliver_date;
    }

    public void setRequest_deliver_date(long request_deliver_date) {
        this.request_deliver_date = request_deliver_date;
    }

    public int getRequest_is_available() {
        return request_is_available;
    }

    public void setRequest_is_available(int request_is_available) {
        this.request_is_available = request_is_available;
    }

    public int getRequest_is_conformied() {
        return request_is_conformied;
    }

    public void setRequest_is_conformied(int request_is_conformied) {
        this.request_is_conformied = request_is_conformied;
    }

    public long getRequest_payment_date() {
        return request_payment_date;
    }

    public void setRequest_payment_date(long request_payment_date) {
        this.request_payment_date = request_payment_date;
    }

    public SchoolProfileEntity getSchoolProfileEntity() {
        return schoolProfileEntity;
    }

    public void setSchoolProfileEntity(SchoolProfileEntity schoolProfileEntity) {
        this.schoolProfileEntity = schoolProfileEntity;
    }

    public RequestCategoryEntity getRequestCategoryEntity() {
        return requestCategoryEntity;
    }

    public void setRequestCategoryEntity(RequestCategoryEntity requestCategoryEntity) {
        this.requestCategoryEntity = requestCategoryEntity;
    }

    public SchoolRecievePlacesEntity getReceive_palce_id() {
        return receive_palce_id;
    }

    public void setReceive_palce_id(SchoolRecievePlacesEntity receive_palce_id) {
        this.receive_palce_id = receive_palce_id;
    }

    public int getExtended_payment() {
        return extended_payment;
    }

    public void setExtended_payment(int extended_payment) {
        this.extended_payment = extended_payment;
    }

    public int getRequest_count() {
        return request_count;
    }

    public void setRequest_count(int request_count) {
        this.request_count = request_count;
    }
}
