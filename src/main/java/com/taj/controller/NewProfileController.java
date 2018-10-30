package com.taj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.controller.test_image.StorageService;
import com.taj.controller.test_image.UploadController;
import com.taj.model.*;
import com.taj.model.Pagination.CompanyProfileDtoThreeDTOPaginate;
import com.taj.model.new_profile_map.*;
import com.taj.repository.NewProfileRepo;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by User on 9/11/2018.
 */
@RequestMapping("/service/evvaz/s/company/profil")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class NewProfileController {

    private static final String STATUS = "status";
    private static final String MESSAGE = "message";
    private final Path rootLocation = Paths.get("upload-dir");
    @Autowired
    NewProfileRepo repo;
    @Autowired
    ObjectMapper mapper;
    List<String> files = new ArrayList<String>();
    @Autowired
    StorageService storageService;

    @PostMapping("/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> addProfileWithCategories(@RequestBody @Valid MultiCategoryProfileModel model, Errors errors) {
        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(STATUS, 400);
            objectNode.put(MESSAGE, "Validation Failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        if (repo.isExist(model.getCompany_id())) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(STATUS, 400);
            objectNode.put(MESSAGE, "already has profile in this id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        } else {
            repo.addProfileWithCategories(model.getCompany_id(), model.getCompany_name(), model.getCompany_logo_image(),
                    model.getCompany_address(), model.getCompany_link_youtube(),
                    model.getCompany_website_url(), model.getCompany_lng(), model.getCompany_lat(),
                    model.getCompany_cover_image(), model.getCompany_phone_number(), model.getCompany_desc(), model.getCategory());

            ArrayNode category = mapper.createArrayNode();

            for (int i = 0; i < model.getCategory().size(); i++) {
                ObjectNode node = mapper.createObjectNode();
                node.put("category_name", model.getCategory().get(i).getCategory_name());
                category.add(node);
            }
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(STATUS, 200);
            objectNode.put("company_id", model.getCompany_id());
            objectNode.put("company_name", model.getCompany_name());
            objectNode.put("company_logo_image", model.getCompany_logo_image());
            objectNode.put("company_address", model.getCompany_address());
            objectNode.put("company_link_youtube", model.getCompany_link_youtube());
            objectNode.put("company_website_url", model.getCompany_website_url());
            objectNode.put("company_lng", model.getCompany_lng());
            objectNode.put("company_lat", model.getCompany_lat());
            objectNode.put("company_cover_image", model.getCompany_cover_image());
            objectNode.put("company_phone_number", model.getCompany_phone_number());
            objectNode.put("company_desc", model.getCompany_desc());
            objectNode.set("categories", category);
            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


        }
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<NewProfileModel> getProfiles() {
        return repo.getProfiles();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public NewProfileDto3 getProfile(@PathVariable int id) {
        List<TakatfTenderCategoryPOJO> category = new ArrayList<>();
        List<NewProfileDto> allData = repo.getProfile(id);
        for (int i = 0; i < allData.size(); i++) {
            TakatfTenderCategoryPOJO pojo = new TakatfTenderCategoryPOJO(allData.get(i).getCompanyCatName());
            category.add(pojo);
        }
        NewProfileDto3 result = new NewProfileDto3(allData.get(0).getCompanyId(), allData.get(0).getCompanyName(),
                allData.get(0).getCompanyLogoImage(), allData.get(0).getCompanyAddress(),
                allData.get(0).getCompanyLinkYoutube(), allData.get(0).getCompanyWebsiteUrl(), allData.get(0).getCompanyLng(),
                allData.get(0).getCompanyLat(), allData.get(0).getCompanyCoverImage(), allData.get(0).getCompanyPhoneNumber(),
                allData.get(0).getFollowerCount(), allData.get(0).getOfferCount(), allData.get(0).getCompanyDesc(),
                allData.get(0).getCity(), allData.get(0).getArea(), category);
        return result;
    }


    @PutMapping("/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> updateProfile(@RequestParam(value = "logo", required = false) MultipartFile logo, @RequestParam(value = "cover", required = false) MultipartFile cover,
                                                    @Valid @RequestPart String newProfileDto3String, Errors errors) {


        try {
            NewProfileDto32 model = new ObjectMapper().readValue(newProfileDto3String, NewProfileDto32.class);


            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put(STATUS, 400);
                objectNode.put(MESSAGE, "Validation Failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
            if (repo.isExist(model.getCompanyId())) {


                String logoUrl = "";
                String coverUrl = "";
                try {
                    logoUrl = imageUrl(logo, rootLocation);
                } catch (Exception e) {
                    logoUrl = "";
                }
                try {
                    coverUrl = imageUrl(cover, rootLocation);
                } catch (Exception e) {
                    coverUrl = "";
                }


                if (logoUrl.equals("") || logoUrl.equals(null) || logoUrl.isEmpty()) {

                    if (coverUrl.equals("") || coverUrl.equals(null) || coverUrl.isEmpty()) {

                        int res = repo.updateProfile(model.getCompanyId(), model.getCompanyName(), model.getCompanyLogoImage(), model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), model.getCompanyCoverImage(), model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getCategory());
                        if (res == 1) {
                            ArrayNode category = mapper.createArrayNode();

                            for (int i = 0; i < model.getCategory().size(); i++) {
                                ObjectNode node = mapper.createObjectNode();
                                node.put("category_name", model.getCategory().get(i).getCategory_name());
                                category.add(node);
                            }
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 200);
                            objectNode.put("company_id", model.getCompanyId());
                            objectNode.put("company_name", model.getCompanyName());
                            objectNode.put("company_logo_image", model.getCompanyLogoImage());
                            objectNode.put("company_address", model.getCompanyAddress());
                            objectNode.put("company_link_youtube", model.getCompanyLinkYoutube());
                            objectNode.put("company_website_url", model.getCompanyWebsiteUrl());
                            objectNode.put("company_lng", model.getCompanyLng());
                            objectNode.put("company_lat", model.getCompanyLat());
                            objectNode.put("company_cover_image", model.getCompanyCoverImage());
                            objectNode.put("company_phone_number", model.getCompanyPhoneNumber());
                            objectNode.put("company_desc", model.getCompanyDesc());
                            objectNode.put("city", model.getCity());
                            objectNode.put("area", model.getArea());
                            objectNode.set("categories", category);
                            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                        } else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 400);
                            objectNode.put(MESSAGE, "failed");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                        }


                    } else {

                        int res = repo.updateProfile(model.getCompanyId(), model.getCompanyName(), coverUrl, model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), model.getCompanyCoverImage(), model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getCategory());
                        if (res == 1) {
                            ArrayNode category = mapper.createArrayNode();

                            for (int i = 0; i < model.getCategory().size(); i++) {
                                ObjectNode node = mapper.createObjectNode();
                                node.put("category_name", model.getCategory().get(i).getCategory_name());
                                category.add(node);
                            }
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 200);
                            objectNode.put("company_id", model.getCompanyId());
                            objectNode.put("company_name", model.getCompanyName());
                            objectNode.put("company_logo_image", model.getCompanyLogoImage());
                            objectNode.put("company_address", model.getCompanyAddress());
                            objectNode.put("company_link_youtube", model.getCompanyLinkYoutube());
                            objectNode.put("company_website_url", model.getCompanyWebsiteUrl());
                            objectNode.put("company_lng", model.getCompanyLng());
                            objectNode.put("company_lat", model.getCompanyLat());
                            objectNode.put("company_cover_image", coverUrl);
                            objectNode.put("company_phone_number", model.getCompanyPhoneNumber());
                            objectNode.put("company_desc", model.getCompanyDesc());
                            objectNode.put("city", model.getCity());
                            objectNode.put("area", model.getArea());
                            objectNode.set("categories", category);
                            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                        } else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 400);
                            objectNode.put(MESSAGE, "failed");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                        }

                    }
                } else if (coverUrl.equals("") || coverUrl.equals(null) || coverUrl.isEmpty()) {
                    if (logoUrl.equals("") || logoUrl.equals(null) || logoUrl.isEmpty()) {


                        int res = repo.updateProfile(model.getCompanyId(), model.getCompanyName(), model.getCompanyLogoImage(), model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), model.getCompanyCoverImage(), model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getCategory());
                        if (res == 1) {
                            ArrayNode category = mapper.createArrayNode();

                            for (int i = 0; i < model.getCategory().size(); i++) {
                                ObjectNode node = mapper.createObjectNode();
                                node.put("category_name", model.getCategory().get(i).getCategory_name());
                                category.add(node);
                            }
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 200);
                            objectNode.put("company_id", model.getCompanyId());
                            objectNode.put("company_name", model.getCompanyName());
                            objectNode.put("company_logo_image", model.getCompanyLogoImage());
                            objectNode.put("company_address", model.getCompanyAddress());
                            objectNode.put("company_link_youtube", model.getCompanyLinkYoutube());
                            objectNode.put("company_website_url", model.getCompanyWebsiteUrl());
                            objectNode.put("company_lng", model.getCompanyLng());
                            objectNode.put("company_lat", model.getCompanyLat());
                            objectNode.put("company_cover_image", model.getCompanyCoverImage());
                            objectNode.put("company_phone_number", model.getCompanyPhoneNumber());
                            objectNode.put("company_desc", model.getCompanyDesc());
                            objectNode.put("city", model.getCity());
                            objectNode.put("area", model.getArea());
                            objectNode.set("categories", category);
                            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                        } else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 400);
                            objectNode.put(MESSAGE, "failed");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                        }


                    } else {


                        int res = repo.updateProfile(model.getCompanyId(), model.getCompanyName(), logoUrl, model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), model.getCompanyCoverImage(), model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getCategory());
                        if (res == 1) {
                            ArrayNode category = mapper.createArrayNode();

                            for (int i = 0; i < model.getCategory().size(); i++) {
                                ObjectNode node = mapper.createObjectNode();
                                node.put("category_name", model.getCategory().get(i).getCategory_name());
                                category.add(node);
                            }
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 200);
                            objectNode.put("company_id", model.getCompanyId());
                            objectNode.put("company_name", model.getCompanyName());
                            objectNode.put("company_logo_image", logoUrl);
                            objectNode.put("company_address", model.getCompanyAddress());
                            objectNode.put("company_link_youtube", model.getCompanyLinkYoutube());
                            objectNode.put("company_website_url", model.getCompanyWebsiteUrl());
                            objectNode.put("company_lng", model.getCompanyLng());
                            objectNode.put("company_lat", model.getCompanyLat());
                            objectNode.put("company_cover_image", model.getCompanyCoverImage());
                            objectNode.put("company_phone_number", model.getCompanyPhoneNumber());
                            objectNode.put("company_desc", model.getCompanyDesc());
                            objectNode.put("city", model.getCity());
                            objectNode.put("area", model.getArea());
                            objectNode.set("categories", category);
                            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                        } else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 400);
                            objectNode.put(MESSAGE, "failed");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                        }

                    }
                } else {
                    int res = repo.updateProfile(model.getCompanyId(), model.getCompanyName(), logoUrl, model.getCompanyAddress(),
                            model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                            model.getCompanyLat(), coverUrl, model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                            model.getCity(), model.getArea(), model.getCategory());
                    if (res == 1) {
                        ArrayNode category = mapper.createArrayNode();

                        for (int i = 0; i < model.getCategory().size(); i++) {
                            ObjectNode node = mapper.createObjectNode();
                            node.put("category_name", model.getCategory().get(i).getCategory_name());
                            category.add(node);
                        }
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put(STATUS, 200);
                        objectNode.put("company_id", model.getCompanyId());
                        objectNode.put("company_name", model.getCompanyName());
                        objectNode.put("company_logo_image", logoUrl);
                        objectNode.put("company_address", model.getCompanyAddress());
                        objectNode.put("company_link_youtube", model.getCompanyLinkYoutube());
                        objectNode.put("company_website_url", model.getCompanyWebsiteUrl());
                        objectNode.put("company_lng", model.getCompanyLng());
                        objectNode.put("company_lat", model.getCompanyLat());
                        objectNode.put("company_cover_image", coverUrl);
                        objectNode.put("company_phone_number", model.getCompanyPhoneNumber());
                        objectNode.put("company_desc", model.getCompanyDesc());
                        objectNode.put("city", model.getCity());
                        objectNode.put("area", model.getArea());
                        objectNode.set("categories", category);
                        return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                    } else {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put(STATUS, 400);
                        objectNode.put(MESSAGE, "failed");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }
                }


            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put(STATUS, 400);
                objectNode.put(MESSAGE, "already has profile in this id");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }


    @GetMapping("/category/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<NewProfileDto4> getProfileByCategoryAndroid(@PathVariable String id) {
        List<NewProfileDto2> model = repo.getProfileByCategory(id);
        if (model.size() > 0) {
            return ResponseEntity.status(HttpStatus.OK).body(new NewProfileDto4("200", model));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new NewProfileDto4("400", null));
        }
    }


    @GetMapping("/all/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public CompanyProfileDtoThree getTestObject() {

        Map<CompanyProfileDtoTwo, List<CompanyProfileDtoOne>> res = new HashMap<>();
        List<Map<String, Object>> list = repo.getCompaniesProfilesObject();
        List<CompanyProfileDtoTwo> schoolsList = new ArrayList<>();
        Set<CompanyProfileDtoTwo> schools = new HashSet<>();
        List<CompanyProfileDtoOne> test2Models = new ArrayList<>();
        for (Map<String, Object> map : list) {
            CompanyProfileDtoTwo model = new CompanyProfileDtoTwo();
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
            //String encodedString = Base64.getEncoder().encodeToString(schoolLogo);
            schools.add(model);

            test2Models.add(test2Model);
        }
        for (CompanyProfileDtoTwo obj : schools) {
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

        CompanyProfileDtoThree mainModel = new CompanyProfileDtoThree(schoolsList);


//        ObjectNode tenderNode = objectMapper.createObjectNode();
//        tenderNode.put("tender_id", mainModel.getTender_id());
//        tenderNode.put("tender_title", mainModel.getTender_title());
//        tenderNode.put("tender_explain", mainModel.getTender_explain());
//        ArrayNode tenderCategory = objectMapper.createArrayNode();

        return mainModel;
    }


    @GetMapping("/all/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public CompanyProfileDtoThree getTestObject2(@PathVariable int id) {

        Map<CompanyProfileDtoTwo, List<CompanyProfileDtoOne>> res = new HashMap<>();
        List<Map<String, Object>> list = repo.getCompaniesProfilesObjectForAll(id);
        List<CompanyProfileDtoTwo> schoolsList = new ArrayList<>();
        Set<CompanyProfileDtoTwo> schools = new HashSet<>();
        List<CompanyProfileDtoOne> test2Models = new ArrayList<>();
        for (Map<String, Object> map : list) {
            CompanyProfileDtoTwo model = new CompanyProfileDtoTwo();
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
            int isFollow = (int) map.get("is_follow");
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
            model.setIs_follow(isFollow);
            //String encodedString = Base64.getEncoder().encodeToString(schoolLogo);
            if (schools.contains(model)) {
                continue;
            } else {
                schools.add(model);
                test2Models.add(test2Model);
                System.out.println("=====> this is the size   " + schools.size());
            }

        }
        for (CompanyProfileDtoTwo obj : schools) {
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

        CompanyProfileDtoThree mainModel = new CompanyProfileDtoThree(schoolsList);


//        ObjectNode tenderNode = objectMapper.createObjectNode();
//        tenderNode.put("tender_id", mainModel.getTender_id());
//        tenderNode.put("tender_title", mainModel.getTender_title());
//        tenderNode.put("tender_explain", mainModel.getTender_explain());
//        ArrayNode tenderCategory = objectMapper.createArrayNode();

        return mainModel;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @GetMapping("/s/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<NewProfileModelDTO> getProfiles2() {
        return repo.getProfiles2();
    }

    @GetMapping("/s/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public NewProfileDto3DTO getProfile2(@PathVariable int id) {
        List<TakatfTenderCategoryPOJO> category = new ArrayList<>();
        List<NewProfileDtoDTO> allData = repo.getProfile2(id);
        for (int i = 0; i < allData.size(); i++) {
            TakatfTenderCategoryPOJO pojo = new TakatfTenderCategoryPOJO(allData.get(i).getCompanyCatName());
            category.add(pojo);
        }
        NewProfileDto3DTO result = new NewProfileDto3DTO(allData.get(0).getCompanyId(), allData.get(0).getCompanyName(),
                allData.get(0).getCompanyLogoImage(), allData.get(0).getCompanyAddress(),
                allData.get(0).getCompanyLinkYoutube(), allData.get(0).getCompanyWebsiteUrl(), allData.get(0).getCompanyLng(),
                allData.get(0).getCompanyLat(), allData.get(0).getCompanyCoverImage(), allData.get(0).getCompanyPhoneNumber(),
                allData.get(0).getFollowerCount(), allData.get(0).getOfferCount(), allData.get(0).getCompanyDesc(),
                allData.get(0).getCity(), allData.get(0).getArea(), allData.get(0).getLng(), allData.get(0).getLat(), category);
        return result;
    }


    @PutMapping("/s/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> updateProfile2(@RequestParam(value = "logo", required = false) MultipartFile logo, @RequestParam(value = "cover", required = false) MultipartFile cover,
                                                     @Valid @RequestPart String newProfileDto3DTOString, Errors errors) {


        try {
            NewProfileDto33 model = new ObjectMapper().readValue(newProfileDto3DTOString, NewProfileDto33.class);


            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put(STATUS, 400);
                objectNode.put(MESSAGE, "Validation Failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
            if (repo.isExist(model.getCompanyId())) {


                String logoUrl = "";
                String coverUrl = "";
                try {
                    logoUrl = imageUrl(logo, rootLocation);
                } catch (Exception e) {
                    logoUrl = "";
                }
                try {
                    coverUrl = imageUrl(cover, rootLocation);
                } catch (Exception e) {
                    coverUrl = "";
                }

                if (logoUrl.isEmpty() || logoUrl.equals(null) || logoUrl.equals("")) {
                    if (coverUrl.equals(null) || coverUrl.equals("") || coverUrl.isEmpty()) {
                        int res = repo.updateProfile2(model.getCompanyId(), model.getCompanyName(), model.getCompanyLogoImage(), model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), model.getCompanyCoverImage(), model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getLng(), model.getLat(), model.getCategory());
                        if (res == 1) {
                            ArrayNode category = mapper.createArrayNode();

                            for (int i = 0; i < model.getCategory().size(); i++) {
                                ObjectNode node = mapper.createObjectNode();
                                node.put("category_name", model.getCategory().get(i).getCategory_name());
                                category.add(node);
                            }
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 200);
                            objectNode.put("company_id", model.getCompanyId());
                            objectNode.put("company_name", model.getCompanyName());
                            objectNode.put("company_logo_image", model.getCompanyLogoImage());
                            objectNode.put("company_address", model.getCompanyAddress());
                            objectNode.put("company_link_youtube", model.getCompanyLinkYoutube());
                            objectNode.put("company_website_url", model.getCompanyWebsiteUrl());
                            objectNode.put("company_lng", model.getCompanyLng());
                            objectNode.put("company_lat", model.getCompanyLat());
                            objectNode.put("company_cover_image", model.getCompanyCoverImage());
                            objectNode.put("company_phone_number", model.getCompanyPhoneNumber());
                            objectNode.put("company_desc", model.getCompanyDesc());
                            objectNode.put("city", model.getCity());
                            objectNode.put("area", model.getArea());
                            objectNode.put("lng", model.getLng());
                            objectNode.put("lat", model.getLat());
                            objectNode.set("categories", category);

                            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                        } else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 400);
                            objectNode.put(MESSAGE, "failed");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                        }

                    } else {


                        int res = repo.updateProfile2(model.getCompanyId(), model.getCompanyName(), model.getCompanyLogoImage(), model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), coverUrl, model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getLng(), model.getLat(), model.getCategory());
                        if (res == 1) {
                            ArrayNode category = mapper.createArrayNode();

                            for (int i = 0; i < model.getCategory().size(); i++) {
                                ObjectNode node = mapper.createObjectNode();
                                node.put("category_name", model.getCategory().get(i).getCategory_name());
                                category.add(node);
                            }
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 200);
                            objectNode.put("company_id", model.getCompanyId());
                            objectNode.put("company_name", model.getCompanyName());
                            objectNode.put("company_logo_image", model.getCompanyLogoImage());
                            objectNode.put("company_address", model.getCompanyAddress());
                            objectNode.put("company_link_youtube", model.getCompanyLinkYoutube());
                            objectNode.put("company_website_url", model.getCompanyWebsiteUrl());
                            objectNode.put("company_lng", model.getCompanyLng());
                            objectNode.put("company_lat", model.getCompanyLat());
                            objectNode.put("company_cover_image", coverUrl);
                            objectNode.put("company_phone_number", model.getCompanyPhoneNumber());
                            objectNode.put("company_desc", model.getCompanyDesc());
                            objectNode.put("city", model.getCity());
                            objectNode.put("area", model.getArea());
                            objectNode.put("lng", model.getLng());
                            objectNode.put("lat", model.getLat());
                            objectNode.set("categories", category);

                            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                        } else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 400);
                            objectNode.put(MESSAGE, "failed");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                        }

                    }
                } else if (coverUrl.equals(null) || coverUrl.equals("") || coverUrl.isEmpty()) {


                    if (logoUrl.isEmpty() || logoUrl.equals(null) || logoUrl.equals("")) {

                        int res = repo.updateProfile2(model.getCompanyId(), model.getCompanyName(), model.getCompanyLogoImage(), model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), model.getCompanyCoverImage(), model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getLng(), model.getLat(), model.getCategory());
                        if (res == 1) {
                            ArrayNode category = mapper.createArrayNode();

                            for (int i = 0; i < model.getCategory().size(); i++) {
                                ObjectNode node = mapper.createObjectNode();
                                node.put("category_name", model.getCategory().get(i).getCategory_name());
                                category.add(node);
                            }
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 200);
                            objectNode.put("company_id", model.getCompanyId());
                            objectNode.put("company_name", model.getCompanyName());
                            objectNode.put("company_logo_image", model.getCompanyLogoImage());
                            objectNode.put("company_address", model.getCompanyAddress());
                            objectNode.put("company_link_youtube", model.getCompanyLinkYoutube());
                            objectNode.put("company_website_url", model.getCompanyWebsiteUrl());
                            objectNode.put("company_lng", model.getCompanyLng());
                            objectNode.put("company_lat", model.getCompanyLat());
                            objectNode.put("company_cover_image", model.getCompanyCoverImage());
                            objectNode.put("company_phone_number", model.getCompanyPhoneNumber());
                            objectNode.put("company_desc", model.getCompanyDesc());
                            objectNode.put("city", model.getCity());
                            objectNode.put("area", model.getArea());
                            objectNode.put("lng", model.getLng());
                            objectNode.put("lat", model.getLat());
                            objectNode.set("categories", category);

                            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                        } else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 400);
                            objectNode.put(MESSAGE, "failed");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                        }
                    } else {


                        int res = repo.updateProfile2(model.getCompanyId(), model.getCompanyName(), logoUrl, model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), model.getCompanyCoverImage(), model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getLng(), model.getLat(), model.getCategory());
                        if (res == 1) {
                            ArrayNode category = mapper.createArrayNode();

                            for (int i = 0; i < model.getCategory().size(); i++) {
                                ObjectNode node = mapper.createObjectNode();
                                node.put("category_name", model.getCategory().get(i).getCategory_name());
                                category.add(node);
                            }
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 200);
                            objectNode.put("company_id", model.getCompanyId());
                            objectNode.put("company_name", model.getCompanyName());
                            objectNode.put("company_logo_image", logoUrl);
                            objectNode.put("company_address", model.getCompanyAddress());
                            objectNode.put("company_link_youtube", model.getCompanyLinkYoutube());
                            objectNode.put("company_website_url", model.getCompanyWebsiteUrl());
                            objectNode.put("company_lng", model.getCompanyLng());
                            objectNode.put("company_lat", model.getCompanyLat());
                            objectNode.put("company_cover_image", model.getCompanyCoverImage());
                            objectNode.put("company_phone_number", model.getCompanyPhoneNumber());
                            objectNode.put("company_desc", model.getCompanyDesc());
                            objectNode.put("city", model.getCity());
                            objectNode.put("area", model.getArea());
                            objectNode.put("lng", model.getLng());
                            objectNode.put("lat", model.getLat());
                            objectNode.set("categories", category);

                            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                        } else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put(STATUS, 400);
                            objectNode.put(MESSAGE, "failed");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                        }

                    }


                } else {


                    int res = repo.updateProfile2(model.getCompanyId(), model.getCompanyName(), logoUrl, model.getCompanyAddress(),
                            model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                            model.getCompanyLat(), coverUrl, model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                            model.getCity(), model.getArea(), model.getLng(), model.getLat(), model.getCategory());
                    if (res == 1) {
                        ArrayNode category = mapper.createArrayNode();

                        for (int i = 0; i < model.getCategory().size(); i++) {
                            ObjectNode node = mapper.createObjectNode();
                            node.put("category_name", model.getCategory().get(i).getCategory_name());
                            category.add(node);
                        }
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put(STATUS, 200);
                        objectNode.put("company_id", model.getCompanyId());
                        objectNode.put("company_name", model.getCompanyName());
                        objectNode.put("company_logo_image", logoUrl);
                        objectNode.put("company_address", model.getCompanyAddress());
                        objectNode.put("company_link_youtube", model.getCompanyLinkYoutube());
                        objectNode.put("company_website_url", model.getCompanyWebsiteUrl());
                        objectNode.put("company_lng", model.getCompanyLng());
                        objectNode.put("company_lat", model.getCompanyLat());
                        objectNode.put("company_cover_image", coverUrl);
                        objectNode.put("company_phone_number", model.getCompanyPhoneNumber());
                        objectNode.put("company_desc", model.getCompanyDesc());
                        objectNode.put("city", model.getCity());
                        objectNode.put("area", model.getArea());
                        objectNode.put("lng", model.getLng());
                        objectNode.put("lat", model.getLat());
                        objectNode.set("categories", category);

                        return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                    } else {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put(STATUS, 400);
                        objectNode.put(MESSAGE, "failed");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }


                }


            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put(STATUS, 400);
                objectNode.put(MESSAGE, "already has profile in this id");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }


    @GetMapping("/s/category/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<NewProfileDto4DTO> getProfileByCategoryAndroid2(@PathVariable String id) {
        try {
            List<NewProfileDto2DTO> model = repo.getProfileByCategory2(id);
            if (model.size() > 0) {
                return ResponseEntity.status(HttpStatus.OK).body(new NewProfileDto4DTO("200", model));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new NewProfileDto4DTO("400", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new NewProfileDto4DTO("400", null));
        }
    }


    @GetMapping("/s/all/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public CompanyProfileDtoThreeDTO getTestObject2() {

        Map<CompanyProfileDtoTwoDTO, List<CompanyProfileDtoOne>> res = new HashMap<>();
        List<Map<String, Object>> list = repo.getCompaniesProfilesObject2();
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
            float lng = (float) map.get("lng");
            float lat = (float) map.get("lat");
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

        CompanyProfileDtoThreeDTO mainModel = new CompanyProfileDtoThreeDTO(schoolsList);


//        ObjectNode tenderNode = objectMapper.createObjectNode();
//        tenderNode.put("tender_id", mainModel.getTender_id());
//        tenderNode.put("tender_title", mainModel.getTender_title());
//        tenderNode.put("tender_explain", mainModel.getTender_explain());
//        ArrayNode tenderCategory = objectMapper.createArrayNode();

        return mainModel;
    }

    @PostMapping("/s/all/pages/{page}/{pageSize}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public CompanyProfileDtoThreeDTOPaginate getCompaniesProfilesObject2PaginationWithFilter(@PathVariable int page, @PathVariable int pageSize,
                                                                                             @RequestBody Filter2Model filter) {
        Map<CompanyProfileDtoTwoDTO, List<CompanyProfileDtoOne>> res = new HashMap<>();
        List<Map<String, Object>> list = repo.getCompaniesProfilesObject2PaginationWithFilter(filter.getName(),
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

    @GetMapping("/s/all/page/{page}/{pageSize}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public CompanyProfileDtoThreeDTOPaginate getTestObject2Pagination(@PathVariable int page, @PathVariable int pageSize) {

        int pages = (int) Math.ceil(((float) repo.getCompaniesProfilesObject2PaginationCount()) / ((float) pageSize));
        Map<CompanyProfileDtoTwoDTO, List<CompanyProfileDtoOne>> res = new HashMap<>();
        List<Map<String, Object>> list = repo.getCompaniesProfilesObject2Pagination();
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


    @GetMapping("/s/all/page/{page}/{pageSize}/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public CompanyProfileDtoThreeDTOPaginate getTestObject2Pagination(@PathVariable int page, @PathVariable int pageSize, @PathVariable int id) {

        int pages = (int) Math.ceil(((float) repo.getCompaniesProfilesObject2PaginationCount()) / ((float) pageSize));
        Map<CompanyProfileDtoTwoDTO, List<CompanyProfileDtoOne>> res = new HashMap<>();
        List<Map<String, Object>> list = repo.getCompaniesProfilesObject2Pagination2(id);
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


    @PostMapping("/s/all/page/{page}/{pageSize}/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public CompanyProfileDtoThreeDTOPaginate getTestObject2PaginationWithFilter(@PathVariable int page, @PathVariable int pageSize,
                                                                                @PathVariable int id, @RequestBody Filter2Model filter) {


        Map<CompanyProfileDtoTwoDTO, List<CompanyProfileDtoOne>> res = new HashMap<>();
        List<Map<String, Object>> list = repo.getCompaniesProfilesObject2Pagination2WithFilter(id, filter.getName(), filter.getCat(),
                filter.getArea(), filter.getCity(), filter.getView());
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

        int limitOffset = (page - 1) * pageSize;
        int pages = (int) Math.ceil(((float) schoolsList.size()) / ((float) pageSize));
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


    @GetMapping("/s/all/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public CompanyProfileDtoThreeDTO getTestObject22(@PathVariable int id) {

        Map<CompanyProfileDtoTwoDTO, List<CompanyProfileDtoOne>> res = new HashMap<>();
        List<Map<String, Object>> list = repo.getCompaniesProfilesObjectForAll2(id);
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
            int isFollow = (int) map.get("is_follow");
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
            model.setIs_follow(isFollow);
            model.setLng(lng);
            model.setLat(lat);
            //String encodedString = Base64.getEncoder().encodeToString(schoolLogo);
            if (schools.contains(model)) {
                continue;
            } else {
                schools.add(model);
                test2Models.add(test2Model);
                System.out.println("=====> this is the size   " + schools.size());
            }

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

        CompanyProfileDtoThreeDTO mainModel = new CompanyProfileDtoThreeDTO(schoolsList);


//        ObjectNode tenderNode = objectMapper.createObjectNode();
//        tenderNode.put("tender_id", mainModel.getTender_id());
//        tenderNode.put("tender_title", mainModel.getTender_title());
//        tenderNode.put("tender_explain", mainModel.getTender_explain());
//        ArrayNode tenderCategory = objectMapper.createArrayNode();

        return mainModel;
    }


    @PostMapping("/s/all/{id}/pages/{page}/{pageSize}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public CompanyProfileDtoThreeDTOPaginate getTestObject22PaginationWithFilter(@PathVariable int id, @PathVariable int page,
                                                                                 @PathVariable int pageSize, @RequestBody Filter3Model filter) {
        Map<CompanyProfileDtoTwoDTO, List<CompanyProfileDtoOne>> res = new HashMap<>();
        List<Map<String, Object>> list = repo.getCompaniesProfilesObjectForAll2WithFilter(id, filter.getName(), filter.getCat(),
                filter.getArea(), filter.getCity(), filter.getView(), filter.getFollow());
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
            int isFollow = (int) map.get("is_follow");
            float lng = 0;
            try {
                lng = (float) map.get("lng");
            } catch (Exception e) {

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
            model.setIs_follow(isFollow);
            model.setLng(lng);
            model.setLat(lat);
            //String encodedString = Base64.getEncoder().encodeToString(schoolLogo);
            if (schools.contains(model)) {
                continue;
            } else {
                schools.add(model);
                test2Models.add(test2Model);
                System.out.println("=====> this is the size   " + schools.size());
            }

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
        int pages = (int) Math.ceil(((float) schoolsList.size()) / ((float) pageSize));
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


    @GetMapping("/s/all/{id}/page/{page}/{pageSize}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public CompanyProfileDtoThreeDTOPaginate getTestObject22Pagination(@PathVariable int id, @PathVariable int page, @PathVariable int pageSize) {
        int pages = (int) Math.ceil(((float) repo.getCompaniesProfilesObjectForAll2PaginateCount(id)) / ((float) pageSize));
        Map<CompanyProfileDtoTwoDTO, List<CompanyProfileDtoOne>> res = new HashMap<>();
        List<Map<String, Object>> list = repo.getCompaniesProfilesObjectForAll2(id);
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
            int isFollow = (int) map.get("is_follow");
            float lng = 0;
            try {
                lng = (float) map.get("lng");
            } catch (Exception e) {

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
            model.setIs_follow(isFollow);
            model.setLng(lng);
            model.setLat(lat);
            //String encodedString = Base64.getEncoder().encodeToString(schoolLogo);
            if (schools.contains(model)) {
                continue;
            } else {
                schools.add(model);
                test2Models.add(test2Model);
                System.out.println("=====> this is the size   " + schools.size());
            }

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
        } catch (Exception e) {
            CompanyProfileDtoThreeDTOPaginate mainModel = new CompanyProfileDtoThreeDTOPaginate(pages, null);
            return mainModel;
        }
    }


    public String imageUrl(MultipartFile file, Path rootLoc) throws Exception {
        String s = file.getOriginalFilename().replace("\\s+", "");
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getOriginalFilename().replace(file.getOriginalFilename(),
                        FilenameUtils.getBaseName(s.replaceAll("\\s+", "")).replaceAll("\\s+", "")
                                .concat(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())) + "." +
                                FilenameUtils.getExtension(file.getOriginalFilename())).toLowerCase().replaceAll("\\s+", ""), "image/*", file.getInputStream());
        System.out.println(MvcUriComponentsBuilder
                .fromMethodName(UploadController.class, "getFile", multipartFile.getOriginalFilename()).build().toString());
        try {
            if (Files.isDirectory(rootLoc)) {

            } else {
                Files.createDirectory(rootLoc);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage!");
        }
        storageService.store(multipartFile);
        String logoImage = MvcUriComponentsBuilder
                .fromMethodName(UploadController.class, "getFile", multipartFile.getOriginalFilename()).build().toString();

        return logoImage;

    }


}
