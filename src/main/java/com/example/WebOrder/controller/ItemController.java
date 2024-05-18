package com.example.WebOrder.controller;

import com.example.WebOrder.dto.CategoryDto;
import com.example.WebOrder.dto.ItemDto;
import com.example.WebOrder.exception.status4xx.ForbiddenException;
import com.example.WebOrder.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

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
    public String getMenuByOwner(@PathVariable("itemId") Long itemId, Model model, @RequestParam(name="page", defaultValue = "1") Integer page) {
        ItemDto itemInfo = itemService.getItemInfoById(itemId);
        if (!loginService.getCurrentUserEntity().getId().equals(itemInfo.getAdminId())) throw new ForbiddenException("해당 메뉴를 볼 권한이 없습니다!");
        model.addAttribute("itemInfo", itemInfo);
        model.addAttribute("reviewList", reviewService.getReviewsOfItem(itemId, PageRequest.of(page - 1, 10, Sort.Direction.DESC, "id")));
        model.addAttribute("totalPage", reviewService.getNumberOfPages(itemId));
        return "item/itemDetail";
    }

    //메뉴 추가하기
    @GetMapping("/admin/item/create")
    public String createItemForm(Model model){
        Long adminId = loginService.getCurrentUserEntity().getId();
        model.addAttribute("itemInfo", new ItemDto());//빈 아이템 dto
        model.addAttribute("categories", categoryService.getAllCategory(adminId));
        return "item/itemCreateForm";
    }

    @PostMapping("/admin/item/create")
    public String createItem(ItemDto dto, @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        String fileName;
        if (Objects.equals(image.getOriginalFilename(), "")) {
            fileName = "";
        }
        else {
            fileName = s3UploadService.upload(image);
        }
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
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        Long adminId = loginService.getCurrentUserEntity().getId();
        ItemDto itemInfo = itemService.getItemInfoById(itemId);
        if (!itemInfo.getAdminId().equals(adminId)) throw new ForbiddenException("해당 메뉴에 대한 권한이 없습니다!");
        model.addAttribute("itemInfo", itemInfo);
        model.addAttribute("categories", categoryService.getAllCategory(adminId));
        return "item/itemUpdateForm";
    }
    @PostMapping("/admin/item/update/{itemId}")
    public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute ItemDto dto, @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        ItemDto itemInfo = itemService.getItemInfoById(itemId);
        if (Objects.equals(itemInfo.getItemImageUrl(), dto.getItemImageUrl())) {
            itemService.updateItem(loginService.getCurrentUserEntity().getId(), itemId, dto);
        }
        else {
            String fileName = s3UploadService.upload(image);
            itemService.updateItem(loginService.getCurrentUserEntity().getId(), itemId, dto, fileName);
        }
        return "redirect:/admin/item";
    }
}
