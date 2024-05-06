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
        // 주문정보까지 포함된 좌석 정보 리스트
        model.addAttribute("seatList", seatService.getWholeSeatOrdersOfCurrentUser());
        model.addAttribute("seatNum", seatService.getTotalSeatNum());
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

    //테이블 관리 페이지
    @GetMapping("/admin/seat/manage")
    public String getSeatManagePageByOwner(Model model) {
        model.addAttribute("mostOrderedSeat", seatService.getMostOrderedSeatOfCurrentUser()); //가장 많이 주문된 좌석
        model.addAttribute("leastOrderedSeat", seatService.getLeastOrderedSeatOfCurrentUser()); //가장 적게 주문된 좌석
        model.addAttribute("totalSeatNum", seatService.getTotalSeatNum()); //총 좌석 개수
        model.addAttribute("seatList", seatService.getBasicSeatListOfCurrentUser()); //좌석 이름 리스트

        return "seat/seatManage";
    }

    //테이블 생성하기
    @GetMapping("/admin/seat/create")
    public String getCreateSeatForm(Model model){
        model.addAttribute("seat", new SeatDto());
        model.addAttribute("isCreate", true);
        model.addAttribute("seatName", null);
        return "seat/seatCreate";
    }
    @PostMapping("/admin/seat/create")
    public String createSeatByOwner(String seatName){
        seatService.addSeat(seatName);
        return "redirect:/admin/seat/manage";
    }


    //테이블 삭제하기
    @PostMapping("/admin/seat/delete/{seatId}")
    public String deleteSeatByOwner(@PathVariable("seatId") Long seatId){
        seatService.deleteSeat(seatId);
        return "redirect:/admin/seat/manage";
    }

    //테이블 수정하기
    @GetMapping("/admin/seat/update/{seatId}")
    public String getUpdateSeatForm(@PathVariable("seatId") Long seatId, Model model){
        model.addAttribute("seat", seatService.getSeatDto(seatId));
        model.addAttribute("isCreate", false);
        model.addAttribute("seatName", seatService.getSeatName(seatId));
        return "seat/seatCreate";
    }
    @PostMapping("/admin/seat/update/{seatId}")
    public String updateSeatByOwner(@PathVariable("seatId") Long seatId, String seatName){
        seatService.updateSeatName(seatId, seatName);
        return "redirect:/admin/seat/manage";
    }

    //테이블 치우기
    @PostMapping("/admin/seat/clear/{seatId}")
    public String clearSeatByOwner(@PathVariable("seatId") Long seatId){
        seatService.clearSeat(seatId);
        return "redirect:/admin/seat/manage";
    }
}
