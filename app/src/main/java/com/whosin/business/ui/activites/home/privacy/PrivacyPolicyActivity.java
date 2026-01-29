package com.whosin.business.ui.activites.home.privacy;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.whosin.business.databinding.ActivityPrivacyPolicyBinding;
import com.whosin.business.ui.activites.comman.BaseActivity;

public class PrivacyPolicyActivity extends BaseActivity {

    private ActivityPrivacyPolicyBinding binding;

    private String title = "";


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initUi() {

        title = getIntent().getStringExtra("type");


        String url;
        if ("Privacy Policy".equalsIgnoreCase(title)) {
            url = "https://whosin.me/privacy-policy/";
            binding.tvName.setText(getValue("privacy_policy"));
        } else {
            url = "https://whosin.me/terms-conditions/";
            binding.tvName.setText(getValue("terms_condition"));
        }

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setWebViewClient(new WebViewClient());
        WebView webView = binding.webView;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        binding.webView.loadUrl(url);


    }

    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener( v -> {
            onBackPressed();
        } );


        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showProgress();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideProgress();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                hideProgress();
            }

            @Override
            public void onReceivedSslError(WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error) {
                super.onReceivedSslError(view, handler, error);
                hideProgress();
            }
        });


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityPrivacyPolicyBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }
}