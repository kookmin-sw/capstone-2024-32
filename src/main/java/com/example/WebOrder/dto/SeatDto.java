package com.example.WebOrder.dto;

import com.example.WebOrder.entity.Order;
import com.example.WebOrder.entity.OrderItem;
import com.example.WebOrder.entity.Seat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SeatDto {
    private Long seatId;
    private String name;
    private String orders;

    public static SeatDto fromEntity(Seat seat){
        SeatDto dto = new SeatDto();
        dto.setSeatId(seat.getId());
        dto.setName(seat.getName());

        return dto;
    }
}
