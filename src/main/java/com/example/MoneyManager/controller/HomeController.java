package com.example.MoneyManager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/check")
    public String  greet(){
        return "What up, Biatch?";
    }

}
