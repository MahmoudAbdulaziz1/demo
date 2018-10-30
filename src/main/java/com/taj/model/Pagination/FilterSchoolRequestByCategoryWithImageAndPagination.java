package com.taj.model.Pagination;

import com.taj.model.school_request_image_web.schoolRequestWithImageDto;

import java.util.List;

/**
 * Created by User on 10/18/2018.
 */
public class FilterSchoolRequestByCategoryWithImageAndPagination {

    private int pagesNum;
    private List<schoolRequestWithImageDto> requests;

    public FilterSchoolRequestByCategoryWithImageAndPagination(int pagesNum, List<schoolRequestWithImageDto> requests) {
        this.pagesNum = pagesNum;
        this.requests = requests;
    }

    public FilterSchoolRequestByCategoryWithImageAndPagination() {
    }

    public int getPagesNum() {
        return pagesNum;
    }

    public void setPagesNum(int pagesNum) {
        this.pagesNum = pagesNum;
    }

    public List<schoolRequestWithImageDto> getRequests() {
        return requests;
    }

    public void setRequests(List<schoolRequestWithImageDto> requests) {
        this.requests = requests;
    }
}
