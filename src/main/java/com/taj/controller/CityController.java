package com.taj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taj.model.CityModel;
import com.taj.model.CityModelEnglish;
import com.taj.repository.CityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by User on 9/16/2018.
 */
@RequestMapping("/service/evvaz/s/city")
@RestController
@CrossOrigin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CityController {

    private static final String STATUS = "status";
    private static final String MESSAGE = "message";
    private static final String NOFOUND = "This id not found";
    private static final String CITYNAME = "city_name";
    @Autowired
    CityRepo cityRepo;

    @Autowired
    ObjectMapper mapper;

    @PostMapping("/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> addCity(@RequestBody CityModel model) {
        int response = cityRepo.addCity(model.getCityName(), model.getCityAreaId(), model.getCityNameAr());
        if (response == 1) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(CITYNAME, model.getCityName());
            return ResponseEntity.status(HttpStatus.OK).body(objectNode);
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(MESSAGE, "Not added");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
    }

    @GetMapping("/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<List<CityModelEnglish>> getCities(int flag_ar) {
        if (cityRepo.getCities(flag_ar).isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(cityRepo.getCities(flag_ar));
        }
        return ResponseEntity.status(HttpStatus.OK).body(cityRepo.getCities(flag_ar));


    }

    @GetMapping("/{cityId}/{flag_ar}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> getCityById(@PathVariable int cityId,
                                                  @PathVariable int flag_ar) {
        if (!(cityRepo.checkIfExist(cityId))) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(STATUS, "400");
            objectNode.put(MESSAGE, NOFOUND);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        } else {
            CityModelEnglish cityModel = cityRepo.getCityById(cityId, flag_ar);
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("city_id", cityModel.getCitId());
            objectNode.put(NOFOUND, cityModel.getCityName());
            return ResponseEntity.status(HttpStatus.OK).body(objectNode);
        }
    }

    @PutMapping("/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> updateCity(@RequestBody CityModel model) {
        if (cityRepo.checkIfExist(model.getCitId())) {

            int response = cityRepo.updateCity(model.getCitId(), model.getCityName(), model.getCityAreaId());
            if (response == 1) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("city_id", model.getCitId());
                objectNode.put(NOFOUND, model.getCityName());
                return ResponseEntity.status(HttpStatus.OK).body(objectNode);
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put(STATUS, "400");
                objectNode.put(MESSAGE, "not updated");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }


        } else {

            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(STATUS, "400");
            objectNode.put(MESSAGE, NOFOUND);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);


        }
    }

    @DeleteMapping("/{cityId}")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public ResponseEntity<ObjectNode> deleteCity(@PathVariable int cityId) {
        if (!(cityRepo.checkIfExist(cityId))) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(STATUS, "400");
            objectNode.put(MESSAGE, NOFOUND);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        } else {
            int response = cityRepo.deleteCity(cityId);
            if (response == 1) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put(STATUS, "200");
                objectNode.put(MESSAGE, "Successfully deleted");
                return ResponseEntity.status(HttpStatus.OK).body(objectNode);
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put(STATUS, "400");
                objectNode.put(MESSAGE, "not deleted");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
        }
    }
}
