package com.taj.controller;

import com.taj.model.CollectiveTenderCompaniesRequestForCompanyModel;
import com.taj.repository.CollectiveTenderCompaniesRequestForCompanyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by User on 9/3/2018.
 */
@RequestMapping("/service/evvaz/s/tender/companies")
@CrossOrigin
@RestController
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CollectiveTenderCompaniesRequestForCompanyController {

    @Autowired
    CollectiveTenderCompaniesRequestForCompanyRepo repo;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<CollectiveTenderCompaniesRequestForCompanyModel> getTenderCompanies(@PathVariable int id){
        return repo.getTenderCompanies(id);
    }

    @PutMapping("/{company_id}/{request_id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public int approveRequest(@PathVariable int company_id,@PathVariable int request_id){
        return repo.approveRequest(company_id, request_id);
    }
}
