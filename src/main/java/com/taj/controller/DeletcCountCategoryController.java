package com.taj.controller;

import com.taj.repository.CategoryCountDemoREpo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by User on 9/2/2018.
 */
@RequestMapping("/service/evvaz/s/del")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class DeletcCountCategoryController {
    @Autowired
    CategoryCountDemoREpo repo;
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public  int delete(@PathVariable int id){
        return  repo.deleteCat(id);
    }
}
