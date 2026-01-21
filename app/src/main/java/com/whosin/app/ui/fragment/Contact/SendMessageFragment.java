package com.whosin.app.ui.fragment.Contact;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.FragmentSendMessageBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.ChangePhoneNumberActivity;
import com.whosin.app.ui.activites.home.ContactUsActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.util.HashMap;
import java.util.Map;


public class SendMessageFragment extends BaseFragment {

    private FragmentSendMessageBinding binding;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @SuppressLint("SetTextI18n")
    @Override
    public void initUi(View view) {
        binding = FragmentSendMessageBinding.bind(view);


        applyTranslations();
        binding.btnSubmit.setTxtTitle(getValue("submit"));

        if (SessionManager.shared.getUser().getEmail() != null && !SessionManager.shared.getUser().getEmail().isEmpty()) {
            binding.etEmail.setText(SessionManager.shared.getUser().getEmail());
        }

        String heyMessage = getValue("hey") + " ";
        String shareYouThoughts = " " + getValue("share_your_thoughts");
        binding.tvThought.setText(heyMessage + SessionManager.shared.getUser().getFirstName() + shareYouThoughts);

        binding.btnSubmit.setProgressColor(ContextCompat.getColor(context, R.color.white));

        if (SessionManager.shared.getUser().getIsEmailVerified() == 1) {
            binding.editEmail.setVisibility(View.VISIBLE);
            binding.emailVerify.setVisibility(View.GONE);
            binding.etEmail.setClickable(false);
            binding.etEmail.setFocusable(false);
        } else {
            binding.editEmail.setVisibility(View.GONE);
            binding.emailVerify.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void setListeners() {
        binding.btnSubmit.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            checkEmptyData();
        });

        binding.emailVerify.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ChangePhoneNumberActivity.class);
            intent.putExtra("email", binding.etEmail.getText().toString());
            intent.putExtra("isEmailVerify", true);
            activityLauncher.launch(intent, result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    boolean isVerify = result.getData().getBooleanExtra("verify", false);
                    if (isVerify) {
                        binding.emailVerify.setVisibility(View.GONE);
                        binding.editEmail.setVisibility(View.VISIBLE);
                        SessionManager.shared.getCurrentUserProfile(requireActivity(), (success, error) -> {
                            if (!Utils.isNullOrEmpty(error)) {
                                Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (SessionManager.shared.getUser().getEmail() != null  && !SessionManager.shared.getUser().getEmail().isEmpty()) {
                                binding.etEmail.setText(SessionManager.shared.getUser().getEmail());
                            }

                        });
                    }
                }
            });

        });

        binding.editEmail.setOnClickListener(view -> {

            Intent intent = new Intent(requireActivity(), ChangePhoneNumberActivity.class);
            intent.putExtra("email", binding.etEmail.getText().toString());
            intent.putExtra("isEmailVerify", true);
            activityLauncher.launch(intent, result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    boolean isVerify = result.getData().getBooleanExtra("verify", false);
                    if (isVerify) {
                        binding.emailVerify.setVisibility(View.GONE);
                        binding.editEmail.setVisibility(View.VISIBLE);
                        SessionManager.shared.getCurrentUserProfile(requireActivity(), (success, error) -> {
                            if (!Utils.isNullOrEmpty(error)) {
                                Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (SessionManager.shared.getUser().getEmail() != null  && !SessionManager.shared.getUser().getEmail().isEmpty()) {
                                binding.etEmail.setText(SessionManager.shared.getUser().getEmail());
                            }
                        });
                    }
                }
            });
        });

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_send_message;
    }


    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.etEmail, "email");
        map.put(binding.etSubject, "subject");
        map.put(binding.edtMessage, "message");
        map.put(binding.emailVerify, "write_message_placeholder");
        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void checkEmptyData() {

        if (TextUtils.isEmpty(binding.etEmail.getText().toString())) {
            Toast.makeText(context, getValue("error_email_required"), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Utils.isValidEmail(binding.etEmail.getText().toString())) {
            Toast.makeText(context, getValue("invalid_email"), Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(binding.etSubject.getText().toString())) {
            Toast.makeText(context, getValue("enter_subject"), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(binding.edtMessage.getText().toString())) {
            Toast.makeText(context, getValue("enter_message"), Toast.LENGTH_SHORT).show();
            return;
        }

        requestContactAddDetail();


    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestContactAddDetail() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("image", SessionManager.shared.getUser().getImage());
        jsonObject.addProperty("name", SessionManager.shared.getUser().getFullName());
        jsonObject.addProperty("email", binding.etEmail.getText().toString());
        jsonObject.addProperty("phone", SessionManager.shared.getUser().getPhone());
        jsonObject.addProperty("subject", binding.etSubject.getText().toString());
        jsonObject.addProperty("message", binding.edtMessage.getText().toString());
        binding.btnSubmit.startProgress();
        DataService.shared(requireActivity()).requestContactAddQuery(jsonObject, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                binding.btnSubmit.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(context, model.message, Toast.LENGTH_SHORT).show();
                ((ContactUsActivity) requireActivity()).switchToInboxFragment();

                binding.edtMessage.setText("");
                binding.etSubject.setText("");

            }
        });
    }


    // endregion
    // --------------------------------------

}