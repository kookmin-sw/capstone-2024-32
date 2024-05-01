package com.example.WebOrder.service;

import com.example.WebOrder.dto.OrderDto;
import com.example.WebOrder.dto.OrderItemDto;
import com.example.WebOrder.entity.*;
import com.example.WebOrder.repository.ItemRepository;
import com.example.WebOrder.repository.OrderRepository;
import com.example.WebOrder.repository.SeatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 주문 관련 업무를 처리하는 서비스
 */
@Slf4j
@Service
@Transactional
public class OrderService {
    private final LoginService loginService;
    private final OrderRepository orderRepository;
    private final SeatRepository seatRepository;
    private final ItemRepository itemRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public OrderService(LoginService loginService, OrderRepository orderRepository, SeatRepository seatRepository, ItemRepository itemRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.loginService = loginService;
        this.orderRepository = orderRepository;
        this.seatRepository = seatRepository;
        this.itemRepository = itemRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * 주문
     */
    public Long order(Long seatId, String json) throws JsonProcessingException {
        Seat seat = seatRepository.findById(seatId).get();
        ObjectMapper objectMapper = new ObjectMapper();
        OrderItemDto[] dtoList = objectMapper.readValue(json, OrderItemDto[].class);
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDto dto : dtoList) {
            if (dto.getCount() > 0) {
                Item item = itemRepository.findById(dto.getItemId()).get();
                orderItems.add(OrderItem.createOrderItem(item, dto.getCount()));
            }
        }
        // 주문 생성
        Order order = Order.createOrder(seat, orderItems);
        // 주문 저장
        orderRepository.save(order);

        return order.getId();
    }

    public List<OrderDto> getUnfinishedOrder(Long userId){
        if (!loginService.isCurrentUserAuthenticated(userId)) throw new RuntimeException("권한없음");

        List<Order> orderList = orderRepository.findOrdersByUserIdAndStatusIn(userId, Arrays.asList(OrderStatus.ORDER, OrderStatus.PROGRESS));

        return orderList.stream().map(OrderDto::fromEntity).toList();
    }

    @Transactional
    public void deleteOrder(Long userId, Long orderId){
        if (!loginService.isCurrentUserAuthenticated(userId)) throw new RuntimeException("권한 없음");

        orderRepository.deleteById(orderId);
    }

    public void changeOrderStatus(Long userId, Long orderId, String action) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) throw new RuntimeException("엔티티없음");
        Order order = optionalOrder.get();

        if (action.equals("완료")){
            order.setStatus(OrderStatus.COMPLETE);
            orderRepository.save(order);
        }
        else if (action.equals("진행중")){
            order.setStatus(OrderStatus.PROGRESS);
            orderRepository.save(order);
        }
        else if (action.equals("취소")){
            deleteOrder(userId, orderId);
        }

        simpMessagingTemplate.convertAndSend("/topic/queue", getUnfinishedOrder(userId));
    }
}
