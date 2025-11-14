package com.garmentmanagement.garmentmanagement.Service.Implementation;

import org.springframework.stereotype.Service;

@Service
public class LocationVerificationService {


    private final double OFFICE_LAT = 23.8103;
    private final double OFFICE_LNG = 90.4125;
    private final double ALLOWED_RADIUS = 100.0;

    public boolean isWithinOfficeRadius(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return false;
        }

        double distance = calculateDistance(latitude, longitude, OFFICE_LAT, OFFICE_LNG);
        return distance <= ALLOWED_RADIUS;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000; // Convert to meters
    }
}