package com.example.WebOrder.service;

import com.example.WebOrder.entity.Order;
import com.example.WebOrder.entity.OrderItem;
import com.example.WebOrder.entity.Seat;
import com.example.WebOrder.repository.OrderRepository;
import com.example.WebOrder.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Long addSeat(Long userId, String seatName) {
        //검증
        if (!loginService.isCurrentUserAuthenticated(userId)) throw new RuntimeException("권한 없음");

        Seat seat = new Seat();

        seat.setName(seatName);
        return seatRepository.save(seat).getId();
    }

    @Transactional
    public void deleteSeat(Long userId, Long seatId) {
        if (!loginService.isCurrentUserAuthenticated(userId)) throw new RuntimeException("권한 없음");
        seatRepository.deleteById(seatId);
    }

    public void clearSeat(Long userId, Long seatId) {
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

    public void generateSeatQRCode(Long userId, Long seatId){
        throw new RuntimeException("미구현");
    }
}
