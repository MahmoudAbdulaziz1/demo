package com.taj;

import com.taj.controller.test_image.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;

@SpringBootApplication
@ComponentScan(basePackages="com.taj")
public class EfazApplication implements CommandLineRunner {

    @Resource
    StorageService storageService;
    public static void main(String[] args) {

        SpringApplication.run(EfazApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    public void run(String... strings) throws Exception {
        storageService.init();
    }


}
