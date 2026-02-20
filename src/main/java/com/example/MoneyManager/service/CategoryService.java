package com.example.MoneyManager.service;

import com.example.MoneyManager.dto.CategoryDto;
import com.example.MoneyManager.model.Category;
import com.example.MoneyManager.model.Profile;
import com.example.MoneyManager.repository.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProfileService profileService;
    private final CategoryRepo categoryRepo;

    public CategoryDto saveCategory(CategoryDto categoryDto) {

        Profile profile = profileService.getCurrentProfile();

        if(categoryRepo.existsByNameAndProfileId(categoryDto.getName(), profile.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }

        Category newCategory = toEntity(categoryDto, profile);
        newCategory = categoryRepo.save(newCategory);
        return toDto(newCategory);

    }

    public List<CategoryDto> getAllCategoriesForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        List<Category> categories = categoryRepo.findByProfileId(profile.getId());
        return categories.stream().map(this::toDto).toList();
    }

    public List<CategoryDto> getAllCategoriesForCurrentUserByType(String type) {
        Profile profile = profileService.getCurrentProfile();
        List<Category> categories = categoryRepo.findByTypeAndProfileId(type, profile.getId());
        return categories.stream().map(this::toDto).toList();
    }

    public CategoryDto updateCategoryForCurrentUser(CategoryDto categoryDto, Long categoryId) {
        Profile profile = profileService.getCurrentProfile();
        Category existingCategory = categoryRepo.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existingCategory.setName(categoryDto.getName());
        existingCategory.setIcon(categoryDto.getIcon());
        categoryRepo.save(existingCategory);
        return toDto(existingCategory);
    }

    private Category toEntity(CategoryDto categoryDto, Profile profile) {
        return Category.builder()
                .name(categoryDto.getName())
                .icon(categoryDto.getIcon())
                .profile(profile)
                .type(categoryDto.getType())
                .build();
    }

    private CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .profile_id(category.getProfile() != null ? category.getProfile().getId() : null)
                .name(category.getName())
                .icon(category.getIcon())
                .type(category.getType())
                .createdOn(category.getCreatedOn())
                .updatedOn(category.getUpdatedOn())
                .build();
    }

}
