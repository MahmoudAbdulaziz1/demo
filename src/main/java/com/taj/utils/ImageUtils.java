package com.taj.utils;

import com.taj.controller.test_image.StorageService;
import com.taj.controller.test_image.UploadController;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by User on 10/23/2018.
 */
public class ImageUtils {

    @Autowired
    StorageService storageService;

    public String getUrl(MultipartFile file, Path rootLocation) {
        String message = "";
        try {

            String currentDate = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());


            String s = file.getOriginalFilename().replace("\\s+", "");
            MultipartFile multipartFile = new MockMultipartFile("file",
                    file.getOriginalFilename().replace(file.getOriginalFilename(),
                            FilenameUtils.getBaseName(s.replaceAll("\\s+", "")).replaceAll("\\s+", "")
                                    .concat(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())) + "." +
                                    FilenameUtils.getExtension(file.getOriginalFilename())).toLowerCase().replaceAll("\\s+", ""), "image/*", file.getInputStream());
            System.out.println(MvcUriComponentsBuilder
                    .fromMethodName(UploadController.class, "getFile", multipartFile.getOriginalFilename()).build().toString());
            storageService.store(multipartFile);
//            repo.insertData(MvcUriComponentsBuilder
//                    .fromMethodName(UploadController.class, "getFile", multipartFile.getOriginalFilename()).build().toString(), m.getName(), m.getSecond());
            System.out.println(file.getName() + "     " + file.getOriginalFilename());
            //files.add(f.replace("\\s+", ""));

            message = "You successfully uploaded " + multipartFile.getOriginalFilename() + "!   " +
                    MvcUriComponentsBuilder
                            .fromMethodName(ImageUtils.class, "getFile", multipartFile.getOriginalFilename()).build().toString();
            return message;
        } catch (Exception e) {
            message = "FAIL to upload " + file.getOriginalFilename() + "! " + e.getMessage();
            return message;
        }
    }

}
