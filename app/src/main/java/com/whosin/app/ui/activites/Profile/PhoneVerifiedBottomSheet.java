package com.whosin.app.ui.activites.Profile;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

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
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.ActivityPhoneVerifiedBottomSheetBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;

import java.util.Objects;

public class PhoneVerifiedBottomSheet extends BottomSheetDialogFragment {

    private ActivityPhoneVerifiedBottomSheetBinding binding;
    private boolean success = false;

    public String userId;
    public CommanCallback<Boolean> callback;
    public String phone, countryCode;
    public boolean isNewPhone = false;
    public CommanCallback<String> resendOTP;
    private CountDownTimer countDownTimer;
    private static final long TIMER_DURATION = 30000;

    public String type = "" ;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setStyle( DialogFragment.STYLE_NORMAL, R.style.DialogStyle );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityPhoneVerifiedBottomSheetBinding.inflate( inflater, container, false );
        initUi();
        setListeners();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((BottomSheetDialog) getDialog()).getBehavior().setState(STATE_EXPANDED);
    }


    private void initUi() {
        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

        binding.infoTextView.setMainTitle(Utils.getLangValue("verification"));
        binding.infoTextView.setSubTitleText(Utils.getLangValue("otp_sent_email"));
        binding.btnVerify.setTxtTitle(Utils.getLangValue("verify"));


        startTimer();
        binding.otpTextField.requestFocus();
        disableVerifyButton();

        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.otpTextField.getWindowToken(), 0);

        if(type.equals( "email" )) {
            binding.infoTextView.setMainTitle(Utils.getLangValue("email_verification"));
            binding.infoTextView.setSubTitleText(Utils.getLangValue("We_sent_digits_to_your_email"));
        }

    }

    private void setListeners() {
        binding.btnVerify.setOnClickListener( view -> {

            if (isNewPhone) {
                requestUserChangeEmailPhone();
            } else {
                requestUserVerifyOtp();

            }
        } );

        binding.ivClose.setOnClickListener(view -> {
            dismiss();
            cancelTimer();
        });

        binding.otpTextField.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.otpTextField.getText().toString().length() == 4) {
                    enableVerifyButton();
                } else {
                    disableVerifyButton();
                }
            }
        } );


    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------
    private void startTimer() {
        countDownTimer = new CountDownTimer(TIMER_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isAdded()) {
                    long secondsRemaining = millisUntilFinished / 1000;
                    binding.resendOtpTv.setText(getString(R.string.resend_otp_timer_format, secondsRemaining));
                }
            }

            @Override
            public void onFinish() {
                if (isAdded()) {
                    binding.resendOtpTv.setText(setResendTextColor(getString(R.string.resend_otp)));
                    binding.resendOtpTv.setClickable(true);
                    binding.resendOtpTv.setOnClickListener(view -> {
                        startTimer();
                        if (resendOTP != null) {
                            resendOTP.onReceive("ResendOTP");
                        }
                    });
                }
            }
        };

        if (isAdded()) {
            binding.resendOtpTv.setClickable(false);
            countDownTimer.start();
        }
    }

    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }


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
                new ForegroundColorSpan(requireActivity().getColor(R.color.brand_pink)),
                realStart,
                realEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        return spannableString;
    }

    private void disableVerifyButton() {
        binding.btnVerify.setClickable(false);
        binding.btnVerify.setBgColor( ContextCompat.getColor(requireContext(), R.color.medium_pink) );
    }

    private void enableVerifyButton() {
        binding.btnVerify.setClickable(true);
        binding.btnVerify.setBgColor( ContextCompat.getColor(requireContext(), R.color.brand_pink) );
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    //


    private void requestUserVerifyOtp() {

        binding.indicatorView.setImage( R.drawable.icon_cent_clock );

        JsonObject object = new JsonObject();
        object.addProperty( "type", type );
        object.addProperty("userId",SessionManager.shared.getUser().getId());
        object.addProperty( "otp", binding.otpTextField.getText().toString() );

        binding.progress.setVisibility( View.VISIBLE );
        DataService.shared( requireActivity() ).requestUserVerifyOtp( object, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                binding.progress.setVisibility( View.GONE );
                if (!Utils.isNullOrEmpty( error )) {
                    binding.indicatorView.setImage( R.drawable.icon_right_check );
                    Toast.makeText( requireContext(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (success) {
                    binding.indicatorView.setImage( R.drawable.icon_check_green );
                    new Handler().postDelayed( () -> {
                        dismiss();
                        callback.onReceive( success );
                    }, 500 );
                } else {
                    binding.indicatorView.setImage( R.drawable.icon_right_check );
                }
                String type = "";

                if (type.equals( "phone" )) {
                    model.getData().setIsPhoneVerified( 1 );

                } else {
                    if (callback != null){
                        callback.onReceive(true);
                    }
                    model.getData().setIsEmailVerified( 1 );
                }



                Toast.makeText( requireActivity(), model.message, Toast.LENGTH_SHORT ).show();
                dismiss();
            }
        } );

    }



    private void requestUserChangeEmailPhone() {

        binding.progress.setVisibility( View.VISIBLE);
        DataService.shared( requireActivity() ).requestVerifyChangePhone( binding.otpTextField.getText().toString(), type, phone, countryCode, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                binding.progress.setVisibility( View.GONE );
                if (!Utils.isNullOrEmpty( error )) {
                    binding.indicatorView.setImage( R.drawable.icon_right_check );
                    Toast.makeText( requireContext(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (success) {
                    binding.indicatorView.setImage( R.drawable.icon_check_green );
                    new Handler().postDelayed( () -> {
                        dismiss();
                        callback.onReceive( success );
                    }, 500 );
                } else {
                    binding.indicatorView.setImage( R.drawable.icon_right_check );
                }

                if (callback != null){
                    callback.onReceive(true);
                }
//                startActivity( new Intent( requireActivity(), UpdateProfileActivity.class ));
                Toast.makeText( requireActivity(), model.message, Toast.LENGTH_SHORT ).show();
                dismiss();


            }
        } );
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


}