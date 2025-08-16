package com.telkkom10x.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NearestTaxiRankFinder {

    private static final String DB_URL = "jdbc:sqlite:taxirank.sqlite";

    private static class TaxiRank {
        String name;
        double lat;
        double lon;

        public TaxiRank(String name, double lat, double lon) {
            this.name = name;
            this.lat = lat;
            this.lon = lon;
        }
    }

    public static TaxiRank findNearest(double userLat, double userLon) {
        TaxiRank nearest = null;
        double minDist = Double.MAX_VALUE;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, latitude, longitude FROM taxi_ranks")) {

            while (rs.next()) {
                String name = rs.getString("name");
                double lat = rs.getDouble("latitude");
                double lon = rs.getDouble("longitude");
                double dist = haversine(userLat, userLon, lat, lon);
                if (dist < minDist) {
                    minDist = dist;
                    nearest = new TaxiRank(name, lat, lon);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nearest;
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public static void main(String[] args) {
        // Example location: Braamfontein, Johannesburg
        double userLat = -26.1880;
        double userLon = 28.0303;

        TaxiRank nearest = findNearest(userLat, userLon);
        if (nearest != null) {
            System.out.println("Nearest Taxi Rank: " + nearest.name);
            System.out.println("Location: Lat " + nearest.lat + ", Lon " + nearest.lon);
            double distance = haversine(userLat, userLon, nearest.lat, nearest.lon);
            System.out.println("Distance: " + distance + " km");
        } else {
            System.out.println("No taxi ranks found in the database.");
        }
    }
}