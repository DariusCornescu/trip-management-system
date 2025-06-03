package TestClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class JavaClient {
    private static final String BASE_URL = "http://localhost:8080/api/trips";

    public static void main(String[] args) throws Exception {
        testGetAllTrips();
        testGetTripById(1);
        testCreateTrip(5);
        testUpdateTrip(5);
        testDeleteTrip(5);
        // Search endpoints:
        testSearchByAttraction("Champs-Élysées");
        testSearchByAttractionAndTime("Champs-Élysées", "08:00", "12:00");
    }

    public static void testGetAllTrips() throws Exception {
        System.out.println("==== GET all trips ====");
        URL url = new URL(BASE_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        System.out.println("Status: " + status);
        System.out.println(readBody(con));
        con.disconnect();
    }

    public static void testGetTripById(int id) throws Exception {
        System.out.println("==== GET trip/" + id + " ====");
        URL url = new URL(BASE_URL + "/" + id);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        System.out.println("Status: " + status);
        System.out.println(readBody(con));
        con.disconnect();
    }

    public static void testCreateTrip(int id) throws Exception {
        System.out.println("==== POST create trip/" + id + " ====");
        URL url = new URL(BASE_URL + "/" + id);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        String json = "{"
                + "\"attractionName\":\"Mountain Hike\","
                + "\"transportCompany\":\"Adventure Tours\","
                + "\"departureTime\":\"09:00\","
                + "\"price\":45.99,"
                + "\"availableSeats\":20"
                + "}";

        con.getOutputStream()
                .write(json.getBytes(StandardCharsets.UTF_8));

        int status = con.getResponseCode();
        System.out.println("Status: " + status);
        System.out.println(readBody(con));
        con.disconnect();
    }

    public static void testUpdateTrip(int id) throws Exception {
        System.out.println("==== PUT update trip/" + id + " ====");
        URL url = new URL(BASE_URL + "/" + id);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        String json = "{"
                + "\"attractionName\":\"Updated Mountain Trek\","
                + "\"transportCompany\":\"Adventure Tours Inc.\","
                + "\"departureTime\":\"10:00\","
                + "\"price\":49.99,"
                + "\"availableSeats\":15"
                + "}";

        con.getOutputStream()
                .write(json.getBytes(StandardCharsets.UTF_8));

        int status = con.getResponseCode();
        System.out.println("Status: " + status);
        System.out.println(readBody(con));
        con.disconnect();
    }

    public static void testDeleteTrip(int id) throws Exception {
        System.out.println("==== DELETE trip/" + id + " ====");
        URL url = new URL(BASE_URL + "/" + id);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");

        int status = con.getResponseCode();
        System.out.println("Status: " + status);
        System.out.println(readBody(con));
        con.disconnect();
    }

    public static void testSearchByAttraction(String attraction) throws Exception {
        System.out.println("==== GET search by attraction: " + attraction + " ====");
        String encodedAttraction = URLEncoder.encode(attraction, StandardCharsets.UTF_8.toString());
        URL url = new URL(BASE_URL + "/search/attraction?attraction=" + encodedAttraction);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        System.out.println("Status: " + status);
        System.out.println(readBody(con));
        con.disconnect();
    }

    public static void testSearchByAttractionAndTime(String attraction, String startTime, String endTime) throws Exception {
        System.out.println("==== GET search by attraction and time ====");
        String encodedAttraction = URLEncoder.encode(attraction, StandardCharsets.UTF_8.toString());
        String encodedStartTime = URLEncoder.encode(startTime, StandardCharsets.UTF_8.toString());
        String encodedEndTime = URLEncoder.encode(endTime, StandardCharsets.UTF_8.toString());

        String urlString = String.format("%s/search/time?attraction=%s&startTime=%s&endTime=%s",
                BASE_URL, encodedAttraction, encodedStartTime, encodedEndTime);

        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        System.out.println("Status: " + status);
        System.out.println(readBody(con));
        con.disconnect();
    }

    private static String readBody(HttpURLConnection con) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            // probably no response body
        }
        return sb.toString();
    }
}