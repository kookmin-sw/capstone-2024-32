package com.example.WebOrder.dto;

import com.example.WebOrder.entity.Category;
import com.example.WebOrder.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {
    private Long itemId;
    private String name;
    private int count;
    private int totalPrice;

    public static OrderItemDto fromEntity(OrderItem orderItem){
        OrderItemDto dto = new OrderItemDto();
        dto.setItemId(orderItem.getId());
        dto.setName(orderItem.getItem().getName());
        dto.setCount(orderItem.getCount());
        dto.setTotalPrice(orderItem.getTotalPrice());
        return dto;
    }
}
