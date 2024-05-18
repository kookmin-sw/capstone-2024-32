package com.example.WebOrder.service;

import com.example.WebOrder.dto.ItemDto;
import com.example.WebOrder.dto.OrderItemDto;
import com.example.WebOrder.dto.SeatDto;
import com.example.WebOrder.dto.SeatOrderDto;
import com.example.WebOrder.entity.*;
import com.example.WebOrder.exception.status4xx.ForbiddenException;
import com.example.WebOrder.exception.status4xx.NoEntityException;
import com.example.WebOrder.exception.status4xx.TypicalException;
import com.example.WebOrder.repository.OrderRepository;
import com.example.WebOrder.repository.SeatRepository;
import com.example.WebOrder.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ItemService itemService;
    private final UserRepository userRepository;

    public SeatService(SeatRepository seatRepository, OrderRepository orderRepository, LoginService loginService, OrderService orderService, SimpMessagingTemplate simpMessagingTemplate, ItemService itemService, UserRepository userRepository) {
        this.seatRepository = seatRepository;
        this.orderRepository = orderRepository;
        this.loginService = loginService;
        this.orderService = orderService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.itemService = itemService;
        this.userRepository = userRepository;
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
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new NoEntityException("해당하는 테이블이 존재하지 않습니다!");
        Seat seat = optionalSeat.get();

        // 검증하기
        if (!loginService.isCurrentUserAuthenticated(seat.getUser().getId())) throw new ForbiddenException("해당 작업을 할 권한이 존재하지 않습니다.");

        seatRepository.deleteById(seatId);
    }

    public Long updateSeatName(Long seatId, String seatName){
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new NoEntityException("해당하는 테이블이 존재하지 않습니다!");
        Seat seat = optionalSeat.get();

        // 검증하기
        if (!loginService.getCurrentUserEntity().getId().equals(seat.getUser().getId())) throw new ForbiddenException("해당 작업을 할 권한이 존재하지 않습니다.");

        seat.setName(seatName);
        return seatRepository.save(seat).getId();
    }

    // 테이블 비우기 (ex: 테이블 손님들이 음식을 먹지 않고 자리를 비우고 나간다거나...)
    // 테이블에 있는 주문들 전부 Cancel로 만든다.
    public void clearSeat(Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new NoEntityException("해당하는 테이블이 존재하지 않습니다!");
        Seat seat = optionalSeat.get();

        // 검증하기
        if (!loginService.getCurrentUserEntity().getId().equals(seat.getUser().getId())) throw new ForbiddenException("해당 작업을 할 권한이 존재하지 않습니다.");
        Optional<User> optionalUser = userRepository.findById(seat.getUser().getId());
        if (optionalUser.isEmpty()) throw new NoEntityException("잘못된 요청입니다!");
        User user = optionalUser.get();

        List<Order> orderList = orderRepository.findOrdersByStatusNotBilledOrCancelAndSeat(seat);

        for (Order order: orderList){
            order.setStatus(OrderStatus.CANCEL);
            orderRepository.save(order);
        }
        simpMessagingTemplate.convertAndSendToUser(user.getUsername(),"/topic/queue", orderService.getUnfinishedOrder(seat.getUser().getId()));
    }

    // 테이블 계산하기
    // 해당 테이블에 걸려 있는 계산 처리 안 된 주문들을 계산한다.
    public void makeSeatBilled(Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new NoEntityException("해당하는 테이블이 존재하지 않습니다!");
        Seat seat = optionalSeat.get();

        // 검증하기
        if (!loginService.getCurrentUserEntity().getId().equals(seat.getUser().getId())) throw new ForbiddenException("해당 작업을 할 권한이 존재하지 않습니다.");
        Optional<User> optionalUser = userRepository.findById(seat.getUser().getId());
        if (optionalUser.isEmpty()) throw new NoEntityException("잘못된 요청입니다!");
        User user = optionalUser.get();

        seat.increaseOrderedTime();
        seatRepository.save(seat);
        List<Order> orderList = orderRepository.findOrdersByStatusNotBilledOrCancelAndSeat(seat);

        for (Order order : orderList){
            order.setStatus(OrderStatus.BILLED);
            orderRepository.save(order);
        }
        simpMessagingTemplate.convertAndSendToUser(user.getUsername(),"/topic/queue", orderService.getUnfinishedOrder(seat.getUser().getId()));
    }

    // 현재 유저의 seat 중 가장 많이 주문을 받은 seat를 가져오기
    // '좌석이름 (주문횟수)' 의 형태로 리턴된다
    // 2개 이상의 공동 순위일 수도 있다. 이경우 id에 따라 한개만 보여진다.
    public String getMostOrderedSeatOfCurrentUser(){
        Optional<Seat> optionalTopSeat = seatRepository.findTopByUserOrderByOrderedTimeDesc(loginService.getCurrentUserEntity());
        if (optionalTopSeat.isEmpty()) return "테이블 없음";
        Seat topSeat = optionalTopSeat.get();

        // 검증하기
        if (!loginService.getCurrentUserEntity().getId().equals(topSeat.getUser().getId())) throw new ForbiddenException("해당 작업을 할 권한이 존재하지 않습니다.");

        return topSeat.getName() + " : " + topSeat.getOrderedTime() + "회";
    }

    // 현재 유저의 seat 중 가장 적게 주문을 받은 seat를 가져오기
    // '좌석이름 (주문횟수)' 의 형태로 리턴된다
    // 2개 이상의 공동 순위일 수도 있다. 이경우 id에 따라 한개만 보여진다.
    public String getLeastOrderedSeatOfCurrentUser(){
        Optional<Seat> optionalTopSeat = seatRepository.findTopByUserOrderByOrderedTimeAsc(loginService.getCurrentUserEntity());
        if (optionalTopSeat.isEmpty()) return "테이블 없음";
        Seat topSeat = optionalTopSeat.get();

        // 검증하기
        if (!loginService.getCurrentUserEntity().getId().equals(topSeat.getUser().getId())) throw new ForbiddenException("해당 작업을 할 권한이 존재하지 않습니다.");

        return topSeat.getName() + " : " + topSeat.getOrderedTime() + "회";
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
        //검증하기
        User user = loginService.getCurrentUserEntity();
        if (!seat.getUser().getId().equals(userId)) throw new TypicalException("잘못된 요청입니다.");
        if (!user.getId().equals(userId)) throw new ForbiddenException("권한이 없는 요청입니다.");

        SeatOrderDto dto = new SeatOrderDto();
        dto.setSeatId(seat.getId());
        dto.setSeatName(seat.getName());
        dto.setOwnerId(userId);
        List<Order> orderList = orderRepository.findOrdersByStatusNotBilledOrCancelAndSeat(seat);

        StringBuilder sb = new StringBuilder();
        int totalPrice = 0;
        // 해당 테이블에 걸려있는 주문들을 순회한다.
        for (Order order: orderList){
            List<OrderItemDto> orderItems = order.getOrderItems();

            // 주문 하나에 걸려있는 아이템들의 정보를 seatViewOrderedItemDto에 넣는다.
            for (OrderItemDto orderItem: orderItems){
                ItemDto itemDto = itemService.getItemInfoById(orderItem.getItemId());
                sb.append(itemDto.getName());
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
        if (optionalSeat.isEmpty()) throw new NoEntityException("해당하는 테이블이 존재하지 않습니다!");

        return optionalSeat.get().getName();
    }

    public SeatDto getSeatDto(Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new NoEntityException("해당하는 테이블이 존재하지 않습니다!");

        return SeatDto.fromEntity(optionalSeat.get());
    }


    public Seat getSeatEntity(Long seatId) {

        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new NoEntityException("해당하는 테이블이 존재하지 않습니다!");

        Seat seat = optionalSeat.get();
        //검증하기
        User user = loginService.getCurrentUserEntity();
        if (!seat.getUser().getId().equals(user.getId())) throw new TypicalException("잘못된 요청입니다.");


        return optionalSeat.get();
    }

    public Boolean seatExistsByUserIdAndSeatId(Long userId, Long seatId){
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isEmpty()) throw new NoEntityException("해당하는 테이블이 존재하지 않습니다!");
        Seat seat = optionalSeat.get();

        // 검증하기
        if (!seat.getUser().getId().equals(userId)) throw new TypicalException("테이블과 가게 정보가 일치하지 않습니다!");
        return true;
    }
}
