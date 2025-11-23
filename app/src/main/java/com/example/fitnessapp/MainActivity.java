package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    TextView streakText, xpText, levelText;
    ProgressBar xpProgress;

    Button buttonWorkouts, buttonMeals, buttonWeekPlan;

    FirebaseFirestore db;
    FirebaseAuth auth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        streakText = findViewById(R.id.streakText);
        xpText = findViewById(R.id.xpText);
        levelText = findViewById(R.id.levelText);
        xpProgress = findViewById(R.id.xpProgress);

        buttonWorkouts = findViewById(R.id.buttonWorkouts);
        buttonMeals = findViewById(R.id.buttonMeals);
        buttonWeekPlan = findViewById(R.id.buttonWeekPlan);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();

        loadUserData();

        buttonWorkouts.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, WorkoutListActivity.class)));

        buttonMeals.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MealPlanActivity.class)));

        buttonWeekPlan.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, WorkoutListActivity.class))); // same weekly plan
    }

    private void loadUserData() {
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    long xp = doc.getLong("xp") != null ? doc.getLong("xp") : 0;
                    long level = doc.getLong("level") != null ? doc.getLong("level") : 1;
                    long streak = doc.getLong("current_streak") != null ? doc.getLong("current_streak") : 0;

                    int xpCap = (int) (level * 100);

                    xpProgress.setMax(xpCap);
                    xpProgress.setProgress((int) xp);

                    xpText.setText(xp + " / " + xpCap + " XP");
                    levelText.setText("Level " + level);
                    streakText.setText("Current Streak: " + streak + " days");
                })
                .addOnFailureListener(e -> Log.e("MAIN", "Failed to load user data", e));
    }
}
