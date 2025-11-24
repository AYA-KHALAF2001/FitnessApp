package com.example.fitnessapp.models;

public class Exercise {
    private String exercise;
    private int sets;
    private String reps;
    private String rest;

    public Exercise(String exercise, int sets, String reps, String rest) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
        this.rest = rest;
    }

    public String getExercise() {
        return exercise;
    }

    public int getSets() {
        return sets;
    }

    public String getReps() {
        return reps;
    }

    public String getRest() {
        return rest;
    }
}
