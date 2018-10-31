package com.taj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.model.LoginIsLoggedDTO;
import com.taj.repository.NewLoginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by User on 9/11/2018.
 */
@RequestMapping("/service/evvaz/s/new/login")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class NewLoginController {

    private static final String STATUS = "status";
    private static final String MESSAGE = "message";
    @Autowired
    NewLoginRepo loginRepo;
    @Autowired
    ObjectMapper mapper;

    @PostMapping("/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> loginUsers(@RequestBody @Valid LoginIsLoggedDTO model, Errors errors, @PathVariable int flag_ar) {
        if (errors.hasErrors()) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(STATUS, 400);
            objectNode.put(MESSAGE, "Validation Failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }

//        loginRepo.loginUser(model.getUserEmail(), model.getUserPassword(),
//                model.getIsActive(), model.getLoginRole(), model.getLoginToken(), model.getCity(), model.getArea(), model.getLng(), model.getLat());
//        ObjectNode objectNode = mapper.createObjectNode();
//        objectNode.put("user_email", model.getUserEmail());
//        objectNode.put("user_password", model.getUserPassword());
//        objectNode.put("is_active", model.getIsActive());
//        objectNode.put("login_role", model.getLoginRole());
//        objectNode.put("login_token", model.getLoginToken());
//        objectNode.put("city", model.getCity());
//        objectNode.put("area", model.getArea());
//        objectNode.put("lng", model.getLng());
//        objectNode.put("lat", model.getLat());
//
//        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);

        return loginRepo.isLogged(model.getUser_email(), model.getUser_password(), model.getLogin_role(), flag_ar);

    }


}
