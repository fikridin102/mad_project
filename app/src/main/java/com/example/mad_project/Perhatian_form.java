package com.example.mad_project;

import static java.sql.Types.NULL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

public class Perhatian_form extends Activity {

    EditText loc;
    EditText bencana;
    Button tambah;
    DatabaseReference databaseReference;
    int bencanaid;

    private MapView mapView;
    private Marker singleMarker;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perhatian_form);

        databaseReference = FirebaseDatabase.getInstance().getReference("perhatian");
//        loc = findViewById(R.id.location);
        bencana = findViewById(R.id.bencana);
        tambah = findViewById(R.id.tambah);

        Configuration.getInstance().setUserAgentValue(getPackageName());

        // Initialize MapView
        mapView = findViewById(R.id.map);
        mapView.setMultiTouchControls(true); // Enable zoom gestures

        // Set the map to focus on Malaysia
        mapView.getController().setZoom(6.0); // Suitable zoom level for Malaysia
        mapView.getController().setCenter(new GeoPoint(4.8, 109.53)); // Malaysia's center

        // Add MapEventsOverlay to handle map clicks
        MapEventsOverlay eventsOverlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint point) {
                // Check if a marker already exists
                if (singleMarker == null) {
                    // Create a new marker
                    singleMarker = new Marker(mapView);
                    singleMarker.setPosition(point);
                    singleMarker.setTitle("Pinned Location");
                    singleMarker.setSnippet("Lat: " + point.getLatitude() + ", Lng: " + point.getLongitude());
                    mapView.getOverlays().add(singleMarker);
                } else {
                    // Update the position of the existing marker
                    singleMarker.setPosition(point);
                    singleMarker.setSnippet("Lat: " + point.getLatitude() + ", Lng: " + point.getLongitude());
                    singleMarker.showInfoWindow();
                }

                latitude = point.getLatitude();
                longitude = point.getLongitude();

                return true; // Event handled
            }

            @Override
            public boolean longPressHelper(GeoPoint point) {
                // Handle long-press events if needed
                return false;
            }
        });

        // Add the overlay to the map
        mapView.getOverlays().add(eventsOverlay);

        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input from EditText
//                String location = loc.getText().toString().trim();
                String disaster = bencana.getText().toString().trim().toLowerCase();

                if(disaster.equals("banjir")) {
                     bencanaid = 1;
                }else if(disaster.equals("kebakaran")) {
                     bencanaid = 2;
                }else if(disaster.equals("tanah runtuh")) {
                     bencanaid = 3;
                }else if(disaster.equals("pencemaran")) {
                     bencanaid = 4;
                }else if(disaster.equals("penutupan jalan")) {
                     bencanaid = 5;
                }else if(disaster.equals("lain lain")) {
                     bencanaid = 6;
                }

                if (latitude == NULL || disaster.isEmpty()) {
                    // Show a toast message if any field is empty
                    Toast.makeText(Perhatian_form.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Insert data into Firebase Realtime Database
                    insertData(latitude, longitude, disaster);
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(Perhatian_form.this, Main.class));
                return true;
            } else if (item.getItemId() == R.id.nav_map) {
                startActivity(new Intent(Perhatian_form.this, Map.class));
                return true;
            }
            return false;
        });
    }

    private void insertData(Double latitude, Double Longitude, String bencana) {
        // Generate a unique key for each entry
        String id = databaseReference.push().getKey();  // Firebase will generate a unique ID for each entry

        if (id != null) {
            // Create a new object to store the data
            Perhatian_item perhatianItem = new Perhatian_item(latitude,longitude, bencanaid);

            // Insert the data into Firebase under the unique key
            databaseReference.child(id).setValue(perhatianItem)
                    .addOnSuccessListener(aVoid -> {
                        // Successfully added data
                        Toast.makeText(Perhatian_form.this, "Data added successfully!", Toast.LENGTH_SHORT).show();
                        clearFields();
                    })
                    .addOnFailureListener(e -> {
                        // Failed to add data
                        Toast.makeText(Perhatian_form.this, "Failed to add data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void clearFields() {
        // Clear the input fields after data is inserted
        loc.setText("");
        bencana.setText("");
    }


    }

