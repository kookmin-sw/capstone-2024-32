package com.example.WebOrder.controller;

import com.example.WebOrder.service.OrderPasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class GuestController {
    private final OrderPasswordService orderPasswordService;

    public GuestController(OrderPasswordService orderPasswordService) {
        this.orderPasswordService = orderPasswordService;
    }

    //QR를 찍었을 때 접근가능한 page
    @GetMapping("/guest/{userId}/{seatId}/login")
    public String getShopPageByGuestNotAuthenticated(@PathVariable("userId") Long userId, Model model, @PathVariable("seatId") Long seatId){
        model.addAttribute("userId", userId);
        model.addAttribute("seatId", seatId);
        return "guest/guestWelcome";
    }


    // 주문하기를 눌러서 인증번호를 받아야 할 때 보내야할 페이지
    @GetMapping("/guest/{userId}/{seatId}/checkEntrance")
    public String getCheckEntranceCode(@PathVariable Long userId, @PathVariable Long seatId, Model model){
        return "guest/guestCheckEntrance";
    }

    // 인증번호 비교 페이지
    @PostMapping("/guest/{userId}/{seatId}/checkEntrance")
    public String checkEntranceCode(@PathVariable Long userId, @PathVariable Long seatId, String entranceCode){
        log.info("인증번호 입력 : " + entranceCode);
         if (orderPasswordService.authenticateByEntranceCode(userId, entranceCode)){
             return "redirect:/order/" + userId + "/" + seatId;
         }
         else
             return "redirect:/guest/fail";
    }

    @GetMapping("/guest/fail")
    public String notallowed(){
        return "/guest/notAllowed";
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
