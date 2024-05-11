package com.example.WebOrder.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MenuStatisticsDto {
    private Long userId;

    //가장 별점이 좋은 메뉴
    private String bestRateMenu;
    private Double bestRateNum;


    //가장 많이 주문된 메뉴
    private String mostOrderedMenu;
    private Long mostOrderedNum;
}
