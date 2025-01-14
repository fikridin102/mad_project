package com.example.mad_project;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditPanduanActivity extends Activity {

    private EditText editTextTitle, editTextDescription;
    private Button buttonSave, buttonCancel;

    private String guideId; // ID of the guide to edit
    private DatabaseReference guideRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panduan_edit);

        // Initialize views
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Get data passed from the adapter
        guideId = getIntent().getStringExtra("guideId");
        String guideTitle = getIntent().getStringExtra("title");
        String guideDescription = getIntent().getStringExtra("description");

        // Set initial values in the EditText fields
        editTextTitle.setText(guideTitle);
        editTextDescription.setText(guideDescription);

        // Reference to the specific guide in Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mad-project-2fa59-default-rtdb.firebaseio.com/");
        guideRef = database.getReference("DisasterGuides").child(guideId);

        // Save changes when the save button is clicked
        buttonSave.setOnClickListener(v -> saveChanges());

        // Cancel and go back to the previous activity
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void saveChanges() {
        String newTitle = editTextTitle.getText().toString().trim();
        String newDescription = editTextDescription.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(newTitle)) {
            editTextTitle.setError("Title is required");
            return;
        }
        if (TextUtils.isEmpty(newDescription)) {
            editTextDescription.setError("Description is required");
            return;
        }

        // Show a saving message
        Toast.makeText(this, "Saving changes...", Toast.LENGTH_SHORT).show();

        // Update the guide in Firebase
        guideRef.child("title").setValue(newTitle);
        guideRef.child("description").setValue(newDescription).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditPanduanActivity.this, "Guide updated successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity and go back
            } else {
                Toast.makeText(EditPanduanActivity.this, "Failed to update guide. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
