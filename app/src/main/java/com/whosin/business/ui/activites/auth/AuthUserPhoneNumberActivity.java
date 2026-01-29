package com.whosin.business.ui.activites.auth;

import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.play.core.integrity.IntegrityManager;
import com.google.android.play.core.integrity.IntegrityManagerFactory;
import com.google.android.play.core.integrity.IntegrityTokenRequest;
import com.google.gson.JsonObject;
import com.whosin.business.comman.Utils;

import com.whosin.business.databinding.ActivityAuthUserPhoneNumberBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.Repository.ChatRepository;
import com.whosin.business.service.manager.GetNotificationManager;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.comman.BaseActivity;
import com.whosin.business.ui.activites.home.MainHomeActivity;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class AuthUserPhoneNumberActivity extends BaseActivity {

    private ActivityAuthUserPhoneNumberBinding binding;

    public JsonObject object = new JsonObject();

    private boolean isGuestLogin = false;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        isGuestLogin = getIntent().getBooleanExtra("isGuestLogin",false);

        binding.termsCondition.setMovementMethod(LinkMovementMethod.getInstance());

        binding.countryCode.setAutoDetectedCountry(true);


    }

    @Override
    protected void setListeners() {
        binding.backButton.setOnClickListener( view -> onBackPressed());

//        binding.imageNext.setOnClickListener( view -> {
//            setMobileNumber();
//        } );

        binding.btnContinue.setOnClickListener(view -> {
//            Intent intent = new Intent(AuthenticationActivity.this, TwoFactorWaitingActivity.class);
//            startActivity(intent);
//            return;

            String value = binding.editTextPhone.getText().toString();
            if (value.isEmpty()) {
                Toast.makeText(this, getValue("please_enter_phone"), Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isDigitsOnly(value)) {
                String region = binding.countryCode.getSelectedCountryNameCode();
                if (!Utils.isValidPhoneNumber(binding.countryCode.getSelectedCountryCode(), value, region)) {
                    Toast.makeText(this, getValue("phone_number_is_not_valid"), Toast.LENGTH_LONG).show();
                    return;
                }
                object.addProperty("phone", value);
                object.addProperty("countryCode", String.valueOf(binding.countryCode.getSelectedCountryCodeAsInt()));
            } else if (Utils.isValidEmail(value)) {
                object.addProperty("phone", value);
                object.addProperty("countryCode", "");
            } else {
                Toast.makeText(this, getValue("valid_email_or_phone_number"), Toast.LENGTH_LONG).show();
                return;
            }
//            getToken();
//            loginWithPhone(object);
            getIntegrityToken();
        });

        binding.editTextPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = binding.editTextPhone.getText().toString();
                if (value.length() > 2 && TextUtils.isDigitsOnly(value)) {
                    binding.countryCode.setVisibility(View.VISIBLE);
                } else {
                    binding.countryCode.setVisibility(View.GONE);
                }
            }
        });

        binding.termsCondition.setOnClickListener(view -> startActivity(new Intent(activity, TermsConditionActivity.class)));

    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();

        map.put(binding.tvMainTitle, "welcome_to");
        map.put(binding.tvSignUpOrLoginTitle, "sign_up_or_login_to_enjoy_our_best_features");
        map.put(binding.editTextPhone, "enter_email");

        String htmlText = getValue("refer_to_our_privacy_policy");
        Spanned spanned = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY);
        binding.termsCondition.setText(spanned);

        return map;
    }

    // region Private
    // --------------------------------------


    private void getIntegrityToken(){
        showProgress();
        IntegrityManager integrityManager = IntegrityManagerFactory.create(this);

        // Create a token request
        IntegrityTokenRequest tokenRequest = IntegrityTokenRequest.builder()
                .setNonce(generateNonce())
                .build();

        // Request the token
        integrityManager.requestIntegrityToken(tokenRequest)
                .addOnSuccessListener(response -> {
                    hideProgress();
                    // Get the token
                    String integrityToken = response.token();
                    object.addProperty("deviceToken",integrityToken);
                    loginWithPhone(object);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        hideProgress();
                        // Handle error
                        e.printStackTrace();
                        Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }


    public String generateNonce() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[50]; // Generate 50 random bytes
        secureRandom.nextBytes(randomBytes);

        // Use Base64 encoding with NO_PADDING, NO_WRAP, and URL_SAFE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        }
        return "";
    }

    // endregion
    // --------------------------------------

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityAuthUserPhoneNumberBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }




    // endregion
    // --------------------------------------



    private void loginWithPhone(JsonObject object) {
        showProgress();
        Log.d("LOGIN", object.toString());
        DataService.shared(this).requestLoginWithPhone(object, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();


                if (!Utils.isNullOrEmpty(error) || model == null ||  model.getData() == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    Log.d("error", "result: " + error);
                    return;
                }

//                if (!error.equalsIgnoreCase("We just send you an OTP please try after 90 seconds!")){
//                    if (!Utils.isNullOrEmpty(error) || model == null) {
//                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
//                        Log.d("error", "result: " + error);
//                        return;
//                    }
//                }


//                if (model != null && model.getData() != null) otpModel = model.getData();

                if (model.getData() != null && model.getData().getType().equals("otp")) {
                    String value = binding.editTextPhone.getText().toString();
                    ChatRepository.shared(AuthUserPhoneNumberActivity.this).clearDb();
                    OtpVerificationBottomSheet bottomSheet = new OtpVerificationBottomSheet();
                    bottomSheet.isForEmail = Utils.isValidEmail(value);
                    bottomSheet.userId = model.getData().getUserId();
                    bottomSheet.isSignup = model.getData().getSignUp();
                    bottomSheet.activity = activity;
                    bottomSheet.isManagePromoter = model.getData().isManagePromoter();
                    bottomSheet.callback = data -> {
                        if (!data) {
                            if (isGuestLogin){
                                Intent closeIntent = new Intent();
                                setResult(RESULT_OK, closeIntent);
                                finish();
                                return;
                            }
                            if (SessionManager.shared.getUser().getFirstName().isEmpty()) {
                                Intent intent = new Intent(AuthUserPhoneNumberActivity.this, AuthUserNameActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                GetNotificationManager.shared.requestActivityUpdatesCount();
                                Utils.saveLastOneMonthSyncDate();
                                Intent intent = new Intent(AuthUserPhoneNumberActivity.this, MainHomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                        } else {
                            Intent intent = new Intent(AuthUserPhoneNumberActivity.this, TwoFactorWaitingActivity.class);
                            startActivity(intent);
                        }
                    };
                    bottomSheet.resendOTP = data -> {
                        if (data.equals("ResendOTP")) {
                            bottomSheet.dismiss();
                            loginWithPhone(object);
                        }
                    };
                    bottomSheet.show(getSupportFragmentManager(), "1");
                } else {
                    startActivity(new Intent(AuthUserPhoneNumberActivity.this, AuthUsingPasswordActivity.class).putExtra("userId", model.getData().getUserId()));
                }
            }
        });
    }
}