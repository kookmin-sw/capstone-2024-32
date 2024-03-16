package com.example.WebOrder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String description;

    private String itemImageUrl;

    private int price;




}
