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


    // 해당 좌석에서 주문 받은 횟수
    private Long orderedTime;

    public void increaseOrderedTime() {
        orderedTime += 1;
    }
}
