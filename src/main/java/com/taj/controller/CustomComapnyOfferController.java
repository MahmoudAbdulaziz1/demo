package com.taj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.controller.test_image.StorageService;
import com.taj.controller.test_image.UploadController;
import com.taj.model.CustomCompanyModelWithViewAndDescRes2;
import com.taj.model.CustomCompanyOfferModel;
import com.taj.model.CustomeCompanyOfferModel2;
import com.taj.model.Pagination.CustomCompanyOfferModel2Res;
import com.taj.model.getCustomeCompanyOffer;
import com.taj.model.new_company_history.CompanyHistoryDto;
import com.taj.model.new_company_history.CompanyHistoryDto2;
import com.taj.model.offer_description.CustomCompanyModelWithViewAndDescRes;
import com.taj.model.offer_description.getCustomeOffer2;
import com.taj.model.offer_description.getCustomeOffer2DTO;
import com.taj.model.test_image.CustomCompanyOfferModelWithImage;
import com.taj.repository.CustomCompanyOfferRepo;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by User on 8/5/2018.
 */
@RequestMapping("/service/evvaz/s/company/offer")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomComapnyOfferController {
    @Autowired
    CustomCompanyOfferRepo repo;
    @Autowired
    ObjectMapper mapper;

    private final Path rootLocation = Paths.get("upload-dir");
    @Autowired
    StorageService storageService;
    List<String> files = new ArrayList<String>();

    /**
     * add offer from company
     *
     * @param model
     * @return 1 if success or 0 if failed
     */

    @PostMapping("/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> addCompanyOffers(@Valid @RequestBody CustomCompanyOfferModel model, Errors errors, int flag_ar) {

        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        } else {
            int res = repo.addOfferEdeited(model.getImage_one(), model.getImage_two(), model.getImage_third(), model.getImage_four(), model.getOffer_title(), model.getOffer_explaination(),
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
                return ResponseEntity.status(HttpStatus.OK).body(objectNode);
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("status", 400);
                objectNode.put("message", "not success");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        }

    }


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
        Resource file = storageService.loadFile(filename, rootLocation);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }


    @PostMapping("/image/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> addCompanyOffersWitImage(@RequestParam("image_one") MultipartFile image1, @RequestParam("image_two") MultipartFile image12,
                                                               @RequestParam("image_three") MultipartFile image13, @RequestParam("image_four") MultipartFile image4,
                                                               @RequestPart @Valid String offerModelString, Errors errors,@PathVariable int flag_ar) {

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



                String s = image1.getOriginalFilename().replace("\\s+", "");
                MultipartFile multipartFile = new MockMultipartFile("file",
                        image1.getOriginalFilename().replace(image1.getOriginalFilename(),
                                FilenameUtils.getBaseName(s.replaceAll("\\s+", "")).replaceAll("\\s+", "")
                                        .concat(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())) + "." +
                                        FilenameUtils.getExtension(image1.getOriginalFilename())).toLowerCase().replaceAll("\\s+", ""), "image/*", image1.getInputStream());
                System.out.println(MvcUriComponentsBuilder
                        .fromMethodName(CustomComapnyOfferController.class, "getFile", multipartFile.getOriginalFilename()).build().toString());
                try {
                    if (Files.isDirectory(rootLocation)) {

                    } else {
                        Files.createDirectory(rootLocation);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Could not initialize storage!");
                }
                storageService.store(multipartFile);
                String image = MvcUriComponentsBuilder
                        .fromMethodName(CustomComapnyOfferController.class, "getFile", multipartFile.getOriginalFilename()).build().toString();
//                System.out.println(file.getName() + "     " + file.getOriginalFilename());
//                files.add(f.replace("\\s+", ""));


                String image2Url = imageUrl(image12, rootLocation);
                String image3Url = imageUrl(image13, rootLocation);
                String image4Url = imageUrl(image4, rootLocation);

                int res = repo.addOfferEdeitedWithImage(image, image2Url, image3Url, image4Url, model.getOffer_title(), model.getOffer_explaination(),
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
            objectNode.put("message", "not success exception \n"+ e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

    }






    @GetMapping("/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<CustomCompanyOfferModel> getCompanyOffers() {
        return repo.getAllOffers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<getCustomeOffer2> getCompanyOffers(@PathVariable int id, @RequestParam(value = "flag") int flag_ar) {
        if (repo.checkIfExist(id)) {
            CustomCompanyModelWithViewAndDescRes model = repo.getCompanyOfferWithDesc(id, flag_ar);

            return ResponseEntity.status(HttpStatus.OK).body(new getCustomeOffer2("200", model));

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new getCustomeOffer2("400", null));
        }

    }


    @GetMapping("/{id}/{schoolId}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<getCustomeOffer2DTO> getCompanyOffer(@PathVariable int id, @PathVariable int schoolId) {
        if (repo.checkIfExist(id)) {
            CustomCompanyModelWithViewAndDescRes2 model = repo.getCompanyOfferWithDescAndSchoolId(id, schoolId);

            return ResponseEntity.status(HttpStatus.OK).body(new getCustomeOffer2DTO("200", model));

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new getCustomeOffer2DTO("400", null));
        }

    }


    @GetMapping("/{id}/company")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<getCustomeCompanyOffer> getSingleCompanyOffer(@PathVariable int id) {
        List<CustomeCompanyOfferModel2> offers = repo.getCompanyOffers(id);
        if (offers != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new getCustomeCompanyOffer("200", offers));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new getCustomeCompanyOffer("400", offers));
        }

    }


    @GetMapping("/{id}/company/page/{page}/{pageSize}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<CustomCompanyOfferModel2Res> getSingleCompanyOfferWithPagination(@PathVariable int page, @PathVariable int pageSize, @PathVariable int id) {
        return  ResponseEntity.status(HttpStatus.OK).body(repo.getCompanyOffersPagination(page, pageSize, id));
    }


    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<List<CustomeCompanyOfferModel2>> getSingleCompanyOffers(@PathVariable int id) {
        List<CustomeCompanyOfferModel2> offers = repo.getCompanyOffers(id);
        return ResponseEntity.status(HttpStatus.OK).body(offers);
    }


    /**
     * update offer of company
     *
     * @param model
     * @return 1 if success or 0 if failed
     */
//    @PutMapping("/")
//    public ResponseEntity<JsonNode> updateCompanyOfferWithImage(@Valid @RequestBody CustomCompanyOfferModel model, Errors errors) {
//
//        if (errors.hasErrors()) {
//            ObjectNode objectNode = mapper.createObjectNode();
//            objectNode.put("state", 400);
//            objectNode.put("message", "Validation Failed");
//            //objectNode.put("details", errors.getAllErrors().toString());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
//        }
//        if (repo.checkIfExist(model.getOffer_id())) {
//            int res = repo.updateCompanyOfferWithImages(model.getOffer_id(), model.getImage_one(), model.getImage_two(), model.getImage_third(), model.getImage_four(),
//                    model.getOffer_title(), model.getOffer_explaination(), model.getOffer_cost(), model.getOffer_display_date(),
//                    model.getOffer_expired_date(), model.getOffer_deliver_date(), model.getCompany_id(), model.getOffer_count(), model.getCity(), model.getArea(), model.getLng(), model.getLat());
//
//            if (res == 1) {
//                ObjectNode objectNode = mapper.createObjectNode();
//                objectNode.put("status", 200);
//                objectNode.put("offer_id", model.getOffer_id());
//                objectNode.put("image_one", model.getImage_one());
//                objectNode.put("image_two", model.getImage_two());
//                objectNode.put("image_three", model.getImage_third());
//                objectNode.put("image_four", model.getImage_four());
//                objectNode.put("offer_title", model.getOffer_title());
//                objectNode.put("offer_explaination", model.getOffer_explaination());
//                objectNode.put("offer_cost", model.getOffer_cost());
//                objectNode.put("offer_display_date", model.getOffer_display_date());
//                objectNode.put("offer_expired_date", model.getOffer_expired_date());
//                objectNode.put("offer_deliver_date", model.getOffer_deliver_date());
//                objectNode.put("company_id", model.getCompany_id());
//                objectNode.put("offer_count", model.getOffer_count());
//                return ResponseEntity.status(HttpStatus.OK).body(objectNode);
//            } else {
//                ObjectNode objectNode = mapper.createObjectNode();
//                objectNode.put("status", 400);
//                objectNode.put("message", "not success");
//
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
//            }
//        } else {
//            ObjectNode objectNode = mapper.createObjectNode();
//            objectNode.put("status", 400);
//            objectNode.put("message", "not exist");
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
//        }
//
//    }
//

    /**
     * delete offer by offer id
     *
     * @param id
     * @return 1 if success or 0 if failed
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> deleteCompanyOffer(@PathVariable int id) {

        if (repo.checkIfExist(id)) {
            int res = repo.deleteCompanyOffer(id);
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

    @GetMapping("/history/{companyId}/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<CompanyHistoryDto> getCompanyHistory(@PathVariable int companyId, @PathVariable int flag_ar) {
        return repo.getCompanyHistory(companyId, flag_ar);
    }

    @GetMapping("/history2/{companyId}/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<CompanyHistoryDto2> getCompanyHistory2(@PathVariable int companyId, @PathVariable int flag_ar) {
        return repo.getCompanyHistory2(companyId, flag_ar);
    }


    @GetMapping("/{id}/count")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public int getCompanyOfferCount(@PathVariable int id) {
        return repo.getCompanyOfferCount(id);
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
