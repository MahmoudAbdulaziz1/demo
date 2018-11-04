package com.taj.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.controller.test_image.StorageService;
import com.taj.controller.test_image.UploadController;
import com.taj.model.*;
import com.taj.model.Pagination.*;
import com.taj.model.collective_image.TenderRequestTenderModel2Dto;
import com.taj.model.company_offer_response_count.CustomeCompanyOfferModel2DToModel;
import com.taj.model.company_offer_response_count.getCustomeCompanyOffer2Model;
import com.taj.model.copany_offer_iamge.MultiCategoryProfileModelWithImage;
import com.taj.model.new_company_history.CompanyHistoryDto;
import com.taj.model.new_company_history.CompanyHistoryDto2;
import com.taj.model.new_school_history.SchoolRequestHistoryDtoDTO;
import com.taj.model.new_school_history.SchoolRequestHistoryDtoDTO2;
import com.taj.model.new_school_profile_map.NewCustomSchoolProfileModelDTO;
import com.taj.model.new_school_profile_map.NewSchoolProfileModelDTO;
import com.taj.model.new_school_request.*;
import com.taj.model.offer_description.CustomCompanyModelWithViewAndDescRes;
import com.taj.model.offer_description.getCustomeOffer2;
import com.taj.model.school.request.image.SchoolNewRequestsDTO2;
import com.taj.model.school_history_admin_dashboard.SchoolOrdersHistoryModel;
import com.taj.model.school_history_admin_dashboard.SchoolOrdersModel;
import com.taj.model.school_history_admin_dashboard.SingleSchoolOrdersModel;
import com.taj.model.test_image.CustomCompanyOfferModelWithImage;
import com.taj.repository.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by User on 9/5/2018.
 */
@RequestMapping("/service/evvaz")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class DasboardsAPIControll {


    private static final String STATUS = "status";
    private static final String MESSAGE = "message";
    private static final String SUCCESS = "success";
    private static final String FAILED = "failed";
    private final Path rootLocation = Paths.get("upload-dir");
    private final Path schoolRootLocation = Paths.get("upload-dir");
    private final Path schoolCoverRootLocation = Paths.get("upload-dir");
    private final Path takatafRootLocation = Paths.get("upload-dir");

    @Autowired
    AdminOrdersRepo adminRepo;
    @Autowired
    TakatafTenderNewRepo repo;
    @Autowired
    CategoryRepo catRepo;
    @Autowired
    RegistrationRepo registrationRepo;
    @Autowired
    NewRegisterRepo newRegisterRepo;
    @Autowired
    TenderRequestRepo tenderRequestRepo;
    @Autowired
    TakatafTenderRequestRepo takatafTenderRequestRepo;
    @Autowired
    CollectiveTenderDetailsForCompanyRepo collectiveTenderDetailsForCompanyRepo;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    CollectiveTenderCompaniesRequestForCompanyRepo collectiveTenderCompaniesRequestForCompanyRepo;
    @Autowired
    AdminOrdersRepo adminOrdersRepo;
    @Autowired
    SchoolFollowCompanyRepo schoolFollowCompanyRepo;
    @Autowired
    OrdersRepo ordersRepo;
    @Autowired
    SchoolRequestNewRepo schoolRequestNewRepo;
    @Autowired
    SchoolRequestRepo schoolRequestRepo;
    @Autowired
    SchoolRequestCategoryRepo schoolRequestCategoryRepo;
    @Autowired
    CompanyResponseSchoolRequestRepo companyResponseSchoolRequestRepo;
    @Autowired
    CustomCompanyOfferRepo customCompanyOfferRepo;
    @Autowired
    CompanyOfferRepo companyOfferRepo;
    List<String> files = new ArrayList<String>();
    @Autowired
    StorageService storageService;
    @Autowired
    MultiCategoryProfileRepo multiCategoryProfileRepo;
    @Autowired
    NewProfileRepo newProfileRepo;
    @Autowired
    private SchoolProfileRepo schoolProfileRepo;
    @Autowired
    private NewSchoolProfileRepo newSchoolProfileRepo;

    @GetMapping("/getallfiles")
    public ResponseEntity<List<String>> getListFiles(Model model) {
        List<String> fileNames = files
                .stream().map(fileName -> MvcUriComponentsBuilder
                        .fromMethodName(CustomComapnyOfferController.class, "getFile", fileName).build().toString())
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(fileNames);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.loadFile(filename, takatafRootLocation);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }


    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("admin/orders/history/")
    public List<AdminHistoryOrdersModel> getAllHistoryOrders() {
        return adminRepo.getAllHistoryOrders();
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("admin/orders/history/page/{page}/{pageSize}")
    public AdminHistoryOrdersModelPagination getAllHistoryOrdersWithPagination(@PathVariable int page, @PathVariable int pageSize) {
//        int resCount = adminRepo.getAllHistoryOrdersWithPaginationCount();
//        System.out.println("Count is " + resCount);
        return adminRepo.getAllHistoryOrdersWithPagination(page, pageSize);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("takataf/tenders/")
    public ResponseEntity<ObjectNode> addTender(@RequestParam("logo") MultipartFile logo,
                                                @RequestPart @Valid String takatafTenderNewModelString, Errors errors) {


        try {

            TakatafTenderNewModel model = new ObjectMapper().readValue(takatafTenderNewModelString, TakatafTenderNewModel.class);
            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
            if (model.getTender_display_date() < new Timestamp(System.currentTimeMillis()).getTime()
                    || model.getTender_expire_date() < new Timestamp(System.currentTimeMillis()).getTime()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed school date in past");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }

            if (model.getTender_display_date() >= model.getTender_expire_date()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed school display date is greater than  expired date");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
            if (model.getTender_company_expired_date() == 0 || model.getTender_company_expired_date() == 0) {

            } else {

                if (model.getTender_company_display_date() < model.getTender_expire_date()) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("state", 400);
                    objectNode.put("message", "Validation Failed company date in past");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }

                if (model.getTender_company_display_date() < new Timestamp(System.currentTimeMillis()).getTime()
                        || model.getTender_company_expired_date() < new Timestamp(System.currentTimeMillis()).getTime()) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("state", 400);
                    objectNode.put("message", "Validation Failed company date in past");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }

                if (model.getTender_company_display_date() >= model.getTender_company_expired_date()) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("state", 400);
                    objectNode.put("message", "Validation Failed company display date is greater than  expired date");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }
            }

            String logoUrl = imageUrl(logo, takatafRootLocation);
            if (model.getCats().isEmpty()) {

                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed ccat is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);


            }
            int res = repo.addTender(logoUrl, model.getTender_title(), model.getTender_explain(),
                    model.getTender_display_date(), model.getTender_expire_date(), model.getTender_company_display_date(),
                    model.getTender_company_expired_date(), model.getCats(), model.getFlag_ar());

            if (res > 0) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("tender_logo", logoUrl);
                objectNode.put("tender_title", model.getTender_title());
                objectNode.put("tender_explain", model.getTender_explain());
                objectNode.put("tender_display_date", model.getTender_display_date());
                objectNode.put("tender_expire_date", model.getTender_expire_date());
                objectNode.put("tender_company_display_date", model.getTender_company_display_date());
                objectNode.put("tender_company_expire_date", model.getTender_company_expired_date());
                objectNode.put("flag_ar", model.getFlag_ar());


                return ResponseEntity.status(HttpStatus.OK).body(objectNode);
            } else if (res == -100) {
                ObjectNode objectNode = mapper.createObjectNode();
                ArrayNode nodes = mapper.createArrayNode();
                if (model.getFlag_ar() == 0) {
                    List<CategoriesInUse> data = repo.getCategoriesInUse();
                    for (int i = 0; i < data.size(); i++) {
                        int categorys = repo.getCategoryId(data.get(i).getCategory_name(), model.getFlag_ar());

                        if (categorys > 0) {
                            nodes.add(data.get(i).getCategory_name());
                        }
                    }
                    objectNode.set("cats", nodes);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                } else {
                    List<CategoriesInUseArabic> data = repo.getCategoriesInUseArabic();
                    for (int i = 0; i < data.size(); i++) {
                        int categorys = repo.getCategoryId(data.get(i).getCategory_name_ar(), model.getFlag_ar());

                        if (categorys > 0) {
                            nodes.add(data.get(i).getCategory_name_ar());
                        }
                    }
                    objectNode.set("cats", nodes);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("value", "not success");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }


        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success e    " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("takataf/tenders/")
    public List<TakatafTenderPOJO> getTenders() {
        return repo.getTenders();
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("takataf/tenders/page/{page}/{pageSize}")
    public TakatafTenderPOJOPagination getTendersPagination(@PathVariable int page, @PathVariable int pageSize) {
        return repo.getTendersPagination(page, pageSize);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("takataf/tenders/{id}")
    public ResponseEntity<TakatafTenderPOJO> getTender(@PathVariable int id) {
        if (repo.isExist(id)) {
            return ResponseEntity.status(HttpStatus.OK).body(repo.getTender(id));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(repo.getTender(id));
        }


    }

    @PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    @GetMapping("takataf/tenders/admin")
    public ResponseEntity<ArrayNode> getAdminTenders() {
        List<TakatafMyTenderPageDTO> model = repo.getAdminTenders();
        ArrayNode arr = mapper.createArrayNode();
        for (int i = 0; i < model.size(); i++) {
            ObjectNode nodes = mapper.createObjectNode();
            nodes.put("tender_id", model.get(i).getTender_id());
            nodes.put("tender_title", model.get(i).getTender_title());
            nodes.put("tender_logo", model.get(i).getTender_logo());
            nodes.put("tender_explain", model.get(i).getTender_explain());
            if (model.get(i).getTender_company_display_date() == 0) {
                String nullvalue = null;
                nodes.put("tender_company_display_date", nullvalue);

            } else {
                nodes.put("tender_company_display_date", model.get(i).getTender_company_display_date());
            }
            if (model.get(i).getTender_company_expired_date() == 0) {
                String nullvalue = null;
                nodes.put("tender_company_expired_date", nullvalue);

            } else {
                nodes.put("tender_company_expired_date", model.get(i).getTender_company_expired_date());

            }
            nodes.put("tender_display_date", model.get(i).getTender_display_date());
            nodes.put("tender_expire_date", model.get(i).getTender_expire_date());
            nodes.put("response_count", model.get(i).getResponse_count());
            nodes.put("cat_num", model.get(i).getCat_num());
            arr.add(nodes);
        }
        return ResponseEntity.status(HttpStatus.OK).body(arr);

    }

    @PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    @GetMapping("takataf/tenders/admin/page/{page}/{pageSize}")
    public TakatafMyTenderPageDTOPagination getAdminTendersPagination(@PathVariable int page, @PathVariable int pageSize) {
        return repo.getAdminTendersPagination(page, pageSize);
    }

    @PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    @GetMapping("takataf/tenders/admins/page/{page}/{pageSize}")
    public TakatafMyTenderPageDTOPagination getAdminTendersPaginationSchool(@PathVariable int page, @PathVariable int pageSize) {
        return repo.getAdminTendersPaginationSchool(page, pageSize);
    }

    @PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    @GetMapping("takataf/tenders/history/admin")
    public ResponseEntity<ArrayNode> getAdminTendersHistory() {
        List<TakatafMyTenderPageDTO> model = repo.getAdminTendersHistory();
        ArrayNode arr = mapper.createArrayNode();
        for (int i = 0; i < model.size(); i++) {
            ObjectNode nodes = mapper.createObjectNode();
            nodes.put("tender_id", model.get(i).getTender_id());
            nodes.put("tender_title", model.get(i).getTender_title());
            nodes.put("tender_logo", model.get(i).getTender_logo());
            nodes.put("tender_explain", model.get(i).getTender_explain());
            if (model.get(i).getTender_company_display_date() == 0) {
                String nullvalue = null;
                nodes.put("tender_company_display_date", nullvalue);

            } else {
                nodes.put("tender_company_display_date", model.get(i).getTender_company_display_date());
            }
            if (model.get(i).getTender_company_expired_date() == 0) {
                String nullvalue = null;
                nodes.put("tender_company_expired_date", nullvalue);

            } else {
                nodes.put("tender_company_expired_date", model.get(i).getTender_company_expired_date());

            }
            nodes.put("tender_display_date", model.get(i).getTender_display_date());
            nodes.put("tender_expire_date", model.get(i).getTender_expire_date());
            nodes.put("response_count", model.get(i).getResponse_count());
            nodes.put("cat_num", model.get(i).getCat_num());
            arr.add(nodes);
        }
        return ResponseEntity.status(HttpStatus.OK).body(arr);

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    @GetMapping("takataf/tenders/history/admin/page/{page}/{pageSize}")
    public TakatafMyTenderPageDTOPagination getAdminTendersHistoryPagination(@PathVariable int page, @PathVariable int pageSize) {
        return repo.getAdminTendersHistoryPagination(page, pageSize);
    }


    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("takataf/tenders/")
    public ResponseEntity<ObjectNode> updateTender(@RequestParam(value = "logo", required = false) MultipartFile logo,
                                                   @RequestPart String takatafTenderUpdatePOJOString, Errors errors) {


        try {

            TakatafTenderUpdatePOJO2 model = new ObjectMapper().readValue(takatafTenderUpdatePOJOString, TakatafTenderUpdatePOJO2.class);

            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }


//        if (model.getTender_display_date() < new Timestamp(System.currentTimeMillis()).getTime()
//                || model.getTender_expire_date() < new Timestamp(System.currentTimeMillis()).getTime()) {
//            ObjectNode objectNode = mapper.createObjectNode();
//            objectNode.put("state", 400);
//            objectNode.put("message", "Validation Failed school date in past");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
//        }

            if (model.getTender_display_date() >= model.getTender_expire_date()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed school display date is greater than  expired date");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }

            if (model.getTender_company_expired_date() == 0 || model.getTender_company_expired_date() == 0) {

            } else {

                if (model.getTender_company_display_date() < new Timestamp(System.currentTimeMillis()).getTime()
                        || model.getTender_company_expired_date() < new Timestamp(System.currentTimeMillis()).getTime()) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("state", 400);
                    objectNode.put("message", "Validation Failed company date in past");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }

                if (model.getTender_company_display_date() >= model.getTender_company_expired_date()) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("state", 400);
                    objectNode.put("message", "Validation Failed company display date is greater than  expired date");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }
            }

            String logoUrl = "";
            try {
                logoUrl = imageUrl(logo, rootLocation);
            } catch (Exception e) {
                logoUrl = "";
            }

            if (logoUrl.equals("") || logoUrl.isEmpty() || logoUrl.equals(null)) {
                int res = repo.updateTender(model.getTender_id(), model.getTender_logo(), model.getTender_title(),
                        model.getTender_explain(), model.getTender_display_date(), model.getTender_expire_date(),
                        model.getTender_company_display_date(), model.getTender_company_expired_date(), model.getCats(), model.getFlag_ar());

                if (res > 0) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    //objectNode.put("tender_id", model.getTender_id());
                    objectNode.put("tender_logo", model.getTender_logo());
                    objectNode.put("tender_title", model.getTender_title());
                    objectNode.put("tender_explain", model.getTender_explain());
                    objectNode.put("tender_display_date", model.getTender_display_date());
                    objectNode.put("tender_expire_date", model.getTender_expire_date());
                    objectNode.put("tender_company_display_date", model.getTender_company_display_date());
                    objectNode.put("tender_company_expire_date", model.getTender_company_expired_date());
                    objectNode.put("flag_ar", model.getFlag_ar());


                    return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                } else if (res == -100) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    ArrayNode nodes = mapper.createArrayNode();


                    if (model.getFlag_ar() == 0) {
                        List<CategoriesInUse> data = repo.getCategoriesInUse();
                        for (int i = 0; i < data.size(); i++) {
                            int categorys = repo.getCategoryId(data.get(i).getCategory_name(), model.getFlag_ar());

                            if (categorys > 0) {
                                nodes.add(data.get(i).getCategory_name());
                            }
                        }
                        objectNode.set("cats", nodes);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    } else {
                        List<CategoriesInUseArabic> data = repo.getCategoriesInUseArabic();
                        for (int i = 0; i < data.size(); i++) {
                            int categorys = repo.getCategoryId(data.get(i).getCategory_name_ar(), model.getFlag_ar());

                            if (categorys > 0) {
                                nodes.add(data.get(i).getCategory_name_ar());
                            }
                        }
                        objectNode.set("cats", nodes);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }


                } else {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("value", "not success");

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }
            } else {
                int res = repo.updateTender(model.getTender_id(), logoUrl, model.getTender_title(),
                        model.getTender_explain(), model.getTender_display_date(), model.getTender_expire_date(),
                        model.getTender_company_display_date(), model.getTender_company_expired_date(), model.getCats(), model.getFlag_ar());

                if (res > 0) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    //objectNode.put("tender_id", model.getTender_id());
                    objectNode.put("tender_logo", logoUrl);
                    objectNode.put("tender_title", model.getTender_title());
                    objectNode.put("tender_explain", model.getTender_explain());
                    objectNode.put("tender_display_date", model.getTender_display_date());
                    objectNode.put("tender_expire_date", model.getTender_expire_date());
                    objectNode.put("tender_company_display_date", model.getTender_company_display_date());
                    objectNode.put("tender_company_expire_date", model.getTender_company_expired_date());
                    objectNode.put("flag_ar", model.getFlag_ar());


                    return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                } else if (res == -100) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    ArrayNode nodes = mapper.createArrayNode();
                    List<CategoriesInUse> data = repo.getCategoriesInUse();
                    for (int i = 0; i < data.size(); i++) {
                        int categorys = repo.getCategoryId(data.get(i).getCategory_name(), model.getFlag_ar());

                        if (categorys > 0) {
                            nodes.add(data.get(i).getCategory_name());
                        }
                    }
                    objectNode.set("cats", nodes);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                } else {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("value", "not success");

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }
            }


        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success e  " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("takataf/tenders/{id}")
    public int deleteTenderWithItsResponse(@PathVariable int id) {
        return repo.deleteTenderWithItsResponse(id);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("cat/getAll/{flag_ar}")
    public List<CategoryModelEnglish> getCategories(@PathVariable int flag_ar) {
        return catRepo.getCategories(flag_ar);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("register/getInActive")
    public List<RegistrationModel> getInActiveCompaines() {
        return registrationRepo.getInActiveCompanies();
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("register/getInActives/{flag_ar}")
    public List<NewRegisterModel> getInActiveCompainesNew(@PathVariable int flag_ar) {
        return newRegisterRepo.getInActiveCompanies(flag_ar);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("register/getInActives/page/{page}/{pageSize}/{flag_ar}")
    public NewRegisterModelPagination getInActiveCompainesNewPagination(@PathVariable int page, @PathVariable int pageSize, @PathVariable int flag_ar) {
        return newRegisterRepo.getInActiveCompaniesPagination(page, pageSize, flag_ar);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("register/confirm/{id}")
    public ObjectNode confirmEmail(@PathVariable int id) {
        int res = registrationRepo.confirmEmail(id);

        if (res == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "success");

            return objectNode;
        } else if (res == -100) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "email with role already exist");

            return objectNode;
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success");

            return objectNode;
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("register/confirms/{id}/{flag_ar}")
    public ObjectNode confirmEmailWithCity(@PathVariable int id, @PathVariable int flag_ar) {
        int res = newRegisterRepo.confirmEmail(id, flag_ar);

        if (res == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "success");

            return objectNode;
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success");

            return objectNode;
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    @PreAuthorize("hasAuthority('admin')")
//    @GetMapping("register/get/{id}")
//    public NewRegisterModel getUser(@PathVariable int id) {
//        return newRegisterRepo.getUser(id);
//    }
//
//

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("register/get/{id}/{flag_ar}")
    public ResponseEntity<ObjectNode> getUser(@PathVariable int id, @PathVariable int flag_ar) {
        ObjectNode objectNode = mapper.createObjectNode();
        RegistrationModel model = null ;
        try {
            model = registrationRepo.getUser(id);
        } catch (Exception e) {
            objectNode.put("message", "not date to this id");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

        objectNode.put("registration_id", model.getRegistration_id());
        objectNode.put("registeration_email", model.getRegisteration_email());
        objectNode.put("registeration_password", model.getRegisteration_password());
        objectNode.put("registeration_username", model.getRegisteration_username());
        objectNode.put("registeration_phone_number", model.getRegisteration_phone_number());
        objectNode.put("registration_organization_name", model.getRegistration_organization_name());
        objectNode.put("registration_address_desc", model.getRegistration_address_desc());
        objectNode.put("registration_website_url", model.getRegistration_website_url());
        objectNode.put("registration_isActive", model.getRegistration_isActive());
        if (flag_ar == 0) {
            objectNode.put("registration_role",model.getRegistration_role());
        } else {
            if (model.getRegistration_role().equals("school")){
                objectNode.put("registration_role", "مدرسة");

            }else if (model.getRegistration_role().equals("company")) {
                objectNode.put("registration_role", "شركة");

            }
        }
        objectNode.put("registeration_date", model.getRegisteration_date());

        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("register/get/address/{id}")
    public NewRegisterModel getUserWithAddresData(@PathVariable int id, int flag_ar) {
        return newRegisterRepo.getUser(id, flag_ar);
    }

    @PreAuthorize("hasAuthority('admin') OR hasAuthority('school')")
    @GetMapping("request/tender/{id}/{flag_ar}")
    public TenderRequestTenderModel2Dto getTenderRequestObject(@PathVariable int id, @PathVariable int flag_ar) {
        Map<TenderRequestSchoolModel, List<TenderRequestCategoriesModel>> res = new HashMap<>();
        List<Map<String, Object>> list = tenderRequestRepo.getTenderRequestObjectWithCompanyDates(id, flag_ar);
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

    @PreAuthorize("hasAuthority('admin') OR hasAuthority('school')")
    @GetMapping("request/tender/{id}/page/{page}/{pageSize}/{flag_ar}")
    public TenderRequestTenderModel2DtoPagination getTenderRequestObjectPagination(@PathVariable int id, @PathVariable int page, @PathVariable int pageSize,
                                                                                   @PathVariable int flag_ar) {

        Map<TenderRequestSchoolModel, List<TenderRequestCategoriesModel>> res = new HashMap<>();
        List<Map<String, Object>> list = tenderRequestRepo.getTenderRequestObjectWithCompanyDates(id, flag_ar);
        List<TenderRequestSchoolModel> schoolsList = new ArrayList<>();
        Set<TenderRequestSchoolModel> schools = new HashSet<>();
        List<TenderRequestCategoriesModel> test2Models = new ArrayList<>();
        for (Map<String, Object> map : list) {
            TenderRequestSchoolModel model = new TenderRequestSchoolModel();
            TenderRequestCategoriesModel test2Model = new TenderRequestCategoriesModel();


            long schoolId = (long) map.get("school_id");
            String schoolName = (String) map.get("school_name");
            String schoolLogo = (String) map.get("school_logo_image");
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
                model.setSchool_logo_image(schoolLogo);
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

            TenderRequestTenderModel2DtoPagination mainModel = new TenderRequestTenderModel2DtoPagination(Long.parseLong(list.get(0).get("tender_id").toString()),
                    (String) list.get(0).get("tender_logo"),
                    (String) list.get(0).get("tender_title"),
                    (String) list.get(0).get("tender_explain"), ((Timestamp) (list.get(0).get("tender_display_date"))).getTime(),
                    ((Timestamp) list.get(0).get("tender_expire_date")).getTime(),
                    ((Timestamp) list.get(0).get("tender_company_display_date")).getTime(),
                    ((Timestamp) list.get(0).get("tender_company_expired_date")).getTime(),
                    Long.parseLong(list.get(0).get("response_count").toString()), category, null, 0, 0);


            return mainModel;
        } else {
            int pages = (int) Math.ceil(((float) schoolsList.size()) / ((float) pageSize));
            System.out.println("Page Size =   " + pages);
            int limitOffset = (page - 1) * pageSize;


            List<TenderRequestSchoolModel> resList = new ArrayList<>();
            for (int i = limitOffset; i < (limitOffset + pageSize); i++) {
                try {
                    resList.add(schoolsList.get(i));
                } catch (Exception e) {

                }
            }
            List<CategoryNameDto> category = takatafTenderRequestRepo.categoryData(id, flag_ar);

            try {
                TenderRequestTenderModel2DtoPagination mainModel = new TenderRequestTenderModel2DtoPagination(Long.parseLong(list.get(0).get("tender_id").toString()),
                        (String) list.get(0).get("tender_logo"),
                        (String) list.get(0).get("tender_title"),
                        (String) list.get(0).get("tender_explain"), ((Timestamp) (list.get(0).get("tender_display_date"))).getTime(),
                        ((Timestamp) list.get(0).get("tender_expire_date")).getTime(),
                        ((Timestamp) list.get(0).get("tender_company_display_date")).getTime(),
                        ((Timestamp) list.get(0).get("tender_company_expired_date")).getTime(),
                        Long.parseLong(list.get(0).get("response_count").toString()), category, resList, schoolsList.size(), pages);


                return mainModel;
            } catch (Exception e) {
                TenderRequestTenderModel2DtoPagination mainModel = new TenderRequestTenderModel2DtoPagination(Long.parseLong(list.get(0).get("tender_id").toString()),
                        (String) list.get(0).get("tender_logo"),
                        (String) list.get(0).get("tender_title"),
                        (String) list.get(0).get("tender_explain"), ((Timestamp) (list.get(0).get("tender_display_date"))).getTime(),
                        ((Timestamp) list.get(0).get("tender_expire_date")).getTime(),
                        ((Timestamp) list.get(0).get("tender_company_display_date")).getTime(),
                        ((Timestamp) list.get(0).get("tender_company_expired_date")).getTime(),
                        Long.parseLong(list.get(0).get("response_count").toString()), category, null, 0, 0);
                return mainModel;
            }


        }


    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/register/Archive/{id}")
    public ResponseEntity<ObjectNode> archiveCompanyRequest(@PathVariable int id) {
        int respons = newRegisterRepo.archiveCompanyRequest(id);
        if (respons == 1) {
            ObjectNode nodes = mapper.createObjectNode();
            nodes.put(MESSAGE, SUCCESS);
            return ResponseEntity.status(HttpStatus.OK).body(nodes);
        }

        ObjectNode nodes = mapper.createObjectNode();
        nodes.put(MESSAGE, FAILED);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(nodes);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("register/unArchive/{id}")
    public ResponseEntity<ObjectNode> unArchiveCompanyRequest(@PathVariable int id) {
        int respons = newRegisterRepo.unArchiveCompanyRequest(id);
        if (respons == 1) {
            ObjectNode nodes = mapper.createObjectNode();
            nodes.put(MESSAGE, SUCCESS);
            return ResponseEntity.status(HttpStatus.OK).body(nodes);
        }

        ObjectNode nodes = mapper.createObjectNode();
        nodes.put(MESSAGE, FAILED);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(nodes);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("register/consider/{id}")
    public ResponseEntity<ObjectNode> considrateCompanyRequest(@PathVariable int id) {
        int respons = newRegisterRepo.considrateCompanyRequest(id);
        if (respons == 1) {
            ObjectNode nodes = mapper.createObjectNode();
            nodes.put(MESSAGE, SUCCESS);
            return ResponseEntity.status(HttpStatus.OK).body(nodes);
        }

        ObjectNode nodes = mapper.createObjectNode();
        nodes.put(MESSAGE, FAILED);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(nodes);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("register/unconsider/{id}")
    public ResponseEntity<ObjectNode> unCosidrateCompanyRequest(@PathVariable int id) {
        int respons = newRegisterRepo.unCosidrateCompanyRequest(id);
        if (respons == 1) {
            ObjectNode nodes = mapper.createObjectNode();
            nodes.put(MESSAGE, SUCCESS);
            return ResponseEntity.status(HttpStatus.OK).body(nodes);
        }

        ObjectNode nodes = mapper.createObjectNode();
        nodes.put(MESSAGE, FAILED);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(nodes);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("register/getInActive/archived")
    public List<NewRegisterModel> getInActiveCompaniesArchived() {
        return newRegisterRepo.getInActiveCompaniesArchived();
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("register/getInActive/archived/page/{page}/{pageSize}/{flag_ar}")
    public NewRegisterModelPagination getInActiveCompaniesArchivedPagination(@PathVariable int page, @PathVariable int pageSize, @PathVariable int flag_ar) {
        return newRegisterRepo.getInActiveCompaniesArchived(page, pageSize, flag_ar);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("register/getInActive/consider")
    public List<NewRegisterModel> getInActiveCompaniesConsiderate() {
        return newRegisterRepo.getInActiveCompaniesConsiderate();
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("register/getInActive/consider/page/{page}/{pageSize}/{flag_ar}")
    public NewRegisterModelPagination getInActiveCompaniesConsideratePagination(@PathVariable int page, @PathVariable int pageSize, @PathVariable int flag_ar) {
        return newRegisterRepo.getInActiveCompaniesConsideratePaginations(page, pageSize, flag_ar);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("register/getInActive/both")
    public List<NewRegisterModel> getInActiveCompaniesBoth() {
        return newRegisterRepo.getInActiveCompaniesBoth();
    }


//    @PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
//    @GetMapping("/tender/request/{id}")
//    public GetSingCollectiveTenderById2 getAllRequestsWithNameById(@PathVariable int id) {
//        List<TakatafSinfleSchoolRequestDTO> tenderList = takatafTenderRequestRepo.getAllRequestsWithNameByTender(id);
//        List<CategoryNameDto> categorie = takatafTenderRequestRepo.categoryData(id);
//
//        GetSingleCollectiveByIdPartOneDTO2 data = new GetSingleCollectiveByIdPartOneDTO2(tenderList.get(0).getTender_id(), tenderList.get(0).getTender_title(),
//                tenderList.get(0).getTender_explain(), tenderList.get(0).getTender_display_date(),
//                tenderList.get(0).getTender_expire_date(), tenderList.get(0).getResponse_count(), categorie);
//        List<GetSingleCollectiveByIdPartTwoDTO> schools = new ArrayList<>();
//        List<TenderCollectiveByIdPart3DTO> categories = new ArrayList<>();
//        if (tenderList.get(0).getResponse_count() > 0) {
//
//            TenderCollectiveByIdPart3DTO category = new TenderCollectiveByIdPart3DTO(tenderList.get(0).getId(),
//                    tenderList.get(0).getCategory_name(), tenderList.get(0).getCount());
//            categories.add(category);
//            GetSingleCollectiveByIdPartTwoDTO school0 = new GetSingleCollectiveByIdPartTwoDTO
//                    (tenderList.get(0).getSchool_name(), tenderList.get(0).getSchool_logo_image(), categories);
//            schools.add(school0);
//            int i = 1;
//            while (i < tenderList.size()) {
//                if (tenderList.get(i).getTender_id() == tenderList.get(i - 1).getTender_id()) {
//                    int j = i;
//                    TenderCollectiveByIdPart3DTO categorys = new TenderCollectiveByIdPart3DTO(tenderList.get(i).getId(),
//                            tenderList.get(i).getCategory_name(), tenderList.get(i).getCount());
//                    categories.add(categorys);
//                } else {
//                    GetSingleCollectiveByIdPartTwoDTO school = new GetSingleCollectiveByIdPartTwoDTO
//                            (tenderList.get(i).getSchool_name(), tenderList.get(i).getSchool_logo_image(), categories);
//                    schools.add(school);
//                }
//                i++;
//            }
//
//
//        }
//
//
//        GetSingCollectiveTenderById2 tener = new GetSingCollectiveTenderById2(data, schools);
//        return tener;
//    }

    @PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    @GetMapping("/tender/request/{id}/{flag_ar}")
    public TenderRequestTenders getAllRequestsWithNameById2(@PathVariable int id, @PathVariable int flag_ar) {


        Map<TenderRequestSchools, List<TenderRequestCategories>> res = new HashMap<>();
        List<Map<String, Object>> list = takatafTenderRequestRepo.getAllRequestsWithNameByTender2(id, flag_ar);
        List<TenderRequestSchools> schoolsList = new ArrayList<>();
        Set<TenderRequestSchools> schools = new HashSet<>();
        List<TenderRequestCategories> test2Models = new ArrayList<>();
        for (Map<String, Object> map : list) {
            TenderRequestSchools model = new TenderRequestSchools();
            TenderRequestCategories test2Model = new TenderRequestCategories();

            try {
                int schoolId = (int) map.get("school_id");
                String schoolName = (String) map.get("school_name");
                byte[] schoolLogo = (byte[]) map.get("school_logo_image");

                long categoryId = (long) map.get("id");
                String categoryName = (String) map.get("category_name");
                long count = (long) map.get("count");


                test2Model.setId(categoryId);
                test2Model.setCategory_name(categoryName);
                test2Model.setCount(count);

                model.setSchool_id(schoolId);
                model.setSchool_name(schoolName);
                model.setSchool_logo_image(Base64.getEncoder().encodeToString(schoolLogo));
                //String encodedString = Base64.getEncoder().encodeToString(schoolLogo);
                schools.add(model);

                test2Models.add(test2Model);
            } catch (NullPointerException e) {
                schools.clear();
            }
        }
        for (TenderRequestSchools obj : schools) {
            System.out.println(obj.toString());
//            res.put(obj,new ArrayList<Test2Model>());
//            List<Test2Model>  test2ModelArrayList=new ArrayList<>();
            List<TenderRequestCategories> test2ModelArrayList = null;
            test2ModelArrayList = new ArrayList<>();

            int i = 0;
            for (Map<String, Object> map : list) {
//                obj.getCategories().add(test2ModelList);


                if (res.get(obj) == null) {
                    res.put(obj, new ArrayList<TenderRequestCategories>());

                }
//                if (res.containsKey(obj)) {
                if (map.get("school_id").equals(obj.getSchool_id())) {
                    TenderRequestCategories test2Model = new TenderRequestCategories();
                    long categoryId = (long) map.get("id");
                    String categoryName = (String) map.get("category_name");
                    long count = (long) map.get("count");


                    test2Model.setId(categoryId);
                    test2Model.setCategory_name(categoryName);
                    test2Model.setCount(count);

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

        List<CategoryNameDto> categorie = takatafTenderRequestRepo.categoryData(id, flag_ar);
        TenderRequestTenders mainModel = new TenderRequestTenders(Long.parseLong(list.get(0).get("tender_id").toString()),
                (String) list.get(0).get("tender_title"),
                (String) list.get(0).get("tender_explain"), ((Timestamp) (list.get(0).get("tender_display_date"))).getTime(),
                ((Timestamp) list.get(0).get("tender_expire_date")).getTime(), Long.parseLong(list.get(0).get("response_count").toString()), categorie, schoolsList);


//        ObjectNode tenderNode = objectMapper.createObjectNode();
//        tenderNode.put("tender_id", mainModel.getTender_id());
//        tenderNode.put("tender_title", mainModel.getTender_title());
//        tenderNode.put("tender_explain", mainModel.getTender_explain());
//        ArrayNode tenderCategory = objectMapper.createArrayNode();

        return mainModel;
    }

    @PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    @GetMapping("/tender/request/{id}/page/{page}/{pageSize}/{flag_ar}")
    public TenderRequestTendersPagination getAllRequestsWithNameById2Pagination(@PathVariable int id, @PathVariable int page,
                                                                                @PathVariable int pageSize, @PathVariable int flag_ar) {


        Map<TenderRequestSchools, List<TenderRequestCategories>> res = new HashMap<>();
        List<Map<String, Object>> list = takatafTenderRequestRepo.getAllRequestsWithNameByTender2(id, flag_ar);
        List<TenderRequestSchools> schoolsList = new ArrayList<>();
        Set<TenderRequestSchools> schools = new HashSet<>();
        List<TenderRequestCategories> test2Models = new ArrayList<>();
        for (Map<String, Object> map : list) {
            TenderRequestSchools model = new TenderRequestSchools();
            TenderRequestCategories test2Model = new TenderRequestCategories();

            try {
                int schoolId = (int) map.get("school_id");
                String schoolName = (String) map.get("school_name");
                String schoolLogo = (String) map.get("school_logo_image");

                long categoryId = (long) map.get("id");
                String categoryName = (String) map.get("category_name");
                long count = (long) map.get("count");


                test2Model.setId(categoryId);
                test2Model.setCategory_name(categoryName);
                test2Model.setCount(count);

                model.setSchool_id(schoolId);
                model.setSchool_name(schoolName);
                model.setSchool_logo_image(schoolLogo);
                //String encodedString = Base64.getEncoder().encodeToString(schoolLogo);
                schools.add(model);

                test2Models.add(test2Model);
            } catch (NullPointerException e) {
                schools.clear();
            }
        }
        for (TenderRequestSchools obj : schools) {
            System.out.println(obj.toString());
//            res.put(obj,new ArrayList<Test2Model>());
//            List<Test2Model>  test2ModelArrayList=new ArrayList<>();
            List<TenderRequestCategories> test2ModelArrayList = null;
            test2ModelArrayList = new ArrayList<>();

            int i = 0;
            for (Map<String, Object> map : list) {
//                obj.getCategories().add(test2ModelList);


                if (res.get(obj) == null) {
                    res.put(obj, new ArrayList<TenderRequestCategories>());

                }
//                if (res.containsKey(obj)) {
                if (map.get("school_id").equals(obj.getSchool_id())) {
                    TenderRequestCategories test2Model = new TenderRequestCategories();
                    long categoryId = (long) map.get("id");
                    String categoryName = (String) map.get("category_name");
                    long count = (long) map.get("count");


                    test2Model.setId(categoryId);
                    test2Model.setCategory_name(categoryName);
                    test2Model.setCount(count);

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

        List<CategoryNameDto> categorie = takatafTenderRequestRepo.categoryData(id, flag_ar);

        int pages = (int) Math.ceil(((float) schoolsList.size()) / ((float) pageSize));
        System.out.println("Page Size =   " + pages);
        int limitOffset = (page - 1) * pageSize;

        List<TenderRequestSchools> resList = new ArrayList<>();
        for (int i = limitOffset; i < (limitOffset + pageSize); i++) {
            try {
                resList.add(schoolsList.get(i));
            } catch (Exception e) {

            }
        }
        List<CategoryNameDto> category = takatafTenderRequestRepo.categoryData(id, flag_ar);

        try {

            TenderRequestTendersPagination mainModel = new TenderRequestTendersPagination(Long.parseLong(list.get(0).get("tender_id").toString()),
                    (String) list.get(0).get("tender_title"),
                    (String) list.get(0).get("tender_explain"), ((Timestamp) (list.get(0).get("tender_display_date"))).getTime(),
                    ((Timestamp) list.get(0).get("tender_expire_date")).getTime(), Long.parseLong(list.get(0).get("response_count").toString()),
                    categorie, resList, schoolsList.size(), pages);

            return mainModel;
        } catch (Exception e) {

            TenderRequestTendersPagination mainModel = new TenderRequestTendersPagination(Long.parseLong(list.get(0).get("tender_id").toString()),
                    (String) list.get(0).get("tender_title"),
                    (String) list.get(0).get("tender_explain"), ((Timestamp) (list.get(0).get("tender_display_date"))).getTime(),
                    ((Timestamp) list.get(0).get("tender_expire_date")).getTime(), Long.parseLong(list.get(0).get("response_count").toString()),
                    categorie, null, 0, 0);

            return mainModel;
        }


    }

    @PreAuthorize("hasAuthority('admin') or hasAuthority('school')")
    @PutMapping("/tender/request/update")
    public ResponseEntity<ObjectNode> updateRequest(@RequestBody TakatafTenderRequestModel model) {
        int res = takatafTenderRequestRepo.updateRequest(model.getRequest_school_id(), model.getRequest_tender_id(), model.getCategory(), model.getFlag_ar());
        ObjectNode node = mapper.createObjectNode();
        if (res == 1) {
            node.put(STATUS, 200);
            node.put(MESSAGE, "Updated");
            return ResponseEntity.status(HttpStatus.OK).body(node);
        } else {
            node.put(STATUS, 400);
            node.put(MESSAGE, "Failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(node);
        }
    }

    @PreAuthorize("hasAuthority('admin') or hasAuthority('school')")
    @GetMapping("/tender/request/get/{id}/{school_id}/{flag_ar}")
    public ResponseEntity<ObjectNode> getRequestWithNameById(@PathVariable int id, @PathVariable int school_id, @PathVariable int flag_ar) {//GetTakatafTenderForScoolRequestDTO2
        List<TakatafSingleSchoolRequestByIDDTO2> tenderList = takatafTenderRequestRepo.getAllRequestsWithNameByIdzs2(id, school_id, flag_ar);
        List<CategoryNameDto> categorie = takatafTenderRequestRepo.categoryData(id, flag_ar);
        ArrayNode cats = mapper.createArrayNode();

        for (int j = 0; j < categorie.size(); j++) {
            ObjectNode nodes = mapper.createObjectNode();
            nodes.put("category_name", categorie.get(j).getCat_name());
            cats.add(nodes);
        }

        ObjectNode dataNode = mapper.createObjectNode();
        dataNode.put("tender_id", tenderList.get(0).getTender_id());
        dataNode.put("tender_logo", tenderList.get(0).getTender_logo());
        dataNode.put("tender_title", tenderList.get(0).getTender_title());
        dataNode.put("tender_explain", tenderList.get(0).getTender_explain());
        dataNode.put("tender_display_date", tenderList.get(0).getTender_display_date());
        dataNode.put("tender_expire_date", tenderList.get(0).getTender_expire_date());
        dataNode.put("response_count", tenderList.get(0).getResponse_count());
        dataNode.set("category", cats);
        GetSingleCollectiveByIdPartOneDTO data = new GetSingleCollectiveByIdPartOneDTO(tenderList.get(0).getTender_id(), tenderList.get(0).getTender_title(),
                tenderList.get(0).getTender_explain(), tenderList.get(0).getTender_display_date(),
                tenderList.get(0).getTender_expire_date(), tenderList.get(0).getResponse_count());
        List<GetGetTakatafTenderSchollPrt2DTO2> categories = new ArrayList<>();
        ArrayNode arrNode = mapper.createArrayNode();
        for (TakatafSingleSchoolRequestByIDDTO2 obj : tenderList) {
            GetGetTakatafTenderSchollPrt2DTO2 category = new GetGetTakatafTenderSchollPrt2DTO2(obj.getId(), obj.getCategory_name(), obj.getCount());
            categories.add(category);
            if (obj.getId() == 0 || obj.getCategory_name().equals(null) || obj.getCategory_name().trim().equals("")) {
                ObjectNode catData = mapper.createObjectNode();
                arrNode.add(catData);
            } else {
                ObjectNode catData = mapper.createObjectNode();
                catData.put("id", obj.getId());
                catData.put("category_name", obj.getCategory_name());
                catData.put("count", obj.getCount());

                arrNode.add(catData);
            }

        }

        ObjectNode result = mapper.createObjectNode();
        result.set("data", dataNode);
        result.set("categories", arrNode);
        GetTakatafTenderForScoolRequestDTO2 tener = new GetTakatafTenderForScoolRequestDTO2(data, categories);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/tender/details/company/{id}/{flag_ar}")
    public List<CollectiveTenderDetailsForCompanyModel> getTenderDetails(@PathVariable int id, @PathVariable int flag_ar) {
        return collectiveTenderDetailsForCompanyRepo.getTenderDetails(id, flag_ar);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/tender/details/company/{id}/page/{page}/{pageSize}/{flag_ar}")
    public CollectiveTenderDetailsForCompanyModelPagination getTenderDetailsPagination(@PathVariable int id, @PathVariable int page, @PathVariable int pageSize, @PathVariable int flag_ar) {
        return collectiveTenderDetailsForCompanyRepo.getTenderDetailsPAgination(id, page, pageSize, flag_ar);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/tender/companies/{id}")
    public List<CollectiveTenderCompaniesRequestForCompanyModel> getTenderCompanies(@PathVariable int id) {
        return collectiveTenderCompaniesRequestForCompanyRepo.getTenderCompanies(id);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/tender/companies/{id}/page/{page}/{pageSize}")
    public CollectiveTenderCompaniesRequestForCompanyModelPagination getTenderCompaniesPagination(@PathVariable int id, @PathVariable int page,
                                                                                                  @PathVariable int pageSize) {
        return collectiveTenderCompaniesRequestForCompanyRepo.getTenderCompaniesPagination(id, page, pageSize);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/tender/companies/{company_id}/{request_id}")
    public int approveRequest(@PathVariable int company_id, @PathVariable int request_id) {
        return collectiveTenderCompaniesRequestForCompanyRepo.approveRequest(company_id, request_id);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/tender/companies/cancel/{company_id}/{request_id}")
    public int cancelRequest(@PathVariable int company_id, @PathVariable int request_id) {
        return collectiveTenderCompaniesRequestForCompanyRepo.cancelRequest(company_id, request_id);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/admin/orders/")
    public List<AdminOrdersModel> getAllOrders() {
        return adminOrdersRepo.getAllOrders();
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/admin/orders/page/{page}/{pageSize}")
    public AdminOrdersModelPagination getAllOrdersPagination(@PathVariable int page, @PathVariable int pageSize) {
        return adminOrdersRepo.getAllOrdersPagination(page, pageSize);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/admin/orders/{id}")
    public AdminSingleOrderModel getOrder(@PathVariable int id) {
        return adminOrdersRepo.getOrder(id);
    }

//    @PreAuthorize("hasAuthority('school')  or hasAuthority('company')")
//    @GetMapping("/follow/school/{id}")
//    public List<FollowSchoolProfilesDto> getSchoolsWithFollow(@PathVariable int id) {
//        return schoolFollowCompanyRepo.getSchoolsWithFollow(id);
//    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/admin/orders/history/{id}")
    public AdminSingleOrderHistoryModel getHistoryOrder(@PathVariable int id) {
        return adminOrdersRepo.getHistoryOrder(id);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/admin/orders/ship")
    public int addShipping(@RequestBody ShippingDTO dto) {
        return adminOrdersRepo.addShipping(dto.getShip(), dto.getShip_company_offer_id());
    }

    ///////////ADMIN//////////////////////////////////////////////////////////////////////////////////////////////////////////
    @PreAuthorize("hasAuthority('admin')  or hasAuthority('school')  or hasAuthority('company')")
    @GetMapping("/follow/companies/{id}")
    public List<FollowSchoolProfilesDto> getSchoolsFollowCompany(@PathVariable int id) {
        return schoolFollowCompanyRepo.getSchoolsFollowCompany(id);
    }

    @PreAuthorize("hasAuthority('admin')  or hasAuthority('school')  or hasAuthority('company')")
    @GetMapping("/follow/companies/{id}/page/{page}/{pageSize}")
    public FollowSchoolProfilesDtoPAgination getSchoolsFollowCompanyPagination(@PathVariable int id, @PathVariable int page, @PathVariable int pageSize) {
        return schoolFollowCompanyRepo.getSchoolsFollowCompanyPagination(id, page, pageSize);
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//    @PreAuthorize("hasAuthority('school')")
//    @GetMapping("/follow/company/{id}")
//    public List<getCompaniesWithFollowDTo> getCompaniesWithFollow(@PathVariable int id) {
//        return schoolFollowCompanyRepo.getCompaniesWithFollow(id);
//    }

    @PreAuthorize("hasAuthority('admin')  or hasAuthority('school')  or hasAuthority('company')")
    @GetMapping("/follow/schools/{id}")
    public List<FollowCompanyProfilesDto> getCompainesFollowedBySchool(@PathVariable int id) {
        return schoolFollowCompanyRepo.getCompainesFollowedBySchool(id);
    }

    @PreAuthorize("hasAuthority('admin')  or hasAuthority('school')  or hasAuthority('company')")
    @GetMapping("/follow/schools/{id}/page/{page}/{pageSize}")
    public FollowCompanyProfilesDtoPagination getCompaniesFollowedBySchoolPagination(@PathVariable int id, @PathVariable int page, @PathVariable int pageSize) {
        return schoolFollowCompanyRepo.getCompaniesFollowedBySchoolPagination(id, page, pageSize);
    }

    @PreAuthorize("hasAuthority('school')")
    @GetMapping("/follow/school/followers/{schoolId}")
    public ResponseEntity<List<CompanyFollowSch0oolDto>> getSchoolAllFollowers(@PathVariable int schoolId) {

        if (schoolFollowCompanyRepo.isExistFollwer(schoolId)) {
            List<CompanyFollowSch0oolDto> followed = schoolFollowCompanyRepo.getSchoolAllFollowersNew(schoolId);
            return ResponseEntity.status(HttpStatus.OK).body(followed);
        } else {
            List<CompanyFollowSch0oolDto> followed = schoolFollowCompanyRepo.getSchoolAllFollowersNew(schoolId);
            return ResponseEntity.status(HttpStatus.OK).body(followed);
        }
    }

    @PreAuthorize("hasAuthority('school') or hasAuthority('company')")
    @GetMapping("/orders/{id}")
    public ResponseEntity<List<CompanyBackOrderModel>> getOrdersForCompany(@PathVariable int id) {
        return ordersRepo.getOrdersForCompany(id);
    }


    @PreAuthorize("hasAuthority('school') or hasAuthority('company')")
    @GetMapping("/orders/{id}/page/{page}/{pageSize}")
    public ResponseEntity<CompanyBackOrderModelPagination> getOrdersForCompanyPagination(@PathVariable int id, @PathVariable int page, @PathVariable int pageSize) {
        return ordersRepo.getOrdersForCompanyPagination(id, page, pageSize);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @PreAuthorize("hasAuthority('school')")
    @PostMapping("/school/profil/")
    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    public ResponseEntity<ObjectNode> AddSchoolProfile(@RequestParam("logo") MultipartFile logo, @RequestParam("cover") MultipartFile cover,
                                                       @Valid @RequestPart String schoolProfileModelString, Errors errors) {

        try {

            SchoolProfileModeNoImage model = new ObjectMapper().readValue(schoolProfileModelString, SchoolProfileModeNoImage.class);

            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");
                objectNode.put("details", errors.getAllErrors().toString());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
            if (newSchoolProfileRepo.isExistInLogin(model.getSchool_id(), "school")) {

                if (!repo.isExist(model.getSchool_id())) {

                    String logoUrl = imageUrl(logo, schoolRootLocation);
                    String coverUrl = imageUrl(cover, schoolRootLocation);
                    int res = newSchoolProfileRepo.addSchoolProfile(model.getSchool_id(), model.getSchool_name(), logoUrl,
                            model.getSchool_address(), model.getSchool_service_desc(), model.getSchool_link_youtube(),
                            model.getSchool_website_url(), model.getSchool_lng(), model.getSchool_lat(), coverUrl, model.getSchool_phone_number());
                    if (res == 1) {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("school_name", model.getSchool_name());
                        objectNode.put("school_logo_image", logoUrl);
                        objectNode.put("school_address", model.getSchool_address());
                        objectNode.put("school_service_desc", model.getSchool_service_desc());
                        objectNode.put("school_link_youtube", model.getSchool_link_youtube());
                        objectNode.put("school_website_url", model.getSchool_website_url());
                        objectNode.put("school_lng", model.getSchool_lng());
                        objectNode.put("school_lat", model.getSchool_lat());
                        objectNode.put("school_cover_image", coverUrl);
                        objectNode.put("school_phone_number", model.getSchool_phone_number());
                        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                    } else {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("status", 400);
                        objectNode.put("message", "not success");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }
                } else {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("status", 400);
                    objectNode.put("message", "id Already has profile");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }


            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("status", 400);
                objectNode.put("message", "id not found");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

    }

    @GetMapping("school/profil/{id}/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> getProfile(@PathVariable int id, @PathVariable int flag_ar) {


        if (repo.isExist(id)) {
            NewSchoolProfileModel model = newSchoolProfileRepo.getSchoolProfile(id, flag_ar);
            ObjectNode node = mapper.createObjectNode();
            node.put("school_id", model.getSchool_id());
            node.put("school_name", model.getSchool_name());
            node.put("school_logo_image", model.getSchool_logo_image());
            node.put("school_address", model.getSchool_address());
            node.put("school_service_desc", model.getSchool_service_desc());
            node.put("school_link_youtube", model.getSchool_link_youtube());
            node.put("school_website_url", model.getSchool_website_url());
            node.put("school_lng", model.getSchool_lng());
            node.put("school_lat", model.getSchool_lat());
            node.put("school_cover_image", model.getSchool_cover_image());
            node.put("school_phone_number", model.getSchool_phone_number());
            node.put("area", model.getArea());
            node.put("city", model.getCity());
            return ResponseEntity.status(HttpStatus.OK).body(node);

        } else {
            ObjectNode node = mapper.createObjectNode();
            node.put("status", 400);
            node.put("message", "no profile to this id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(node);

        }
    }


    @GetMapping("school/profil/s/{id}/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> getProfile2(@PathVariable int id, @PathVariable int flag_ar) {


        if (repo.isExist(id)) {
            NewSchoolProfileModelDTO model = newSchoolProfileRepo.getSchoolProfile2(id, flag_ar);
            ObjectNode node = mapper.createObjectNode();
            node.put("school_id", model.getSchool_id());
            node.put("school_name", model.getSchool_name());
            node.put("school_logo_image", model.getSchool_logo_image());
            node.put("school_address", model.getSchool_address());
            node.put("school_service_desc", model.getSchool_service_desc());
            node.put("school_link_youtube", model.getSchool_link_youtube());
            node.put("school_website_url", model.getSchool_website_url());
            node.put("school_lng", model.getSchool_lng());
            node.put("school_lat", model.getSchool_lat());
            node.put("school_cover_image", model.getSchool_cover_image());
            node.put("school_phone_number", model.getSchool_phone_number());
            node.put("area", model.getArea());
            node.put("city", model.getCity());
            node.put("lng", model.getLng());
            node.put("lat", model.getLat());
            return ResponseEntity.status(HttpStatus.OK).body(node);

        } else {
            ObjectNode node = mapper.createObjectNode();
            node.put("status", 400);
            node.put("message", "no profile to this id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(node);

        }
    }


    @PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    @GetMapping("/school/profil/get/{id}/{flag_ar}")
    public NewCustomSchoolProfileModelDTO getProfileForAdmin2(@PathVariable int id, @PathVariable int flag_ar) {
        return newSchoolProfileRepo.getSchoolProfileForAdmin2(id, flag_ar);
    }

    @PreAuthorize("hasAuthority('school')")
    @PutMapping("/school/profil/update/{flag_ar}")
    public ResponseEntity<ObjectNode> updateProfileForAdmin2(@RequestParam(value = "logo", required = false) MultipartFile logo,
                                                             @RequestParam(value = "cover", required = false) MultipartFile cover,
                                                             @RequestPart String newSchoolProfileModelDTOString, Errors errors, @PathVariable int flag_ar) {

        try {
            UpdateSchoolProfilModel model = new ObjectMapper().readValue(newSchoolProfileModelDTOString, UpdateSchoolProfilModel.class);

            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);

            }


            String logoUrl = "";
            String coverUrl = "";
            try {
                logoUrl = imageUrl(logo, schoolRootLocation);
            } catch (Exception e) {
                logoUrl = "";
            }
            try {
                coverUrl = imageUrl(cover, schoolRootLocation);
            } catch (Exception e) {
                coverUrl = "";
            }


            if (logoUrl.equals(null) || logoUrl.isEmpty()) {

                if (coverUrl.isEmpty() || coverUrl.equals(null)) {


                    int res = newSchoolProfileRepo.updateProfileForAdmin2(model.getSchool_id(), model.getSchool_name(), model.getSchool_logo_image(),
                            model.getSchool_address(), model.getSchool_service_desc(), model.getSchool_link_youtube(),
                            model.getSchool_website_url(), model.getSchool_cover_image(), model.getSchool_phone_number(), model.getCity(), model.getArea(),
                            model.getLng(), model.getLat(), flag_ar);

                    if (res == 1) {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("school_name", model.getSchool_name());
                        objectNode.put("school_logo_image", model.getSchool_logo_image());
                        objectNode.put("school_address", model.getSchool_address());
                        objectNode.put("school_service_desc", model.getSchool_service_desc());
                        objectNode.put("school_link_youtube", model.getSchool_link_youtube());
                        objectNode.put("school_website_url", model.getSchool_website_url());
                        objectNode.put("school_cover_image", model.getSchool_cover_image());
                        objectNode.put("school_phone_number", model.getSchool_phone_number());
                        objectNode.put("city", model.getCity());
                        objectNode.put("area", model.getArea());
                        objectNode.put("lng", model.getLng());
                        objectNode.put("lat", model.getLat());
                        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                    } else {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("value", "not success");

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }


                } else {

                    int res = newSchoolProfileRepo.updateProfileForAdmin2(model.getSchool_id(), model.getSchool_name(), model.getSchool_logo_image(),
                            model.getSchool_address(), model.getSchool_service_desc(), model.getSchool_link_youtube(),
                            model.getSchool_website_url(), coverUrl, model.getSchool_phone_number(), model.getCity(), model.getArea(),
                            model.getLng(), model.getLat(), flag_ar);

                    if (res == 1) {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("school_name", model.getSchool_name());
                        objectNode.put("school_logo_image", model.getSchool_logo_image());
                        objectNode.put("school_address", model.getSchool_address());
                        objectNode.put("school_service_desc", model.getSchool_service_desc());
                        objectNode.put("school_link_youtube", model.getSchool_link_youtube());
                        objectNode.put("school_website_url", model.getSchool_website_url());
                        objectNode.put("school_cover_image", coverUrl);
                        objectNode.put("school_phone_number", model.getSchool_phone_number());
                        objectNode.put("city", model.getCity());
                        objectNode.put("area", model.getArea());
                        objectNode.put("lng", model.getLng());
                        objectNode.put("lat", model.getLat());
                        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                    } else {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("value", "not success  ");

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }


                }


            } else if (coverUrl.isEmpty() || coverUrl.equals(null)) {


                if (logoUrl.equals(null) || logoUrl.isEmpty()) {


                    int res = newSchoolProfileRepo.updateProfileForAdmin2(model.getSchool_id(), model.getSchool_name(), model.getSchool_logo_image(),
                            model.getSchool_address(), model.getSchool_service_desc(), model.getSchool_link_youtube(),
                            model.getSchool_website_url(), model.getSchool_cover_image(), model.getSchool_phone_number(), model.getCity(), model.getArea(),
                            model.getLng(), model.getLat(), flag_ar);

                    if (res == 1) {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("school_name", model.getSchool_name());
                        objectNode.put("school_logo_image", model.getSchool_logo_image());
                        objectNode.put("school_address", model.getSchool_address());
                        objectNode.put("school_service_desc", model.getSchool_service_desc());
                        objectNode.put("school_link_youtube", model.getSchool_link_youtube());
                        objectNode.put("school_website_url", model.getSchool_website_url());
                        objectNode.put("school_cover_image", model.getSchool_cover_image());
                        objectNode.put("school_phone_number", model.getSchool_phone_number());
                        objectNode.put("city", model.getCity());
                        objectNode.put("area", model.getArea());
                        objectNode.put("lng", model.getLng());
                        objectNode.put("lat", model.getLat());
                        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                    } else {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("value", "not success");

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }


                } else {

                    int res = newSchoolProfileRepo.updateProfileForAdmin2(model.getSchool_id(), model.getSchool_name(), logoUrl,
                            model.getSchool_address(), model.getSchool_service_desc(), model.getSchool_link_youtube(),
                            model.getSchool_website_url(), model.getSchool_cover_image(), model.getSchool_phone_number(), model.getCity(), model.getArea(),
                            model.getLng(), model.getLat(), flag_ar);

                    if (res == 1) {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("school_name", model.getSchool_name());
                        objectNode.put("school_logo_image", logoUrl);
                        objectNode.put("school_address", model.getSchool_address());
                        objectNode.put("school_service_desc", model.getSchool_service_desc());
                        objectNode.put("school_link_youtube", model.getSchool_link_youtube());
                        objectNode.put("school_website_url", model.getSchool_website_url());
                        objectNode.put("school_cover_image", model.getSchool_cover_image());
                        objectNode.put("school_phone_number", model.getSchool_phone_number());
                        objectNode.put("city", model.getCity());
                        objectNode.put("area", model.getArea());
                        objectNode.put("lng", model.getLng());
                        objectNode.put("lat", model.getLat());
                        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                    } else {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("value", "not success");

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }

                }


            } else {
                int res = newSchoolProfileRepo.updateProfileForAdmin2(model.getSchool_id(), model.getSchool_name(), logoUrl,
                        model.getSchool_address(), model.getSchool_service_desc(), model.getSchool_link_youtube(),
                        model.getSchool_website_url(), coverUrl, model.getSchool_phone_number(), model.getCity(), model.getArea(),
                        model.getLng(), model.getLat(), flag_ar);

                if (res == 1) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("school_name", model.getSchool_name());
                    objectNode.put("school_logo_image", logoUrl);
                    objectNode.put("school_address", model.getSchool_address());
                    objectNode.put("school_service_desc", model.getSchool_service_desc());
                    objectNode.put("school_link_youtube", model.getSchool_link_youtube());
                    objectNode.put("school_website_url", model.getSchool_website_url());
                    objectNode.put("school_cover_image", coverUrl);
                    objectNode.put("school_phone_number", model.getSchool_phone_number());
                    objectNode.put("city", model.getCity());
                    objectNode.put("area", model.getArea());
                    objectNode.put("lng", model.getLng());
                    objectNode.put("lat", model.getLat());
                    return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                } else {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("value", "not success");

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }
            }


        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }


    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @PreAuthorize("hasAuthority('school')")
    @PostMapping("/school/profile/addProfile")
    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    public ResponseEntity<ObjectNode> AddUserProfile(@RequestParam(value = "logo") MultipartFile logo,
                                                     @RequestParam(value = "cover") MultipartFile cover,
                                                     @Valid @RequestPart String schoolProfileModelString, Errors errors) {

        try {

            SchoolProfileModeNoImage model = new ObjectMapper().readValue(schoolProfileModelString, SchoolProfileModeNoImage.class);

            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");
                //objectNode.put("details", errors.getAllErrors().toString());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
            if (schoolProfileRepo.isExistInLogin(model.getSchool_id(), "school")) {

                System.out.println("test1  : "+  schoolProfileRepo.isExistInLogin(model.getSchool_id(), "school"));

                System.out.println("testxxxx2  : "+  !(schoolProfileRepo.isExist(model.getSchool_id())));

                if (!(schoolProfileRepo.isExist(model.getSchool_id()))) {
                    System.out.println("testxxxx  : "+  !(schoolProfileRepo.isExist(model.getSchool_id())));


                    String logoUrl = imageUrl(logo, schoolRootLocation);
                    System.out.println("test2  : "+  !(schoolProfileRepo.isExist(model.getSchool_id())));
                    TimeUnit.MILLISECONDS.sleep(11);
                    String coverUrl = imageUrl(cover, schoolRootLocation);

                    System.out.println("test3  : "+ model.getSchool_name());


                    int res = schoolProfileRepo.addSchoolProfile(model.getSchool_id(), model.getSchool_name(), logoUrl,
                            model.getSchool_address(), model.getSchool_service_desc(), model.getSchool_link_youtube(),
                            model.getSchool_website_url(), model.getSchool_lng(), model.getSchool_lat(), coverUrl, model.getSchool_phone_number());
                    if (res == 1) {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("school_name", model.getSchool_name());
                        objectNode.put("school_logo_image", logoUrl);
                        objectNode.put("school_address", model.getSchool_address());
                        objectNode.put("school_service_desc", model.getSchool_service_desc());
                        objectNode.put("school_link_youtube", model.getSchool_link_youtube());
                        objectNode.put("school_website_url", model.getSchool_website_url());
                        objectNode.put("school_lng", model.getSchool_lng());
                        objectNode.put("school_lat", model.getSchool_lat());
                        objectNode.put("school_cover_image", coverUrl);
                        objectNode.put("school_phone_number", model.getSchool_phone_number());
                        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                    } else {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("status", 400);
                        objectNode.put("message", "not success");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }
                } else {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("status", 400);
                    objectNode.put("message", "id Already has profile");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }


            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("status", 400);
                objectNode.put("message", "id not found");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "id Already has profile");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }


    }

    @PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    @GetMapping("/school/profile/get/{id}")
    public CustomSchoolProfileModel getProfileForAdmin(@PathVariable int id) {
        return schoolProfileRepo.getSchoolProfileForAdmin(id);
    }

    //
    @PreAuthorize("hasAuthority('school')")
    @PutMapping("/school/profile/update")
    public ResponseEntity<ObjectNode> updateProfileForAdmin(@RequestParam(value = "logo", required = false) MultipartFile logo,
                                                            @RequestParam(value = "cover", required = false) MultipartFile cover,
                                                            @Valid @RequestPart String schoolProfileModelString, Errors errors) {
        try {

            SchoolWithImageUrlModel model = new ObjectMapper().readValue(schoolProfileModelString, SchoolWithImageUrlModel.class);
            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);

            }

            String logoUrl = "";
            String coverUrl = "";
            try {
                logoUrl = imageUrl(logo, schoolRootLocation);
            } catch (Exception e) {
                logoUrl = "";
            }
            try {
                coverUrl = imageUrl(cover, schoolRootLocation);
            } catch (Exception e) {
                coverUrl = "";
            }
            if ((logoUrl.isEmpty() || logoUrl.equals(null))) {
                if ((coverUrl.isEmpty() || coverUrl.equals(null))) {


                    int res = schoolProfileRepo.updateProfileForAdmin(model.getSchool_id(), model.getSchool_name(), model.getSchool_logo_image(),
                            model.getSchool_address(), model.getSchool_service_desc(), model.getSchool_link_youtube(),
                            model.getSchool_website_url(), model.getScholl_cover_image(), model.getSchool_phone_number());

                    if (res == 1) {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("school_name", model.getSchool_name());
                        objectNode.put("school_logo_image", model.getSchool_logo_image());
                        objectNode.put("school_address", model.getSchool_address());
                        objectNode.put("school_service_desc", model.getSchool_service_desc());
                        objectNode.put("school_link_youtube", model.getSchool_link_youtube());
                        objectNode.put("school_website_url", model.getSchool_website_url());
                        objectNode.put("school_cover_image", model.getScholl_cover_image());
                        objectNode.put("school_phone_number", model.getSchool_phone_number());
                        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                    } else {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("value", "not success");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }


                } else {


                    int res = schoolProfileRepo.updateProfileForAdmin(model.getSchool_id(), model.getSchool_name(), model.getSchool_logo_image(),
                            model.getSchool_address(), model.getSchool_service_desc(), model.getSchool_link_youtube(),
                            model.getSchool_website_url(), coverUrl, model.getSchool_phone_number());

                    if (res == 1) {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("school_name", model.getSchool_name());
                        objectNode.put("school_logo_image", model.getSchool_logo_image());
                        objectNode.put("school_address", model.getSchool_address());
                        objectNode.put("school_service_desc", model.getSchool_service_desc());
                        objectNode.put("school_link_youtube", model.getSchool_link_youtube());
                        objectNode.put("school_website_url", model.getSchool_website_url());
                        objectNode.put("school_cover_image", coverUrl);
                        objectNode.put("school_phone_number", model.getSchool_phone_number());
                        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                    } else {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("value", "not success");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }


                }
            } else if ((coverUrl.isEmpty() || coverUrl.equals(null))) {


                if ((logoUrl.isEmpty() || logoUrl.equals(null))) {
                    int res = schoolProfileRepo.updateProfileForAdmin(model.getSchool_id(), model.getSchool_name(), model.getSchool_logo_image(),
                            model.getSchool_address(), model.getSchool_service_desc(), model.getSchool_link_youtube(),
                            model.getSchool_website_url(), model.getScholl_cover_image(), model.getSchool_phone_number());

                    if (res == 1) {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("school_name", model.getSchool_name());
                        objectNode.put("school_logo_image", model.getSchool_logo_image());
                        objectNode.put("school_address", model.getSchool_address());
                        objectNode.put("school_service_desc", model.getSchool_service_desc());
                        objectNode.put("school_link_youtube", model.getSchool_link_youtube());
                        objectNode.put("school_website_url", model.getSchool_website_url());
                        objectNode.put("school_cover_image", model.getScholl_cover_image());
                        objectNode.put("school_phone_number", model.getSchool_phone_number());
                        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                    } else {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("value", "not success");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }


                } else {


                    int res = schoolProfileRepo.updateProfileForAdmin(model.getSchool_id(), model.getSchool_name(), logoUrl,
                            model.getSchool_address(), model.getSchool_service_desc(), model.getSchool_link_youtube(),
                            model.getSchool_website_url(), model.getScholl_cover_image(), model.getSchool_phone_number());

                    if (res == 1) {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("school_name", model.getSchool_name());
                        objectNode.put("school_logo_image", logoUrl);
                        objectNode.put("school_address", model.getSchool_address());
                        objectNode.put("school_service_desc", model.getSchool_service_desc());
                        objectNode.put("school_link_youtube", model.getSchool_link_youtube());
                        objectNode.put("school_website_url", model.getSchool_website_url());
                        objectNode.put("school_cover_image", model.getScholl_cover_image());
                        objectNode.put("school_phone_number", model.getSchool_phone_number());
                        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                    } else {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("value", "not success");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }
                }


            } else {
                int res = schoolProfileRepo.updateProfileForAdmin(model.getSchool_id(), model.getSchool_name(), logoUrl,
                        model.getSchool_address(), model.getSchool_service_desc(), model.getSchool_link_youtube(),
                        model.getSchool_website_url(), coverUrl, model.getSchool_phone_number());

                if (res == 1) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("school_name", model.getSchool_name());
                    objectNode.put("school_logo_image", logoUrl);
                    objectNode.put("school_address", model.getSchool_address());
                    objectNode.put("school_service_desc", model.getSchool_service_desc());
                    objectNode.put("school_link_youtube", model.getSchool_link_youtube());
                    objectNode.put("school_website_url", model.getSchool_website_url());
                    objectNode.put("school_cover_image", coverUrl);
                    objectNode.put("school_phone_number", model.getSchool_phone_number());
                    return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                } else {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("value", "not success");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }
            }


        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

    }

    @PreAuthorize("hasAuthority('school')")
    @GetMapping("/school/tenders/school/{id}/{flag_ar}")
    public List<SchoolRequestNewDto2Model> getSchoolRequestsBySchool(@PathVariable int id, @PathVariable int flag_ar) {
        return schoolRequestNewRepo.getRequestsBySchoolID(id, flag_ar);
    }

    @PreAuthorize("hasAuthority('school')")
    @GetMapping("/school/tenders/school/{id}/page/{page}/{pageSize}/{flag_ar}")
    public SchoolRequestNewDto2ModelPagination getSchoolRequestsBySchool(@PathVariable int id,
                                                                         @PathVariable int page, @PathVariable int pageSize, @PathVariable int flag_ar) {
        return schoolRequestNewRepo.getRequestsBySchoolIDPagination(id, page, pageSize, flag_ar);
    }

    @PreAuthorize("hasAuthority('school')")
    @GetMapping("/school/tenders/{flag_ar}")
    public List<SchoolRequestNewDtoModel> getSchoolSingleRequest(@PathVariable int flag_ar) {
        return schoolRequestNewRepo.getRequestsAll(flag_ar);
    }

    @PreAuthorize("hasAuthority('school')")
    @GetMapping("/school/tenders/page/{page}/{pageSize}/{flag_ar}")
    public SchoolRequestNewDtoModelPagination getSchoolSingleRequest(@PathVariable int page,
                                                                     @PathVariable int pageSize, @PathVariable int flag_ar) {
        return schoolRequestNewRepo.getRequestsAllPagination(page, pageSize, flag_ar);
    }

    @PreAuthorize("hasAuthority('school')")
    @PutMapping("/school/tenders/")
    public JsonNode updateRequestModel(@Valid @RequestBody SchoolRequestsDTOAr model, Errors errors) {
        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");

            return objectNode;
        }
        int res = schoolRequestNewRepo.updateRequest(model.getRequest_id(), model.getRequest_title(), model.getRequest_explaination(),
                model.getRequest_display_date(), model.getRequest_expired_date(), model.getSchool_id(), model.getRequest_category_id(),
                model.getRequest_count(), model.getFlag_ar());

        if (res == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("request_id", model.getRequest_id());
            objectNode.put("request_title", model.getRequest_title());
            objectNode.put("request_explaination", model.getRequest_explaination());
            objectNode.put("request_display_date", model.getRequest_display_date());
            objectNode.put("request_expired_date", model.getRequest_expired_date());
            objectNode.put("request_count", model.getRequest_count());
            objectNode.put("school_id", model.getSchool_id());
            objectNode.put("request_category_id", model.getRequest_category_id());
            objectNode.put("flag_ar", model.getFlag_ar());

            return objectNode;
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "not success");

            return objectNode;
        }
    }

    @PreAuthorize("hasAuthority('school')")
    @DeleteMapping("/school/tenders/{id}")
    public ObjectNode deleteSchoolRequest(@PathVariable int id) {
        if (schoolRequestNewRepo.isExist(id)) {
            int res = schoolRequestNewRepo.deleteSchoolRequest(id);
            if (res == 1) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("message", "success");

                return objectNode;
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("message", "not success");

                return objectNode;
            }
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("message", "not element by this id");

            return objectNode;
        }

    }

    @PreAuthorize("hasAuthority('school')")
    @PostMapping("/school/tenders/")
    public ResponseEntity<JsonNode> addSchoolRequest(@RequestParam(value = "image") MultipartFile image,
                                                     @RequestPart @Valid String schoolNewRequestsDTO2String, Errors errors) {

        try {
            SchoolNewRequestsDTO2 model = new ObjectMapper().readValue(schoolNewRequestsDTO2String, SchoolNewRequestsDTO2.class);
            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }

            if (model.getRequest_display_date() < new Timestamp(System.currentTimeMillis()).getTime()
                    || model.getRequest_expired_date() < new Timestamp(System.currentTimeMillis()).getTime()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed offer date in past");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }

            if (model.getRequest_display_date() >= model.getRequest_expired_date()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed offer display date is greater than  expired date");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }


            String imageUrl = imageUrl(image, rootLocation);

            int res = schoolRequestNewRepo.addRequest(model.getRequest_title(), model.getRequest_explaination(),
                    model.getRequest_display_date(), model.getRequest_expired_date(), model.getSchool_id(),
                    model.getRequest_category_name(), imageUrl, model.getRequest_count(), model.getFlag_ar());
            if (res == 1) {
                ObjectNode objectNode = mapper.createObjectNode();

                objectNode.put("request_title", model.getRequest_title());
                objectNode.put("request_explaination", model.getRequest_explaination());
                objectNode.put("request_display_date", model.getRequest_display_date());
                objectNode.put("request_expired_date", model.getRequest_expired_date());
                objectNode.put("school_id", model.getSchool_id());
                objectNode.put("request_category_id", model.getRequest_category_name());
                objectNode.put("image", imageUrl);
                objectNode.put("request_count", model.getRequest_count());
                objectNode.put("flag_ar", model.getFlag_ar());

                return ResponseEntity.status(HttpStatus.OK).body(objectNode);
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("status", 400);
                objectNode.put("message", "not success");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "not success  " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }

    @PreAuthorize("hasAuthority('school')")
    @GetMapping("/school/tenders/get/{id}/{flag_ar}")
    public ResponseEntity<ObjectNode> getSchoolRequest(@PathVariable int id, @PathVariable int flag_ar) {

        return schoolRequestNewRepo.getSchoolRequest(id, flag_ar);


    }

    @PreAuthorize("hasAuthority('school')")
    @PutMapping("/school/tenders/update/")
    public ResponseEntity<JsonNode> updateSchoolRequest
            (@RequestParam(value = "logo", required = false) MultipartFile logo,
             @RequestPart @Valid String schoolRequestModelUpdateString, Errors errors) {

        try {

            SchoolRequestModelUpdateAr model = new ObjectMapper().readValue(schoolRequestModelUpdateString, SchoolRequestModelUpdateAr.class);

            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }

            if (model.getRequest_expired_date() < new Timestamp(System.currentTimeMillis()).getTime()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed offer date in past");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }

            if (model.getRequest_display_date() >= model.getRequest_expired_date()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed offer display date is greater than  expired date");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }

            String logoUrl = "";
            try {
                logoUrl = imageUrl(logo, rootLocation);
            } catch (Exception e) {
                logoUrl = "";
            }


            if (logoUrl.isEmpty() || logoUrl.equals(null) || logoUrl.equals("")) {
                int res = schoolRequestNewRepo.updateSchoolRequest(model.getRequest_id(), model.getRequest_title(), model.getRequest_explaination(),
                        model.getRequest_display_date(), model.getRequest_expired_date(), model.getSchool_id(),
                        model.getRequest_category_name(), model.getImage_one(), model.getRequest_count(), model.getRequest_is_conformied(), model.getFlag_ar());
                if (res == 1) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("request_id", model.getRequest_id());
                    objectNode.put("request_title", model.getRequest_title());
                    objectNode.put("request_explaination", model.getRequest_explaination());
                    objectNode.put("request_display_date", model.getRequest_display_date());
                    objectNode.put("request_expired_date", model.getRequest_expired_date());
                    objectNode.put("school_id", model.getSchool_id());
                    objectNode.put("request_category_id", model.getRequest_category_name());
                    objectNode.put("image", model.getImage_one());
                    objectNode.put("request_count", model.getRequest_count());
                    objectNode.put("flag_ar", model.getFlag_ar());

                    return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                } else {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("status", 400);
                    objectNode.put("message", "not success");

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }
            } else {
                int res = schoolRequestNewRepo.updateSchoolRequest(model.getRequest_id(), model.getRequest_title(), model.getRequest_explaination(),
                        model.getRequest_display_date(), model.getRequest_expired_date(), model.getSchool_id(),
                        model.getRequest_category_name(), logoUrl, model.getRequest_count(), model.getRequest_is_conformied(), model.getFlag_ar());
                if (res == 1) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("request_id", model.getRequest_id());
                    objectNode.put("request_title", model.getRequest_title());
                    objectNode.put("request_explaination", model.getRequest_explaination());
                    objectNode.put("request_display_date", model.getRequest_display_date());
                    objectNode.put("request_expired_date", model.getRequest_expired_date());
                    objectNode.put("school_id", model.getSchool_id());
                    objectNode.put("request_category_id", model.getRequest_category_name());
                    objectNode.put("image", logoUrl);
                    objectNode.put("request_count", model.getRequest_count());
                    objectNode.put("flag_ar", model.getFlag_ar());

                    return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                } else {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("status", 400);
                    objectNode.put("message", "not success");

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }
            }


        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "exception   " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }

    @PreAuthorize("hasAuthority('school')")
    @GetMapping("/school/tenders/request/school/{id}/{flag_ar}")
    public GetCollectiveTenders2Model getRequestOfSchoolByID(@PathVariable int id, @PathVariable int flag_ar) {
        List<getSchoolCustomNewRequestByIdModel> obj = schoolRequestNewRepo.getRequestOfSchoolByID(id, flag_ar);
        GetCollectiveTenderPartOneDTO2Model tender = new GetCollectiveTenderPartOneDTO2Model(obj.get(0).getRequest_id(), obj.get(0).getRequest_title(),
                obj.get(0).getRequest_explaination(), obj.get(0).getRequest_display_date(), obj.get(0).getRequest_expired_date(), obj.get(0).getRequest_count(),
                obj.get(0).getSchool_id(), obj.get(0).getResponse_count(), obj.get(0).getImage_one());


        List<GetCollectiveTenderPartYTwoDTO> companies = new ArrayList<>();
        if (obj.get(0).getResponse_count() > 0) {
            for (getSchoolCustomNewRequestByIdModel one : obj) {
                //if( one.getRequest_category_name().equals(null) )
                GetCollectiveTenderPartYTwoDTO part2 = new GetCollectiveTenderPartYTwoDTO(one.getRequest_category_name() + "", one.getCompany_name() + "",
                        one.getCompany_logo_image(), one.getCategory_name() + "", one.getResponsed_cost(), one.getResponse_date(),
                        one.getResponse_id(), one.getResponsed_company_id(), one.getIs_aproved(), one.getResponse_desc());
                companies.add(part2);
            }
        }

        if (obj.get(0).getResponse_count() == 0) {
            GetCollectiveTenders2Model tenders = new GetCollectiveTenders2Model(tender, null);
            return tenders;
        } else {
            GetCollectiveTenders2Model tenders = new GetCollectiveTenders2Model(tender, companies);
            return tenders;
        }


    }

    @PreAuthorize("hasAuthority('school')")
    @GetMapping("/school/requests/get/history/{id}")
    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    public List<SchoolRequestHistoryDto> getSchoolHistoryRequestBySchoolId(@PathVariable int id) {
        return schoolRequestRepo.getHistoryRequestsBySchoolId(id);
    }

    @PreAuthorize("hasAuthority('school')")
    @GetMapping("/school/requests/get/history/desc/{id}")
    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    public List<SchoolRequestNewDto2Model> getSchoolHistoryRequestBySchoolIdWithDesc(@PathVariable int id) {
        return schoolRequestRepo.getHistoryRequestsBySchoolId2(id);
    }

    @PreAuthorize("hasAuthority('school')")
    @GetMapping("/school/requests/get/history/desc/{id}/page/{page}/{pageSize}/{flag_ar}")
    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    public SchoolRequestNewDto2ModelPagination getSchoolHistoryRequestBySchoolIdWithDescPagination(
            @PathVariable int id, @PathVariable int page, @PathVariable int pageSize, @PathVariable int flag_ar) {
        return schoolRequestRepo.getHistoryRequestsBySchoolId2Pagination(id, page, pageSize, flag_ar);
    }

    @PreAuthorize("hasAuthority('school')")
    @GetMapping("/school/requests/get/aproved/{id}")
    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    public List<SchoolRequestHistoryDtoDTO> getSchoolAprrovedRequestBySchoolId(@PathVariable int id) {
        return schoolRequestRepo.getApprovedSchoolRequests(id);
    }

    @PreAuthorize("hasAuthority('school')")
    @GetMapping("/school/requests/get/aproved/{id}/page/{page}/{pageSize}")
    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    public SchoolRequestHistoryDtoDTOPagination getSchoolAprrovedRequestBySchoolIdPagination(@PathVariable int id,
                                                                                             @PathVariable int page, @PathVariable int pageSize) {
        return schoolRequestRepo.getApprovedSchoolRequestsPagination(id, page, pageSize);
    }

    @PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    @GetMapping("/school/requests/get/history/desc2/{id}")
    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    public List<SchoolRequestHistoryDtoDTO2> getSchoolHistoryRequestBySchoolIdWithDesc2(@PathVariable int id) {
        return schoolRequestRepo.getHistoryRequestsBySchoolId3(id);
    }

    @PreAuthorize("hasAuthority('company')")
    @GetMapping("/company/offer/history/{companyId}/{flag_ar}")
    public List<CompanyHistoryDto> getCompanyHistory(@PathVariable int companyId, @PathVariable int flag_ar) {
        return customCompanyOfferRepo.getCompanyHistory(companyId,flag_ar);
    }

    @PreAuthorize("hasAuthority('company') or hasAuthority('admin') or hasAuthority('school')")
    @GetMapping("/company/offer/history/{companyId}/page/{page}/{pageSize}/{flag_ar}")
    public CompanyHistoryDtoPaginatin getCompanyHistoryPagination(@PathVariable int companyId,
                                                                  @PathVariable int page, @PathVariable int pageSize, @PathVariable int flag_ar) {
        return customCompanyOfferRepo.getCompanyHistoryPagination(companyId, page, pageSize, flag_ar);
    }

    @PreAuthorize("hasAuthority('company') or hasAuthority('admin') or hasAuthority('school')")
    @GetMapping("/company/offer/history2/{companyId}/{flag_ar}")
    public List<CompanyHistoryDto2> getCompanyHistory2(@PathVariable int companyId, @PathVariable int flag_ar) {
        return customCompanyOfferRepo.getCompanyHistory2(companyId, flag_ar);
    }

    @PreAuthorize("hasAuthority('school')")
    @GetMapping("/school/requests/get/order/{id}")
    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    public List<SchoolRequestHistoryDto> getSchoolOrderRequestBySchoolId(@PathVariable int id) {
        return schoolRequestRepo.getOrderRequestsBySchoolId(id);
    }

    @PreAuthorize("hasAuthority('school')")
    @GetMapping("/school/category/getAll/{flag_ar}")
//    @PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    public List<SchoolRequestCategoryModelEnglish> getSchoolCategories(@PathVariable int flag_ar) {

        return schoolRequestCategoryRepo.getSchoolRequestCategories(flag_ar);
    }

    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    @PostMapping("/tender/request/add")
    public ResponseEntity<ObjectNode> addTenderRequest(@Valid @RequestBody TakatafTenderRequestModel model, Errors
            errors) {

        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            objectNode.put("details", errors.getAllErrors().toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        int res = takatafTenderRequestRepo.add$Request(model.getRequest_school_id(), model.getRequest_tender_id(),
                model.getIs_aproved(), model.getDate(), model.getCategory(), model.getFlag_ar());
        if (res == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            //objectNode.put("request_id", model.getRequest_id());
            objectNode.put("request_school_id", model.getRequest_school_id());
            objectNode.put("request_tender_id", model.getRequest_tender_id());
            objectNode.put("is_aproved", model.getIs_aproved());
            objectNode.put("date", model.getDate());

            return ResponseEntity.status(HttpStatus.OK).body(objectNode);
        } else if (res == -1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("message", "you already request this");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("message", "not success");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }

    @PreAuthorize("hasAuthority('school')")
    @PutMapping("/response/school/request/accept/{id}")
    public ResponseEntity<JsonNode> acceptCompanyResponseSchoolRequest(@PathVariable int id) {

        int res = companyResponseSchoolRequestRepo.acceptResponseSchoolRequest(id);
        if (res == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 200);
            objectNode.put("message", "accepted");
            objectNode.put("agree_flag", 1);
            return ResponseEntity.status(HttpStatus.OK).body(objectNode);
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);

        }
    }

    @PreAuthorize("hasAuthority('school')")
    @PutMapping("/response/school/request/refuse/{id}")
    public ResponseEntity<JsonNode> refuseCompanyResponseSchoolRequest(@PathVariable int id) {
        int res = companyResponseSchoolRequestRepo.refuseResponseSchoolRequest(id);
        if (res == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 200);
            objectNode.put("message", "refuse success");
            objectNode.put("agree_flag", 0);
            return ResponseEntity.status(HttpStatus.OK).body(objectNode);
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "refuse failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }

    @PreAuthorize("hasAuthority('school')")
    @DeleteMapping("/response/school/request/delete/{id}")
    public ObjectNode deleteCompanyResponseSchoolRequest(@PathVariable int id) {
        int res = companyResponseSchoolRequestRepo.deleteResponseSchoolRequest(id);

        if (res == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "success");

            return objectNode;
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success");

            return objectNode;
        }
    }

    @PreAuthorize("hasAuthority('company')")
    @PostMapping("/company/offer/{flag_ar}")
    public ResponseEntity<ObjectNode> addCompanyOffers(@RequestParam("image1") MultipartFile image1,
                                                       @RequestParam(value = "image2", required = false) MultipartFile image2,
                                                       @RequestParam(value = "image3", required = false) MultipartFile image3,
                                                       @RequestParam(value = "image4", required = false) MultipartFile image4,
                                                       @RequestPart @Valid String customCompanyOfferModelString, Errors errors, @PathVariable int flag_ar) {

        try {

            System.out.println("1");
            CustomCompanyOfferModelNoImage model = new ObjectMapper().readValue(customCompanyOfferModelString, CustomCompanyOfferModelNoImage.class);
            System.out.println("2");
            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            } else {
                if (model.getOffer_display_date() < new Timestamp(System.currentTimeMillis()).getTime()
                        || model.getOffer_expired_date() < new Timestamp(System.currentTimeMillis()).getTime()) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("state", 400);
                    objectNode.put("message", "Validation Failed offer date in past");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }

                if (model.getOffer_display_date() >= model.getOffer_expired_date()) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("state", 400);
                    objectNode.put("message", "Validation Failed offer display date is greater than  expired date");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }

                String image2Url = "";
                String image3Url = "";
                String image4Url = "";
                String image1Url = imageUrl(image1, takatafRootLocation);
                try {
                    image2Url = imageUrl(image2, rootLocation);
                    image3Url = imageUrl(image3, rootLocation);
                    image4Url = imageUrl(image4, rootLocation);
                } catch (Exception e) {
                    image2Url = "";
                    image3Url = "";
                    image4Url = "";
                }

                int res = customCompanyOfferRepo.addOfferEdeited(image1Url, image2Url, image3Url, image4Url, model.getOffer_title(), model.getOffer_explaination(),
                        model.getOffer_cost(), model.getOffer_display_date(), model.getOffer_expired_date(), model.getOffer_deliver_date(),
                        model.getCompany_id(), model.getOffer_count(), model.getCity(), model.getArea(), model.getLng(), model.getLat(), flag_ar);
                if (res == 1) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("status", 200);
                    //objectNode.put("offer_images_id", model.getOffer_images_id());
                    objectNode.put("offer_title", model.getOffer_title());
                    objectNode.put("offer_explaination", model.getOffer_explaination());
                    objectNode.put("offer_cost", model.getOffer_cost());
                    objectNode.put("offer_display_date", model.getOffer_display_date().toString());
                    objectNode.put("offer_expired_date", model.getOffer_expired_date().toString());
                    objectNode.put("offer_deliver_date", model.getOffer_deliver_date().toString());
                    objectNode.put("company_id", model.getCompany_id());
                    objectNode.put("offer_count", model.getOffer_count());
                    objectNode.put("flag_ar", flag_ar);
                    return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                } else {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("status", 400);
                    objectNode.put("message", "not success");

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }
            }
        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "exception   " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

    }

    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    @PostMapping("/company/offer/image/{flag_ar}")
    public ResponseEntity<ObjectNode> addCompanyOffersWitImage(@RequestParam("image_one") MultipartFile
                                                                       image1, @RequestParam("image_two") MultipartFile image12,
                                                               @RequestParam("image_three") MultipartFile image13, @RequestParam("image_four") MultipartFile image4,
                                                               @RequestPart @Valid String offerModelString, Errors errors, @PathVariable  int flag_ar) {

        try {
            CustomCompanyOfferModelWithImage model = new ObjectMapper().readValue(offerModelString, CustomCompanyOfferModelWithImage.class);

            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            } else {
                if (model.getOffer_display_date() < new Timestamp(System.currentTimeMillis()).getTime()
                        || model.getOffer_expired_date() < new Timestamp(System.currentTimeMillis()).getTime()) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("state", 400);
                    objectNode.put("message", "Validation Failed offer date in past");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }

                if (model.getOffer_display_date() >= model.getOffer_expired_date()) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("state", 400);
                    objectNode.put("message", "Validation Failed offer display date is greater than  expired date");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }


//                String s = image1.getOriginalFilename().replace("\\s+", "");
//                MultipartFile multipartFile = new MockMultipartFile("file",
//                        image1.getOriginalFilename().replace(image1.getOriginalFilename(),
//                                FilenameUtils.getBaseName(s.replaceAll("\\s+", "")).replaceAll("\\s+", "")
//                                        .concat(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())) + "." +
//                                        FilenameUtils.getExtension(image1.getOriginalFilename())).toLowerCase().replaceAll("\\s+", ""), "image/*", image1.getInputStream());
//                System.out.println(MvcUriComponentsBuilder
//                        .fromMethodName(DasboardsAPIControll.class, "getFile", multipartFile.getOriginalFilename()).build().toString());
//                storageService.store(multipartFile, rootLocation);
//                String image = MvcUriComponentsBuilder
//                        .fromMethodName(DasboardsAPIControll.class, "getFile", multipartFile.getOriginalFilename()).build().toString();
////                System.out.println(file.getName() + "     " + file.getOriginalFilename());
////                files.add(f.replace("\\s+", ""));

                String logoUrl = imageUrl(image1, rootLocation);


                int res = customCompanyOfferRepo.addOfferEdeitedWithImage(logoUrl, "", "", "", model.getOffer_title(), model.getOffer_explaination(),
                        model.getOffer_cost(), model.getOffer_display_date(), model.getOffer_expired_date(), model.getOffer_deliver_date(),
                        model.getCompany_id(), model.getOffer_count(), model.getCity(), model.getArea(), model.getLng(), model.getLat(), flag_ar);
                if (res == 1) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("status", 200);
                    //objectNode.put("offer_images_id", model.getOffer_images_id());
                    objectNode.put("offer_title", model.getOffer_title());
                    objectNode.put("offer_explaination", model.getOffer_explaination());
                    objectNode.put("offer_cost", model.getOffer_cost());
                    objectNode.put("offer_display_date", model.getOffer_display_date().toString());
                    objectNode.put("offer_expired_date", model.getOffer_expired_date().toString());
                    objectNode.put("offer_deliver_date", model.getOffer_deliver_date().toString());
                    objectNode.put("company_id", model.getCompany_id());
                    objectNode.put("offer_count", model.getOffer_count());
                    objectNode.put("flag_ar", flag_ar);

                    return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                } else {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("status", 400);
                    objectNode.put("message", "not success");

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }
            }
        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "not success exception");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

    }


    @PreAuthorize("hasAuthority('company')")
    @GetMapping("/company/offers/{id}")
    public ResponseEntity<getCustomeOffer> getCompanyOffer(@PathVariable int id) {
        if (customCompanyOfferRepo.checkIfExist(id)) {
            CustomCompanyModelWithView model = customCompanyOfferRepo.getCompanyOffer(id);

            return ResponseEntity.status(HttpStatus.OK).body(new getCustomeOffer("200", model));

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new getCustomeOffer("400", null));
        }

    }


    @PreAuthorize("hasAuthority('company')")
    @GetMapping("/company/offer/{id}/{flag_ar}")
    public ResponseEntity<getCustomeOffer2> getCompanyOfferWithDesc(@PathVariable int id, @PathVariable int flag_ar) {
        if (customCompanyOfferRepo.checkIfExist(id)) {
            CustomCompanyModelWithViewAndDescRes model = customCompanyOfferRepo.getCompanyOfferWithDesc(id, flag_ar);

            return ResponseEntity.status(HttpStatus.OK).body(new getCustomeOffer2("200", model));

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new getCustomeOffer2("400", null));
        }

    }

    ///getCompanyOffersWithDesc
    @PreAuthorize("hasAuthority('company')")
    @GetMapping("/company/offer/{id}/company/{flag_ar}")
    public ResponseEntity<getCustomeCompanyOffer2Model> getSingleCompanyOfferWithDesc(@PathVariable int id, @PathVariable int flag_ar) {
        List<CustomeCompanyOfferModel2DToModel> offers = customCompanyOfferRepo.getCompanyOffersWithDesc(id, flag_ar);
        if (offers != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new getCustomeCompanyOffer2Model("200", offers));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new getCustomeCompanyOffer2Model("400", offers));
        }

    }

    @PostMapping("/company/profile/image/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> addProfileWithCategoriesAndImage(@RequestParam("logo") MultipartFile logo, @RequestParam("cover") MultipartFile cover,
                                                                       @RequestPart @Valid String multiCategoryProfileModelString, Errors errors) {

        try {

            MultiCategoryProfileModelWithImage model = new ObjectMapper().readValue(multiCategoryProfileModelString, MultiCategoryProfileModelWithImage.class);
            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
            if (multiCategoryProfileRepo.isExist(model.getCompany_id())) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "already has profile in this id");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            } else {



                String logoImage = imageUrl(logo, rootLocation);
                TimeUnit.MILLISECONDS.sleep(11);

                String coverImage = imageUrl(cover, rootLocation);


                int res = multiCategoryProfileRepo.addProfileWithCategoriesWithImage(model.getCompany_id(), model.getCompany_name(), logoImage,
                        model.getCompany_address(), model.getCompany_link_youtube(),
                        model.getCompany_website_url(), model.getCompany_lng(), model.getCompany_lat(),
                        coverImage, model.getCompany_phone_number(), model.getCompany_desc(), model.getCategory(), model.getFlag_ar());
                if (res == -1000) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("state", 400);
                    objectNode.put("message", "no user with this id");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }
                ArrayNode category = mapper.createArrayNode();

                for (int i = 0; i < model.getCategory().size(); i++) {
                    ObjectNode node = mapper.createObjectNode();
                    node.put("category_name", model.getCategory().get(i).getCategory_name());
                    category.add(node);
                    System.out.println(node.toString() + "");
                }
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 200);
                objectNode.put("company_id", model.getCompany_id());
                objectNode.put("company_name", model.getCompany_name());
                objectNode.put("company_logo_image", logoImage);
                objectNode.put("company_address", model.getCompany_address());
                //objectNode.put("company_category_id", model.getCompany_category_id());
                objectNode.put("company_link_youtube", model.getCompany_link_youtube());
                objectNode.put("company_website_url", model.getCompany_website_url());
                objectNode.put("company_lng", model.getCompany_lng());
                objectNode.put("company_lat", model.getCompany_lat());
                objectNode.put("company_cover_image", coverImage);
                objectNode.put("company_phone_number", model.getCompany_phone_number());
                objectNode.put("company_desc", model.getCompany_desc());
                objectNode.set("categories", category);
                return ResponseEntity.status(HttpStatus.OK).body(objectNode);


            }
        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "exception   " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }


    @GetMapping("/company/profile/{id}/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public MultiCategoryProfileDTO getProfileCompany(@PathVariable int id, @PathVariable int flag_ar) {
        List<TakatfTenderCategoryPOJO> category = new ArrayList<>();
        List<MultiCategoryProfileDTOS> allData = multiCategoryProfileRepo.getProfile(id, flag_ar);
        for (int i = 0; i < allData.size(); i++) {
            TakatfTenderCategoryPOJO pojo = new TakatfTenderCategoryPOJO(allData.get(i).getCompany_cat_name());
            category.add(pojo);
        }
        MultiCategoryProfileDTO result = new MultiCategoryProfileDTO(allData.get(0).getCompany_id(), allData.get(0).getCompany_name(),
                allData.get(0).getCompany_logo_image(), allData.get(0).getCompany_address(),
                allData.get(0).getCompany_link_youtube(), allData.get(0).getCompany_website_url(), allData.get(0).getCompany_lng(),
                allData.get(0).getCompany_lat(), allData.get(0).getCompany_cover_image(), allData.get(0).getCompany_phone_number(),
                allData.get(0).getFollower_count(), allData.get(0).getOffer_count(), allData.get(0).getCompany_desc(), category);
        return result;
    }

    @PutMapping("/company/profile/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> updateProfile(@RequestParam(value = "logo", required = false) MultipartFile logo, @RequestParam(value = "cover", required = false) MultipartFile cover,
                                                    @Valid @RequestPart String multiCategoryProfileModelString, Errors errors) {

        try {

            MultiCategoryProfileModel model = new ObjectMapper().readValue(multiCategoryProfileModelString, MultiCategoryProfileModel.class);

            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
            if (multiCategoryProfileRepo.isExist(model.getCompany_id())) {


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


                        int res = multiCategoryProfileRepo.updateProfile(model.getCompany_id(), model.getCompany_name(), model.getCompany_logo_image(),
                                model.getCompany_address(),
                                model.getCompany_link_youtube(), model.getCompany_website_url(), model.getCompany_lng(),
                                model.getCompany_lat(), model.getCompany_cover_image(), model.getCompany_phone_number(), model.getCompany_desc(),
                                model.getCategory(), model.getFlag_ar());
                        if (res == 1) {
                            ArrayNode category = mapper.createArrayNode();

                            for (int i = 0; i < model.getCategory().size(); i++) {
                                ObjectNode node = mapper.createObjectNode();
                                node.put("category_name", model.getCategory().get(i).getCategory_name());
                                category.add(node);
                                System.out.println(node.toString() + "");
                            }
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put("state", 200);
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
                            objectNode.put("flag_ar", model.getFlag_ar());
                            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                        } else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put("state", 400);
                            objectNode.put("message", "failed");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                        }

                    } else {


                        int res = multiCategoryProfileRepo.updateProfile(model.getCompany_id(), model.getCompany_name(), model.getCompany_logo_image(), model.getCompany_address(),
                                model.getCompany_link_youtube(), model.getCompany_website_url(), model.getCompany_lng(),
                                model.getCompany_lat(), coverUrl, model.getCompany_phone_number(), model.getCompany_desc(), model.getCategory(), model.getFlag_ar());
                        if (res == 1) {
                            ArrayNode category = mapper.createArrayNode();

                            for (int i = 0; i < model.getCategory().size(); i++) {
                                ObjectNode node = mapper.createObjectNode();
                                node.put("category_name", model.getCategory().get(i).getCategory_name());
                                category.add(node);
                                System.out.println(node.toString() + "");
                            }
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put("state", 200);
                            objectNode.put("company_id", model.getCompany_id());
                            objectNode.put("company_name", model.getCompany_name());
                            objectNode.put("company_logo_image", model.getCompany_logo_image());
                            objectNode.put("company_address", model.getCompany_address());
                            objectNode.put("company_link_youtube", model.getCompany_link_youtube());
                            objectNode.put("company_website_url", model.getCompany_website_url());
                            objectNode.put("company_lng", model.getCompany_lng());
                            objectNode.put("company_lat", model.getCompany_lat());
                            objectNode.put("company_cover_image", coverUrl);
                            objectNode.put("company_phone_number", model.getCompany_phone_number());
                            objectNode.put("company_desc", model.getCompany_desc());
                            objectNode.set("categories", category);
                            objectNode.put("flag_ar", model.getFlag_ar());
                            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                        } else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put("state", 400);
                            objectNode.put("message", "failed");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                        }


                    }


                } else if (coverUrl.equals("") || coverUrl.equals(null) || coverUrl.isEmpty()) {


                    if (logoUrl.equals("") || logoUrl.equals(null) || logoUrl.isEmpty()) {


                        int res = multiCategoryProfileRepo.updateProfile(model.getCompany_id(), model.getCompany_name(), model.getCompany_logo_image(), model.getCompany_address(),
                                model.getCompany_link_youtube(), model.getCompany_website_url(), model.getCompany_lng(),
                                model.getCompany_lat(), model.getCompany_cover_image(), model.getCompany_phone_number(), model.getCompany_desc(), model.getCategory(), model.getFlag_ar());
                        if (res == 1) {
                            ArrayNode category = mapper.createArrayNode();

                            for (int i = 0; i < model.getCategory().size(); i++) {
                                ObjectNode node = mapper.createObjectNode();
                                node.put("category_name", model.getCategory().get(i).getCategory_name());
                                category.add(node);
                                System.out.println(node.toString() + "");
                            }
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put("state", 200);
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
                            objectNode.put("flag_ar", model.getFlag_ar());
                            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                        } else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put("state", 400);
                            objectNode.put("message", "failed");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                        }


                    } else {


                        int res = multiCategoryProfileRepo.updateProfile(model.getCompany_id(), model.getCompany_name(), logoUrl, model.getCompany_address(),
                                model.getCompany_link_youtube(), model.getCompany_website_url(), model.getCompany_lng(),
                                model.getCompany_lat(), model.getCompany_cover_image(), model.getCompany_phone_number(), model.getCompany_desc(), model.getCategory(), model.getFlag_ar());
                        if (res == 1) {
                            ArrayNode category = mapper.createArrayNode();

                            for (int i = 0; i < model.getCategory().size(); i++) {
                                ObjectNode node = mapper.createObjectNode();
                                node.put("category_name", model.getCategory().get(i).getCategory_name());
                                category.add(node);
                                System.out.println(node.toString() + "");
                            }
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put("state", 200);
                            objectNode.put("company_id", model.getCompany_id());
                            objectNode.put("company_name", model.getCompany_name());
                            objectNode.put("company_logo_image", logoUrl);
                            objectNode.put("company_address", model.getCompany_address());
                            objectNode.put("company_link_youtube", model.getCompany_link_youtube());
                            objectNode.put("company_website_url", model.getCompany_website_url());
                            objectNode.put("company_lng", model.getCompany_lng());
                            objectNode.put("company_lat", model.getCompany_lat());
                            objectNode.put("company_cover_image", model.getCompany_cover_image());
                            objectNode.put("company_phone_number", model.getCompany_phone_number());
                            objectNode.put("company_desc", model.getCompany_desc());
                            objectNode.set("categories", category);
                            objectNode.put("flag_ar", model.getFlag_ar());
                            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                        } else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put("state", 400);
                            objectNode.put("message", "failed");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                        }


                    }


                } else {


                    int res = multiCategoryProfileRepo.updateProfile(model.getCompany_id(), model.getCompany_name(), logoUrl, model.getCompany_address(),
                            model.getCompany_link_youtube(), model.getCompany_website_url(), model.getCompany_lng(),
                            model.getCompany_lat(), coverUrl, model.getCompany_phone_number(), model.getCompany_desc(), model.getCategory(), model.getFlag_ar());
                    if (res == 1) {
                        ArrayNode category = mapper.createArrayNode();

                        for (int i = 0; i < model.getCategory().size(); i++) {
                            ObjectNode node = mapper.createObjectNode();
                            node.put("category_name", model.getCategory().get(i).getCategory_name());
                            category.add(node);
                            System.out.println(node.toString() + "");
                        }
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("state", 200);
                        objectNode.put("company_id", model.getCompany_id());
                        objectNode.put("company_name", model.getCompany_name());
                        objectNode.put("company_logo_image", logoUrl);
                        objectNode.put("company_address", model.getCompany_address());
                        objectNode.put("company_link_youtube", model.getCompany_link_youtube());
                        objectNode.put("company_website_url", model.getCompany_website_url());
                        objectNode.put("company_lng", model.getCompany_lng());
                        objectNode.put("company_lat", model.getCompany_lat());
                        objectNode.put("company_cover_image", coverUrl);
                        objectNode.put("company_phone_number", model.getCompany_phone_number());
                        objectNode.put("company_desc", model.getCompany_desc());
                        objectNode.set("categories", category);
                        objectNode.put("flag_ar", model.getFlag_ar());
                        return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                    } else {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("state", 400);
                        objectNode.put("message", "failed");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }


                }


            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "no profile to this id");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "Not Success  " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }

    @PutMapping("company/profil/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> updateProfile2(@RequestParam(value = "logo", required = false) MultipartFile logo,
                                                     @RequestParam(value = "cover", required = false) MultipartFile cover,
                                                     @Valid @RequestPart String newProfileDto3String, Errors errors, @PathVariable int flag_ar) {


        try {
            NewProfileDto32 model = new ObjectMapper().readValue(newProfileDto3String, NewProfileDto32.class);


            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put(STATUS, 400);
                objectNode.put(MESSAGE, "Validation Failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
            if (newProfileRepo.isExist(model.getCompanyId())) {


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

                        int res = newProfileRepo.updateProfile(model.getCompanyId(), model.getCompanyName(), model.getCompanyLogoImage(), model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), model.getCompanyCoverImage(), model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getCategory(), flag_ar);
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

                        int res = newProfileRepo.updateProfile(model.getCompanyId(), model.getCompanyName(), coverUrl, model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), model.getCompanyCoverImage(), model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getCategory(), flag_ar);
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


                        int res = newProfileRepo.updateProfile(model.getCompanyId(), model.getCompanyName(), model.getCompanyLogoImage(), model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), model.getCompanyCoverImage(), model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getCategory(), flag_ar);
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


                        int res = newProfileRepo.updateProfile(model.getCompanyId(), model.getCompanyName(), logoUrl, model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), model.getCompanyCoverImage(), model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getCategory(), flag_ar);
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
                    int res = newProfileRepo.updateProfile(model.getCompanyId(), model.getCompanyName(), logoUrl, model.getCompanyAddress(),
                            model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                            model.getCompanyLat(), coverUrl, model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                            model.getCity(), model.getArea(), model.getCategory(), flag_ar);
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
                objectNode.put(MESSAGE, "already has no profile in this id");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }


    @PutMapping("/s/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> updateProfile2x(@RequestParam(value = "logo", required = false) MultipartFile logo, @RequestParam(value = "cover", required = false) MultipartFile cover,
                                                      @Valid @RequestPart String newProfileDto3DTOString, Errors errors ,@PathVariable int flag_ar) {


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
                        int res = newProfileRepo.updateProfile2(model.getCompanyId(), model.getCompanyName(), model.getCompanyLogoImage(), model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), model.getCompanyCoverImage(), model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getLng(), model.getLat(), model.getCategory(), flag_ar);
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


                        int res = newProfileRepo.updateProfile2(model.getCompanyId(), model.getCompanyName(), model.getCompanyLogoImage(), model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), coverUrl, model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getLng(), model.getLat(), model.getCategory(), flag_ar);
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

                        int res = newProfileRepo.updateProfile2(model.getCompanyId(), model.getCompanyName(), model.getCompanyLogoImage(), model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), model.getCompanyCoverImage(), model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getLng(), model.getLat(), model.getCategory(), flag_ar);
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


                        int res = newProfileRepo.updateProfile2(model.getCompanyId(), model.getCompanyName(), logoUrl, model.getCompanyAddress(),
                                model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                                model.getCompanyLat(), model.getCompanyCoverImage(), model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                                model.getCity(), model.getArea(), model.getLng(), model.getLat(), model.getCategory(), flag_ar);
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


                    int res = newProfileRepo.updateProfile2(model.getCompanyId(), model.getCompanyName(), logoUrl, model.getCompanyAddress(),
                            model.getCompanyLinkYoutube(), model.getCompanyWebsiteUrl(), model.getCompanyLng(),
                            model.getCompanyLat(), coverUrl, model.getCompanyPhoneNumber(), model.getCompanyDesc(),
                            model.getCity(), model.getArea(), model.getLng(), model.getLat(), model.getCategory(), flag_ar);
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


    @PreAuthorize("hasAuthority('company')")
    @GetMapping("/company/offer/{id}/company/page/{page}/{pageSize}/{flag_ar}")
    public ResponseEntity<CustomeCompanyOfferModel2DToModelPagination> getSingleCompanyOfferWithDescPagination(
            @PathVariable int id, @PathVariable int page, @PathVariable int pageSize, @PathVariable int flag_ar) {
        return customCompanyOfferRepo.getCompanyOffersWithDescPagination(id, page, pageSize, flag_ar);
    }


    @PreAuthorize("hasAuthority('company') ")
    @GetMapping("/company/offer/{id}/companies")
    public ResponseEntity<getCustomeCompanyOffer> getSingleCompanyOffer(@PathVariable int id) {
        List<CustomeCompanyOfferModel2> offers = customCompanyOfferRepo.getCompanyOffers(id);
        if (offers != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new getCustomeCompanyOffer("200", offers));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new getCustomeCompanyOffer("400", offers));
        }

    }


    @PreAuthorize("hasAuthority('company')")
    @PutMapping("/company/offer/{flag_ar}")
    public ResponseEntity<JsonNode> updateCompanyOfferWithImage
            (@RequestParam(value = "image1", required = false) MultipartFile
                     image1, @RequestParam(value = "image2", required = false) MultipartFile image2,
             @RequestParam(value = "image3", required = false) MultipartFile
                     image3, @RequestParam(value = "image4", required = false) MultipartFile image4,
             @RequestPart @Valid String customCompanyOfferModelWithImageString, Errors errors, @PathVariable int flag_ar) {


        try {

            CustomCompanyOfferModel model = new ObjectMapper().readValue(customCompanyOfferModelWithImageString, CustomCompanyOfferModel.class);

            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");
                //objectNode.put("details", errors.getAllErrors().toString());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
            if (customCompanyOfferRepo.checkIfExist(model.getOffer_id())) {
                String image1Url = "";
                try {
                    image1Url = imageUrl(image1, rootLocation);
                } catch (Exception e) {
                    image1Url = "";
                }


                String image2Url = "";
                String image3Url = "";
                String image4Url = "";

                try {
                    image2Url = imageUrl(image2, rootLocation);
                    image3Url = imageUrl(image3, rootLocation);
                    image4Url = imageUrl(image4, rootLocation);
                } catch (Exception e) {
                    image2Url = "";
                    image3Url = "";
                    image4Url = "";
                }


                if (image1Url.isEmpty() || image1Url.equals(null) || image1Url.equals("")) {


                    int res = customCompanyOfferRepo.updateCompanyOfferWithImages(model.getOffer_id(), model.getImage_one(), image2Url, image3Url, image4Url,
                            model.getOffer_title(), model.getOffer_explaination(), model.getOffer_cost(), model.getOffer_display_date(),
                            model.getOffer_expired_date(), model.getOffer_deliver_date(), model.getCompany_id(), model.getOffer_count(), model.getCity(), model.getArea(),
                            model.getLng(), model.getLat(), flag_ar);

                    if (res == 1) {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("status", 200);
                        objectNode.put("offer_id", model.getOffer_id());
                        objectNode.put("image_one", model.getImage_one());
                        objectNode.put("image_two", image2Url);
                        objectNode.put("image_three", image3Url);
                        objectNode.put("image_four", image4Url);
                        objectNode.put("offer_title", model.getOffer_title());
                        objectNode.put("offer_explaination", model.getOffer_explaination());
                        objectNode.put("offer_cost", model.getOffer_cost());
                        objectNode.put("offer_display_date", model.getOffer_display_date());
                        objectNode.put("offer_expired_date", model.getOffer_expired_date());
                        objectNode.put("offer_deliver_date", model.getOffer_deliver_date());
                        objectNode.put("company_id", model.getCompany_id());
                        objectNode.put("offer_count", model.getOffer_count());
                        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                    } else {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("status", 400);
                        objectNode.put("message", "not success");

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }


                } else {


                    int res = customCompanyOfferRepo.updateCompanyOfferWithImages(model.getOffer_id(), image1Url, image2Url, image3Url, image4Url,
                            model.getOffer_title(), model.getOffer_explaination(), model.getOffer_cost(), model.getOffer_display_date(),
                            model.getOffer_expired_date(), model.getOffer_deliver_date(), model.getCompany_id(), model.getOffer_count(), model.getCity(), model.getArea(),
                            model.getLng(), model.getLat(), flag_ar);

                    if (res == 1) {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("status", 200);
                        objectNode.put("offer_id", model.getOffer_id());
                        objectNode.put("image_one", image1Url);
                        objectNode.put("image_two", image2Url);
                        objectNode.put("image_three", image3Url);
                        objectNode.put("image_four", image4Url);
                        objectNode.put("offer_title", model.getOffer_title());
                        objectNode.put("offer_explaination", model.getOffer_explaination());
                        objectNode.put("offer_cost", model.getOffer_cost());
                        objectNode.put("offer_display_date", model.getOffer_display_date());
                        objectNode.put("offer_expired_date", model.getOffer_expired_date());
                        objectNode.put("offer_deliver_date", model.getOffer_deliver_date());
                        objectNode.put("company_id", model.getCompany_id());
                        objectNode.put("offer_count", model.getOffer_count());
                        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                    } else {
                        ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("status", 400);
                        objectNode.put("message", "not success");

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                    }


                }


            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("status", 400);
                objectNode.put("message", "not exist");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "not success e" + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

    }

    @PreAuthorize("hasAuthority('company')")
    @DeleteMapping("/companyOffer/delete/{id}")
    public ResponseEntity<ObjectNode> deleteCompanyOffer(@PathVariable int id) {

        if (companyOfferRepo.checkIfExist(id)) {
            int res = companyOfferRepo.deleteCompanyOffer(id);
            if (res == 1) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("message", "success");

                return ResponseEntity.status(HttpStatus.OK).body(objectNode);
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("message", "not success");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("message", "not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

    }


    @PreAuthorize("hasAuthority('company') or hasAuthority('school')")
    @PostMapping("/follow/add")
    public ResponseEntity<ObjectNode> addFollower(@RequestBody @Valid SchoolFollowCompany model, Errors errors) {
        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            //objectNode.put("details", errors.getAllErrors().toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

        if (repo.isExist(model.getFollow_id())) {

            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "id exist");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        } else {
            if (schoolFollowCompanyRepo.isExistFollwing(model.getOrganization_id()) && schoolFollowCompanyRepo.isExistFollwer(model.getFollower_id())) {
                int res = schoolFollowCompanyRepo.addFollower(model.getOrganization_id(), model.getFollower_id());
                if (res == 1) {

                    int id = schoolFollowCompanyRepo.getId(model.getOrganization_id(), model.getFollower_id());

                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("status", 200);
                    objectNode.put("follow_id", id);
                    objectNode.put("Organization_id", model.getOrganization_id());
                    objectNode.put("Follower_id", model.getFollower_id());

                    return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                } else if (res == 0) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("status", 400);
                    objectNode.put("message", "not success");

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                } else {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("status", 400);
                    objectNode.put("message", "is already follow it");

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }

            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("status", 400);
                objectNode.put("message", "organization not exist");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }

        }


    }


    @PreAuthorize("hasAuthority('admin') or hasAuthority('school') or hasAuthority('company')")
    @DeleteMapping("/follow/org/{org_id}/follower/{follow_id}")
    public ObjectNode deleteSchoolFollowCompanyByIds(@PathVariable int org_id, @PathVariable int follow_id) {
        if (schoolFollowCompanyRepo.isRecordExist(org_id, follow_id)) {
            int res = schoolFollowCompanyRepo.deleteSchoolFollowCompanyByIds(org_id, follow_id);
            if (res == 1) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("status", 200);
                objectNode.put("message", "success");

                return objectNode;
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("status", 400);
                objectNode.put("message", "not success");

                return objectNode;
            }
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "not exist");

            return objectNode;
        }

    }

    @GetMapping("/admin/orders/school/")
    public List<SchoolOrdersModel> getAllSchoolOrders() {
        return adminRepo.getAllSchoolOrders();
    }

    @GetMapping("/admin/orders/school/page/{page}/{pageSize}")
    public SchoolOrdersModelPagination getAllSchoolOrdersPagination(@PathVariable int page,
                                                                    @PathVariable int pageSzie) {
        return adminRepo.getAllSchoolOrdersPagination(page, pageSzie);
    }

    @GetMapping("/admin/orders/school/{id}")
    public ResponseEntity<SingleSchoolOrdersModel> getSchoolOrder(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(adminRepo.getSchoolOrder(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/admin/orders/school/history/")
    public List<SchoolOrdersHistoryModel> getAllSchoolOrdersHistory() {
        return adminRepo.getAllSchoolOrdersHistory();
    }

    @GetMapping("/admin/orders/school/history/{id}")
    public SingleSchoolOrdersModel getSchoolOrderHistory(@PathVariable int id) {
        return adminRepo.getSchoolOrderHistory(id);
    }


    public String imageUrl(MultipartFile file, Path rootLoc) throws Exception {
        String s = file.getOriginalFilename().replace("\\s+", "");
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getOriginalFilename().replace(file.getOriginalFilename(),
                        FilenameUtils.getBaseName(s.replaceAll("\\s+", "")).replaceAll("\\s+", "")
                                .concat(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())) + "." +
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
