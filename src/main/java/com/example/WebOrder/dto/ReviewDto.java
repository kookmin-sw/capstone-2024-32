package com.example.WebOrder.dto;

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
}
