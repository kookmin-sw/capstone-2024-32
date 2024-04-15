package com.example.WebOrder.controller;

import com.example.WebOrder.dto.ItemDto;
import com.example.WebOrder.dto.SeatDto;
import com.example.WebOrder.entity.User;
import com.example.WebOrder.service.*;
import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@Slf4j
public class OwnerController {
    private final OrderPasswordService orderPasswordService;
    private final LoginService loginService;
    private final SeatService seatService;
    private final ItemService itemService;
    private final ReviewService reviewService;

    public OwnerController(OrderPasswordService orderPasswordService, LoginService loginService, SeatService seatService, ItemService itemService, ReviewService reviewService) {
        this.orderPasswordService = orderPasswordService;
        this.loginService = loginService;
        this.seatService = seatService;
        this.itemService = itemService;
        this.reviewService = reviewService;
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

    @GetMapping("/owner/seat/create")
    public String getCreateSeatForm(Model model){
        model.addAttribute("seat", new SeatDto());
        model.addAttribute("isCreate", true);
        model.addAttribute("seatName", null);
        return "html/seatCreate";
    }
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
    @GetMapping("/owner/seat/update/{seatId}")
    public String getUpdateSeatForm(@PathVariable("seatId") Long seatId, Model model){
        model.addAttribute("seat", seatService.getSeatDto(seatId));
        model.addAttribute("isCreate", false);
        model.addAttribute("seatName", seatService.getSeatName(seatId));
        return "html/seatCreate";
    }
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



    //메뉴 전체보기
    @GetMapping("/owner/menu")
    public String getWholeMenuByOwner(Model model){
        Long userId = loginService.getCurrentUserEntity().getId();
        model.addAttribute("itemStat", itemService.getBestItemStat(userId));
        model.addAttribute("itemList",itemService.getAllItemsOfUser(userId));

        return "menu/menuManage";
    }

    //메뉴 상세보기
    @GetMapping("/owner/menu/detail/{itemId}")
    public String getMenuByOwner(@PathVariable("itemId") Long itemId, Model model) {
        model.addAttribute("itemInfo", itemService.getItemInfoById(itemId));
        model.addAttribute("reviewList", reviewService.getReviewsOfItem(itemId));
        return "menu/menuDetail";
    }

    //메뉴 추가하기

    @GetMapping("/owner/menu/create")
    public String getMenuCreateForm(Model model){
        model.addAttribute("isCreate", true);
        model.addAttribute("itemInfo", new ItemDto());//빈 아이템 dto
        return "menu/menuForm";
    }
    @PostMapping("/owner/menu/create")
    public String addMenuByOwner(ItemDto dto){
        itemService.createItem(loginService.getCurrentUserEntity().getId(), dto);
        return "redirect:/owner/menu";
    }

    //메뉴 삭제하기
    @PostMapping("/owner/menu/delete/{itemId}")
    public String deleteMenuByOwner(@PathVariable("itemId") Long itemId){
        itemService.deleteItem(loginService.getCurrentUserEntity().getId(), itemId);
        return "redirect:/owner/menu";
    }

    //메뉴 업데이트하기
    @GetMapping("/owner/menu/update/{itemId}")
    public String getMenuUpdateForm(@PathVariable("itemId") Long itemId, Model model){
        model.addAttribute("isCreate", false);
        model.addAttribute("itemInfo", itemService.getItemInfoById(itemId));
        return "menu/menuForm";
    }
    @PostMapping("/owner/menu/update/{itemId}")
    public String updateMenuByOwner(@PathVariable("itemId") Long itemId, @ModelAttribute ItemDto dto){
        itemService.updateItem(loginService.getCurrentUserEntity().getId(), itemId, dto);
        return "redirect:/owner/menu";
    }

}
