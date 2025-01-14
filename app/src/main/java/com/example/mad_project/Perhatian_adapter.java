package com.example.mad_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Perhatian_adapter extends BaseAdapter {

    private double currLatitude;
    private double currLongitude;
    private LocationManager locationManager;
    private Context context;
    private List<Perhatian_item> items;
    private double userLatitude;
    private double userLongitude;


    public Perhatian_adapter(Context context, List<Perhatian_item> items, double currLatitude, double currLongitude) {
        this.context = context;
        this.items = items;
        this.currLatitude = currLatitude;
        this.currLongitude = currLongitude;
    }

    private void startLocationUpdates() {
        // Check if location permission is granted before requesting location updates
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        } else {
            // Request permission if not granted
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            userLatitude = location.getLatitude();
            userLongitude = location.getLongitude();
            Log.d("Perhatian_adapter", "User Location: Lat = " + userLatitude + ", Lon = " + userLongitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.perhatian_item, parent, false);
        }

        TextView loc = convertView.findViewById(R.id.loc);
        TextView jarak = convertView.findViewById(R.id.jarak);
        ImageView bencana = convertView.findViewById(R.id.bencana);

        Perhatian_item item = items.get(position);

        double latitude = item.getLatitude();
        double longitude = item.getLongitude();

        String locationName = getLocationName(latitude, longitude);
        double dist = calculateDistance(currLatitude, currLongitude, latitude, longitude);
//        Toast.makeText(context, "User Location: Lat = " + currLatitude + ", Lon = " + currLongitude, Toast.LENGTH_LONG).show();
//        Toast.makeText(context, "Target Location: Lat = " + latitude + ", Lon = " + longitude, Toast.LENGTH_LONG).show();

        loc.setText(locationName);
        jarak.setText(String.format(Locale.getDefault(), "%.2f km", dist));
        bencana.setImageResource(item.getBencana());

        convertView.setOnClickListener(v -> {
            // Create an Intent to navigate to the Perhatian_edit activity
            Intent intent = new Intent(context, Perhatian_edit.class);

            // Pass the data (id, latitude, longitude, and bencana)
             // Assuming getId() method exists in Perhatian_item
            intent.putExtra("latitude", item.getLatitude());
            intent.putExtra("longitude", item.getLongitude());
            intent.putExtra("bencana", item.getBencana());

            // Start the activity
            context.startActivity(intent);
        });

        return convertView;
    }

    private String getLocationName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Extract the district (locality) and state (administrative area)
                String district = address.getLocality();  // e.g., City or district
                String state = address.getAdminArea();  // e.g., State or region

                if (district != null && state != null) {
                    return district + ", " + state;  // Format as "District, State"
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to fetch location name", Toast.LENGTH_SHORT).show();
        }
        return "Unknown Location";
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance in kilometers
    }
}
