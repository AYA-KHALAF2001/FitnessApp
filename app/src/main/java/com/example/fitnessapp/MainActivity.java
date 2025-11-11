package com.example.fitnessapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declare your UI elements
    TextView streakText;
    Button workoutBtn, recipesBtn, progressBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link activity_main.XML components to Java
        streakText = findViewById(R.id.streakText);
        workoutBtn = findViewById(R.id.workoutBtn);
        recipesBtn = findViewById(R.id.recipesBtn);
        progressBtn = findViewById(R.id.progressBtn);

        // Button actions
        workoutBtn.setOnClickListener(v -> streakText.setText("Opening Workout Log"));
        recipesBtn.setOnClickListener(v -> streakText.setText("Opening Recipes"));
        progressBtn.setOnClickListener(v -> streakText.setText("Opening Progress Tracker"));
    }
}
