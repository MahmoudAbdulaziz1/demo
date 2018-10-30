package com.taj.controller;

import com.taj.model.CompanyBackOrderModel;
import com.taj.repository.OrdersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by User on 8/6/2018.
 */
@RequestMapping("/service/evvaz/s/orders")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OredersController {

    @Autowired
    OrdersRepo repo;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<List<CompanyBackOrderModel>> getOrdersForCompany(@PathVariable int id){
        return  repo.getOrdersForCompany(id);
    }


}
