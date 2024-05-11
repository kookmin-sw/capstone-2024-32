package com.example.WebOrder.controller;

import com.example.WebOrder.dto.CategoryDto;
import com.example.WebOrder.service.CategoryService;
import com.example.WebOrder.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Slf4j
@Controller
public class CategoryController {

    private final LoginService loginService;
    private final CategoryService categoryService;

    public CategoryController(LoginService loginService, CategoryService categoryService) {
        this.loginService = loginService;
        this.categoryService = categoryService;
    }

    // 카테고리 추가
    @PostMapping("/admin/category/create")
    public String createCategory(CategoryDto dto) {
        categoryService.createCategory(loginService.getCurrentUserEntity().getId(), dto);
        return "redirect:/admin/item";
    }

    @PostMapping("/admin/category/delete/{categoryId}")
    public String deleteCategory(@PathVariable("categoryId") Long categoryId) throws IOException {
        categoryService.deleteCategory(loginService.getCurrentUserEntity().getId(), categoryId);
        return "redirect:/admin/item";
    }
}
