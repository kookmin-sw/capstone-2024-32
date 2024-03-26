package com.example.WebOrder.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.Base64;

@Controller
@Slf4j
public class GuestController {

    //QR를 찍었을 때 접근가능한 page
    @GetMapping("/guest/{userId}/{seatId}/login")
    public String getShopPageByGuestNotAuthenticated(@PathVariable String userId, Model model){
        return null;
    }

    //비밀번호 인증을 성공했을 시 접근가능한 page
    @GetMapping("/guest/{userId}/{seatId}")
    public String getShopPageByGuest(@PathVariable Long userId, @PathVariable Long seatId){
        return null;
    }



    //주문하기를 눌렀을 때 접근가능한 page
    @GetMapping("/guest/{userId}/{seatId}/order")
    public String getOrderPageByGuest(@PathVariable Long userId, @PathVariable Long seatId){
        return null;
    }


    //메뉴 상세정보 보기
    @GetMapping("/guest/menu/{userId}/{menuId}")
    public String getMenuByGuest(@PathVariable Long userId, @PathVariable Long menuId){
        return null;
    }

    //주문하기
    @PostMapping("/guest/order/{encodedUserId}")
    public String postOrderByGuest(@PathVariable String encodedUserId){
        return null;
    }


    //리뷰하기를 눌렀을 때 접근가능한 page
    @GetMapping("/guest/review/{encodedUserId}")
    public String getReviewPageByGuest(@PathVariable String encodedUserId){
        return null;
    }


    //리뷰 작성하기
    @PostMapping("/guest/review/{encodedUserId}")
    public String postReviewByGuest(@PathVariable String encodedUserId){
        return null;
    }

}
