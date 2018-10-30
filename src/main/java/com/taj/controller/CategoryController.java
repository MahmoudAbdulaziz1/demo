package com.taj.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.model.CategoryModel;
import com.taj.repository.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by Taj 51 on 6/10/2018.
 */
@RequestMapping("/service/evvaz/s/cat")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CategoryController {

    @Autowired
    CategoryRepo repo;
    @Autowired
    ObjectMapper mapper;

    /**
     * @return list of company categories
     */
//    @PreAuthorize("hasAuthority('company') or hasAuthority('admin')")
    @GetMapping("/getAll")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public List<CategoryModel> getCategories() {
        return repo.getCategories();
    }

    /**
     * @param id
     * @return category by id
     */

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public CategoryModel getCategory(@PathVariable int id) {
        return repo.getCategory(id);
    }


    /**
     * @param model add company category to database
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ObjectNode addCategory(@Valid @RequestBody CategoryModel model, Errors errors) {
        if (errors.hasErrors()){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            objectNode.put("details", errors.getAllErrors().toString());
            return objectNode;
        }
        int res = repo.addCategory(model.getCategory_name(), model.getCategory_name_ar());

        if (res == 1){
            ObjectNode objectNode = mapper.createObjectNode();
            //objectNode.put("login_id", model.getCategory_id());
            objectNode.put("category_name", model.getCategory_name());
            objectNode.put("category_name_ar", model.getCategory_name_ar());

            return objectNode;
        }else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success");

            return objectNode;
        }

    }

    /**
     * @param model update current category
     */


    @PutMapping("/update")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ObjectNode  updateCategory(@Valid @RequestBody CategoryModel model, Errors errors) {

        if (errors.hasErrors()){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            objectNode.put("details", errors.getAllErrors().toString());
            return objectNode;
        }
        int res = repo.updateCategory(model.getCategory_id(), model.getCategory_name(), model.getCategory_name_ar());

        if (res == 1){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("category_id", model.getCategory_id());
            objectNode.put("category_name", model.getCategory_name());
            objectNode.put("category_name_ar", model.getCategory_name_ar());

            return objectNode;
        }else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("value", "not success");

            return objectNode;
        }

    }

    /**
     * @param model delete current category
     */

    @PutMapping("/delete")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public JsonNode deleteCategory(@Valid @RequestBody CategoryModel model, Errors errors) {
        if (errors.hasErrors()){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("state", 400);
            objectNode.put("message", "Validation Failed");
            objectNode.put("details", errors.getAllErrors().toString());
            return objectNode;
        }
        int res= repo.deleteCategory(model.getCategory_id());

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
