package com.whosin.app.ui.activites.auth;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityTwoFactorAuthBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class TwoFactorAuthActivity extends BaseActivity {

    private ActivityTwoFactorAuthBinding binding;
    private String reqId = "";

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        String jsonString = getIntent().getStringExtra("metadata");
        if(!TextUtils.isEmpty(jsonString)) {
            JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
            if(!jsonObject.isJsonNull()) {
                if (jsonObject.has("reqId")) {
                    reqId = jsonObject.get("reqId").getAsString();
                } else if (jsonObject.has("_id")) {
                    reqId = jsonObject.get("_id").getAsString();
                }
                if (jsonObject.has("metadata")) {
                    JsonObject metaData = jsonObject.get("metadata").getAsJsonObject();
                    String deviceName = "";
                    if (metaData.has("device_name")) {
                        deviceName = metaData.get("device_name").getAsString();
                    }
                    if (metaData.has("device_model")) {
                        if (TextUtils.isEmpty(deviceName)) {
                            deviceName = metaData.get("device_model").getAsString();
                        }else {
                            deviceName = deviceName + "(" + metaData.get("device_model").getAsString() +")";
                        }
                    }
                    binding.txtDevice.setText(deviceName);
                    binding.txtLocation.setText(metaData.get("device_location").getAsString());
                }
            }
        }

    }

    @Override
    protected void setListeners() {
        binding.btnNo.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            requestApproveLogin("reject");
        });

        binding.btnYes.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            requestApproveLogin("approved");
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityTwoFactorAuthBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvMainTitle, "you_trying_to_signin");
        map.put(binding.tvDeviceNameTitle, "device");
        map.put(binding.tvLocationTitle, "location");
        map.put(binding.idTimeTitle, "time");

        binding.btnYes.setTxtTitle(getValue("yes"));
        binding.btnNo.setTxtTitle(getValue("no_its_not_me"));

        return map;
    }

    // endregion
    // --------------------------------------
    // region private
    // --------------------------------------

    private void requestApproveLogin(String status) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status",status);
        jsonObject.addProperty("reqId",reqId);
        showProgress();
        DataService.shared(this).requestApprovedLogin(jsonObject, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                }
                onBackPressed();
            }
        });

    }
}