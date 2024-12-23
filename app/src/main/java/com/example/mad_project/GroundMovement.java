package com.example.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;
import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class GroundMovement extends Activity {

    private TextView lblStatus, lblAlert, intensity, time;
    private Button btnStart, btnStop;
    private LineChart lineChart;
    private TableLayout tableLayout;
    private Handler handler;
    private Runnable runnable;
    private boolean isMonitoring = false;

    // List to hold intensity data
    private ArrayList<Entry> intensityData;

    // Cache to hold the last 10 intensity inputs
    private ArrayList<Entry> intensityCache;
    private int maxCacheSize = 18; // Cache size limit
    private int readingSequence = 1; // Start from the first reading


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

        initializeLineChart();

        // Set up Handler and Runnable
        handler = new Handler();
        intensityData = new ArrayList<>();
        intensityCache = new ArrayList<>();
        runnable = new Runnable() {
            @Override
            public void run() {
                simulateMovementDetection();
                if (isMonitoring) {
                    handler.postDelayed(this, 5000); // Update every 5 seconds
                }
            }
        };

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(GroundMovement.this, Main.class);
                startActivity(main);
            }
        });
        btnStart.setOnClickListener(view -> startMonitoring());
        btnStop.setOnClickListener(view -> stopMonitoring());

        //navbar
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

    private void initializeLineChart() {
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);

        // X-Axis setup (representing the sequence of intensities)
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // Set interval to 1 for each intensity reading
        xAxis.setLabelCount(10);  // Show labels for 10 intensity readings

        // Custom formatter for X-axis to show sequence of readings (1st, 2nd, etc.)
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.0f", value); // Showing the sequence (1, 2, 3...)
            }
        });


        // Y-Axis setup (representing intensity rate from 1 to 10)
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setLabelCount(11, false);  // Set 11 labels to cover the range 0-10 (inclusive)
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f); // Minimum intensity rate (0)
        leftAxis.setAxisMaximum(10f); // Maximum intensity rate (10)
        leftAxis.setGranularity(1f); // Set granularity to 1 to ensure only whole numbers are shown

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false); // Disable right axis

        // Initialize LineData
        LineData data = new LineData();
        lineChart.setData(data);
        lineChart.animateX(500); // Animates horizontal updates over 500 ms
    }

    private void addEntryToGraph(float intensityValue, int sequenceNumber) {
        LineData data = lineChart.getData();
        if (data != null) {
            LineDataSet dataSet = (LineDataSet) data.getDataSetByIndex(0);
            if (dataSet == null) {
                dataSet = createSet();
                data.addDataSet(dataSet);
            }

            // Add new entry to the cache
            if (intensityCache.size() >= maxCacheSize) {
                intensityCache.remove(0); // Remove the oldest entry if max cache size is reached
            }

            // Add the sequence number as the X value and intensity as the Y value
            intensityCache.add(new Entry(sequenceNumber, intensityValue));

            refreshGraph();

            // Update the dataset with the cache data
            dataSet.clear();
            for (Entry entry : intensityCache) {
                dataSet.addEntry(entry);
            }

            // Notify data changes
            data.notifyDataChanged();

            // Refresh the chart
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(maxCacheSize); // Show the last `maxCacheSize` points
            lineChart.moveViewToX(data.getEntryCount() - 1); // Move to the latest entry
            lineChart.invalidate(); // Redraw the chart
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
        handler.post(runnable);

        // Stop monitoring after 90 seconds (1 minute and 30 seconds)
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopMonitoring();
            }
        }, 90 * 1000); // 90 seconds in milliseconds
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
    }

    private void simulateMovementDetection() {
        Random random = new Random();
        float simulatedIntensity = random.nextFloat() * 9 + 1; // Random intensity between 1 and 10
        int sequenceNumber = readingSequence++; // Increment the sequence number for each reading

        // Get the current timestamp
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Update UI with simulated data
        intensity.setText(String.format(Locale.getDefault(), "Keamatan Tanah: %.1f", simulatedIntensity));
        time.setText(String.format("Masa: %s", timestamp));

        // Update Alert based on intensity
        if (simulatedIntensity > 5) {
            lblAlert.setText("Amaran: Pergerakan tinggi dikesan!");
            lblAlert.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            lblAlert.setText("Tiada pergerakan");
            lblAlert.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }

        // Plot the intensity on the graph
        addEntryToGraph(simulatedIntensity, sequenceNumber);

        // Update the TableLayout with the recorded data
        updateDataTable(sequenceNumber, timestamp, simulatedIntensity);
    }

    private void refreshGraph() {
        LineData data = lineChart.getData();
        if (data != null) {
            LineDataSet dataSet = (LineDataSet) data.getDataSetByIndex(0);
            if (dataSet == null) {
                dataSet = createSet();
                data.addDataSet(dataSet);
            }

            // Clear existing entries and repopulate from cache
            dataSet.clear();
            for (Entry entry : intensityCache) {
                dataSet.addEntry(entry);
            }

            // Notify data changes
            data.notifyDataChanged();

            // Refresh the chart
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(maxCacheSize); // Show the last `maxCacheSize` points
            lineChart.moveViewToX(data.getEntryCount() - 1); // Move to the latest entry
            lineChart.invalidate(); // Redraw the chart
        }
    }

    private void updateDataTable(int sequenceNumber, String timestamp, float intensityValue) {
        // Get the reference to the TableLayout
        tableLayout = findViewById(R.id.tableLayout);

        // Create a new TableRow for each entry
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setPadding(10, 10, 10, 10);

        // Create and add a TextView for the sequence number
        TextView sequenceTextView = new TextView(this);
        sequenceTextView.setText(String.valueOf(sequenceNumber));
        sequenceTextView.setPadding(8, 8, 8, 8);
        sequenceTextView.setGravity(Gravity.CENTER); // Center align the text
        sequenceTextView.setLayoutParams(new TableRow.LayoutParams(
                0, // Weight-based width
                TableRow.LayoutParams.WRAP_CONTENT, // Height
                1f // Weight
        ));
        tableRow.addView(sequenceTextView);

        // Create and add a TextView for the timestamp
        TextView timestampTextView = new TextView(this);
        timestampTextView.setText(timestamp);
        timestampTextView.setPadding(8, 8, 8, 8);
        timestampTextView.setGravity(Gravity.CENTER); // Center align the text
        timestampTextView.setLayoutParams(new TableRow.LayoutParams(
                0, // Weight-based width
                TableRow.LayoutParams.WRAP_CONTENT, // Height
                2f // Weight
        ));
        tableRow.addView(timestampTextView);

        // Create and add a TextView for the intensity value
        TextView intensityTextView = new TextView(this);
        intensityTextView.setText(String.format(Locale.getDefault(), "%.1f", intensityValue));
        intensityTextView.setPadding(8, 8, 8, 8);
        intensityTextView.setGravity(Gravity.CENTER); // Center align the text
        intensityTextView.setLayoutParams(new TableRow.LayoutParams(
                0, // Weight-based width
                TableRow.LayoutParams.WRAP_CONTENT, // Height
                1f // Weight
        ));
        tableRow.addView(intensityTextView);

        // Add the TableRow to the TableLayout
        tableLayout.addView(tableRow);
    }

}
