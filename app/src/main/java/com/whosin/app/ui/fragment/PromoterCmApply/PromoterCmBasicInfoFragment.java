package com.whosin.app.ui.fragment.PromoterCmApply;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.whosin.app.R;
import com.whosin.app.comman.CountryCode;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.FragmentPromoterCmBasicInfoBinding;
import com.whosin.app.service.manager.PromoterCmApplyManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PromoterCmBasicInfoFragment extends BaseFragment {

    private FragmentPromoterCmBasicInfoBinding binding;

    private PromoterCmApplyManager manager = PromoterCmApplyManager.shared;

    private UserDetailModel userDetailModel = manager.userDetailModel;

    private boolean isSelectNationality = false;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentPromoterCmBasicInfoBinding.bind(view);

        applyTranslations();

        if (manager.isEditProfile) {
            if (userDetailModel == null) {
                return;
            }

            binding.layoutEmail.setText(getEmailFromMailto(userDetailModel.getEmail()));


            if (!manager.isPromoter) {
                binding.layoutDob.setVisibility(View.VISIBLE);
                binding.layoutDob.setUpdata(requireActivity(), true, Utils.changeDateFormat(userDetailModel.getDateOfBirth(), "yyyy-MM-dd","dd MMM yyyy"));
            }

            binding.countryCode.setVisibility(View.VISIBLE);


            binding.etPhone.setText(userDetailModel.getPhone());
            binding.layoutGender.setText(userDetailModel.getGender());
            binding.layoutLocation.setText(userDetailModel.getAddress());
//            binding.editGetBio.setText(userDetailModel.getBio());


//            String result = userDetailModel.getNationality().replaceAll("\\(.*?\\)", "");
            String nationality = CountryCode.getCountryCodeByName(userDetailModel.getNationality());
            try {
                binding.countryCode.setCountryForNameCode(nationality);
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (!Utils.isNullOrEmpty(userDetailModel.getCountryCode())) {
                try {
                    String countryCode = userDetailModel.getCountryCode().replace("+", "");
                    int parsedCountryCode = Integer.parseInt(countryCode);
                    binding.countryCodePhone.setCountryForPhoneCode(parsedCountryCode);
                } catch (Exception e) {
                    binding.countryCodePhone.setAutoDetectedCountry(true);
                }
            } else {
                binding.countryCodePhone.setAutoDetectedCountry(true);
            }
        } else {

            if (!manager.isPromoter) {
                binding.layoutDob.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(SessionManager.shared.getUser().getDateOfBirth())){
                    binding.layoutDob.setUpdata(requireActivity(), true, Utils.changeDateFormat(SessionManager.shared.getUser().getDateOfBirth(), "yyyy-MM-dd","dd MMM yyyy"));
                }else {
                    binding.layoutDob.setUpdata(requireActivity(), true, "");
                }
            }

            setValueFormSession();

            binding.nationalityTv.setVisibility(View.VISIBLE);
            binding.countryCode.setVisibility(View.GONE);
        }
    }

    @Override
    public void setListeners() {

        binding.countryCode.setOnCountryChangeListener(() -> {
            binding.nationalityTv.setVisibility(View.GONE);
            binding.countryCode.setVisibility(View.VISIBLE);
            isSelectNationality = true;
        });

        binding.inputContainer.setOnClickListener(v -> {
            binding.countryCode.launchCountrySelectionDialog();
        });

        binding.etPhone.setOnClickListener(v -> {
            binding.etPhone.setCursorVisible(true);
        });

        binding.etPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.etPhone.setCursorVisible(true);
            }
        });



    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_promoter_cm_basic_info;
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();

        binding.layoutEmail.setHint(getValue("enter_your_email"));
        binding.layoutEmail.setTitle(getValue("emailRequired"));

        binding.layoutGender.setHint(getValue("select_gender"));
        binding.layoutGender.setTitle(getValue("genderReq"));

        binding.layoutDob.setHint(getValue("select_dob"));
        binding.layoutDob.setTitle(getValue("dob"));

        binding.layoutLocation.setHint(getValue("enter_your_location"));
        binding.layoutLocation.setTitle(getValue("location"));

        binding.nationalityTv.setHint(getValue("select_nationality"));

        map.put(binding.tvPhoneTitle, "mobile_number");
        map.put(binding.etPhone, "enter_your_mobile_number");
        map.put(binding.tvNationalityTitle, "national");



        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private String getEmailFromMailto(String mailtoString) {
        if (mailtoString != null && mailtoString.startsWith("mailto:")) {
            return mailtoString.substring(7);
        }
        return mailtoString;
    }

    private void setValueFormSession() {

        UserDetailModel model = SessionManager.shared.getUser();
        if (model == null) {
            return;
        }

        binding.layoutEmail.setText(model.getEmail());
        binding.layoutGender.setText(model.getGender());
        binding.etPhone.setText(model.getPhone());
        binding.layoutLocation.setText(model.getAddress());


        if (!TextUtils.isEmpty(model.getNationality())){
            String nationality = CountryCode.getCountryCodeByName(model.getNationality());
            try {
                binding.nationalityTv.setVisibility(View.GONE);
                binding.countryCode.setVisibility(View.VISIBLE);
                binding.countryCode.setCountryForNameCode(nationality);
            } catch (Exception e) {
                binding.countryCode.setVisibility(View.GONE);
                binding.nationalityTv.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }
        }


        if (!Utils.isNullOrEmpty(model.getCountryCode())) {
            try {
                String countryCode = model.getCountryCode().replace("+", "");
                int parsedCountryCode = Integer.parseInt(countryCode);
                binding.countryCodePhone.setCountryForPhoneCode(parsedCountryCode);
            } catch (NumberFormatException e) {
                binding.countryCodePhone.setAutoDetectedCountry(true);
            } catch (Exception e) {
                binding.countryCodePhone.setAutoDetectedCountry(true);
            }
        } else {
            binding.countryCodePhone.setAutoDetectedCountry(true);
        }
    }

    public boolean isDataValid() {

        if (Utils.isNullOrEmpty(binding.layoutEmail.getText())) {
            Toast.makeText(requireActivity(), getValue("please_enter_email"), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Utils.isValidEmail(binding.layoutEmail.getText())) {
            Toast.makeText(requireActivity(), getValue("invalid_email"), Toast.LENGTH_SHORT).show();
            return false;
        }


        if (Utils.isNullOrEmpty(binding.etPhone.getText().toString())) {
            Toast.makeText(requireActivity(), getValue("please_enter_phone"), Toast.LENGTH_SHORT).show();
            return false;
        }

        String phoneNumber = binding.etPhone.getText().toString();
        String countryCode = binding.countryCodePhone.getSelectedCountryCode();
        String region = binding.countryCodePhone.getSelectedCountryNameCode();

        if (!Utils.isValidPhoneNumber(countryCode, phoneNumber, region)) {
            Toast.makeText(requireActivity(),getValue("invalid_phone"), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!manager.isEditProfile && !isSelectNationality) {
            Toast.makeText(requireActivity(), getValue("please_select_nationality"), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Utils.isNullOrEmpty(binding.layoutGender.getText())) {
            Toast.makeText(requireActivity(), getValue("please_select_gender"), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!manager.isPromoter && Utils.isNullOrEmpty(binding.layoutDob.getText())) {
            Toast.makeText(requireActivity(), getValue("please_select_date_of_birth"), Toast.LENGTH_SHORT).show();
            return false;
        }

//        if (Utils.isNullOrEmpty(binding.layoutLocation.getText())) {
//            Toast.makeText(requireActivity(), "Please enter location", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (Utils.isNullOrEmpty(binding.editGetBio.getText().toString().trim())) {
//            Toast.makeText(requireActivity(), "Please enter bit about interests", Toast.LENGTH_SHORT).show();
//            return false;
//        }


        if (!manager.isPromoter)
            manager.object.addProperty("dateOfBirth", Utils.changeDateFormat(binding.layoutDob.getText(), "dd MMM yyyy", "yyyy-MM-dd"));
        manager.object.addProperty("address", binding.layoutLocation.getText());
        manager.object.addProperty("nationality", binding.countryCode.getSelectedCountryName());
        manager.object.addProperty("country_code", binding.countryCodePhone.getSelectedCountryCode());
        manager.object.addProperty("gender", binding.layoutGender.getText());
        manager.object.addProperty("phone", binding.etPhone.getText().toString());
        manager.object.addProperty("email", binding.layoutEmail.getText());


        return true;
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
}