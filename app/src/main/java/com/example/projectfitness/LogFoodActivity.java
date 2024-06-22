package com.example.projectfitness;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LogFoodActivity extends AppCompatActivity {

    private EditText editTextFoodName, editTextCalories, editTextCarbs, editTextProtein, editTextFats;
    private Button buttonConfirm, buttonScanBarcode;
    private LinearLayout foodLogContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_food);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // This will close the activity and go back to the previous one

        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextCalories = findViewById(R.id.editTextCalories);
        editTextCarbs = findViewById(R.id.editTextCarbs);
        editTextProtein = findViewById(R.id.editTextProtein);
        editTextFats = findViewById(R.id.editTextFats);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonScanBarcode = findViewById(R.id.buttonScanBarcode);
        foodLogContainer = findViewById(R.id.foodLogContainer);

        buttonConfirm.setOnClickListener(v -> {
            // Retrieve input values
            String foodName = editTextFoodName.getText().toString();
            String calories = editTextCalories.getText().toString();
            String carbs = editTextCarbs.getText().toString();
            String protein = editTextProtein.getText().toString();
            String fats = editTextFats.getText().toString();

            // Validate inputs (example: ensure they are not empty)
            if (foodName.isEmpty() || calories.isEmpty() || carbs.isEmpty() || protein.isEmpty() || fats.isEmpty()) {
                Toast.makeText(LogFoodActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Add food log to the layout and save it
                addFoodLog(foodName, calories, carbs, protein, fats);
                saveFoodLog(foodName, calories, carbs, protein, fats);

                // Update progress bars and save the data using SharedPreferences
                updateProgressBars(Integer.parseInt(calories), Integer.parseInt(carbs), Integer.parseInt(protein), Integer.parseInt(fats), false);

                // Clear the input fields
                editTextFoodName.setText("");
                editTextCalories.setText("");
                editTextCarbs.setText("");
                editTextProtein.setText("");
                editTextFats.setText("");

                // Show a confirmation message
                Toast.makeText(LogFoodActivity.this, "Food logged!", Toast.LENGTH_SHORT).show();

                // Notify MainActivity to refresh the progress bars
                setResult(RESULT_OK);
            }
        });

        buttonScanBarcode.setOnClickListener(v -> {
            // Implement barcode scanning functionality
            Toast.makeText(LogFoodActivity.this, "WIP", Toast.LENGTH_SHORT).show();
        });

        // Load existing food logs
        loadFoodLogs();
    }

    private void addFoodLog(String foodName, String calories, String carbs, String protein, String fats) {
        View foodLogView = getLayoutInflater().inflate(R.layout.food_log_item, foodLogContainer, false);
        TextView foodNameTextView = foodLogView.findViewById(R.id.foodNameTextView);
        TextView foodDetailsTextView = foodLogView.findViewById(R.id.foodDetailsTextView);
        Button deleteFoodLogButton = foodLogView.findViewById(R.id.deleteFoodLogButton);

        foodNameTextView.setText(foodName);
        foodDetailsTextView.setText(String.format("Calories: %s, Fat: %s, Carbs: %s, Protein: %s", calories, fats, carbs, protein));

        deleteFoodLogButton.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Delete Food Log")
                    .setMessage("Are you sure you want to delete this food log?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Remove the food log from the layout
                        foodLogContainer.removeView(foodLogView);
                        // Remove the food log from SharedPreferences
                        removeFoodLog(foodName, calories, carbs, protein, fats);
                        // Update progress bars
                        updateProgressBars(Integer.parseInt(calories), Integer.parseInt(carbs), Integer.parseInt(protein), Integer.parseInt(fats), true);

                        // Notify MainActivity to refresh the progress bars
                        setResult(RESULT_OK);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        foodLogContainer.addView(foodLogView);
    }

    private void saveFoodLog(String foodName, String calories, String carbs, String protein, String fats) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String foodLogs = preferences.getString("foodLogs", "[]");
        try {
            JSONArray foodLogsArray = new JSONArray(foodLogs);
            JSONObject foodLog = new JSONObject();
            foodLog.put("foodName", foodName);
            foodLog.put("calories", calories);
            foodLog.put("carbs", carbs);
            foodLog.put("protein", protein);
            foodLog.put("fats", fats);

            foodLogsArray.put(foodLog);
            editor.putString("foodLogs", foodLogsArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadFoodLogs() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String foodLogs = preferences.getString("foodLogs", "[]");
        try {
            JSONArray foodLogsArray = new JSONArray(foodLogs);
            for (int i = 0; i < foodLogsArray.length(); i++) {
                JSONObject foodLog = foodLogsArray.getJSONObject(i);
                String foodName = foodLog.getString("foodName");
                String calories = foodLog.getString("calories");
                String carbs = foodLog.getString("carbs");
                String protein = foodLog.getString("protein");
                String fats = foodLog.getString("fats");

                addFoodLog(foodName, calories, carbs, protein, fats);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeFoodLog(String foodName, String calories, String carbs, String protein, String fats) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String foodLogs = preferences.getString("foodLogs", "[]");
        try {
            JSONArray foodLogsArray = new JSONArray(foodLogs);
            JSONArray newFoodLogsArray = new JSONArray();
            for (int i = 0; i < foodLogsArray.length(); i++) {
                JSONObject foodLog = foodLogsArray.getJSONObject(i);
                if (!foodLog.getString("foodName").equals(foodName) ||
                        !foodLog.getString("calories").equals(calories) ||
                        !foodLog.getString("carbs").equals(carbs) ||
                        !foodLog.getString("protein").equals(protein) ||
                        !foodLog.getString("fats").equals(fats)) {
                    newFoodLogsArray.put(foodLog);
                }
            }
            editor.putString("foodLogs", newFoodLogsArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateProgressBars(int calories, int carbs, int protein, int fats, boolean isDeletion) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        int currentCalories = preferences.getInt("currentCalories", 0);
        int currentCarbs = preferences.getInt("currentCarbs", 0);
        int currentProtein = preferences.getInt("currentProtein", 0);
        int currentFats = preferences.getInt("currentFats", 0);

        if (isDeletion) {
            currentCalories -= calories;
            currentCarbs -= carbs;
            currentProtein -= protein;
            currentFats -= fats;
        } else {
            currentCalories += calories;
            currentCarbs += carbs;
            currentProtein += protein;
            currentFats += fats;
        }

        editor.putInt("currentCalories", currentCalories);
        editor.putInt("currentCarbs", currentCarbs);
        editor.putInt("currentProtein", currentProtein);
        editor.putInt("currentFats", currentFats);
        editor.apply();

        // Notify MainActivity to refresh the progress bars
        setResult(RESULT_OK);
    }
}
