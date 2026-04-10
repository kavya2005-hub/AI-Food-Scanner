package com.ai.food.detection.repository;

import com.ai.food.detection.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    Food findByFoodIgnoreCase(String food);
}