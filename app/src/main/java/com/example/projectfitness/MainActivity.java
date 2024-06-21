package com.example.projectfitness;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar progressCalories = findViewById(R.id.progressCalories);
        ProgressBar progressProtein = findViewById(R.id.progressProtein);
        ProgressBar progressCarbs = findViewById(R.id.progressCarbs);
        ProgressBar progressFats = findViewById(R.id.progressFats);
        ProgressBar progressSteps = findViewById(R.id.progressSteps);
        Button stepsButton = findViewById(R.id.stepsButton);
        Button exerciseButton = findViewById(R.id.exerciseButton);
        Button logFoodButton = findViewById(R.id.logFoodButton);

        TextView textCalories = findViewById(R.id.textCalories);
        TextView textProtein = findViewById(R.id.textProtein);
        TextView textCarbs = findViewById(R.id.textCarbs);
        TextView textFats = findViewById(R.id.textFats);

        // Example usage
        //progressCalories.setProgress(250);
        //progressProtein.setProgress(15);
        //progressCarbs.setProgress(31);
        //progressFats.setProgress(6);

        //textCalories.setText("250\nRemaining");
        //textProtein.setText("15\nRemaining");
        //textCarbs.setText("31\nRemaining");
        //textFats.setText("6\nRemaining");

        // Handle steps button click
        stepsButton.setOnClickListener(v -> {

        });

        // Handle exercise button click
        exerciseButton.setOnClickListener(v -> {

        });

        // Handle log food button click
        logFoodButton.setOnClickListener(v -> {

        });
    }
}