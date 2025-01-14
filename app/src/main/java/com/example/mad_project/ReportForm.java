package com.example.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ReportForm extends Activity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private ImageView imgPreview;
    String reporterName, email, address, description, type, reportId, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_form);

        // Find UI components
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

        // Receive parameters from either Report or ReportDetail
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        int position = intent.getIntExtra("id", -1);
        reportId = intent.getStringExtra("reportId");

        if (title != null && reportId == null) {
            // If title exists (from Report), display the label and handle based on position
            label1.setText("Aduan: " + title);
            if (position == 6) {
                txtType.setVisibility(View.VISIBLE);  // Show txtType for position 6
            } else {
                txtType.setVisibility(View.INVISIBLE);
            }
        } else {
            // If reportId exists, retrieve the report details using reportId
            label1.setText("Aduan: " + title);
            getReportDetails(reportId, reportDetails -> {
                if (reportDetails != null) {
                    // Display the retrieved data
                    txtFullName.setText(reporterName);
                    txtEmail.setText(email);
                    txtAddress.setText(address);
                    txtDetail.setText(description);
                    // Optionally set the type if available
                    txtType.setText(type);
                } else {
                    Toast.makeText(ReportForm.this, "Report details not found", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Handle back button click
        btnBack.setOnClickListener(v -> finish());

        // Handle upload button click
        btnUpload.setOnClickListener(v -> {
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

        // Handle submit button click
        btnSubmit.setOnClickListener(v -> {
            String type = txtType.getText().toString().trim();
            String fullName = txtFullName.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String address = txtAddress.getText().toString().trim();
            String detail = txtDetail.getText().toString().trim();

            if (fullName.isEmpty() || email.isEmpty() || address.isEmpty() || detail.isEmpty()) {
                Toast.makeText(ReportForm.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String, Object> hmInfo = new HashMap<>();
                if (!type.isEmpty()) {
                    hmInfo.put("Type", type);
                }
                hmInfo.put("Reporter Name", fullName);
                hmInfo.put("Email", email);
                hmInfo.put("Address", address);
                hmInfo.put("Description", detail);

                // Get the current date and time in the format ddMMyyyy/HHmmss
                SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault());
                String dateTimeId = dateFormat.format(new Date());

                // Insert or update data in Firebase
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://mad-project-2fa59-default-rtdb.firebaseio.com/");
                DatabaseReference dbRef = database.getReference("Aduan").child(title);

                if (reportId == null) {
                    // Create a new report
                    dbRef.child(dateTimeId).setValue(hmInfo).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ReportForm.this, "Report submitted successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ReportForm.this, Report.class));
                        } else {
                            Toast.makeText(ReportForm.this, "Failed to submit data", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Update the existing report with the given reportId
                    dbRef.child(reportId).setValue(hmInfo).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ReportForm.this, "Report updated successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ReportForm.this, Report.class));
                        } else {
                            Toast.makeText(ReportForm.this, "Failed to update data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // Handle clear button click
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

    private void getReportDetails(String reportId, final ReportDetailsCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mad-project-2fa59-default-rtdb.firebaseio.com/");
        DatabaseReference dbRef = database.getReference("Aduan").child(title);

        dbRef.child(reportId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot reportSnapshot = task.getResult();

                // Null check and logging
                reporterName = reportSnapshot.child("Reporter Name").getValue(String.class);
                email = reportSnapshot.child("Email").getValue(String.class);
                address = reportSnapshot.child("Address").getValue(String.class);
                description = reportSnapshot.child("Description").getValue(String.class);
                type = reportSnapshot.child("Type").getValue(String.class);


                // Set values to the UI
                callback.onReportDetailsRetrieved(new HashMap<String, String>() {{
                    put("Reporter Name", reporterName);
                    put("Email", email);
                    put("Address", address);
                    put("Description", description);
                    put("Type", type);
                }});

            } else {
                Toast.makeText(ReportForm.this, "Failed to retrieve report details", Toast.LENGTH_SHORT).show();
                callback.onReportDetailsRetrieved(null);
            }
        });
    }

    // Callback interface to handle report details retrieval
    private interface ReportDetailsCallback {
        void onReportDetailsRetrieved(HashMap<String, String> reportDetails);
    }
}
