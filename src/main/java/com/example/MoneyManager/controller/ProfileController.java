package com.example.MoneyManager.controller;

import com.example.MoneyManager.dto.AuthDto;
import com.example.MoneyManager.dto.ProfileDto;
import com.example.MoneyManager.repository.ProfileRepo;
import com.example.MoneyManager.service.ProfileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import com.example.MoneyManager.service.LoginAttemptService;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ProfileController {

    private final ProfileService profileService;
    private final LoginAttemptService loginAttemptService;

    @Value("${guest.email}")
    private String guestEmail;

    @Value("${guest.password}")
    private String guestPassword;

    public ProfileController(ProfileService profileService, LoginAttemptService loginAttemptService) {
        this.profileService = profileService;
        this.loginAttemptService = loginAttemptService;
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

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDto authDto){

        if (loginAttemptService.isBlocked(authDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "message", "Too many failed attempts, try again later"
            ));
        }

        try {
            if(!profileService.isAccountActivated(authDto.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "Account not active"
                ));
            }
            Map<String,Object>response =  profileService.authenticateAndGenerateToken(authDto);
            loginAttemptService.loginSucceeded(authDto.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            loginAttemptService.loginFailed(authDto.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", e.getMessage()
            ));
        }

    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getPublicProfile(){
        ProfileDto profileDto = profileService.getPublicProfile(null);
        return ResponseEntity.status(HttpStatus.OK).body(profileDto);
    }

    @PostMapping("/guest-login")
    public ResponseEntity<Map<String, Object>> guestLogin() {
        AuthDto guestAuth = AuthDto.builder()
                .email(guestEmail)
                .password(guestPassword)
                .build();
        Map<String, Object> response = profileService.authenticateAndGenerateToken(guestAuth);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
