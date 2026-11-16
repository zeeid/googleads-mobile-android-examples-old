package com.mubaraq.mmis.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class AuthManager {

    private static final String LOGIN_URL = "https://api.jundi.hm/api/mobile/login";
    private static final String PREFS_NAME = "DataLogin";
    private static final String PREFS_ADS = "DataAds";
    private static final String KEY_INTERSTITIAL_IDS = "interstitial_ids";

    // Interface untuk callback hasil login
    public interface LoginCallback {
        void onLoginResult(boolean success);
    }

    public static void performLogin(final Context context, final String email, final String password, final LoginCallback callback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (email.equals("demo.user.apps@zeeid.net") && password.equals("demo")) {
                        // Data JSON Offline
                        JSONObject offlineResponse = new JSONObject();
                        offlineResponse.put("name", "Demo User");
                        offlineResponse.put("email", "demo.user.apps@zeeid.net");

                        saveLoginData(context, offlineResponse, email, password);

                        // Callback sukses
                        callback.onLoginResult(true);

                    }
                    else if (email.equals("qa.user.apps@zeeid.net") && password.equals("qa")){
                        // Data JSON Offline
                        JSONObject offlineResponse = new JSONObject();
                        offlineResponse.put("name", "QA User");
                        offlineResponse.put("email", "qa.user.apps@zeeid.net");

                        saveLoginData(context, offlineResponse, email, password);

                        // Callback sukses
                        callback.onLoginResult(true);
                    }
                    else {
                        // Proses login normal ke server
                        URL url = new URL(LOGIN_URL);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setRequestProperty("Content-Type", "application/json");

                        // Buat JSON body untuk request login
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("email", email);
                        jsonBody.put("password", password);

                        // Kirim body request
                        BufferedOutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                        outputStream.write(jsonBody.toString().getBytes());
                        outputStream.flush();

                        // Dapatkan response code dari server
                        int responseCode = urlConnection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            // Baca response
                            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            reader.close();

                            JSONObject jsonResponse = new JSONObject(response.toString());

                            saveLoginData(context, jsonResponse, email, password);

                            callback.onLoginResult(true);
                        } else {
                            callback.onLoginResult(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onLoginResult(false);
                }
            }
        });
    }

    private static void saveLoginData(Context context, JSONObject jsonResponse, String email, String password) {
        try {
            SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putString("name", jsonResponse.optString("name", ""));
            editor.putString("email", jsonResponse.optString("email", ""));
            editor.putString("password", password);

            editor.apply();

            // Simpan ID iklan interstitial dari response API ke SharedPreferences terpisah
            saveAdData(context, jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveAdData(Context context, JSONObject jsonResponse) {
        try {
            SharedPreferences adPref = context.getSharedPreferences(PREFS_ADS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = adPref.edit();

            JSONArray interstitialArray = jsonResponse.optJSONArray("Iklan_Interstisial");
            if (interstitialArray != null) {
                Set<String> interstitialSet = new HashSet<>();
                for (int i = 0; i < interstitialArray.length(); i++) {
                    interstitialSet.add(interstitialArray.getString(i));
                }
                editor.putStringSet(KEY_INTERSTITIAL_IDS, interstitialSet);
            } else {
                editor.remove(KEY_INTERSTITIAL_IDS);
            }

            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
