package com.example.fitnessapp.models;

public class Exercise {
    public String exercise;
    public int sets;
    public String reps;
    public String rest;

    public Exercise(String exercise, int sets, String reps, String rest) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
        this.rest = rest;
    }
}