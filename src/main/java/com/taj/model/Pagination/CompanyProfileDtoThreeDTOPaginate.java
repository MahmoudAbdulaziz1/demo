package com.taj.model.Pagination;

import com.taj.model.CompanyProfileDtoOne;
import com.taj.model.new_profile_map.CompanyProfileDtoTwoDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by User on 10/18/2018.
 */
public class CompanyProfileDtoThreeDTOPaginate {


    Map<CompanyProfileDtoTwoDTO, List<CompanyProfileDtoOne>> res;
    private List<CompanyProfileDtoTwoDTO> companies;
    private int pageNum;

    public CompanyProfileDtoThreeDTOPaginate(int pageNum, Map<CompanyProfileDtoTwoDTO, List<CompanyProfileDtoOne>> res, List<CompanyProfileDtoTwoDTO> companies) {
        this.res = res;
        this.companies = companies;
        this.pageNum = pageNum;
    }

    public CompanyProfileDtoThreeDTOPaginate(int pageNum, List<CompanyProfileDtoTwoDTO> companies) {
        this.companies = companies;
        this.pageNum = pageNum;
    }

    public CompanyProfileDtoThreeDTOPaginate() {
    }

    public Map<CompanyProfileDtoTwoDTO, List<CompanyProfileDtoOne>> getRes() {
        return res;
    }

    public void setRes(Map<CompanyProfileDtoTwoDTO, List<CompanyProfileDtoOne>> res) {
        this.res = res;
    }

    public List<CompanyProfileDtoTwoDTO> getCompanies() {
        return companies;
    }

    public void setCompanies(List<CompanyProfileDtoTwoDTO> companies) {
        this.companies = companies;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompanyProfileDtoThreeDTOPaginate that = (CompanyProfileDtoThreeDTOPaginate) o;

        if (pageNum != that.pageNum) return false;
        if (res != null ? !res.equals(that.res) : that.res != null) return false;
        return !(companies != null ? !companies.equals(that.companies) : that.companies != null);

    }

    @Override
    public int hashCode() {
        int result = res != null ? res.hashCode() : 0;
        result = 31 * result + (companies != null ? companies.hashCode() : 0);
        result = 31 * result + pageNum;
        return result;
    }

    @Override
    public String toString() {
        return "CompanyProfileDtoThreeDTO{" +
                "res=" + res +
                ", companies=" + companies +
                ", pageNum=" + pageNum +
                '}';
    }

}
