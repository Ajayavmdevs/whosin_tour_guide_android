package com.whosin.app.ui.activites.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.ActivityTwoFactorAuthBinding;
import com.whosin.app.databinding.ActivityTwoFactorWaitingBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.GetNotificationManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.LoginRequestModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.MainHomeActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

public class TwoFactorWaitingActivity extends BaseActivity {

    private ActivityTwoFactorWaitingBinding binding;

    private CountDownTimer countDownTimer;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (SessionManager.shared.getUser() != null) {
            binding.txtPhoneNumber.setText(String.format("%s %s", SessionManager.shared.getUser().getCountryCode(), SessionManager.shared.getUser().getPhone()));
        }
        binding.txtBottomSubtitle.setText(Utils.makeWordBold(getValue("sent_notification_to_your_phone_tap_for_continue"), "YES"));
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
                AppSettingManager.shared.authRequestId = "";
                TwoFactorWaitingActivity.this.onBackPressed();
            }
        }.start();
    }

    @Override
    protected void setListeners() {
        binding.btnMoreOption.setOnClickListener(v -> {
            TwoFactorEmailOptionBottomSheet bottomSheet = new TwoFactorEmailOptionBottomSheet();
            bottomSheet.callback = data -> {
                countDownTimer.cancel();
                countDownTimer.start();
                UserDetailModel user = SessionManager.shared.getUser();
                if (user != null) {
                    binding.txtPhoneNumber.setText(user.getEmail());
                    binding.txtBottomTitle.setText(getValue("check_your_email"));
                    binding.txtBottomSubtitle.setText(getValue("sent_link_email"));
                }
            };
            bottomSheet.show(getSupportFragmentManager(), "1");
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityTwoFactorWaitingBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestGetLoginRequest();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(JsonObject event) {
        Log.d("TAG", "onMessageEvent: " + new Gson().toJson(event));
        if (event.get("status").getAsString().equals("approved") && event.get("device_id").getAsString().equals(Utils.getDeviceUniqueId(Graphics.context))) {
            AppSettingManager.shared.authRequestId = "";
            if (SessionManager.shared.getUser().getFirstName().isEmpty()) {
                startActivity(new Intent(this, AuthUserNameActivity.class));
                finish();
            } else {
                GetNotificationManager.shared.requestActivityUpdatesCount();
                AppSettingManager.shared.requestAppSetting( TwoFactorWaitingActivity.this );
                Intent intent = new Intent(this, MainHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Utils.hideKeyboard(this);
                finish();
            }
        } else  if (event.get("status").getAsString().equals("reject") && event.get("device_id").getAsString().equals(Utils.getDeviceUniqueId(Graphics.context))) {
            AppSettingManager.shared.authRequestId = "";
            finish();
        }
    }


    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();

        map.put(binding.tvTwoStepVerificationTitle, "two_step_verification");
        map.put(binding.tvExtraStepTitle, "its_really_you_trying_to_sign_in");
        map.put(binding.txtBottomTitle, "check_your_other_phone");
        map.put(binding.txtBottomSubtitle, "sent_notification_to_your_phone_tap_for_continue");

        binding.btnMoreOption.setTxtTitle(getValue("more_option"));

        return map;
    }

    // endregion
    // --------------------------------------
    // region private
    // --------------------------------------

    private void requestGetLoginRequest() {
        String id = AppSettingManager.shared.authRequestId;
        if (TextUtils.isEmpty(id)) {
            return;
        }
        DataService.shared(this).requestUserAuthRequest(id, new RestCallback<ContainerModel<LoginRequestModel>>(this) {
            @Override
            public void result(ContainerModel<LoginRequestModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model.getData() == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    Log.d("TAG", "requestUserAuthRequest: " + new Gson().toJson(model));
                    AppSettingManager.shared.authRequestId = "";
                    LoginRequestModel.Metadata metaData = model.getData().getMetadata();
                    if (model.getData().getStatus().equals("approved") && metaData.getDeviceId().equals(Utils.getDeviceUniqueId(Graphics.context))) {
                        if (!TextUtils.isEmpty(metaData.getToken())) {
                            Preferences.shared.setString( "token", metaData.getToken() );
                        }
                        startActivity(new Intent(TwoFactorWaitingActivity.this, MainHomeActivity.class));
                        Utils.hideKeyboard(activity);
                        finish();
                    }
                    else if (model.getData().getStatus().equals("reject") && metaData.getDeviceId().equals(Utils.getDeviceUniqueId(Graphics.context))) {
                        SessionManager.shared.clearSessionData(Graphics.context);
                        finish();
                    }
                }
            }
        });
    }
}