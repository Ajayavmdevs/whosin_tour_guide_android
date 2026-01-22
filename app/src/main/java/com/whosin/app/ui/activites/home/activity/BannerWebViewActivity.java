package com.whosin.app.ui.activites.home.activity;

import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.whosin.app.R;
import com.whosin.app.databinding.ActivityBannerWebViewBinding;
import com.whosin.app.ui.activites.comman.BaseActivity;

public class BannerWebViewActivity extends BaseActivity {

    private ActivityBannerWebViewBinding binding;

    private String Link;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {
        openWebView();

    }

    @Override
    protected void setListeners() {
        binding.iconClose.setOnClickListener(view -> onBackPressed());
        Glide.with( activity ).load( R.drawable.icon_close_btn ).into( binding.iconClose );


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    protected View getLayoutView() {
        binding = ActivityBannerWebViewBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

//    private void openWebView() {
//        binding.webView.getSettings().setJavaScriptEnabled( true );
//
//        Link = getIntent().getStringExtra("link");
//
//        String url = Link;
//        binding.webView.loadUrl( url );
//        binding.webView.setWebViewClient( new WebViewClient() );
//
//
//    }

    private void openWebView() {
        String link = getIntent().getStringExtra("link");

        if (link != null && !link.trim().isEmpty()) {
            WebSettings settings = binding.webView.getSettings();
            settings.setJavaScriptEnabled(true);
            binding.webView.setWebViewClient(new WebViewClient());
            binding.webView.loadUrl(link);
        } else {
            Toast.makeText(this, "Invalid or empty link", Toast.LENGTH_SHORT).show();
        }
    }



    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

}