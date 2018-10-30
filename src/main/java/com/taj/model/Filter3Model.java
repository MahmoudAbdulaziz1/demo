package com.taj.model;

/**
 * Created by MahmoudAhmed on 10/28/2018.
 */
public class Filter3Model {

    private String name;
    private int cat;
    private String area;
    private String city;
    private int view;
    private int follow;

    public Filter3Model(String name, int cat, String area, String city, int view, int follow) {
        this.name = name;
        this.cat = cat;
        this.area = area;
        this.city = city;
        this.view = view;
        this.follow = follow;
    }

    public Filter3Model() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCat() {
        return cat;
    }

    public void setCat(int cat) {
        this.cat = cat;
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

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getFollow() {
        return follow;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }
}
