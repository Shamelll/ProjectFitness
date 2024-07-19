package com.example.projectfitness;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;

import java.util.UUID;

public class ExerciseLoggingActivity extends AppCompatActivity {

    private EditText editTextExerciseName, editTextDuration, editTextCaloriesBurned, editTextSets, editTextReps;
    private Button buttonAddExercise, buttonSaveRoutine;
    private LinearLayout exercisesContainer;
    private JSONArray exercisesArray;
    private String routineId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_logging);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // This will close the activity and go back to the previous one

        editTextExerciseName = findViewById(R.id.editTextExerciseName);
        editTextDuration = findViewById(R.id.editTextDuration);
        editTextCaloriesBurned = findViewById(R.id.editTextCaloriesBurned);
        editTextSets = findViewById(R.id.editTextSets);
        editTextReps = findViewById(R.id.editTextReps);
        buttonAddExercise = findViewById(R.id.buttonAddExercise);
        buttonSaveRoutine = findViewById(R.id.buttonSaveRoutine);
        exercisesContainer = findViewById(R.id.exercisesContainer);
        exercisesArray = new JSONArray();

        buttonAddExercise.setOnClickListener(v -> {
            String exerciseName = editTextExerciseName.getText().toString();
            String duration = editTextDuration.getText().toString();
            String caloriesBurned = editTextCaloriesBurned.getText().toString();
            String sets = editTextSets.getText().toString();
            String reps = editTextReps.getText().toString();

            if (exerciseName.isEmpty() || duration.isEmpty() || caloriesBurned.isEmpty() || sets.isEmpty() || reps.isEmpty()) {
                Toast.makeText(ExerciseLoggingActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject exercise = new JSONObject();
                exercise.put("name", exerciseName);
                exercise.put("duration", Integer.parseInt(duration));
                exercise.put("caloriesBurned", Integer.parseInt(caloriesBurned));
                exercise.put("sets", Integer.parseInt(sets));
                exercise.put("reps", Integer.parseInt(reps));
                exercisesArray.put(exercise);

                addExerciseToView(exercise);

                editTextExerciseName.setText("");
                editTextDuration.setText("");
                editTextCaloriesBurned.setText("");
                editTextSets.setText("");
                editTextReps.setText("");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        buttonSaveRoutine.setOnClickListener(v -> {
            if (exercisesArray.length() == 0) {
                Toast.makeText(ExerciseLoggingActivity.this, "Please add at least one exercise", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                String routines = preferences.getString("exerciseRoutines", "[]");
                JSONArray routinesArray = new JSONArray(routines);

                JSONObject routine = new JSONObject();
                if (routineId == null) {
                    routineId = UUID.randomUUID().toString();
                }
                routine.put("id", routineId);
                routine.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                routine.put("exercises", exercisesArray);

                int totalTime = 0;
                int totalCalories = 0;
                for (int i = 0; i < exercisesArray.length(); i++) {
                    JSONObject exercise = exercisesArray.getJSONObject(i);
                    totalTime += exercise.getInt("duration");
                    totalCalories += exercise.getInt("caloriesBurned");
                }
                routine.put("totalTime", totalTime);
                routine.put("totalCalories", totalCalories);
                routine.put("exerciseCount", exercisesArray.length());

                // If editing an existing routine, find and replace it
                boolean routineExists = false;
                for (int i = 0; i < routinesArray.length(); i++) {
                    JSONObject existingRoutine = routinesArray.getJSONObject(i);
                    if (existingRoutine.getString("id").equals(routineId)) {
                        routinesArray.put(i, routine);
                        routineExists = true;
                        break;
                    }
                }

                if (!routineExists) {
                    routinesArray.put(routine);
                }

                editor.putString("exerciseRoutines", routinesArray.toString());
                editor.apply();

                // Broadcast an event indicating a routine has been saved
                Intent intent = new Intent("com.example.projectfitness.ROUTINE_SAVED");
                sendBroadcast(intent);

                Toast.makeText(ExerciseLoggingActivity.this, "Routine saved!", Toast.LENGTH_SHORT).show();
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // Check if we are editing an existing routine
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("routineId")) {
            routineId = intent.getStringExtra("routineId");
            String routineDate = intent.getStringExtra("routineDate");
            String routineExercises = intent.getStringExtra("routineExercises");

            try {
                JSONArray exercises = new JSONArray(routineExercises);
                for (int i = 0; i < exercises.length(); i++) {
                    JSONObject exercise = exercises.getJSONObject(i);
                    exercisesArray.put(exercise);
                    addExerciseToView(exercise);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addExerciseToView(JSONObject exercise) throws JSONException {
        View exerciseView = getLayoutInflater().inflate(R.layout.exercise_item, exercisesContainer, false);
        TextView exerciseNameTextView = exerciseView.findViewById(R.id.exerciseNameTextView);
        TextView exerciseDetailsTextView = exerciseView.findViewById(R.id.exerciseDetailsTextView);
        Button deleteExerciseButton = exerciseView.findViewById(R.id.deleteExerciseButton);

        String name = exercise.getString("name");
        int duration = exercise.getInt("duration");
        int caloriesBurned = exercise.getInt("caloriesBurned");
        int sets = exercise.getInt("sets");
        int reps = exercise.getInt("reps");

        exerciseNameTextView.setText(name);
        exerciseDetailsTextView.setText(String.format("Duration: %d mins, Calories: %d, Sets: %d, Reps: %d", duration, caloriesBurned, sets, reps));

        deleteExerciseButton.setOnClickListener(v -> {
            exercisesContainer.removeView(exerciseView);
            removeExerciseFromArray(exercise);
        });

        exercisesContainer.addView(exerciseView);

        Toast.makeText(ExerciseLoggingActivity.this, "Exercise added", Toast.LENGTH_SHORT).show();

    }

    private void removeExerciseFromArray(JSONObject exercise) {
        for (int i = 0; i < exercisesArray.length(); i++) {
            try {
                if (exercisesArray.getJSONObject(i).toString().equals(exercise.toString())) {
                    exercisesArray.remove(i);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
