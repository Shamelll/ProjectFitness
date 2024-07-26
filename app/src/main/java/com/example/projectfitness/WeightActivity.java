package com.example.projectfitness;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeightActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // This will close the activity and go back to the previous one

        EditText editTextWeight = findViewById(R.id.editTextWeight);
        Button buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weightStr = editTextWeight.getText().toString();
                if (!weightStr.isEmpty()) {
                    double weight = Double.parseDouble(weightStr);
                    saveWeight(weight);
                } else {
                    Toast.makeText(WeightActivity.this, "Please enter a valid weight", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveWeight(double weight) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String weightLogs = preferences.getString("weightLogs", "[]");
        JSONArray weightLogsArray;

        try {
            weightLogsArray = new JSONArray(weightLogs);
            String currentDate = new SimpleDateFormat("dd MMM", Locale.getDefault()).format(new Date());
            boolean entryExists = false;

            for (int i = 0; i < weightLogsArray.length(); i++) {
                JSONObject weightLog = weightLogsArray.getJSONObject(i);
                String logDate = weightLog.getString("date");

                if (logDate.equals(currentDate)) {
                    weightLog.put("weight", weight);
                    weightLogsArray.put(i, weightLog);
                    entryExists = true;
                    break;
                }
            }

            if (!entryExists) {
                JSONObject newLog = new JSONObject();
                newLog.put("date", currentDate);
                newLog.put("weight", weight);
                weightLogsArray.put(newLog);
            }

            editor.putString("weightLogs", weightLogsArray.toString());
            editor.apply();

            Toast.makeText(this, "Weight saved successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity after saving
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving weight", Toast.LENGTH_SHORT).show();
        }
    }
}
