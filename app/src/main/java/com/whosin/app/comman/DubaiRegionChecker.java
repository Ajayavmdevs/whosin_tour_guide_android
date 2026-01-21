package com.whosin.app.comman;

public class DubaiRegionChecker {

    // Internal class to represent a coordinate
    private static class Coordinate {
        double latitude;
        double longitude;

        Coordinate(double lat, double lng) {
            this.latitude = lat;
            this.longitude = lng;
        }
    }

    // Internal class to represent a region
    private static class Region {
        Coordinate center;
        double latitudeDelta;
        double longitudeDelta;

        Region(Coordinate center, double latDelta, double lonDelta) {
            this.center = center;
            this.latitudeDelta = latDelta;
            this.longitudeDelta = lonDelta;
        }
    }

    // Method to check if a coordinate is inside the allowed region
    public static boolean isInsideDubai(double latitude, double longitude) {
        // Define the region — centered around UAE with a span covering Dubai
        Region allowedRegion = new Region(new Coordinate(23.4241, 53.8478), 10.0, 10.0);

        double minLat = allowedRegion.center.latitude - allowedRegion.latitudeDelta / 2.0;
        double maxLat = allowedRegion.center.latitude + allowedRegion.latitudeDelta / 2.0;
        double minLng = allowedRegion.center.longitude - allowedRegion.longitudeDelta / 2.0;
        double maxLng = allowedRegion.center.longitude + allowedRegion.longitudeDelta / 2.0;

        return latitude >= minLat && latitude <= maxLat &&
                longitude >= minLng && longitude <= maxLng;
    }

}

