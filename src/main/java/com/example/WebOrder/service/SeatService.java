package com.example.WebOrder.service;

import com.example.WebOrder.dto.SeatDto;
import com.example.WebOrder.entity.Order;
import com.example.WebOrder.entity.OrderItem;
import com.example.WebOrder.entity.Seat;
import com.example.WebOrder.repository.OrderRepository;
import com.example.WebOrder.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeatService {
    private final SeatRepository seatRepository;
    private final OrderRepository orderRepository;
    private final LoginService loginService;

    public SeatService(SeatRepository seatRepository, OrderRepository orderRepository, LoginService loginService) {
        this.seatRepository = seatRepository;
        this.orderRepository = orderRepository;
        this.loginService = loginService;
    }

    public Long addSeat(String seatName) {

        Seat seat = new Seat();

        seat.setName(seatName);
        seat.setOrderedTime(0L);
        return seatRepository.save(seat).getId();
    }

    @Transactional
    public void deleteSeat(Long seatId) {
        seatRepository.deleteById(seatId);
    }

    public Long updateSeat(Long seatId, String seatName){
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new RuntimeException("엔티티 없음");
        Seat seat = optionalSeat.get();

        seat.setName(seatName);
        return seatRepository.save(seat).getId();
    }

    public void clearSeat(Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new RuntimeException("엔티티 없음");
        Seat seat = optionalSeat.get();

        for (Order order: seat.getOrders()){
            orderRepository.deleteById(order.getId());
        }
        seat.getOrders().clear();
        seatRepository.save(seat);
    }

    public int getSeatBill(Long userId, Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new RuntimeException("엔티티 없음");

        Seat seat = optionalSeat.get();

        int bill = 0;
        for (Order order : seat.getOrders()){
            for (OrderItem orderItem : order.getOrderItems()){
                bill += orderItem.getOrderPrice();
            }
        }

        return bill;
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

    // 현재 유저의 모든 seat 개수를 가져오기
    public Integer getTotalSeatNum() {
        return seatRepository.countByUser(loginService.getCurrentUserEntity());
    }

}
