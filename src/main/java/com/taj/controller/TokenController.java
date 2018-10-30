package com.taj.controller;

import com.taj.model.NewLoginModelDto;
import com.taj.security.JwtGenerator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/evvaz/token")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TokenController {


    private JwtGenerator jwtGenerator;

    public TokenController(JwtGenerator jwtGenerator) {
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('super_admin') or hasAuthority('admin') or hasAuthority('company') or hasAuthority('school')")
    public String generate(@RequestBody final NewLoginModelDto jwtUser) {

        return jwtGenerator.generate(jwtUser);

    }
}
