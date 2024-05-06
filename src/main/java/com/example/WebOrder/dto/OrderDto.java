package com.example.WebOrder.dto;

import com.example.WebOrder.entity.Order;
import com.example.WebOrder.entity.OrderItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private Long seatId;
    private List<OrderItemDto> orderItems;
    private LocalDateTime orderDateTime;
    private String orderStatus;

    public String seatName;


    public static OrderDto fromEntity(Order order){
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setSeatId(order.getSeat().getId());

        dto.setOrderDateTime(order.getOrderDateTime());
        dto.setOrderStatus(order.getStatus().name());
        dto.setSeatName(order.getSeat().getName());
        dto.setOrderItems(order.getOrderItems());

        return dto;
    }
}
