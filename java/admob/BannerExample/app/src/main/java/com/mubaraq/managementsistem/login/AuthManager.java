package com.mubaraq.managementsistem.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

    private static final String LOGIN_URL = "https://zeeid.net/api/mobile/login";
    private static final String PREFS_NAME = "DataLogin";

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
                        offlineResponse.put("jml_baner", 1);
                        offlineResponse.put("ReLoadBaner", 0);
                        offlineResponse.put("TimerBaner", 60);
                        offlineResponse.put("ReLoadInata", 20);
                        offlineResponse.put("jml_inata", 1);
                        offlineResponse.put("TimerInata", 60);
                        offlineResponse.put("isClearCache", 0);
                        offlineResponse.put("isVPNProtection", 0);
                        offlineResponse.put("isTestAds", 0);
                        offlineResponse.put("isRotation", 0);
                        offlineResponse.put("isMixadstype", 0);
                        offlineResponse.put("isIndoprot", 0);
                        offlineResponse.put("isKeepgoing", 0);
                        offlineResponse.put("isAcakSponsor", 0);
                        offlineResponse.put("maxsuccess", 10);
                        offlineResponse.put("maxfail", 10);

                        // Simulasi array iklan
                        offlineResponse.put("Iklan_Layar_Pembuka_Aplikasi", new JSONArray()
                                .put("ca-app-pub-3940256099942544/9257395921") // App Open Ad
                                .put("ca-app-pub-3940256099942544/9257395921")); // Bisa pakai sama atau tambah lagi kalau mau variasi

                        offlineResponse.put("Iklan_Banner_Adaptif", new JSONArray()
                                .put("ca-app-pub-3940256099942544/9214589741")); // Adaptive Banner

                        offlineResponse.put("Iklan_Banner_Ukuran_Tetap", new JSONArray()
                                .put("ca-app-pub-3940256099942544/6300978111")); // Fixed Size Banner

                        offlineResponse.put("Iklan_Interstisial", new JSONArray()
                                .put("ca-app-pub-3940256099942544/1033173712")); // Interstitial

                        offlineResponse.put("Iklan_Iklan_Reward", new JSONArray()
                                .put("ca-app-pub-3940256099942544/5224354917")); // Rewarded Ad

                        offlineResponse.put("Iklan_Interstisial_Reward", new JSONArray()
                                .put("ca-app-pub-3940256099942544/5354046379")); // Rewarded Interstitial

                        offlineResponse.put("Iklan_Native", new JSONArray()
                                .put("ca-app-pub-3940256099942544/2247696110")); // Native

                        offlineResponse.put("Iklan_Video_Native", new JSONArray()
                                .put("ca-app-pub-3940256099942544/1044960115")); // Native Video


                        saveLoginData(context, offlineResponse, email, password);

                        // Callback sukses
                        callback.onLoginResult(true);

                    } else {
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

            editor.putString("name", jsonResponse.getString("name"));
            editor.putString("email", jsonResponse.getString("email"));
            editor.putString("password", password);
            editor.putInt("jml_baner", jsonResponse.getInt("jml_baner"));
            editor.putInt("ReLoadBaner", jsonResponse.getInt("ReLoadBaner"));
            editor.putInt("TimerBaner", jsonResponse.getInt("TimerBaner"));
            editor.putInt("ReLoadInata", jsonResponse.getInt("ReLoadInata"));
            editor.putInt("jml_inata", jsonResponse.getInt("jml_inata"));
            editor.putInt("TimerInata", jsonResponse.getInt("TimerInata"));
            editor.putInt("isClearCache", jsonResponse.getInt("isClearCache"));
            editor.putInt("isVPNProtection", jsonResponse.getInt("isVPNProtection"));
            editor.putInt("isTestAds", jsonResponse.getInt("isTestAds"));
            editor.putInt("isRotation", jsonResponse.getInt("isRotation"));
            editor.putInt("isMixadstype", jsonResponse.getInt("isMixadstype"));
            editor.putInt("isIndoprot", jsonResponse.getInt("isIndoprot"));
            editor.putInt("isKeepgoing", jsonResponse.getInt("isKeepgoing"));
            editor.putInt("isAcakSponsor", jsonResponse.getInt("isAcakSponsor"));
            editor.putInt("maxsuccess", jsonResponse.getInt("maxsuccess"));
            editor.putInt("maxfail", jsonResponse.getInt("maxfail"));

            saveJSONArrayToPreferences(editor, "Iklan_Layar_Pembuka_Aplikasi", jsonResponse.optJSONArray("Iklan_Layar_Pembuka_Aplikasi"));
            saveJSONArrayToPreferences(editor, "Iklan_Banner_Adaptif", jsonResponse.optJSONArray("Iklan_Banner_Adaptif"));
            saveJSONArrayToPreferences(editor, "Iklan_Banner_Ukuran_Tetap", jsonResponse.optJSONArray("Iklan_Banner_Ukuran_Tetap"));
            saveJSONArrayToPreferences(editor, "Iklan_Interstisial", jsonResponse.optJSONArray("Iklan_Interstisial"));
            saveJSONArrayToPreferences(editor, "Iklan_Iklan_Reward", jsonResponse.optJSONArray("Iklan_Iklan_Reward"));
            saveJSONArrayToPreferences(editor, "Iklan_Interstisial_Reward", jsonResponse.optJSONArray("Iklan_Interstisial_Reward"));
            saveJSONArrayToPreferences(editor, "Iklan_Native", jsonResponse.optJSONArray("Iklan_Native"));
            saveJSONArrayToPreferences(editor, "Iklan_Video_Native", jsonResponse.optJSONArray("Iklan_Video_Native"));

            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void saveJSONArrayToPreferences(SharedPreferences.Editor editor, String key, JSONArray jsonArray) {
        if (jsonArray != null) {
            Set<String> set = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    set.add(jsonArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            editor.putStringSet(key, set);
        }
    }
}
