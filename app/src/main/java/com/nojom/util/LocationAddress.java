package com.nojom.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAddress {

    private static final String TAG = "LocationAddress";

    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                Address address = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("address", address);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                                "\n Unable to get address for this lat-long.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }

    private String fetchCityNameUsingGoogleMap() {
//        String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
//                + location.getLatitude() + "," + location.getLongitude()
//                + "&sensor=false";
//        try {
//            JSONObject googleMapResponse = new JSONObject(ANDROID_HTTP_CLIENT.execute(new HttpGet(googleMapUrl),
//                    new BasicResponseHandler()));
//            JSONArray results = (JSONArray) googleMapResponse.get("results");
//            for (int i = 0; i < results.length(); i++) {
//                JSONObject result = results.getJSONObject(i);
//                if (result.has("address_components")) {
//                    JSONArray addressComponents = result.getJSONArray("address_components");
//                    for (int j = 0; j < addressComponents.length(); j++) {
//                        JSONObject addressComponent = addressComponents.getJSONObject(j);
//                        if (result.has("types")) {
//                            JSONArray types = addressComponent.getJSONArray("types");
//                            String cityName = null;
//                            for (int k = 0; k < types.length(); k++) {
//                                if ("locality".equals(types.getString(k)) && cityName == null) {
//                                    if (addressComponent.has("long_name")) {
//                                        cityName = addressComponent.getString("long_name");
//                                    } else if (addressComponent.has("short_name")) {
//                                        cityName = addressComponent.getString("short_name");
//                                    }
//                                }
//                                if ("sublocality".equals(types.getString(k))) {
//                                    if (addressComponent.has("long_name")) {
//                                        cityName = addressComponent.getString("long_name");
//                                    } else if (addressComponent.has("short_name")) {
//                                        cityName = addressComponent.getString("short_name");
//                                    }
//                                }
//                            }
//                            if (cityName != null) {
//                                return cityName;
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception ignored) {
//            ignored.printStackTrace();
//        }
        return null;
    }
}
