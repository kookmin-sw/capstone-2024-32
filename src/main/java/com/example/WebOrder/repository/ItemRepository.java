package com.example.WebOrder.repository;

import com.example.WebOrder.entity.Item;
import com.example.WebOrder.entity.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByAdminIdAndStatus(Long adminId, ItemStatus status);

    Optional<Item> findTopByAdminIdOrderByOrderedCountDesc(Long ownerId);
    Optional<Item> findTopByAdminIdOrderByAvgRateDesc(Long ownerId);
}
