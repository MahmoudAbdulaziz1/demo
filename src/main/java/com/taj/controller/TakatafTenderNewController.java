package com.taj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.controller.test_image.StorageService;
import com.taj.controller.test_image.UploadController;
import com.taj.model.CategoriesInUse;
import com.taj.model.TakatafMyTenderPageDTO;
import com.taj.model.TakatafTenderNewModel;
import com.taj.model.TakatafTenderPOJO;
import com.taj.repository.TakatafTenderNewRepo;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 8/19/2018.
 */
@RequestMapping("/service/evvaz/s/takataf/tenders")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TakatafTenderNewController {

    private final Path rootLocation = Paths.get("upload-dir");
    @Autowired
    TakatafTenderNewRepo repo;
    @Autowired
    ObjectMapper mapper;
    List<String> files = new ArrayList<String>();
    @Autowired
    StorageService storageService;

    @PostMapping("/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
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

            String logoUrl = imageUrl(logo, rootLocation);

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

        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success e");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<TakatafTenderPOJO> getTenders() {
        return repo.getTenders();

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<TakatafTenderPOJO> getTender(@PathVariable int id) {
        if (repo.isExist(id)) {
            return ResponseEntity.status(HttpStatus.OK).body(repo.getTender(id));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(repo.getTender(id));
        }


    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<TakatafMyTenderPageDTO> getAdminTenders() {
        return repo.getAdminTenders();

    }

    @PutMapping("/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> updateTender(@RequestParam("logo") MultipartFile logo,
                                                   @RequestPart @Valid String takatafTenderNewModelString, Errors errors) {

        try {

            TakatafTenderNewModel model = new ObjectMapper().readValue(takatafTenderNewModelString, TakatafTenderNewModel.class);
            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }

            String logoUrl = imageUrl(logo, rootLocation);

            int res = repo.addTender(logoUrl, model.getTender_title(), model.getTender_explain(),
                    model.getTender_display_date(), model.getTender_expire_date(), model.getTender_company_display_date(),
                    model.getTender_company_expired_date(), model.getCats(), model.getFlag_ar());

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
                for (int i = 0; i < model.getCats().size(); i++) {
                    int categorys = repo.getCategoryId(model.getCats().get(i).getCategory_name(), model.getFlag_ar());
                    if (categorys > 0) {
                        nodes.add(model.getCats().get(i).getCategory_name());
                    }
                }
                objectNode.set("cats", nodes);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("value", "not success");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success e");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public int deleteTenderWithItsResponse(@PathVariable int id) {
        return repo.deleteTenderWithItsResponse(id);
    }


    //    @GetMapping("/detail/{id}")
//    public TakatafTenderWithCompanies getTendersByCompanies(@PathVariable int id){
//        return repo.getSingleTenderDetails(id);
//    }
//
//    @PutMapping("/accept/{request_id}")
//    public ResponseEntity<ObjectNode> acceptOffer(@PathVariable int request_id) {
//        if (repo.isExist(request_id)) {
//            int response = repo.acceptOffer(request_id);
//            if (response == 1) {
//                ObjectNode objectNode = mapper.createObjectNode();
//                objectNode.put("message", "success");
//                return ResponseEntity.status(HttpStatus.OK).body(objectNode);
//            } else {
//                ObjectNode objectNode = mapper.createObjectNode();
//                objectNode.put("message", "not sucess");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
//            }
//        } else {
//            ObjectNode objectNode = mapper.createObjectNode();
//            objectNode.put("message", "no exist request");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
//        }
//
//    }
//
//    @PutMapping("/cancel/{request_id}")
//    public ResponseEntity<ObjectNode> cancelOffer(@PathVariable int request_id) {
//        if (repo.isExist(request_id)) {
//            int response = repo.cancelOffer(request_id);
//            if (response == 1) {
//                ObjectNode objectNode = mapper.createObjectNode();
//                objectNode.put("message", "success");
//                return ResponseEntity.status(HttpStatus.OK).body(objectNode);
//            } else {
//                ObjectNode objectNode = mapper.createObjectNode();
//                objectNode.put("message", "not sucess");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
//            }
//        } else {
//            ObjectNode objectNode = mapper.createObjectNode();
//            objectNode.put("message", "no exist request");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
//        }
//    }
//
//    @PutMapping("/")
//    public JsonNode updateRequestModel(@RequestBody @Valid TakatafTenderNewModel model, Errors errors) {
//        if (errors.hasErrors()) {
//            ObjectNode objectNode = mapper.createObjectNode();
//            objectNode.put("state", 400);
//            objectNode.put("message", "Validation Failed");
//
//            return objectNode;
//        }
//        int res = repo.updateRequest(model.getTender_id(), model.getTender_logo(), model.getTender_title(),
//                model.getTender_explain(), model.getTender_display_date(), model.getTender_expire_date(),
//                model.getTender_company_display_date(), model.getTender_company_expired_date(), model.getTender_cat_id());
//
//        if (res == 1) {
//            ObjectNode objectNode = mapper.createObjectNode();
//            objectNode.put("request_id", model.getTender_cat_id());
//            objectNode.put("request_logo", model.getTender_logo());
//            objectNode.put("request_title", model.getTender_title());
//            objectNode.put("request_explaination", model.getTender_explain());
//            objectNode.put("request_display_date", model.getTender_display_date());
//            objectNode.put("request_expired_date", model.getTender_expire_date());
//            objectNode.put("tender_company_display_date", model.getTender_company_display_date());
//            objectNode.put("tender_company_expire_date", model.getTender_company_expired_date());
//            objectNode.put("request_category_id", model.getTender_cat_id());
//
//            return objectNode;
//        } else {
//            ObjectNode objectNode = mapper.createObjectNode();
//            objectNode.put("status", 400);
//            objectNode.put("message", "not success");
//
//            return objectNode;
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ObjectNode deleteSchoolRequest(@PathVariable int id) {
//        if (repo.isExist(id)) {
//            int res = repo.deleteSchoolRequest(id);
//            if (res == 1) {
//                ObjectNode objectNode = mapper.createObjectNode();
//                objectNode.put("message", "success");
//
//                return objectNode;
//            } else {
//                ObjectNode objectNode = mapper.createObjectNode();
//                objectNode.put("message", "not success");
//
//                return objectNode;
//            }
//        } else {
//            ObjectNode objectNode = mapper.createObjectNode();
//            objectNode.put("message", "not element by this id");
//
//            return objectNode;
//        }
//
//    }
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
