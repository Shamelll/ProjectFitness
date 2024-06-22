package com.example.projectfitness;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ChangeCalorieGoalActivity extends AppCompatActivity {

    private EditText editDailyCalories, editDailyCarbs, editDailyProtein, editDailyFats;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_calorie_goal);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // This will close the activity and go back to the previous one

        editDailyCalories = findViewById(R.id.editDailyCalories);
        editDailyCarbs = findViewById(R.id.editDailyCarbs);
        editDailyProtein = findViewById(R.id.editDailyProtein);
        editDailyFats = findViewById(R.id.editDailyFats);
        buttonSave = findViewById(R.id.buttonSave);

        // Load saved values
        loadSavedValues();

        buttonSave.setOnClickListener(v -> {
            // Retrieve input values
            String dailyCaloriesStr = editDailyCalories.getText().toString();
            String dailyCarbsStr = editDailyCarbs.getText().toString();
            String dailyProteinStr = editDailyProtein.getText().toString();
            String dailyFatsStr = editDailyFats.getText().toString();

            // Validate inputs (example: ensure they are not empty)
            if (dailyCaloriesStr.isEmpty() || dailyCarbsStr.isEmpty() || dailyProteinStr.isEmpty() || dailyFatsStr.isEmpty()) {
                Toast.makeText(ChangeCalorieGoalActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Parse to integers
                int dailyCalories = Integer.parseInt(dailyCaloriesStr);
                int dailyCarbs = Integer.parseInt(dailyCarbsStr);
                int dailyProtein = Integer.parseInt(dailyProteinStr);
                int dailyFats = Integer.parseInt(dailyFatsStr);

                // Save the data using SharedPreferences
                SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("dailyCalories", dailyCalories);
                editor.putInt("dailyCarbs", dailyCarbs);
                editor.putInt("dailyProtein", dailyProtein);
                editor.putInt("dailyFats", dailyFats);
                editor.apply();

                Toast.makeText(ChangeCalorieGoalActivity.this, "Goals saved!", Toast.LENGTH_SHORT).show();

                // Finish the activity and go back to the main screen
                finish();
            }
        });
    }

    private void loadSavedValues() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        int dailyCalories = preferences.getInt("dailyCalories", 0);
        int dailyCarbs = preferences.getInt("dailyCarbs", 0);
        int dailyProtein = preferences.getInt("dailyProtein", 0);
        int dailyFats = preferences.getInt("dailyFats", 0);

        editDailyCalories.setText(String.valueOf(dailyCalories));
        editDailyCarbs.setText(String.valueOf(dailyCarbs));
        editDailyProtein.setText(String.valueOf(dailyProtein));
        editDailyFats.setText(String.valueOf(dailyFats));
    }
}
