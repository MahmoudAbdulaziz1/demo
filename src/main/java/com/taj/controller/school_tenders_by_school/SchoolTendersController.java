package com.taj.controller.school_tenders_by_school;

import com.taj.entity.school_tenders_by_school.SchoolTenderEntity;
import com.taj.service.school_tenders_by_school.SchoolTenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by User on 10/16/2018.
 */
@RestController
@CrossOrigin
@RequestMapping("/service/evvaz/s/tester")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SchoolTendersController {
    @Autowired
    SchoolTenderService service;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<SchoolTenderEntity> getAll(){
        return service.getAll();
    }

}
