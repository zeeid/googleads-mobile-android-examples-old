/*
 * Copyright (C) 2013 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zeei.penyegaran;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.widget.ScrollView;
import android.os.Handler; // Import Handler

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.zeei.penyegaran.data.InterstialMe;
import com.zeei.penyegaran.data.GeoIpChecker;

import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Arrays;

import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAdsShowOptions;

import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;

@SuppressLint("SetTextI18n")
public  class InataRoomActivity_level3 extends AppCompatActivity implements IUnityAdsInitializationListener  {

    public static final String TEST_DEVICE_HASHED_ID = "ABCDEF012345";

    private static final long GAME_LENGTH_MILLISECONDS = 9000;
    private static final String TAG = "InataRoomActivity";

    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private InterstitialAd interstitialAd;
    private CountDownTimer countDownTimer, countDownTimerAR;
    private Button retryButton;
    private boolean gamePaused;
    private boolean gameOver;
    private boolean adIsLoading;
    private long timerMilliseconds;


    public final String RELOADE="reload";
    public boolean reload=false,autoclose,autoreload,IsIndo, IsAdmob=true, isTimerDone = false;
    public boolean rotation,vpnprot,indoprot,keepgoing,mixbanerinter,usetestunit,AcakSponsor,isMultiAdsNetwork,isFailOverMultiNetwork;
    public int maxsuccess = 1, maxfail = 1, isAutoLoad = 0, isFailOverMaxCount = 4;
    public int gagalt=0,berhasilt=0,cik=0,show=0,impressed = 0,requestot=0,TimerInata=60,failovercount=0;
    public String ratess,AdsUnitID;
    Button sett;
    Button clearLogButton, loadAdmobButton, loadUnityButton;
    TextView jmlrequest,berhasil,gagal,auto,categori,close,tanggalan,adopen,rate,showon,times,impreson,logprogram;

    private ScrollView logScrollView;

    private String unityGameID;
    private Boolean testMode = false;
    //private String adUnitId = "Interstitial_Android";
    private final String[] unityAdUnitIds = {
            "Interstitial_Android", // GANTI DENGAN ID PERTAMA ANDA
            "Interstitial_Android_Bidding", // GANTI DENGAN ID KEDUA ANDA
            "Gabungan_intertial_ads",
            "PL_air_terjun",
            "geo_collection"  // GANTI DENGAN ID KETIGA ANDA
    };

    Random random = new Random();
    boolean useAdmob = random.nextBoolean();


    @Override
    public void onBackPressed() {
        if(countDownTimerAR != null) {
            countDownTimerAR.cancel();
            countDownTimerAR = null;
        }


        if (isTaskRoot()) {
            // Jika aktivitas ini adalah aktivitas teratas (tidak ada aktivitas lain dalam tumpukan)
            // tambahkan logika untuk membuka menu Home activity atau lakukan tindakan yang sesuai.
            // Misalnya:
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Selesai dengan aktivitas ini
        } else {
            super.onBackPressed(); // Panggil perilaku default jika tidak ada dalam tumpukan teratas
        }
    }

    public String GetUnitID() {

        String randomAdCode = "";

        // Mengambil SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("DataLogin", Context.MODE_PRIVATE);

        // Mengambil array ads sebagai Set
        Set<String> layarPembukaAplikasiSet = sharedPref.getStringSet("Iklan_Interstisial", new HashSet<>());

        // Konversi Set menjadi array
        String[] layarPembukaAplikasiArray = layarPembukaAplikasiSet.toArray(new String[0]);

        // Memilih adCode secara random jika ada data
        if (layarPembukaAplikasiArray.length > 0) {
            Random random = new Random();
            int randomIndex = random.nextInt(layarPembukaAplikasiArray.length);

            AcakSponsor     = (sharedPref.getInt("isAcakSponsor", 0) == 1 );

            if (AcakSponsor){
                randomAdCode = layarPembukaAplikasiArray[randomIndex];
            }
            else{
                if(layarPembukaAplikasiArray.length > 1){
                    randomAdCode = layarPembukaAplikasiArray[0];
                }else{
                    randomAdCode = layarPembukaAplikasiArray[0];
                }
            }

            // Menampilkan adCode random
            Log.d("AnotherActivity", "Random Ad Code: " + randomAdCode);
        }

        return randomAdCode;
    }

    private String getRandomUnityAdUnitId() {
        // Memilih indeks acak dari 0 sampai (panjang array - 1)
        int randomIndex = random.nextInt(unityAdUnitIds.length);
        String selectedAdUnitId = unityAdUnitIds[randomIndex];
        Log.d(TAG, "Menggunakan Unity Ad Unit ID: " + selectedAdUnitId);
        appendLog("Log: Memilih Unity Ad Unit ID: " + selectedAdUnitId);
        return selectedAdUnitId;
    }

    private IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
        @Override
        public void onUnityAdsAdLoaded(String placementId) {
            failovercount = 0;
            UnityAds.show(InataRoomActivity_level3.this, placementId, new UnityAdsShowOptions(), showListener);
            berhasilt++;
            InterstialMe.saveInteger(InterstialMe.BERHASIL,berhasilt,InataRoomActivity_level3.this);
            dataC();

            Toast.makeText(InataRoomActivity_level3.this, "Unity Ads loaded ad for " + placementId, Toast.LENGTH_SHORT).show();

            if(autoclose) {
                Log.d("AD_CHECK","autoclose from Unity Ads loaded ad for " + placementId);
//                //nutupsponsor();
                countDownTimeAR();
            }
        }

        @Override
        public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
            Log.e("UnityAdsExample", "Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
            appendLog("Log : Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
            categori.setText("Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);

            gagalt++;
            InterstialMe.saveInteger(InterstialMe.GAGAL,gagalt,InataRoomActivity_level3.this);
            dataC();
            failovercount++;
            if (isFailOverMultiNetwork) {

                if (failovercount < isFailOverMaxCount) {
                    appendLog("Log : Mencoba Load Network Lain ke "+failovercount);
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (googleMobileAdsConsentManager != null && googleMobileAdsConsentManager.canRequestAds()) {
                                loadAd(); // Langsung panggil fungsi untuk memuat iklan AdMob
                            }
                            else{
                                appendLog("Log : Admob Belum Consent #FailoverUnity");
                            }
                        }
                    }, 5000);
                } else {
                    appendLog("Log : gagal failover load multi network check internet / akun nya kena limit");
                    Toast.makeText(InataRoomActivity_level3.this, "gagal failover load multi network check internet / akun nya kena limit", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                if (keepgoing){
                    if (failovercount < isFailOverMaxCount) {
                        appendLog("Log : Mencoba Load Lagi "+failovercount);
                        createTimer(0,true);
                    } else {
                        appendLog("Log : gagal failover load multi network check internet / akun nya kena limit");
                        Toast.makeText(InataRoomActivity_level3.this, "gagal failover load multi network check internet / akun nya kena limit", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    private IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
            Log.e("UnityAdsExample", "Unity Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);

            appendLog("Log : Unity Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);

        }

        @Override
        public void onUnityAdsShowStart(String placementId) {
            Log.v("UnityAdsExample", "onUnityAdsShowStart: " + placementId);
            Log.d("Unitylog","onUnityAdsShowStart: " + placementId);
            appendLog("Log : The Unity ad was shown.");
            show++;
            InterstialMe.saveInteger(InterstialMe.SHOW,show,InataRoomActivity_level3.this);
            dataC();


        }

        @Override
        public void onUnityAdsShowClick(String placementId) {
            Log.v("UnityAdsExample", "onUnityAdsShowClick: " + placementId);

            appendLog("Log : Unity Ad was clicked.");
            cik++;
            InterstialMe.saveInteger(InterstialMe.OPEN,cik,InataRoomActivity_level3.this);
            dataC();
        }

        @Override
        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
            Log.v("UnityAdsExample", "onUnityAdsShowComplete: " + placementId);
            Log.d("AD_CHECK","onUnityAdsShowComplete: " + placementId);

            appendLog("Log : Unity Ad recorded an impression.");
            impressed++;
            InterstialMe.saveInteger(InterstialMe.IMPRESSED,impressed,InataRoomActivity_level3.this);
            dataC();

            Toast.makeText(InataRoomActivity_level3.this, "Unity Ad recorded an impression.", Toast.LENGTH_SHORT).show();

            Log.d("Unitylog","Unity Ad recorded an impression.");
            if(autoclose & !isTimerDone) {
                nutupsponsor();
                //countDownTimeAR();
            }
        }
    };

    @Override
    public void onInitializationComplete() {
        DisplayInterstitialAd();
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
        Log.e("UnityAdsExample", "Unity Ads initialization failed with error: [" + error + "] " + message);

        gagalt++;
        InterstialMe.saveInteger(InterstialMe.GAGAL,gagalt,InataRoomActivity_level3.this);
        dataC();

        if (keepgoing){
            if(autoclose) {
                countDownTimeAR();
            }
        }else{
            countDownTimer.cancel();
            appendLog("Log: Unity Ads initialization failed with error: [" + error + "] " + message);
            Toast.makeText(InataRoomActivity_level3.this, "Reload Jika Fail: "+keepgoing, Toast.LENGTH_SHORT).show();
        }
    }

    // Implement a function to load an interstitial ad. The ad will start to show after the ad has been loaded.
    public void DisplayInterstitialAd () {

        String randomAdUnitId = getRandomUnityAdUnitId();
        UnityAds.load(randomAdUnitId, loadListener);

        requestot++;
        InterstialMe.saveInteger(InterstialMe.JMLREQUEST,requestot,this);
        dataC();
        appendLog("Log : Berhasil Memuat iklan interstitial Unity");
    }

    // REMOVED loadAdmobAd() method. Its logic is integrated into onCreate.

    private void loadUnityAd(){
        if (!UnityAds.isInitialized()) {
            UnityAds.initialize(getApplicationContext(), unityGameID, testMode, InataRoomActivity_level3.this);
            appendLog("Log : UnityAds initialize... ");
        } else {
            appendLog("Log : UnityAds READY to Show... ");
            // Jika sudah terinisialisasi, Anda bisa langsung coba muat iklannya
            DisplayInterstitialAd();
        }
    }

    private void loadAdsInternal() {
        if (isMultiAdsNetwork) {
            appendLog("Log : Start MultiAdsNetwork ");
            boolean useAdmob = random.nextBoolean();
            if (useAdmob) {
                IsAdmob = true;
                appendLog("Log : Start Admob ");
                if (googleMobileAdsConsentManager != null && googleMobileAdsConsentManager.canRequestAds()) {
                    loadAd(); // Langsung panggil fungsi untuk memuat iklan AdMob
                }
                else{
                    appendLog("Log : Admob Belum Consent #MultiAdsNetwork");
                }
            } else {
                IsAdmob = false;
                appendLog("Log : Start Unity ");
                loadUnityAd(); // Panggil fungsi untuk memuat Unity
            }
        } else {
            appendLog("Log : Start Single Admob ");
            if (googleMobileAdsConsentManager != null && googleMobileAdsConsentManager.canRequestAds()) {
                loadAd(); // Langsung panggil fungsi untuk memuat iklan AdMob
            }
            else{
                appendLog("Log : Admob Belum Consent #SingleAdsNetwork");
            }
        }
    }

    private void loadAds() {
        if (indoprot) {
            appendLog("Log : Checking IP for Indonesia protection...");
            GeoIpChecker.checkIfIpIsIndonesia(this, new GeoIpChecker.OnIpCheckListener() {
                @Override
                public void onIpCheckResult(boolean isIndonesia) {
                    InataRoomActivity_level3.this.IsIndo = isIndonesia;
                    if (isIndonesia) {
                        appendLog("Log : IP is from Indonesia. Stopping timers and returning to MainActivity.");
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                        if (countDownTimerAR != null) {
                            countDownTimerAR.cancel();
                            countDownTimerAR = null;
                        }
                        Intent intent = new Intent(InataRoomActivity_level3.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Close InataRoomActivity_level3
                    } else {
                        appendLog("Log : Not in Indonesia (IP Protection active)");
                        loadAdsInternal(); // Proceed to load ads if not in Indonesia
                    }
                }

                @Override
                public void onIpCheckError(String error) {
                    Log.e(TAG, "IP Check Error: " + error);
                    appendLog("Log : IP Check Error: " + error + ". Attempting to load ads anyway.");
                    // If there's an error in IP checking, decide whether to proceed or block
                    // For now, let's proceed to load ads to avoid blocking due to API issues.
                    loadAdsInternal();
                }
            });
        } else {
            appendLog("Log : IP Protection is OFF. Proceeding to load ads.");
            loadAdsInternal();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_inata);
        viewBinds();
        CekDateUP();
        data();

        unityGameID = getString(R.string.unity_game_id);

        // Mengambil SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("DataLogin", Context.MODE_PRIVATE);

        rotation        = (sharedPref.getInt("isRotation", 0) == 1 );
        mixbanerinter   = (sharedPref.getInt("isMixadstype", 0) == 1 );
        usetestunit     = (sharedPref.getInt("isTestAds", 0) == 1 );
        vpnprot         = (sharedPref.getInt("isVPNProtection", 0) == 1 );;
        indoprot        = (sharedPref.getInt("isIndoprot", 0) == 1 );
        keepgoing       = (sharedPref.getInt("isKeepgoing", 0) == 1 );
        isMultiAdsNetwork       = (sharedPref.getInt("isMultiAdsNetwork", 0) == 1 );
        isFailOverMultiNetwork  = (sharedPref.getInt("isFailOverMultiNetwork", 0) == 1 );

        isFailOverMaxCount  = sharedPref.getInt("isFailOverMaxCount", 4);
        maxsuccess  = sharedPref.getInt("maxsuccess", 0);
        maxfail     = sharedPref.getInt("maxfail", 0);
        isAutoLoad  = sharedPref.getInt("isAutoLoad", 0);
        Log.d("SettingsLog", "isAutoLoad: " + isAutoLoad);
        Log.d("SettingsLog", "maxsuccess: " + maxsuccess);
        Log.d("SettingsLog", "maxfail: " + maxfail);
        Log.d("SettingsLog", "berhasilt: " + berhasilt);
        Log.d("SettingsLog", "gagalt: " + gagalt);

        if (gagalt > maxfail || berhasilt > maxsuccess) {
            String message;
            if (gagalt > maxfail) {
                message = "Maksimal Fail tercukupi : " + gagalt + "/" + maxfail;
            } else {
                message = "Maksimal Load tercukupi : " + berhasilt + "/" + maxsuccess;
            }

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            if (isTaskRoot()) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }

            finish(); // Selesai dengan aktivitas ini
            return;
        }

        // Initialize GoogleMobileAdsConsentManager here
        googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(getApplicationContext());

        // Gather consent first. The ad loading logic will be inside the callback.
        googleMobileAdsConsentManager.gatherConsent(
                this,
                consentError -> {
                    if (consentError != null) {
                        Log.w(TAG, String.format("%s: %s", consentError.getErrorCode(), consentError.getMessage()));
                        appendLog("Log: Consent error: " + consentError.getMessage());
                    }

                    // Start the game (or whatever initial setup is needed) after consent is handled.
                    startGame();

                    if (googleMobileAdsConsentManager.isPrivacyOptionsRequired()) {
                        invalidateOptionsMenu();
                    }

                    // Now that consent is determined, proceed to load ads if auto-load is enabled.
                    if (isAutoLoad == 1) {
                        loadAds();
                    }
                });

        retryButton = findViewById(R.id.retry_button);
        retryButton.setVisibility(View.INVISIBLE);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When retry button is clicked, re-check consent and load ads if possible
                if (googleMobileAdsConsentManager.canRequestAds()) {
                    loadAds();
                } else {

                    if (googleMobileAdsConsentManager.isPrivacyOptionsRequired()) {
                        // If privacy options are required, show the form.
                        googleMobileAdsConsentManager.showPrivacyOptionsForm(InataRoomActivity_level3.this, formError -> {
                            if (formError != null) {
                                Toast.makeText(InataRoomActivity_level3.this, formError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                // After showing form, retry loading ads if consent is now granted
                                if (googleMobileAdsConsentManager.canRequestAds()) {
                                    loadAds();
                                }
                            }
                        });
                    } else {
                        appendLog("Log: Cannot request ads. Consent not granted or form required.");
                        Toast.makeText(InataRoomActivity_level3.this, "Cannot request ads. Consent not granted.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        clearLogButton.setOnClickListener(v -> {
            logprogram.setText(""); // Mengosongkan textview log
            appendLog("Log telah dibersihkan."); // Memberi pesan konfirmasi di log baru
        });

        Button buttonreset = findViewById(R.id.reset);
        buttonreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetResult();
            }
        });

        Button buttonsetting = findViewById(R.id.set_interes);
        buttonsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobileAds.openAdInspector(
                        InataRoomActivity_level3.this,
                        error -> {
                            // Error will be non-null if ad inspector closed due to an error.
                            if (error != null) {
                                Toast.makeText(InataRoomActivity_level3.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        loadAdmobButton.setOnClickListener(v -> {
            appendLog("Tombol 'Load Admob' diklik. Memulai proses...");
            // Pastikan SDK sudah siap dan ada izin sebelum memuat
            if (googleMobileAdsConsentManager != null && googleMobileAdsConsentManager.canRequestAds()) {
                IsAdmob = true; // Force AdMob for this button click
                loadAd(); // Langsung panggil fungsi untuk memuat iklan AdMob
            } else {
                appendLog("Log : Admob Belum Consent #LoadAdmobButton");
                Toast.makeText(InataRoomActivity_level3.this, "Admob Belum Consent", Toast.LENGTH_SHORT).show();
            }
        });

        loadUnityButton.setOnClickListener(v -> {
            appendLog("Tombol 'Load Unity' diklik. Memulai proses...");
            // Fungsi loadUnityAd sudah menangani inisialisasi jika diperlukan
            IsAdmob = false; // Force Unity for this button click
            loadUnityAd();
        });
    }

    public void loadAd() {
        requestot++;
        InterstialMe.saveInteger(InterstialMe.JMLREQUEST,requestot,this);
        dataC();
        appendLog("Log : Memuat iklan ADMOB interstitial");
        // Request a new ad if one isn't already loaded.
        if (adIsLoading || interstitialAd != null) {
            return;
        }
        adIsLoading = true;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                GetUnitID(),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        failovercount = 0;
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        InataRoomActivity_level3.this.interstitialAd = interstitialAd;
                        adIsLoading = false;
                        Log.i(TAG, "ADMOB LOADED");
                        Toast.makeText(InataRoomActivity_level3.this, "ADMOB LOADED", Toast.LENGTH_SHORT).show();
                        berhasilt++;
                        InterstialMe.saveInteger(InterstialMe.BERHASIL,berhasilt,InataRoomActivity_level3.this);
                        dataC();
                        appendLog("Log : Berhasil Memuat iklan interstitial");
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdClicked() {
                                        // Called when a click is recorded for an ad.
                                        appendLog("Log : Ad was clicked.");
                                        cik++;
                                        InterstialMe.saveInteger(InterstialMe.OPEN,cik,InataRoomActivity_level3.this);
                                        dataC();
                                    }

                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        InataRoomActivity_level3.this.interstitialAd = null;
                                        appendLog("Log : The ad was dismissed.");
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        InataRoomActivity_level3.this.interstitialAd = null;
                                        appendLog("Log : The ad failed to show.");
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdImpression() {
                                        // Called when an impression is recorded for an ad.
                                        appendLog("Log : Ad recorded an impression.");
                                        impressed++;
                                        InterstialMe.saveInteger(InterstialMe.IMPRESSED,impressed,InataRoomActivity_level3.this);
                                        dataC();


                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                        appendLog("Log : The ad ADMOB was shown.");
                                        show++;
                                        InterstialMe.saveInteger(InterstialMe.SHOW,show,InataRoomActivity_level3.this);
                                        dataC();

                                        if(autoclose) {
                                            countDownTimeAR();
                                        }
                                    }
                                });

                        // Add a delay before showing the ad.
                        int minDelay = 10000; // 10 seconds
                        int maxDelay = 15000; // 15 seconds
                        int randomDelay = new Random().nextInt(maxDelay - minDelay + 1) + minDelay;

                        appendLog("Log : AdMob interstitial loaded. Showing in " + (randomDelay / 1000) + " seconds.");

                        new Handler(android.os.Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (InataRoomActivity_level3.this.interstitialAd != null) {
                                    InataRoomActivity_level3.this.interstitialAd.show(InataRoomActivity_level3.this);
                                }
                            }
                        }, randomDelay);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        gagalt++;
                        InterstialMe.saveInteger(InterstialMe.GAGAL,gagalt,InataRoomActivity_level3.this);
                        dataC();
                        failovercount++;
                        if (isFailOverMultiNetwork) {

                            if (failovercount < isFailOverMaxCount) {
                                appendLog("Log : Mencoba Load Network Lain ke "+failovercount);
                                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadUnityAd();
                                    }
                                }, 5000);
                            } else {
                                appendLog("Log : gagal failover load multi network check internet / akun nya kena limit");
                                Toast.makeText(InataRoomActivity_level3.this, "gagal failover load multi network check internet / akun nya kena limit", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            if (keepgoing){
                                if (failovercount < isFailOverMaxCount) {
                                    appendLog("Log : Mencoba Load Lagi "+failovercount);
                                    createTimer(0,true);
                                } else {
                                    appendLog("Log : gagal failover load multi network check internet / akun nya kena limit");
                                    Toast.makeText(InataRoomActivity_level3.this, "gagal failover load multi network check internet / akun nya kena limit", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        interstitialAd = null;
                        adIsLoading = false;

                        String error =
                                String.format(
                                        java.util.Locale.US,
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(),
                                        loadAdError.getCode(),
                                        loadAdError.getMessage());
                        Toast.makeText(
                                        InataRoomActivity_level3.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                                .show();

                        appendLog("Log : Error ADMOB "+error);

                        categori.setText("Log : Error ADMOB "+error);


                    }
                });
    }

    private void createTimer(long milliseconds, boolean klikretry ) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        final TextView textView = findViewById(R.id.timer);

        if (milliseconds < 1) {
            int min = 10;   // detik
            int max = 15;  // detik

            int randomDetik = min + new Random().nextInt(max - min + 1);
            milliseconds = randomDetik * 1000;
            appendLog("Log: Timer akan dimulai dalam "+randomDetik+" detik.");
        }

        countDownTimer = new CountDownTimer(milliseconds, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                timerMilliseconds = millisUnitFinished;
                textView.setText("seconds remaining: " + ((millisUnitFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                gameOver = true;
                textView.setText("done!");
                retryButton.setVisibility(View.VISIBLE);

                if(autoclose && !mixbanerinter && countDownTimerAR == null) {
                    //retryButton.performClick();
                }

                if(klikretry){
                    retryButton.performClick();
                    appendLog("Log: Retry button clicked.");
                }

            }
        };

        countDownTimer.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeGame();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseGame();
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (countDownTimerAR != null) {
            countDownTimerAR.cancel();
        }

        // Hancurkan referensi AdMob Interstitial Ad
        interstitialAd = null;

        // Untuk Unity Ads, tidak ada metode destroy() per iklan interstisial.
        // SDK Unity Ads menangani siklus hidupnya sendiri dan listener akan
        // otomatis dibersihkan saat Activity dihancurkan.

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem moreMenu = menu.findItem(R.id.action_more);
        moreMenu.setVisible(googleMobileAdsConsentManager.isPrivacyOptionsRequired());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View menuItemView = findViewById(item.getItemId());
        PopupMenu popup = new PopupMenu(this, menuItemView);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.show();
        popup
                .getMenu()
                .findItem(R.id.privacy_settings)
                .setVisible(googleMobileAdsConsentManager.isPrivacyOptionsRequired());
        popup.setOnMenuItemClickListener(
                popupMenuItem -> {
                    if (popupMenuItem.getItemId() == R.id.privacy_settings) {
                        pauseGame();
                        // Handle changes to user consent.
                        googleMobileAdsConsentManager.showPrivacyOptionsForm(
                                this,
                                formError -> {
                                    if (formError != null) {
                                        Toast.makeText(this, formError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    resumeGame();
                                });
                        return true;
                    } else if (popupMenuItem.getItemId() == R.id.ad_inspector) {
                        MobileAds.openAdInspector(
                                this,
                                error -> {
                                    // Error will be non-null if ad inspector closed due to an error.
                                    if (error != null) {
                                        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        return true;
                    }
                    return false;
                });
        return super.onOptionsItemSelected(item);
    }



    private void startGame() {
        // Hide the button, and kick off the timer.
        retryButton.setVisibility(View.INVISIBLE);
        createTimer(GAME_LENGTH_MILLISECONDS, false);
        gamePaused = false;
        gameOver = false;
    }

    private void resumeGame() {
        if (gameOver || !gamePaused) {
            return;
        }
        // Create a new timer for the correct length.
        gamePaused = false;
        createTimer(timerMilliseconds, false);
    }

    private void pauseGame() {
        if (gameOver || gamePaused) {
            return;
        }
        // TAMBAHKAN PENGECEKAN INI
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        gamePaused = true;
    }

    public void countDownTimeAR(){

        // Initialize the CountDownTimer for 60 seconds, with 1-second intervals
        if (countDownTimerAR == null) {
            countDownTimerAR = new CountDownTimer(TimerInata * 1000L, 1000) {

                @SuppressLint("SetTextI18n")
                public void onTick(long millisUntilFinished) {
                    times.setText(millisUntilFinished / 1000 + " s");
                }

                public void onFinish() {
                    isTimerDone = true;
                    if (autoclose) {
                        if (countDownTimerAR != null) {
                            countDownTimerAR.cancel();
                            countDownTimerAR = null;
                        }

                        nutupsponsor();
                    }
                }
            }.start();
        }
    }

    private void nutupsponsor(){
        // --- IMPLEMENTASI TEKNIK MEDIUM ---
        MyApplication application = (MyApplication) getApplicationContext();
        final Activity foregroundActivity = application.getCurrentForegroundActivity();

        // Cek apakah Activity yang sedang tampil adalah Activity iklan (AdMob atau sejenisnya)
        if (foregroundActivity != null){

            String className = foregroundActivity.getClass().getName();
            Log.d("AD_CHECK", "Foreground Activity: " + className);
            if (className.contains("AdActivity")) { // 1. ADMOB
                // Pilihan: Gunakan onBackPressed() jika Anda ingin memicu callback standar AdMob
                // atau gunakan finish() jika Anda ingin penutupan paling paksa.
                appendLog("Log : Timer selesai. Menutup AdMob Activity: " + className);

                // Opsi Terbaik: Langsung finish() untuk keseragaman dan keandalan
                foregroundActivity.onBackPressed();

            } else if (className.contains("com.unity3d.ads")) { // 2. UNITY ADS
                // Gunakan finish() karena onBackPressed() tidak bekerja pada Unity.
                appendLog("Log : Timer selesai. Menutup Unity Ads Activity: " + className);
                foregroundActivity.onBackPressed();
                foregroundActivity.finish();
            }
//                            else{
//                                appendLog("Log : Timer selesai. Menutup Unity Ads Activity: " + className);
//                                foregroundActivity.finish();
//                            }
            Log.d("AD_CHECK", "Foreground Activity 2: " + className);


            // 2. Tambahkan jeda (delay) dan kemudian transisi Intent
            // Jeda diperlukan agar Activity Iklan punya waktu untuk menghilang
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Re-check foreground activity before transitioning
                    MyApplication currentApplication = (MyApplication) getApplicationContext();
                    Activity currentForegroundActivity = currentApplication.getCurrentForegroundActivity();
                    String currentClassName = currentForegroundActivity.getClass().getName();
                    if (currentForegroundActivity != null) {

                        if (currentClassName.contains("AdActivity") || currentClassName.contains("com.unity3d.ads")) {
                            Toast.makeText(InataRoomActivity_level3.this, "Ad activity still open, please close it first!", Toast.LENGTH_LONG).show();
                            appendLog("Log: Ad activity (" + currentClassName + ") still open after delay, not transitioning yet.");
                            Log.d("AD_CHECK", "currentForegroundActivity still open after delay, not transitioning yet.");
                        } else {
                            // Ad activity is no longer in foreground, proceed with transition
                            Toast.makeText(InataRoomActivity_level3.this, "RUN next act ", Toast.LENGTH_SHORT).show();
                            Log.d("AD_CHECK", "currentForegroundActivity else RUN next act "+currentClassName);
                            goToNextActivity();
                        }
                    } else {
                        // No foreground activity, or it's not an ad activity, proceed with transition
                        Toast.makeText(InataRoomActivity_level3.this, "RUN next act ", Toast.LENGTH_SHORT).show();
                        Log.d("AD_CHECK", "currentForegroundActivity null RUN next act "+currentClassName);
                        goToNextActivity();
                    }
                }
            }, 11500); // Jeda 11.5 seconds

        } else {
            // Jika iklan sudah tertutup atau tidak ditemukan, langsung transisi
            appendLog("Log : Timer selesai. Activity Iklan tidak ditemukan, langsung transisi.");
            goToNextActivity();
        }
        // --- AKHIR IMPLEMENTASI TEKNIK MEDIUM ---
    }

    // Di dalam class InataRoomActivity_level3
    private void goToNextActivity() {
        // Pastikan timer auto-close/reload dihentikan
        if (countDownTimerAR != null) {
            countDownTimerAR.cancel();
            countDownTimerAR = null;
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        if (times != null) {
            times.setText("Auto-Closing...");
        }
        showListener = null;
        Intent intent;
        if (mixbanerinter) {
            intent = new Intent(InataRoomActivity_level3.this, BananaRoomActivity.class);
        } else {
            intent = new Intent(InataRoomActivity_level3.this, InataRoomActivity.class);
        }


        // Menggunakan FLAG_ACTIVITY_CLEAR_TASK sebagai fallback keamanan tambahan
        // jika onBackedPressed() gagal menghancurkan AdActivity.
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
        appendLog("Log : Transisi Intent baru dimulai.");
        finishAffinity();
    }

    public void viewBinds(){
        berhasil=findViewById(R.id.succsestotint);
        jmlrequest=findViewById(R.id.jmlRequest);
        gagal=findViewById(R.id.failtotint);
        adopen=findViewById(R.id.adopenBanint);
        tanggalan=findViewById(R.id.tanggalint);
        rate=findViewById(R.id.rateSBanint);
        auto=findViewById(R.id.A11);
        close=findViewById(R.id.timeautoReloadINTERTV);
        categori=findViewById(R.id.keywordInter);
        logprogram=findViewById(R.id.logprogram);
        logScrollView = findViewById(R.id.logScrollView);
        sett=findViewById(R.id.set_interes);
        clearLogButton = findViewById(R.id.clear_log_button);
        loadAdmobButton = findViewById(R.id.load_admob_button);
        loadUnityButton = findViewById(R.id.load_unity_button);
        showon=findViewById(R.id.shoewint);
        impreson=findViewById(R.id.impresint);
        times=findViewById(R.id.timede);
    }

    private void appendLog(String message) {
        // Membuat stempel waktu sederhana
        String timeStamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Menambahkan pesan baru ke TextView
        logprogram.append(timeStamp + " - " + message + "\n");

        // Otomatis scroll ke paling bawah
        logScrollView.post(() -> logScrollView.fullScroll(View.FOCUS_DOWN));
    }

    @SuppressLint("SetTextI18n")
    public void data(){
        gagalt= InterstialMe.getInteger(InterstialMe.GAGAL,this);
        berhasilt=InterstialMe.getInteger(InterstialMe.BERHASIL,this);
        cik=InterstialMe.getInteger(InterstialMe.OPEN,this);
        ratess=InterstialMe.getString(InterstialMe.RATE,this);
        show=InterstialMe.getInteger(InterstialMe.SHOW,this);
        impressed=InterstialMe.getInteger(InterstialMe.IMPRESSED,this);
        requestot=InterstialMe.getInteger(InterstialMe.JMLREQUEST,this);


        // Mengambil SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("DataLogin", Context.MODE_PRIVATE);

        autoclose=(sharedPref.getInt("ReLoadInata", 0) == 1 );
        autoreload=(sharedPref.getInt("ReLoadInata", 0) == 1 );

        TimerInata = sharedPref.getInt("TimerInata", 0);

        reload=getBool(RELOADE,this);

        tanggalan.setText("Estimates calculation in :\n"+InterstialMe.getString(InterstialMe.DATE,this));
        if((sharedPref.getInt("ReLoadInata", 0) == 1 )) {
            auto.setText("AUTO RELOAD ACTIVE");
        }else {
            auto.setText("AUTO RELOAD OFF");
            times.setText("");
        }
        if((sharedPref.getInt("ReLoadInata", 0) == 1 )) {
            close.setText("AUTO CLOSE ACTIVE ");
        }
        else{
            close.setText("AUTO CLOSE OFF");
        }

        berhasil.setText("LOAD :"+berhasilt);
        jmlrequest.setText("Request:"+requestot);
        gagal.setText("FAILED :"+gagalt);
        adopen.setText("CLICK :"+cik);
        rate.setText("CTR :"+ratess+"%");
        showon.setText("SHOW :"+show);
        impreson.setText("IMPRES :"+impressed);
    }

    public void dataC(){
        float total = ((float)cik/(float)show)*100;
        DecimalFormat df = new DecimalFormat("####.##");
        ratess = df.format(total);
        InterstialMe.saveString(InterstialMe.RATE,ratess,this);
        CekDateUP();
        data();
    }
    public void CekDateUP(){
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        if(!date.equals(InterstialMe.getString(InterstialMe.DATE,this))) {
            InterstialMe.saveString(InterstialMe.DATE,date,this);
            resetResult();

        }else{
            InterstialMe.saveString(InterstialMe.DATE,date,this);
        }
    }
    public void resetResult(){
        InterstialMe.saveInteger(InterstialMe.SHOW,0,InataRoomActivity_level3.this);
        InterstialMe.saveInteger(InterstialMe.GAGAL,0,InataRoomActivity_level3.this);
        InterstialMe.saveInteger(InterstialMe.BERHASIL,0,InataRoomActivity_level3.this);
        InterstialMe.saveInteger(InterstialMe.OPEN,0,InataRoomActivity_level3.this);
        InterstialMe.saveInteger(InterstialMe.IMPRESSED,0,InataRoomActivity_level3.this);
        InterstialMe.saveString(InterstialMe.RATE,"0",this);
        InterstialMe.saveInteger(InterstialMe.JMLREQUEST,0,InataRoomActivity_level3.this);
        data();
        CekDateUP();
    }
    @SuppressLint("ApplySharedPref")
    public static void saveBool(String key, Boolean value, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    @NonNull
    public static Boolean getBool(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }
}