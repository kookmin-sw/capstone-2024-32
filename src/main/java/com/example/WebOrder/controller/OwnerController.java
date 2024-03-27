package com.example.WebOrder.controller;

import com.example.WebOrder.entity.User;
import com.example.WebOrder.service.LoginService;
import com.example.WebOrder.service.OrderPasswordService;
import com.example.WebOrder.service.SeatService;
import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
@Slf4j
public class OwnerController {
    private final OrderPasswordService orderPasswordService;
    private final LoginService loginService;
    private final SeatService seatService;

    public OwnerController(OrderPasswordService orderPasswordService, LoginService loginService, SeatService seatService) {
        this.orderPasswordService = orderPasswordService;
        this.loginService = loginService;
        this.seatService = seatService;
    }


    //마이페이지 (가게 관리 페이지) 보기
    @GetMapping("/owner/shoppage")
    public String getShopPageByOwner(){
        return null;
    }

    //전체 테이블뷰 보기
    @GetMapping("/owner/seat/view")
    public String getWholeSeatByOwner(){
        return null;
    }

    //테이블 관리 페이지
    // !!!!!!!!! 새로 추가한 url !!!!!!!!!!!!!
    @GetMapping("/owner/seat/manage")
    public String getSeatManagePageByOwner(Model model) {
        model.addAttribute("mostOrderedSeat", seatService.getMostOrderedSeatOfCurrentUser()); //가장 많이 주문된 좌석
        model.addAttribute("leastOrderedSeat", seatService.getLeastOrderedSeatOfCurrentUser()); //가장 적게 주문된 좌석
        model.addAttribute("totalSeatNum", seatService.getTotalSeatNum()); //총 좌석 개수
        model.addAttribute("seatList", seatService.getBasicSeatListOfCurrentUser()); //좌석 이름 리스트


        return "html/seatManage";
    }

    //테이블 생성하기
    @PostMapping("/owner/seat/create")
    public String createSeatByOwner(String seatName){
        seatService.addSeat(seatName);
        return "redirect:/owner/seat/manage";
    }


    //테이블 삭제하기
    @PostMapping("/owner/seat/delete/{seatId}")
    public String deleteSeatByOwner(@PathVariable("seatId") Long seatId){
        seatService.deleteSeat(seatId);
        return "redirect:/owner/seat/manage";
    }

    //테이블 수정하기
    @PostMapping("/owner/seat/update/{seatId}")
    public String updateSeatByOwner(@PathVariable("seatId") Long seatId, String seatName){
        seatService.updateSeat(seatId, seatName);
        return "redirect:/owner/seat/manage";
    }

    //테이블 치우기
    @PostMapping("/owner/seat/clear/{seatId}")
    public String clearSeatByOwner(@PathVariable("seatId") Long seatId){
        seatService.clearSeat(seatId);
        return "redirect:/owner/seat/manage";
    }

    //주문대기열 보기
    @GetMapping("/owner/queue")
    public String getOrderQueueByOwner(){
        return null;
    }

    //주문처리하기
    @PostMapping("/owner/order/{orderId}/check")
    public String checkOrderByOwner(){
        return null;
    }

    //주문삭제하기
    @DeleteMapping("/owner/order/{orderId}/delete")
    public String deleteOrderByOwner(){
        return null;
    }


    //테이블 계산서보기
    @GetMapping("/owner/bills/{seatId}")
    public String getBillsOfSeatByOwner(){
        return null;
    }

    //QR코드 생성하기
    @GetMapping("/owner/code/qr/{seatId}")
    public String getQRCodeOfSeatByOnwer(@PathVariable Long seatId, Model model) throws IOException, WriterException {
        String img = orderPasswordService.generateQRCode(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId(), seatId);
        model.addAttribute("img", img);
        return "html/qrcode";
    }

    //주문 비밀번호 페이지 보기
    @GetMapping("/owner/code/password")
    public String getPasswordOfShopByOwner(){
        return null;
    }

    //메뉴 전체보기
    @GetMapping("/owner/menu")
    public String getWholeMenuByOwner(){
        return null;
    }

    //메뉴 상세보기
    @GetMapping("/owner/menu/{menuId}")
    public String getMenuByOwner() {
        return null;
    }

    //메뉴 추가하기
    @PostMapping("/owner/menu")
    public String addMenuByOwner(){
        return null;
    }

    //메뉴 삭제하기
    @PostMapping("/owner/menu/{menuId}/delete")
    public String deleteMenuByOwner(){
        return null;
    }

    //메뉴 업데이트하기
    @PostMapping("/onwer/menu/{menuId}/update")
    public String updateMenuByOwner(){
        return null;
    }

}
