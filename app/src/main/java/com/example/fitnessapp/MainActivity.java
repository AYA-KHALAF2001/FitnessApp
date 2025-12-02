package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    TextView streakText, xpText, levelText;
    ProgressBar xpProgress;

    FirebaseFirestore db;
    FirebaseAuth auth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        streakText = findViewById(R.id.streakText);
        xpText = findViewById(R.id.xpText);
        levelText = findViewById(R.id.levelText);
        xpProgress = findViewById(R.id.xpProgress);


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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
        }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_workout) {
            startActivity(new Intent(MainActivity.this, WorkoutListActivity.class));
            return true;
        }

        if (id == R.id.menu_update_user) {
            Intent i = new Intent(MainActivity.this, UserUpdateData.class);
            i.putExtra("USER_ID", userID);
            startActivity(i);
            return true;
        }

        if (id == R.id.menu_progress) {
            startActivity(new Intent(MainActivity.this, XpActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

