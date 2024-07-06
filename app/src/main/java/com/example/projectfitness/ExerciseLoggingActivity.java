package com.example.projectfitness;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ExerciseLoggingActivity extends AppCompatActivity {

    private EditText editTextDate, editTextTotalTime, editTextCaloriesBurnt;
    private LinearLayout exercisesContainer;
    private Button buttonAddExercise, buttonConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_logging);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // This will close the activity and go back to the previous one

        editTextDate = findViewById(R.id.editTextDate);
        editTextTotalTime = findViewById(R.id.editTextTotalTime);
        editTextCaloriesBurnt = findViewById(R.id.editTextCaloriesBurnt);
        exercisesContainer = findViewById(R.id.exercisesContainer);
        buttonAddExercise = findViewById(R.id.buttonAddExercise);
        buttonConfirm = findViewById(R.id.buttonConfirm);

        buttonAddExercise.setOnClickListener(v -> addExerciseView());

        buttonConfirm.setOnClickListener(v -> {
            String date = editTextDate.getText().toString();
            String totalTimeStr = editTextTotalTime.getText().toString();
            String caloriesBurntStr = editTextCaloriesBurnt.getText().toString();

            if (date.isEmpty() || totalTimeStr.isEmpty() || caloriesBurntStr.isEmpty()) {
                // Show a message to fill all fields
                return;
            }

            int totalTime = Integer.parseInt(totalTimeStr);
            int caloriesBurnt = Integer.parseInt(caloriesBurntStr);
            StringBuilder exercisesBuilder = new StringBuilder();

            for (int i = 0; i < exercisesContainer.getChildCount(); i++) {
                EditText exerciseView = (EditText) exercisesContainer.getChildAt(i);
                String exercise = exerciseView.getText().toString();
                if (!exercise.isEmpty()) {
                    exercisesBuilder.append(exercise).append(", ");
                }
            }

            if (exercisesBuilder.length() > 0) {
                exercisesBuilder.setLength(exercisesBuilder.length() - 2); // Remove last comma and space
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("date", date);
            resultIntent.putExtra("exercises", exercisesBuilder.toString());
            resultIntent.putExtra("totalTime", totalTime);
            resultIntent.putExtra("caloriesBurnt", caloriesBurnt);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void addExerciseView() {
        EditText exerciseView = new EditText(this);
        exerciseView.setHint("Exercise");
        exercisesContainer.addView(exerciseView);
    }
}
