package com.example.WebOrder.repository;

import com.example.WebOrder.entity.Order;
import com.example.WebOrder.entity.OrderStatus;
import com.example.WebOrder.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findOrdersByUserIdAndStatus(Long userId, OrderStatus status);
    //status OR문으로 Select해서 가져오는 메소드
    List<Order> findOrdersByUserIdAndStatusIn(Long userId, List<OrderStatus> statuses);

    
    //특정 테이블의 CANCEL이나 BILLED 가 아닌 (ORDER, PROGRESS, COMPLETE)인 상태의 주문 가져오기
    // 테이블 현황에서 현재 해당 테이블이 지금까지 주문한 내역을 보기 위한 메소드
    @Query("SELECT o FROM Order o WHERE o.status <> 'BILLED' AND o.status <> 'CANCEL' AND o.seat = :seat")
    List<Order> findOrdersByStatusNotBilledOrCancelAndSeat(@Param("seat") Seat seat);
}
