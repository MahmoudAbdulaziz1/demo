package com.taj.model;

import javax.validation.constraints.*;

/**
 * Created by User on 10/30/2018.
 */
public class SchoolRequestModelUpdateAr {




        private int request_id;
        @NotNull
        @NotBlank
        @NotEmpty
        @Size(max = 450, min = 1, message = "title should have at least 1 characters")
        private String request_title;
        @NotNull
        @NotBlank
        @NotEmpty
        @Size(max = 450, min = 1, message = "explain should have at least 1 characters")
        private String request_explaination;
        @NotNull
        private long request_display_date;
        @NotNull
        private long request_expired_date;

        @NotNull
        @Min(0)
        private int request_is_conformied;//التعميد
        @NotNull
        @Min(1)
        private int school_id;
        @NotNull
        private String request_category_name;
        private String image_one;
        @Min(0)
        private int request_count;
        private int flag_ar;

        public SchoolRequestModelUpdateAr() {
        }

        public SchoolRequestModelUpdateAr(int request_id, @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "title should have at least 1 characters") String request_title, @NotNull @NotBlank @NotEmpty @Size(max = 450, min = 1, message = "explain should have at least 1 characters") String request_explaination, @NotNull long request_display_date, @NotNull long request_expired_date,
                                        @NotNull @Min(0) int request_is_conformied, @NotNull @Min(1) int school_id,
                                        @NotNull @Min(1) String request_category_name, String image_one, @Min(1) int request_count, int flag_ar) {
            this.request_id = request_id;
            this.request_title = request_title;
            this.request_explaination = request_explaination;
            this.request_display_date = request_display_date;
            this.request_expired_date = request_expired_date;
            this.request_is_conformied = request_is_conformied;
            this.school_id = school_id;
            this.request_category_name = request_category_name;
            this.image_one = image_one;
            this.request_count = request_count;
            this.flag_ar = flag_ar;
        }

        public int getRequest_id() {
            return request_id;
        }

        public void setRequest_id(int request_id) {
            this.request_id = request_id;
        }

        public String getRequest_title() {
            return request_title;
        }

        public void setRequest_title(String request_title) {
            this.request_title = request_title;
        }

        public String getRequest_explaination() {
            return request_explaination;
        }

        public void setRequest_explaination(String request_explaination) {
            this.request_explaination = request_explaination;
        }

        public long getRequest_display_date() {
            return request_display_date;
        }

        public void setRequest_display_date(long request_display_date) {
            this.request_display_date = request_display_date;
        }

        public long getRequest_expired_date() {
            return request_expired_date;
        }

        public void setRequest_expired_date(long request_expired_date) {
            this.request_expired_date = request_expired_date;
        }

        public int getRequest_is_conformied() {
            return request_is_conformied;
        }

        public void setRequest_is_conformied(int request_is_conformied) {
            this.request_is_conformied = request_is_conformied;
        }

        public int getSchool_id() {
            return school_id;
        }

        public void setSchool_id(int school_id) {
            this.school_id = school_id;
        }

        public String getRequest_category_name() {
            return request_category_name;
        }

        public void setRequest_category_name(String request_category_name) {
            this.request_category_name = request_category_name;
        }

        public String getImage_one() {
            return image_one;
        }

        public void setImage_one(String image_one) {
            this.image_one = image_one;
        }

        public int getRequest_count() {
            return request_count;
        }

        public void setRequest_count(int request_count) {
            this.request_count = request_count;
        }

        public int getFlag_ar() {
            return flag_ar;
        }

        public void setFlag_ar(int flag_ar) {
            this.flag_ar = flag_ar;
        }




}
