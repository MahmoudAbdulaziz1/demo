package com.taj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.model.*;
import com.taj.model.Pagination.CompanyProfileDtoThreeDTOPaginate;
import com.taj.model.admin_login.AdminLogin;
import com.taj.model.new_profile_map.CompanyProfileDtoTwoDTO;
import com.taj.model.new_register.CheckOrgNameNotFoundDto;
import com.taj.repository.*;
import com.taj.repository.admin_login.AdminLoginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by User on 10/25/2018.
 */
@RequestMapping("/service/")
@RestController
@CrossOrigin
public class NoTokenController {

    private static final String STATUS = "status";
    private static final String MESSAGE = "message";
    private static final String NOFOUND = "This id not found";
    private static final String AREANAME = "area_name";
    @Autowired
    AdminLoginRepo adminRepo;
    @Autowired
    CategoryRepo catRepo;
    @Autowired
    LoginRepo loginRepo;
    @Autowired
    NewProfileRepo companyProfilerRepo;
    @Autowired
    NewRegisterRepo registrationRepo;
    @Autowired
    RegistrationRepo registration2Repo;
    @Autowired
    SchoolRequestCategoryRepo schoolRequestCategoryRepo;
    @Autowired
    NewLoginRepo loginRepo2;
    @Autowired
    AreaRepo areaRepo;
    @Autowired
    AreaWithCityRepo repo;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    ObjectMapper mapper;

    @PostMapping("admin/login/super/logging/")
//    @PreAuthorize("hasAuthority('super_admin')")
    public ResponseEntity<ObjectNode> isLogged(@RequestBody @Valid AdminLogin model, Errors errors) {

        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        ObjectNode objectNode = adminRepo.isLogged(model.getEmail(), model.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
    }

    @GetMapping("cat/getAll/{flag_ar}")
    public List<CategoryModelEnglish> getCategories(@PathVariable int flag_ar) {
        return catRepo.getCategories(flag_ar);
    }

    @PostMapping("login/isLogged")
    public ResponseEntity<ObjectNode> isLogged(@RequestBody @Valid LoginIsLoggedDTO model, Errors errors) {

        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        ObjectNode objectNode = loginRepo.isLogged(model.getUser_email(), model.getUser_password(), model.getLogin_role());
        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
    }

    @GetMapping("company/profil/s/all/page/{page}/{pageSize}")
    public CompanyProfileDtoThreeDTOPaginate getTestObject2Pagination(@PathVariable int page, @PathVariable int pageSize) {

        int pages = (int) Math.ceil(((float) companyProfilerRepo.getCompaniesProfilesObject2PaginationCount()) / ((float) pageSize));
        Map<CompanyProfileDtoTwoDTO, List<CompanyProfileDtoOne>> res = new HashMap<>();
        List<Map<String, Object>> list = companyProfilerRepo.getCompaniesProfilesObject2Pagination();
        List<CompanyProfileDtoTwoDTO> schoolsList = new ArrayList<>();
        Set<CompanyProfileDtoTwoDTO> schools = new HashSet<>();
        List<CompanyProfileDtoOne> test2Models = new ArrayList<>();
        for (Map<String, Object> map : list) {
            CompanyProfileDtoTwoDTO model = new CompanyProfileDtoTwoDTO();
            CompanyProfileDtoOne test2Model = new CompanyProfileDtoOne();
            int companyId = (int) map.get("company_id");
            String companyName = (String) map.get("company_name");
            String companyLogoImage = (String) map.get("company_logo_image");
            String companyAddress = (String) map.get("company_address");
            String companyLinkYoutube = (String) map.get("company_link_youtube");
            String companyWebsiteUrl = (String) map.get("company_website_url");
            float companyLng = (float) map.get("company_lng");
            float companyLat = (float) map.get("company_lat");
            String companyCoverImage = (String) map.get("company_cover_image");
            String companyPhoneNumber = (String) map.get("company_phone_number");
            long followerCount = (long) map.get("follower_count");
            long orderCount = (long) map.get("order_count");
            String companyDesc = (String) map.get("company_desc");
            long categoryNum = (long) map.get("category_num");
            String city = (String) map.get("city");
            String area = (String) map.get("area");
            float lng =0;
            try {
                lng = (float) map.get("lng");
            }catch (Exception e){
                lng = 0;
            }
            float lat =0;
            try {
                lat = (float) map.get("lat");
            }catch (Exception e){
                lat=0;
            }
            List<CompanyProfileDtoOne> categories;

            int categoryId = (int) map.get("category_id");
            String categoryName = (String) map.get("category_name");


            test2Model.setCategory_id(categoryId);
            test2Model.setCategory_name(categoryName);

            model.setCompanyId(companyId);
            model.setCompanyName(companyName);
            model.setCompanyLogoImage(companyLogoImage);
            model.setCompanyAddress(companyAddress);
            model.setCompanyLinkYoutube(companyLinkYoutube);
            model.setCompanyWebsiteUrl(companyWebsiteUrl);
            model.setCompanyLng(companyLng);
            model.setCompanyLat(companyLat);
            model.setCompanyCoverImage(companyCoverImage);
            model.setCompanyPhoneNumber(companyPhoneNumber);
            model.setFollowerCount(followerCount);
            model.setOrderCount(orderCount);
            model.setCompanyDesc(companyDesc);
            model.setCategoryNum(categoryNum);
            model.setCity(city);
            model.setArea(area);
            model.setLng(lng);
            model.setLat(lat);
            //String encodedString = Base64.getEncoder().encodeToString(schoolLogo);
            schools.add(model);

            test2Models.add(test2Model);
        }
        for (CompanyProfileDtoTwoDTO obj : schools) {
            //System.out.println(obj.toString());
//            res.put(obj,new ArrayList<Test2Model>());
//            List<Test2Model>  test2ModelArrayList=new ArrayList<>();
            List<CompanyProfileDtoOne> test2ModelArrayList = null;
            test2ModelArrayList = new ArrayList<>();

            int i = 0;
            for (Map<String, Object> map : list) {
//                obj.getCategories().add(test2ModelList);


                if (res.get(obj) == null) {
                    res.put(obj, new ArrayList<CompanyProfileDtoOne>());

                }
//                if (res.containsKey(obj)) {
                if (map.get("company_id").equals(obj.getCompanyId())) {
                    CompanyProfileDtoOne test2Model = new CompanyProfileDtoOne();
                    int categoryId = (int) map.get("category_id");
                    String categoryName = (String) map.get("category_name");


                    test2Model.setCategory_id(categoryId);
                    test2Model.setCategory_name(categoryName);
                    test2ModelArrayList.add(test2Model);
                    res.get(obj).add(i, test2Model);
                    i++;
                }

            }

            obj.setCategories(test2ModelArrayList);
            schoolsList.add(obj);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        //res = objectMapper.convertValue(res, Map.class);

        int limitOffset = (page - 1) * pageSize;
        List<CompanyProfileDtoTwoDTO> resList = new ArrayList<>();
        for (int i = limitOffset; i < (limitOffset + pageSize); i++) {
            try {
                resList.add(schoolsList.get(i));
            } catch (Exception e) {

            }
        }
        try {
            CompanyProfileDtoThreeDTOPaginate mainModel = new CompanyProfileDtoThreeDTOPaginate(pages, resList);
            return mainModel;
        }catch (Exception e){
            CompanyProfileDtoThreeDTOPaginate mainModel = new CompanyProfileDtoThreeDTOPaginate(pages, null);
            return mainModel;
        }



    }


    @PostMapping("company/profil/s/all/pages/{page}/{pageSize}")
    public CompanyProfileDtoThreeDTOPaginate getCompaniesProfilesObject2PaginationWithFilter(@PathVariable int page, @PathVariable int pageSize,
                                                                                             @RequestBody Filter2Model filter) {
        Map<CompanyProfileDtoTwoDTO, List<CompanyProfileDtoOne>> res = new HashMap<>();
        List<Map<String, Object>> list = companyProfilerRepo.getCompaniesProfilesObject2PaginationWithFilter(filter.getName(),
                filter.getCat(), filter.getArea(), filter.getCity(), filter.getView());
        List<CompanyProfileDtoTwoDTO> schoolsList = new ArrayList<>();
        Set<CompanyProfileDtoTwoDTO> schools = new HashSet<>();
        List<CompanyProfileDtoOne> test2Models = new ArrayList<>();
        for (Map<String, Object> map : list) {
            CompanyProfileDtoTwoDTO model = new CompanyProfileDtoTwoDTO();
            CompanyProfileDtoOne test2Model = new CompanyProfileDtoOne();
            int companyId = (int) map.get("company_id");
            String companyName = (String) map.get("company_name");
            String companyLogoImage = (String) map.get("company_logo_image");
            String companyAddress = (String) map.get("company_address");
            String companyLinkYoutube = (String) map.get("company_link_youtube");
            String companyWebsiteUrl = (String) map.get("company_website_url");
            float companyLng = (float) map.get("company_lng");
            float companyLat = (float) map.get("company_lat");
            String companyCoverImage = (String) map.get("company_cover_image");
            String companyPhoneNumber = (String) map.get("company_phone_number");
            long followerCount = (long) map.get("follower_count");
            long orderCount = (long) map.get("order_count");
            String companyDesc = (String) map.get("company_desc");
            long categoryNum = (long) map.get("category_num");
            String city = (String) map.get("city");
            String area = (String) map.get("area");
            float lng = 0;
            try {
                lng = (float) map.get("lng");
            } catch (Exception e) {
                lng = 0;
            }
            float lat = 0;
            try {
                lat = (float) map.get("lat");
            } catch (Exception e) {
                lat = 0;
            }
            List<CompanyProfileDtoOne> categories;

            int categoryId = (int) map.get("category_id");
            String categoryName = (String) map.get("category_name");


            test2Model.setCategory_id(categoryId);
            test2Model.setCategory_name(categoryName);

            model.setCompanyId(companyId);
            model.setCompanyName(companyName);
            model.setCompanyLogoImage(companyLogoImage);
            model.setCompanyAddress(companyAddress);
            model.setCompanyLinkYoutube(companyLinkYoutube);
            model.setCompanyWebsiteUrl(companyWebsiteUrl);
            model.setCompanyLng(companyLng);
            model.setCompanyLat(companyLat);
            model.setCompanyCoverImage(companyCoverImage);
            model.setCompanyPhoneNumber(companyPhoneNumber);
            model.setFollowerCount(followerCount);
            model.setOrderCount(orderCount);
            model.setCompanyDesc(companyDesc);
            model.setCategoryNum(categoryNum);
            model.setCity(city);
            model.setArea(area);
            model.setLng(lng);
            model.setLat(lat);
            //String encodedString = Base64.getEncoder().encodeToString(schoolLogo);
            schools.add(model);

            test2Models.add(test2Model);
        }
        for (CompanyProfileDtoTwoDTO obj : schools) {
            //System.out.println(obj.toString());
//            res.put(obj,new ArrayList<Test2Model>());
//            List<Test2Model>  test2ModelArrayList=new ArrayList<>();
            List<CompanyProfileDtoOne> test2ModelArrayList = null;
            test2ModelArrayList = new ArrayList<>();

            int i = 0;
            for (Map<String, Object> map : list) {
//                obj.getCategories().add(test2ModelList);


                if (res.get(obj) == null) {
                    res.put(obj, new ArrayList<CompanyProfileDtoOne>());

                }
//                if (res.containsKey(obj)) {
                if (map.get("company_id").equals(obj.getCompanyId())) {
                    CompanyProfileDtoOne test2Model = new CompanyProfileDtoOne();
                    int categoryId = (int) map.get("category_id");
                    String categoryName = (String) map.get("category_name");


                    test2Model.setCategory_id(categoryId);
                    test2Model.setCategory_name(categoryName);
                    test2ModelArrayList.add(test2Model);
                    res.get(obj).add(i, test2Model);
                    i++;
                }

            }

            obj.setCategories(test2ModelArrayList);
            schoolsList.add(obj);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        //res = objectMapper.convertValue(res, Map.class);

        int pages = (int) Math.ceil(((float) schoolsList.size()) / ((float) pageSize));
        int limitOffset = (page - 1) * pageSize;
        List<CompanyProfileDtoTwoDTO> resList = new ArrayList<>();
        for (int i = limitOffset; i < (limitOffset + pageSize); i++) {
            try {
                resList.add(schoolsList.get(i));
            } catch (Exception e) {

            }
        }
        try {
            CompanyProfileDtoThreeDTOPaginate mainModel = new CompanyProfileDtoThreeDTOPaginate(pages, resList);
            return mainModel;
        } catch (Exception e) {
            CompanyProfileDtoThreeDTOPaginate mainModel = new CompanyProfileDtoThreeDTOPaginate(pages, null);
            return mainModel;
        }


    }





    @PostMapping("usr/register/{flag_ar}")
    public ResponseEntity<ObjectNode> addUserRegistration(@RequestBody @Valid NewRegisterModel registrationModel, Errors errors, @PathVariable int flag_ar) {

        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(STATUS, 400);
            objectNode.put(MESSAGE, "Validation Failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        } else if (registrationRepo.checkIfEmailExist(registrationModel.getRegisterationEmail(), registrationModel.getRegistrationRole())) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(STATUS, 400);
            objectNode.put(MESSAGE, "Email is exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

        if (registrationModel.getRegistrationRole().trim().compareTo(UserType.admin.toString()) == 0 ||
                registrationModel.getRegistrationRole().trim().compareTo(UserType.school.toString()) == 0 ||
                registrationModel.getRegistrationRole().trim().compareTo(UserType.company.toString()) == 0) {

            String encodedPassword = bCryptPasswordEncoder.encode(registrationModel.getRegisterationPassword());
            int model = registrationRepo.addUser(registrationModel.getRegisterationEmail(), encodedPassword,
                    registrationModel.getRegisterationUsername(), registrationModel.getRegisterationPhoneNumber(),
                    registrationModel.getRegistrationOrganizationName(), registrationModel.getRegistrationAddressDesc(),
                    registrationModel.getRegistrationWebsiteUrl(), registrationModel.getRegistrationRole(),
                    new Timestamp(System.currentTimeMillis()).getTime(), registrationModel.getCity(), registrationModel.getArea(),
                    registrationModel.getLng(), registrationModel.getLat(), flag_ar);
            if (model == 1) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put(STATUS, 201);
                objectNode.put("registeration_email", registrationModel.getRegisterationEmail());
                objectNode.put("registeration_password", registrationModel.getRegisterationPassword());
                objectNode.put("registeration_username", registrationModel.getRegisterationUsername());
                objectNode.put("registeration_phone_number", registrationModel.getRegisterationPhoneNumber());
                objectNode.put("registration_organization_name", registrationModel.getRegistrationOrganizationName());
                objectNode.put("registration_address_desc", registrationModel.getRegistrationAddressDesc());
                objectNode.put("registration_website_url", registrationModel.getRegistrationWebsiteUrl());
                objectNode.put("registration_isActive", registrationModel.getRegistrationIsActive());
                objectNode.put("registration_role", registrationModel.getRegistrationRole());
                objectNode.put("registration_date", new Timestamp(System.currentTimeMillis()).getTime());
                objectNode.put("city", registrationModel.getCity());
                objectNode.put("Area", registrationModel.getArea());
                objectNode.put("lng", registrationModel.getLng());
                objectNode.put("lat", registrationModel.getLat());
                return ResponseEntity.status(HttpStatus.OK).body(objectNode);
            } else if (model == -100) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put(STATUS, 400);
                objectNode.put(MESSAGE, "This Name Is Already Exist");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put(STATUS, 400);
                objectNode.put(MESSAGE, "sign up error");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        } else {

            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(STATUS, 400);
            objectNode.put(MESSAGE, "not valid role");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);

        }


    }

    @RequestMapping(value = "register/add", method = RequestMethod.POST)
    public ResponseEntity<ObjectNode> addUserRegistration(@Valid @RequestBody RegistrationModel registrationModel, Errors errors) {

        if (errors.hasErrors()){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }else if (registrationRepo.checkIfEmailExist(registrationModel.getRegisteration_email(), registrationModel.getRegistration_role())){
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Email is exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

        if (registrationModel.getRegistration_role().trim().compareTo(UserType.admin.toString())==0 ||
                registrationModel.getRegistration_role().trim().compareTo(UserType.school.toString())==0 ||
                registrationModel.getRegistration_role().trim().compareTo(UserType.company.toString())==0) {

            String encodedPassword = bCryptPasswordEncoder.encode(registrationModel.getRegisteration_password());
            RegistrationModel model = registration2Repo.addUser(registrationModel.getRegisteration_email(), encodedPassword,
                    registrationModel.getRegisteration_username(), registrationModel.getRegisteration_phone_number(),
                    registrationModel.getRegistration_organization_name(), registrationModel.getRegistration_address_desc(),
                    registrationModel.getRegistration_website_url(),
                    registrationModel.getRegistration_isActive(), registrationModel.getRegistration_role(), new Timestamp(System.currentTimeMillis()).getTime());
            if (model != null) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("states", 201);
                objectNode.put("registeration_email", registrationModel.getRegisteration_email());
                objectNode.put("registeration_password", registrationModel.getRegisteration_password());
                objectNode.put("registeration_username", registrationModel.getRegisteration_username());
                objectNode.put("registeration_phone_number", registrationModel.getRegisteration_phone_number());
                objectNode.put("registration_organization_name", registrationModel.getRegistration_organization_name());
                objectNode.put("registration_address_desc", registrationModel.getRegistration_address_desc());
                objectNode.put("registration_website_url", registrationModel.getRegistration_website_url());
                objectNode.put("registration_isActive", registrationModel.getRegistration_isActive());
                objectNode.put("registration_role", registrationModel.getRegistration_role());
                objectNode.put("registration_date", new Timestamp(System.currentTimeMillis()).getTime());
                return ResponseEntity.status(HttpStatus.OK).body(objectNode);
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("states", 400);
                objectNode.put("message", "sign up error");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        }else {

            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("states", 400);
            objectNode.put("message", "not valid role");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);

        }


    }

    @GetMapping("school/category/getCat/{flag_ar}")
    public List<schoolCategoriesToWEBSITE> getSchoolRequestCategoriesForWeb(@PathVariable int flag_ar) {
        return  schoolRequestCategoryRepo.getSchoolRequestCategoriesForWeb(flag_ar);
    }

    @PutMapping("login/restPassword")
    public ResponseEntity<ObjectNode> restPassword(@RequestBody @Valid RestPasswordModel model, Errors errors) {
        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        int res = loginRepo.restPassword(model.getUser_email(), model.getLogin_role());

        if (res == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 200);
            objectNode.put("message", "success");
            //objectNode.put("login_role", model.getLogin_role());


            return ResponseEntity.status(HttpStatus.OK).body(objectNode);
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("message", "not success");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }

    @PostMapping("new/login/{flag_ar}")
    public ResponseEntity<ObjectNode> loginUsers(@RequestBody @Valid LoginIsLoggedDTO model, Errors errors, @PathVariable int flag_ar) {
        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(STATUS, 400);
            objectNode.put(MESSAGE, "Validation Failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

//        loginRepo.loginUser(model.getUserEmail(), model.getUserPassword(),
//                model.getIsActive(), model.getLoginRole(), model.getLoginToken(), model.getCity(), model.getArea(), model.getLng(), model.getLat());
//        ObjectNode objectNode = mapper.createObjectNode();
//        objectNode.put("user_email", model.getUserEmail());
//        objectNode.put("user_password", model.getUserPassword());
//        objectNode.put("is_active", model.getIsActive());
//        objectNode.put("login_role", model.getLoginRole());
//        objectNode.put("login_token", model.getLoginToken());
//        objectNode.put("city", model.getCity());
//        objectNode.put("area", model.getArea());
//        objectNode.put("lng", model.getLng());
//        objectNode.put("lat", model.getLat());
//
//        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);

        return loginRepo2.isLogged(model.getUser_email(), model.getUser_password(), model.getLogin_role(), flag_ar);

    }



    @PostMapping("area/")
    public ResponseEntity<ObjectNode> addArea(@RequestBody AreaModel model) {
        int response = areaRepo.addArea(model.getAreaName(),model.getAreaNameAr());
        if (response == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(AREANAME, model.getAreaName());
            return ResponseEntity.status(HttpStatus.OK).body(objectNode);
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(MESSAGE, "Not added");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }

    @GetMapping("area/")
    public ResponseEntity<List<AreaModelEnglish>> getCities(int flag_ar) {
        if (areaRepo.getAreas(flag_ar).isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(areaRepo.getAreas(flag_ar));
        }
        return ResponseEntity.status(HttpStatus.OK).body(areaRepo.getAreas(flag_ar));


    }

    @GetMapping("area/cities/{flag_ar}")
    public AreaWithCityDto getTestObject(@PathVariable int flag_ar) {
        Map<AreaDataDto, List<CityDataDto>> res = new HashMap<>();
        List<Map<String, Object>> list = repo.getTestObject(flag_ar);
        List<AreaDataDto> schoolsList = new ArrayList<>();
        Set<AreaDataDto> schools = new HashSet<>();
        List<CityDataDto> test2Models = new ArrayList<>();

        //for add id to set
        for (Map<String, Object> map : list) {
            AreaDataDto model = new AreaDataDto();
            CityDataDto test2Model = new CityDataDto();


            int areaId = (int) map.get("area_id");
            String areaName = (String) map.get("area_name");

            try {
                int cityId = (int) map.get("city_id");
                String cityName = (String) map.get("city_name");


                test2Model.setCityId(cityId);
                test2Model.setCityName(cityName);
            } catch (NullPointerException e) {
                test2Model = null;
            }


            model.setAreaId(areaId);
            model.setAreaName(areaName);
            //String encodedString = Base64.getEncoder().encodeToString(schoolLogo);
            schools.add(model);

            test2Models.add(test2Model);
        }


        for (AreaDataDto obj : schools) {
            System.out.println(obj.toString());
//            res.put(obj,new ArrayList<Test2Model>());
//            List<Test2Model>  test2ModelArrayList=new ArrayList<>();
            List<CityDataDto> test2ModelArrayList = null;
            test2ModelArrayList = new ArrayList<>();

            int i = 0;
            for (Map<String, Object> map : list) {
//                obj.getCategories().add(test2ModelList);


                if (res.get(obj) == null) {
                    res.put(obj, new ArrayList<CityDataDto>());

                }
//                if (res.containsKey(obj)) {
                if (map.get("area_id").equals(obj.getAreaId())) {
                    CityDataDto test2Model = new CityDataDto();
                    try {
                        int categoryId = (int) map.get("city_id");
                        String categoryName = (String) map.get("city_name");


                        test2Model.setCityId(categoryId);
                        test2Model.setCityName(categoryName);
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


        ObjectMapper objectMapper = new ObjectMapper();
        //res = objectMapper.convertValue(res, Map.class);

        AreaWithCityDto mainModel = new AreaWithCityDto(schoolsList);


//        ObjectNode tenderNode = objectMapper.createObjectNode();
//        tenderNode.put("tender_id", mainModel.getTender_id());
//        tenderNode.put("tender_title", mainModel.getTender_title());
//        tenderNode.put("tender_explain", mainModel.getTender_explain());
//        ArrayNode tenderCategory = objectMapper.createArrayNode();

        return mainModel;


    }

    @PostMapping("usr/register/org")
    public boolean IfOrgNameExist(@RequestBody CheckOrgNameNotFoundDto dto) {
        return registrationRepo.IfOrgNameExist(dto.getRegistration_organization_name());
    }


    @Autowired
    RecievesNewsMailRepo recievesNewsMailRepo;

    @PostMapping("receive/news/mail/")
    public ResponseEntity<ObjectNode> addMail(@Valid @RequestBody RecievesNewsMailsModel model, Errors errors) {
        if (errors.hasErrors()) {
            ObjectNode node = mapper.createObjectNode();
            node.put(STATUS, 400);
            node.put(MESSAGE, "Validation Error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(node);
        }
        int response = recievesNewsMailRepo.addMail(model.getMail());
        if (response == 1) {
            ObjectNode node = mapper.createObjectNode();
            node.put(STATUS, 200);
            node.put("mail", model.getMail());
            return ResponseEntity.status(HttpStatus.OK).body(node);
        } else {
            ObjectNode node = mapper.createObjectNode();
            node.put(STATUS, 400);
            node.put(MESSAGE, "Not Added");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(node);
        }
    }

    @GetMapping("receive/news/mail/")
    public ResponseEntity<List<RecievesNewsMailsModel>> getMails() {
        List<RecievesNewsMailsModel> mails = recievesNewsMailRepo.getAllMails();
        if (mails == null) {
            return ResponseEntity.status(HttpStatus.OK).body(mails);
        }
        return ResponseEntity.status(HttpStatus.OK).body(mails);


    }



}
