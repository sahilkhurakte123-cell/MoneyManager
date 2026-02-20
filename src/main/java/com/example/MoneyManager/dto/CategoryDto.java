package com.example.MoneyManager.dto;

import com.example.MoneyManager.model.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CategoryDto {


    private Long id;
    private Long profile_id;
    private String name;
    private String icon;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private String type;

}
