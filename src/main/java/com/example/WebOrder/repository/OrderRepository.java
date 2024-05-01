package com.example.WebOrder.repository;

import com.example.WebOrder.entity.Order;
import com.example.WebOrder.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findOrdersByUserIdAndStatus(Long userId, OrderStatus status);
    //status OR문으로 Select해서 가져오는 메소드
    List<Order> findOrdersByUserIdAndStatusIn(Long userId, List<OrderStatus> statuses);
}
