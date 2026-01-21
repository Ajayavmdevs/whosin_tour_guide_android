package com.whosin.app.ui.activites.raynaTicket;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.Settings;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rtchagas.pingplacepicker.PingPlacePicker;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.CountryCode;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.DubaiRegionChecker;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityParticipantDetailBinding;
import com.whosin.app.databinding.ItemParticipatePrimaryDetailBinding;
import com.whosin.app.service.manager.LogManager;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskOptionDataModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskPickUpListModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.rayna.RaynaPassengerModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.models.rayna.TourOptionsModel;
import com.whosin.app.service.models.whosinTicketModel.WhosinTicketTourOptionModel;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.SelectPaxTypeBottomSheet;
import com.whosin.app.ui.activites.travelDeskTicket.TravelDeskPickUpListSheet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RaynaParticipantDetailActivity extends BaseActivity {

    private ActivityParticipantDetailBinding binding;

    private final TourOptionsDetailsAdapter<TourOptionsModel> tourOptionsDetailsAdapter = new TourOptionsDetailsAdapter<>();

    private final WhosinCustomTourOptionsDetailsAdapter<WhosinTicketTourOptionModel> whosinCustomTicketDetailAdapter = new WhosinCustomTourOptionsDetailsAdapter<>();

    private final TravelDeskTourOptionsDetailsAdapter<TravelDeskOptionDataModel> travelDeskTourOptionsDetailsAdapter = new TravelDeskTourOptionsDetailsAdapter<>();

    private final ArrayList<String> prefixList = new ArrayList<>(Arrays.asList("Mr.", "Ms.", "Mrs."));

    private String countryCode, region, primaryPrefix, primaryPaxType, nationality = "";

    private int selectedAdapterPosition = -1;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private RaynaTicketDetailModel raynaTicketDetailModel = RaynaTicketManager.shared.raynaTicketDetailModel;



    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {


        applyTranslations();

        RaynaTicketManager.shared.activityList.add(activity);

        setPrimaryData();


        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_item, prefixList);
        spinnerAdapter.setDropDownViewResource(R.layout.rooms_spinner_drop_down_item);
        binding.selectPrefix.setAdapter(spinnerAdapter);

        int prefixPosition = prefixList.indexOf(primaryPrefix);
        if (prefixPosition == -1 ) primaryPrefix = prefixList.get(0);
        binding.selectPrefix.setSelection(prefixPosition != -1 ? prefixPosition : 0);

        int adults = getIntOrDefault(AppConstants.ADULTS);
        int child = getIntOrDefault(AppConstants.CHILD);
        int infants = getIntOrDefault(AppConstants.INFANT);

        if (RaynaTicketManager.shared.tourOptionsModel != null && RaynaTicketManager.shared.tourOptionsModel.getOptionDetail().getIsWithoutAdult() && getIntOrDefault(AppConstants.ADULTS) == 0) {
            primaryPaxType = "Child";
            if (getIntOrDefault(AppConstants.CHILD) != 0) child = child - 1;
        } else {
            primaryPaxType = "Adult";
            if (getIntOrDefault(AppConstants.ADULTS) != 0) adults = adults - 1;

        }

        handlePaxType();


        binding.guestDetailRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        if (!RaynaTicketManager.shared.selectedTourModel.isEmpty()) {
            binding.guestDetailRecycler.setAdapter(tourOptionsDetailsAdapter);
            List<TourOptionsModel> filteredList = new ArrayList<>();
            for (TourOptionsModel model : RaynaTicketManager.shared.selectedTourModel) {
                String transType = model.getTransType();
//                if ("Private Transfers".equals(transType) || "Sharing Transfers".equals(transType)) {
//                    filteredList.add(model);
//                }
                if ((model.getSelectedTransferId() == 41844) || (model.getSelectedTransferId() == 41843)) {
                    filteredList.add(model);
                }
            }
            if (!filteredList.isEmpty()) tourOptionsDetailsAdapter.updateData(filteredList);
        } else if (!RaynaTicketManager.shared.selectTravelDeskOptionDataModels.isEmpty()) {
            binding.guestDetailRecycler.setAdapter(travelDeskTourOptionsDetailsAdapter);
            List<TravelDeskOptionDataModel> filteredListForTravelDesk = new ArrayList<>();
            for (TravelDeskOptionDataModel model : RaynaTicketManager.shared.selectTravelDeskOptionDataModels) {
                if (!model.isDirectReporting()) {
                    filteredListForTravelDesk.add(model);
                }
            }
            if (!filteredListForTravelDesk.isEmpty()) travelDeskTourOptionsDetailsAdapter.updateData(filteredListForTravelDesk);

        } else if (!RaynaTicketManager.shared.selectedTourModelForWhosin.isEmpty()) {
            binding.guestDetailRecycler.setAdapter(whosinCustomTicketDetailAdapter);
            whosinCustomTicketDetailAdapter.updateData(RaynaTicketManager.shared.selectedTourModelForWhosin.stream().filter(WhosinTicketTourOptionModel::getPickup).collect(Collectors.toList()));
        }

        EditText[] editTexts = {binding.editFirstName, binding.editLastName, binding.editEmail, binding.editPhoneNumber};

        for (EditText editText : editTexts) {
            editText.addTextChangedListener(getWatcher(editText));
        }


        updateButtonColor();

    }

    @Override
    protected void setListeners() {


        binding.constraintHeader.ivClose.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.nextButton.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            validateMembers();
        });

        binding.countryCode.setOnCountryChangeListener(() -> {
            region = binding.countryCode.getSelectedCountryNameCode();
            countryCode = binding.countryCode.getSelectedCountryCode();
        });

        binding.countryCodeForNationalty.setOnCountryChangeListener(() -> {
            nationality = binding.countryCodeForNationalty.getSelectedCountryName();
            updateButtonColor();
        });


        binding.inputContainer.setOnClickListener(v -> binding.countryCode.launchCountrySelectionDialog());

        binding.passengerNationalityLayout.setOnClickListener(v -> binding.countryCodeForNationalty.launchCountrySelectionDialog());

        binding.selectPrefix.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                primaryPrefix = prefixList.get(position);
                updateButtonColor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        binding.tvPaxTypeHint.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            SelectPaxTypeBottomSheet bottomSheet = new SelectPaxTypeBottomSheet();
            if (!TextUtils.isEmpty(primaryPaxType)) bottomSheet.selectedPaxType = primaryPaxType;
            bottomSheet.callback = data -> {
                if (!TextUtils.isEmpty(data)) {
                    primaryPaxType = data;
                    handlePaxType();
                    updateButtonColor();
                }
            };
            bottomSheet.show(getSupportFragmentManager(), "");
        });

        binding.editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.editPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validPhoneNumber();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.editFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               String tmpStr = s.toString();
               if (TextUtils.isEmpty(tmpStr)){
                   setEditTextStrokeColor(binding.editFirstName, R.color.red);
                   binding.editFirstName.setError(getValue("enter_firstname"));
               }else {
                   setEditTextStrokeColor(binding.editFirstName, R.color.tour_option);
                   binding.editFirstName.setError(null);
               }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.editLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tmpStr = s.toString();
                if (TextUtils.isEmpty(tmpStr)){
                    setEditTextStrokeColor(binding.editLastName, R.color.red);
                    binding.editLastName.setError(getValue("enter_lastname"));
                }else {
                    setEditTextStrokeColor(binding.editLastName, R.color.tour_option);
                    binding.editLastName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityParticipantDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RaynaTicketManager.shared.activityList.remove(activity);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openLocationPicker();
            } else {
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (!showRationale) {
                    Graphics.alertDialogYesNoBtnWithUIFlag(activity, "Permissions Required", "Location permission was permanently denied. Please enable it in app settings.", false, "Cancel", "Go to setting", aBoolean -> {
                        if (aBoolean) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });

                } else {
                    Graphics.alertDialogYesNoBtnWithUIFlag(activity, "Permission Needed", "Location permission is needed to pick your location.", false, "Cancel", "Allow", aBoolean -> {
                        if (aBoolean) {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    LOCATION_PERMISSION_REQUEST_CODE);
                        }
                    });
                }
            }
        }
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.constraintHeader.tvTitle, "guest_details");
        map.put(binding.tvHeaderTitle, "primary_guest_details");
        map.put(binding.tvNext, "next");
        map.put(binding.txtName, "name");
        map.put(binding.txtAge, "pax_type");

//        map.put(binding.txtEmail, "email_id");
//        map.put(binding.emailWarningTv, "make_sure_this_is_your_correct_email");

        String main = getValue("email_id");
        String warning = getValue("make_sure_this_is_your_correct_email");
        SpannableString spannable = new SpannableString(main + warning);

        spannable.setSpan(new TextAppearanceSpan(activity, R.style.txt11Regular), 0, main.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new TextAppearanceSpan(activity, R.style.txt7Regular), main.length(), spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.txtEmail.setText(spannable);

        map.put(binding.contactTitle, "please_enter_your_active_whatsapp_number");
        map.put(binding.nationalityTitle, "nationality");
        map.put(binding.editFirstName, "enter_firstname");
        map.put(binding.editLastName, "enter_lastname");
        map.put(binding.editEmail, "enter_email");
        map.put(binding.editPhoneNumber, "enter_phone_number");

        return map;
    }


    // endregion
    // --------------------------------------
    // region Private static classes
    // --------------------------------------

    private static class CustomSpinnerAdapter extends ArrayAdapter<String> {

        private final LayoutInflater mInflater;

        private final ArrayList<String> mItems;

        public CustomSpinnerAdapter(Context context, int resource, ArrayList<String> items) {
            super(context, resource, items);
            mItems = items;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.rooms_spinner_drop_down_item, parent, false);
            }

            TextView textView = convertView.findViewById(R.id.customTextView);
            textView.setText(mItems.get(position));
            return convertView;
        }

    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void validateEmail() {
        String email = binding.editEmail.getText().toString().trim();
        if (Utils.isValidEmail(email)) {
            setEditTextStrokeColor(binding.editEmail, R.color.tour_option);
            binding.editEmail.setError(null);
        } else {
            setEditTextStrokeColor(binding.editEmail, R.color.red);
            if (TextUtils.isEmpty(email)){
                binding.editEmail.setError(getValue("enter_email"));
            }else {
                binding.editEmail.setError(getValue("invalid_email"));

            }
        }
    }

    private void validateName(boolean isFirstName) {
        EditText target = isFirstName ? binding.editFirstName : binding.editLastName;
        String errorMsg = isFirstName ? getValue("enter_firstname") : getValue("enter_lastname");
        String input = target.getText().toString().trim();

        if (!TextUtils.isEmpty(input)) {
            setEditTextStrokeColor(target, R.color.tour_option);
            target.setError(null);
        } else {
            setEditTextStrokeColor(target, R.color.red);
            target.setError(errorMsg);
        }
    }

    private void validPhoneNumber() {
        if (TextUtils.isEmpty(binding.editPhoneNumber.getText())) {
            binding.editPhoneNumber.setError(getValue("enter_phone_number"));
            setViewStrokeColor(binding.inputContainer, R.color.red);
        } else if (!Utils.isValidPhoneNumber(countryCode, binding.editPhoneNumber.getText().toString(), region)) {
            binding.editPhoneNumber.setError(getValue("invalid_phone"));
            setViewStrokeColor(binding.inputContainer, R.color.red);
        } else {
            setViewStrokeColor(binding.inputContainer, R.color.tour_option);
            binding.editPhoneNumber.setError(null);
        }

    }

    private void setViewStrokeColor(View view, int colorResId) {
        Drawable bg = view.getBackground();
        if (bg instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) bg.mutate();
            int strokeWidth = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 1f, view.getResources().getDisplayMetrics());

            gradientDrawable.setStroke(strokeWidth, ContextCompat.getColor(view.getContext(), colorResId));
        } else {
            Log.w("Stroke", "Background is not a GradientDrawable");
        }
    }

    private void setEditTextStrokeColor(EditText editText, int colorResId) {
        GradientDrawable background = (GradientDrawable) editText.getBackground().mutate();
        int strokeWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 0.8f, editText.getResources().getDisplayMetrics());

        background.setStroke(strokeWidth, ContextCompat.getColor(editText.getContext(), colorResId));
    }

    private void handlePaxType() {
        if (!TextUtils.isEmpty(primaryPaxType)) {
            binding.tvPaxTypeHint.setText(primaryPaxType);
        } else {
            binding.tvPaxTypeHint.setText("");
        }
        binding.tvPaxTypeHint.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_drop_down_with_below_arrow, 0);
    }

    private int getIntOrDefault(String key) {
        JsonElement element = RaynaTicketManager.shared.object.get(key);
        if (element == null || element.isJsonNull() || !element.isJsonPrimitive()) {
            return 0;
        }
        return element.getAsInt();
    }

    private void validateMembers(){

        if (TextUtils.isEmpty(binding.editFirstName.getText().toString())){
            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("enter_primary_guest_first_name"));
            return;
        }
        if (TextUtils.isEmpty(binding.editLastName.getText().toString())) {
            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("enter_primary_guest_last_name"));
            return;
        }

        if (TextUtils.isEmpty(primaryPrefix)){
            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("please_select_primary_member_pax_type"));
            return;
        }

        if (TextUtils.isEmpty(primaryPaxType)) {
            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("please_select_primary_member_pax_type"));
            return;
        }

        for (TourOptionsModel p : tourOptionsDetailsAdapter.getData()) {
            if (p.getOptionDetail()!=null && !p.getOptionDetail().getIsWithoutAdult()) {
                if (!primaryPaxType.equalsIgnoreCase("Adult")) {
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("primary_guest_should_be_adult"));
                    return;
                }
            }
        }

        if (TextUtils.isEmpty(binding.editEmail.getText().toString())){
            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("enter_primary_guest_email"));
            return;
        }

        if (!Utils.isValidEmail(binding.editEmail.getText().toString())){
            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("enter_primary_guest_email"));
            return;
        }

        if (TextUtils.isEmpty(binding.editPhoneNumber.getText().toString())){
            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("enter_primary_guest_phone"));
            return;
        }

        if (!Utils.isValidPhoneNumber( countryCode,binding.editPhoneNumber.getText().toString(), region )){
            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("invalid_phone"));
            return;
        }

        if (TextUtils.isEmpty(nationality)){
            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("please_select_nationality"));
            return;
        }


        if (raynaTicketDetailModel.getBookingType().equals("travel-desk")) {
            for (TravelDeskOptionDataModel p : travelDeskTourOptionsDetailsAdapter.getData()) {
                if (p.getTravelDeskPickUpListModel() == null){
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("pickup_alert",p.getName()));
                    return;
                }
            }
        } else {
            if (raynaTicketDetailModel.getBookingType().equals("whosin-ticket")){
                for (WhosinTicketTourOptionModel p : whosinCustomTicketDetailAdapter.getData()) {
                    if (TextUtils.isEmpty(p.getPickUpLocation())) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("please_enter_pickup_address",p.getDisplayName()));
                        return;
                    }
                }
            }else {
                for (TourOptionsModel p : tourOptionsDetailsAdapter.getData()) {
                    if ((p.getSelectedTransferId() == 41844) || (p.getSelectedTransferId() == 41843)) {
//                        filteredList.add(model);
                        if (TextUtils.isEmpty(p.getPickUpLocation())) {
                            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("please_enter_pickup_address",p.getOptionDetail().getOptionName()));
                            return;
                        }
                    }
//                    if ("Private Transfers".equals(p.getTransType()) || "Sharing Transfers".equals(p.getTransType())) {
//                        if (TextUtils.isEmpty(p.getPickUpLocation())) {
//                            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("please_enter_pickup_address",p.getOptionDetail().getOptionName()));
//                            return;
//                        }
//                    }
                }
            }

        }



        JsonArray tmpArray = new JsonArray();
        RaynaPassengerModel passengerModel = new RaynaPassengerModel(
                "Tour", primaryPrefix,
                binding.editFirstName.getText().toString(), binding.editLastName.getText().toString(),
                binding.editEmail.getText().toString(),
                binding.editPhoneNumber.getText().toString(),
                nationality,
                "", "",
                1, primaryPaxType,binding.countryCode.getSelectedCountryCodeWithPlus());

        JsonObject object = new Gson().toJsonTree(passengerModel).getAsJsonObject();
        tmpArray.add(object);
        RaynaTicketManager.shared.object.add("passengers", tmpArray);

        String id = "";
        String title = "";
        Double price = 0.0;
        if (raynaTicketDetailModel != null) {
            id = raynaTicketDetailModel.getId();
            title = raynaTicketDetailModel.getTitle();
        }
        LogManager.shared.logTicketEvent(LogManager.LogEventType.addUserInfo, id, title, null, null, "AED");

        activity.startActivity(new Intent(activity, RaynaTicketCheckOutActivity.class));


    }

    private void setPrimaryData() {
        UserDetailModel userDetailModel = SessionManager.shared.getUser();


        if (TextUtils.isEmpty(userDetailModel.getFirstName())){
            validateName(true);
        }else {
            binding.editFirstName.setText(userDetailModel.getFirstName());
        }

        if (TextUtils.isEmpty(userDetailModel.getLastName())){
            validateName(false);
        }else {
            binding.editLastName.setText(userDetailModel.getLastName());
        }

        if (TextUtils.isEmpty(userDetailModel.getEmail())) {
            validateEmail();
        }else {
            binding.editEmail.setText(userDetailModel.getEmail());
        }


        if (TextUtils.isEmpty(userDetailModel.getPhone())){
            validPhoneNumber();
        }else {
            binding.editPhoneNumber.setText(userDetailModel.getPhone());
        }

        if (!Utils.isNullOrEmpty(userDetailModel.getCountryCode())) {
            try {
                countryCode = userDetailModel.getCountryCode().replace("+", "");
                int parsedCountryCode = Integer.parseInt(countryCode);
                binding.countryCode.setCountryForPhoneCode(parsedCountryCode);
            } catch (Exception e) {
                binding.countryCode.setAutoDetectedCountry(true);
            }
        } else {
            binding.countryCode.setAutoDetectedCountry(true);
        }

         if (!TextUtils.isEmpty(userDetailModel.getNationality())) {
            try {
                String nationalityCode = CountryCode.getCountryCodeByName(userDetailModel.getNationality());
                if (nationalityCode != null) {
                    binding.countryCodeForNationalty.setCountryForNameCode(nationalityCode);
                 } else {
                    binding.countryCodeForNationalty.setAutoDetectedCountry(true);
                }
            } catch (Exception e) {
                binding.countryCodeForNationalty.setAutoDetectedCountry(true);

            }
        } else {
            binding.countryCodeForNationalty.setAutoDetectedCountry(true);
        }

        region = binding.countryCode.getSelectedCountryNameCode();
        countryCode = binding.countryCode.getSelectedCountryCode();
        nationality = binding.countryCode.getSelectedCountryName();

    }

    private void openLocationPicker() {
        PingPlacePicker.Builder builder = new PingPlacePicker.Builder();
        builder.setAndroidApiKey(getString(R.string.places_api_key));
        builder.setMapsApiKey(getString(R.string.google_map_api_key));
        builder.setLatLng(new LatLng(25.208962, 55.272498));
        builder.setOnPlaceSelectedListener((place, latLng) -> {
            if (!isWithinDubai(latLng.latitude, latLng.longitude)) {
                Toast.makeText(activity, getValue("select_location_within_dubai"), Toast.LENGTH_SHORT).show();
            } else {
                LatLng tmpLatLang = getLatLngFromAddress(activity, place.getAddress());
                if (tmpLatLang != null && !isWithinDubai(tmpLatLang.latitude,tmpLatLang.longitude)){
                    Toast.makeText(activity, getValue("select_location_within_dubai"), Toast.LENGTH_SHORT).show();
                }else {
                    if (raynaTicketDetailModel.getBookingType().equals("whosin-ticket")){
                        whosinCustomTicketDetailAdapter.getData().get(selectedAdapterPosition).setPickUpLocation(place.getName() + ", " + place.getAddress());
                        whosinCustomTicketDetailAdapter.notifyItemChanged(selectedAdapterPosition);
                    }else {
                        tourOptionsDetailsAdapter.getData().get(selectedAdapterPosition).setPickUpLocation(place.getName() + ", " + place.getAddress());
                        tourOptionsDetailsAdapter.notifyItemChanged(selectedAdapterPosition);
                    }

                    selectedAdapterPosition = -1;
                    updateButtonColor();
                }

            }
        });

        try {
            Intent placeIntent = builder.build(activity);
            startActivityForResult(placeIntent, 1);
        }
        catch (Exception ex) {

        }
    }

    private boolean isWithinDubai(double lat, double lng) {
        return DubaiRegionChecker.isInsideDubai(lat,lng);
    }

    private LatLng getLatLngFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses == null || addresses.isEmpty()) {
                return null;
            }
            Address location = addresses.get(0);
            return new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("LocationUtils", "Geocoder IOException: " + e.getMessage());
            return null;
        }
    }

    private boolean hasAllValueFil(){

        if (TextUtils.isEmpty(binding.editFirstName.getText().toString())){
            return false;
        }
        if (TextUtils.isEmpty(binding.editLastName.getText().toString())) {
            return false;
        }

        if (TextUtils.isEmpty(primaryPrefix)){
            return false;
        }

        if (TextUtils.isEmpty(primaryPaxType)) {
            return false;
        }


        if (TextUtils.isEmpty(binding.editEmail.getText().toString())){
            return false;
        }

        if (!Utils.isValidEmail(binding.editEmail.getText().toString())){
            return false;
        }

        if (TextUtils.isEmpty(binding.editPhoneNumber.getText().toString())){
            return false;
        }

        if (!Utils.isValidPhoneNumber( countryCode,binding.editPhoneNumber.getText().toString(), region )){
            return false;
        }

        if (TextUtils.isEmpty(nationality)){
            return false;
        }

        if (raynaTicketDetailModel.getBookingType().equals("travel-desk")) {
            for (TravelDeskOptionDataModel p : travelDeskTourOptionsDetailsAdapter.getData()) {
                if (p.getTravelDeskPickUpListModel() == null){
                    return false;
                }
            }
        } else {
            if (tourOptionsDetailsAdapter.getData() != null){
                for (TourOptionsModel p : tourOptionsDetailsAdapter.getData()) {
                    if ((p.getSelectedTransferId() == 41844) || (p.getSelectedTransferId() == 41843)) {
                        if (TextUtils.isEmpty(p.getPickUpLocation())) {
                             return false;
                        }
                    }

                }
            }
        }



        return true;

    }

    private void updateButtonColor() {
        int color = ContextCompat.getColor(activity,hasAllValueFil() ? R.color.brand_pink : R.color.gray);
        binding.nextButton.setEnabled(hasAllValueFil());
        binding.nextButton.setBackgroundColor(color);
    }

    private TextWatcher getWatcher(EditText editText) {
        return new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateButtonColor();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        };
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            openLocationPicker();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }



    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class TourOptionsDetailsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TourOptionsViewHolder(UiUtils.getViewBy(parent, R.layout.item_participate_primary_detail));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            TourOptionsViewHolder viewHolder = (TourOptionsViewHolder) holder;

            TourOptionsModel model = (TourOptionsModel) getItem(position);

            viewHolder.binding.tvPickUpTitle.setText(getValue("pickup"));
            viewHolder.binding.editPickUp.setHint(getValue("select_pickup_location"));
            viewHolder.binding.tvPickupInfoTitle.setText(getValue("important_info_pickup"));
            viewHolder.binding.tvPickupInfoDesc.setText(getValue("shared_transfer_info"));

            if (model.getOptionDetail() != null){
                viewHolder.binding.tvHeaderTitle.setText(model.getOptionDetail().getOptionName());
            }else {
                viewHolder.binding.tvHeaderTitle.setText(model.getTitle());

            }




            viewHolder.binding.editPickUp.setText(model.getPickUpLocation());
            if (TextUtils.isEmpty(model.getPickUpLocation())) {
                setEditTextStrokeColor(viewHolder.binding.editPickUp, R.color.red);
            } else {
                setEditTextStrokeColor(viewHolder.binding.editPickUp, R.color.tour_option);
            }

//            if (model.getTransType().equals("Private Transfers") || model.getTransType().equals("Sharing Transfers")) {
//                viewHolder.binding.passengerPickUpLayout.setVisibility(View.VISIBLE);
//            } else {
//                viewHolder.binding.passengerPickUpLayout.setVisibility(View.GONE);
//            }

            if ((model.getSelectedTransferId() == 41844) || (model.getSelectedTransferId() == 41843)) {
                viewHolder.binding.passengerPickUpLayout.setVisibility(View.VISIBLE);
                viewHolder.binding.pickupNoteLayout.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.passengerPickUpLayout.setVisibility(View.GONE);
                viewHolder.binding.pickupNoteLayout.setVisibility(View.GONE);
            }

            viewHolder.binding.editPickUp.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                selectedAdapterPosition = position;
                requestLocationPermission();
            });

        }

        public class TourOptionsViewHolder extends RecyclerView.ViewHolder {

            private final ItemParticipatePrimaryDetailBinding binding;

            public TourOptionsViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemParticipatePrimaryDetailBinding.bind(itemView);
            }

        }

    }

    private class WhosinCustomTourOptionsDetailsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TourOptionsViewHolder(UiUtils.getViewBy(parent, R.layout.item_participate_primary_detail));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            TourOptionsViewHolder viewHolder = (TourOptionsViewHolder) holder;

            WhosinTicketTourOptionModel model = (WhosinTicketTourOptionModel) getItem(position);

            viewHolder.binding.tvPickUpTitle.setText(getValue("pickup"));
            viewHolder.binding.editPickUp.setHint(getValue("select_pickup_location"));
            viewHolder.binding.tvPickupInfoTitle.setText(getValue("important_info_pickup"));
            viewHolder.binding.tvPickupInfoDesc.setText(getValue("shared_transfer_info"));

            viewHolder.binding.tvHeaderTitle.setText(model.getDisplayName());

            viewHolder.binding.editPickUp.setText(model.getPickUpLocation());

            viewHolder.binding.passengerPickUpLayout.setVisibility(View.VISIBLE);
            viewHolder.binding.pickupNoteLayout.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(model.getPickUpLocation())) {
                setEditTextStrokeColor(viewHolder.binding.editPickUp, R.color.red);
            } else {
                setEditTextStrokeColor(viewHolder.binding.editPickUp, R.color.tour_option);
            }

            viewHolder.binding.editPickUp.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                selectedAdapterPosition = position;
                requestLocationPermission();
            });

        }

        public class TourOptionsViewHolder extends RecyclerView.ViewHolder {

            private final ItemParticipatePrimaryDetailBinding binding;

            public TourOptionsViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemParticipatePrimaryDetailBinding.bind(itemView);
            }

        }

    }

    private class TravelDeskTourOptionsDetailsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TourOptionsViewHolder(UiUtils.getViewBy(parent, R.layout.item_participate_primary_detail));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            TourOptionsViewHolder viewHolder = (TourOptionsViewHolder) holder;

            TravelDeskOptionDataModel model = (TravelDeskOptionDataModel) getItem(position);

            viewHolder.binding.passengerPickUpLayout.setVisibility(View.VISIBLE);
            viewHolder.binding.pickupNoteLayout.setVisibility(View.VISIBLE);
            Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.icon_drop_down_with_below_arrow);
            viewHolder.binding.editPickUp.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            viewHolder.binding.editPickUp.setHint(Utils.getLangValue("please_select_pickup_location"));
            viewHolder.binding.tvPickUpTitle.setText(Utils.getLangValue("pickup"));
            viewHolder.binding.tvPickupInfoTitle.setText(getValue("important_info_pickup"));
            viewHolder.binding.tvPickupInfoDesc.setText(getValue("shared_transfer_info"));

            viewHolder.binding.tvHeaderTitle.setText(model.getName());

            if (TextUtils.isEmpty(viewHolder.binding.editPickUp.getText())) {
                setEditTextStrokeColor(viewHolder.binding.editPickUp, R.color.red);
            } else {
                setEditTextStrokeColor(viewHolder.binding.editPickUp, R.color.tour_option);
            }


            if (model.getTravelDeskPickUpListModel() != null){
                viewHolder.binding.editPickUp.setText(model.getTravelDeskPickUpListModel().getName());
            }


            viewHolder.binding.editPickUp.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                TravelDeskPickUpListSheet bottomSheet = new TravelDeskPickUpListSheet(activity,String.valueOf(model.getId()));
//                bottomSheet.isDirectReport = model.isDirectReporting();
                if (model.getTravelDeskPickUpListModel() != null) {
                    if (model.getTravelDeskPickUpListModel().getId() != 0){
                        bottomSheet.selectedSlotPosition = model.getTravelDeskPickUpListModel().getId();
                    }else {
                        bottomSheet.location = model.getTravelDeskPickUpListModel().getName();
                        bottomSheet.selectedSlotPosition = 0;
                    }

                }
                bottomSheet.callback = data -> {
                    if (data != null){
                        model.setTravelDeskPickUpListModel(data);
                        viewHolder.binding.editPickUp.setText(data.getName());
                        notifyDataSetChanged();
                        updateButtonColor();
                    }
                };
                bottomSheet.show(getSupportFragmentManager(),"");
            });

        }

        public class TourOptionsViewHolder extends RecyclerView.ViewHolder {

            private final ItemParticipatePrimaryDetailBinding binding;

            public TourOptionsViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemParticipatePrimaryDetailBinding.bind(itemView);
            }

        }

    }


    // endregion
    // --------------------------------------
}
