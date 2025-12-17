package com.zeei.penyegaran;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private Activity currentForegroundActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // Daftarkan callback untuk mulai melacak Activity
        registerActivityLifecycleCallbacks(this);
    }

    public Activity getCurrentForegroundActivity() {
        return currentForegroundActivity;
    }

    // --- Implementasi ActivityLifecycleCallbacks ---

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        // Ketika Activity baru dimulai, itu adalah foreground activity
        currentForegroundActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentForegroundActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        // Jika activity yang stopped adalah foreground activity kita, set ke null
        // (Ini penting agar kita tahu kapan activity kita hilang dari pandangan)
        if (activity.equals(currentForegroundActivity)) {
            currentForegroundActivity = null;
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (activity.equals(currentForegroundActivity)) {
            currentForegroundActivity = null;
        }
    }
}