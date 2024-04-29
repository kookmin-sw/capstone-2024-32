package com.example.WebOrder.service;

import com.example.WebOrder.dto.OrderDto;
import com.example.WebOrder.dto.OrderItemDto;
import com.example.WebOrder.entity.*;
import com.example.WebOrder.repository.ItemRepository;
import com.example.WebOrder.repository.OrderRepository;
import com.example.WebOrder.repository.SeatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public OrderService(LoginService loginService, OrderRepository orderRepository, SeatRepository seatRepository, ItemRepository itemRepository) {
        this.loginService = loginService;
        this.orderRepository = orderRepository;
        this.seatRepository = seatRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * 주문
     */
    public Long order(Long seatId, OrderItemDto... dtoList) throws JsonProcessingException {
        Seat seat = seatRepository.findById(seatId).get();
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

        List<Order> orderList = orderRepository.findOrdersByUserIdAndStatus(userId, OrderStatus.ORDER);

        return orderList.stream().map(OrderDto::fromEntity).toList();
    }

    @Transactional
    public void deleteOrder(Long userId, Long orderId){
        if (!loginService.isCurrentUserAuthenticated(userId)) throw new RuntimeException("권한 없음");

        orderRepository.deleteById(orderId);
    }

}
