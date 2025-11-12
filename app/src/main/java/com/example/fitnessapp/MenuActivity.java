package com.example.fitnessapp;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    Button workoutBtn , recipesBtn , progressBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Link activity_menu.XML components to Java
        workoutBtn = findViewById(R.id.workoutBtn);
        recipesBtn = findViewById(R.id.recipesBtn);
        progressBtn = findViewById(R.id.progressBtn);
    }
}
