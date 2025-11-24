package com.example.fitnessapp.models;

import java.util.List;

public class DayPlan {

    public String day;
    public List<Exercise> workout;
    public Meal meal;
    public boolean completed;

    public DayPlan() {
        // Required for Firestore
    }

    public DayPlan(String dayName, List<Exercise> exercises, Meal meal) {
        this.day = dayName;
        this.workout = exercises;
        this.meal = meal;
        this.completed = false;
    }

    public String getDay() {
        return day;
    }

    public List<Exercise> getWorkout() {
        return workout;
    }

    public Meal getMeal() {
        return meal;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
