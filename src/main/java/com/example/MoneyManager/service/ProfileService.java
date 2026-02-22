package com.example.MoneyManager.service;

import com.example.MoneyManager.dto.AuthDto;
import com.example.MoneyManager.dto.ProfileDto;
import com.example.MoneyManager.model.Profile;
import com.example.MoneyManager.repository.ProfileRepo;
import com.example.MoneyManager.utilities.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class ProfileService {

    private final ActivationEmailService activationEmailService;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepo profileRepo;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Value("${app.activation.url}")
    private String activationUrl;

    public ProfileService(ActivationEmailService activationEmailService, PasswordEncoder passwordEncoder, ProfileRepo profileRepo, AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.activationEmailService = activationEmailService;
        this.passwordEncoder = passwordEncoder;
        this.profileRepo = profileRepo;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public ProfileDto registerProfile(ProfileDto profileDto){

        Profile newProfile = toEntity(profileDto);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepo.save(newProfile);

        ProfileDto newProfileDto = toDTO(newProfile);

        String activationLink = activationUrl+"/activate?token="
                + newProfile.getActivationToken();

        String subject = "Activate MoneyManager account";
        String body = "Click on the given link to activate MoneyManager account " + activationLink;

        try {
            activationEmailService.sendEmail(newProfile.getEmail(), subject, body);
        } catch (Exception e) {
            System.out.println("Email failed but user registered successfully");
            e.printStackTrace();   // VERY useful for debugging
        }

        return newProfileDto;
    }


    public Profile toEntity(ProfileDto profileDto){
        return Profile.builder()
                .id(profileDto.getId())
                .fullname(profileDto.getFullname())
                .email(profileDto.getEmail())
                .password(passwordEncoder.encode(profileDto.getPassword()))
                .profileImgUrl(profileDto.getProfileImgUrl())
                .createdOn(profileDto.getCreatedOn())
                .updatedOn(profileDto.getUpdatedOn())
                .build();

    }

    public ProfileDto toDTO(Profile profile) {
        return ProfileDto.builder()
                .id(profile.getId())
                .fullname(profile.getFullname())
                .email(profile.getEmail())
                .profileImgUrl(profile.getProfileImgUrl())
                .createdOn(profile.getCreatedOn())
                .updatedOn(profile.getUpdatedOn())
                .build();
    }

    public boolean activateProfile(String activationToken){
        return profileRepo.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepo.save(profile);
                    return true;
                })
                .orElse(false);
    }

    public boolean isAccountActivated(String email){
        return profileRepo.findByEmail(email)
                .map(profile -> profile.getIsActive())
                .orElse(false);
    }

    public Profile getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Not found" +  authentication.getName()));
    }

    public ProfileDto getPublicProfile(String email){
        Profile currentUser = null;
        if(email == null){
            currentUser = getCurrentProfile();
        }
        else{
            currentUser = profileRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Not found" +  email));
        }

        return ProfileDto.builder()
                .id(currentUser.getId())
                .fullname(currentUser.getFullname())
                .email(currentUser.getEmail())
                .password(currentUser.getPassword())
                .profileImgUrl(currentUser.getProfileImgUrl())
                .createdOn(currentUser.getCreatedOn())
                .updatedOn(currentUser.getUpdatedOn())
                .build();

    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDto authDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword()));

            String token = jwtUtil.generateToken(authDto.getEmail())    ;
            return Map.of(
                    "token", token,
                    "user", getPublicProfile(authDto.getEmail()));

        }catch (Exception e){
            throw new UsernameNotFoundException("Invalid email or password");
        }
    }
}
