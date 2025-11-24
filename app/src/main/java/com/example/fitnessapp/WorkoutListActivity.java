package com.example.fitnessapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fitnessapp.adapters.WeeklyPlanAdapter;
import com.example.fitnessapp.models.DayPlan;
import com.example.fitnessapp.models.Exercise;
import com.example.fitnessapp.models.Meal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class WorkoutListActivity extends AppCompatActivity {

    private ProgressBar loading;
    private RecyclerView recycler;

    private FirebaseFirestore db;
    private String userID;

    private String experience = "Beginner";
    private String goal = "General fitness";

    private int xp = 0;
    private int level = 1;

    private final String model = "gpt-4o-mini";

    private List<DayPlan> cachedPlans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);

        loading = findViewById(R.id.loading);
        recycler = findViewById(R.id.recyclerWeeklyPlan);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        new android.os.Handler().postDelayed(this::loadUserData, 300);
    }

    private void loadUserData() {
        db.collection("users").document(userID).get()
                .addOnSuccessListener(doc -> {
                    experience = doc.getString("experience");
                    goal = doc.getString("goal");

                    xp = doc.getLong("xp") != null ? doc.getLong("xp").intValue() : 0;
                    level = doc.getLong("level") != null ? doc.getLong("level").intValue() : 1;

                    if (xp >= level * 100) {
                        level++;
                        xp = 0;

                        db.collection("users").document(userID)
                                .update("xp", xp, "level", level);
                    }

                    generateGPTPlan();
                })
                .addOnFailureListener(e -> generateGPTPlan());
    }





    private void generateGPTPlan() {
        loading.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .build();

                String requestBody = generatePromptJson();

                Request req = new Request.Builder()
                        .url("https://api.openai.com/v1/chat/completions")
                        .addHeader("Authorization", "Bearer " + BuildConfig.OPENAI_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                        .build();

                okhttp3.Response res = client.newCall(req).execute();
                String body = res.body().string();

                Log.e("GPT_RAW", "Received GPT response");

                JSONObject root = new JSONObject(body);
                if (!root.has("choices")) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "GPT returned no choices", Toast.LENGTH_LONG).show()
                    );
                    return;
                }

                String content = root.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                List<DayPlan> plans = parseWeek(content);
                cachedPlans = plans;

                runOnUiThread(() -> {
                    loading.setVisibility(View.GONE);
                    recycler.setAdapter(new WeeklyPlanAdapter(plans, day -> {
                        awardXP();
                        updateStreak();
                        Toast.makeText(this, "Workout completed!", Toast.LENGTH_SHORT).show();
                    }));
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "GPT Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    private String generatePromptJson() {
        try {
            JSONObject root = new JSONObject();
            root.put("model", model);

            JSONObject rf = new JSONObject();
            rf.put("type", "json_object");
            root.put("response_format", rf);

            JSONArray msgs = new JSONArray();
            JSONObject user = new JSONObject();

            String prompt =
                    "Return ONLY valid JSON.\n" +
                            "{ \"week\": [ { \"day\": \"Monday\", \"workout\": [{\"exercise\":\"Push-ups\",\"sets\":3,\"reps\":\"12\",\"rest\":\"60s\"}], \"meal\": {\"name\":\"Chicken Bowl\",\"calories\":550,\"protein\":40,\"carbs\":60,\"fat\":15} } ] }\n" +
                            "Rules: 7 days, 3–5 exercises per day, include meal macros, no markdown.";

            user.put("role", "user");
            user.put("content", prompt);
            msgs.put(user);

            root.put("messages", msgs);

            return root.toString();

        } catch (Exception e) {
            return "{}";
        }
    }

    private List<DayPlan> parseWeek(String jsonString) throws Exception {

        JSONObject root = new JSONObject(jsonString);
        JSONArray weekArr = root.getJSONArray("week");

        List<DayPlan> days = new ArrayList<>();

        for (int i = 0; i < weekArr.length(); i++) {
            JSONObject d = weekArr.getJSONObject(i);

            String dayName = d.getString("day");

            JSONArray workoutArr = d.getJSONArray("workout");
            List<Exercise> exList = new ArrayList<>();

            for (int j = 0; j < workoutArr.length(); j++) {
                JSONObject e = workoutArr.getJSONObject(j);

                String exerciseName = e.optString("exercise", "Unknown");
                int sets = e.optInt("sets", 0);

                String repsOrDuration;
                if (e.has("reps")) {
                    repsOrDuration = e.optString("reps", "");
                } else if (e.has("duration")) {
                    repsOrDuration = e.optString("duration", "");
                } else {
                    repsOrDuration = "As many or as long as possible";
                }

                String rest = e.optString("rest", "60s");

                exList.add(new Exercise(exerciseName, sets, repsOrDuration, rest));
            }

            JSONObject mealObj = d.getJSONObject("meal");
            Meal meal = new Meal(
                    mealObj.optString("name", "Unknown Meal"),
                    mealObj.optInt("calories", 0),
                    mealObj.optInt("protein", 0),
                    mealObj.optInt("carbs", 0),
                    mealObj.optInt("fat", 0)
            );

            days.add(new DayPlan(dayName, exList, meal));
        }

        return days;
    }

    private void awardXP() {
        xp += 50;

        int required = level * 100;

        if (xp >= required) {
            level++;
            xp = 0;
        }

        db.collection("users").document(userID)
                .update("xp", xp, "level", level)
                .addOnSuccessListener(unused -> Log.e("XP", "XP updated: " + xp + " level: " + level));
    }


    private void updateStreak() {
        String today = LocalDate.now().toString();

        db.collection("users").document(userID).get()
                .addOnSuccessListener(doc -> {

                    String last = doc.getString("last_workout_date");
                    int streak = doc.getLong("current_streak") != null
                            ? doc.getLong("current_streak").intValue()
                            : 0;

                    if (last == null) {
                        streak = 1;
                    } else {
                        LocalDate lastDate = LocalDate.parse(last);
                        LocalDate now = LocalDate.parse(today);

                        if (lastDate.plusDays(1).isEqual(now)) streak++;
                        else if (!lastDate.isEqual(now)) streak = 1;
                    }

                    db.collection("users").document(userID)
                            .update("current_streak", streak, "last_workout_date", today);
                });
    }
}
