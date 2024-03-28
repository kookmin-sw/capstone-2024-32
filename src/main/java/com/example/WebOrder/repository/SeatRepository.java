package com.example.WebOrder.repository;

import com.example.WebOrder.entity.Seat;
import com.example.WebOrder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findTopByUserOrderByOrderedTimeDesc(User user);
    Optional<Seat> findTopByUserOrderByOrderedTimeAsc(User user);

    Integer countByUser(User user);

    List<Seat> findAllByUser(User user);
}
