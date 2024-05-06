package com.example.WebOrder.repository;

import com.example.WebOrder.entity.Category;
import com.example.WebOrder.entity.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByAdminIdAndStatus(Long adminId, CategoryStatus status);
}
