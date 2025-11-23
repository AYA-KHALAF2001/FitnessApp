package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.fitnessapp.adapters.MealAdapter;
import com.example.fitnessapp.models.Meal;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class MealPlanActivity extends AppCompatActivity {

    RecyclerView recyclerMeals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);

        recyclerMeals = findViewById(R.id.recyclerMeals);
        recyclerMeals.setLayoutManager(new LinearLayoutManager(this));

        String jsonMeals = getIntent().getStringExtra("mealList");

        List<Meal> meals = new Gson().fromJson(jsonMeals, new TypeToken<List<Meal>>(){}.getType());

        MealAdapter adapter = new MealAdapter(meals);
        recyclerMeals.setAdapter(adapter);
    }
}
