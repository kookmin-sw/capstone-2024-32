package com.example.WebOrder.service;

import com.example.WebOrder.dto.CategoryDto;
import com.example.WebOrder.dto.ItemDto;
import com.example.WebOrder.entity.Category;
import com.example.WebOrder.entity.CategoryStatus;
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

    public List<CategoryDto> getAllCategory(Long adminId) {
        List<Category> categoryList = categoryRepository.findAllByAdminIdAndStatus(adminId, CategoryStatus.ACTIVE);

        List<CategoryDto> categoryDtoList = categoryList.stream().map(CategoryDto::fromEntity).toList();
        log.info(String.valueOf(categoryDtoList.size()));
        return categoryDtoList;
    }


    public Long createCategory(Long adminId, CategoryDto dto) {
        if (!loginService.isCurrentUserAuthenticated(adminId)) throw new RuntimeException("권한 없음");

        Category category = new Category();
        category.setName(dto.getName());
        category.setAdminId(adminId);
        category.setStatus(CategoryStatus.ACTIVE);

        Category savedCategory = categoryRepository.save(category);

        return savedCategory.getId();
    }

    @Transactional
    public void deleteCategory(Long adminId, Long categoryId) throws IOException {
        if (!loginService.isCurrentUserAuthenticated(adminId)) throw new RuntimeException("권한 없음");

        Category category = categoryRepository.findById(categoryId).get();
        category.setStatus(CategoryStatus.INACTIVE);
        categoryRepository.save(category);
    }
}
