package com.example.mad_project;

public class Perhatian_item {
    private Double latitude;     // Location name
    private Double longitude;   // Distance
    private int bencana;    // Resource ID for the image representing the disaster

    // Constructor to initialize the Perhatian_item object
    public Perhatian_item(Double latitude, Double longitude, int bencana) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.bencana = bencana;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    // Getter for disaster image resource ID
    public int getBencana() {
        return bencana;
    }

    // Setter for disaster image resource ID (optional, if you want to allow updates)
    public void setBencana(int bencana) {
        this.bencana = bencana;
    }

}
