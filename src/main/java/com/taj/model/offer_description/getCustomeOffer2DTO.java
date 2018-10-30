package com.taj.model.offer_description;

import com.taj.model.CustomCompanyModelWithViewAndDescRes2;

/**
 * Created by User on 10/15/2018.
 */
public class getCustomeOffer2DTO {

    private String status;
    private CustomCompanyModelWithViewAndDescRes2 model;

    public getCustomeOffer2DTO(String status, CustomCompanyModelWithViewAndDescRes2 model) {
        this.status = status;
        this.model = model;
    }

    public getCustomeOffer2DTO() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CustomCompanyModelWithViewAndDescRes2 getModel() {
        return model;
    }

    public void setModel(CustomCompanyModelWithViewAndDescRes2 model) {
        this.model = model;
    }
}
