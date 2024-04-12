package com.example.WebOrder.repository;

import com.example.WebOrder.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long ownerId);

    Optional<Item> findTopByOwnerIdOrderByOrderedCountDesc(Long ownerId);
    Optional<Item> findTopByOwnerIdOrderByAvgRateDesc(Long ownerId);
}
