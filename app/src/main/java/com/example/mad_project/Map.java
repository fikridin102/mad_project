package com.example.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class Map extends Activity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference("perhatian");

        // Initialize the MapView
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK); // Set the map tile source
        IMapController mapController = mapView.getController();
        mapView.getController().setZoom(6.0); // Suitable zoom level for Malaysia
        mapView.getController().setCenter(new GeoPoint(4.8, 109.53));  // Set initial zoom level

        // Fetch data from Firebase and plot on the map
        fetchDataAndPlotOnMap();
    }

    private void fetchDataAndPlotOnMap() {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear existing markers on the map
                mapView.getOverlays().clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Double latitude = snapshot.child("latitude").getValue(Double.class);
                    Double longitude = snapshot.child("longitude").getValue(Double.class);
                    int bencana = snapshot.child("bencana").getValue(Integer.class);

                    // Log coordinates
                    Log.d("Firebase", "Latitude: " + latitude + ", Longitude: " + longitude + ", Bencana: " + bencana);

                    // Add marker to the map
                    addMarker(latitude, longitude, bencana);
                }

                // Refresh map to show new markers
                mapView.invalidate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "Failed to read value.", databaseError.toException());
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(Map.this, Main.class));
                return true;
            } else if (item.getItemId() == R.id.nav_map) {
                startActivity(new Intent(Map.this, Map.class));
                return true;
            }
            return false;
        });
    }

    private void addMarker(double latitude, double longitude, int bencana) {

        String title;
        int gambar;

        if (bencana == 1) {
            title = "Banjir";
            gambar = R.drawable.flood;
        }
        else if (bencana == 2) {
            title = "Kebakaran";
            gambar = R.drawable.api;
        }
        else if (bencana == 3) {
            title = "Tanah Runtuh";
            gambar = R.drawable.landslide;
        }
        else if (bencana == 4) {
            title = "Pencemaran";
            gambar = R.drawable.pollution;
        }
        else if (bencana == 5) {
            title = "Penutupan Jalan";
            gambar = R.drawable.road;
        }
        else  {
            title = "Lain Lain";
            gambar = R.drawable.report;
        }

        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(latitude, longitude));  // Set the position of the marker
        marker.setTitle(title);  // Set a title for the marker
        marker.setIcon(getResources().getDrawable(gambar));

        // Add marker to the map
        mapView.getOverlays().add(marker);
    }
}
