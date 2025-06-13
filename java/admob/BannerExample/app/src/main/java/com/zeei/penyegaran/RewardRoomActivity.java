package com.zeei.penyegaran;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.zeei.penyegaran.data.InterstialMe;
import com.zeei.penyegaran.data.RewardMe;

import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;

/** Main Activity. Inflates main activity xml. */
@SuppressLint("SetTextI18n")
public class RewardRoomActivity extends AppCompatActivity implements IUnityAdsInitializationListener  {

    // Check your logcat output for the test device hashed ID e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device" or
    // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
    // a debug device".
    public static final String TEST_DEVICE_HASHED_ID = "ABCDEF012345";

    private String unityGameID = "5855626";
    private Boolean testMode = false;
    private String adUnitId = "Rewarded_Android";

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final long COUNTER_TIME = 10;
    private static final int GAME_OVER_REWARD = 1;
    private static final String TAG = "RewardRoomActivity";
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);

    private int coinCount;
    private TextView coinCountText;
    private CountDownTimer countDownTimer, countDownTimerAR;
    private boolean gameOver;
    private boolean gamePaused;

    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private RewardedAd rewardedAd;
    private Button retryButton;
    private Button showVideoButton;
    private long timeRemaining;
    boolean isLoading;


    public final String RELOADE="reload";
    public boolean reload=false,autoclose,autoreload,IsIndo,IsAdmob=true;
    public boolean rotation,vpnprot,indoprot,keepgoing,mixbanerinter,usetestunit,AcakSponsor;
    public int maxsuccess = 1, maxfail = 1;
    public int gagalt=0,berhasilt=0,cik=0,show=0,impressed = 0,requestot=0,TimerInata=60;
    public String ratess,AdsUnitID;
    Button sett;
    TextView jmlrequest,berhasil,gagal,auto,categori,close,tanggalan,adopen,rate,showon,times,impreson,logprogram;

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
        Set<String> layarPembukaAplikasiSet = sharedPref.getStringSet("Iklan_Iklan_Reward", new HashSet<>());

        // Konversi Set menjadi array
        String[] layarPembukaAplikasiArray = layarPembukaAplikasiSet.toArray(new String[0]);

        // Memilih adCode secara random jika ada data
        if (layarPembukaAplikasiArray.length > 0) {
            Random random = new Random();
            int randomIndex = random.nextInt(layarPembukaAplikasiArray.length);

            AcakSponsor     = (sharedPref.getInt("isAcakSponsor", 0) == 1 );

            if (AcakSponsor){
                randomAdCode = layarPembukaAplikasiArray[randomIndex];
            }else{
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


    private IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
        @Override
        public void onUnityAdsAdLoaded(String placementId) {
            UnityAds.show(RewardRoomActivity.this, adUnitId, new UnityAdsShowOptions(), showListener);

            berhasilt++;
            RewardMe.saveInteger(RewardMe.BERHASIL,berhasilt,RewardRoomActivity.this);
            dataC();
            logprogram.setText("Log : Berhasil Memuat iklan Unity reward");
        }

        @Override
        public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
            Log.e("UnityAdsExample", "Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);

            gagalt++;
            RewardMe.saveInteger(RewardMe.GAGAL,gagalt,RewardRoomActivity.this);
            dataC();

            if (keepgoing){
                if(autoclose) {
                    countDownTimeAR();
                }
            }else{
                countDownTimer.cancel();
                logprogram.setText("Log: Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
                Toast.makeText(RewardRoomActivity.this, "Reload Jika Fail: "+keepgoing, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
            Log.e("UnityAdsExample", "Unity Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);

            logprogram.setText("Log : Unity Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);
        }

        @Override
        public void onUnityAdsShowStart(String placementId) {
            Log.v("UnityAdsExample", "onUnityAdsShowStart: " + placementId);

            logprogram.setText("Log : The Unity ad was shown.");
            show++;
            RewardMe.saveInteger(RewardMe.SHOW,show,RewardRoomActivity.this);
            dataC();
        }

        @Override
        public void onUnityAdsShowClick(String placementId) {
            Log.v("UnityAdsExample", "onUnityAdsShowClick: " + placementId);

            logprogram.setText("Log : Unity Ad was clicked.");
            cik++;
            RewardMe.saveInteger(RewardMe.OPEN,cik,RewardRoomActivity.this);
            dataC();
        }

        @Override
        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
            Log.v("UnityAdsExample", "onUnityAdsShowComplete: " + placementId);

            logprogram.setText("Log : Unity Ad recorded an impression.");
            impressed++;
            RewardMe.saveInteger(RewardMe.IMPRESSED,impressed,RewardRoomActivity.this);
            dataC();

            if (state.equals(UnityAds.UnityAdsShowCompletionState.COMPLETED)) {
                // Reward the user for watching the ad to completion
                addCoins(1);
            }

            if(autoclose) {
                countDownTimeAR();
            }
        }
    };

    @Override
    public void onInitializationComplete() {
        DisplayRewardedAd();
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
        Log.e("UnityAdsExample", "Unity Ads initialization failed with error: [" + error + "] " + message);

        logprogram.setText("Log : Unity Ads initialization failed with error: [" + error + "] " + message);
    }

    // Implement a function to load a rewarded ad. The ad will start to show after the ad has been loaded.
    public void DisplayRewardedAd () {
        UnityAds.load(adUnitId, loadListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hadiah_room);

        viewBinds();
        CekDateUP();
        data();

        // Mengambil SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("DataLogin", Context.MODE_PRIVATE);

        rotation        = (sharedPref.getInt("isRotation", 0) == 1 );
        mixbanerinter   = (sharedPref.getInt("isMixadstype", 0) == 1 );
        usetestunit     = (sharedPref.getInt("isTestAds", 0) == 1 );
        vpnprot         = (sharedPref.getInt("isVPNProtection", 0) == 1 );;
        indoprot        = (sharedPref.getInt("isIndoprot", 0) == 1 );
        keepgoing       = (sharedPref.getInt("isKeepgoing", 0) == 1 );

        maxsuccess  = sharedPref.getInt("maxsuccess", 0);
        maxfail     = sharedPref.getInt("maxfail", 0);

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
                        RewardRoomActivity.this,
                        error -> {
                            // Error will be non-null if ad inspector closed due to an error.
                            if (error != null) {
                                Toast.makeText(RewardRoomActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        if (IsAdmob){
            // Log the Mobile Ads SDK version.
            Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

            googleMobileAdsConsentManager =
                    GoogleMobileAdsConsentManager.getInstance(getApplicationContext());
            googleMobileAdsConsentManager.gatherConsent(
                    this,
                    consentError -> {
                        if (consentError != null) {
                            // Consent not obtained in current session.
                            Log.w(
                                    TAG,
                                    String.format("%s: %s", consentError.getErrorCode(), consentError.getMessage()));
                        }

                        startGame();

                        if (googleMobileAdsConsentManager.canRequestAds()) {
                            initializeMobileAdsSdk();
                        }

                        if (googleMobileAdsConsentManager.isPrivacyOptionsRequired()) {
                            // Regenerate the options menu to include a privacy setting.
                            invalidateOptionsMenu();
                        }
                    });

            // This sample attempts to load ads using consent obtained in the previous session.
            if (googleMobileAdsConsentManager.canRequestAds()) {
                initializeMobileAdsSdk();
            }
        }

        // Create the "retry" button, which tries to show a rewarded ad between game plays.
        retryButton = findViewById(R.id.retry_button);
        retryButton.setVisibility(View.INVISIBLE);
        retryButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startGame();
                        if (!isLoading && googleMobileAdsConsentManager.canRequestAds()) {
                            loadRewardedAd();
                        }
                    }
                });

        // Create the "show" button, which shows a rewarded video if one is loaded.
        showVideoButton = findViewById(R.id.show_video_button);
        showVideoButton.setVisibility(View.INVISIBLE);
        showVideoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showRewardedVideo();
                    }
                });

        // Display current coin count to user.
        coinCountText = findViewById(R.id.coin_count_text);
        coinCount = 0;
        coinCountText.setText("Duit: " + coinCount);
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseGame();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View menuItemView = findViewById(item.getItemId());
        PopupMenu popup = new PopupMenu(this, menuItemView);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup
                .getMenu()
                .findItem(R.id.privacy_settings)
                .setVisible(googleMobileAdsConsentManager.isPrivacyOptionsRequired());
        popup.show();
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

    private void pauseGame() {
        if (gameOver || gamePaused) {
            return;
        }
        countDownTimer.cancel();
        gamePaused = true;
    }

    private void resumeGame() {
        if (gameOver || !gamePaused) {
            return;
        }
        createTimer(timeRemaining);
        gamePaused = false;
    }

    private void loadRewardedAd() {
        if (rewardedAd == null) {

            requestot++;
            RewardMe.saveInteger(RewardMe.JMLREQUEST,requestot,RewardRoomActivity.this);
            dataC();
            logprogram.setText("Log : Memuat iklan rewards");

            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    this,
                    GetUnitID(),
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                            UnityAds.initialize(getApplicationContext(), unityGameID, testMode, RewardRoomActivity.this);

                            // Handle the error.
                            Log.d(TAG, loadAdError.getMessage());
                            rewardedAd = null;
                            RewardRoomActivity.this.isLoading = false;
                            Toast.makeText(RewardRoomActivity.this, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();


                            String error =
                                    String.format(
                                            java.util.Locale.US,
                                            "domain: %s, code: %d, message: %s",
                                            loadAdError.getDomain(),
                                            loadAdError.getCode(),
                                            loadAdError.getMessage());
                            Toast.makeText(
                                            RewardRoomActivity.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                                    .show();

                            logprogram.setText("Log : ADMOB Error "+error);

                            logprogram.setText("Log : UnityAds initialize ");

                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            RewardRoomActivity.this.rewardedAd = rewardedAd;
                            Log.d(TAG, "onAdLoaded");
                            RewardRoomActivity.this.isLoading = false;
                            Toast.makeText(RewardRoomActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();

                            berhasilt++;
                            RewardMe.saveInteger(RewardMe.BERHASIL,berhasilt,RewardRoomActivity.this);
                            dataC();
                            logprogram.setText("Log : Berhasil Memuat iklan interstitial");
                        }
                    });
        }
    }

    private void addCoins(int coins) {
        coinCount += coins;
        coinCountText.setText("Coins: " + coinCount);
    }

    private void startGame() {
        // Hide the retry button, load the ad, and start the timer.
        retryButton.setVisibility(View.INVISIBLE);
        showVideoButton.setVisibility(View.INVISIBLE);
        createTimer(COUNTER_TIME);
        gamePaused = false;
        gameOver = false;
    }

    // Create the game timer, which counts down to the end of the level
    // and shows the "retry" button.
    private void createTimer(long time) {
        final TextView textView = findViewById(R.id.timer);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer =
                new CountDownTimer(time * 1000, 50) {
                    @Override
                    public void onTick(long millisUnitFinished) {
                        timeRemaining = ((millisUnitFinished / 1000) + 1);
                        textView.setText("seconds remaining: " + timeRemaining);
                    }

                    @Override
                    public void onFinish() {
                        if (rewardedAd != null) {
                            showVideoButton.setVisibility(View.VISIBLE);
                        }
                        textView.setText("You Lose!");
                        addCoins(GAME_OVER_REWARD);
                        retryButton.setVisibility(View.VISIBLE);
                        gameOver = true;
                    }
                };
        countDownTimer.start();
    }

    private void showRewardedVideo() {
        if (rewardedAd == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }
        showVideoButton.setVisibility(View.INVISIBLE);

        rewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "onAdShowedFullScreenContent");
                        Toast.makeText(RewardRoomActivity.this, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT)
                                .show();

                        logprogram.setText("Log : The ad was shown.");
                        show++;
                        RewardMe.saveInteger(RewardMe.SHOW,show,RewardRoomActivity.this);
                        dataC();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.d(TAG, "onAdFailedToShowFullScreenContent");
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null;
                        Toast.makeText(
                                        RewardRoomActivity.this, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT)
                                .show();

                        logprogram.setText("Log : The ad failed to show.");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        logprogram.setText("Log : The ad was dismissed.");
                        rewardedAd = null;
                        Log.d(TAG, "onAdDismissedFullScreenContent");
                        Toast.makeText(RewardRoomActivity.this, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT)
                                .show();
                        if (googleMobileAdsConsentManager.canRequestAds()) {
                            // Preload the next rewarded ad.
                            RewardRoomActivity.this.loadRewardedAd();
                        }
                    }

                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.
                        logprogram.setText("Log : Ad was clicked.");
                        cik++;
                        RewardMe.saveInteger(RewardMe.OPEN,cik,RewardRoomActivity.this);
                        dataC();
                    }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        logprogram.setText("Log : Ad recorded an impression.");
                        impressed++;
                        RewardMe.saveInteger(RewardMe.IMPRESSED,impressed,RewardRoomActivity.this);
                        dataC();

                        if(autoclose) {
                            countDownTimeAR();
                        }
                    }
                });
        Activity activityContext = RewardRoomActivity.this;
        rewardedAd.show(
                activityContext,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d("TAG", "The user earned the reward.");
                        addCoins(1);
                    }
                });
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        // Set your test devices.
//        MobileAds.setRequestConfiguration(
//                new RequestConfiguration.Builder()
//                        .setTestDeviceIds(Arrays.asList(TEST_DEVICE_HASHED_ID))
//                        .build());

        new Thread(
                () -> {
                    // Initialize the Google Mobile Ads SDK on a background thread.
                    MobileAds.initialize(this, initializationStatus -> {});

                    // Load an ad on the main thread.
                    runOnUiThread(() -> loadRewardedAd());
                })
                .start();
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
                    if (autoclose) {
                        onBackPressed();
                        // Close the current activity
                        finish();

                        if (countDownTimerAR != null) {
                            countDownTimerAR.cancel();
                            countDownTimerAR = null;
                        }

                        Intent intent;
                        if (mixbanerinter) {
                            // Open BananaFixedActivity (You might want to change this to BananaFixedActivity)
                            intent = new Intent(RewardRoomActivity.this, RewardRoomActivity.class);
                        } else {
                            // Open InataRoomActivity (stays the same)
                            intent = new Intent(RewardRoomActivity.this, RewardRoomActivity.class);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }.start();
        }
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
        sett=findViewById(R.id.set_interes);
        showon=findViewById(R.id.shoewint);
        impreson=findViewById(R.id.impresint);
        times=findViewById(R.id.timede);
    }

    @SuppressLint("SetTextI18n")
    public void data(){
        gagalt= RewardMe.getInteger(RewardMe.GAGAL,this);
        berhasilt=RewardMe.getInteger(RewardMe.BERHASIL,this);
        cik=RewardMe.getInteger(RewardMe.OPEN,this);
        ratess=RewardMe.getString(RewardMe.RATE,this);
        show=RewardMe.getInteger(RewardMe.SHOW,this);
        impressed=RewardMe.getInteger(RewardMe.IMPRESSED,this);
        requestot=RewardMe.getInteger(RewardMe.JMLREQUEST,this);


        // Mengambil SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("DataLogin", Context.MODE_PRIVATE);

        autoclose=(sharedPref.getInt("ReLoadInata", 0) == 1 );
        autoreload=(sharedPref.getInt("ReLoadInata", 0) == 1 );

        TimerInata = sharedPref.getInt("TimerInata", 0);

        reload=getBool(RELOADE,this);

        tanggalan.setText("Estimates calculation in :\n"+RewardMe.getString(RewardMe.DATE,this));
        if((sharedPref.getInt("ReLoadInata", 0) == 1 )){
            auto.setText("AUTO RELOAD ACTIVE");
        }else {
            auto.setText("AUTO RELOAD OFF");
            times.setText("");
        }
        if((sharedPref.getInt("ReLoadInata", 0) == 1 )){
            close.setText("AUTO CLOSE ACTIVE ");
        }else{
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
        RewardMe.saveString(RewardMe.RATE,ratess,this);
        CekDateUP();
        data();
    }
    public void CekDateUP(){
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        if(!date.equals(RewardMe.getString(RewardMe.DATE,this))){
            RewardMe.saveString(RewardMe.DATE,date,this);
            resetResult();

        }else{
            RewardMe.saveString(RewardMe.DATE,date,this);
        }
    }
    public void resetResult(){
        RewardMe.saveInteger(RewardMe.SHOW,0,RewardRoomActivity.this);
        RewardMe.saveInteger(RewardMe.GAGAL,0,RewardRoomActivity.this);
        RewardMe.saveInteger(RewardMe.BERHASIL,0,RewardRoomActivity.this);
        RewardMe.saveInteger(RewardMe.OPEN,0,RewardRoomActivity.this);
        RewardMe.saveInteger(RewardMe.IMPRESSED,0,RewardRoomActivity.this);
        RewardMe.saveString(RewardMe.RATE,"0",this);
        RewardMe.saveInteger(RewardMe.JMLREQUEST,0,RewardRoomActivity.this);
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