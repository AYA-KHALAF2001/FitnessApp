package com.example.fitnessapp.models;

public class Meal {

    private String name;
    private int calories;
    private int protein;
    private int carbs;
    private int fat;
    private String description; // optional if you want to add later

    public Meal() {
        // empty constructor for JSON / Firestore
    }

    public Meal(String name, int calories, int protein, int carbs, int fat) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.description = "";
    }

    public Meal(String name, int calories, int protein, int carbs, int fat, String description) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getCalories() {
        return calories;
    }

    public int getProtein() {
        return protein;
    }

    public int getCarbs() {
        return carbs;
    }

    public int getFat() {
        return fat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
