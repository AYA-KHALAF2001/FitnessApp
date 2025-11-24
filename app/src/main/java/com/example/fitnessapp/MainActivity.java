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

    Button buttonWorkouts, buttonRank, buttonUserUpdateData;

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
        buttonRank = findViewById(R.id.buttonRank);
        buttonUserUpdateData = findViewById(R.id.buttonUserUpdateData);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();

        db.collection("users").document(userID)
                .addSnapshotListener((doc, error) -> {
                    if (error != null || doc == null || !doc.exists()) return;

                    long xp = doc.getLong("xp") != null ? doc.getLong("xp") : 0;
                    long level = doc.getLong("level") != null ? doc.getLong("level") : 1;
                    long streak = doc.getLong("current_streak") != null ? doc.getLong("current_streak") : 0;

                    long required = level * 100;

                    if (xp >= required) {
                        xp = 0;
                        level++;
                        db.collection("users").document(userID)
                                .update("xp", xp, "level", level);
                    }

                    int xpCap = (int) (level * 100);

                    xpProgress.setMax(xpCap);
                    xpProgress.setProgress((int) xp);

                    xpText.setText(xp + " / " + xpCap + " XP");
                    levelText.setText("Level " + level);
                    streakText.setText("Current Streak: " + streak + " days");
                });

        buttonWorkouts.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, WorkoutListActivity.class)));

        buttonRank.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, XpActivity.class)));

        buttonUserUpdateData.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, UserUpdateData.class);
            i.putExtra("USER_ID", userID);
            startActivity(i);
        });
    }
}
