package com.taj.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.controller.test_image.StorageService;
import com.taj.controller.test_image.UploadController;
import com.taj.model.*;
import com.taj.model.Pagination.SchoolRequestNewDto2ModelPagination;
import com.taj.model.new_school_request.*;
import com.taj.model.school.request.image.SchoolNewRequestsDTO2;
import com.taj.model.school_request_image_web.SchoolRequestWithImageByIdDto;
import com.taj.model.school_request_image_web.SchoolRequestWithImageByIdDto2;
import com.taj.repository.AddSchoolRequestImagesRepo;
import com.taj.repository.SchoolRequestNewRepo;
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
 * Created by User on 8/15/2018.
 */
@RequestMapping("/service/evvaz/s/school/tenders")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SchoolRequestNewController {

    private final Path rootLocation = Paths.get("upload-dir/company-offer");
    @Autowired
    SchoolRequestNewRepo repo;
    @Autowired
    AddSchoolRequestImagesRepo addSchoolRequestImagesRepo;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    StorageService storageService;
    List<String> files = new ArrayList<String>();


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


    @PostMapping("/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<JsonNode> addSchoolRequest(@RequestParam("image") MultipartFile image, @RequestPart @Valid String schoolNewRequestsDTO2String, Errors errors) {

        try {
            SchoolNewRequestsDTO2 model = new ObjectMapper().readValue(schoolNewRequestsDTO2String, SchoolNewRequestsDTO2.class);
            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }


            String logoUrl = imageUrl(image, rootLocation);


            int res = repo.addRequest(model.getRequest_title(), model.getRequest_explaination(),
                    model.getRequest_display_date(), model.getRequest_expired_date(), model.getSchool_id(),
                    model.getRequest_category_name(), logoUrl, model.getRequest_count(), model.getFlag_ar());
            if (res == 1) {
                ObjectNode objectNode = mapper.createObjectNode();

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
        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "not success  " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }

//    @GetMapping("/{id}")
//    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
//    public ResponseEntity<SchoolRequestNewDtoById> getSchoolSingleRequest(@PathVariable int id) {
//        if (repo.isExist(id)) {
//            return ResponseEntity.status(HttpStatus.OK).body(repo.getRequestByID(id));
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//        }
//
//    }

    @GetMapping("/{id}/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<SchoolRequestWithImageByIdDto> getSchoolSingleRequestByImage(@PathVariable int id, @PathVariable int flag_ar) {
        if (repo.isExist(id)) {
            return ResponseEntity.status(HttpStatus.OK).body(repo.getRequestByIDWithImage(id, flag_ar));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

    @GetMapping("/{id}/{companyId}/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<SchoolRequestWithImageByIdDto2> getSchoolSingleRequestByImageAndCompanyRequest(@PathVariable int id, @PathVariable int companyId,
                                                                                                         @PathVariable int flag_ar) {
        if (repo.isExist(id)) {
            return ResponseEntity.status(HttpStatus.OK).body(repo.getRequestByIDWithImageAndCompanyRequest(id, companyId, flag_ar));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

    @GetMapping("/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<SchoolRequestNewDtoModel> getSchoolSingleRequest(@PathVariable  int flag_ar) {
        return repo.getRequestsAll(flag_ar);
    }

    @GetMapping("/mine/{requestId}/{schoolId}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<Boolean> isMine(@PathVariable int requestId, @PathVariable int schoolId) {
        if (repo.isExist(requestId)) {
            if (repo.isExistOrganizationAndOffer(schoolId)) {
                boolean res = repo.isExistMine(requestId, schoolId);
                if (res) {
                    return ResponseEntity.status(HttpStatus.OK).body(true);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @GetMapping("/school/{id}/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<SchoolRequestNewDto2Model> getSchoolRequestsBySchool(@PathVariable int id,@PathVariable int flag_ar) {
        return repo.getRequestsBySchoolID(id, flag_ar);
    }

    @GetMapping("/school/{id}/page/{page}/{pageSize}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public SchoolRequestNewDto2ModelPagination getSchoolRequestsBySchoolPagination(@PathVariable int id, @PathVariable int page, @PathVariable int pageSize,
                                                                                   @PathVariable int flag_ar) {
        return repo.getRequestsBySchoolIDPagination(id, page, pageSize,  flag_ar);

    }


    @PostMapping("/school/{id}/page/{page}/{pageSize}/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public SchoolRequestNewDto2ModelPagination getSchoolRequestsBySchoolPagination(@PathVariable int id, @PathVariable int page,
                                                                                   @PathVariable int pageSize, @PathVariable int flag_ar,@RequestBody FilterModel filter) {

        if (filter.getDay_left() == 0) {

            if (filter.getName().equals(null) || filter.getName().isEmpty()) {
                return repo.getRequestsBySchoolIDPagination(id, page, pageSize, flag_ar);
            } else {
                return repo.getRequestsBySchoolIDPaginationByName(id, page, pageSize, filter.getName(), flag_ar);
            }

        } else if (filter.getDay_left() == 1) {

            if (filter.getName().equals(null) || filter.getName().isEmpty()) {
                return repo.getRequestsBySchoolIDPaginationByMore(id, page, pageSize, flag_ar);

            } else {
                return repo.getRequestsBySchoolIDPaginationByBothMore(id, page, pageSize, filter.getName(), flag_ar);
            }

        } else if (filter.getDay_left() == 2) {

            if (filter.getName().equals(null) || filter.getName().isEmpty()) {
                return repo.getRequestsBySchoolIDPaginationByLess(id, page, pageSize, flag_ar);
            } else {
                return repo.getRequestsBySchoolIDPaginationByBothLess(id, page, pageSize, filter.getName(), flag_ar);
            }

        } else {
            return null;
        }
    }


    @GetMapping("/request/school/{id}/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public GetCollectiveTenders2Model getRequestOfSchoolByID(@PathVariable int id, @PathVariable int flag_ar) {
        List<getSchoolCustomNewRequestByIdModel> obj = repo.getRequestOfSchoolByID(id, flag_ar);
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

    @GetMapping("/cat/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<SchoolRequestNewDto> getSchoolRequestsByCategory(@PathVariable String id) {
        return repo.getRequestsByCategoryID(id);
    }

    @PutMapping("/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public JsonNode updateRequestModel(@Valid @RequestBody SchoolRequestsDTOAr model, Errors errors) {
        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");

            return objectNode;
        }
        int res = repo.updateRequest(model.getRequest_id(), model.getRequest_title(), model.getRequest_explaination(),
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
            objectNode.put("flag_Ar", model.getFlag_ar());

            return objectNode;
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "not success");

            return objectNode;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ObjectNode deleteSchoolRequest(@PathVariable int id) {
        if (repo.isExist(id)) {
            int res = repo.deleteSchoolRequest(id);
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

    @GetMapping("/response/{request_id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<RequstResponsePOJO> getSingleTenderDetails(@PathVariable int request_id) {
        if (repo.isExist(request_id)) {
            if (repo.getSingleTenderDetails(request_id) != null) {
                RequstResponsePOJO requstResponsePOJO = new RequstResponsePOJO(200, repo.getSingleTenderDetails(request_id));
                return ResponseEntity.status(HttpStatus.OK).body(requstResponsePOJO);
            } else {
                RequstResponsePOJO requstResponsePOJO = new RequstResponsePOJO(400, repo.getSingleTenderDetails(request_id));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(requstResponsePOJO);
            }
        } else {

            RequstResponsePOJO requstResponsePOJO = new RequstResponsePOJO(400, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(requstResponsePOJO);

        }


    }

    @PutMapping("/accept/{request_id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> acceptOffer(@PathVariable int request_id) {
        if (repo.isExist(request_id)) {
            int response = repo.acceptOffer(request_id);
            if (response == 1) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("message", "success");
                return ResponseEntity.status(HttpStatus.OK).body(objectNode);
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("message", "not sucess");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("message", "no exist request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

    }

    @PutMapping("/cancel/{request_id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> cancelOffer(@PathVariable int request_id) {
        if (repo.isExist(request_id)) {
            int response = repo.cancelOffer(request_id);
            if (response == 1) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("message", "success");
                return ResponseEntity.status(HttpStatus.OK).body(objectNode);
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("message", "not sucess");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("message", "no exist request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
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
