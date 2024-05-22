package com.example.task9_1p.util;

import android.os.AsyncTask;
import android.util.Log;

import com.example.task9_1p.interfaces.PlaceSearchListener;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlaceSearchTask extends AsyncTask<String, Void, LatLng> {
    private PlaceSearchListener listener;

    private static final String TAG = "PlaceSearchTask";
    public PlaceSearchTask(PlaceSearchListener listener) {
        this.listener = listener;
    }
    @Override
    protected LatLng doInBackground(String... params) {
        String placeName = params[0];
        String apiUrl = "https://nominatim.openstreetmap.org/search?q=" + placeName + "&format=json";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            inputStream.close();
            JSONArray jsonArray = new JSONArray(response.toString());
            if (jsonArray.length() > 0) {
                JSONObject placeObject = jsonArray.getJSONObject(0);
                double latitude = placeObject.getDouble("lat");
                double longitude = placeObject.getDouble("lon");
                return new LatLng(latitude, longitude);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(LatLng latLng) {
        super.onPostExecute(latLng);
        if (latLng != null) {
            Log.d(TAG, "Place found: " + latLng.latitude + ", " + latLng.longitude);
            // Handle the result here
            listener.onPlaceFound(latLng);
        } else {
            Log.e(TAG, "Place not found");
            // Handle error
            listener.onPlaceNotFound();
        }
    }
}
