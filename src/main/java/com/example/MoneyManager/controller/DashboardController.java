package com.example.MoneyManager.controller;

import com.example.MoneyManager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String,Object>> getDashboardData () {
        Map<String,Object> data = dashboardService.getDashboardData();
        return ResponseEntity.ok(data);
    }

}
