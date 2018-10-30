package com.taj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.model.TakatafSchoolSeeTenderModel;
import com.taj.repository.TakatafSchoolSeeTenderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by User on 7/8/2018.
 */
@RequestMapping("/service/evvaz/s/tender/seen")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TakatafSchoolSeeTenderController {

    @Autowired
    TakatafSchoolSeeTenderRepo repo;

    @Autowired
    ObjectMapper mapper;

    //@PreAuthorize("hasAuthority('school') or hasAuthority('company') or hasAuthority('admin')")
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ObjectNode addTenderSeen(@Valid @RequestBody TakatafSchoolSeeTenderModel model, Errors errors){
        ObjectNode objectNode = mapper.createObjectNode();
        if (errors.hasErrors()) {
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            objectNode.put("details", errors.getAllErrors().toString());
            return objectNode;
        }
        if (repo.isExistOrganizationAndOffer(model.getSeen_school_id())) {
            if (repo.isExistTender(model.getSeen_tender_id())) {
                if (!repo.checkIfExist(model.getSeen_tender_id(), model.getSeen_school_id())) {
                    int res = repo.addSeen(model.getSeen_tender_id(), model.getSeen_school_id());

                    if (res == 1) {
                        //objectNode.put("seen_id", model.getSeen_id());
                        objectNode.put("state", 200);
                        objectNode.put("seen_school_id", model.getSeen_school_id());
                        objectNode.put("seen_tender_id", model.getSeen_tender_id());

                        return objectNode;
                    } else {
                        objectNode.put("state", 400);
                        objectNode.put("message", "not success");

                        return objectNode;
                    }
                } else {
                    objectNode.put("state", 400);
                    objectNode.put("message", "Already Exist");

                    return objectNode;
                }
            }else {
                objectNode.put("state", 400);
                objectNode.put("message", "tender id not exist");
                return objectNode;
            }
        }else {
            objectNode.put("state", 400);
            objectNode.put("message", "org id not exist");
            return objectNode;
        }

    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<TakatafSchoolSeeTenderModel> getTendersSeen(){
        return repo.getTendersSeen();
    }

    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    @GetMapping("/get/{id}")
    public TakatafSchoolSeeTenderModel getTendersSeen(@PathVariable int id){
        return repo.getTenderSeen(id);
    }

    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    @GetMapping("/getSchool/{schoolId}")
    public List<TakatafSchoolSeeTenderModel> getTendersSeenBySchool(@PathVariable int schoolId){
        return repo.getTendersSeenBySchool(schoolId);
    }

    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    @GetMapping("/getTender/{tenderId}")
    public List<TakatafSchoolSeeTenderModel> getTendersSeenByTender(@PathVariable int tenderId){
        return repo.getTendersSeenByTender(tenderId);
    }

    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    @PutMapping("/update")
    public ObjectNode updateTendersSeen(@Valid @RequestBody TakatafSchoolSeeTenderModel model, Errors errors){
        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            objectNode.put("details", errors.getAllErrors().toString());
            return objectNode;
        }
        int res =  repo.updateTendersSeen(model.getSeen_id(), model.getSeen_tender_id(), model.getSeen_school_id());

        if (res == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("seen_id", model.getSeen_id());
            objectNode.put("seen_school_id", model.getSeen_school_id());
            objectNode.put("seen_tender_id", model.getSeen_tender_id());
            return objectNode;
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success");

            return objectNode;
        }
    }

    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    @PutMapping("/delete/{id}")
    public ObjectNode deleteTendersSeen(@PathVariable int id){
        int res = repo.deleteTendersSeen(id);
        if (res == 1){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "success");

            return objectNode;
        }else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success");

            return objectNode;
        }
    }
}
