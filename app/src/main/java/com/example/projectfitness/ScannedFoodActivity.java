package com.example.projectfitness;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;


public class ScannedFoodActivity extends AppCompatActivity {

    // UI elements
    private EditText editTextFoodName, editTextAmount;
    private TextView textViewCalories, textViewCarbs, textViewProtein, textViewFats;
    private Button buttonConfirm;

    // Base nutrition values per 100g
    private int baseCalories, baseCarbs, baseProtein, baseFats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_food);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // This will close the activity and go back to the previous one

        // Initialize UI elements
        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextAmount = findViewById(R.id.editTextAmount);
        textViewCalories = findViewById(R.id.textViewCalories);
        textViewCarbs = findViewById(R.id.textViewCarbs);
        textViewProtein = findViewById(R.id.textViewProtein);
        textViewFats = findViewById(R.id.textViewFats);
        buttonConfirm = findViewById(R.id.buttonConfirm);

        // Retrieve the data passed from LogFoodActivity
        Intent intent = getIntent();
        String foodName = intent.getStringExtra("foodName");
        baseCalories = intent.getIntExtra("calories", 0);
        baseCarbs = intent.getIntExtra("carbs", 0);
        baseProtein = intent.getIntExtra("protein", 0);
        baseFats = intent.getIntExtra("fats", 0);

        // Set the retrieved data to the views
        editTextFoodName.setText(foodName);
        editTextAmount.setText("100"); // Default amount is 100g
        updateNutritionValues(100); // Update nutrition values for 100g

        // Add a TextWatcher to editTextAmount to recalculate nutrition values
        editTextAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    // Update nutrition values based on the new amount
                    int amount = Integer.parseInt(s.toString());
                    updateNutritionValues(amount);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Click listener for the confirm button
        buttonConfirm.setOnClickListener(v -> {
            // Pass the data back to LogFoodActivity
            int amount = Integer.parseInt(editTextAmount.getText().toString());
            Intent resultIntent = new Intent();
            resultIntent.putExtra("foodName", editTextFoodName.getText().toString());
            resultIntent.putExtra("calories", (baseCalories * amount) / 100);
            resultIntent.putExtra("carbs", (baseCarbs * amount) / 100);
            resultIntent.putExtra("protein", (baseProtein * amount) / 100);
            resultIntent.putExtra("fats", (baseFats * amount) / 100);
            resultIntent.putExtra("amount", amount); // Pass the amount back
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    // Update the nutrition values based on the specified amount
    private void updateNutritionValues(int amount) {
        int calories = (baseCalories * amount) / 100;
        int carbs = (baseCarbs * amount) / 100;
        int protein = (baseProtein * amount) / 100;
        int fats = (baseFats * amount) / 100;

        textViewCalories.setText(String.format("Calories: %d", calories));
        textViewCarbs.setText(String.format("Carbs: %d", carbs));
        textViewProtein.setText(String.format("Protein: %d", protein));
        textViewFats.setText(String.format("Fats: %d", fats));
    }
}