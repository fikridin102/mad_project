package com.example.mad_project;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class PanduanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PanduanAdapter panduanAdapter;
    private List<String> panduanTitles;
    private List<String> panduanDescriptions;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panduan);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.rv1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the lists
        panduanTitles = new ArrayList<>();
        panduanDescriptions = new ArrayList<>();
        panduanAdapter = new PanduanAdapter(panduanTitles, panduanDescriptions);
        recyclerView.setAdapter(panduanAdapter);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("DisasterGuides");

        // Fetch Data from Firebase
        fetchPanduan();
    }

    private void fetchPanduan() {
        // Add a listener to fetch data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the lists before adding new data
                panduanTitles.clear();
                panduanDescriptions.clear();

                // Iterate through the data in Firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String title = snapshot.child("title").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);

                    // Add title and description to respective lists
                    if (title != null && description != null) {
                        panduanTitles.add(title);
                        panduanDescriptions.add(description);
                    }
                }

                // Notify the adapter that data has been updated
                panduanAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors when fetching data
                Toast.makeText(PanduanActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
