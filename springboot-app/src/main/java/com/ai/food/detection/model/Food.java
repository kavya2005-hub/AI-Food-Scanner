package com.ai.food.detection.model;

import jakarta.persistence.*;

@Entity
@Table(name = "food")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String food;
    private int calories;
    private String protein;
    private String fat;
    private String fiber;
    private String sugar;

    @Column(name = "health_effect")
    private String healthEffect;

    @Column(name = "disease_risk")
    private String diseaseRisk;

    // GETTERS & SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFood() { return food; }
    public void setFood(String food) { this.food = food; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public String getProtein() { return protein; }
    public void setProtein(String protein) { this.protein = protein; }

    public String getFat() { return fat; }
    public void setFat(String fat) { this.fat = fat; }

    public String getFiber() { return fiber; }
    public void setFiber(String fiber) { this.fiber = fiber; }

    public String getSugar() { return sugar; }
    public void setSugar(String sugar) { this.sugar = sugar; }

    public String getHealthEffect() { return healthEffect; }
    public void setHealthEffect(String healthEffect) { this.healthEffect = healthEffect; }

    public String getDiseaseRisk() { return diseaseRisk; }
    public void setDiseaseRisk(String diseaseRisk) { this.diseaseRisk = diseaseRisk; }
}