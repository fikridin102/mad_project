package com.example.mad_project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class EmergencyCall {
    private Context context;

    public EmergencyCall(Context context) {
        this.context = context;
    }

    public void makeCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));

        // Check for CALL_PHONE permission
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            context.startActivity(callIntent);
        } else {
            // Request permission and notify the user
            Toast.makeText(context, "Please grant CALL_PHONE permission.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions((android.app.Activity) context,
                    new String[]{android.Manifest.permission.CALL_PHONE}, 1);
        }
    }
}

