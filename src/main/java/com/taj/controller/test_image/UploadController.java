package com.taj.controller.test_image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taj.model.test_image.TestImage2;
import com.taj.model.test_image.TestImageModel;
import com.taj.repository.CustomCompanyOfferRepo;
import com.taj.repository.test_image.TestImageREpo;
import com.taj.utils.ImageUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class UploadController {

    private final Path rootLocation = Paths.get("upload-dir");
    @Autowired
    StorageService storageService;
    @Autowired
    TestImageREpo repo;
    List<String> files = new ArrayList<String>();

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestPart String name) {
        String message = "";
        try {
            TestImageModel m = new ObjectMapper().readValue(name, TestImageModel.class);
            System.out.println("name : "+m.getName() + "    second:  "+ m.getSecond());
            String currentDate = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());

            String f = file.getOriginalFilename().replace(file.getOriginalFilename(),
                    FilenameUtils.getBaseName(file.getOriginalFilename())
                            .concat(currentDate) + "." + FilenameUtils.getExtension(file.getOriginalFilename())).toLowerCase();


            String s = file.getOriginalFilename().replace("\\s+", "");
            MultipartFile multipartFile = new MockMultipartFile("file",
                    file.getOriginalFilename().replace(file.getOriginalFilename(),
                            FilenameUtils.getBaseName(s.replaceAll("\\s+", "")).replaceAll("\\s+", "")
                                    .concat(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())) + "." +
                                    FilenameUtils.getExtension(file.getOriginalFilename())).toLowerCase().replaceAll("\\s+", ""), "image/*", file.getInputStream());
            System.out.println(MvcUriComponentsBuilder
                    .fromMethodName(UploadController.class, "getFile", multipartFile.getOriginalFilename()).build().toString());
            storageService.store(multipartFile);
            repo.insertData(MvcUriComponentsBuilder
                    .fromMethodName(UploadController.class, "getFile", multipartFile.getOriginalFilename()).build().toString(), m.getName(), m.getSecond());
            System.out.println(file.getName() + "     " + file.getOriginalFilename());
            files.add(f.replace("\\s+", ""));

            message = "You successfully uploaded " + multipartFile.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "FAIL to upload " + file.getOriginalFilename() + "! "+ e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

    @PostMapping("/post2")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        return new ImageUtils().getUrl(file, rootLocation);
    }


    @PostMapping("/posts")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<String> handleFileUpload2(@RequestBody TestImage2 model) {
        int res = repo.insertData2(model.getUrl(), model.getName());
        if (res == 1){
            return ResponseEntity.status(HttpStatus.OK).body("Inserted");
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed");
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






    @Autowired
    CustomCompanyOfferRepo customCompanyOfferRepo;
    @Autowired
    ObjectMapper mapper;

//
//    @PostMapping("/company/offer/images/")
//    public ResponseEntity<ObjectNode> addCompanyOffersWitImage(@RequestParam("image_one") MultipartFile image1, @RequestParam("image_two") MultipartFile image12,
//                                                               @RequestParam("image_three") MultipartFile image13, @RequestParam("image_four") MultipartFile image4,
//                                                               @RequestPart @Valid String offerModelString, Errors errors) {
//
//        try {
//            CustomCompanyOfferModelWithImage model = new ObjectMapper().readValue(offerModelString, CustomCompanyOfferModelWithImage.class);
//
//            if (errors.hasErrors()) {
//                ObjectNode objectNode = mapper.createObjectNode();
//                objectNode.put("state", 400);
//                objectNode.put("message", "Validation Failed");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
//            } else {
//                if (model.getOffer_display_date() < new Timestamp(System.currentTimeMillis()).getTime()
//                        || model.getOffer_expired_date() < new Timestamp(System.currentTimeMillis()).getTime()) {
//                    ObjectNode objectNode = mapper.createObjectNode();
//                    objectNode.put("state", 400);
//                    objectNode.put("message", "Validation Failed offer date in past");
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
//                }
//
//                if (model.getOffer_display_date() >= model.getOffer_expired_date()) {
//                    ObjectNode objectNode = mapper.createObjectNode();
//                    objectNode.put("state", 400);
//                    objectNode.put("message", "Validation Failed offer display date is greater than  expired date");
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
//                }
//
//
//
//                String s = image1.getOriginalFilename().replace("\\s+", "");
//                MultipartFile multipartFile = new MockMultipartFile("file",
//                        image1.getOriginalFilename().replace(image1.getOriginalFilename(),
//                                FilenameUtils.getBaseName(s.replaceAll("\\s+", "")).replaceAll("\\s+", "")
//                                        .concat(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())) + "." +
//                                        FilenameUtils.getExtension(image1.getOriginalFilename())).toLowerCase().replaceAll("\\s+", ""), "image/*", image1.getInputStream());
//                System.out.println(MvcUriComponentsBuilder
//                        .fromMethodName(UploadController.class, "getFile", multipartFile.getOriginalFilename()).build().toString());
//                try {
//                    if (Files.isDirectory(rootLocation)) {
//
//                    } else {
//                        Files.createDirectory(rootLocation);
//                    }
//                } catch (IOException e) {
//                    throw new RuntimeException("Could not initialize storage!");
//                }
//                storageService.store(multipartFile);
//                String image = MvcUriComponentsBuilder
//                        .fromMethodName(UploadController.class, "getFile", multipartFile.getOriginalFilename()).build().toString();
////                System.out.println(file.getName() + "     " + file.getOriginalFilename());
////                files.add(f.replace("\\s+", ""));
//
//
//
//
//
//
//                int res = customCompanyOfferRepo.addOfferEdeitedWithImage(image, "", "", "", model.getOffer_title(), model.getOffer_explaination(),
//                        model.getOffer_cost(), model.getOffer_display_date(), model.getOffer_expired_date(), model.getOffer_deliver_date(),
//                        model.getCompany_id(), model.getOffer_count(), model.getCity(), model.getArea(), model.getLng(), model.getLat());
//                if (res == 1) {
//                    ObjectNode objectNode = mapper.createObjectNode();
//                    objectNode.put("status", 200);
//                    //objectNode.put("offer_images_id", model.getOffer_images_id());
//                    objectNode.put("offer_title", model.getOffer_title());
//                    objectNode.put("offer_explaination", model.getOffer_explaination());
//                    objectNode.put("offer_cost", model.getOffer_cost());
//                    objectNode.put("offer_display_date", model.getOffer_display_date().toString());
//                    objectNode.put("offer_expired_date", model.getOffer_expired_date().toString());
//                    objectNode.put("offer_deliver_date", model.getOffer_deliver_date().toString());
//                    objectNode.put("company_id", model.getCompany_id());
//                    objectNode.put("offer_count", model.getOffer_count());
//                    return ResponseEntity.status(HttpStatus.OK).body(objectNode);
//                } else {
//                    ObjectNode objectNode = mapper.createObjectNode();
//                    objectNode.put("status", 400);
//                    objectNode.put("message", "not success");
//
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
//                }
//            }
//        } catch (Exception e) {
//            ObjectNode objectNode = mapper.createObjectNode();
//            objectNode.put("status", 400);
//            objectNode.put("message", "not success exception \n"+ e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
//        }
//
//    }











}