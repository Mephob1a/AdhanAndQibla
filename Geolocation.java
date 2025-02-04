import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Geolocation {
    public static String[] getLocation() {
        String[] location = new String[4]; // Index 0: City, Index 1: Country, Index 2: Latitude, Index 3: Longitude
        try {
            // Step 1: Use a Geolocation API
            String geoApiUrl = "https://ipapi.co/json/"; // Example API
            URL geoUrl = new URL(geoApiUrl);
            HttpURLConnection geoConnection = (HttpURLConnection) geoUrl.openConnection();
            geoConnection.setRequestMethod("GET");

            int geoResponseCode = geoConnection.getResponseCode();
            if (geoResponseCode == 200) { // HTTP OK
                BufferedReader geoReader = new BufferedReader(new InputStreamReader(geoConnection.getInputStream()));
                StringBuilder geoResponse = new StringBuilder();
                String geoLine;
                while ((geoLine = geoReader.readLine()) != null) {
                    geoResponse.append(geoLine);
                }
                geoReader.close();

                // Step 2: Parse city, country, latitude, and longitude
                String geoJson = geoResponse.toString();

                // Regular expressions for extracting city, country_name, latitude, and longitude
                Pattern cityPattern = Pattern.compile("\"city\"\\s*:\\s*\"(.*?)\"");
                Pattern countryPattern = Pattern.compile("\"country_name\"\\s*:\\s*\"(.*?)\"");
                Pattern latPattern = Pattern.compile("\"latitude\"\\s*:\\s*(-?\\d+\\.\\d+)");
                Pattern lonPattern = Pattern.compile("\"longitude\"\\s*:\\s*(-?\\d+\\.\\d+)");

                // Match and extract city
                Matcher cityMatcher = cityPattern.matcher(geoJson);
                location[0] = cityMatcher.find() ? cityMatcher.group(1) : "Unknown City";

                // Match and extract country
                Matcher countryMatcher = countryPattern.matcher(geoJson);
                location[1] = countryMatcher.find() ? countryMatcher.group(1) : "Unknown Country";

                // Match and extract latitude
                Matcher latMatcher = latPattern.matcher(geoJson);
                location[2] = latMatcher.find() ? latMatcher.group(1) : "0.0";

                // Match and extract longitude
                Matcher lonMatcher = lonPattern.matcher(geoJson);
                location[3] = lonMatcher.find() ? lonMatcher.group(1) : "0.0";
            } else {
                System.out.println("Failed to fetch location. HTTP Response code: " + geoResponseCode);
                location[0] = "Unknown City";
                location[1] = "Unknown Country";
                location[2] = "0.0";
                location[3] = "0.0";
            }

            geoConnection.disconnect();
        } catch (Exception e) {
            System.out.println("Error fetching location: " + e.getMessage());
            e.printStackTrace();
            location[0] = "Unknown City";
            location[1] = "Unknown Country";
            location[2] = "0.0";
            location[3] = "0.0";
        }
        return location;
    }
}
