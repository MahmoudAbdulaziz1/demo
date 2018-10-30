package com.taj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.controller.test_image.StorageService;
import com.taj.controller.test_image.UploadController;
import com.taj.model.*;
import com.taj.model.copany_offer_iamge.MultiCategoryProfileModelWithImage;
import com.taj.repository.MultiCategoryProfileRepo;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by User on 9/4/2018.
 */
@RequestMapping("/service/evvaz/s/company/profile")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MultiCategoryProfileController {

    private final Path rootLocation = Paths.get("upload-dir");
    @Autowired
    MultiCategoryProfileRepo repo;
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
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        if (repo.isExist(model.getCompany_id())) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "already has profile in this id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        } else {
            int res = repo.addProfileWithCategories(model.getCompany_id(), model.getCompany_name(), model.getCompany_logo_image(),
                    model.getCompany_address(), model.getCompany_link_youtube(),
                    model.getCompany_website_url(), model.getCompany_lng(), model.getCompany_lat(),
                    model.getCompany_cover_image(), model.getCompany_phone_number(), model.getCompany_desc(), model.getCategory());

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
            //objectNode.put("company_category_id", model.getCompany_category_id());
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


    @GetMapping("/getallfiles")
    public ResponseEntity<List<String>> getListFiles(Model model) {
        List<String> fileNames = files
                .stream().map(fileName -> MvcUriComponentsBuilder
                        .fromMethodName(UploadController.class, "getFile", fileName).build().toString())
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(fileNames);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.loadFile(filename, rootLocation);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }


    @PostMapping("/image/")
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
            if (repo.isExist(model.getCompany_id())) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "already has profile in this id");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            } else {


                String s = logo.getOriginalFilename().replace("\\s+", "");
                MultipartFile multipartFile = new MockMultipartFile("file",
                        logo.getOriginalFilename().replace(logo.getOriginalFilename(),
                                FilenameUtils.getBaseName(s.replaceAll("\\s+", "")).replaceAll("\\s+", "")
                                        .concat(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())) + "." +
                                        FilenameUtils.getExtension(logo.getOriginalFilename())).toLowerCase().replaceAll("\\s+", ""), "image/*", logo.getInputStream());
                System.out.println(MvcUriComponentsBuilder
                        .fromMethodName(UploadController.class, "getFile", multipartFile.getOriginalFilename()).build().toString());
                try {
                    if (Files.isDirectory(rootLocation)) {

                    } else {
                        Files.createDirectory(rootLocation);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Could not initialize storage!");
                }
                storageService.store(multipartFile);
                String logoImage = MvcUriComponentsBuilder
                        .fromMethodName(UploadController.class, "getFile", multipartFile.getOriginalFilename()).build().toString();


                String coverImage = imageUrl(cover, rootLocation);


                int res = repo.addProfileWithCategoriesWithImage(model.getCompany_id(), model.getCompany_name(), logoImage,
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


    @GetMapping("/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<MultiCategoryProfileGetAllDTO> getProfiles() {
        return repo.getProfiles();
    }

//    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
//    public MultiCategoryProfileDTO getProfile(@PathVariable int id) {
//        List<TakatfTenderCategoryPOJO> category = new ArrayList<>();
//        List<MultiCategoryProfileDTOS> allData = repo.getProfile(id);
//        for (int i = 0; i < allData.size(); i++) {
//            TakatfTenderCategoryPOJO pojo = new TakatfTenderCategoryPOJO(allData.get(i).getCompany_cat_name());
//            category.add(pojo);
//        }
//        MultiCategoryProfileDTO result = new MultiCategoryProfileDTO(allData.get(0).getCompany_id(), allData.get(0).getCompany_name(),
//                allData.get(0).getCompany_logo_image(), allData.get(0).getCompany_address(),
//                allData.get(0).getCompany_link_youtube(), allData.get(0).getCompany_website_url(), allData.get(0).getCompany_lng(),
//                allData.get(0).getCompany_lat(), allData.get(0).getCompany_cover_image(), allData.get(0).getCompany_phone_number(),
//                allData.get(0).getFollower_count(), allData.get(0).getOffer_count(), allData.get(0).getCompany_desc(), category);
//        return result;
//    }

    @PutMapping("/")
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
            if (repo.isExist(model.getCompany_id())) {


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


                        int res = repo.updateProfile(model.getCompany_id(), model.getCompany_name(), model.getCompany_logo_image(),
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
                            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                        } else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put("state", 400);
                            objectNode.put("message", "failed");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                        }

                    } else {


                        int res = repo.updateProfile(model.getCompany_id(), model.getCompany_name(), model.getCompany_logo_image(), model.getCompany_address(),
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


                        int res = repo.updateProfile(model.getCompany_id(), model.getCompany_name(), model.getCompany_logo_image(), model.getCompany_address(),
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
                            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                        } else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put("state", 400);
                            objectNode.put("message", "failed");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                        }


                    } else {


                        int res = repo.updateProfile(model.getCompany_id(), model.getCompany_name(), logoUrl, model.getCompany_address(),
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
                            return ResponseEntity.status(HttpStatus.OK).body(objectNode);


                        } else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put("state", 400);
                            objectNode.put("message", "failed");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                        }


                    }


                } else {


                    int res = repo.updateProfile(model.getCompany_id(), model.getCompany_name(), logoUrl, model.getCompany_address(),
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

    @GetMapping("/category/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<GetCompanyByCategoryCompany> getProfileByCategoryAndroid(@PathVariable String id) {
        List<CompanyProfileDto> model = repo.getProfileByCategory(id);
        if (model.size() > 0) {
            return ResponseEntity.status(HttpStatus.OK).body(new GetCompanyByCategoryCompany("200", model));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GetCompanyByCategoryCompany("400", null));
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
