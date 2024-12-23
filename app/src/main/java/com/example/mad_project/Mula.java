package com.example.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Mula extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        // Find the button by its ID
        Button startButton = findViewById(R.id.button);

        // Set an OnClickListener on the button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to handle the button click
                onStartButtonClicked();
            }
        });
    }

    // Function to handle the button click
    private void onStartButtonClicked() {
        // Navigate to another activity (e.g., EmergencyCallActivity)
        Intent intent = new Intent(Mula.this, Main.class);
        startActivity(intent);
    }
}
