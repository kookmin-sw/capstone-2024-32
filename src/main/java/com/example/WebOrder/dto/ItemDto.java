package com.example.WebOrder.dto;

import com.example.WebOrder.entity.Item;
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
    private String name;
    private String description;
    private String itemImageUrl;
    private int price;
    private Long ownerId;
    private Integer avgRate;
    private Long orderedCount;

    public static ItemDto fromEntity(Item item){
        ItemDto dto = new ItemDto();
        dto.setName(item.getName());
        dto.setPrice(item.getPrice());
        dto.setDescription(item.getDescription());
        dto.setItemImageUrl(item.getItemImageUrl());
        dto.setOwnerId(item.getOwnerId());

        int avgOfRate = 0;
        for (Review incReview : item.getReviews()){
            avgOfRate += incReview.getRate();
        }
        avgOfRate /= item.getReviews().size();
        dto.setAvgRate(avgOfRate);

        dto.setOrderedCount(item.getOrderedCount());

        return dto;
    }
}
