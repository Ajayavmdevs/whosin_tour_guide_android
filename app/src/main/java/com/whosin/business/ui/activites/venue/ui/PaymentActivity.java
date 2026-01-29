package com.whosin.business.ui.activites.venue.ui;

import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.whosin.business.R;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.databinding.ActivityPaymentBinding;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.MemberShipPackageModel;
import com.whosin.business.ui.activites.comman.BaseActivity;

public class PaymentActivity extends BaseActivity {

    private ActivityPaymentBinding binding;
    private MemberShipPackageModel model;
    public CommanCallback<Boolean> callback;
    private String email = "", id = "", url = "";


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        String s = getIntent().getStringExtra( "packageModel" );
        model = new Gson().fromJson( s, MemberShipPackageModel.class );
        email = SessionManager.shared.getUser().getEmail();
        id = SessionManager.shared.getUser().getId();
        openWebView();

    }

    @Override
    protected void setListeners() {
        binding.iconClose.setOnClickListener( view -> onBackPressed() );
        Glide.with( activity ).load( R.drawable.icon_close_btn ).into( binding.iconClose );
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityPaymentBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------





    private void openWebView() {
        binding.webView.getSettings().setJavaScriptEnabled( true );

        String url = model.getPaymentLink().replace( "user_email_here", SessionManager.shared.getUser().getEmail() ).
                replace( "user_id_here", SessionManager.shared.getUser().getId() );


        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                return false;
            }
        });



        binding.webView.loadUrl( url );



    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    // endregion
    // --------------------------------------
}