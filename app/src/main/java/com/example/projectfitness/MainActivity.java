package com.example.projectfitness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;




public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_CHANGE_GOAL = 1;
    private static final int REQUEST_CODE_LOG_FOOD = 2;
    private static final int REQUEST_CODE_CHANGE_STEPS = 3;


    private ProgressBar progressCalories, progressProtein, progressCarbs, progressFats;
    private TextView textCalories, textProtein, textCarbs, textFats, stepsGoal, exerciseTime, exerciseCalories;
    private BroadcastReceiver routineDeletedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressCalories = findViewById(R.id.progressCalories);
        progressProtein = findViewById(R.id.progressProtein);
        progressCarbs = findViewById(R.id.progressCarbs);
        progressFats = findViewById(R.id.progressFats);
        Button stepsButton = findViewById(R.id.stepsButton);
        Button exerciseButton = findViewById(R.id.exerciseButton);
        Button weightButton = findViewById(R.id.weightButton);
        Button logFoodButton = findViewById(R.id.logFoodButton);
        Button changeCalIntakeButton = findViewById(R.id.ChangeIntakeButton);

        textCalories = findViewById(R.id.textCalories);
        textProtein = findViewById(R.id.textProtein);
        textCarbs = findViewById(R.id.textCarbs);
        textFats = findViewById(R.id.textFats);
        stepsGoal = findViewById(R.id.stepsGoal);
        exerciseTime = findViewById(R.id.exerciseTime);
        exerciseCalories = findViewById(R.id.exerciseCalories);

        //deleteAllData();

        stepsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StepsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CHANGE_STEPS);
        });

        exerciseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExerciseRoutineActivity.class);
            startActivity(intent);
        });

        weightButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WeightActivity.class);
            startActivity(intent);
        });

        changeCalIntakeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChangeCalorieGoalActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CHANGE_GOAL);
        });

        logFoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LogFoodActivity.class);
            startActivityForResult(intent, REQUEST_CODE_LOG_FOOD);
        });

        // Load and display the saved values
        loadAndDisplayValues();

        // Register the receiver
        registerRoutineDeletedReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load and display the saved values again when the activity is resumed
        loadAndDisplayValues();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver
        unregisterReceiver(routineDeletedReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHANGE_GOAL || requestCode == REQUEST_CODE_LOG_FOOD || requestCode == 3) {
                // Reload and display the saved values
                loadAndDisplayValues();
            }
        }
    }

    private void registerRoutineDeletedReceiver() {
        routineDeletedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Update the values when a routine is deleted
                loadAndDisplayValues();
            }
        };
        IntentFilter filter = new IntentFilter("com.example.projectfitness.ROUTINE_DELETED");
        registerReceiver(routineDeletedReceiver, filter);
    }

    public void loadAndDisplayValues() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        int dailyCalories = preferences.getInt("dailyCalories", 0);
        int dailyCarbs = preferences.getInt("dailyCarbs", 0);
        int dailyProtein = preferences.getInt("dailyProtein", 0);
        int dailyFats = preferences.getInt("dailyFats", 0);

        int currentCalories = preferences.getInt("currentCalories", 0);
        int currentCarbs = preferences.getInt("currentCarbs", 0);
        int currentProtein = preferences.getInt("currentProtein", 0);
        int currentFats = preferences.getInt("currentFats", 0);
        int stepsGoalValue = preferences.getInt("stepsGoal", 0);

        int totalWorkoutTime = preferences.getInt("totalWorkoutTime", 0);
        int totalCaloriesBurned = preferences.getInt("totalCaloriesBurned", 0);

        //Log.d(TAG, "Loaded values: dailyCalories=" + dailyCalories + ", dailyCarbs=" + dailyCarbs + ", dailyProtein=" + dailyProtein + ", dailyFats=" + dailyFats);
        //Log.d(TAG, "Current values: currentCalories=" + currentCalories + ", currentCarbs=" + currentCarbs + ", currentProtein=" + currentProtein + ", currentFats=" + currentFats);

        // Update progress bars
        if (dailyCalories > 0) {
            progressCalories.setMax(dailyCalories);
            progressCalories.setProgress(currentCalories);
            textCalories.setText(String.format("%d/%d\nRemaining", currentCalories, dailyCalories));
        } else {
            textCalories.setText("0/0\nRemaining");
        }

        if (dailyProtein > 0) {
            progressProtein.setMax(dailyProtein);
            progressProtein.setProgress(currentProtein);
            textProtein.setText(String.format("%dg/%dg\nRemaining", currentProtein, dailyProtein));
        } else {
            textProtein.setText("0g/0g\nRemaining");
        }

        if (dailyCarbs > 0) {
            progressCarbs.setMax(dailyCarbs);
            progressCarbs.setProgress(currentCarbs);
            textCarbs.setText(String.format("%dg/%dg\nRemaining", currentCarbs, dailyCarbs));
        } else {
            textCarbs.setText("0g/0g\nRemaining");
        }

        if (dailyFats > 0) {
            progressFats.setMax(dailyFats);
            progressFats.setProgress(currentFats);
            textFats.setText(String.format("%dg/%dg\nRemaining", currentFats, dailyFats));
        } else {
            textFats.setText("0g/0g\nRemaining");
        }

        // Update steps goal
        stepsGoal.setText(String.format("Goal: %d", stepsGoalValue));

        // Display exercise stats
        //exerciseTime.setText(String.format("Workout Time: %d mins", totalWorkoutTime));
        //exerciseCalories.setText(String.format("Calories Burnt: %d kcal", totalCaloriesBurned));
        calculateAndDisplayExerciseData();
    }

    private void calculateAndDisplayExerciseData() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String exerciseLogs = preferences.getString("exerciseRoutines", "[]");
        int totalWorkoutTime = 0;
        int totalCaloriesBurnt = 0;

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        try {
            JSONArray exerciseLogsArray = new JSONArray(exerciseLogs);
            Log.d(TAG, "Loaded exercise logs: " + exerciseLogsArray.toString()); // Log loaded exercise logs
            for (int i = 0; i < exerciseLogsArray.length(); i++) {
                JSONObject exerciseLog = exerciseLogsArray.getJSONObject(i);
                String logDate = exerciseLog.getString("date");

                if (logDate.equals(currentDate)) {
                    totalWorkoutTime += exerciseLog.getInt("totalTime");
                    totalCaloriesBurnt += exerciseLog.getInt("totalCalories");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        exerciseTime.setText(String.format("Workout Time: %d mins", totalWorkoutTime));
        exerciseCalories.setText(String.format("Calories Burnt: %d kcal", totalCaloriesBurnt));
    }

    // FOR DEBUGGING
    private void deleteAllData() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        // Clear the progress bars
        progressCalories.setProgress(0);
        progressProtein.setProgress(0);
        progressCarbs.setProgress(0);
        progressFats.setProgress(0);
        textCalories.setText("0/0\nRemaining");
        textProtein.setText("0g/0g\nRemaining");
        textCarbs.setText("0g/0g\nRemaining");
        textFats.setText("0g/0g\nRemaining");
        exerciseTime.setText("Workout Time: 0 mins");
        exerciseCalories.setText("Calories Burnt: 0 kcal");
    }
}