package com.example.projectfitness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;


public class StepsActivity extends AppCompatActivity {

    private EditText editDailySteps;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // This will close the activity and go back to the previous one

        // Reference the views in StepsActivity layout
        editDailySteps = findViewById(R.id.editDailySteps);
        buttonSave = findViewById(R.id.buttonSave);

        // Load the current steps goal from SharedPreferences and set it as the initial value in the EditText
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        int currentStepsGoal = preferences.getInt("stepsGoal", 0);
        editDailySteps.setText(String.valueOf(currentStepsGoal));

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the daily steps goal from the input
                String stepsGoalStr = editDailySteps.getText().toString();

                if (stepsGoalStr.isEmpty()) {
                    Toast.makeText(StepsActivity.this, "Please enter a steps goal", Toast.LENGTH_SHORT).show();
                    return;
                }

                int stepsGoal;
                try {
                    stepsGoal = Integer.parseInt(stepsGoalStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(StepsActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (stepsGoal < 1 || stepsGoal > 100000) {
                    Toast.makeText(StepsActivity.this, "Please enter a steps goal between 1 and 100,000", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save the steps goal to SharedPreferences
                SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("stepsGoal", stepsGoal);
                editor.apply();

                Toast.makeText(StepsActivity.this, "Daily Steps Goal Changed", Toast.LENGTH_SHORT).show();


                // Return to MainActivity
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
