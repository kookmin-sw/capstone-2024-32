package com.example.WebOrder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Seat {

    @Id @GeneratedValue
    @Column(name = "seat_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

}
