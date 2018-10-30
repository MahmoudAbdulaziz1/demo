package com.taj.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.model.CompanyResponseSchoolRequestModel;
import com.taj.repository.CompanyResponseSchoolRequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by User on 7/8/2018.
 */
@RequestMapping("/service/evvaz/s/response/school/request")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CompanyResponseSchoolRequestController {

    @Autowired
    CompanyResponseSchoolRequestRepo repo;
    @Autowired
    ObjectMapper mapper;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ObjectNode addCompanyResponseSchoolRequest(@Valid @RequestBody CompanyResponseSchoolRequestModel model, Errors errors) {
        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            objectNode.put("details", errors.getAllErrors().toString());
            return objectNode;
        }
        int res = repo.addResponseSchoolRequest(model.getResponsed_company_id(), model.getResponsed_request_id(), model.getResponsed_from(),
                model.getResponsed_to(), model.getResponsed_cost(), model.getIs_aproved(), model.getResponse_desc());

        if (res == 200) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            ObjectNode objectNode = mapper.createObjectNode();
            //objectNode.put("response_id", model.getResponse_id());
            objectNode.put("responsed_company_id", model.getResponsed_company_id());
            objectNode.put("responsed_request_id", model.getResponsed_request_id());
            objectNode.put("responsed_from", model.getResponsed_from());
            objectNode.put("responsed_from", model.getResponsed_to());
            objectNode.put("responsed_cost", model.getResponsed_cost());
            objectNode.put("is_aproved", model.getIs_aproved());
            objectNode.put("response_date", timestamp.getTime());
            objectNode.put("response_desc", model.getResponse_desc());
            return objectNode;
        } else if (res == 100) {
            ObjectNode objectNode = mapper.createObjectNode();
            //objectNode.put("response_id", model.getResponse_id());
            objectNode.put("responsed_company_id", model.getResponsed_company_id());
            objectNode.put("responsed_request_id", model.getResponsed_request_id());
            objectNode.put("responsed_from", model.getResponsed_from());
            objectNode.put("responsed_from", model.getResponsed_to());
            objectNode.put("responsed_cost", model.getResponsed_cost());
            objectNode.put("is_aproved", model.getIs_aproved());
            objectNode.put("response_desc", model.getResponse_desc());
            objectNode.put("updated", 1);
            return objectNode;
        } else if (res == -100) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("message", "cant not updated");
            objectNode.put("updated", 0);
            return objectNode;
        }else if (res == -999){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("message", "cant not updated school accept your response");
            objectNode.put("updated", 0);
            return objectNode;
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("message", "fail");
            return objectNode;
        }

    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<CompanyResponseSchoolRequestModel> getCompanyResponseSchoolRequest() {
        return repo.getResponseSchoolRequest();
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public CompanyResponseSchoolRequestModel getCompanyResponseSchoolRequest(@PathVariable int id) {
        return repo.getResponseSchoolRequest(id);
    }

    @GetMapping("/getCompany/{companyId}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<CompanyResponseSchoolRequestModel> getCompanyResponseSchoolRequestByCompany(@PathVariable int companyId) {
        return repo.getResponseSchoolRequestByCompany(companyId);
    }

    @GetMapping("/getRequest/{requestId}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<CompanyResponseSchoolRequestModel> CompanyResponseSchoolRequestByRequest(@PathVariable int requestId) {
        return repo.getResponseSchoolRequestByRequest(requestId);
    }

    @GetMapping("/approval/{accept}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<CompanyResponseSchoolRequestModel> CompanyResponseSchoolRequestByAprove(@PathVariable int accept) {
        return repo.getResponseSchoolRequestByAccept(accept);
    }

    @PutMapping("/accept/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public JsonNode acceptCompanyResponseSchoolRequest(@PathVariable int id) {

        int res = repo.acceptResponseSchoolRequest(id);
        if (res == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 200);
            objectNode.put("message", "accepted");
            return objectNode;
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "failed");
            return objectNode;

        }
    }

    @PutMapping("/refuse/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public JsonNode refuseCompanyResponseSchoolRequest(@PathVariable int id) {
        int res = repo.refuseResponseSchoolRequest(id);
        if (res == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 200);
            objectNode.put("message", "accepted");
            return objectNode;
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "failed");
            return objectNode;

        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ObjectNode updateCompanyResponseSchoolRequest(@Valid @RequestBody CompanyResponseSchoolRequestModel model, Errors errors) {
        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            objectNode.put("details", errors.getAllErrors().toString());
            return objectNode;
        }
        int res = repo.updateResponseSchoolRequest(model.getResponse_id(), model.getResponsed_company_id(), model.getResponsed_request_id(),
                model.getResponsed_from(), model.getResponsed_to(), model.getResponsed_cost(), model.getIs_aproved(), model.getResponse_desc());

        if (res == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("response_id", model.getResponse_id());
            objectNode.put("responsed_company_id", model.getResponsed_company_id());
            objectNode.put("responsed_request_id", model.getResponsed_request_id());
            objectNode.put("responsed_from", model.getResponsed_from());
            objectNode.put("responsed_from", model.getResponsed_to());
            objectNode.put("responsed_cost", model.getResponsed_cost());
            objectNode.put("is_aproved", model.getIs_aproved());
            objectNode.put("response_desc", model.getResponse_desc());

            return objectNode;
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success");

            return objectNode;
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ObjectNode deleteCompanyResponseSchoolRequest(@PathVariable int id) {
        int res = repo.deleteResponseSchoolRequest(id);

        if (res == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "success");

            return objectNode;
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success");

            return objectNode;
        }
    }
}
