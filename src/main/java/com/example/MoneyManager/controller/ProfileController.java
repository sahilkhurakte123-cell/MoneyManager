package com.example.MoneyManager.controller;

import com.example.MoneyManager.dto.AuthDto;
import com.example.MoneyManager.dto.ProfileDto;
import com.example.MoneyManager.repository.ProfileRepo;
import com.example.MoneyManager.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ProfileController {

    private final ProfileService profileService;
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/register")
    public ResponseEntity<ProfileDto> registerProfile(@RequestBody ProfileDto profileDto){
        ProfileDto registeredProfile = profileService.registerProfile(profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        boolean isActivated = profileService.activateProfile(token);
        if(isActivated){
            return ResponseEntity.status(HttpStatus.OK).body("Activated");
        }
        else  {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid activation Token");
        }
    }

    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDto authDto){

        try {
            if(!profileService.isAccountActivated(authDto.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "Account not active"
                ));
            }
            Map<String,Object>response =  profileService.authenticateAndGenerateToken(authDto);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", e.getMessage()
            ));
        }

    }

}
