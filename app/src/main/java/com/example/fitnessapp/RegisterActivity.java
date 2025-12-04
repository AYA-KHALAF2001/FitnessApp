package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnessapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    EditText editUsername, editEmail, editPassword;
    Button buttonRegister;
// firebase
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonRegister.setOnClickListener(v -> createAccount());
    }

    private void createAccount() {
        Toast.makeText(this, "Starting account creation...", Toast.LENGTH_SHORT).show();
        String username = editUsername.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    Toast.makeText(this, "Firebase created user!", Toast.LENGTH_SHORT).show();
                    String userID = result.getUser().getUid();
                    User user = new User(userID, username, email);

                    db.collection("users").document(userID).set(user)
                            .addOnSuccessListener(v -> {
                                Toast.makeText(this, "Firestore user saved!", Toast.LENGTH_SHORT).show();
                                new android.app.AlertDialog.Builder(this)
                                        .setTitle("Success!")
                                        .setMessage("Account created successfully.\nRedirecting you to personalization…")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", (dialog, which) -> {

                                            // REDIRECT TO NEXT SCREEN
                                            Intent intent = new Intent(RegisterActivity.this, UserPersonalization.class);
                                            intent.putExtra("USER_ID", userID);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .show();

                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
