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
    private Integer avgRate;
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
        dto.setCategoryId(item.getCategory().getId());
        dto.setStatus(item.getStatus());

        return dto;
    }
}
