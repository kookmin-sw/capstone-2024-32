package com.example.WebOrder.dto;

import com.example.WebOrder.entity.Order;
import com.example.WebOrder.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private Long seatId;
    private String orderItems;
    private LocalDateTime orderedTime;
    private String orderStatus;

    public static OrderDto fromEntity(Order order){
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setSeatId(order.getSeat().getId());

        dto.setOrderedTime(order.getOrderDateTime());
        dto.setOrderStatus(order.getStatus().name());

        StringBuilder sb = new StringBuilder();
        for (OrderItem orderItem : order.getOrderItems()) {
            sb.append(orderItem.getItem().getName());
            sb.append(" ");
            sb.append(orderItem.getCount());
            sb.append("개 \n");
        }
        dto.setOrderItems(sb.toString());

        return dto;
    }
}
