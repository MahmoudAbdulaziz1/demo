package com.taj.controller;

import com.taj.model.CategoryNameDto;
import com.taj.model.TenderRequestCategoriesModel;
import com.taj.model.TenderRequestSchoolModel;
import com.taj.model.collective_image.TenderRequestTenderModel2Dto;
import com.taj.repository.TakatafTenderRequestRepo;
import com.taj.repository.TenderRequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by User on 8/30/2018.
 */
@RequestMapping("/service/evvaz/s/request/tender")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TenderRequestController {

    @Autowired
    TenderRequestRepo repo;
    @Autowired
    TakatafTenderRequestRepo takatafTenderRequestRepo;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('admin') OR hasAuthority('school')")
    public TenderRequestTenderModel2Dto getTenderRequestObject(@PathVariable int id, @PathVariable int flag_ar) {
        Map<TenderRequestSchoolModel, List<TenderRequestCategoriesModel>> res = new HashMap<>();
        List<Map<String, Object>> list = repo.getTenderRequestObjectWithCompanyDates(id, flag_ar);
        List<TenderRequestSchoolModel> schoolsList = new ArrayList<>();
        Set<TenderRequestSchoolModel> schools = new HashSet<>();
        List<TenderRequestCategoriesModel> test2Models = new ArrayList<>();
        for (Map<String, Object> map : list) {
            TenderRequestSchoolModel model = new TenderRequestSchoolModel();
            TenderRequestCategoriesModel test2Model = new TenderRequestCategoriesModel();


            long schoolId = (long) map.get("school_id");
            String schoolName = (String) map.get("school_name");
            byte[] schoolLogo = (byte[]) map.get("school_logo_image");
            Timestamp date = (Timestamp) map.get("t_date");
            long t_date = 0;
            if (date == null) {
                t_date = 0;
            } else {
                t_date = date.getTime();
            }

            //System.out.println(map.get("t_date")+" +++ "+ date+" ++++++ "+ date.getTime()+ " ++++ "+ new Timestamp(0));
            try {
                int categoryId = (int) map.get("id");
                String categoryName = (String) map.get("category_name");
                int count = (int) map.get("count");
                test2Model.setId(categoryId);
                test2Model.setCategory_name(categoryName);
                test2Model.setCount(count);
            } catch (NullPointerException e) {
                test2Model = null;
            }
            try {
                model.setSchool_id(schoolId);
                model.setSchool_name(schoolName);
                model.setSchool_logo_image(Base64.getEncoder().encodeToString(schoolLogo));
                model.setT_date(t_date);
                //String encodedString = Base64.getEncoder().encodeToString(schoolLogo);
                schools.add(model);
            } catch (NullPointerException e) {
                schools.add(null);
            }

            test2Models.add(test2Model);
        }

        try {


            for (TenderRequestSchoolModel obj : schools) {

                List<TenderRequestCategoriesModel> test2ModelArrayList = null;
                test2ModelArrayList = new ArrayList<>();

                int i = 0;
                for (Map<String, Object> map : list) {
//                obj.getCategories().add(test2ModelList);


                    if (res.get(obj) == null) {
                        res.put(obj, new ArrayList<TenderRequestCategoriesModel>());

                    }
//                if (res.containsKey(obj)) {
                    if (map.get("school_id").equals(obj.getSchool_id())) {
                        TenderRequestCategoriesModel test2Model = new TenderRequestCategoriesModel();
                        try {
                            int categoryId = (int) map.get("id");
                            String categoryName = (String) map.get("category_name");
                            int count = (int) map.get("count");


                            test2Model.setId(categoryId);
                            test2Model.setCategory_name(categoryName);
                            test2Model.setCount(count);
                        } catch (NullPointerException e) {
                            test2Model = null;
                        }


                        test2ModelArrayList.add(test2Model);
                        res.get(obj).add(i, test2Model);
                        i++;
                    }

                }

                obj.setCategories(test2ModelArrayList);
                schoolsList.add(obj);
            }
        } catch (NullPointerException e) {
            schoolsList.add(null);
        }

        if (Long.parseLong(list.get(0).get("response_count").toString()) == 0) {
            List<CategoryNameDto> category = takatafTenderRequestRepo.categoryData(id, flag_ar);

            TenderRequestTenderModel2Dto mainModel = new TenderRequestTenderModel2Dto(Long.parseLong(list.get(0).get("tender_id").toString()),
                    (byte[]) list.get(0).get("tender_logo"),
                    (String) list.get(0).get("tender_title"),
                    (String) list.get(0).get("tender_explain"), ((Timestamp) (list.get(0).get("tender_display_date"))).getTime(),
                    ((Timestamp) list.get(0).get("tender_expire_date")).getTime(),
                    ((Timestamp) list.get(0).get("tender_company_display_date")).getTime(),
                    ((Timestamp) list.get(0).get("tender_company_expired_date")).getTime(),
                    Long.parseLong(list.get(0).get("response_count").toString()), category, null);


            return mainModel;
        } else {
            List<CategoryNameDto> category = takatafTenderRequestRepo.categoryData(id, flag_ar);
            TenderRequestTenderModel2Dto mainModel = new TenderRequestTenderModel2Dto(Long.parseLong(list.get(0).get("tender_id").toString()),
                    (byte[]) list.get(0).get("tender_logo"),
                    (String) list.get(0).get("tender_title"),
                    (String) list.get(0).get("tender_explain"), ((Timestamp) (list.get(0).get("tender_display_date"))).getTime(),
                    ((Timestamp) list.get(0).get("tender_expire_date")).getTime(),
                    ((Timestamp) list.get(0).get("tender_company_display_date")).getTime(),
                    ((Timestamp) list.get(0).get("tender_company_expired_date")).getTime(),
                    Long.parseLong(list.get(0).get("response_count").toString()), category, schoolsList);


            return mainModel;
        }


    }
}
