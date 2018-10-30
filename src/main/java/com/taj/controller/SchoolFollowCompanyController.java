package com.taj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.model.*;
import com.taj.repository.SchoolFollowCompanyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by User on 7/19/2018.
 */

@RequestMapping("/service/evvaz/s/follow")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SchoolFollowCompanyController {


    @Autowired
    SchoolFollowCompanyRepo repo;
    @Autowired
    ObjectMapper mapper;


    @PostMapping("/add")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> addFollower(@RequestBody @Valid SchoolFollowCompany model, Errors errors) {
        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            //objectNode.put("details", errors.getAllErrors().toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

        if (repo.isExist(model.getFollow_id())) {

            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "id exist");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        } else {
            if (repo.isExistFollwing(model.getOrganization_id()) && repo.isExistFollwer(model.getFollower_id())) {
                int res = repo.addFollower(model.getOrganization_id(), model.getFollower_id());
                if (res == 1) {

                    int id = repo.getId(model.getOrganization_id(), model.getFollower_id());

                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("status", 200);
                    objectNode.put("follow_id", id);
                    objectNode.put("Organization_id", model.getOrganization_id());
                    objectNode.put("Follower_id", model.getFollower_id());

                    return ResponseEntity.status(HttpStatus.OK).body(objectNode);
                } else if (res == 0) {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("status", 400);
                    objectNode.put("message", "not success");

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                } else  {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("status", 400);
                    objectNode.put("message", "is already follow it");

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
                }

            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("status", 400);
                objectNode.put("message", "organization not exist");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }

        }


    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<SchoolFollowCompany> getAllFollowers() {
        return repo.getAllFollowers();
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public SchoolFollowCompany getById(@PathVariable int id) {
        return repo.getById(id);
    }

    //followers
    @GetMapping("/get/following/{schoolId}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<getFollowersList> getAllSchoolFollowing(@PathVariable int schoolId) {


        if (repo.isExistFollwer(schoolId)) {
            List<SchoolFollowCompany> followers = repo.getAllSchoolFollowing(schoolId);

            return ResponseEntity.status(HttpStatus.OK).body(new getFollowersList("200", followers));
        } else {
            List<SchoolFollowCompany> followers = repo.getAllSchoolFollowing(schoolId);

            return ResponseEntity.status(HttpStatus.OK).body(new getFollowersList("400", followers));
        }

    }

    //who's followed
    @GetMapping("/get/followers/{companyId}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<List<SchoolProfileModel>> getCompanyAllFollowers(@PathVariable int companyId) {

        if (repo.isExistFollwer(companyId)) {
            List<SchoolProfileModel> followed = repo.getCompanyAllFollowersNew(companyId);
            return ResponseEntity.status(HttpStatus.OK).body(followed);
        } else {
            List<SchoolProfileModel> followed = repo.getCompanyAllFollowersNew(companyId);
            return ResponseEntity.status(HttpStatus.OK).body(followed);
        }
    }

    //who's followed
    @GetMapping("/school/followers/{schoolId}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<List<CompanyFollowSch0oolDto>> getSchoolAllFollowers(@PathVariable int schoolId) {

        if (repo.isExistFollwer(schoolId)) {
            List<CompanyFollowSch0oolDto> followed = repo.getSchoolAllFollowersNew(schoolId);
            return ResponseEntity.status(HttpStatus.OK).body(followed);
        } else {
            List<CompanyFollowSch0oolDto> followed = repo.getSchoolAllFollowersNew(schoolId);
            return ResponseEntity.status(HttpStatus.OK).body(followed);
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> updateSchoolFollowCompany(@Valid @RequestBody SchoolFollowCompany model, Errors errors) {
        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            //objectNode.put("details", errors.getAllErrors().toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        if (repo.isExist(model.getFollow_id()) && repo.isExistFollwer(model.getFollower_id()) && repo.isExistFollwing(model.getOrganization_id())) {
            int res = repo.updateSchoolFollowCompany(model.getFollow_id(), model.getOrganization_id(), model.getFollower_id());

            if (res == 1) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("status", 200);
                objectNode.put("Follow_id", model.getFollow_id());
                objectNode.put("Organization_id", model.getOrganization_id());
                objectNode.put("Follower_id", model.getFollower_id());

                return ResponseEntity.status(HttpStatus.OK).body(objectNode);
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("status", 400);
                objectNode.put("message", "not success");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "id not exist");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ObjectNode deleteSchoolFollowCompany(@PathVariable int id) {
        if (repo.isExist(id)) {
            int res = repo.deleteSchoolFollowCompany(id);
            if (res == 1) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("status", 200);
                objectNode.put("message", "success");

                return objectNode;
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("status", 400);
                objectNode.put("message", "not success");

                return objectNode;
            }
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "not exist");

            return objectNode;
        }

    }


    @DeleteMapping("/org/{org_id}/follower/{follow_id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ObjectNode deleteSchoolFollowCompanyByIds(@PathVariable int org_id, @PathVariable int follow_id) {
        if (repo.isRecordExist(org_id, follow_id)) {
            int res = repo.deleteSchoolFollowCompanyByIds(org_id, follow_id);
            if (res == 1) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("status", 200);
                objectNode.put("message", "success");

                return objectNode;
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("status", 400);
                objectNode.put("message", "not success");

                return objectNode;
            }
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("status", 400);
            objectNode.put("message", "not exist");

            return objectNode;
        }

    }


    //who's followed
    @GetMapping("/followers/{companyId}/count")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public int getFollowersCount(@PathVariable int companyId) {
        return repo.getFollowersCount(companyId);
    }


    @GetMapping("/school/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<FollowSchoolProfilesDto> getSchoolsWithFollow(@PathVariable int id) {
        return repo.getSchoolsWithFollow(id);
    }


    @GetMapping("/company/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<getCompaniesWithFollowDTo> getCompaniesWithFollow(@PathVariable int id) {
        return repo.getCompaniesWithFollow(id);
    }

    @GetMapping("/companies/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<GetCompaniesWithFollowANDOffers> getCompaniesWithFollowANDOffres(@PathVariable int id) {
        return repo.getCompaniesWithFollowAndOffers(id);
    }

    @GetMapping("/company/{org_id}/school/{fol_id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public boolean isRecordExist(@PathVariable int org_id,@PathVariable int fol_id){
        return repo.isRecordExist(org_id, fol_id);
    }


}
