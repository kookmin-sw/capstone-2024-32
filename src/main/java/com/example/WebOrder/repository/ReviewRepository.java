package com.example.WebOrder.repository;

import com.example.WebOrder.entity.Item;
import com.example.WebOrder.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByItemId(Long itemId);
}
