package com.example.WebOrder.service;

import com.example.WebOrder.dto.SeatDto;
import com.example.WebOrder.dto.SeatOrderDto;
import com.example.WebOrder.entity.*;
import com.example.WebOrder.repository.OrderRepository;
import com.example.WebOrder.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// SeatDto는 주문 정보를 포함하지 않는 정말 테이블 정보만을 가져온다.
// SeatOrderDto는 주문 정보를 포함한 테이블 정보이다. 이때 주문 정보는 String 형태이다.
@Service
@Slf4j
public class SeatService {
    private final SeatRepository seatRepository;
    private final OrderRepository orderRepository;
    private final LoginService loginService;
    private final OrderService orderService;

    public SeatService(SeatRepository seatRepository, OrderRepository orderRepository, LoginService loginService, OrderService orderService) {
        this.seatRepository = seatRepository;
        this.orderRepository = orderRepository;
        this.loginService = loginService;
        this.orderService = orderService;
    }

    public Long addSeat(String seatName) {

        Seat seat = new Seat();

        seat.setName(seatName);
        seat.setOrderedTime(0L);
        seat.setUser(loginService.getCurrentUserEntity());
        return seatRepository.save(seat).getId();
    }

    @Transactional
    public void deleteSeat(Long seatId) {
        seatRepository.deleteById(seatId);
    }

    public Long updateSeatName(Long seatId, String seatName){
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new RuntimeException("엔티티 없음");
        Seat seat = optionalSeat.get();

        seat.setName(seatName);
        return seatRepository.save(seat).getId();
    }

    // 테이블 비우기 (ex: 테이블 손님들이 음식을 먹지 않고 자리를 비우고 나간다거나...)
    // 테이블에 있는 주문들 전부 Cancel로 만든다.
    public void clearSeat(Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new RuntimeException("엔티티 없음");
        Seat seat = optionalSeat.get();
        List<Order> orderList = orderRepository.findOrdersByStatusNotBilledOrCancelAndSeat(seat);

        for (Order order: orderList){
            order.setStatus(OrderStatus.CANCEL);
            orderRepository.save(order);
        }
    }

    // 테이블 계산하기
    // 해당 테이블에 걸려 있는 계산 처리 안 된 주문들을 계산한다.
    public void makeSeatBilled(Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new RuntimeException("엔티티 없음");

        Seat seat = optionalSeat.get();
        List<Order> orderList = orderRepository.findOrdersByStatusNotBilledOrCancelAndSeat(seat);

        for (Order order : orderList){
            order.setStatus(OrderStatus.BILLED);
            orderRepository.save(order);
        }
    }

    // 현재 유저의 seat 중 가장 많이 주문을 받은 seat를 가져오기
    // '좌석이름 (주문횟수)' 의 형태로 리턴된다
    // 2개 이상의 공동 순위일 수도 있다. 이경우 id에 따라 한개만 보여진다.
    public String getMostOrderedSeatOfCurrentUser(){
        Optional<Seat> optionalTopSeat = seatRepository.findTopByUserOrderByOrderedTimeDesc(loginService.getCurrentUserEntity());
        if (optionalTopSeat.isEmpty()) return "테이블 없음";
        Seat topSeat = optionalTopSeat.get();

        return topSeat.getName() + " (" + topSeat.getOrderedTime() + ") ";
    }

    // 현재 유저의 seat 중 가장 적게 주문을 받은 seat를 가져오기
    // '좌석이름 (주문횟수)' 의 형태로 리턴된다
    // 2개 이상의 공동 순위일 수도 있다. 이경우 id에 따라 한개만 보여진다.
    public String getLeastOrderedSeatOfCurrentUser(){
        Optional<Seat> optionalTopSeat = seatRepository.findTopByUserOrderByOrderedTimeAsc(loginService.getCurrentUserEntity());
        if (optionalTopSeat.isEmpty()) return "테이블 없음";
        Seat topSeat = optionalTopSeat.get();

        return topSeat.getName() + " (" + topSeat.getOrderedTime() + ") ";
    }
    
    // userId를 가진 user의 SeatDto 리스트 가져오기 (주문 내역은 볼 수 없음!)
    public List<SeatDto> getBasicSeatListOfCurrentUser(){
        List<Seat> seatList = seatRepository.findAllByUser(loginService.getCurrentUserEntity());

        return seatList.stream().map(SeatDto::fromEntity).collect(Collectors.toList());
    }

    // 전체 테이블 현황 표를 List 형태로 가져온다.
    // 주문 내역을 String 형태로 포함한다.
    public List<SeatOrderDto> getWholeSeatOrdersOfCurrentUser(){
        User user = loginService.getCurrentUserEntity();

        List<Seat> seatList = seatRepository.findAllByUser(user);
        List<SeatOrderDto> dtoList = new ArrayList<>();

        for (Seat seat: seatList){
            dtoList.add(getSeatOrdersOfSeat(user.getId(), seat));
        }
        return dtoList;
    }


    // 특정 테이블의 현황을 가져온다.
    public SeatOrderDto getSeatOrdersOfSeat(Long userId, Seat seat){
        SeatOrderDto dto = new SeatOrderDto();
        dto.setSeatId(seat.getId());
        dto.setSeatName(seat.getName());
        dto.setOwnerId(userId);
        List<Order> orderList = orderRepository.findOrdersByStatusNotBilledOrCancelAndSeat(seat);

        StringBuilder sb = new StringBuilder();
        int totalPrice = 0;
        // 해당 테이블에 걸려있는 주문들을 순회한다.
        for (Order order: orderList){
            List<OrderItem> orderItems = order.getOrderItems();

            // 주문 하나에 걸려있는 아이템들의 정보를 seatViewOrderedItemDto에 넣는다.
            for (OrderItem orderItem: orderItems){
                sb.append(orderItem.getItem().getName());
                sb.append(" X ");
                sb.append(orderItem.getCount());
                sb.append(" = ");
                sb.append(orderItem.getTotalPrice());
                sb.append("\n");
                totalPrice += orderItem.getTotalPrice();
            }
        }
        dto.setOrderedItems(sb.toString());
        dto.setTotalPrice(totalPrice);

        return dto;
    }


    // 현재 유저의 모든 seat 개수를 가져오기
    public Integer getTotalSeatNum() {
        return seatRepository.countByUser(loginService.getCurrentUserEntity());
    }

    public String getSeatName(Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new RuntimeException("엔티티없음");

        return optionalSeat.get().getName();
    }

    public SeatDto getSeatDto(Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new RuntimeException("엔티티없음");

        return SeatDto.fromEntity(optionalSeat.get());
    }


    public Seat getSeatEntity(Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new RuntimeException("엔티티없음");

        return optionalSeat.get();
    }
}
