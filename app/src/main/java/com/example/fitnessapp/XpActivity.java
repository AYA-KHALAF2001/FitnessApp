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

public class XpActivity extends AppCompatActivity {

    TextView xpLevelText, xpValueText, xpStreakText, xpRankText;
    ProgressBar xpProgressBar;

    FirebaseFirestore db;
    FirebaseAuth auth;
    String userID;

    Button Returnmainbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xpactivity);

        xpLevelText   = findViewById(R.id.xpLevelText);
        xpValueText   = findViewById(R.id.xpValueText);
        xpStreakText  = findViewById(R.id.xpStreakText);
        xpRankText    = findViewById(R.id.xpRankText);
        xpProgressBar = findViewById(R.id.xpProgressBar);
        Returnmainbutton = findViewById(R.id.returnButton);


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();

        loadUserData();
        Returnmainbutton.setOnClickListener(v -> startActivity(new Intent(XpActivity.this, MainActivity.class)));
    }

    private void loadUserData() {
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    long xp     = doc.getLong("xp") != null ? doc.getLong("xp") : 0;
                    long level  = doc.getLong("level") != null ? doc.getLong("level") : 1;
                    long streak = doc.getLong("current_streak") != null ? doc.getLong("current_streak") : 0;

                    int xpCap = (int) (level * 100);

                    xpProgressBar.setMax(xpCap);
                    xpProgressBar.setProgress((int) xp);

                    xpValueText.setText(xp + " / " + xpCap + " XP");
                    xpLevelText.setText("Level " + level);
                    xpStreakText.setText("Current Streak: " + streak + " days");

                    // RANK
                    String rank = getRankForLevel(level);
                    xpRankText.setText("Rank: " + rank);

                })
                .addOnFailureListener(e -> Log.e("XP", "Failed to load XP data", e));
    }

    private String getRankForLevel(long level) {
        if (level < 5) return "Starter";
        if (level < 10) return "Crew";
        if (level < 15) return "Elite";
        if (level < 20) return "Prestige";
        return "Leader";
    }
}
