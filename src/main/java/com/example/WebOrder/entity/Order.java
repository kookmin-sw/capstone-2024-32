package com.example.WebOrder.entity;

import com.example.WebOrder.dto.ItemDto;
import com.example.WebOrder.dto.OrderItemDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    private Long userId;//가게 주인 Id

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime orderDateTime;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 [ORDER, PROGRESS, COMPLETE, BILLED]

    public List<OrderItemDto> getOrderItems() {
        List<OrderItemDto> dtoList = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
            dtoList.add(OrderItemDto.fromEntity(orderItem));
        }
        return dtoList;
    }

    // 연관관계 메서드
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // 생성 메서드
    public static Order createOrder(Seat seat, List<OrderItem> orderItems) {
        Order order = new Order();
        order.setSeat(seat);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setUserId(seat.getUser().getId());
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDateTime(LocalDateTime.now());
        return order;
    }

    // 비즈니스 로직
    /**
     * 주문 취소
     */
    public void cancel() {
        if (status == OrderStatus.PROGRESS || status == OrderStatus.COMPLETE) {
            throw new IllegalStateException("진행 중 또는 완료된 주문입니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
    }

    // 조회 로직

    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
