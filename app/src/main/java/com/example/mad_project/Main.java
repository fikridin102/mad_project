package com.example.mad_project;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);  // Set the main layout

        // grid img and lbl
        String[] labels = {"Report"};
        int[] images = {
                R.drawable.report,
        };

        // Create a map of grid item index to target activity
        Map<Integer, Class<?>> activityMap = new HashMap<>();
        activityMap.put(0, Report.class); // Report button

        // Initialize GridView and set the custom adapter with the map
        GridView gridView = findViewById(R.id.funcGrid);
        GridAdapter adapter = new GridAdapter(this, labels, images, activityMap);
        gridView.setAdapter(adapter);

        //navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int selectedItem = item.getItemId();
                String itemId = String.valueOf(selectedItem);
                if (itemId.equals(R.id.nav_home)) {
//                    Intent intent = new Intent(MainActivity.this, Report.class);
//                    startActivity(intent);
                    return true;
                } else if (itemId.equals(R.id.nav_map)) {
//                    Intent intent = new Intent(MainActivity.this, Report.class);
//                    startActivity(intent);
                    return true;
                } else if (itemId.equals(R.id.nav_profile)) {
//                    Intent intent = new Intent(MainActivity.this, Report.class);
//                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        // Emergency Call functionality for 'kecemasan' layout
        setEmergencyCallFunctionality();
    }

    private void setEmergencyCallFunctionality() {
        // Initialize Emergency Call view from 'kecemasan.xml'
        ImageButton imageButton = findViewById(R.id.imageButton3);

        // Create an instance of EmergencyCall
        EmergencyCall emergencyCall = new EmergencyCall(this);

        // Set click listener for the ImageButton to make an emergency call
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emergencyCall.makeCall("60182508259"); // Replace with the desired phone number
            }
        });
    }
}
