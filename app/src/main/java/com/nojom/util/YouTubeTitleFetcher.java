package com.nojom.util;

import android.os.StrictMode;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YouTubeTitleFetcher {

    private static final String API_KEY = "AIzaSyCaANBOG4REWXed28699PjG6jUPNDMobAI"; // Replace with your API key
    public static String getVideoId(String url) {
        try {
            URL youtubeUrl = new URL(url);
            String query = youtubeUrl.getQuery();
            if (query != null && query.contains("v=")) {
                return query.split("v=")[1].split("&")[0];
            } else if (url.contains("youtu.be/")) {
                return url.split("youtu.be/")[1];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String fetchVideoTitle(String videoId) {
        String apiUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + videoId + "&key=" + API_KEY;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Parse JSON response to get the title
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray items = jsonResponse.getJSONArray("items");
                if (items.length() > 0) {
                    JSONObject snippet = items.getJSONObject(0).getJSONObject("snippet");
                    return snippet.getString("title");
                }
            } else {
                System.out.println("Error: Response Code " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return "Unknown Title";
    }
}
