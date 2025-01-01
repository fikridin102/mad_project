package com.example.mad_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class GroundMovement extends Activity implements SensorEventListener {

    private TextView lblStatus, lblAlert, intensity, time;
    private Button btnStart, btnStop;
    private LineChart lineChart;
    private Handler handler;
    private Runnable runnable;
    private boolean isMonitoring = false;

    // List to hold intensity data
    private ArrayList<Float> intensityAverage; // ArrayList to store intensity averages
    private ArrayList<Entry> intensityData; // Cache for intensity data
    private ArrayList<String> timeData; // To hold time data corresponding to intensity
    private int maxCacheSize = 18; // Cache size limit
    private int readingSequence = 0; // Start from the first reading
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private long startTime;
    private long elapsedTime = 0;
    private final long monitoringDuration = 90 * 1000; // 90 seconds (1.5 minutes)
    private TableLayout tableLayout;

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
        lineChart = findViewById(R.id.viewGraph);
        tableLayout = findViewById(R.id.tableLayout);
        Button btnYoutube = findViewById(R.id.btnYoutube);

        // Initialize intensityData and timeData
        intensityAverage = new ArrayList<>();
        intensityData = new ArrayList<>();
        timeData = new ArrayList<>();
        
        // Initialize SensorManager and Accelerometer
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) {
            Log.e("GroundMovement", "SensorManager is null");
            return;
        }

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensor == null) {
            Log.e("GroundMovement", "Accelerometer sensor not available");
            btnStart.setEnabled(false);
            btnStop.setEnabled(false);
            return;
        }

        Log.d("GroundMovement", "Accelerometer sensor initialized");

        // Initialize Handler and Runnable
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isMonitoring) {
                    long currentTime = SystemClock.elapsedRealtime();
                    elapsedTime = currentTime - startTime; // Calculate elapsed time

                    if (elapsedTime < monitoringDuration) {
                        // Continue updating every 5 seconds
                        handler.postDelayed(this, 5000); // Update every 5 seconds
                    } else {
                        // Stop monitoring after 90 seconds
                        stopMonitoring();
                    }
                }
            }
        };

        btnBack.setOnClickListener(v -> {
            Intent main = new Intent(GroundMovement.this, Main.class);
            startActivity(main);
        });

        btnYoutube.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=aV89_yUJunM"));
            startActivity(intent);
        });

        btnStart.setOnClickListener(view -> startMonitoring());

        btnStop.setOnClickListener(view -> stopMonitoring());

        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int selectedItem = item.getItemId();
            if (selectedItem == R.id.nav_home) {
                Intent main = new Intent(GroundMovement.this, Main.class);
                startActivity(main);
                return true;
            }
            return false;
        });
    }

    private void initializeChart() {
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(maxCacheSize);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.0f", value);
            }
        });

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setLabelCount(11, false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(25f);
        leftAxis.setGranularity(1f);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        LineData data = new LineData();
        lineChart.setData(data);
        lineChart.animateX(500);
    }

    private void addEntryToGraph(float intensityValue, int sequenceNumber) {
        LineData data = lineChart.getData();
        if (data != null) {
            LineDataSet dataSet = (LineDataSet) data.getDataSetByIndex(0);
            if (dataSet == null) {
                dataSet = createSet();
                data.addDataSet(dataSet);
            }

            dataSet.addEntry(new Entry(sequenceNumber, intensityValue));
            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(maxCacheSize);
            lineChart.moveViewToX(data.getEntryCount() - 1);
            lineChart.invalidate();
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(new ArrayList<>(), "Keamatan / Masa");
        set.setLineWidth(2.5f);
        set.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        set.setCircleColor(getResources().getColor(android.R.color.holo_blue_dark));
        set.setCircleRadius(5f);
        set.setDrawValues(false);
        set.setValueTextSize(10f);
        return set;
    }

    private void startMonitoring() {
        if (isMonitoring) {
            Toast.makeText(this, "Pemantauan sedang dijalankan", Toast.LENGTH_SHORT).show();
            return;
        }
        isMonitoring = true;
        lblStatus.setText("Status: Sedang memantau...");
        lblStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        lblAlert.setText("Pemantauan bermula");
        lblAlert.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        startTime = SystemClock.elapsedRealtime();
        elapsedTime = 0;
        readingSequence = 0; // Reset reading sequence
        intensityData.clear(); // Clear previous data
        timeData.clear(); // Clear previous time data
        initializeChart();
        handler.post(runnable);
    }

    private void stopMonitoring() {
        if (!isMonitoring) {
            Toast.makeText(this, "Pemantauan tidak dijalankan", Toast.LENGTH_SHORT).show();
            return;
        }
        isMonitoring = false;
        lblStatus.setText("Status: Pemantauan tamat");
        lblStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        lblAlert.setText("Pemantauan tamat");
        lblAlert.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        handler.removeCallbacks(runnable);

        long elapsedTime = SystemClock.elapsedRealtime() - startTime;
        long minutes = elapsedTime / 60000; // Convert milliseconds to minutes
        long seconds = (elapsedTime % 60000) / 1000;
        String timeString = String.format(Locale.getDefault(), "Total masa pemantauan: %d menit %d detik", minutes, seconds);
        Toast.makeText(this, timeString, Toast.LENGTH_LONG).show();

        displayResultsInTable();
    }

    //masuk db
    private void displayResultsInTable() {
        // Clear the table layout
        int childCount = tableLayout.getChildCount();
        for (int i = childCount - 1; i > 0; i--) {
            tableLayout.removeViewAt(i);
        }

        // Populate table with sequence number, time, and intensity data
        for (int i = 0; i < intensityData.size(); i++) {
            TableRow row = new TableRow(this);

            // Create TextView for sequence number
            TextView sequenceValue = new TextView(this);
            sequenceValue.setText(String.format(Locale.getDefault(), "%d", i + 1));
            sequenceValue.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)); // Weight 1
            sequenceValue.setGravity(Gravity.CENTER);

            // Create TextView for time
            TextView timeValue = new TextView(this);
            timeValue.setText(timeData.get(i));
            timeValue.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f)); // Weight 2
            timeValue.setGravity(Gravity.CENTER);

            // Create TextView for intensity
            TextView intensityValue = new TextView(this);
            intensityValue.setText(String.format(Locale.getDefault(), "%.1f", intensityData.get(i).getY()));
            intensityValue.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)); // Weight 1
            intensityValue.setGravity(Gravity.CENTER);

            // Add TextViews to the row
            row.addView(sequenceValue);
            row.addView(timeValue);
            row.addView(intensityValue);

            // Add the row to the table layout
            tableLayout.addView(row);
        }

        // Calculate total sequence
        int totalSequence = intensityData.size();

        // Calculate average intensity
        float totalIntensity = 0f;
        for (int i = 0; i < intensityData.size(); i++) {
            totalIntensity += intensityData.get(i).getY();
        }
        float averageIntensity = totalSequence > 0 ? totalIntensity / totalSequence : 0;

        // Retrieve start and end time
        String startTime = timeData.isEmpty() ? "" : timeData.get(0);
        String endTime = timeData.isEmpty() ? "" : timeData.get(timeData.size() - 1);

        // Add these details to the hashmap (hmInfo)
        HashMap hmInfo = new HashMap();
        hmInfo.put("Total Sequence", totalSequence);
        hmInfo.put("Average Intensity", averageIntensity);
        hmInfo.put("Start", startTime);
        hmInfo.put("End", endTime);

        // Get the current date and time in the format ddMMyyyy/HHmmss
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault());
        String dateTimeId = dateFormat.format(new Date());

        // Insert data into Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mad-project-2fa59-default-rtdb.firebaseio.com/");
        DatabaseReference dbRef = database.getReference("Ground Movement").child(dateTimeId);
        dbRef.setValue(hmInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(GroundMovement.this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GroundMovement.this, "Failed to insert data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // Add this variable to track the last recorded time
    private long lastRecordedTime = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isMonitoring) {
            // Get accelerometer values
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calculate intensity based on accelerometer data
            float intensityValue = (float) Math.sqrt(x * x + y * y + z * z);
            String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            // Update UI with actual sensor data
            runOnUiThread(() -> {
                intensity.setText(String.format(Locale.getDefault(), "Keamatan Tanah: %.1f", intensityValue));
                time.setText(String.format("Masa: %s", timestamp));

                long currentTime = SystemClock.elapsedRealtime();

                // For the first reading, add it directly
                if (readingSequence == 0) {
                    intensityData.add(new Entry(readingSequence, intensityValue));
                    timeData.add(timestamp);
                    addEntryToGraph(intensityValue, readingSequence);
                    readingSequence++;
                    lastRecordedTime = currentTime; // Initialize last recorded time
                } else {
                    // Calculate the time difference in seconds
                    long timeDiff = (currentTime - lastRecordedTime) / 1000;

                    // Add the current intensity value to the average list for each second
                    for (int i = 0; i < timeDiff; i++) {
                        intensityAverage.add(intensityValue);
                    }

                    // Check if 5 seconds have passed
                    if (currentTime - lastRecordedTime >= 5000 && readingSequence < maxCacheSize) {
                        // Calculate the average intensity
                        float sum = 0;
                        for (Float avg : intensityAverage) {
                            sum += avg;
                        }
                        float averageIntensity = sum / intensityAverage.size();

                        // Store the average intensity in intensityData
                        intensityData.add(new Entry(readingSequence, averageIntensity));
                        timeData.add(timestamp);
                        addEntryToGraph(averageIntensity, readingSequence);
                        readingSequence++;

                        // Clear the intensity average list
                        intensityAverage.clear();

                        // Update last recorded time
                        lastRecordedTime = currentTime;
                    }
                }

                // Update alert based on intensity value
                if (intensityValue > 10) {
                    lblAlert.setText("Amaran: Pergerakan tinggi dikesan!");
                    lblAlert.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    lblAlert.setText("Tiada pergerakan");
                    lblAlert.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                }
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if necessary
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}