package com.telkkom10x;

public class Location {
    private double latitude;
    private double longitude;
    private String city; // Optional, for IP-based fallback
    private String country; // Optional, for IP-based fallback

    // Constructor for browser-based geolocation
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Constructor for IP-based geolocation
    public Location(double latitude, double longitude, String city, String country) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.country = country;
    }

    // Getters and setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}