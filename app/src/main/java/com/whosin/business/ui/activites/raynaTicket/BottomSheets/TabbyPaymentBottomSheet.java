package com.whosin.business.ui.activites.raynaTicket.BottomSheets;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.business.R;
import com.whosin.business.comman.PermissionRequester;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.databinding.FragmentTabbyPaymentBottomSheetBinding;
import com.whosin.business.service.models.PaymentTabbyModel;

public class TabbyPaymentBottomSheet extends DialogFragment {

    private FragmentTabbyPaymentBottomSheetBinding binding;

    public PaymentTabbyModel paymentTabbyModel;

    public CommanCallback<String> callback;

    private final PermissionRequester permissionRequester = new PermissionRequester();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;


    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initUi(View v) {

        binding = FragmentTabbyPaymentBottomSheetBinding.bind(v);

        permissionRequester.multiplePermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissionRequester);

        permissionRequester.fileChooserLauncher =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), permissionRequester::handleFileChooserResult);

        binding.webView.setWebChromeClient(permissionRequester.getWebChromeClient());

        binding.webView.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        if (paymentTabbyModel != null){
            binding.webView.loadUrl(paymentTabbyModel.web_url);
        }


    }
    private void setListener() {

        binding.ivClose.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dismiss();
        });

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                if (url.contains("https://whosin.me/tabby/success")) {
                    if (callback != null) {
                        callback.onReceive("success");
                        dismiss();
                    }
                    return true;
                } else if (url.contains("https://whosin.me/tabby/cancel")) {

                    if (callback != null) {
                        callback.onReceive("cancel");
                        dismiss();
                    }
                    return true;
                } else if (url.contains("https://whosin.me/tabby/failure")) {
                    if (callback != null) {
                        callback.onReceive("failure");
                        dismiss();
                    }
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, request);
            }
        });



    }
    public int getLayoutRes() {
        return R.layout.fragment_tabby_payment_bottom_sheet;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------



}