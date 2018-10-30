package com.taj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.controller.test_image.StorageService;
import com.taj.controller.test_image.UploadController;
import com.taj.model.CustomSchoolProfileModel;
import com.taj.model.SchoolProfileModeNoImage;
import com.taj.model.SchoolProfileModel;
import com.taj.repository.SchoolProfileRepo;
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
 * Created by MahmoudAhmed on 6/3/2018.
 */
//@RequestMapping("/evvaz/school/profile")
@RequestMapping("/service/evvaz/s/school/profile")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SchoolProfileController {


    private final Path schoolRootLocation = Paths.get("upload-dir/school-profile");
    @Autowired
    ObjectMapper mapper;
    List<String> files = new ArrayList<String>();
    @Autowired
    StorageService storageService;
    @Autowired
    private SchoolProfileRepo repo;

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
        Resource file = storageService.loadFile(filename, schoolRootLocation);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }


    /**
     * add profile data to db
     *
     * @param model
     * @return 1 if added and 0  if not
     */
    @PostMapping("/addProfile")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    public ResponseEntity<ObjectNode> AddUserProfile(@Valid @RequestBody SchoolProfileModel model, Errors errors) {

        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        if (repo.isExistInLogin(model.getSchool_id(), "school")) {

            if (!repo.isExist(model.getSchool_id())) {
                int res = repo.addSchoolProfile(model.getSchool_id(), model.getSchool_name(), model.getSchool_logo_image(),
                        model.getSchool_address(), model.getSchool_service_desc(), model.getSchool_link_youtube(),
                        model.getSchool_website_url(), model.getSchool_lng(), model.getSchool_lat(), model.getSchool_cover_image(), model.getSchool_phone_number());
                if (res == 1) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("school_name", model.getSchool_name());
                    objectNode.put("school_logo_image", model.getSchool_logo_image());
                    objectNode.put("school_address", model.getSchool_address());
                    objectNode.put("school_service_desc", model.getSchool_service_desc());
                    objectNode.put("school_link_youtube", model.getSchool_link_youtube());
                    objectNode.put("school_website_url", model.getSchool_website_url());
                    objectNode.put("school_lng", model.getSchool_lng());
                    objectNode.put("school_lat", model.getSchool_lat());
                    objectNode.put("school_cover_image", model.getSchool_cover_image());
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


    }

    /**
     * get all school profiles
     *
     * @return list of profiles
     */

    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    @GetMapping("/getProfiles")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<SchoolProfileModel> getProfiles() {
        return repo.getSchoolSProfiles();
    }


    @GetMapping("/logo/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public String getProfilesLogo(@PathVariable int id) {
        return repo.getSchoolSProfilesLogo(id);
    }


    /**
     * get single profile using school id
     *
     * @param id
     * @return school profile by id
     */
    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    @GetMapping("/getProfile/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public SchoolProfileModel getProfile(@PathVariable int id) {
        return repo.getSchoolProfile(id);
    }

    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    @PutMapping("/updateProfile")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> updateProfile(@RequestParam("logo") MultipartFile logo, @RequestParam("cover") MultipartFile cover,
                                                    @Valid @RequestPart String schoolProfileModelString, Errors errors) {

        try {

            SchoolProfileModeNoImage model = new ObjectMapper().readValue(schoolProfileModelString, SchoolProfileModeNoImage.class);

            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);

            }

            String logoUrl = imageUrl(logo, schoolRootLocation);
            String coverUrl = imageUrl(cover, schoolRootLocation);

            int res = repo.updateProfile(model.getSchool_id(), model.getSchool_name(), logoUrl,
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
                objectNode.put("value", "not success");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }


    }

    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    @GetMapping("/profileExist/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public int isExist(@PathVariable int id) {
        return repo.checkSchoolProfile(id);
    }

    //@PreAuthorize("hasAuthority('school') or hasAuthority('admin')")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> deleteSchoolProfile(@PathVariable int id) {
        int res = repo.deleteSchoolProfile(id);
        if (res == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("messsage", "success");
            return ResponseEntity.status(HttpStatus.OK).body(objectNode);
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public CustomSchoolProfileModel getProfileForAdmin(@PathVariable int id) {
        return repo.getSchoolProfileForAdmin(id);
    }


    @PutMapping("/update")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> updateProfileForAdmin(@RequestParam("logo") MultipartFile logo, @RequestParam("cover") MultipartFile cover,
                                                            @Valid @RequestPart String schoolProfileModelString, Errors errors) {

        try {

            SchoolProfileModeNoImage model = new ObjectMapper().readValue(schoolProfileModelString, SchoolProfileModeNoImage.class);

            if (errors.hasErrors()) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("state", 400);
                objectNode.put("message", "Validation Failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);

            }


            String logoUrl = imageUrl(logo, schoolRootLocation);
            String coverUrl = imageUrl(cover, schoolRootLocation);


            int res = repo.updateProfileForAdmin(model.getSchool_id(), model.getSchool_name(), logoUrl,
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
        } catch (Exception e) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success  " + e.getMessage());
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
