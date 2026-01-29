package com.whosin.business.ui.activites.comman;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.whosin.business.R;
import com.whosin.business.comman.Preferences;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.ui.activites.auth.AuthUserNameActivity;
import com.whosin.business.ui.activites.auth.AuthenticationActivity;
import com.whosin.business.ui.activites.home.MainHomeActivity;

import java.io.InputStream;
import java.util.List;


public class SplashActivity extends BaseActivity {


    private static final int PERMISSION_REQUEST_CODE = 100;
    private String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private int currentPermissionIndex = 0;
    protected Activity activity;
    private ImageView imageView;


    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void initUi() {

        ImageView splashImage = findViewById(R.id.splash_gif);
        try {
            // Load the GIF from res/raw
            InputStream inputStream = getResources().openRawResource(R.raw.app_logo);
            Drawable drawable = Drawable.createFromStream(inputStream, null);

            if (drawable instanceof AnimatedImageDrawable animatedDrawable) {


                animatedDrawable.setRepeatCount(0);

                splashImage.setImageDrawable(animatedDrawable);

                animatedDrawable.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        handleDeepLink(getIntent());
        SessionManager.shared.setContext(getApplicationContext());
        Preferences.shared.setContext(getApplicationContext());
        Preferences.shared.setBoolean("isMute", true);
        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            Log.d("push_notification_msg", "initUi: "+ extras.toString());
            if (extras.containsKey("msg")) {
                String msgString = extras.getString("msg");
                Preferences.shared.setString("push_notification_msg",msgString);
            } else if (extras.containsKey("data")) {
                String msgString = extras.getString("data");
                Preferences.shared.setString("push_notification_data",msgString);
            }
        }
        new Handler().postDelayed(this::openHomeScreen, 2000);

    }

    private void handleDeepLink(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri data = intent.getData();
            if (data != null) {
                Preferences.shared.removeKey("venueId_deeplink");
                Preferences.shared.removeKey("userId_deeplink");
                Preferences.shared.removeKey("offerId_deeplink");
                Preferences.shared.removeKey("promoterEventId_deeplink");
                Preferences.shared.removeKey("raynaTicket_deeplink");
                Preferences.shared.removeKey("raynaTicketDetail_deeplink");
                List<String> paths = data.getPathSegments();
                String path = data.getLastPathSegment();
                if (paths.contains("v")) {
                    Preferences.shared.setString("venueId_deeplink", path);
                } else if (paths.contains("u")) {
                    Preferences.shared.setString("userId_deeplink", path);
                } else if (paths.contains("o")) {
                    Preferences.shared.setString("offerId_deeplink", path);
                } else if (paths.contains("p")) {
                    Preferences.shared.setString("promoterEventId_deeplink", path);
                }else if (paths.contains("t")) {
                    Preferences.shared.setString("raynaTicketDetail_deeplink", path);
                } else if (paths.contains("invoice")) {
                    Preferences.shared.setString("raynaTicket_deeplink", path);
                }
            }
        }
    }


    protected void setListeners() { }


    protected int getLayoutRes() {
        return R.layout.activity_splash;
    }


    protected View getLayoutView() {
        return null;
    }

    @Override
    protected boolean shouldApplyTopInsetPadding() {
        return false;
    }
    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void openHomeScreen() {
        if (!SessionManager.shared.getToken().isEmpty()) {
            if (!SessionManager.shared.getUser().isGuest() && TextUtils.isEmpty(SessionManager.shared.getUser().getFirstName())){
                startActivity(new Intent(this, AuthUserNameActivity.class));
            } else {
                startActivity(new Intent(this, MainHomeActivity.class));
            }
        } else {
            startActivity(new Intent(this, AuthenticationActivity.class));
        }
        finish();
    }

    private void requestNextPermission() {
        if (currentPermissionIndex < REQUIRED_PERMISSIONS.length) {
            String permission = REQUIRED_PERMISSIONS[currentPermissionIndex];
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                currentPermissionIndex++;
                requestNextPermission();
                return;
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // Permission has been denied previously, don't show the dialog again, move to the next permission
                currentPermissionIndex++;
                requestNextPermission();
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        } else {
            openHomeScreen();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            currentPermissionIndex++;
            requestNextPermission();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------



}