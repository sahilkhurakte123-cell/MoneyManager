package com.example.MoneyManager.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDto {

    private Long id;
    private String fullname;
    private String email;
    private String password;
    private String profileImgUrl;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

}
