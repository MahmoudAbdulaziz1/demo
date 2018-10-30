package com.taj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.model.RequestEnquiriesModel;
import com.taj.repository.RequestEnquiriesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by User on 7/3/2018.
 */
@RequestMapping("/service/evvaz/s/enquiry")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class RequestEnquiriesController {

    @Autowired
    RequestEnquiriesRepo repo;
    @Autowired
    ObjectMapper mapper;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ObjectNode addRequestEnquiry(@Valid @RequestBody RequestEnquiriesModel model, Errors errors) {

        if (errors.hasErrors()){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            objectNode.put("details", errors.getAllErrors().toString());
            return objectNode;
        }
        int res = repo.addNewEnquiry(model.getSchool_request_id(), model.getEnquiry_message());

        if (res == 1){
            ObjectNode objectNode = mapper.createObjectNode();
            //objectNode.put("enquiry_id", model.getEnquiry_id());
            objectNode.put("school_request_id", model.getSchool_request_id());
            objectNode.put("enquiry_message", model.getEnquiry_message());

            return objectNode;
        }else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success");

            return objectNode;
        }
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<RequestEnquiriesModel> getRequestEnquiry() {
        return repo.getNewEnquiries();
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public RequestEnquiriesModel getNewEnquiry(@PathVariable int id) {
        return repo.getNewEnquiry(id);
    }

    @GetMapping("/getReq/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<RequestEnquiriesModel> getNewEnquiries(@PathVariable int id) {
        return repo.getNewEnquiries(id);
    }

    /**
     * @param model update current category
     */

    @PutMapping("/updateEnquiry")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ObjectNode updateCategory(@Valid @RequestBody RequestEnquiriesModel model, Errors errors) {

        if (errors.hasErrors()){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            objectNode.put("details", errors.getAllErrors().toString());
            return objectNode;
        }
        int res = repo.updateNewEnquiry(model.getEnquiry_id(), model.getSchool_request_id(), model.getEnquiry_message());


            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("enquiry_id", model.getEnquiry_id());
            objectNode.put("school_request_id", model.getSchool_request_id());
            objectNode.put("enquiry_message", model.getEnquiry_message());

            return objectNode;

    }

    /**
     * @param model delete current category
     */

    @PutMapping("/deleteEnquiry")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ObjectNode deleteCategory(@Valid @RequestBody RequestEnquiriesModel model, Errors errors) {
        if (errors.hasErrors()){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            objectNode.put("details", errors.getAllErrors().toString());
            return objectNode;
        }
        int res = repo.deleteNewEnquiry(model.getEnquiry_id());


            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("enquiry_id", model.getEnquiry_id());
            objectNode.put("school_request_id", model.getSchool_request_id());
            objectNode.put("enquiry_message", model.getEnquiry_message());

            return objectNode;


    }
}
