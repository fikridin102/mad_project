package com.example.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
//import com.github.mikephil.charting.charts.LineChart;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class GroundMovement extends Activity {

    private TextView lblStatus, lblAlert, intensity, time;
    private Button btnStart, btnStop;
    private Handler handler;
    private Runnable runnable;
    private boolean isMonitoring = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ground_movement);

        // Initialize Views
        lblStatus = findViewById(R.id.lblStatus);
        lblAlert = findViewById(R.id.lblAlert);
        intensity = findViewById(R.id.intensity);
        time = findViewById(R.id.time);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        ImageButton btnBack = findViewById(R.id.btnBack);

//        LineChart lineChart = findViewById(R.id.viewGraph);
//        // Optional: Customize the LineChart appearance
//        lineChart.getDescription().setEnabled(false);
//        lineChart.setTouchEnabled(true);
//        lineChart.setDragEnabled(true);
//        lineChart.setScaleEnabled(true);

        btnBack.setOnClickListener(view -> finish());

        // Set up Handler and Runnable
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                simulateMovementDetection();
                if (isMonitoring) {
                    handler.postDelayed(this, 2000); // Repeat every 2 seconds
                }
            }
        };
        btnStart.setOnClickListener(view -> startMonitoring());
        btnStop.setOnClickListener(view -> stopMonitoring());

        //navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int selectedItem = item.getItemId();
                String itemId = String.valueOf(selectedItem);
                if (itemId.equals(R.id.nav_home)) {
                    Intent intent = new Intent(GroundMovement.this, Main.class);
                    startActivity(intent);
                    return true;
                } else if (itemId.equals(R.id.nav_map)) {
//                    Intent intent = new Intent(Report.this, Main.class);
//                    startActivity(intent);
                    return true;
                } else if (itemId.equals(R.id.nav_profile)) {
//                    Intent intent = new Intent(Report.this, Main.class);
//                    startActivity(intent);
                    return true;
                }

                // After selection, unselect the item by selecting a dummy item
                bottomNavigationView.setSelectedItemId(R.id.nav_home);  // Set to a valid item to reset

                return false;
            }
        });
    }

    private void startMonitoring() {
        if (isMonitoring) {
            Toast.makeText(this, "Monitoring already running", Toast.LENGTH_SHORT).show();
            return;
        }
        isMonitoring = true;
        lblStatus.setText("Status: Monitoring...");
        lblStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        lblAlert.setText("Alert: Monitoring started");
        lblAlert.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        handler.post(runnable);
    }

    private void stopMonitoring() {
        if (!isMonitoring) {
            Toast.makeText(this, "Monitoring is not running", Toast.LENGTH_SHORT).show();
            return;
        }
        isMonitoring = false;
        lblStatus.setText("Status: Monitoring Stopped");
        lblStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        lblAlert.setText("Alert: Monitoring stopped");
        lblAlert.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        handler.removeCallbacks(runnable);
    }

    private void simulateMovementDetection() {
        Random random = new Random();
        float simulatedIntensity = random.nextFloat() * 10; // Random intensity between 0 and 10
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Update UI with simulated data
        intensity.setText(String.format(Locale.getDefault(), "Intensity: %.1f", simulatedIntensity));
        time.setText(String.format("Timestamp: %s", timestamp));

        // Update Alert based on intensity
        if (simulatedIntensity > 5) {
            lblAlert.setText("Alert: High movement detected!");
            lblAlert.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            lblAlert.setText("Alert: No significant movement");
            lblAlert.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }
}

