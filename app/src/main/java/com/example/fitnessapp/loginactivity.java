package com.example.fitnessapp;

import android.os.Bundle;
import android.content.Intent;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class loginactivity extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button buttonLogin,buttonsignup;
    TextView textRegister;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        loginEmail = findViewById(R.id.email);
        loginPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.login);
        buttonsignup = findViewById(R.id.signup);

        auth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(v -> loginUser());

        buttonsignup.setOnClickListener(v ->
                startActivity(new Intent(loginactivity.this, RegisterActivity.class)));
    }

    private void loginUser() {
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(loginactivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}