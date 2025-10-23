package com.zeei.penyegaran;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.zeei.penyegaran.data.FetchGeoIp;
import com.zeei.penyegaran.login.AuthManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unity3d.ads.metadata.MetaData;


public class MainActivity extends AppCompatActivity {

    public static TextView data;
    private static final String TAG = "Home Activity";
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private static final String PREFS_NAME = "DataLogin";
    private static final String DARK_MODE_PREF = "dark_mode";
    private DrawerLayout drawerLayout;

    private class GetAdvertisingIdTask extends AsyncTask<Void, Void, String> {

        private Context context;

        public GetAdvertisingIdTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            AdvertisingIdClient.Info adInfo = null;
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
            } catch (GooglePlayServicesNotAvailableException |
                     GooglePlayServicesRepairableException | IOException e) {
                // Error handling omitted.
            }
            return adInfo == null ? null : adInfo.getId();
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String advertisingId) {
            super.onPostExecute(advertisingId);

            // Gunakan advertisingId untuk pelacakan.
            Log.d("Home", "Advertising ID: " + advertisingId);
            TextView AdvertisingId= findViewById(R.id.AdvertisingId);
            AdvertisingId.setText("Advertising ID: "+advertisingId);
        }
    }

    // ================ Metode untuk mengatur zona waktu sistem ==========
    private static boolean setSystemTimeZone(Context context, String timeZoneId) {
        try {
            TimeZone.setDefault(TimeZone.getTimeZone(timeZoneId));
            Log.d("TimeZoneManager", "Zona waktu sistem diatur ke: " + timeZoneId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String getTimeZoneFromIP() {
        String timeZoneId = null;
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            // Membuat URL untuk mengakses informasi zona waktu berdasarkan IP
            URL url = new URL("http://ip-api.com/json/");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Membaca respons JSON
            StringBuilder response = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Parsing JSON untuk mendapatkan informasi zona waktu
            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.getString("status").equals("success")) {
                timeZoneId = jsonResponse.getString("timezone");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            // Menutup koneksi dan pembaca
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return timeZoneId;
    }

    private static void setSystemTimeZoneByIP(final Context context) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Mengambil informasi zona waktu dari IP
                    String timeZoneId = getTimeZoneFromIP();

                    // Mengatur zona waktu sistem jika informasi berhasil didapatkan
                    if (timeZoneId != null && !timeZoneId.isEmpty()) {
                        if (setSystemTimeZone(context, timeZoneId)) {
                            // Menampilkan Toast jika zona waktu sistem berhasil diatur
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Zona waktu sistem diatur ke: " + timeZoneId, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Menampilkan Toast jika terjadi kesalahan
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Gagal mengatur zona waktu sistem", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    // ================= END TIME ZONE ====================================

    @Override
    public void onResume() {
        // Start or resume the game.
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @SuppressLint("SetTextI18n")
    public void viewLayout(){
        TextView packageName= findViewById(R.id.textView);
        data = findViewById(R.id.textView6);
        packageName.setText("this package :"+getPackageName());
    }

    private void cekIp() {
        FetchGeoIp process = new FetchGeoIp();
        process.execute();
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_PREF, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String email = sharedPref.getString("email", "user@example.com");

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderEmail = headerView.findViewById(R.id.nav_header_textView_email);
        navHeaderEmail.setText(email);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_sync_data) {
                    Log.d("NavBarLog","nav_sync_data di klik");
                    // Mengecek apakah sudah ada data di SharedPreferences
                    SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    String storedEmail = sharedPref.getString("email", null);
                    String storedPassword = sharedPref.getString("password", null);

                    // Panggil fungsi login di AuthManager dengan callback untuk hasil login
                    AuthManager.performLogin(MainActivity.this, storedEmail, storedPassword, new AuthManager.LoginCallback() {
                        @Override
                        public void onLoginResult(boolean success) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (success) {
                                        // Login berhasil
                                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Login gagal
                                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                }
                            });
                        }
                    });
                } else if (id == R.id.nav_banana_room) {
                    Intent intent = new Intent(MainActivity.this, BananaRoomActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_logout) {
                    // Hapus semua data SharedPreferences
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    settings.edit().clear().apply();

                    // Pindah ke LoginActivity dan hapus semua activity sebelumnya
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                // Handle other navigation item clicks here.
                return true;
            }
        });

        MetaData gdprMetaData = new MetaData(this);
        gdprMetaData.set("gdpr.consent", true);
        gdprMetaData.commit();

        MetaData ccpaMetaData = new MetaData(this);
        ccpaMetaData.set("privacy.consent", true);
        ccpaMetaData.commit();


        new GetAdvertisingIdTask(this).execute();

        viewLayout();
        cekIp();
        setSystemTimeZoneByIP(MainActivity.this);

        // Initialize the Google Mobile Ads SDK.
        googleMobileAdsConsentManager =
                GoogleMobileAdsConsentManager.getInstance(getApplicationContext());

        googleMobileAdsConsentManager.gatherConsent(
                this,
                consentError -> {
                    if (consentError != null) {
                        Log.w(
                                TAG,
                                String.format("%s: %s", consentError.getErrorCode(), consentError.getMessage()));
                    }

                    if (googleMobileAdsConsentManager.canRequestAds()) {
                        initializeMobileAdsSdk();
                    }
                });

        if (googleMobileAdsConsentManager.canRequestAds()) {
            initializeMobileAdsSdk();
        }

        Button buttonInata = findViewById(R.id.buttoninata);
        buttonInata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InataRoomActivity.class);
                startActivity(intent);
            }
        });

        Button buttonInatamedatio = findViewById(R.id.buttoninatamedatio);
        buttonInatamedatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InataRoomActivity.class);
                startActivity(intent);
            }
        });

        Button buttonhadiahroom = findViewById(R.id.buttonhadiahroom);
        buttonhadiahroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RewardRoomActivity.class);
                startActivity(intent);
            }
        });


        Button buttonRefresh = findViewById(R.id.buttonRefresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekIp();
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kode yang akan dijalankan ketika FAB diklik

                // Tambahkan kode lain sesuai dengan tindakan yang ingin Anda lakukan ketika FAB diklik

                setSystemTimeZoneByIP(MainActivity.this);
            }
        });

        FloatingActionButton darkModeButton = findViewById(R.id.darkModeButton);
        darkModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("NavBarLog","darkModeButton di klik");
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_PREF, false);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean(DARK_MODE_PREF, false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean(DARK_MODE_PREF, true);
                }
                editor.apply();
            }
        });

    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }
        // Initialize the Google Mobile Ads SDK on a background thread.
        new Thread(
                        () -> {
                            MobileAds.initialize(this, initializationStatus -> {
                                // MobileAds.initialize() must be called on the main thread.
                                runOnUiThread(() -> {
                                    Toast.makeText(MainActivity.this, "Ads are ready to be loaded.", Toast.LENGTH_SHORT).show();
                                    Log.d("ADMOB", "Ads are ready to be loaded.");
                                });
                            });
                        })
                .start();
    }


}
