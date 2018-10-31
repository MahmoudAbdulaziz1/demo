package com.taj.model;

/**
 * Created by User on 10/31/2018.
 */
public class AreaModelEnglish {


    private int areaId;
    private String areaName;

    public AreaModelEnglish(int areaId, String areaName) {
        this.areaId = areaId;
        this.areaName = areaName;
    }

    public AreaModelEnglish() {
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }


}
