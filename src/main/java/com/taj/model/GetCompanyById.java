package com.taj.model;

import java.util.List;

/**
 * Created by User on 7/24/2018.
 */
public class GetCompanyById {

    private String states;
    private CompanyProfileModel model;

    public GetCompanyById(String states, CompanyProfileModel model) {
        this.states = states;
        this.model = model;
    }

    public GetCompanyById() {
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public CompanyProfileModel getModel() {
        return model;
    }

    public void setModel(CompanyProfileModel model) {
        this.model = model;
    }
}
