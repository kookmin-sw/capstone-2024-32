package com.example.WebOrder.controller;

import com.example.WebOrder.dto.CategoryDto;
import com.example.WebOrder.dto.ItemDto;
import com.example.WebOrder.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Controller
public class ItemController {

    private final LoginService loginService;
    private final ItemService itemService;
    private final ReviewService reviewService;
    private final S3UploadService s3UploadService;
    private final CategoryService categoryService;

    public ItemController(LoginService loginService, ItemService itemService, ReviewService reviewService, S3UploadService s3UploadService, CategoryService categoryService) {
        this.loginService = loginService;
        this.itemService = itemService;
        this.reviewService = reviewService;
        this.s3UploadService = s3UploadService;
        this.categoryService = categoryService;
    }

    //메뉴 전체보기
    @GetMapping("/admin/item")
    public String getWholeMenuByOwner(Model model){
        Long userId = loginService.getCurrentUserEntity().getId();
        model.addAttribute("itemStat", itemService.getBestItemStat(userId));
        model.addAttribute("itemList",itemService.getAllItemsOfUser(userId));
        model.addAttribute("categories", categoryService.getAllCategory(userId));
        return "item/itemManage";
    }

    //메뉴 상세보기
    @GetMapping("/admin/item/detail/{itemId}")
    public String getMenuByOwner(@PathVariable("itemId") Long itemId, Model model) {
        model.addAttribute("itemInfo", itemService.getItemInfoById(itemId));
        return "item/itemDetail";
    }

    //메뉴 추가하기
    @GetMapping("/admin/item/create")
    public String createItemForm(Model model){
        Long adminId = loginService.getCurrentUserEntity().getId();
        model.addAttribute("isCreate", true);
        model.addAttribute("itemInfo", new ItemDto());//빈 아이템 dto
        model.addAttribute("categories", categoryService.getAllCategory(adminId));
        return "item/itemForm";
    }

    @PostMapping("/admin/item/create")
    public String createItem(ItemDto dto, @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        String fileName = s3UploadService.upload(image);
        log.info("filename = " + fileName);
        itemService.createItem(loginService.getCurrentUserEntity().getId(), dto, fileName);
        return "redirect:/admin/item";
    }

    //메뉴 삭제하기
    @PostMapping("/admin/item/delete/{itemId}")
    public String deleteItem(@PathVariable("itemId") Long itemId) throws IOException {
        itemService.deleteItem(loginService.getCurrentUserEntity().getId(), itemId);
        return "redirect:/admin/item";
    }

    //메뉴 업데이트하기
    @GetMapping("/admin/item/update/{itemId}")
    public String createItemForm(@PathVariable("itemId") Long itemId, Model model){
        model.addAttribute("isCreate", false);
        model.addAttribute("itemInfo", itemService.getItemInfoById(itemId));
        return "item/itemForm";
    }
    @PostMapping("/admin/item/update/{itemId}")
    public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute ItemDto dto){
        itemService.updateItem(loginService.getCurrentUserEntity().getId(), itemId, dto);
        return "redirect:/admin/item";
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
