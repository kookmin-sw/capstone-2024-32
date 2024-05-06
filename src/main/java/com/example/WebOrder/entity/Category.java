package com.example.WebOrder.entity;

import com.example.WebOrder.dto.ItemDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Item> items = new ArrayList<>();

    private Long adminId;

    private CategoryStatus status;

    public List<ItemDto> getItems() {
        List<ItemDto> dtoList = new ArrayList<>();

        for (Item item : items) {
            if (item.getStatus() == ItemStatus.ACTIVE) {
                dtoList.add(ItemDto.fromEntity(item));
            }
        }
        return dtoList;
    }
}

