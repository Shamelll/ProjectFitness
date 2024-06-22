package com.example.projectfitness;

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

        buttonSave.setOnClickListener(v -> {
            // Retrieve input values
            String dailyCalories = editDailyCalories.getText().toString();
            String dailyCarbs = editDailyCarbs.getText().toString();
            String dailyProtein = editDailyProtein.getText().toString();
            String dailyFats = editDailyFats.getText().toString();

            // Validate inputs (they are not empty)
            if (dailyCalories.isEmpty() || dailyCarbs.isEmpty() || dailyProtein.isEmpty() || dailyFats.isEmpty()) {
                Toast.makeText(ChangeCalorieGoalActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: SAVE DATA AND SEND IT TO PROGRESS BARS IN MAIN SCREEN


                // For demonstration, just showing a Toast
                Toast.makeText(ChangeCalorieGoalActivity.this, "Goals saved!", Toast.LENGTH_SHORT).show();

                // Finish the activity and go back to the main screen
                finish();
            }
        });
    }
}
