package com.example.WebOrder.service;

import com.example.WebOrder.dto.OrderDto;
import com.example.WebOrder.dto.OrderItemDto;
import com.example.WebOrder.entity.*;
import com.example.WebOrder.exception.status4xx.ForbiddenException;
import com.example.WebOrder.exception.status4xx.NoEntityException;
import com.example.WebOrder.exception.status4xx.NotAuthenticatedException;
import com.example.WebOrder.repository.ItemRepository;
import com.example.WebOrder.repository.OrderRepository;
import com.example.WebOrder.repository.SeatRepository;
import com.example.WebOrder.repository.UserRepository;
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
import java.util.stream.Collectors;

/**
 * 주문 관련 업무를 처리하는 서비스
 */
@Slf4j
@Service
@Transactional
public class OrderService {
    private final UserRepository userRepository;
    private final LoginService loginService;
    private final OrderRepository orderRepository;
    private final SeatRepository seatRepository;
    private final ItemRepository itemRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public OrderService(UserRepository userRepository, LoginService loginService, OrderRepository orderRepository, SeatRepository seatRepository, ItemRepository itemRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.userRepository = userRepository;
        this.loginService = loginService;
        this.orderRepository = orderRepository;
        this.seatRepository = seatRepository;
        this.itemRepository = itemRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * 주문
     */
    public Long order(Long userId, Long seatId, String json) throws JsonProcessingException {
        Seat seat = seatRepository.findById(seatId).get();
        // 검증
        if (!seat.getUser().getId().equals(userId)) throw new ForbiddenException("권한이 없는 요청입니다.");

        ObjectMapper objectMapper = new ObjectMapper();
        OrderItemDto[] dtoList = objectMapper.readValue(json, OrderItemDto[].class);
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDto dto : dtoList) {
            if (dto.getCount() > 0) {
                Item item = itemRepository.findById(dto.getItemId()).get();
                if(!item.getAdminId().equals(userId)) throw new ForbiddenException("권한이 없는 요청입니다.");
                orderItems.add(OrderItem.createOrderItem(item, dto.getCount()));
                item.setOrderedCount(item.getOrderedCount() + dto.getCount());
            }
        }
        // 주문 생성
        Order order = Order.createOrder(seat, orderItems);
        // 주문 저장
        orderRepository.save(order);

        //Queue 전송
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) throw new NoEntityException("존재하지 않는 유저입니다!");
        User user = optionalUser.get();
        simpMessagingTemplate.convertAndSendToUser(user.getUsername(),"/topic/queue", getUnfinishedOrder(seat.getUser().getId()));

        return order.getId();
    }

    public List<OrderDto> getUnfinishedOrder(Long userId){

        List<Order> orderList = orderRepository.findOrdersByUserIdAndStatusIn(userId, Arrays.asList(OrderStatus.ORDER, OrderStatus.PROGRESS));

        return orderList.stream().map(OrderDto::fromEntity).toList();
    }

    @Transactional
    public void deleteOrder(Long userId, Long orderId){
        if (!loginService.isCurrentUserAuthenticated(userId)) throw new NotAuthenticatedException("권한이 없는 작업입니다!");

        orderRepository.deleteById(orderId);
    }

    public void changeOrderStatus(Long orderId, String action) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) throw new NoEntityException("해당하는 주문 내역이 존재하지 않습니다!");
        Order order = optionalOrder.get();

        Optional<User> optionalUser = userRepository.findById(order.getUserId());
        if (optionalUser.isEmpty()) throw new NoEntityException("잘못된 요청입니다!");
        User user = optionalUser.get();

        if (action.equals("완료")){
            order.setStatus(OrderStatus.COMPLETE);
            orderRepository.save(order);
        }
        else if (action.equals("접수")){
            order.setStatus(OrderStatus.PROGRESS);
            orderRepository.save(order);
        }
        else if (action.equals("취소")){
            order.setStatus(OrderStatus.CANCEL);
            orderRepository.save(order);
        }

        simpMessagingTemplate.convertAndSendToUser(user.getUsername(),"/topic/queue", getUnfinishedOrder(order.getUserId()));
    }

    public List<OrderDto> getUnbilledOrderOfSeat(Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new NoEntityException("해당하는 테이블이 존재하지 않습니다!");
        Seat seat = optionalSeat.get();

        List<Order> orderList = orderRepository.findOrdersByStatusNotBilledOrCancelAndSeat(seat);
        return orderList.stream().map(OrderDto::fromEntity).collect(Collectors.toList());
    }
}
