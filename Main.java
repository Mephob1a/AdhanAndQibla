import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Fetching location from Geolocation class
        String[] location = Geolocation.getLocation(); // City, Country, Latitude, Longitude
        if (location != null && location.length == 4 && location[0] != null && location[1] != null) {
            System.out.println("Your location is: " + location[0] + ", " + location[1]);
        } else {
            System.out.println("Could not determine your location. Please enter manually.");
            System.out.println("What is your country: ");
            String country = scanner.nextLine();

            System.out.println("What is your city: ");
            String city = scanner.nextLine();

            location = new String[]{city, country, "0.0", "0.0"};
        }

        try {
            // Using the location for the API call
            String city = location[0];
            String country = location[1];
            double userLatitude = Double.parseDouble(location[2]);
            double userLongitude = Double.parseDouble(location[3]);

            // Call QiblaFinder to calculate Qibla direction
            double qiblaDirection = QiblaFinder.calculateQiblaDirection(userLatitude, userLongitude);
            System.out.println("\nThe Qibla direction is: " + String.format("%.2f", qiblaDirection) + "° (clockwise from North).");
            System.out.println("Face this direction for Salah.");

            // API call for prayer times
            String apiUrl = "https://api.aladhan.com/v1/timingsByCity?city=" + city + "&country=" + country + "&method=2";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse and display prayer times
                String jsonResponse = response.toString();
                String timings = jsonResponse.split("\"timings\":\\{")[1].split("},")[0];
                String fajr = timings.split("\"Fajr\":\"")[1].split("\"")[0];
                String dhuhr = timings.split("\"Dhuhr\":\"")[1].split("\"")[0];
                String asr = timings.split("\"Asr\":\"")[1].split("\"")[0];
                String maghrib = timings.split("\"Maghrib\":\"")[1].split("\"")[0];
                String isha = timings.split("\"Isha\":\"")[1].split("\"")[0];

                fajr = convertTo12HourFormat(fajr);
                dhuhr = convertTo12HourFormat(dhuhr);
                asr = convertTo12HourFormat(asr);
                maghrib = convertTo12HourFormat(maghrib);
                isha = convertTo12HourFormat(isha);

                System.out.println("\nPrayer Times for " + city + ", " + country + ":");
                System.out.println("Fajr: " + fajr);
                System.out.println("Dhuhr: " + dhuhr);
                System.out.println("Asr: " + asr);
                System.out.println("Maghrib: " + maghrib);
                System.out.println("Isha: " + isha);
            } else {
                System.out.println("Failed to fetch data. HTTP Response code: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Transforming 24-hour time to 12-hour format
    private static String convertTo12HourFormat(String time24) {
        try {
            // Split the time into hours and minutes
            String[] parts = time24.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            // Determine AM or PM and adjust the hour
            String period = (hour >= 12) ? "PM" : "AM";
            hour = (hour > 12) ? hour - 12 : hour;
            hour = (hour == 0) ? 12 : hour; // Handle midnight (00:00)

            // Return the formatted time
            return String.format("%02d:%02d %s", hour, minute, period);
        } catch (Exception e) {
            e.printStackTrace();
            return time24; // Return original if parsing fails
        }
    }
}

