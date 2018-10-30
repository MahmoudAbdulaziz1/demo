package com.taj.controller;

import com.taj.model.CollectiveTenderDetailsForCompanyModel;
import com.taj.repository.CollectiveTenderDetailsForCompanyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by User on 9/3/2018.
 */
@RequestMapping("/service/evvaz/s/tender/details/company")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CollectiveTenderDetailsForCompanyController {


    @Autowired
    CollectiveTenderDetailsForCompanyRepo repo;


    @GetMapping("/{id}/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<CollectiveTenderDetailsForCompanyModel> getTenderDetails(@PathVariable int id, @PathVariable int flag_ar){
        return repo.getTenderDetails(id, flag_ar);
    }
}
