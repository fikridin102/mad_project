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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanduanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PanduanAdapter panduanAdapter;
    private List<Map<String, String>> panduanList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panduan);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        panduanList = new ArrayList<>();
        panduanAdapter = new PanduanAdapter(panduanList);
        recyclerView.setAdapter(panduanAdapter);

        // Firebase Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("DisasterGuides");

        // Fetch Data from Firebase
        fetchPanduan();
    }

    private void fetchPanduan() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                panduanList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, String> panduan = (Map<String, String>) snapshot.getValue();
                    panduanList.add(panduan);
                }
                panduanAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PanduanActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
