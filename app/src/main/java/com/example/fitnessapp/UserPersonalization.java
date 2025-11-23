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

import com.example.fitnessapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserPersonalization extends AppCompatActivity {

    EditText editAge, editWeight, editHeight;
    private String selectedGender = "";
    private String selectedGoal = "";
    private String selectedExperience = "";


    Button userpersonalcontinue;

    FirebaseFirestore db;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpersonalization);

        db = FirebaseFirestore.getInstance();

        userID = getIntent().getStringExtra("USER_ID");
        if (userID == null) {
            Toast.makeText(this, "No user data found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        editAge = findViewById(R.id.editAge);
        editWeight = findViewById(R.id.editWeight);
        editHeight = findViewById(R.id.editHeight);
        Spinner spinnerGender = findViewById(R.id.editspinnerGender);
        Spinner spinnerGoal = findViewById(R.id.editSpinnerGoal);
        Spinner spinnerExperience = findViewById(R.id.editSpinnerExperience);


        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
                selectedGender = adapter.getItemAtPosition(position).toString();
            }
            @Override public void onNothingSelected(AdapterView<?> adapter) {}
        });

        spinnerGoal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
                selectedGoal = adapter.getItemAtPosition(position).toString();
            }
            @Override public void onNothingSelected(AdapterView<?> adapter) {}
        });

        spinnerExperience.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
                selectedExperience = adapter.getItemAtPosition(position).toString();
            }
            @Override public void onNothingSelected(AdapterView<?> adapter) {}
        });



        userpersonalcontinue = findViewById(R.id.userpersonalcontinue);
        userpersonalcontinue.setOnClickListener(v-> personalizeAccount());

    }


    private void personalizeAccount(){
        Toast.makeText(this,"Updating account data...", Toast.LENGTH_SHORT).show();
        int age = Integer.parseInt(editAge.getText().toString().trim());
        int weight = Integer.parseInt(editWeight.getText().toString().trim());
        int height = Integer.parseInt(editHeight.getText().toString().trim());

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
                    Toast.makeText(this, "Personalization complete!", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(UserPersonalization.this, MenuActivity.class);
                    startActivity(i);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("FIRESTORE", "Update failed", e);
                });
    }
}
