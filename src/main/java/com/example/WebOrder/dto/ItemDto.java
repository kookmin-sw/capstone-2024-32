package com.example.WebOrder.dto;

import com.example.WebOrder.entity.Category;
import com.example.WebOrder.entity.Item;
import com.example.WebOrder.entity.ItemStatus;
import com.example.WebOrder.entity.Review;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemDto {
    private Long itemId;
    private String name;
    private String description;
    private String itemImageUrl;
    private int price;
    private Long adminId;
    private Double avgRate;
    private Long orderedCount;
    private Long categoryId;
    private ItemStatus status;

    public static ItemDto fromEntity(Item item){
        ItemDto dto = new ItemDto();
        dto.setItemId(item.getId());
        dto.setName(item.getName());
        dto.setPrice(item.getPrice());
        dto.setDescription(item.getDescription());
        dto.setItemImageUrl(item.getItemImageUrl());
        dto.setAdminId(item.getAdminId());
        dto.setAvgRate(item.getAvgRate());


        dto.setOrderedCount(item.getOrderedCount());
        dto.setCategoryId(item.getCategory().getId());
        dto.setStatus(item.getStatus());

        return dto;
    }
}
