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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.view.MenuItem;

import java.util.*;
import java.lang.*;
import java.io.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public class ExerciseRoutineActivity extends AppCompatActivity {

    private LinearLayout routineLogContainer;
    private Button addRoutineButton;
    private BroadcastReceiver routineSavedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_routine);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // This will close the activity and go back to the previous one

        routineLogContainer = findViewById(R.id.routineLogContainer);
        addRoutineButton = findViewById(R.id.addRoutineButton);

        addRoutineButton.setOnClickListener(v -> {
            Intent intent = new Intent(ExerciseRoutineActivity.this, ExerciseLoggingActivity.class);
            startActivity(intent);
        });

        loadExerciseRoutines();

        // Register the receiver
        registerRoutineSavedReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver
        unregisterReceiver(routineSavedReceiver);
    }

    private void registerRoutineSavedReceiver() {
        routineSavedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Update the list when a routine is saved
                loadExerciseRoutines();
            }
        };
        IntentFilter filter = new IntentFilter("com.example.projectfitness.ROUTINE_SAVED");
        registerReceiver(routineSavedReceiver, filter);
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

    private void addRoutineToView(JSONObject routine) {
        try {
            View routineView = getLayoutInflater().inflate(R.layout.exercise_routine_item, routineLogContainer, false);
            TextView routineDateTextView = routineView.findViewById(R.id.routineDateTextView);
            TextView routineDetailsTextView = routineView.findViewById(R.id.routineDetailsTextView);
            Button deleteRoutineButton = routineView.findViewById(R.id.deleteRoutineButton);

            String id = routine.getString("id");
            String date = routine.getString("date");
            int totalTime = routine.getInt("totalTime");
            int totalCalories = routine.getInt("totalCalories");
            int exerciseCount = routine.getInt("exerciseCount");

            routineDateTextView.setText(date);
            routineDetailsTextView.setText(String.format("Exercises: %d, Total Time: %d mins, Calories Burned: %d kcal", exerciseCount, totalTime, totalCalories));

            deleteRoutineButton.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Routine")
                        .setMessage("Are you sure you want to delete this routine?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            routineLogContainer.removeView(routineView);
                            removeRoutineFromPreferences(id);
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            });

            routineView.setOnClickListener(v -> {
                Intent intent = new Intent(ExerciseRoutineActivity.this, ExerciseLoggingActivity.class);
                intent.putExtra("routineId", id);
                intent.putExtra("routineDate", date);
                try {
                    intent.putExtra("routineExercises", routine.getString("exercises"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            });

            routineView.setTag(id);
            routineLogContainer.addView(routineView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeRoutineFromPreferences(String id) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String routines = preferences.getString("exerciseRoutines", "[]");
        try {
            JSONArray routinesArray = new JSONArray(routines);
            JSONArray newRoutinesArray = new JSONArray();
            for (int i = 0; i < routinesArray.length(); i++) {
                JSONObject routine = routinesArray.getJSONObject(i);
                if (!routine.getString("id").equals(id)) {
                    newRoutinesArray.put(routine);
                }
            }
            editor.putString("exerciseRoutines", newRoutinesArray.toString());
            editor.apply();

            // Broadcast an event indicating a routine has been deleted
            Intent intent = new Intent("com.example.projectfitness.ROUTINE_DELETED");
            sendBroadcast(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
