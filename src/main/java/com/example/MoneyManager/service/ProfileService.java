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

    private final ProfileRepo profileRepo;
    public ProfileService(ProfileRepo profileRepo) {
        this.profileRepo = profileRepo;
    }

    public ProfileDto registerProfile(ProfileDto profileDto){

        Profile newProfile = toEntity(profileDto);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepo.save(newProfile);

        ProfileDto newProfileDto = toDTO(newProfile);
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

}
