package com.whosin.app.ui.activites.Profile;

import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityChangePhoneNumberBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class ChangePhoneNumberActivity extends BaseActivity {

    private ActivityChangePhoneNumberBinding binding;
    private String phone, countryCode;
    private boolean isEmailVerify;


    // --------------------------------------
    // region Life Cycle
    // --------------------------------------


    @Override
    protected void initUi() {

        applyTranslations();

        isEmailVerify =  getIntent().getBooleanExtra("isEmailVerify",false);

        if (!isEmailVerify){
            String phone = getIntent().getStringExtra( "phone" );
            String code = getIntent().getStringExtra( "country_code" );

            binding.editTextPhone.setText( phone );
            if (code != null && !code.isEmpty()) {
                try {
                    int countryCode = Integer.parseInt(code);
                    binding.countryCode.setCountryForPhoneCode(countryCode);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    binding.countryCode.setAutoDetectedCountry(true);
                }
            } else {
                binding.countryCode.setAutoDetectedCountry(true);
            }
        }else {
            binding.editTextPhone.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            binding.editTextPhone.setHint(setValue("enter_your_email_address"));
            binding.countryCode.setVisibility(View.GONE);
            binding.nameControl.setText(getValue("want_to_change_your_email_address"));
            binding.subTitle.setText(getValue("please_enter_your_new_email_address"));
            String email = getIntent().getStringExtra( "email" );
            if (!email.isEmpty() && email !=null){
                binding.editTextPhone.setText(email);
            }else {
                binding.editTextPhone.setText("");
            }
        }
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener( view -> onBackPressed() );

        binding.imageNext.setOnClickListener(view -> {
            if (!isEmailVerify) {
                requestChangePhone();
            } else {
                sendOtpInEmail();
            }
        });



    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityChangePhoneNumberBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.nameControl, "change_your_mobile_number");
        map.put(binding.subTitle, "please_enter_phone");
        map.put(binding.editTextPhone, "please_enter_phone");
        map.put(binding.btnSubmit, "submit");
        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Services
    // --------------------------------------

    private void requestChangePhone() {
        phone = binding.editTextPhone.getText().toString();
        countryCode = binding.countryCode.getSelectedCountryCode();


        if(Utils.isNullOrEmpty( phone )){
            Toast.makeText( activity, getValue("please_enter_phone"), Toast.LENGTH_SHORT ).show();
            return;
        }
        String region = binding.countryCode.getSelectedCountryNameCode();
        if(!Utils.isValidPhoneNumber( countryCode, phone, region )){
            Toast.makeText( activity, getValue("invalid_phone"), Toast.LENGTH_SHORT ).show();
            return;
        }

        showProgress();
        JsonObject object = new JsonObject();
        object.addProperty("phone", phone);
        object.addProperty("country_code", countryCode);
        DataService.shared( activity ).requestSendOtpNewPhone( object, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Toast.makeText( activity, model.message, Toast.LENGTH_SHORT ).show();
                PhoneVerifiedBottomSheet bottomSheet = new PhoneVerifiedBottomSheet();
                bottomSheet.callback = data -> {
                    if (data){
                        finish();
                    }
                };
                bottomSheet.phone = phone;
                bottomSheet.countryCode = countryCode;
                bottomSheet.show( getSupportFragmentManager(), "1" );
                bottomSheet.isNewPhone = true;
                bottomSheet.type = "phone";
            }
        } );
    }


    private void requestSentOtp(String type) {
        JsonObject object = new JsonObject();
        object.addProperty( "type", type );
        showProgress();
        DataService.shared( activity ).requestSentOtp( object, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error )) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Toast.makeText( activity, model.message, Toast.LENGTH_SHORT ).show();
            }

        } );
    }


    private void sendOtpInEmail() {

        phone = binding.editTextPhone.getText().toString();

        if(Utils.isNullOrEmpty( phone )){
            Toast.makeText( activity, getValue("please_enter_email_address"), Toast.LENGTH_SHORT ).show();
            return;
        }

        if (!Utils.isValidEmail(phone)) {
            Toast.makeText( this, getValue("invalid_email"), Toast.LENGTH_SHORT ).show();
            return;
        }


        showProgress();
        JsonObject object = new JsonObject();
        object.addProperty("type", "email");
        object.addProperty("userId", SessionManager.shared.getUser().getId());
        object.addProperty("email", phone);
        DataService.shared( activity ).requestSendOtpNewPhone( object, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }

//                requestSentOtp("email");

                PhoneVerifiedBottomSheet bottomSheet = new PhoneVerifiedBottomSheet();
                bottomSheet.userId = SessionManager.shared.getUser().getId();
                bottomSheet.callback = data -> {
                    if (data) {
                        Intent intent = new Intent();
                        intent.putExtra("verify",true);
                        setResult(RESULT_OK, intent);
                        finish();

                    }
                };
                bottomSheet.isNewPhone = false;
                bottomSheet.type = "email";
                bottomSheet.show(getSupportFragmentManager(), "1");
            }
        } );


    }



    // endregion
    // --------------------------------------
}