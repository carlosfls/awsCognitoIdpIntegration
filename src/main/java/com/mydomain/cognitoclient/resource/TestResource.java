package com.mydomain.cognitoclient.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestResource {

    @GetMapping("/health")
    public ResponseEntity<String>testHealth(){
        return ResponseEntity.ok("Application Running!!");
    }
}
