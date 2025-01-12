package com.example.mad_project;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.events.MapEventsReceiver;

public class TestMap extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure osmdroid
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.testmap);

        // Initialize MapView
        mapView = findViewById(R.id.map);
        mapView.setMultiTouchControls(true); // Enable zoom gestures
        mapView.getController().setZoom(15.0); // Set zoom level
        mapView.getController().setCenter(new GeoPoint(51.505, -0.09)); // Default location

        // Add MapEventsOverlay to handle map clicks
        MapEventsOverlay eventsOverlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint point) {
                // Add a marker at the clicked location
                Marker marker = new Marker(mapView);
                marker.setPosition(point);
                marker.setTitle("Pinned Location");
                marker.setSnippet("Lat: " + point.getLatitude() + ", Lng: " + point.getLongitude());
                mapView.getOverlays().add(marker);
                marker.showInfoWindow();
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
    }
}
