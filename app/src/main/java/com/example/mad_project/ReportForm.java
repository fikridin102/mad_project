package com.example.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class ReportForm extends Activity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private ImageView imgPreview;

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
        imgPreview = findViewById(R.id.imgPreview);

        // Receive parameters from Report
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        int position = intent.getIntExtra("id", -1);
        if (title != null) {
            label1.setText("Aduan: " + title);
        }
        if (position == 6) {
            txtType.setVisibility(View.VISIBLE);
        } else {
            txtType.setVisibility(View.INVISIBLE);
        }

        btnBack.setOnClickListener(v -> {
            Intent report = new Intent(ReportForm.this, Report.class);
            startActivity(report);
        });

        btnUpload.setOnClickListener(v -> {
            // Open dialog to choose Camera or Gallery
            String[] options = {"Camera", "Gallery"};
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ReportForm.this);
            builder.setTitle("Upload Image")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            // Open Camera
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                        } else if (which == 1) {
                            // Open Gallery
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            galleryIntent.setType("image/*");
                            startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
                        }
                    });
            builder.show();
        });

        btnSubmit.setOnClickListener(v -> {
            String type = txtType.getText().toString().trim();
            String fullName = txtFullName.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String address = txtAddress.getText().toString().trim();
            String detail = txtDetail.getText().toString().trim();

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
        });

        btnClear.setOnClickListener(v -> {
            txtType.setText("");
            txtFullName.setText("");
            txtEmail.setText("");
            txtAddress.setText("");
            txtDetail.setText("");
            imgPreview.setImageDrawable(null);
            imgPreview.setVisibility(View.GONE);
            Toast.makeText(ReportForm.this, "Form cleared.", Toast.LENGTH_SHORT).show();
        });

        // Navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int selectedItem = item.getItemId();
            if (selectedItem == R.id.nav_home) {
                Intent main = new Intent(ReportForm.this, Main.class);
                startActivity(main);
                return true;
            } else if (selectedItem == R.id.nav_map) {
                return true;
            } else if (selectedItem == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Handle image captured by camera
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imgPreview.setImageBitmap(photo);
                imgPreview.setVisibility(View.VISIBLE);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                // Handle image selected from gallery
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    imgPreview.setImageBitmap(bitmap);
                    imgPreview.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
