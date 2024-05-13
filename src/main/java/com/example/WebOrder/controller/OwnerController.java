package com.example.WebOrder.controller;

import com.example.WebOrder.dto.ItemDto;
import com.example.WebOrder.dto.OrderDto;
import com.example.WebOrder.dto.SeatDto;
import com.example.WebOrder.entity.User;
import com.example.WebOrder.service.*;
import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
public class OwnerController {
    private final OrderPasswordService orderPasswordService;
    private final LoginService loginService;
    private final SeatService seatService;
    private final ItemService itemService;
    private final ReviewService reviewService;
    private final OrderService orderService;

    private final S3UploadService s3UploadService;

    public OwnerController(OrderPasswordService orderPasswordService, LoginService loginService, SeatService seatService, ItemService itemService, ReviewService reviewService, OrderService orderService, S3UploadService s3UploadService) {
        this.orderPasswordService = orderPasswordService;
        this.loginService = loginService;
        this.seatService = seatService;
        this.itemService = itemService;
        this.reviewService = reviewService;
        this.orderService = orderService;
        this.s3UploadService = s3UploadService;
    }


    //마이페이지 (가게 관리 페이지) 보기
    @GetMapping("/owner/shoppage")
    public String getShopPageByOwner(){
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
    @GetMapping("/admin/code/qr/{seatId}")
    public String getQRCodeOfSeatByOwner(@PathVariable Long seatId, Model model) throws IOException, WriterException {
        String img = orderPasswordService.generateQRCode(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId(), seatId);
        model.addAttribute("img", img);
        return "html/qrcode";
    }





}
