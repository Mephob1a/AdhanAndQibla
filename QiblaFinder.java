public class QiblaFinder {
    // Coordinates of the Kaaba in Makkah
    private static final double KAABA_LATITUDE = 21.4225;
    private static final double KAABA_LONGITUDE = 39.8262;

    public static void main(String[] args) {
        // Fetch user's location
        String[] location = Geolocation.getLocation();

        // Extract latitude and longitude
        double userLatitude = Double.parseDouble(location[2]);
        double userLongitude = Double.parseDouble(location[3]);

        System.out.println("\nYour location:");
        System.out.println("City: " + location[0]);
        System.out.println("Country: " + location[1]);
        System.out.println("Latitude: " + userLatitude);
        System.out.println("Longitude: " + userLongitude);

        // Calculate Qibla direction
        double qiblaDirection = calculateQiblaDirection(userLatitude, userLongitude);

    }

    public static double calculateQiblaDirection(double userLat, double userLon) {
        // Convert degrees to radians
        double userLatRad = Math.toRadians(userLat);
        double userLonRad = Math.toRadians(userLon);
        double kaabaLatRad = Math.toRadians(KAABA_LATITUDE);
        double kaabaLonRad = Math.toRadians(KAABA_LONGITUDE);

        // Calculate the difference in longitudes
        double deltaLon = kaabaLonRad - userLonRad;

        // Apply the formula for initial bearing
        double x = Math.sin(deltaLon) * Math.cos(kaabaLatRad);
        double y = Math.cos(userLatRad) * Math.sin(kaabaLatRad) -
                Math.sin(userLatRad) * Math.cos(kaabaLatRad) * Math.cos(deltaLon);

        double initialBearing = Math.toDegrees(Math.atan2(x, y));

        // Normalize the bearing to 0°–360°
        return (initialBearing + 360) % 360;
    }
}

