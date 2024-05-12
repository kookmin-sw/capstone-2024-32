package com.example.WebOrder.controller;

import com.example.WebOrder.dto.SeatDto;
import com.example.WebOrder.dto.SeatOrderDto;
import com.example.WebOrder.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Controller
public class SeatController {
    private final SeatService seatService;
    private final LoginService loginService;

    public SeatController(SeatService seatService, LoginService loginService) {
        this.seatService = seatService;
        this.loginService = loginService;
    }

    //전체 테이블뷰 보기
    @GetMapping("/admin/seat/view")
    public String getWholeSeatByOwner(Model model){
        LocalDateTime now = LocalDateTime.now();
        // 원하는 포맷 지정 (예: "yyyy-MM-dd HH:mm:ss")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 포맷에 맞게 시간을 문자열로 변환
        String formattedTime = now.format(formatter);
        // 주문정보까지 포함된 좌석 정보 리스트
        model.addAttribute("currentTime", formattedTime);
        model.addAttribute("seatList", seatService.getWholeSeatOrdersOfCurrentUser());
        model.addAttribute("seatNum", seatService.getTotalSeatNum());
        model.addAttribute("mostOrderedSeat", seatService.getMostOrderedSeatOfCurrentUser()); //가장 많이 주문된 좌석
        model.addAttribute("leastOrderedSeat", seatService.getLeastOrderedSeatOfCurrentUser()); //가장 적게 주문된 좌석
        return "seat/seatView";
    }

    // 테이블 비우기
    @PostMapping("/admin/seat/view")
    public String getSeatEmpty(Long seatId){
        seatService.clearSeat(seatId);
        return "redirect:/admin/seat/view";
    }

    //테이블 계산 페이지
    @GetMapping("/admin/seat/bill/{seatId}")
    public String getSeatBillPage(Model model, @PathVariable("seatId") Long seatId){
        model.addAttribute("seat", seatService.getSeatOrdersOfSeat(loginService.getCurrentUserEntity().getId(), seatService.getSeatEntity(seatId)));
        return "seat/seatBill";
    }
    
    // api 활용 위해 ResponseBody 형태로 seat 계산서 리턴하기
    @ResponseBody
    @GetMapping("/admin/seat/bill/{seatId}/api")
    public SeatOrderDto getSeatBilledApi(Model model, @PathVariable("seatId") Long seatId){

        return seatService.getSeatOrdersOfSeat(loginService.getCurrentUserEntity().getId(), seatService.getSeatEntity(seatId));
    }

    // 테이블 계산하기
    @PostMapping("/admin/seat/bill/{seatId}")
    public String getSeatBilled(@PathVariable("seatId") Long seatId){
        seatService.makeSeatBilled(seatId);
        return "redirect:/admin/seat/view";
    }

    //테이블 생성하기
    @PostMapping("/admin/seat/create")
    public String createSeatByOwner(String seatName){
        seatService.addSeat(seatName);
        return "redirect:/admin/seat/view";
    }

    //테이블 삭제하기
    @PostMapping("/admin/seat/delete/{seatId}")
    public String deleteSeatByOwner(@PathVariable("seatId") Long seatId){
        seatService.deleteSeat(seatId);
        return "redirect:/admin/seat/view";
    }

    //테이블 수정하기
    @PostMapping("/admin/seat/update/{seatId}")
    public String updateSeatByOwner(@PathVariable("seatId") Long seatId, String seatName){
        seatService.updateSeatName(seatId, seatName);
        return "redirect:/admin/seat/view";
    }

    //테이블 치우기
    @PostMapping("/admin/seat/clear/{seatId}")
    public String clearSeatByOwner(@PathVariable("seatId") Long seatId){
        seatService.clearSeat(seatId);
        return "redirect:/admin/seat/view";
    }
}
