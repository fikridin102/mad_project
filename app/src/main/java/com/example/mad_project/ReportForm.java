package com.example.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ReportForm extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_form);

        TextView label1 = findViewById(R.id.label1);
        EditText txtType = findViewById(R.id.txtType);
        EditText txtFullName = findViewById(R.id.txtFullName);
        EditText txtEmail = findViewById(R.id.txtEmail);
        EditText txtAddress = findViewById(R.id.txtAddress);
        EditText txtDetail = findViewById(R.id.txtDetail);
        Button btnUpload = findViewById(R.id.btnUpload);
        Button btnSubmit = findViewById(R.id.btnSubmit);
        Button btnClear = findViewById(R.id.btnClear);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Receive parameters from Report
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        int position = intent.getIntExtra("id", -1);
        if (title != null) {
            label1.setText("Aduan: " + title);
        }
        if (position == 6){
            txtType.setVisibility(View.VISIBLE);
        } else {
            txtType.setVisibility(View.INVISIBLE);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent report = new Intent(ReportForm.this, Report.class);
                startActivity(report);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReportForm.this, "Upload functionality coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture inputs
                String type = txtType.getText().toString().trim();
                String fullName = txtFullName.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String address = txtAddress.getText().toString().trim();
                String detail = txtDetail.getText().toString().trim();

                // Validate inputs
                if (fullName.isEmpty() || email.isEmpty() || address.isEmpty() || detail.isEmpty()) {
                    Toast.makeText(ReportForm.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                } else if (type.isEmpty()) {
                    Toast.makeText(ReportForm.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ReportForm.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle submission logic
                    Toast.makeText(ReportForm.this, "Report submitted successfully!", Toast.LENGTH_SHORT).show();
                    Intent report = new Intent(ReportForm.this, Report.class);
                    startActivity(report);
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtType.setText("");
                txtFullName.setText("");
                txtEmail.setText("");
                txtAddress.setText("");
                txtDetail.setText("");
                Toast.makeText(ReportForm.this, "Form cleared.", Toast.LENGTH_SHORT).show();
            }
        });

        //navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int selectedItem = item.getItemId();
                String itemId = String.valueOf(selectedItem);
                if (itemId.equals(R.id.nav_home)) {
                    Intent main = new Intent(ReportForm.this, Main.class);
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
                return false;
            }
        });
    }
}

