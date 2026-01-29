package com.whosin.business.ui.activites.setting;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.whosin.business.R;
import com.whosin.business.comman.Utils;
import com.whosin.business.databinding.ActivityBankDetailsBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.bankDetails.UserBankDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.comman.BaseActivity;

public class BankDetailsActivity extends BaseActivity {

    private ActivityBankDetailsBinding binding;
    private boolean isEditing = false;
    private String originalIban = "";
    private UserBankDetailModel userBankDetailModel;

    @Override
    protected void initUi() {
        setEditingEnabled(false);
        getBankDetails();
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> onBackPressed());
        binding.tvEdit.setOnClickListener(view -> toggleEditing());
    }

    private String maskIban(String iban) {
        if (iban == null || iban.length() < 6) return iban;

        String start = iban.substring(0, 4);
        String end = iban.substring(iban.length() - 4);

        return start + " •••• •••• •••• " + end;
    }

    private void toggleEditing() {
        if (isEditing) {
            if (isValidInput()) {
                updateBankDetails();
            }
        } else {
            isEditing = true;
            if (!TextUtils.isEmpty(originalIban)) {
                binding.etIBAN.setText(originalIban);
            }
            binding.etIBAN.setSelection(binding.etIBAN.getText().length());
            setEditingEnabled(true);
            binding.tvEdit.setText("Save");
        }
    }

    private boolean isValidInput() {
        if (TextUtils.isEmpty(binding.etAccountHolderName.getText().toString().trim())) {
            Toast.makeText(activity, "Please enter account holder name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(binding.etAccountNumber.getText().toString().trim())) {
            Toast.makeText(activity, "Please enter account number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(binding.etBankName.getText().toString().trim())) {
            Toast.makeText(activity, "Please enter bank name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getBankDetails() {
        showProgress();
        JsonObject jsonObject = new JsonObject();
        DataService.shared(activity).requestGetBankDetails(jsonObject, new RestCallback<ContainerModel<UserBankDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserBankDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error)) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model != null && model.getData() != null) {
                    userBankDetailModel = model.getData();
                    populateUi(userBankDetailModel);
                }
            }
        });
    }

    private void populateUi(UserBankDetailModel model) {
        if (model == null) return;

        binding.etAccountHolderName.setText(model.getHolderName());

        if (model.getBankDetails() != null) {
            binding.etAccountNumber.setText(model.getBankDetails().getAccountNumber());
            binding.etBankName.setText(model.getBankDetails().getBankName());
            originalIban = model.getBankDetails().getIban();
            if (originalIban == null) originalIban = "";
            binding.etIBAN.setText(maskIban(originalIban));
            binding.etSwift.setText(model.getBankDetails().getSwiftCode());
            binding.etBranch.setText(model.getBankDetails().getBsb());
        }
    }

    private void updateBankDetails() {
        showProgress();

        JsonObject bankDetails = new JsonObject();
        bankDetails.addProperty("bankName", binding.etBankName.getText().toString().trim());
        bankDetails.addProperty("accountNumber", binding.etAccountNumber.getText().toString().trim());
        bankDetails.addProperty("swiftCode", binding.etSwift.getText().toString().trim());

        String iban = binding.etIBAN.getText().toString().trim();
        bankDetails.addProperty("iban", iban);

        if (userBankDetailModel != null && userBankDetailModel.getBankDetails() != null) {
            addPropertyIfNotNull(bankDetails, "ifsc", userBankDetailModel.getBankDetails().getIfsc());
            addPropertyIfNotNull(bankDetails, "routingNumber", userBankDetailModel.getBankDetails().getRoutingNumber());
            addPropertyIfNotNull(bankDetails, "sortCode", userBankDetailModel.getBankDetails().getSortCode());
        } else {
            bankDetails.addProperty("ifsc", "");
            bankDetails.addProperty("routingNumber", "");
            bankDetails.addProperty("sortCode", "");
        }

        JsonObject root = new JsonObject();
        root.addProperty("holderName", binding.etAccountHolderName.getText().toString().trim());

        if (userBankDetailModel != null) {
            root.addProperty("holderType", userBankDetailModel.getHolderType());
            root.addProperty("country", userBankDetailModel.getCountry());
            root.addProperty("currency", userBankDetailModel.getCurrency());
        } else {
            root.addProperty("holderType", "INDIVIDUAL");
            root.addProperty("country", "IN");
            root.addProperty("currency", "INR");
        }

        root.add("bankDetails", bankDetails);

        DataService.shared(activity).requestUpdateBankDetails(root, new RestCallback<ContainerModel<UserBankDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserBankDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error)) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model != null && model.getData() != null) {
                    userBankDetailModel = model.getData();
                    populateUi(userBankDetailModel);

                    isEditing = false;
                    setEditingEnabled(false);
                    binding.tvEdit.setText("Edit");
                    Toast.makeText(activity, "Bank details updated successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addPropertyIfNotNull(JsonObject json, String key, String value) {
        if (value != null) {
            json.addProperty(key, value);
        } else {
            json.addProperty(key, "");
        }
    }

    private void setEditingEnabled(boolean enabled) {
        setEditTextState(binding.etAccountHolderName, enabled);
        setEditTextState(binding.etAccountNumber, enabled);
        setEditTextState(binding.etBankName, enabled);
        setEditTextState(binding.etBranch, enabled);
        setEditTextState(binding.etIBAN, enabled);
        setEditTextState(binding.etSwift, enabled);
    }

    private void setEditTextState(EditText editText, boolean enabled) {
        editText.setEnabled(enabled);
        editText.setFocusable(enabled);
        editText.setFocusableInTouchMode(enabled);
        editText.setClickable(enabled);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_bank_details;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityBankDetailsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}
