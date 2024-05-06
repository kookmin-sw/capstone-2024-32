package com.example.WebOrder.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatOrderDto {
    private Long seatId;
    private String seatName;

    private Long ownerId;

    private String orderedItems;
    private Integer totalPrice;
}
