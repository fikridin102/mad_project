package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Perhatian_edit extends AppCompatActivity {

    private MapView mapView;
    private EditText bencanaEditText;
    private Button saveButton, deleteButton;

    private double latitude, longitude;
    private int bencana;
    private int bencanaId;
    private Marker marker;
    double latitude2;
    double longitude2;
    String title;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perhatian_edit);

        mapView = findViewById(R.id.map);
        bencanaEditText = findViewById(R.id.bencana);
        saveButton = findViewById(R.id.tambah);
        deleteButton = findViewById(R.id.delete);

        mapView.setMultiTouchControls(true);

        // Initialize Firebase reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("perhatian");

        // Get data passed from intent
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude", 0.0);
        bencana = intent.getIntExtra("bencana",0);
        if (bencana == R.drawable.flood) {
            title = "Banjir";
        }
        else if (bencana == R.drawable.api) {
            title = "Kebakaran";
        }
        else if (bencana == R.drawable.landslide) {
            title = "Tanah Runtuh";
        }
        else if (bencana == R.drawable.pollution) {
            title = "Pencemaran";
        }
        else if (bencana == R.drawable.road) {
            title = "Penutupan Jalan";
        }
        else  {
            title = "R.drawable.report";
        }

        // Display the disaster type in the EditText
        bencanaEditText.setText(title);

        // Add a draggable pin to the map
        addDraggablePin(latitude, longitude);

//         Set up save button functionality
        saveButton.setOnClickListener(v -> updateDataToFirebase());

//         Set up delete button functionality
        deleteButton.setOnClickListener(v -> deleteDataFromFirebase());

        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(Perhatian_edit.this, Main.class));
                return true;
            } else if (item.getItemId() == R.id.nav_map) {
                startActivity(new Intent(Perhatian_edit.this, Map.class));
                return true;
            }
            return false;
        });
    }

    private void addDraggablePin(double lat, double lon) {
        GeoPoint geoPoint = new GeoPoint(lat, lon);
        mapView.getController().setCenter(geoPoint);
        mapView.getController().setZoom(15.0);

        marker = new Marker(mapView);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Drag to Change Location");
        marker.setDraggable(true);

        // Listener for when the marker is dragged
        marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                // You can handle actions when dragging starts if needed
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                // Continuously update marker position while dragging (optional)
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // Update latitude and longitude when dragging ends
                GeoPoint newPosition = marker.getPosition();
                latitude2 = newPosition.getLatitude();
                longitude2 = newPosition.getLongitude();
                Toast.makeText(Perhatian_edit.this, "Location updated: Lat = " + latitude2 + ", Lon = " + longitude2, Toast.LENGTH_SHORT).show();
            }
        });

        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    private void updateDataToFirebase() {
        String newBencana = bencanaEditText.getText().toString().trim().toLowerCase();
        if(newBencana.equals("banjir")) {
            bencanaId = 1;
        }else if(newBencana.equals("kebakaran")) {
            bencanaId = 2;
        }else if(newBencana.equals("tanah runtuh")) {
            bencanaId = 3;
        }else if(newBencana.equals("pencemaran")) {
            bencanaId = 4;
        }else if(newBencana.equals("penutupan jalan")) {
            bencanaId = 5;
        }else if(newBencana.equals("lain lain")) {
            bencanaId = 6;
        }

        if (newBencana.isEmpty()) {
            Toast.makeText(this, "Please enter a disaster type!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the Firebase database entry
        mDatabaseReference.orderByChild("latitude").equalTo(latitude)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().hasChildren()) {
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            snapshot.getRef().child("bencana").setValue(bencanaId);
                            snapshot.getRef().child("latitude").setValue(latitude2);
                            snapshot.getRef().child("longitude").setValue(longitude2);
                        }
                        Toast.makeText(this, "Data updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No matching data found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        Intent intent = new Intent(Perhatian_edit.this, Perhatian.class);
        startActivity(intent);
    }

    private void deleteDataFromFirebase() {
        mDatabaseReference.orderByChild("latitude").equalTo(latitude)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().hasChildren()) {
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            snapshot.getRef().removeValue();
                        }
                        Toast.makeText(this, "Data deleted successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    } else {
                        Toast.makeText(this, "No matching data found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Deletion failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
