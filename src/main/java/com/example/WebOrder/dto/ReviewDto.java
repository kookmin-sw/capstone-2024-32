package com.example.WebOrder.dto;

import com.example.WebOrder.entity.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {

    private Long itemId;
    private String comment;

    @Max(value = 5)
    @Min(value = 0)
    private Integer rate;

    public static ReviewDto fromEntity(Review review){
        ReviewDto dto = new ReviewDto();
        dto.setRate(review.getRate());
        dto.setComment(review.getComment());
        return dto;
    }
}
