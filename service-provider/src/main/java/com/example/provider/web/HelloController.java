package com.example.provider.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api")
public class HelloController {

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/hello")
    public String hello() {
        return "Hola desde " + appName;
    }

    // New endpoint that fails approximately 70% of the time with HTTP 500
    @GetMapping("/unreliable")
    public ResponseEntity<String> unreliable() {
        int rnd = ThreadLocalRandom.current().nextInt(100);
        if (rnd < 70) { // fail 70% of requests
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Service failure (simulated). Random=" + rnd);
        }
        return ResponseEntity.ok("Success from " + appName + " (Random=" + rnd + ")");
    }
}
