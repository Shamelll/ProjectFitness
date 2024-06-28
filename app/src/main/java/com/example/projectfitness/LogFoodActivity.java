package com.example.projectfitness;

import android.content.Intent;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class LogFoodActivity extends AppCompatActivity {

    // UI elements
    private EditText editTextFoodName, editTextCalories, editTextCarbs, editTextProtein, editTextFats;
    private Button buttonConfirm, buttonScanBarcode;
    private LinearLayout foodLogContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_food);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // This will close the activity and go back to the previous one

        // Initialize UI elements
        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextCalories = findViewById(R.id.editTextCalories);
        editTextCarbs = findViewById(R.id.editTextCarbs);
        editTextProtein = findViewById(R.id.editTextProtein);
        editTextFats = findViewById(R.id.editTextFats);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonScanBarcode = findViewById(R.id.buttonScanBarcode);
        foodLogContainer = findViewById(R.id.foodLogContainer);

        // Click listener for the confirm button
        buttonConfirm.setOnClickListener(v -> {
            // Retrieve input values
            String foodName = editTextFoodName.getText().toString();
            String calories = editTextCalories.getText().toString();
            String carbs = editTextCarbs.getText().toString();
            String protein = editTextProtein.getText().toString();
            String fats = editTextFats.getText().toString();
            String amount = "100"; // Default amount is 100g
            String id = String.valueOf(System.currentTimeMillis());

            // Validate inputs (example: ensure they are not empty)
            if (foodName.isEmpty() || calories.isEmpty() || carbs.isEmpty() || protein.isEmpty() || fats.isEmpty()) {
                Toast.makeText(LogFoodActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Add food log to the layout and save it
                addFoodLog(foodName, calories, carbs, protein, fats, amount, id);
                saveFoodLog(foodName, calories, carbs, protein, fats, amount, id);

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
            }
        });

        // Click listener for the scan barcode button
        buttonScanBarcode.setOnClickListener(v -> {
            // Implement barcode scanning functionality
            Intent intent = new Intent(LogFoodActivity.this, BarcodeScannerActivity.class);
            startActivityForResult(intent, 1);
        });

        // Load existing food logs
        loadFoodLogs();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Handle barcode scan result
            String barcode = data.getStringExtra("barcode");
            new FetchFoodDataTask().execute(barcode);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            // Handle scanned food result
            String foodName = data.getStringExtra("foodName");
            int calories = data.getIntExtra("calories", 0);
            int carbs = data.getIntExtra("carbs", 0);
            int protein = data.getIntExtra("protein", 0);
            int fats = data.getIntExtra("fats", 0);
            int amount = data.getIntExtra("amount", 100);
            String id = String.valueOf(System.currentTimeMillis());

            // Add and save the food log
            addFoodLog(foodName, String.valueOf(calories), String.valueOf(carbs), String.valueOf(protein), String.valueOf(fats), String.valueOf(amount), id);
            saveFoodLog(foodName, String.valueOf(calories), String.valueOf(carbs), String.valueOf(protein), String.valueOf(fats), String.valueOf(amount), id);
            updateProgressBars(calories, carbs, protein, fats, false);

            Toast.makeText(LogFoodActivity.this, "Food logged!", Toast.LENGTH_SHORT).show();

            // Notify MainActivity to refresh the progress bars
            setResult(RESULT_OK);
        }
    }

    // AsyncTask to fetch food data from the Open Food Facts API
    private class FetchFoodDataTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String barcode = params[0];
            String urlString = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";
            try {
                URL url = new URL(urlString);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                // Set the SSL context with the custom TrustManager
                SSLContext sslContext = CustomTrustManager.getSSLContext(getApplicationContext());
                urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return new JSONObject(result.toString());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                try {
                    // Parse the JSON result
                    JSONObject product = result.getJSONObject("product");
                    String foodName = product.getString("product_name");
                    int calories = product.getJSONObject("nutriments").getInt("energy-kcal_100g");
                    int carbs = product.getJSONObject("nutriments").getInt("carbohydrates_100g");
                    int protein = product.getJSONObject("nutriments").getInt("proteins_100g");
                    int fats = product.getJSONObject("nutriments").getInt("fat_100g");

                    // Open the ScannedFoodActivity to display the fetched data
                    Intent intent = new Intent(LogFoodActivity.this, ScannedFoodActivity.class);
                    intent.putExtra("foodName", foodName);
                    intent.putExtra("calories", calories);
                    intent.putExtra("carbs", carbs);
                    intent.putExtra("protein", protein);
                    intent.putExtra("fats", fats);
                    startActivityForResult(intent, 2);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LogFoodActivity.this, "Error parsing food data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LogFoodActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Add a food log entry to the UI
    private void addFoodLog(String foodName, String calories, String carbs, String protein, String fats, String amount, String id) {
        View foodLogView = getLayoutInflater().inflate(R.layout.food_log_item, foodLogContainer, false);
        TextView foodNameTextView = foodLogView.findViewById(R.id.foodNameTextView);
        TextView foodDetailsTextView = foodLogView.findViewById(R.id.foodDetailsTextView);
        Button deleteFoodLogButton = foodLogView.findViewById(R.id.deleteFoodLogButton);

        // Set the food details
        foodNameTextView.setText(foodName);
        foodDetailsTextView.setText(String.format("%sg, Calories: %sg, Fat: %sg, Carbs: %sg, Protein: %sg", amount, calories, fats, carbs, protein));
        foodLogView.setTag(id);

        // Click listener for the delete button
        deleteFoodLogButton.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Delete Food Log")
                    .setMessage("Are you sure you want to delete this food log?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Remove the food log from the layout
                        foodLogContainer.removeView(foodLogView);
                        // Remove the food log from SharedPreferences
                        removeFoodLog(id);
                        // Update progress bars
                        updateProgressBars(Integer.parseInt(calories), Integer.parseInt(carbs), Integer.parseInt(protein), Integer.parseInt(fats), true);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
        foodLogContainer.addView(foodLogView);
    }

    // Save a food log entry to SharedPreferences
    private void saveFoodLog(String foodName, String calories, String carbs, String protein, String fats, String amount, String id) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String foodLogs = preferences.getString("foodLogs", "[]");
        try {
            JSONArray foodLogsArray = new JSONArray(foodLogs);
            JSONObject foodLog = new JSONObject();
            foodLog.put("id", id);
            foodLog.put("foodName", foodName);
            foodLog.put("calories", calories);
            foodLog.put("carbs", carbs);
            foodLog.put("protein", protein);
            foodLog.put("fats", fats);
            foodLog.put("amount", amount);

            foodLogsArray.put(foodLog);
            editor.putString("foodLogs", foodLogsArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Load existing food logs from SharedPreferences
    private void loadFoodLogs() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String foodLogs = preferences.getString("foodLogs", "[]");
        try {
            JSONArray foodLogsArray = new JSONArray(foodLogs);
            for (int i = 0; i < foodLogsArray.length(); i++) {
                JSONObject foodLog = foodLogsArray.getJSONObject(i);
                String id = foodLog.getString("id");
                String foodName = foodLog.getString("foodName");
                String calories = foodLog.getString("calories");
                String carbs = foodLog.getString("carbs");
                String protein = foodLog.getString("protein");
                String fats = foodLog.getString("fats");
                String amount = foodLog.getString("amount");

                // Add each food log to the UI
                addFoodLog(foodName, calories, carbs, protein, fats, amount, id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Remove a food log entry from SharedPreferences
    private void removeFoodLog(String id) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String foodLogs = preferences.getString("foodLogs", "[]");
        try {
            JSONArray foodLogsArray = new JSONArray(foodLogs);
            JSONArray newFoodLogsArray = new JSONArray();
            for (int i = 0; i < foodLogsArray.length(); i++) {
                JSONObject foodLog = foodLogsArray.getJSONObject(i);
                if (!foodLog.getString("id").equals(id)) {
                    newFoodLogsArray.put(foodLog);
                }
            }
            editor.putString("foodLogs", newFoodLogsArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Update the progress bars in the main activity
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