package com.example.WebOrder.controller;

import com.example.WebOrder.exception.status4xx.NoEntityException;
import com.example.WebOrder.exception.status4xx.NotAuthenticatedException;
import com.example.WebOrder.exception.status4xx.TypicalException;
import com.example.WebOrder.service.LoginService;
import com.example.WebOrder.service.OrderPasswordService;
import com.example.WebOrder.service.SeatService;
import com.example.WebOrder.service.UrlEncodeService;
import jakarta.servlet.http.HttpServletResponse;
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
    private final LoginService loginService;
    private final SeatService seatService;

    public GuestController(OrderPasswordService orderPasswordService, LoginService loginService, SeatService seatService) {
        this.orderPasswordService = orderPasswordService;
        this.loginService = loginService;
        this.seatService = seatService;
    }

    //QR를 찍었을 때 접근가능한 page
    @GetMapping("/guest/{encodedUserId}/{encodedSeatId}/login")
    public String getShopPageByGuestNotAuthenticated(@PathVariable("encodedUserId") String encodedUserId, @PathVariable("encodedSeatId") String encodedSeatId, Model model){
        Long userId = Long.parseLong(UrlEncodeService.decodeBase64UrlSafe(encodedUserId));
        Long seatId = Long.parseLong(UrlEncodeService.decodeBase64UrlSafe(encodedSeatId));

        if (!loginService.userExistsByUserId(userId)) throw new NoEntityException("잘못된 요청입니다!");
        if (!seatService.seatExistsByUserIdAndSeatId(userId, seatId)) throw new TypicalException("잘못된 요청입니다!");

        model.addAttribute("userId", encodedUserId);
        model.addAttribute("seatId", encodedSeatId);
        return "guest/guestWelcome";
    }


    // 주문하기를 눌러서 인증번호를 받아야 할 때 보내야할 페이지
    @GetMapping("/guest/{encodedUserId}/{encodedSeatId}/checkEntrance")
    public String getCheckEntranceCode(@PathVariable("encodedUserId") String encodedUserId, @PathVariable("encodedSeatId") String encodedSeatId, Model model){
        Long userId = Long.parseLong(UrlEncodeService.decodeBase64UrlSafe(encodedUserId));
        Long seatId = Long.parseLong(UrlEncodeService.decodeBase64UrlSafe(encodedSeatId));

        if (!loginService.userExistsByUserId(userId)) throw new NoEntityException("잘못된 요청입니다!");
        if (!seatService.seatExistsByUserIdAndSeatId(userId, seatId)) throw new TypicalException("잘못된 요청입니다!");

        return "guest/guestCheckEntrance";
    }

    // 인증번호 비교 페이지
    @PostMapping("/guest/{encodedUserId}/{encodedSeatId}/checkEntrance")
    public String checkEntranceCode(HttpServletResponse response, @PathVariable("encodedUserId") String encodedUserId, @PathVariable("encodedSeatId") String encodedSeatId, String entranceCode){
        Long userId = Long.parseLong(UrlEncodeService.decodeBase64UrlSafe(encodedUserId));
        Long seatId = Long.parseLong(UrlEncodeService.decodeBase64UrlSafe(encodedSeatId));

        log.info("인증번호 입력 : " + entranceCode);
        if (orderPasswordService.authenticateByEntranceCode(userId, entranceCode)){
            response.addCookie(orderPasswordService.getCookieAfterEntranceCode(userId, seatId));
            return "redirect:/order/" + userId + "/" + seatId;
        }
        else
            throw new NotAuthenticatedException("인증에 실패하였습니다!");
    }

    @GetMapping("/guest/fail")
    public String notallowed(){
        return "/guest/notAllowed";
    }

}
