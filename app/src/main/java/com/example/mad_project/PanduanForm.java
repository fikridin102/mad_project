package com.example.mad_project;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class PanduanForm extends Activity {

    private EditText editTextTitle, editTextDescription;
    private Button buttonSave, buttonCancel;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panduan_form);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("DisasterGuides");

        // Bind UI elements
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        buttonSave.setOnClickListener(v -> saveGuideToFirebase());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void saveGuideToFirebase() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String guideId = databaseReference.push().getKey();
        HashMap<String, String> guideData = new HashMap<>();
        guideData.put("id", guideId);
        guideData.put("title", title);
        guideData.put("description", description);

        databaseReference.child(guideId).setValue(guideData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Guide saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save guide", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
