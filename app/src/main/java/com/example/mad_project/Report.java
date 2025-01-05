package com.example.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class Report extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnView = findViewById(R.id.btnView);

        // grid img and lbl
        String[] labels = {"Banjir", "Ribut", "Tanah Runtuh", "Pencemaran", "Kemarau", "Penutupan Jalan", "Lain-lain"};
        int[] images = {
                R.drawable.flood,
                R.drawable.wind,
                R.drawable.landslide,
                R.drawable.virus,
                R.drawable.thermostat,
                R.drawable.road,
                R.drawable.shield,
        };

        GridView gridView = findViewById(R.id.reportGrid);
        GridAdapter adapter = new GridAdapter(this, labels, images, null);
        gridView.setAdapter(adapter);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(Report.this, Main.class);
                startActivity(main);
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportDetail = new Intent(Report.this, ReportDetail.class);
                startActivity(reportDetail);
            }
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            String label = labels[position];
            Intent reportform = new Intent(Report.this, ReportForm.class);
            reportform.putExtra("title", label);
            reportform.putExtra("id", position);
            startActivity(reportform);
        });

        //navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int selectedItem = item.getItemId();
                String itemId = String.valueOf(selectedItem);
                if (itemId.equals(R.id.nav_home)) {
                    Intent main = new Intent(Report.this, Main.class);
                    startActivity(main);
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
}

