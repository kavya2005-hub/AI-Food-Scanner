package com.ai.food.detection.model;

import jakarta.persistence.*;

@Entity
@Table(name = "food_report")
public class FoodReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String food;
    private String category;
    private Double calories;
    private Double fat;
    private Double protein;
    private Double sugar;
    private Double fiber;
    private Double carbs;

    public Long getId()           { return id; }
    public String getFood()       { return food; }
    public String getCategory()   { return category; }
    public Double getCalories()   { return calories; }
    public Double getFat()        { return fat; }
    public Double getProtein()    { return protein; }
    public Double getSugar()      { return sugar; }
    public Double getFiber()      { return fiber; }
    public Double getCarbs()      { return carbs; }

    public void setId(Long id)             { this.id = id; }
    public void setFood(String food)       { this.food = food; }
    public void setCategory(String c)      { this.category = c; }
    public void setCalories(Double v)      { this.calories = v; }
    public void setFat(Double v)           { this.fat = v; }
    public void setProtein(Double v)       { this.protein = v; }
    public void setSugar(Double v)         { this.sugar = v; }
    public void setFiber(Double v)         { this.fiber = v; }
    public void setCarbs(Double v)         { this.carbs = v; }
}
