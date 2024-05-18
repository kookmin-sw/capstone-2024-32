package com.example.WebOrder.controller;

import com.example.WebOrder.dto.OrderDto;
import com.example.WebOrder.service.LoginService;
import com.example.WebOrder.service.OrderPasswordService;
import com.example.WebOrder.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class WebSocketController {
    private final LoginService loginService;
    private final OrderService orderService;
    private final OrderPasswordService orderPasswordService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketController(LoginService loginService, OrderService orderService, OrderPasswordService orderPasswordService, SimpMessagingTemplate simpMessagingTemplate) {
        this.loginService = loginService;
        this.orderService = orderService;
        this.orderPasswordService = orderPasswordService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    //주문 인증번호 관련
    // 웹소켓 내부적으로 통신을 주고 받기 위해 사용함
    @MessageMapping("/entranceCode")
    @SendToUser("/topic/entranceCode")
    public String sendEntranceCode(String code){
        return code;
    }

    //주문 비밀번호 페이지 보기
    @GetMapping("/owner/code/entrance")
    public String getEntranceCodeOfShopByOwner(Model model, HttpServletRequest request){
        // 주문 비밀번호 받아오기
        String entranceCode = orderPasswordService.getEntranceCode(loginService.getCurrentUserEntity().getId());
        model.addAttribute("entranceCode", entranceCode);
        // 세션 시간 30분 연장하기
        // 만약 30분이 넘도록 해당 페이지 안에서 머무른다면 js를 통해 해당 페이지 안에서 30분 간격으로 새로고침을 하는 방법을 고려중이다. 아직 이 부분은 미구현.
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(1800);
        return "html/entranceCode";
    }

    @GetMapping("/owner/code/entrance/update")
    public String updateEntranceCode(){
        orderPasswordService.updateEntranceCode(loginService.getCurrentUserEntity().getId());
        return "redirect:/owner/code/entrance";
    }

    //주문 대기열 관련
    // 웹소켓 내부적으로 통신을 주고 받기 위해 사용함
    @MessageMapping("/queue")
    @SendToUser("/topic/queue")
    public List<OrderDto> sendQueue(List<OrderDto> dtoList) {return dtoList;}

    //주문 대기열 보기
    //주문대기열 보기
    @GetMapping("/admin/queue")
    public String getOrderQueueByOwner(Model model){
        model.addAttribute("orders", orderService.getUnfinishedOrder(loginService.getCurrentUserEntity().getId()));
        return "order/orderQueue";
    }

    @MessageMapping("/updateOrderStatus")
    public void handleOrderStatusUpdate(@Payload Map<String, Object> payload) {
        // 주문 상태 업데이트 처리
        // message.getOrderId()와 message.getAction()을 사용하여 처리 로직 작성
        Long orderId = Long.parseLong(payload.get("orderId").toString());
        String action = payload.get("action").toString();

        orderService.changeOrderStatus(orderId, action);
    }

}