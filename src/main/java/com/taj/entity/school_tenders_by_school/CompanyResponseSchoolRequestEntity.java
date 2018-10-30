//package com.taj.entity.school_tenders_by_school;
//
//import javax.persistence.Entity;
//import javax.persistence.OneToMany;
//import javax.persistence.Table;
//import java.sql.Timestamp;
//
///**
// * Created by User on 10/16/2018.
// */
//@Entity
//@Table(name = "efaz_company_response_school_request")
//public class CompanyResponseSchoolRequestEntity {
//
//    private int response_id;
//    @OneToMany
//    private CompanyProfileEntity companyProfileEntity;
//    @OneToMany
//    private SchoolTenderEntity schoolTenderEntity;
//    private double responsed_cost;
//    private int responsed_from;
//    private int responsed_to;
//    private int is_aproved;
//    private Timestamp response_date;
//    private String response_desc;
//
//    public CompanyResponseSchoolRequestEntity(int response_id, int companyProfileEntity, int schoolTenderEntity,
//                                              double responsed_cost, int responsed_from, int responsed_to, int is_aproved,
//                                              Timestamp response_date, String response_desc) {
//        this.response_id = response_id;
//        this.companyProfileEntity = new CompanyProfileEntity(companyProfileEntity, "", null, "","", null, "", "", 0, 0, );
//        this.schoolTenderEntity = schoolTenderEntity;
//        this.responsed_cost = responsed_cost;
//        this.responsed_from = responsed_from;
//        this.responsed_to = responsed_to;
//        this.is_aproved = is_aproved;
//        this.response_date = response_date;
//        this.response_desc = response_desc;
//    }
//}
