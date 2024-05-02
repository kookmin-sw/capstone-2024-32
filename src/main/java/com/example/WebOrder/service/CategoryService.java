package com.example.WebOrder.service;

import com.example.WebOrder.dto.CategoryDto;
import com.example.WebOrder.dto.ItemDto;
import com.example.WebOrder.entity.Category;
import com.example.WebOrder.entity.Item;
import com.example.WebOrder.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final LoginService loginService;

    public CategoryService(CategoryRepository categoryRepository, LoginService loginService) {
        this.categoryRepository = categoryRepository;
        this.loginService = loginService;
    }

    public List<CategoryDto> getAllCategoryDtos(Long adminId) {
        List<Category> categoryList = categoryRepository.findAllByAdminId(adminId);

        List<CategoryDto> categoryDtoList = categoryList.stream().map(CategoryDto::fromEntity).toList();
        return categoryDtoList;
    }

    public List<Category> getAllCategories(Long adminId) {
        return categoryRepository.findAllByAdminId(adminId);
    }

    public Long createCategory(Long adminId, CategoryDto dto) {
        if (!loginService.isCurrentUserAuthenticated(adminId)) throw new RuntimeException("권한 없음");

        Category category = new Category();
        category.setName(dto.getName());
        category.setAdminId(adminId);

        Category savedCategory = categoryRepository.save(category);

        return savedCategory.getId();
    }

    @Transactional
    public void deleteCategory(Long adminId, Long categoryId) throws IOException {
        if (!loginService.isCurrentUserAuthenticated(adminId)) throw new RuntimeException("권한 없음");

        categoryRepository.deleteById(categoryId);
    }
}
