package com.ai.food.detection.repository;

import com.ai.food.detection.model.FoodReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodReportRepository extends JpaRepository<FoodReport, Long> {

    @Query("SELECT f FROM FoodReport f WHERE LOWER(f.food) = LOWER(:name)")
    Optional<FoodReport> findByFoodIgnoreCase(@Param("name") String name);
}
