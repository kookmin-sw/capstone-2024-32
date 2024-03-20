package com.example.WebOrder.service;

import com.example.WebOrder.dto.OrderDto;
import com.example.WebOrder.entity.Order;
import com.example.WebOrder.entity.OrderStatus;
import com.example.WebOrder.repository.OrderRepository;
import jakarta.transaction.Transactional;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 주문 관련 업무를 처리하는 서비스
 */
@Slf4j
@Service
public class OrderService {
    private final LoginService loginService;
    private final OrderRepository orderRepository;
    public OrderService(LoginService loginService, OrderRepository orderRepository) {
        this.loginService = loginService;
        this.orderRepository = orderRepository;
    }

    public List<OrderDto> getUnfinishedOrder(Long userId){
        if (!loginService.isCurrentUserAuthenticated(userId)) throw new RuntimeException("권한없음");

        List<Order> orderList = orderRepository.findOrdersByUserIdAndStatus(userId, OrderStatus.ORDER);

        return orderList.stream().map(OrderDto::fromEntity).toList();
    }

    public void addOrder(){
        throw new RuntimeException("미구현");
    }

    @Transactional
    public void deleteOrder(Long userId, Long orderId){
        if (!loginService.isCurrentUserAuthenticated(userId)) throw new RuntimeException("권한 없음");

        orderRepository.deleteById(orderId);
    }

}
