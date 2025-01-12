package com.example.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PanduanActivity extends Activity {

    private ExpandableListView expandableListView;
    private PanduanAdapter adapter;
    private DatabaseReference databaseReference;
    private DatabaseReference connectedRef;  // Reference for connectivity status

    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ImageButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panduan);

        ImageButton btnBackNew = findViewById(R.id.btnBackNew);
        addButton = findViewById(R.id.addButton);

        // Back Button click listener
        btnBackNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(PanduanActivity.this, Main.class);
                startActivity(main);
            }
        });

        // Add Button click listener (navigate to PanduanForm)
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PanduanActivity.this, PanduanForm.class);
                startActivity(intent);
            }
        });

        expandableListView = findViewById(R.id.expandableListView);

        // Initialize lists
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Firebase reference to "DisasterGuides"
        databaseReference = FirebaseDatabase.getInstance().getReference("DisasterGuides");

        // Set up the adapter
        adapter = new PanduanAdapter(this, listDataHeader, listDataChild);
        expandableListView.setAdapter(adapter);

        // Initialize Firebase connectivity reference
        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        // Set up connectivity listener
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean isConnected = snapshot.getValue(Boolean.class);

                if (!isConnected) {
                    // Disable the Add Button if offline
                    addButton.setEnabled(false);
                    addButton.setAlpha(0.5f);  // Optional: Reduce opacity for visual feedback
                } else {
                    // Enable the Add Button if online
                    addButton.setEnabled(true);
                    addButton.setAlpha(1.0f);  // Restore opacity
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error if needed
            }
        });

        // Fetch data from Firebase
        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listDataHeader.clear();
                listDataChild.clear();

                // Loop through the Firebase data
                for (DataSnapshot guideSnapshot : dataSnapshot.getChildren()) {
                    String title = guideSnapshot.child("title").getValue(String.class);
                    String description = guideSnapshot.child("description").getValue(String.class);

                    if (title != null && description != null) {
                        // Add title to the header list if it's not already there
                        if (!listDataHeader.contains(title)) {
                            listDataHeader.add(title);
                        }

                        // Create a list for the child (description)
                        List<String> descriptionList = listDataChild.get(title);
                        if (descriptionList == null) {
                            descriptionList = new ArrayList<>();
                            listDataChild.put(title, descriptionList);
                        }

                        // Add the description to the list for this title
                        descriptionList.add(description);
                    }
                }

                // Notify the adapter to refresh the data
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PanduanActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

