package com.example.projectfitness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ScannedFoodActivity extends AppCompatActivity {

    private EditText editTextFoodName;
    private TextView textViewCalories, textViewCarbs, textViewProtein, textViewFats;
    private Button buttonConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_food);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // This will close the activity and go back to the previous one

        editTextFoodName = findViewById(R.id.editTextFoodName);
        textViewCalories = findViewById(R.id.textViewCalories);
        textViewCarbs = findViewById(R.id.textViewCarbs);
        textViewProtein = findViewById(R.id.textViewProtein);
        textViewFats = findViewById(R.id.textViewFats);
        buttonConfirm = findViewById(R.id.buttonConfirm);

        // Retrieve the data passed from LogFoodActivity
        Intent intent = getIntent();
        String foodName = intent.getStringExtra("foodName");
        int calories = intent.getIntExtra("calories", 0);
        int carbs = intent.getIntExtra("carbs", 0);
        int protein = intent.getIntExtra("protein", 0);
        int fats = intent.getIntExtra("fats", 0);

        // Set the retrieved data to the views
        editTextFoodName.setText(foodName);
        textViewCalories.setText(String.format("Calories: %d", calories));
        textViewCarbs.setText(String.format("Carbs: %d", carbs));
        textViewProtein.setText(String.format("Protein: %d", protein));
        textViewFats.setText(String.format("Fats: %d", fats));

        buttonConfirm.setOnClickListener(v -> {
            // Pass the data back to LogFoodActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("foodName", editTextFoodName.getText().toString());
            resultIntent.putExtra("calories", calories);
            resultIntent.putExtra("carbs", carbs);
            resultIntent.putExtra("protein", protein);
            resultIntent.putExtra("fats", fats);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}