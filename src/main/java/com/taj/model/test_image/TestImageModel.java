package com.taj.model.test_image;

/**
 * Created by MahmoudAhmed on 10/14/2018.
 */
public class TestImageModel {


    private int id;
    private String name;
    private String second;

    public TestImageModel(int id ,String name, String second) {
        this.id = id;
        this.name = name;
        this.second = second;
    }

    public TestImageModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }
}