package com.taj.controller;

import com.taj.model.*;
import com.taj.model.school_history_admin_dashboard.SchoolOrdersHistoryModel;
import com.taj.model.school_history_admin_dashboard.SchoolOrdersModel;
import com.taj.model.school_history_admin_dashboard.SchoolShipModel;
import com.taj.model.school_history_admin_dashboard.SingleSchoolOrdersModel;
import com.taj.repository.AdminOrdersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by User on 9/3/2018.
 */
@RequestMapping("/service/evvaz/s/admin/orders")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AdminOrdersController {

    @Autowired
    AdminOrdersRepo repo;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<AdminOrdersModel> getAllOrders() {
        return repo.getAllOrders();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public AdminSingleOrderModel getOrder(@PathVariable int id) {
        return repo.getOrder(id);
    }

    @GetMapping("/history/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<AdminHistoryOrdersModel> getAllHistoryOrders() {
        return repo.getAllHistoryOrders();
    }

    @GetMapping("/history/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public AdminSingleOrderHistoryModel getHistoryOrder(@PathVariable int id) {
        return repo.getHistoryOrder(id);
    }

    @PostMapping("/ship")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public int addShipping(@RequestBody ShippingDTO dto) {
        return repo.addShipping(dto.getShip(), dto.getShip_company_offer_id());
    }

    @GetMapping("/school/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<SchoolOrdersModel> getAllSchoolOrders() {
        return repo.getAllSchoolOrders();
    }

    @GetMapping("/school/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public SingleSchoolOrdersModel getSchoolOrder(@PathVariable int id) {
        return repo.getSchoolOrder(id);
    }

    @GetMapping("/school/history/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<SchoolOrdersHistoryModel> getAllSchoolOrdersHistory() {
        return repo.getAllSchoolOrdersHistory();
    }
    @GetMapping("/school/history/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public SingleSchoolOrdersModel getSchoolOrderHistory(@PathVariable int id) {
        return repo.getSchoolOrderHistory(id);
    }

    @PostMapping("/school/ship")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public int addSchoolShipping(@RequestBody SchoolShipModel dto) {
        return repo.addShipping(dto.getShip(), dto.getShip_school_request_id());
    }
}
