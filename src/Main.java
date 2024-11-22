import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {

    // Reemplaza esto con tu clave de API de Google Maps
    private static final String GOOGLE_API_KEY = "AIzaSyBaJTjqGvWR7MItf2ruUE1ILLb92lbqnuk";

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            String location;
            do {

                System.out.println("===================================================");
                System.out.print("Enter location (Say No to Quit): ");
                location = scanner.nextLine();

                if (location.equalsIgnoreCase("No")) break;


                JSONObject cityLocationData = getLocationDataFromGoogle(location);
                if (cityLocationData == null) {
                    System.out.println("Could not find location.");
                    continue;
                }

                double latitude = (double) cityLocationData.get("latitude");
                double longitude = (double) cityLocationData.get("longitude");

                System.out.println("Latitude: " + latitude);
                System.out.println("Longitude: " + longitude);



            } while (!location.equalsIgnoreCase("No"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static JSONObject getLocationDataFromGoogle(String place) {
        place = place.replaceAll(" ", "+");
        String geocodeUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + place + "&key=" + GOOGLE_API_KEY;

        try {

            HttpURLConnection apiConnection = fetchApiResponse(geocodeUrl);

            if (apiConnection.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to Google Geocoding API");
                return null;
            }

            String jsonResponse = readApiResponse(apiConnection);


            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(jsonResponse);

            JSONArray results = (JSONArray) resultsJsonObj.get("results");
            if (results.size() > 0) {

                JSONObject locationData = (JSONObject) ((JSONObject) results.get(0)).get("geometry");
                JSONObject location = (JSONObject) locationData.get("location");


                double latitude = (double) location.get("lat");
                double longitude = (double) location.get("lng");


                JSONObject locationDetails = new JSONObject();
                locationDetails.put("latitude", latitude);
                locationDetails.put("longitude", longitude);

                return locationDetails;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readApiResponse(HttpURLConnection apiConnection) {
        try {
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(apiConnection.getInputStream());

            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }

            scanner.close();
            return resultJson.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}