package com.example.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    }

    // Method to make the emergency call
    public void makeCall(String phoneNumber) {
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
