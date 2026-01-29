package com.whosin.business.ui.activites.setting;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;

import com.tapadoo.alerter.Alerter;
import com.whosin.business.R;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.databinding.ActivitySettingBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.AppSettingManager;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.manager.TranslationManager;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.TravelDeskModels.TravelDeskPickUpListModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.Profile.BlockUserListActivity;
import com.whosin.business.ui.activites.travelDeskTicket.AddOtherLocationDialog;
import com.whosin.business.ui.commonBottomSheets.ChangeCurrencyDialog;
import com.whosin.business.ui.activites.home.MainHomeActivity;
import com.whosin.business.ui.activites.reportedUser.ReportedUserListActivity;
import com.whosin.business.ui.activites.Profile.UpdateProfileActivity;
import com.whosin.business.ui.activites.Profile.UpdateSelectPreferenceActivity;
import com.whosin.business.ui.activites.comman.BaseActivity;
import com.whosin.business.ui.commonBottomSheets.SelectCurrencyBottomSheet;
import com.whosin.business.ui.commonBottomSheets.SelectLanguageBottomSheet;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends BaseActivity {

    private ActivitySettingBinding binding;

    private ChangeCurrencyDialog changeCurrencyDialog;

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private CommanCallback<Boolean> permissionCallback;


    // --------------------------------------
    // region Life Cycle
    // --------------------------------------
    @Override
    protected void initUi() {


        applyTranslations();

        if (Utils.isGuestLogin()){
            Utils.hideViews(binding.ilUpdateProfile,binding.ilBlockUser,binding.ilReportUser,
                    binding.ilUserReviewList,binding.ilAuth,binding.ilAccountType,
                    binding.viewLine1,binding.viewLine2,binding.viewLine3,binding.viewAuth,
                    binding.viewPrivate,binding.viewLineChangeCurreny);
        }

        Utils.hideViews(binding.ilUpdateLang,binding.langViewLine);

        binding.tvCurrentUserCurrency.setText(SessionManager.shared.getUser().getCurrency());
        binding.markupAmount.setText(
                SessionManager.shared.getUser().getGlobelMarkup() > 0
                        ? String.valueOf(Utils.roundFloatValue(
                        SessionManager.shared.getUser().getGlobelMarkup()))
                        : ""
        );
        binding.tvCurrentUserSelectLang.setText(SessionManager.shared.getUser().getLang());

    }

    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener( view -> onBackPressed() );
        binding.ilPreference.setOnClickListener( view -> startActivity( new Intent( activity, UpdateSelectPreferenceActivity.class ) ) );
        binding.ilUpdateProfile.setOnClickListener( view -> startActivity( new Intent( activity, UpdateProfileActivity.class ) ) );

        binding.ilBlockUser.setOnClickListener( view -> startActivity( new Intent( activity, BlockUserListActivity.class ) ) );
        binding.ilReportUser.setOnClickListener( view -> startActivity( new Intent( activity, ReportedUserListActivity.class ) ) );
        binding.ilUserReviewList.setOnClickListener( view -> startActivity( new Intent( activity, MyReviewListActivity.class ) ) );

        binding.ivMarkUp.setOnClickListener(view -> {

            AddMarkUpDialog markupDialog = new AddMarkUpDialog();
            markupDialog.markup = String.valueOf(Utils.roundFloatValue(SessionManager.shared.getUser().getGlobelMarkup()));
            markupDialog.callback = value -> {
                if (TextUtils.isEmpty(value)) return;

                JsonObject json = new JsonObject();
                json.addProperty("globelMarkup", value);

                requestUpdateProfile(json, () -> {

                    // 🔥 Update local user instantly
                    SessionManager.shared.getUser().setGlobelMarkup(Float.parseFloat(value));

                    // 🔥 Update UI instantly
                    binding.markupAmount.setText(
                            SessionManager.shared.getUser().getGlobelMarkup() > 0
                                    ? String.valueOf(Utils.roundFloatValue(
                                    SessionManager.shared.getUser().getGlobelMarkup()))
                                    : ""
                    );

                    Toast.makeText(activity, "Markup Updated", Toast.LENGTH_SHORT).show();
                });
            };

            markupDialog.show(getSupportFragmentManager(), "AddMarkUpDialog");
        });


//        binding.ilContact.setOnClickListener( view -> {
//            if (hasContactPermission()){
//                Intent intent = new Intent();
//                intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
//                Uri uri = Uri.fromParts( "package", getPackageName(), null );
//                intent.setData( uri );
//                startActivity( intent );
//            }else {
//                CollectionBottomSheet dialog = new CollectionBottomSheet();
//                dialog.setListener(data -> {
//                    ContactManager.shared.context = SettingActivity.this;
//                    ContactManager.shared.requestPermission( true, data1 -> {
//                        if (data1) {
//                        }
//                    } );
//                });
//                dialog.show(getSupportFragmentManager(), "CollectionBottomSheet");
//            }
//
//        } );

//        binding.tvNotification.setOnClickListener( view -> {
//            String[] permissions = {Manifest.permission.POST_NOTIFICATIONS};
//            String rationale = "Please allow the notification permission to get updated";
//            requestPermission( permissions, rationale, data -> {
//
//            } );
//
//        } );

        binding.tvNotification.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                String[] permissions = {Manifest.permission.POST_NOTIFICATIONS};
                String rationale = "Please allow the notification permission to get updates";

                requestPermission(this, permissions, rationale, granted -> {
                    if (granted) {
                        Log.d("TAG", "Notification Permission Granted");
                    } else {
                        Log.d("TAG", "Notification Permission Denied or Blocked");
                    }
                });
            } else {
                Log.d("TAG", "Notification permission not required on this Android version");

            }
        });


//        binding.switchContact.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (buttonView.isPressed()) {
//                    if (isChecked) {
//                        CollectionBottomSheet dialog = new CollectionBottomSheet();
//                        dialog.setListener(data -> {
//                            ContactManager.shared.context = SettingActivity.this;
//                            ContactManager.shared.requestPermission( true, data1 -> {
//                                if (data1) {
//                                }
//                            } );
//                        });
//                        dialog.show(getSupportFragmentManager(), "CollectionBottomSheet");
//                    } else {
//                        Intent intent = new Intent();
//                        intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
//                        Uri uri = Uri.fromParts( "package", getPackageName(), null );
//                        intent.setData( uri );
//                        startActivity( intent );
//                    }
//                }
//            }
//        } );

//        binding.switchNotification.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (buttonView.isPressed()) {
//                    if (isChecked) {
//                        String[] permissions = {Manifest.permission.POST_NOTIFICATIONS};
//                        String rationale = "Please allow the notification permission to get updated";
//                        requestPermission( permissions, rationale, data -> {
//
//                        } );
//                    } else {
//                        Intent intent = new Intent();
//                        intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
//                        Uri uri = Uri.fromParts( "package", getPackageName(), null );
//                        intent.setData( uri );
//                        startActivity( intent );
//                    }
//                }
//            }
//        } );
        binding.switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        String[] permissions = {Manifest.permission.POST_NOTIFICATIONS};
                        String rationale = "Please allow the notification permission to get updated";

                        requestPermission(this, permissions, rationale, granted -> {
                            if (granted) {
                                Log.d("TAG", "Notification Permission Granted");

                            } else {
                                Log.d("TAG", "Notification Permission Denied/Blocked");
                                binding.switchNotification.setChecked(false);
                            }
                        });
                    } else {
                        Log.d("TAG", "Notification permission not required (below Android 13)");
                    }
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        });


        binding.switchPrivate.setChecked(SessionManager.shared.getUser().isProfilePrivate());
        binding.switchPrivate.setOnCheckedChangeListener( (buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                if (isChecked) {
                    showSwitchToPrivateAlert();
                } else {
                    showSwitchToPublicAlert();

                }
            }

        } );

        binding.switchAuth.setChecked(SessionManager.shared.getUser().isTwoFactorActive());

        binding.switchAuth.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (compoundButton.isPressed()) {
                if (isChecked) {
                    if (SessionManager.shared.getUser().getIsEmailVerified() == 1) {
                        showSwitchToTwoFactorAuthentication();
                        binding.switchAuth.setChecked(true);
                    } else {
                        showEmailNotVerifiedAlert();
                        binding.switchAuth.setChecked(false);

                    }
                } else {
                    showSwitchToDisableAlert();
                }
            }
        });

        binding.ilChangeCurrency.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            showChangeCurrencySheet();
        });

        binding.ilUpdateLang.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            showChangeLagSheet();
        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivitySettingBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean contactPermission = hasContactPermission( Manifest.permission.READ_CONTACTS );
        boolean notificationPermission = hasContactPermission( Manifest.permission.POST_NOTIFICATIONS );
//        binding.switchContact.setChecked( contactPermission ? true : false );
        binding.switchNotification.setChecked( notificationPermission ? true : false );



        // binding.ilContact.setVisibility(contactPermission ? View.GONE : View.VISIBLE);
        //  binding.ilNotification.setVisibility(notificationPermission ? View.GONE : View.VISIBLE);

        // binding.roundLinearPermission.setVisibility((contactPermission && notificationPermission) ? View.GONE : View.VISIBLE);
        //  binding.view.setVisibility((!contactPermission && !notificationPermission) ? View.VISIBLE : View.GONE);
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvSettingTitle, "settings");
        map.put(binding.tvSubTitle, "account_settings_info");
        map.put(binding.tvUpdateYourProfile, "update_your_profile");
        map.put(binding.tvBlockUserTitle, "blocked_users_list");
        map.put(binding.reportedUserListTitle, "reported_users_list");
        map.put(binding.tvMyReviewsTitle, "my_reviews");
        map.put(binding.tvChangeCurrencyTitle, "change_currency");
        map.put(binding.tvChangeLangTitle, "change_language");
        map.put(binding.tvTwoFactorTitle, "two_factor_authentication");
        map.put(binding.tvAccount, "private_account");
        map.put(binding.tvNotification, "allow_notification");
        return map;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (permissionCallback != null) {
                permissionCallback.onReceive(allGranted);

                if (!allGranted) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED &&
                                !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            break;
                        }
                    }
                }
            }
        }
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void requestPermission(Activity activity, String[] permissions, String rationale, CommanCallback<Boolean> callback) {
        this.permissionCallback = callback;

        // Check already granted
        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (allGranted) {
            // Already granted
            callback.onReceive(true);
        } else {
            // Need to request
            ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean hasContactPermission(String permission) {
        return ContextCompat.checkSelfPermission( activity, permission ) == PackageManager.PERMISSION_GRANTED;
    }

    private void showSwitchToPublicAlert() {
        Graphics.showAlertDialogWithOkCancel( activity, getValue("switch_to_public_account"),
                getValue("switch_to_public_account_confirmation"),
                getValue("yes"), getValue("cancel"), isConfirmed -> {
                    if (isConfirmed) {
                        requestUserUpdateSetting( false ,false,"account");
                    } else {
                        binding.switchPrivate.setChecked( true );
                    }
                } );
    }

    private void showSwitchToPrivateAlert() {
        Graphics.showAlertDialogWithOkCancel( activity, getValue("switch_to_private_account"),
                getValue("switch_to_private_account_confirmation"),
                getValue("yes"), getValue("cancel"), isConfirmed -> {
                    if (isConfirmed) {
                        requestUserUpdateSetting( true ,false,"account");
                    } else {
                        binding.switchPrivate.setChecked( false );
                    }
                } );
    }

    private void showSwitchToTwoFactorAuthentication() {
        Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name),  getValue("enable_two_factor_auth_confirmation"),
                getValue("yes"), getValue("cancel"), isConfirmed -> {
                    if (isConfirmed) {
                        requestUserUpdateSetting(false, true, "auth");
                    } else {
                        binding.switchAuth.setChecked(false);
                    }
                });
    }

    private void showSwitchToDisableAlert() {
        if (SessionManager.shared.getUser().getIsEmailVerified() == 1) {
            Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name),
                    getValue("disable_two_factor_auth_confirmation"),
                    getValue("yes"), getValue("cancel"), isConfirmed -> {
                        if (isConfirmed) {
                            requestUserUpdateSetting(false, false, "auth");
                        } else {
                            binding.switchAuth.setChecked(true);
                        }
                    });
        } else {
            showEmailNotVerifiedAlert();
        }
    }

    private void showEmailNotVerifiedAlert() {
        Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name),
                getValue("email_not_verified"), getValue("ok"), getValue("cancel"),
                isConfirmed -> {
                    if (!isConfirmed) {
                        binding.switchAuth.setChecked(false);
                    } else {
                        startActivity(new Intent(activity, UpdateProfileActivity.class));
                    }
                });
    }


    private void showChangeCurrencySheet() {
        SelectCurrencyBottomSheet currencyBottomSheet = new SelectCurrencyBottomSheet();
        currencyBottomSheet.callback = data -> {
            if (!TextUtils.isEmpty(data)) {
                changeCurrencyDialog = new ChangeCurrencyDialog();
                changeCurrencyDialog.show(getSupportFragmentManager(), "");
                requestChangeCurrency(data,false);
            }
        };
        currencyBottomSheet.show(getSupportFragmentManager(), "");
    }

    private void showChangeLagSheet() {
        SelectLanguageBottomSheet languageBottomSheet = new SelectLanguageBottomSheet();
        languageBottomSheet.callback = data -> {
            if (!TextUtils.isEmpty(data)) {
                changeCurrencyDialog = new ChangeCurrencyDialog();
                changeCurrencyDialog.isUpdateLang = true;
                changeCurrencyDialog.show(getSupportFragmentManager(), "");
                requestChangeCurrency(data,true);
            }
        };
        languageBottomSheet.show(getSupportFragmentManager(), "");
    }

    private void requestUpdateProfile(JsonObject jsonObject, Runnable onSuccess) {

        showProgress();

        SessionManager.shared.updateProfile(activity, jsonObject, (success, error) -> {
            hideProgress();

            if (!Utils.isNullOrEmpty(error)) {
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                return;
            }

            if (onSuccess != null) {
                onSuccess.run();
            }
        });
    }

    private void requestChangeCurrency(String code,boolean isUpdateLang) {
        AppSettingManager.shared.callHomeCommanApi = false;
        JsonObject jsonObject = new JsonObject();
        if (isUpdateLang){
            jsonObject.addProperty("lang", code);
        }else {
            jsonObject.addProperty("currency", code);
        }
        SessionManager.shared.updateProfile(activity, jsonObject, (success, error) -> {
            if (changeCurrencyDialog != null) {
                changeCurrencyDialog.dismiss();
            }
            if (!Utils.isNullOrEmpty(error)) {
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                return;
            }
            if (changeCurrencyDialog != null) {
                changeCurrencyDialog.dismiss();
            }
            if (isUpdateLang){TranslationManager.shared.changeLang();}
            Intent intent = new Intent(activity, MainHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestUserUpdateSetting(boolean isProfilePrivate,boolean isTwoAuthString ,String type) {
        JsonObject object = new JsonObject();
        if(type.equals( "account" )){
            object.addProperty( "isProfilePrivate", isProfilePrivate );

        }else {
            object.addProperty( "isTwoFactorActive", isTwoAuthString );

        }
        showProgress();
        DataService.shared( activity ).requestUserUpdateSettings( object, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                assert model.getData() != null;
                SessionManager.shared.saveUserData( model.getData() );
                Alerter.create( activity ).setTitle(getValue("thank_you")).setText( model.message).setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
            }
        } );
    }

}