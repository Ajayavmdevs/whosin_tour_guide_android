package com.whosin.app.ui.activites.auth;

import static com.whosin.app.comman.Graphics.context;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.whosin.app.R;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityAuthenticationBinding;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.MainHomeActivity;

public class AuthenticationActivity extends BaseActivity {

    private ActivityAuthenticationBinding mBinding;

    private GoogleLogin googleLogin;

    private  boolean isGuestLogin = false;

    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    @Override
    protected void initUi() {

        mBinding.tvMainTitle.setText(getValue("welcome_to"));
        mBinding.tvSignUpOrLoginTitle.setText(getValue("sign_up_or_login_to_enjoy_our_best_features"));
        mBinding.tvSignInWithGoogle.setText(getValue("sign_in_with_google"));
        mBinding.tvSignInWithEmail.setText(getValue("sign_in_with_email"));
        mBinding.continueGuest.setText(getValue("continue_as_a_guest"));
        String htmlText = getValue("refer_to_our_privacy_policy");
        Spanned spanned = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY);
        mBinding.termsCondition.setText(spanned);

        isGuestLogin = getIntent().getBooleanExtra("isGuestLogin",false);

        if (!isGuestLogin){
            if (!TextUtils.isEmpty(AppSettingManager.shared.authRequestId)) {
                Intent intent = new Intent(AuthenticationActivity.this, TwoFactorWaitingActivity.class);
                startActivity(intent);
            } else {
                SessionManager.shared.clearSessionData(activity);
            }
            SessionManager.shared.clearSessionData(activity);

            Preferences.shared.setContext(activity);
            Preferences.shared.setBoolean("isMute", true);
            SessionManager.shared.setContext(activity);

            Preferences.shared.setString("loginType", "");
            Preferences.shared.setString("promoterId", "");
        }else {
            mBinding.continueGuest.setVisibility(View.GONE);
        }

        mBinding.termsCondition.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.termsCondition.setTextColor(ContextCompat.getColor(context, R.color.grey_text));

        googleLogin = new GoogleLogin(this);

        String text = getValue("contact_us") + " : info@whosin.me";
        SpannableString spannableString = new SpannableString(text);

        // Create a ClickableSpan for the email part
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:info@whosin.me")); // Use mailto: scheme
                widget.getContext().startActivity(emailIntent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
//                Context context = widget.getContext();
                ds.setColor(ContextCompat.getColor(context, R.color.white));
                ds.setColor(Color.WHITE); // Set text color to white
                ds.setUnderlineText(true); // Enable underline
            }
        };


        int startIndex = text.indexOf("info@whosin.me");
        int endIndex = startIndex + "info@whosin.me".length();
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        mBinding.contactUs.setText(spannableString);
        mBinding.contactUs.setMovementMethod(LinkMovementMethod.getInstance());


    }

    @Override
    protected void setListeners() {

        googleListener();


        mBinding.emailContainer.setOnClickListener(view -> {
            Intent intent = new Intent(AuthenticationActivity.this, AuthUserPhoneNumberActivity.class);
            intent.putExtra("isGuestLogin",isGuestLogin);
            activityLauncher.launch(intent, result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Preferences.shared.setInt("isGuestLogin", 0);
                    Intent closeIntent = new Intent();
                    setResult(RESULT_OK, closeIntent);
                    finish();
                }
            });

//            startActivity(intent);
        });

        mBinding.googleContainer.setOnClickListener(view -> {
            googleLogin.signIn();
        });

        mBinding.continueGuest.setOnClickListener(view -> {
            getGuestLogin();
        });


        mBinding.termsCondition.setOnClickListener(view -> startActivity(new Intent(activity, TermsConditionActivity.class)));
    }


    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        mBinding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (GoogleLogin.RC_SIGN_IN == requestCode) {
            googleLogin.onActivityResult(requestCode, resultCode, data);
        }
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void googleListener() {
        try {
            googleLogin.setGoogleListener(new GoogleLogin.GoogleListener() {
                @Override
                public void onConnectionFailed(int errorCode, String errorMessage) {
                    hideProgress();
                    Toast.makeText(activity, getValue("connection_failed"), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLoginSuccess(GoogleSignInAccount googleSignInAccount) {
                    try {
                        hideProgress();
                        getGoogleLogin(googleSignInAccount.getIdToken());

                    } catch (Exception e) {
                        Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        hideProgress();
                    }
                }

                @Override
                public void onFailed(ApiException status) {
                    hideProgress();
                    Log.d("TAG", "Message: " + status.getMessage());
                    Log.d("TAG", "Status: " + status.getStatus());
                    Log.d("TAG", "Status: " + status.toString());
//                    Toast.makeText( activity, "abc " + status.getMessage() + " def " + status.getStatus() + " fgh " + status.toString(), Toast.LENGTH_SHORT ).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }
    }


    // endregion
    // --------------------------------------
    // region Data/Services
    // --------------------------------------

    private void getGoogleLogin(String token) {
        showProgress();
        SessionManager.shared.loginWithGoogle(token, activity, (success, error) -> {
            hideProgress();
            if (!Utils.isNullOrEmpty(error)) {
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                return;
            }
            if (success) {
                AppSettingManager.shared.requestAppSetting( context );
                Preferences.shared.setInt("isUserGoogleLogin", 1);
                if (isGuestLogin) {
                    Preferences.shared.setInt("isGuestLogin", 0);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    startActivity(new Intent(activity, MainHomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            }
        });

    }

    private void getGuestLogin() {
        showProgress();
        SessionManager.shared.guestLogin(activity, (success, error) -> {
            hideProgress();
            if (!Utils.isNullOrEmpty(error)) {
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                return;
            }
            if (success) {
                AppSettingManager.shared.requestAppSetting( context );
                Preferences.shared.setInt("isGuestLogin", 1);
                startActivity(new Intent(activity, MainHomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

    }


    // endregion
    // --------------------------------------
}



