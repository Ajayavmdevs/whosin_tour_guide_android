package com.whosin.business.ui.activites.comman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.whosin.business.R;
import com.whosin.business.comman.Graphics;
import com.whosin.business.service.manager.TranslationManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class BaseActivity extends AppCompatActivity {

    @Nullable
    private AlertDialog dialog = null;
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);
    private View view;

    protected Activity activity;
    protected Bundle saveBundle;

    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    protected boolean shouldApplyTopInsetPadding() {
        return true; // default is true
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Graphics.context = getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Whosin);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        activity = this;
        Graphics.context = getApplicationContext();
        if (getLayoutView() != null) {
            view = getLayoutView();
            setContentView(view);
        } else {
            setContentView(getLayoutRes());
        }
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).hide();
        }
//        setUpNointernetScreen();
        saveBundle = savedInstanceState;
        initUi();
        setListeners();
        populateData();

//        busWrapper = getOttoBusWrapper(new Bus());
//        networkEvents = new NetworkEvents(activity, busWrapper).enableInternetCheck();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        applyInsets(findViewById(android.R.id.content));
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        applyInsets(view);
    }

    private void applyInsets(View rootView) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            int topPadding = shouldApplyTopInsetPadding() ? systemBars.top : 0;
            v.setPadding(0, topPadding, 0, systemBars.bottom);
            return insets;
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
//        busWrapper.register(this);
//        networkEvents.register();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        busWrapper.unregister(this);
//        networkEvents.unregister();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TranslationManager.updateLocale(newBase));
    }

    // endregion
    // --------------------------------------
    // region Protected
    // --------------------------------------


    protected void showProgress() {
        if (isFinishing()) {
            return;
        }
        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setView(R.layout.layout_loading_dialog);
            dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.show();
    }

    protected void showProgress(String msg) {
        if (isFinishing()) {
            return;
        }
        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setView(R.layout.layout_loading_dialog);
            dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.show();

    }

    protected void hideProgress() {
        if (isFinishing()) { return; }
        if (isDestroyed()) { return; }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

//    protected BusWrapper getOttoBusWrapper(final Bus bus) {
//        return new BusWrapper() {
//            @Override
//            public void register(Object object) {
//                bus.register(object);
//            }
//
//            @Override
//            public void unregister(Object object) {
//                bus.unregister(object);
//            }
//
//            @Override
//            public void post(Object event) {
//                bus.post(event);
//            }
//        };
//    }
//
//    @Subscribe
//    public void onEvent(ConnectivityChanged event) {
//        Log.d("TAG", "onEvent: " + event.getConnectivityStatus());
//
//    }
//
//    @Subscribe
//    public void onEvent(WifiSignalStrengthChanged event) {
//        Log.d("TAG", "onEvent: " + event);
//        // do whatever you want - e.g. read fresh list of access points
//        // via event.getWifiScanResults() method
//    }


    protected abstract void initUi();

    protected abstract void setListeners();

    protected void populateData() {
    }

    protected abstract int getLayoutRes();

    protected abstract View getLayoutView();


    protected void setTranslatedText(View view, String key) {
        if (view != null && key != null) {
            String translated = TranslationManager.shared.get(key);

            if (translated != null) {
                if (view instanceof TextView) {
                    if (view instanceof EditText) {
                        ((EditText) view).setHint(translated);
                    } else {
                        ((TextView) view).setText(translated);
                    }
                }
            }
        }
    }

    protected void setTranslatedTexts(Map<View, String> map) {
        for (Map.Entry<View, String> entry : map.entrySet()) {
            setTranslatedText(entry.getKey(), entry.getValue());
        }
    }

    protected Map<View, String> getTranslationMap() {
        return new HashMap<>();
    }

    protected void applyTranslations() {
        Map<View, String> map = getTranslationMap();
        if (map == null) return;

        for (Map.Entry<View, String> entry : map.entrySet()) {
            setTranslatedText(entry.getKey(), entry.getValue());
        }
    }


    protected String getValue(String key){
        return TranslationManager.shared.get(key);
    }

    protected String setValue(String key, String value) {
        String template = TranslationManager.shared.get(key);
        if (template == null) return "";
        return template.replaceAll("\\{.*?\\}", value);
    }

    protected String setValue(String key, String... values) {
        String template = TranslationManager.shared.get(key);
        if (template == null) return "";

        for (String val : values) {
            template = template.replaceFirst("\\{.*?\\}", val);
        }

        return template;
    }



    // endregion
    // --------------------------------------
}
