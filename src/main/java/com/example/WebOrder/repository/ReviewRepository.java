package com.example.WebOrder.repository;

import com.example.WebOrder.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByItemId(Long itemId, Pageable pageable);
    @Query("SELECT ROUND(AVG(r.rate), 1) FROM Review r WHERE r.item.id = :itemId")
    Double findAverageRateByItemId(@Param("itemId") Long itemId);

}
