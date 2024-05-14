package com.example.WebOrder.service;

import com.example.WebOrder.dto.CategoryDto;
import com.example.WebOrder.dto.ItemDto;
import com.example.WebOrder.entity.Category;
import com.example.WebOrder.entity.CategoryStatus;
import com.example.WebOrder.entity.Item;
import com.example.WebOrder.entity.ItemStatus;
import com.example.WebOrder.exception.status4xx.ForbiddenException;
import com.example.WebOrder.repository.CategoryRepository;
import com.example.WebOrder.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final LoginService loginService;

    public CategoryService(CategoryRepository categoryRepository, ItemRepository itemRepository, LoginService loginService) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        this.loginService = loginService;
    }

    public List<CategoryDto> getAllCategory(Long adminId) {
        List<Category> categoryList = categoryRepository.findAllByAdminIdAndStatus(adminId, CategoryStatus.ACTIVE);

        List<CategoryDto> categoryDtoList = categoryList.stream().map(CategoryDto::fromEntity).toList();
        log.info(String.valueOf(categoryDtoList.size()));
        return categoryDtoList;
    }


    public Long createCategory(Long adminId, CategoryDto dto) {
        if (!loginService.isCurrentUserAuthenticated(adminId)) throw new ForbiddenException("해당 작업을 할 권한이 없습니다!");

        Category category = new Category();
        category.setName(dto.getName());
        category.setAdminId(adminId);
        category.setStatus(CategoryStatus.ACTIVE);

        Category savedCategory = categoryRepository.save(category);

        return savedCategory.getId();
    }

    @Transactional
    public void deleteCategory(Long adminId, Long categoryId) throws IOException {
        if (!loginService.isCurrentUserAuthenticated(adminId)) throw new ForbiddenException("해당 작업을 할 권한이 없습니다!");

        Category category = categoryRepository.findById(categoryId).get();
        category.setStatus(CategoryStatus.INACTIVE);
        List<Item> items = category.getItems();

        for (Item item : items) {
            item.setStatus(ItemStatus.INACTIVE);
            itemRepository.save(item);
        }

        categoryRepository.save(category);
    }
}
