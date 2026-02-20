package com.example.MoneyManager.repository;

import com.example.MoneyManager.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepo extends JpaRepository<Category, Long> {

    List<Category> findByProfileId(Long profileId);
    Optional<Category> findByIdAndProfileId(Long id, Long profileId);
    List<Category> findByTypeAndProfileId(String type, Long profileId);
    Boolean existsByNameAndProfileId(String name, Long profileId);
}
