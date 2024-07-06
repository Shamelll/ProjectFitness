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

public class ExerciseListActivity extends AppCompatActivity {

    private LinearLayout exerciseLogContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // This will close the activity and go back to the previous one

        exerciseLogContainer = findViewById(R.id.exerciseLogContainer);
        Button buttonAddExercise = findViewById(R.id.buttonAddExercise);

        buttonAddExercise.setOnClickListener(v -> {
            Intent intent = new Intent(ExerciseListActivity.this, ExerciseLoggingActivity.class);
            startActivityForResult(intent, 1);
        });

        // Load existing exercise logs
        loadExerciseLogs();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String date = data.getStringExtra("date");
            String exercises = data.getStringExtra("exercises");
            int totalTime = data.getIntExtra("totalTime", 0);
            int caloriesBurnt = data.getIntExtra("caloriesBurnt", 0);
            String id = String.valueOf(System.currentTimeMillis());

            addExerciseLog(date, exercises, totalTime, caloriesBurnt, id);
            saveExerciseLog(date, exercises, totalTime, caloriesBurnt, id);

            // Show a confirmation message
            Toast.makeText(this, "Exercise logged!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addExerciseLog(String date, String exercises, int totalTime, int caloriesBurnt, String id) {
        View exerciseLogView = getLayoutInflater().inflate(R.layout.exercise_log_item, exerciseLogContainer, false);
        TextView exerciseDateTextView = exerciseLogView.findViewById(R.id.exerciseDateTextView);
        TextView exerciseDetailsTextView = exerciseLogView.findViewById(R.id.exerciseDetailsTextView);
        Button deleteExerciseLogButton = exerciseLogView.findViewById(R.id.deleteExerciseLogButton);

        exerciseDateTextView.setText(date);
        exerciseDetailsTextView.setText(String.format("Exercises: %s\nTotal Time: %d min\nCalories Burnt: %d", exercises, totalTime, caloriesBurnt));
        exerciseLogView.setTag(id);

        deleteExerciseLogButton.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Delete Exercise Log")
                    .setMessage("Are you sure you want to delete this exercise log?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Remove the exercise log from the layout
                        exerciseLogContainer.removeView(exerciseLogView);
                        // Remove the exercise log from SharedPreferences
                        removeExerciseLog(id);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        exerciseLogContainer.addView(exerciseLogView);
    }

    private void saveExerciseLog(String date, String exercises, int totalTime, int caloriesBurnt, String id) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String exerciseLogs = preferences.getString("exerciseLogs", "[]");
        try {
            JSONArray exerciseLogsArray = new JSONArray(exerciseLogs);
            JSONObject exerciseLog = new JSONObject();
            exerciseLog.put("id", id);
            exerciseLog.put("date", date);
            exerciseLog.put("exercises", exercises);
            exerciseLog.put("totalTime", totalTime);
            exerciseLog.put("caloriesBurnt", caloriesBurnt);

            exerciseLogsArray.put(exerciseLog);
            editor.putString("exerciseLogs", exerciseLogsArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadExerciseLogs() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String exerciseLogs = preferences.getString("exerciseLogs", "[]");
        try {
            JSONArray exerciseLogsArray = new JSONArray(exerciseLogs);
            for (int i = 0; i < exerciseLogsArray.length(); i++) {
                JSONObject exerciseLog = exerciseLogsArray.getJSONObject(i);
                String id = exerciseLog.getString("id");
                String date = exerciseLog.getString("date");
                String exercises = exerciseLog.getString("exercises");
                int totalTime = exerciseLog.getInt("totalTime");
                int caloriesBurnt = exerciseLog.getInt("caloriesBurnt");

                addExerciseLog(date, exercises, totalTime, caloriesBurnt, id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeExerciseLog(String id) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String exerciseLogs = preferences.getString("exerciseLogs", "[]");
        try {
            JSONArray exerciseLogsArray = new JSONArray(exerciseLogs);
            JSONArray newExerciseLogsArray = new JSONArray();
            for (int i = 0; i < exerciseLogsArray.length(); i++) {
                JSONObject exerciseLog = exerciseLogsArray.getJSONObject(i);
                if (!exerciseLog.getString("id").equals(id)) {
                    newExerciseLogsArray.put(exerciseLog);
                }
            }
            editor.putString("exerciseLogs", newExerciseLogsArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
