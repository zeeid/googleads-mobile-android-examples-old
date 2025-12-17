package com.zeei.penyegaran.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class GeoIpChecker {

    private static final String TAG = "GeoIpChecker";
    private static final String IP_API_URL = "http://ip-api.com/json"; // Public API for IP geolocation

    public interface OnIpCheckListener {
        void onIpCheckResult(boolean isIndonesia);
        void onIpCheckError(String error);
    }

    public static void checkIfIpIsIndonesia(Context context, OnIpCheckListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, IP_API_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String countryCode = jsonResponse.getString("countryCode");
                        boolean isIndonesia = "ID".equalsIgnoreCase(countryCode);
                        if (listener != null) {
                            listener.onIpCheckResult(isIndonesia);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        if (listener != null) {
                            listener.onIpCheckError("Failed to parse IP data: " + e.getMessage());
                        }
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error: " + error.getMessage());
                    if (listener != null) {
                        listener.onIpCheckError("Network error: " + error.getMessage());
                    }
                });

        queue.add(stringRequest);
    }
}
