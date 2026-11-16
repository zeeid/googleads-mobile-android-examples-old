package com.mubaraq.mmis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mubaraq.mmis.login.AuthManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static TextView data;
    private static final String PREFS_NAME = "DataLogin";

    @SuppressLint("SetTextI18n")
    public void viewLayout(){
        TextView packageName= findViewById(R.id.textView);
        data = findViewById(R.id.data);
        packageName.setText("this package :"+getPackageName());
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewLayout();

        fetchIpData();

        Button buttonRefresh = findViewById(R.id.buttonRefresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchIpData();
            }
        });

        // Tombol untuk sinkronisasi akun
        Button buttonSyncAccount = findViewById(R.id.buttonSyncAccount);
        buttonSyncAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengecek apakah sudah ada data di SharedPreferences
                SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String storedEmail = sharedPref.getString("email", null);
                String storedPassword = sharedPref.getString("password", null);

                if (storedEmail == null || storedPassword == null) {
                    Toast.makeText(MainActivity.this, "Belum ada data login disimpan", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Panggil fungsi login di AuthManager dengan callback untuk hasil login
                AuthManager.performLogin(MainActivity.this, storedEmail, storedPassword, new AuthManager.LoginCallback() {
                    @Override
                    public void onLoginResult(boolean success) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (success) {
                                    // Login berhasil, data diambil ulang dari server
                                    Toast.makeText(MainActivity.this, "Data berhasil disinkronkan", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Login gagal
                                    Toast.makeText(MainActivity.this, "Gagal sinkronkan data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        Button buttonMiniGame = findViewById(R.id.buttonMiniGame);
        buttonMiniGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MiniGameActivity.class);
                startActivity(intent);
            }
        });

    }

    private void fetchIpData() {
        data.setText("Detail checking ..");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL("https://api.jundi.hm/ip?json=true");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        connection.disconnect();

                        JSONObject json = new JSONObject(response.toString());
                        String query = json.optString("query", "-");
                        String isp = json.optString("isp", "-");
                        String country = json.optString("country", "-");
                        String city = json.optString("city", "-");
                        return "IP: " + query + "\nISP: " + isp + "\nCountry: " + country + "\nCity: " + city;
                    } else {
                        connection.disconnect();
                        return "Gagal mengambil data IP (code " + responseCode + ")";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                data.setText(result);
            }
        }.execute();
    }

}
