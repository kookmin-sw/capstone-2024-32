package com.example.WebOrder.dto;

import com.example.WebOrder.entity.Category;
import com.example.WebOrder.entity.Review;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CategoryDto {
    private Long categoryId;
    private String name;
    private Long adminId;

    public static CategoryDto fromEntity(Category category){
        CategoryDto dto = new CategoryDto();
        dto.setCategoryId(category.getId());
        dto.setName(category.getName());
        dto.setAdminId(category.getAdminId());
        return dto;
    }
}
