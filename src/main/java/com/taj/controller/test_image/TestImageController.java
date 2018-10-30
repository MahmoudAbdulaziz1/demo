package com.taj.controller.test_image;

import com.taj.repository.test_image.TestImageREpo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by MahmoudAhmed on 10/14/2018.
 */
@RequestMapping("/service/evvaz/s/test/image")
@RestController
@CrossOrigin
public class TestImageController {

    @Autowired
    TestImageREpo repo;
//    @PostMapping("/")
//    public int insert(@RequestBody TestImageModel model){
//        return repo.insertData( model.getImage_url(), model.getName());
//    }

}
