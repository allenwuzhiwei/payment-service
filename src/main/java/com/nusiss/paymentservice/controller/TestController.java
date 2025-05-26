package com.nusiss.paymentservice.controller;

import com.nusiss.paymentservice.config.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Object>> test() {

        return ResponseEntity.status(200).body(new ApiResponse<>(true, "test successfully", null));

    }
}
