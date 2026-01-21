package com.whosin.app.ui.activites.auth;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentOtpVerificationBottomSheetBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.UserTokenModel;
import com.whosin.app.service.rest.RestCallback;

import java.util.Objects;

public class OtpVerificationBottomSheet extends BottomSheetDialogFragment {

    private FragmentOtpVerificationBottomSheetBinding binding;
    public String userId;
    public boolean isForEmail = false;

    public boolean isManagePromoter = false;

    public boolean isSignup;
    public CommanCallback<Boolean> callback;
    public CommanCallback<String> resendOTP;

    private CountDownTimer countDownTimer;
    private String resendOtpText;
    public Activity activity;

    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.OtpDialogStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOtpVerificationBottomSheetBinding.inflate(inflater, container, false);

        binding.btnVerify.setTxtTitle(Utils.getLangValue("verify"));

        if (activity == null){
            activity = requireActivity();
        }

        initUi();
        setupListener();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((BottomSheetDialog) getDialog()).getBehavior().setState(STATE_EXPANDED);
    }



    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void initUi() {
        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);

        resendOtpText = Utils.getLangValue("resend_otp");

        binding.infoTextView.setMainTitle(isSignup ? Utils.getLangValue("account_created") : Utils.getLangValue("verification"));
        binding.infoTextView.setSubTitleText(isForEmail ? Utils.getLangValue("otp_sent_phone") : Utils.getLangValue("otp_sent_email"));
        if (isForEmail){
            binding.infoTextView.setAlertTitleText(Utils.getLangValue("otp_check_folders"));
        }

        if (isForEmail && !isSignup){
            binding.infoTextView.setMainTitle(Utils.getLangValue("verify_email_address"));
        } else if (!isForEmail && !isSignup) {
            binding.infoTextView.setMainTitle(Utils.getLangValue("verify_mobile"));
        }
        binding.otpTextField.requestFocus();
        disableVerifyButton();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.otpTextField.getWindowToken(), 0);

        binding.resendOtpTv.setClickable(false);
        binding.resendOtpTv.setText("");


        countDownTimer = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = (int) (millisUntilFinished / 1200);
                int timer = (int) (millisUntilFinished / 1000);
                binding.txtTimer.setText(String.valueOf(timer));
                binding.progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                binding.resendOtpTv.setVisibility(View.VISIBLE);
//                binding.resendOtpTv.setText(setResendTextColor(getString(R.string.resend_otp)));
                binding.resendOtpTv.setText(setResendTextColor(resendOtpText));
                binding.resendOtpTv.setClickable(true);
                binding.indicatorView.setImage(R.drawable.icon_right_check);
                binding.indicatorView.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                binding.txtTimer.setVisibility(View.GONE);


            }
        }.start();

    }

    private void setupListener() {
        binding.otpTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && s.length() == 4) {
                    enableVerifyButton();
                } else {
                    disableVerifyButton();
                }
            }
        });


        binding.btnVerify.setOnClickListener(view -> verifyOtp());

        binding.ivClose.setOnClickListener(view -> {
            dismiss();
            countDownTimer.cancel();
        });

        binding.resendOtpTv.setOnClickListener(view -> {
            binding.indicatorView.setVisibility(View.GONE);
            countDownTimer.start();
            if (resendOTP != null) {
                resendOTP.onReceive("ResendOTP");
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void verifyOtp() {

        binding.indicatorView.setImage(R.drawable.icon_cent_clock);
        binding.btnVerify.startProgress();
        SessionManager.shared.verifyOtp(isManagePromoter,userId, binding.otpTextField.getText().toString(), activity, new RestCallback<UserTokenModel>() {
            @Override
            public void result(UserTokenModel model, String error) {
                binding.btnVerify.stopProgress();
                if (!Utils.isNullOrEmpty(error)) {
                    binding.indicatorView.setImage(R.drawable.icon_right_check);
                    Toast.makeText(Graphics.context, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model != null) {
                    binding.indicatorView.setImage(R.drawable.icon_check_green);
                    if (!Utils.isNullOrEmpty(model.getLoginType())) {
                        if (model.getLoginType().equals("sub-admin")){
                            SessionManager.shared.saveSubAdminUserData(model,requireContext());
                        }
                        Preferences.shared.setString("loginType", model.getLoginType());
                    }
                    if (!Utils.isNullOrEmpty(model.getPromoterId())) {
                        Preferences.shared.setString("promoterId", model.getPromoterId());
                    }
                    new Handler().postDelayed(() -> {
                        dismiss();
                        countDownTimer.cancel();
                        callback.onReceive(model.isAuthenticationPending());
//                        callback.onReceive(false);
                        Utils.hideKeyboard( activity);
                    }, 300);
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.otpTextField.getWindowToken(), 0);
                } else {
                    binding.indicatorView.setImage(R.drawable.icon_right_check);
                }
            }
        });
//        SessionManager.shared.verifyOtp(userId, binding.otpTextField.getText().toString(), getContext(), (success, error) -> {
//            binding.btnVerify.stopProgress();
//            if (!Utils.isNullOrEmpty(error)) {
//                binding.indicatorView.setImage(R.drawable.icon_right_check);
//                Toast.makeText(Graphics.context, error, Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (success) {
//                binding.indicatorView.setImage(R.drawable.icon_check_green);
//                new Handler().postDelayed(() -> {
//                    dismiss();
//                    callback.onReceive(true);
//                    cancelTimer();
//                    Utils.hideKeyboard( getView(),requireActivity());
//                }, 500);
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(binding.otpTextField.getWindowToken(), 0);
//            } else {
//                binding.indicatorView.setImage(R.drawable.icon_right_check);
//            }
//        });
    }





//    private SpannableString setResendTextColor(String text) {
//        SpannableString spannableString = new SpannableString(text);
//        int startIndex = text.indexOf("Resend");
//        int endIndex = startIndex + "Resend".length();
//        spannableString.setSpan(new ForegroundColorSpan(activity.getColor(R.color.brand_pink)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return spannableString;
//    }

    private SpannableString setResendTextColor(String text) {
        String openTag = "<resend>";
        String closeTag = "</resend>";

        int startIndex = text.indexOf(openTag);
        int endIndex = text.indexOf(closeTag);

        if (startIndex == -1 || endIndex == -1) {
            return new SpannableString(text); // fallback
        }

        // Extract clean text (without tags)
        String cleanText = text.replace(openTag, "").replace(closeTag, "");

        SpannableString spannableString = new SpannableString(cleanText);

        // Adjust indexes after removing openTag
        int tagLength = openTag.length();
        int realStart = startIndex;
        int realEnd = endIndex - tagLength;

        spannableString.setSpan(
                new ForegroundColorSpan(activity.getColor(R.color.brand_pink)),
                realStart,
                realEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        return spannableString;
    }


    private void disableVerifyButton() {
        binding.btnVerify.setClickable(false);
        binding.btnVerify.setBgColor( ContextCompat.getColor(activity, R.color.medium_pink) );
    }

    private void enableVerifyButton() {
        binding.btnVerify.setClickable(true);
        binding.btnVerify.setBgColor( ContextCompat.getColor(activity, R.color.brand_pink) );
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(activity, R.style.BottomSheetDialogThemeNoFloating);
    }
    // endregion
    // --------------------------------------
}