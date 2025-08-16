package com.telkkom10x;

public class LocationUtils {
    // Haversine formula to calculate distance between two coordinates (in kilometers)
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // Determine chat group based on location
    public static String getChatGroup(Location location) {
        if (location.getCity() != null && !location.getCity().isEmpty()) {
            // Use city for IP-based geolocation
            return location.getCity().toLowerCase().replace(" ", "_");
        } else {
            // Use proximity-based grouping (e.g., within 10km radius)
            // Round coordinates to create a cluster (simplified for demo)
            int latBucket = (int) Math.round(location.getLatitude() / 0.1);
            int lonBucket = (int) Math.round(location.getLongitude() / 0.1);
            return "proximity_" + latBucket + "_" + lonBucket;
        }
    }
}