package com.taj.entity.school_tenders_by_school;

import javax.persistence.*;

/**
 * Created by User on 10/16/2018.
 */
@Entity
@Table(name = "efaz_school_request_recive_places")
public class SchoolRecievePlacesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int place_id;
    private String place_name;

    public SchoolRecievePlacesEntity(int place_id, String place_name) {
        this.place_id = place_id;
        this.place_name = place_name;
    }

    public SchoolRecievePlacesEntity() {
    }

    public SchoolRecievePlacesEntity(String place_name) {
        this.place_name = place_name;
    }

    public int getPlace_id() {
        return place_id;
    }

    public void setPlace_id(int place_id) {
        this.place_id = place_id;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }
}
