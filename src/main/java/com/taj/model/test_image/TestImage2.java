package com.taj.model.test_image;

/**
 * Created by MahmoudAhmed on 10/16/2018.
 */
public class TestImage2 {
    private int id;
    private byte[] url;
    private String name;

    public TestImage2(int id, byte[] url, String name) {
        this.id = id;
        this.url = url;
        this.name = name;
    }

    public TestImage2() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getUrl() {
        return url;
    }

    public void setUrl(byte[] url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}