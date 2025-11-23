package com.example.fitnessapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapp.R;
import com.example.fitnessapp.models.DayPlan;
import com.example.fitnessapp.models.Exercise;
import com.example.fitnessapp.models.Meal;

import java.util.List;

public class WeeklyPlanAdapter extends RecyclerView.Adapter<WeeklyPlanAdapter.DayHolder> {

    private List<DayPlan> days;
    private OnDayCompleteListener listener;

    public interface OnDayCompleteListener {
        void onDayCompleted(DayPlan day);
    }

    public WeeklyPlanAdapter(List<DayPlan> days, OnDayCompleteListener listener) {
        this.days = days;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day_plan, parent, false);
        return new DayHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayHolder h, int position) {
        DayPlan day = days.get(position);

        // Day name
        h.dayName.setText(day.day);

        // Build workout string
        StringBuilder w = new StringBuilder();
        for (Exercise e : day.workout) {
            w.append(e.getExercise())
                    .append("  |  ")
                    .append(e.getSets()).append(" x ")
                    .append(e.getReps())
                    .append("\n");
        }
        h.workoutText.setText(w.toString().trim());

        // Build meal string
        Meal m = day.getMeal();
        if (m != null) {
            String mealString =
                    m.getName() + "\n" +
                            "Calories: " + m.getCalories() + "\n" +
                            "Protein: " + m.getProtein() + "g | Carbs: " + m.getCarbs() + "g | Fat: " + m.getFat() + "g";

            h.mealText.setText(mealString);
        } else {
            h.mealText.setText("No meal available.");
        }

        // Click complete
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onDayCompleted(day);
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class DayHolder extends RecyclerView.ViewHolder {
        TextView dayName, workoutText, mealText;

        DayHolder(View v) {
            super(v);
            dayName = v.findViewById(R.id.textDayName);
            workoutText = v.findViewById(R.id.textWorkout);
            mealText = v.findViewById(R.id.textMeal);
        }
    }
}
