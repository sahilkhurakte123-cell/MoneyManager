package com.example.MoneyManager.controller;

import com.example.MoneyManager.dto.CategoryDto;
import com.example.MoneyManager.model.Category;
import com.example.MoneyManager.service.CategoryService;
import jdk.dynalink.linker.LinkerServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/category")
    public ResponseEntity<CategoryDto> saveCategory(@RequestBody CategoryDto categoryDto) {
         CategoryDto savedCategory = categoryService.saveCategory(categoryDto);
         return ResponseEntity.ok().body(savedCategory);
    }

    @GetMapping("/category")
    public ResponseEntity<List<CategoryDto>> getAllCategoriesForCurrentUser() {
        List<CategoryDto> list = categoryService.getAllCategoriesForCurrentUser();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDto>> getCategoriesByType(@PathVariable String type) {
        List<CategoryDto> categories = categoryService.getAllCategoriesForCurrentUserByType(type);
        return ResponseEntity.ok().body(categories);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<List<CategoryDto>> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDto categoryDto) {
        categoryService.updateCategoryForCurrentUser(categoryDto,categoryId);
        return ResponseEntity.ok().body(categoryService.getAllCategoriesForCurrentUser());
    }

}
