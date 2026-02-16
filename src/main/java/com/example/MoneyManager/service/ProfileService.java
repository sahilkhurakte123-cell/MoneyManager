package com.example.MoneyManager.service;

import com.example.MoneyManager.dto.ProfileDto;
import com.example.MoneyManager.model.Profile;
import com.example.MoneyManager.repository.ProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

    private final ActivationEmailService activationEmailService;

    private final ProfileRepo profileRepo;
    public ProfileService(ActivationEmailService activationEmailService, ProfileRepo profileRepo) {
        this.activationEmailService = activationEmailService;
        this.profileRepo = profileRepo;
    }

    public ProfileDto registerProfile(ProfileDto profileDto){

        Profile newProfile = toEntity(profileDto);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepo.save(newProfile);

        ProfileDto newProfileDto = toDTO(newProfile);

        String activationLink = "http://localhost:8080/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate MoneyManager account";
        String body = "Click on the given like to activate MoneyManager account " + activationLink;
        activationEmailService.sendEmail(newProfile.getEmail(), subject, body);

        return newProfileDto;
    }

    public Profile toEntity(ProfileDto profileDto){
        return Profile.builder()
                .id(profileDto.getId())
                .fullname(profileDto.getFullname())
                .email(profileDto.getEmail())
                .password(profileDto.getPassword())
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

}
