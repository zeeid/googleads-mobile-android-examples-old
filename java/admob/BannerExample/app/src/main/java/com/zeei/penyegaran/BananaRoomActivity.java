package com.zeei.penyegaran;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.ResponseInfo;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class BananaRoomActivity extends AppCompatActivity {
    // Check your logcat output for the test device hashed ID e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device" or
    // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
    // a debug device".
    public static final String TEST_DEVICE_HASHED_ID = "ABCDEF012345";

    // This is an ad unit ID for a test ad. Replace with your own banner ad unit ID.
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741";
    private static final String TAG = "MyActivity";
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private AdView adView;
    private FrameLayout adContainerView;

    private CountDownTimer countDownTimer;
    private int sizebans;
    private int banyak;
    private int totalAdsToLoad = 0;
    private int adsLoadedCount = 0;
    TextView berhasil,gagal,auto,jumato,jumbanner,categori,size,tanggalan,adopen,rate,logprogram;

    private ScrollView logScrollView;

    private static final String DATE="yyasd",GG="gsdag",BB="beqrewb",CIK="casdfsc",CT="crewt",IMP="imasfdpr";

    public int gagalt=0,berhasilt=0,impre=0,cik=0,show=0,impressed = 0,requestot=0,TimerBaner=60,SizeBaner=6;
    public String ratess;

    public boolean sedang=false,asd;


    public final String RELOADE="reload";
    public boolean isTimerJalan=false, reload=false,autoclose,autoreload,IsIndo, IsAdmob=true;
    public boolean rotation,vpnprot,indoprot,keepgoing,mixbanerinter,usetestunit,AcakSponsor;
    public int maxsuccess = 1, maxfail = 1, isAutoLoad = 0;

    Button sett;
    Button clearLogButton, loadAdmobButton, loadUnityButton;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        destroyBanner();
        finish();
        asd=false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_banana);
        CekDateUP();
        asd=true;

        viewBinds();
        logprogram.setText("");
        appendLog("Log cleared.");
        data();

        SharedPreferences sharedPref = getSharedPreferences("DataLogin", Context.MODE_PRIVATE);

        rotation        = (sharedPref.getInt("isRotation", 0) == 1 );
        mixbanerinter   = (sharedPref.getInt("isMixadstype", 0) == 1 );
        usetestunit     = (sharedPref.getInt("isTestAds", 0) == 1 );
        vpnprot         = (sharedPref.getInt("isVPNProtection", 0) == 1 );;
        indoprot        = (sharedPref.getInt("isIndoprot", 0) == 1 );
        keepgoing       = (sharedPref.getInt("isKeepgoing", 0) == 1 );

        autoreload       = (sharedPref.getInt("ReLoadBaner", 0) == 1 );

        maxsuccess  = sharedPref.getInt("maxsuccess", 0);
        maxfail     = sharedPref.getInt("maxfail", 0);
        isAutoLoad  = sharedPref.getInt("isAutoLoad", 0);

        TimerBaner  = sharedPref.getInt("TimerBaner", 0);
        SizeBaner  = sharedPref.getInt("SizeBaner", 6);

        banyak=sharedPref.getInt("jml_baner", 1);
        jumbanner.setText("Total ad per imprs :"+banyak);

        if (keepgoing){
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
        }

        if(autoreload){
            auto.setText("AUTO RELOAD ACTIVE");
            jumato.setText("in :"+TimerBaner+" second");
        }else{
            auto.setText("AUTO RELOAD DEACTIVE");
            jumato.setText("NULL");
        }


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

                    if (googleMobileAdsConsentManager.canRequestAds()) {
                        loadMain();
                    }

                    if (googleMobileAdsConsentManager.isPrivacyOptionsRequired()) {
                        // Regenerate the options menu to include a privacy setting.
                        invalidateOptionsMenu();
                    }
                });

        // This sample attempts to load ads using consent obtained in the previous session.
        if (googleMobileAdsConsentManager.canRequestAds()) {
            loadMain();
        }
    }


    public void StartAutoReload(){
        if (isTimerJalan){
            return;
        }

        if(autoreload){
            isTimerJalan = true;
            AutoReload();
        }

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
                        // Handle changes to user consent.
                        googleMobileAdsConsentManager.showPrivacyOptionsForm(
                                this,
                                formError -> {
                                    if (formError != null) {
                                        Toast.makeText(this, formError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
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

    @SuppressLint("SetTextI18n")
    public void data(){

        tanggalan.setText("Estimates calculation in : "+getString(DATE,this));
        berhasilt = getInteger(BB, this);
        gagalt=getInteger(GG,this);
        impre=getInteger(IMP,this);
        cik=getInteger(CIK,this);
        ratess = getString(CT, this);

        cekRate();

        berhasil.setText("LOAD :"+berhasilt);
        gagal.setText("FAILED :"+gagalt);
        adopen.setText("CLICK :"+cik);

    }

    public void resetResult(){
        saveInteger(GG,0,this);
        saveInteger(CIK,0,this);
        saveInteger(BB,0,this);
        saveInteger(IMP,0,this);
        saveString(CT,"0",this);
    }

    public void AutoReload(){
        if (countDownTimer == null) countDownTimer =  new CountDownTimer(TimerBaner*1000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                sedang=true;
                if (autoreload){
                    jumato.setText("in :"+((millisUntilFinished/1000)-1)+" second");
                }

            }

            public void onFinish() {

                if (autoreload){
                    if (adView != null) {
                        adView.destroy();
                    }

                    // Menutup aktivitas saat ini
                    finish();
                    Intent intent;
                    if (mixbanerinter){
                        // Membuka BananaFixedActivity
                        intent = new Intent(BananaRoomActivity.this, InataRoomActivity.class);
                    }else{
                        intent = new Intent(BananaRoomActivity.this, BananaRoomActivity.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            }
        }.start();
    }

    public void viewBinds(){
        berhasil=findViewById(R.id.succsestot);
        gagal=findViewById(R.id.failtot);
        auto=findViewById(R.id.autoReloads);
        adopen=findViewById(R.id.adopenBan);
        jumato=findViewById(R.id.timereload);
        jumbanner=findViewById(R.id.jumlahbanner);
        categori=findViewById(R.id.categoryban);
        size=findViewById(R.id.sizebanner);
        rate=findViewById(R.id.rateSBan);
        tanggalan=findViewById(R.id.tanggal);
        logprogram = findViewById(R.id.logprogram);
        logScrollView = findViewById(R.id.logScrollView);
        clearLogButton = findViewById(R.id.clear_log_button);

        clearLogButton.setOnClickListener(v -> {
            logprogram.setText("");
            appendLog("Log cleared.");
        });
    }

    private void appendLog(String message) {
        String timeStamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        logprogram.append(timeStamp + " - " + message + "\n");
        logScrollView.post(() -> logScrollView.fullScroll(View.FOCUS_DOWN));
    }

    public void CekDateUP(){
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        if(!date.equals(getString(DATE, this))){
            resetResult();
            saveString(DATE,date,this);
        }else{
            saveString(DATE,date,this);
        }
    }

    public void resetOnClick(View view){
        resetResult();
        data();
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        CekDateUP();
        data();
        super.onResume();

        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        if (adView != null) {
            adView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        destroyBanner();
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }



    public void loadMain(){
        Toast.makeText(this, "please wait for loading..", Toast.LENGTH_SHORT).show();
        if(!sedang){
            loadAd(banyak);

        }
    }

    public int GetSizeBaner (){
        SharedPreferences sharedPref = getSharedPreferences("DataLogin", Context.MODE_PRIVATE);
        int SizeValue;
        if ((sharedPref.getInt("IsRamdomSizeBaner", 0) == 1 )){
            SizeValue = new Random().nextInt(6) + 1;
        }else{
            SizeValue = SizeBaner;
        }

        Log.d("BANNER", "GetSizeBaner:"+SizeValue);
        return SizeValue;
    }

    public String GetUnitID() {

        String randomAdCode = "";

        if(usetestunit){
            randomAdCode = AD_UNIT_ID;
        } else {
            // Mengambil SharedPreferences
            SharedPreferences sharedPref = getSharedPreferences("DataLogin", Context.MODE_PRIVATE);

            // Mengambil array ads sebagai Set
            Set<String> layarPembukaAplikasiSet = sharedPref.getStringSet("Iklan_Banner_Ukuran_Tetap", new HashSet<>());

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
        }

        appendLog("Ad Code: "+randomAdCode);

        return randomAdCode;
    }


    @SuppressLint("SetTextI18n")
    public void loadAd(int banyak) {
        appendLog("Starting ad load sequence for " + banyak + " ads.");
        this.totalAdsToLoad = banyak;
        this.adsLoadedCount = 0;
        LinearLayout layout = findViewById(R.id.banner_layout);
        layout.removeAllViews();
        loadNextAd();
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getDisplay().getRealMetrics(outMetrics);
        } else {
            getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        }

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    @SuppressLint("SetTextI18n")
    private void loadNextAd() {
        if (adsLoadedCount >= totalAdsToLoad) {
            appendLog("All ads loaded successfully.");
            StartAutoReload();
            return;
        }

        sizebans = GetSizeBaner();
        appendLog("Loading ad " + (adsLoadedCount + 1) + " of " + totalAdsToLoad);
        adView = new AdView(this);
        adView.setAdUnitId(GetUnitID());

        TextView sixe = new TextView(this);
        sixe.setText("AdView " + (adsLoadedCount + 1));

        AdSize adSize;

        if (sizebans == 1) {
            adSize = AdSize.BANNER;
            appendLog("Ad Size: Banner");
        } else if (sizebans == 2) {
            adSize = AdSize.LARGE_BANNER;
            appendLog("Ad Size: Large Banner");
        } else if (sizebans == 3) {
            adSize = AdSize.MEDIUM_RECTANGLE;
            appendLog("Ad Size: Medium Rectangle");
        } else if (sizebans == 4) {
            adSize = AdSize.FULL_BANNER;
            appendLog("Ad Size: Full Banner");
        } else if (sizebans == 5) {
            adSize = AdSize.LEADERBOARD;
            appendLog("Ad Size: Leaderboard");
        } else {
            adSize = AdSize.BANNER;
            appendLog("Ad Size: Banner");
//            adSize = getAdSize();
//            appendLog("Ad Size: Adaptive");
        }
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                appendLog("Ad " + (adsLoadedCount + 1) + " loaded successfully.");
                berhasilt++;
                saveInteger(BB, berhasilt, BananaRoomActivity.this);
                data();

                adsLoadedCount++;
                loadNextAd();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                String errorDomain = adError.getDomain();
                int errorCode = adError.getCode();
                String errorMessage = adError.getMessage();
                ResponseInfo responseInfo = adError.getResponseInfo();
                AdError cause = adError.getCause();

                appendLog("Ad " + (adsLoadedCount + 1) + " failed to load: " + adError.toString());
                gagalt++;
                saveInteger(GG, gagalt, BananaRoomActivity.this);
                data();

                if (keepgoing) {
                    AutoReload();
                } else {
                    if (gagalt > maxfail) {
                        String message;
                        message = "Maksimal Fail tercukupi : " + gagalt + "/" + maxfail;
                        if (isTaskRoot()) {
                            Intent intent = new Intent(BananaRoomActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }
                }
            }

            @Override
            public void onAdOpened() {
                appendLog("Ad opened.");
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
            }

            @Override
            public void onAdClicked() {
                appendLog("Ad clicked.");
                cik++;
                saveInteger(CIK, cik, BananaRoomActivity.this);
                data();

                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
            }

            @Override
            public void onAdClosed() {
                appendLog("Ad closed.");
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        LinearLayout layout = findViewById(R.id.banner_layout);
        layout.addView(sixe);
        layout.addView(adView);
        adView.loadAd(adRequest);
    }

    public void destroyBanner() {
        // Remove banner from view hierarchy.
        if (adView != null) {
            View parentView = (View) adView.getParent();
            if (parentView instanceof ViewGroup) {
                ((ViewGroup) parentView).removeView(adView);
            }

            // Destroy the banner ad resources.
            adView.destroy();
        }

        // Drop reference to the banner ad.
        adView = null;
    }

    @SuppressLint("SetTextI18n")
    public void cekRate(){

        float total = ((float)cik/(float)berhasilt)*100;
        DecimalFormat df = new DecimalFormat("####.##");
        ratess = df.format(total);
        saveString(CT,ratess,BananaRoomActivity.this);
        rate.setText("CTR :"+ratess+"%");
    }
    @SuppressLint("ApplySharedPref")
    public void saveString(String key, String value, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "empty");
    }

    @SuppressLint("ApplySharedPref")
    public void saveInteger(String key, Integer value, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key,value);
        editor.commit();
    }
    public static int getInteger(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, 0);
    }

    public static void startActivity(Context context) {

        Intent intent = new Intent(context, BananaRoomActivity.class);
        context.startActivity(intent);
    }
}
