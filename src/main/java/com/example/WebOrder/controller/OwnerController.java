package com.example.WebOrder.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class OwnerController {

    //마이페이지 (가게 관리 페이지) 보기
    @GetMapping("/owner/shoppage")
    public String getShopPageByOwner(){
        return null;
    }

    //전체 테이블뷰 보기
    @GetMapping("/owner/seat")
    public String getWholeSeatByOwner(){
        return null;
    }

    //테이블 생성하기
    @PostMapping("/owner/seat/create")
    public String postCreateSeatByOwner(){
        return null;
    }


    //테이블 삭제하기
    @PostMapping("/owner/seat/delete/{seatId}")
    public String deleteSeatByOwner(){
        return null;
    }

    //테이블 수정하기
    @PostMapping("/owner/seat/update/{seatId}")
    public String updateSeatByOwner(){
        return null;
    }

    //테이블 치우기
    @PostMapping("/owner/seat/clear/{seatId}")
    public String clearSeatByOwner(){
        return null;
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

    //QR코드 가져오기
    @GetMapping("/owner/code/qr/{seatId}")
    public String getQRCodeOfSeatByOnwer(){
        return null;
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
