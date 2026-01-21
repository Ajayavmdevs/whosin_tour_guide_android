package com.whosin.app.comman;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.text.TextUtils;

import com.amplitude.api.Amplitude;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;
import com.whosin.app.R;
import com.whosin.app.service.Repository.RealmRepository;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.ActivityTrackerManager;
import com.whosin.app.service.manager.LogManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.manager.TranslationManager;

public class AppDelegate extends Application {

    public static Activity activity;

    private String channelId = "Default";

    // --------------------------------------
    // region Singleton
    // --------------------------------------

    @Override
    public void onCreate() {
        super.onCreate();

        Preferences.shared.setContext(getApplicationContext());
        TranslationManager.shared.setContext(this);
        SessionManager.shared.setContext(this);
        LogManager.shared.init(this);
        FirebaseApp.initializeApp(this);
        FirebaseAnalytics.getInstance(this);
        AppSettingManager.shared.setContext(this);
        AppSettingManager.shared.requestAppSetting(this);
        RealmRepository.configRealm(this.getApplicationContext());

        ActivityTrackerManager.getInstance().init(this);

        Amplitude.getInstance().initialize(this,this.getString(R.string.amplitude_api_key)).enableForegroundTracking(this);

//        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
        OneSignal.initWithContext(this, this.getString(R.string.oneSignal_api_key));


//        TranslationManager.shared.reequestCommanLang();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    //  endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
}
