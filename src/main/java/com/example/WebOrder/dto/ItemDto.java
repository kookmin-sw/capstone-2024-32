package com.example.WebOrder.dto;

import com.example.WebOrder.entity.Item;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemDto {
    private String name;
    private String description;
    private String itemImageUrl;
    private int price;
    private Long ownerId;

    public static ItemDto fromEntity(Item item){
        ItemDto dto = new ItemDto();
        dto.setName(item.getName());
        dto.setPrice(item.getPrice());
        dto.setDescription(item.getDescription());
        dto.setItemImageUrl(item.getItemImageUrl());
        dto.setOwnerId(item.getOwnerId());

        return dto;
    }
}
