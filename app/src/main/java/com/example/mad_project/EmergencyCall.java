package com.example.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmergencyCall extends Activity {

    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kecemasan); // Load the emergency call layout

        // Find the ImageButton for SOS
        imageButton = findViewById(R.id.imageButton3);

        // Set an OnClickListener to make the call when the button is clicked
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall("60182508259"); // Replace with the desired emergency phone number
            }
        });

        // Setup BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int selectedItemId = item.getItemId();

                // Navigation using if-else
                if (selectedItemId == R.id.nav_home) {
                    Intent main = new Intent(EmergencyCall.this, Main.class);
                    startActivity(main);
                    return true;
                } else if (selectedItemId == R.id.nav_map) {
                    // TODO: Add logic for Map navigation
                    // Intent mapIntent = new Intent(EmergencyCall.this, MapActivity.class);
                    // startActivity(mapIntent);
                    return true;
                } else if (selectedItemId == R.id.nav_profile) {
                    // TODO: Add logic for Profile navigation
                    // Intent profileIntent = new Intent(EmergencyCall.this, ProfileActivity.class);
                    // startActivity(profileIntent);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    // Method to make the emergency call
    private void makeCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));

        // Check for CALL_PHONE permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        } else {
            // Request permission and notify the user
            Toast.makeText(this, "Please grant CALL_PHONE permission.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE}, 1);
        }
    }
}
