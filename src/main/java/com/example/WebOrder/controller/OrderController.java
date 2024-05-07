package com.example.WebOrder.controller;

import com.example.WebOrder.dto.OrderItemDto;
import com.example.WebOrder.service.CategoryService;
import com.example.WebOrder.service.ItemService;
import com.example.WebOrder.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
public class OrderController {

    private final ItemService itemService;
    private final OrderService orderService;
    private final CategoryService categoryService;

    public OrderController(ItemService itemService, OrderService orderService, CategoryService categoryService) {
        this.itemService = itemService;
        this.orderService = orderService;
        this.categoryService = categoryService;
    }


    // 인증을 성공했을 시 접근가능한 page
    @GetMapping("/order/{userId}/{seatId}")
    public String getShopPageByGuest(@PathVariable Long userId, @PathVariable Long seatId, Model model){
        // 인증 과정 했다 치고
        model.addAttribute("categories", categoryService.getAllCategory(userId));
        model.addAttribute("items",itemService.getAllItemsOfUser(userId));
        return "order/orderForm";
    }

    //주문하기
    @ResponseBody
    @PostMapping("/order/{userId}/{seatId}")
    public Boolean order(@PathVariable Long userId, @PathVariable Long seatId, @RequestBody String json) throws JsonProcessingException {
        orderService.order(seatId, json);
        log.info("주문 성공");
        return true;
    }

    @GetMapping("/order/success")
    public String orderSuccess(){
        log.info("주문 성공 리다이렉트");
        return "order/orderSuccess";
    }

}
