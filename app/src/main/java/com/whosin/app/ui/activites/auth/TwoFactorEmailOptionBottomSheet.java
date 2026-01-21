package com.whosin.app.ui.activites.auth;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.IPInfoTask;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentOtpVerificationBottomSheetBinding;
import com.whosin.app.databinding.FragmentTwoFactorEmailOptionBottomSheetBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.ChatManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.UserTokenModel;
import com.whosin.app.service.rest.RestCallback;

public class TwoFactorEmailOptionBottomSheet extends BottomSheetDialogFragment {

    private FragmentTwoFactorEmailOptionBottomSheetBinding binding;
    public CommanCallback<Boolean> callback;

    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.OtpDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTwoFactorEmailOptionBottomSheetBinding.inflate(inflater, container, false);
        initUi();
        setupListener();
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

        binding.subTitle.setText(Utils.getLangValue("enter_email_varifation_recive"));
        binding.editTextEmail.setHint(Utils.getLangValue("enter_your_email"));
        binding.submitBtn.setTxtTitle(Utils.getLangValue("submit"));
    }

    private void setupListener() {
        binding.ivClose.setOnClickListener(v -> dismiss());
        binding.submitBtn.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            requestTwoAuthWithEmail();
        });
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void requestTwoAuthWithEmail() {
        String email = binding.editTextEmail.getText().toString();
        if (!Utils.isValidEmail(email)) {
            Toast.makeText( getContext(), Utils.getLangValue("invalid_email"), Toast.LENGTH_SHORT ).show();
            return;
        }

        new IPInfoTask(data -> {
            JsonObject params = new JsonObject();
            params.addProperty( "email", email );
            params.addProperty( "userId", SessionManager.shared.getUser().getId() );
            JsonObject metaData = new JsonObject();
            metaData.addProperty("device_id", Utils.getDeviceUniqueId(getContext()));
            metaData.addProperty("device_name", Build.DEVICE);
            metaData.addProperty("device_model", Build.MODEL);
            metaData.addProperty("device_location", data);
            params.add("metadata", metaData);

            binding.submitBtn.setProgressColor(getResources().getColor(R.color.white));
            binding.submitBtn.startProgress();
            DataService.shared(getActivity()).requestTwoAuthEmail(params, new RestCallback<ContainerModel<UserDetailModel>>(this) {
                @Override
                public void result(ContainerModel<UserDetailModel> model, String error) {
                    binding.submitBtn.stopProgress();
                    if (!Utils.isNullOrEmpty( error ) || model == null) {
                        Toast.makeText( getActivity(), error, Toast.LENGTH_SHORT ).show();
                        return;
                    }
                    AppSettingManager.shared.authRequestId = model.getData().getId();
                    if (callback != null) {
                        callback.onReceive(true);
                    }
                    dismiss();
                }
            });

        }).execute();


    }
}