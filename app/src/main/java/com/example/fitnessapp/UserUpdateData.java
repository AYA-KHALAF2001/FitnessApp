package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserUpdateData extends AppCompatActivity {

    EditText editAge, editWeight, editHeight;
    Spinner spinnerGender, spinnerGoal, spinnerExperience;

    private String selectedGender = "";
    private String selectedGoal = "";
    private String selectedExperience = "";

    Button userpersonalcontinue, ButtonReturn;

    FirebaseFirestore db;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_data);

        db = FirebaseFirestore.getInstance();

        userID = getIntent().getStringExtra("USER_ID");
        if (userID == null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        if (userID == null) {
            Toast.makeText(this, "No user data found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        editAge = findViewById(R.id.editAge);
        editWeight = findViewById(R.id.editWeight);
        editHeight = findViewById(R.id.editHeight);

        spinnerGender = findViewById(R.id.editspinnerGender);
        spinnerGoal = findViewById(R.id.editSpinnerGoal);
        spinnerExperience = findViewById(R.id.editSpinnerExperience);

        loadExistingUserData();

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
                selectedGender = adapter.getItemAtPosition(position).toString();
            }
            @Override public void onNothingSelected(AdapterView<?> adapter) {}
        });

        spinnerGoal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
                selectedGoal = adapter.getItemAtPosition(position).toString();
            }
            @Override public void onNothingSelected(AdapterView<?> adapter) {}
        });

        spinnerExperience.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
                selectedExperience = adapter.getItemAtPosition(position).toString();
            }
            @Override public void onNothingSelected(AdapterView<?> adapter) {}
        });

        userpersonalcontinue = findViewById(R.id.userpersonalcontinue);
        userpersonalcontinue.setOnClickListener(v -> personalizeAccount());

        userpersonalcontinue = findViewById(R.id.userpersonalcontinue);
        ButtonReturn = findViewById(R.id.returnButton);

        userpersonalcontinue.setOnClickListener(v -> personalizeAccount());

        ButtonReturn.setOnClickListener(v ->
                startActivity(new Intent(UserUpdateData.this, MainActivity.class))
        );
    }

    private void loadExistingUserData() {
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    try {
                        if (doc.getLong("age") != null)
                            editAge.setText(String.valueOf(doc.getLong("age")));

                        if (doc.getLong("weight") != null)
                            editWeight.setText(String.valueOf(doc.getLong("weight")));

                        if (doc.getLong("height") != null)
                            editHeight.setText(String.valueOf(doc.getLong("height")));

                        if (doc.getString("gender") != null)
                            setSpinnerSelection(spinnerGender, doc.getString("gender"));

                        if (doc.getString("goal") != null)
                            setSpinnerSelection(spinnerGoal, doc.getString("goal"));

                        if (doc.getString("experience") != null)
                            setSpinnerSelection(spinnerExperience, doc.getString("experience"));

                    } catch (Exception e) {
                        Log.e("UPDATE_DATA", "Prefill error", e);
                    }
                })
                .addOnFailureListener(e -> Log.e("UPDATE_DATA", "Failed to load existing data", e));
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void personalizeAccount() {

        String ageText = editAge.getText().toString().trim();
        String weightText = editWeight.getText().toString().trim();
        String heightText = editHeight.getText().toString().trim();

        if (ageText.isEmpty() || weightText.isEmpty() || heightText.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int age = Integer.parseInt(ageText);
            int weight = Integer.parseInt(weightText);
            int height = Integer.parseInt(heightText);

            if (age > 100 || age < 10) {
                Toast.makeText(this, "Please enter a valid age.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (weight > 700 || weight < 50) {
                Toast.makeText(this, "Please enter a valid weight.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (height > 260 || height < 100) {
                Toast.makeText(this, "Please enter a valid height.", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("age", age);
            updates.put("weight", weight);
            updates.put("height", height);
            updates.put("gender", selectedGender);
            updates.put("goal", selectedGoal);
            updates.put("experience", selectedExperience);

            db.collection("users").document(userID)
                    .update(updates)
                    .addOnSuccessListener(v -> {
                        Toast.makeText(this, "Updates Complete!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("FIRESTORE", "Update failed", e);
                    });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Numbers only please!", Toast.LENGTH_SHORT).show();
        }
    }
}
