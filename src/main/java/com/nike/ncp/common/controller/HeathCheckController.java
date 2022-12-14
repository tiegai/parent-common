package com.nike.ncp.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeathCheckController {

    @GetMapping("/healthcheck")
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.ok().build();
    }
}
