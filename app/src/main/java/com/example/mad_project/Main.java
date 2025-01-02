package com.example.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // grid img and lbl
        String[] labels = {"Aduan", "Kecemasan", "Pergerakan Tanah", "Panduan"};
        int[] images = {
                R.drawable.report,
                R.drawable.emergency,
                R.drawable.landslide,
                R.drawable.panduan,
        };

        // Create a map of grid item index to target activity
        Map<Integer, Class<?>> activityMap = new HashMap<>();
        activityMap.put(0, Report.class);
        activityMap.put(1, EmergencyCall.class);
        activityMap.put(2, GroundMovement.class);
        activityMap.put(3, PanduanActivity.class);

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
                    Intent main = new Intent(Main.this, Main.class);
                    startActivity(main);
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
    }

}

