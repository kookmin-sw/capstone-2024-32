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
    private Long itemId;
    private String name;
    private String description;
    private String itemImageUrl;
    private int price;
    private Long ownerId;
    private Integer avgRate;
    private Long orderedCount;

    public static ItemDto fromEntity(Item item){
        ItemDto dto = new ItemDto();
        dto.setItemId(item.getId());
        dto.setName(item.getName());
        dto.setPrice(item.getPrice());
        dto.setDescription(item.getDescription());
        dto.setItemImageUrl(item.getItemImageUrl());
        dto.setOwnerId(item.getOwnerId());

        if (item.getReviews().size() != 0){
            int avgOfRate = 0;
            for (Review incReview : item.getReviews()){
                avgOfRate += incReview.getRate();
            }
            avgOfRate /= item.getReviews().size();
            dto.setAvgRate(avgOfRate);
        }
        else
            dto.setAvgRate(0);


        dto.setOrderedCount(item.getOrderedCount());

        return dto;
    }
}
