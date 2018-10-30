package com.taj.model;

/**
 * Created by MahmoudAhmed on 10/26/2018.
 */
public class FilterModel {

    private String name;
    private int day_left;

    public FilterModel(String name, int day_left) {
        this.name = name;
        this.day_left = day_left;
    }

    public FilterModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDay_left() {
        return day_left;
    }

    public void setDay_left(int day_left) {
        this.day_left = day_left;
    }
}
