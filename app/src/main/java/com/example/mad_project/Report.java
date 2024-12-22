package com.example.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        // grid img and lbl
        String[] labels = {"Banjir", "Ribut", "Tanah Runtuh", "Pencemaran", "Kemarau", "Penutupan Jalan"};
        int[] images = {
                R.drawable.home,
                R.drawable.home,
                R.drawable.home,
                R.drawable.home,
                R.drawable.home,
                R.drawable.home,
        };

        GridView gridView = findViewById(R.id.reportGrid);
        GridAdapter adapter = new GridAdapter(this, labels, images, null);
        gridView.setAdapter(adapter);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Report.this, Main.class);
                startActivity(intent);
            }
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            String label = labels[position];
            Intent intent = new Intent(Report.this, ReportForm.class);
            intent.putExtra("title", label);
            intent.putExtra("id", position);
            startActivity(intent);
        });

        //navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int selectedItem = item.getItemId();
                String itemId = String.valueOf(selectedItem);
                if (itemId.equals(R.id.nav_home)) {
                    Intent intent = new Intent(Report.this, Main.class);
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
}

