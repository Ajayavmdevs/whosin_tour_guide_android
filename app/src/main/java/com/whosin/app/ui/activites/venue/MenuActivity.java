package com.whosin.app.ui.activites.venue;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.whosin.app.databinding.ActivityMenuBinding;
import com.whosin.app.ui.activites.comman.BaseActivity;

public class MenuActivity extends BaseActivity {

    private ActivityMenuBinding binding;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initUi() {
        String name = getIntent().getStringExtra( "name" );
        String menu = getIntent().getStringExtra( "url" );

        if (!TextUtils.isEmpty(name)) binding.tvName.setText( name );


        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(false);
        webSettings.setAllowContentAccess(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebViewConsole", consoleMessage.message() + " -- From line " +
                        consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
                return false;
            }
        });

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                hideProgress();
                Log.e("WebView", "Error: " + error.getDescription() + " | Code: " + error.getErrorCode());

            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showProgress();
                Log.d("WebView", "Started: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideProgress();
                Log.d("WebView", "Finished: " + url);
            }
        });

        if (!TextUtils.isEmpty(menu)) {
            if (menu.toLowerCase().endsWith(".pdf")) {
//                binding.webView.loadUrl( "https://docs.google.com/gview?embedded=true&url=" + menu );
                String encodedUrl = Uri.encode(menu);
                String driveViewerUrl = "https://drive.google.com/viewerng/viewer?embedded=true&url=" + encodedUrl;
                binding.webView.loadUrl(driveViewerUrl);
            } else {
                binding.webView.loadUrl(menu);
            }
        } else {
            Log.e("WebView", "URL is empty or null");
        }

    }

    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener( view -> {
            onBackPressed();
        } );
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityMenuBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }
    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

}