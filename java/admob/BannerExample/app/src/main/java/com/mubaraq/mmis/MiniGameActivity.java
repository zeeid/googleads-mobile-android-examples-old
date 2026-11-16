package com.mubaraq.mmis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MiniGameActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MiniGamePrefs";
    private static final String KEY_LUCK_POINTS = "luck_points";
    private static final long AD_INTERVAL_MS = 60000; // 1 menit antar iklan

    private int secretNumber;
    private int attempts;
    private int luckPoints;
    private long lastAdTime = 0;

    private InterstitialAd interstitialAd;
    private GoogleMobileAdsConsentManager consentManager;

    private TextView textAttempts;
    private TextView textResult;
    private TextView textLuckPoints;
    private EditText editGuess;
    private Button buttonGuess;
    private Button buttonReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_game);

        consentManager = GoogleMobileAdsConsentManager.getInstance(getApplicationContext());
        consentManager.gatherConsent(
                this,
                consentError -> {
                    if (consentError != null) {
                        android.util.Log.w(
                                "MiniGame",
                                String.format("%s: %s", consentError.getErrorCode(), consentError.getMessage()));
                    }

                    if (consentManager.canRequestAds()) {
                        initializeMobileAdsSdk();
                    }
                });

        // Coba gunakan consent dari sesi sebelumnya
        if (consentManager.canRequestAds()) {
            initializeMobileAdsSdk();
        }

        textAttempts = findViewById(R.id.textAttempts);
        textResult = findViewById(R.id.textResult);
        textLuckPoints = findViewById(R.id.textLuckPoints);
        editGuess = findViewById(R.id.editGuess);
        buttonGuess = findViewById(R.id.buttonGuess);
        buttonReset = findViewById(R.id.buttonReset);
        Button buttonBack = findViewById(R.id.buttonBack);

        luckPoints = loadLuckPoints();
        textLuckPoints.setText("Poin Keberuntungan: " + luckPoints);

        startNewGame();

        buttonGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editGuess.getText().toString().trim();
                if (input.isEmpty()) {
                    Toast.makeText(MiniGameActivity.this, "Masukkan angka terlebih dahulu", Toast.LENGTH_SHORT).show();
                    return;
                }
                int guess = Integer.parseInt(input);
                attempts++;
                textAttempts.setText("Percobaan: " + attempts);

                if (guess == secretNumber) {
                    luckPoints++;
                    saveLuckPoints();
                    textLuckPoints.setText("Poin Keberuntungan: " + luckPoints);
                    textResult.setText("Benar! Angkanya adalah " + secretNumber + "\nAnda menang dalam " + attempts + " percobaan.\n+1 Poin Keberuntungan!");
                    textResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    editGuess.setEnabled(false);
                    buttonGuess.setEnabled(false);
                } else if (guess < secretNumber) {
                    textResult.setText("Terlalu rendah, coba lagi.");
                    textResult.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                    showInterstitialAd();
                } else {
                    textResult.setText("Terlalu tinggi, coba lagi.");
                    textResult.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                    showInterstitialAd();
                }
                editGuess.setText("");
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initializeMobileAdsSdk() {
        MobileAds.initialize(this, initializationStatus -> {
            // Inisialisasi selesai, baru muat iklan
            loadInterstitialAd();
        });
    }

    private void loadInterstitialAd() {
        // Ambil ID interstitial dari SharedPreferences (data login API)
        SharedPreferences adPref = getSharedPreferences("DataAds", Context.MODE_PRIVATE);
        Set<String> interstitialSet = adPref.getStringSet("interstitial_ids", null);

        String interstitialId = null;
        if (interstitialSet != null && !interstitialSet.isEmpty()) {
            interstitialId = interstitialSet.iterator().next();
        }

        // Fallback ke ID default jika tidak ada data dari API
        if (interstitialId == null) {
            interstitialId = "ca-app-pub-3940256099942544/1033173712";
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, interstitialId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {
                interstitialAd = ad;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        interstitialAd = null;
                        loadInterstitialAd(); // Muat ulang iklan setelah ditutup
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        interstitialAd = null;
                        android.util.Log.e("MiniGame", "Interstitial ad failed to show: " + adError.getMessage());
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        android.util.Log.d("MiniGame", "Interstitial ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                interstitialAd = null;
                android.util.Log.e("MiniGame", "Interstitial ad failed to load: " + loadAdError.getMessage() + " (code: " + loadAdError.getCode() + ")");
            }
        });
    }

    private void showInterstitialAd() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAdTime < AD_INTERVAL_MS) {
            return; // Belum mencapai jeda 1 menit
        }
        if (interstitialAd != null) {
            new Handler(Looper.getMainLooper()).post(() -> interstitialAd.show(MiniGameActivity.this));
            lastAdTime = currentTime;
            loadInterstitialAd(); // Muat ulang iklan untuk berikutnya
        }
    }

    private void startNewGame() {
        Random random = new Random();
        secretNumber = random.nextInt(100) + 1;
        attempts = 0;
        textAttempts.setText("Percobaan: 0");
        textResult.setText("");
        editGuess.setEnabled(true);
        editGuess.setText("");
        buttonGuess.setEnabled(true);
    }

    private int loadLuckPoints() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_LUCK_POINTS, 0);
    }

    private void saveLuckPoints() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_LUCK_POINTS, luckPoints);
        editor.apply();
    }
}
