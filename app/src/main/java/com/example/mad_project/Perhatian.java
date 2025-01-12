package com.example.mad_project;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Perhatian extends Activity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    double currentLatitude;
    double currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perhatian);

        ImageButton add = findViewById(R.id.tambah);
        GridView gridView = findViewById(R.id.gridView);
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference("perhatian");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        // Setup the location listener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                // Target location (example: fixed location coordinates)
//                double targetLatitude = 3.8168;  // replace with target latitude
//                double targetLongitude = 103.3317; // replace with target longitude

                // Calculate the distance
//                Location targetLocation = new Location("");
//                targetLocation.setLatitude(targetLatitude);
//                targetLocation.setLongitude(targetLongitude);

//                float distance = location.distanceTo(targetLocation)/1000;  // Distance in metersF

//                Toast.makeText(Perhatian.this, currentLatitude + ": " + currentLongitude , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(Perhatian.this, provider + " enabled", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(Perhatian.this, provider + " disabled", Toast.LENGTH_SHORT).show();
            }
        };

        // Handle permissions
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            currentLatitude = lastKnownLocation.getLatitude();
            currentLongitude = lastKnownLocation.getLongitude();
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);

//        Toast.makeText(Perhatian.this, currentLatitude + ": " + currentLongitude , Toast.LENGTH_LONG).show();

        // Initialize the list of Perhatian_item objects
        List<Perhatian_item> items = new ArrayList<>();
        Perhatian_adapter adapter = new Perhatian_adapter(this, items, currentLatitude, currentLongitude);
        gridView.setAdapter(adapter);

        // Bottom Navigation view listener
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(Perhatian.this, Main.class));
                return true;
            } else if (item.getItemId() == R.id.nav_map) {
                startActivity(new Intent(Perhatian.this, Map.class));
                return true;
            }
            return false;
        });

        // Add button listener
        add.setOnClickListener(v -> startActivity(new Intent(Perhatian.this, Perhatian_form.class)));
        // Fetch data from Firebase
        fetchData(items, adapter);
    }


    private void fetchData(List<Perhatian_item> items, Perhatian_adapter adapter) {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Double latitude = snapshot.child("latitude").getValue(Double.class);
                    Double longitude = snapshot.child("longitude").getValue(Double.class);
                    int bencana = snapshot.child("bencana").getValue(int.class);

                    // Determine image resource based on bencana value
                    if (bencana == 1) bencana = R.drawable.flood;
                    else if (bencana == 2) bencana = R.drawable.api;
                    else if (bencana == 3) bencana = R.drawable.landslide;
                    else if (bencana == 4) bencana = R.drawable.pollution;
                    else if (bencana == 5) bencana = R.drawable.road;
                    else if (bencana == 6) bencana = R.drawable.report;

                    items.add(new Perhatian_item(latitude, longitude, bencana));
                    Log.d("Firebase", "Location: " + latitude + ", " + longitude + ", Bencana: " + bencana);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "Failed to read value.", databaseError.toException());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }
}
