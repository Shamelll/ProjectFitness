package com.example.projectfitness;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;

public class ExerciseRoutineActivity extends AppCompatActivity {

    private LinearLayout routineLogContainer;
    private Button addRoutineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_routine);

        routineLogContainer = findViewById(R.id.routineLogContainer);
        addRoutineButton = findViewById(R.id.addRoutineButton);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish()); // This will close the activity and go back to the previous one

        addRoutineButton.setOnClickListener(v -> {
            Intent intent = new Intent(ExerciseRoutineActivity.this, ExerciseLoggingActivity.class);
            startActivity(intent);
        });

        loadExerciseRoutines();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExerciseRoutines();
    }

    private void loadExerciseRoutines() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String routines = preferences.getString("exerciseRoutines", "[]");
        try {
            JSONArray routinesArray = new JSONArray(routines);
            routineLogContainer.removeAllViews();
            for (int i = 0; i < routinesArray.length(); i++) {
                JSONObject routine = routinesArray.getJSONObject(i);
                addRoutineToView(routine);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addRoutineToView(JSONObject routine) throws JSONException {
        View routineView = getLayoutInflater().inflate(R.layout.exercise_routine_item, routineLogContainer, false);
        TextView routineDateTextView = routineView.findViewById(R.id.routineDateTextView);
        TextView routineDetailsTextView = routineView.findViewById(R.id.routineDetailsTextView);
        Button deleteRoutineButton = routineView.findViewById(R.id.deleteRoutineButton);

        // Convert timestamp to a readable date format
        String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                .format(new java.util.Date(routine.getLong("date")));

        int totalTime = routine.getInt("totalTime");
        int totalCalories = routine.getInt("totalCalories");
        int exerciseCount = routine.getInt("exerciseCount");

        routineDateTextView.setText(date);
        routineDetailsTextView.setText(String.format("Exercises: %d, Total Time: %d mins, Calories Burned: %d kcal", exerciseCount, totalTime, totalCalories));

        deleteRoutineButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Exercise Routine")
                    .setMessage("Are you sure you want to delete this exercise routine?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        routineLogContainer.removeView(routineView);
                        try {
                            removeRoutineFromPreferences(routine.getLong("date"));
                            Toast.makeText(this, "Routine deleted", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        routineLogContainer.addView(routineView);
    }

    private void removeRoutineFromPreferences(long date) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String routines = preferences.getString("exerciseRoutines", "[]");
        try {
            JSONArray routinesArray = new JSONArray(routines);
            JSONArray newRoutinesArray = new JSONArray();
            for (int i = 0; i < routinesArray.length(); i++) {
                JSONObject routine = routinesArray.getJSONObject(i);
                if (routine.getLong("date") != date) {
                    newRoutinesArray.put(routine);
                }
            }
            editor.putString("exerciseRoutines", newRoutinesArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
