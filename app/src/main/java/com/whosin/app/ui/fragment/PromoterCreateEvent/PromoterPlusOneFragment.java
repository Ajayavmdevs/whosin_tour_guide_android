package com.whosin.app.ui.fragment.PromoterCreateEvent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.FragmentPromoterPlusOneBinding;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.PromoterPaidPassModel;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PromoterPlusOneFragment extends BaseFragment {

    private FragmentPromoterPlusOneBinding binding;

    private JsonObject promoterEventObject = PromoterProfileManager.shared.promoterEventObject;

    private PromoterEventModel promoterEventModel = PromoterProfileManager.shared.promoterEventModel;

    private Integer minAgeValue = 16;
    private Integer maxAgeValue = 60;

    private int totalAvailableSeats;

    private String faq = "";

    private String paidPassTitle = "";

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    public void initUi(View view) {

        binding = FragmentPromoterPlusOneBinding.bind(view);

        applyTranslations();

        binding.radioVenue.setChecked(true);


        boolean check = PromoterProfileManager.shared.isEventEdit || PromoterProfileManager.shared.isEventSaveToDraft || PromoterProfileManager.shared.isEventRepost;
        if (promoterEventModel != null && check) {
            boolean isAllowExtrasGuest = promoterEventModel.isPlusOneAccepted();
            binding.checkbox.setChecked(isAllowExtrasGuest);
            binding.guestInformationLayout.setVisibility(isAllowExtrasGuest ? View.VISIBLE : View.GONE);

            if (!TextUtils.isEmpty(promoterEventModel.getFaq())) {
                binding.editFaq.setText(promoterEventModel.getFaq());
                faq = promoterEventModel.getFaq();
            }
            paidPassLayoutVisibility();

            totalAvailableSeats = promoterEventModel.getPlusOneQty();
            if (isAllowExtrasGuest) {
                if (promoterEventModel.getPlusOneQty() != 0) {
                    binding.numberOfExtraGuest.setText(String.valueOf(promoterEventModel.getPlusOneQty()));
                }
                UpdateLayoutVisibility();
            }
        } else {

            binding.tvMinAge.setText(String.format("Min Age : %d", minAgeValue));
            binding.tvMaxAge.setText(String.format("Max Age : %d", maxAgeValue));

            binding.radioBoth.setChecked(true);
            binding.randomPreferences.setChecked(true);
            binding.radioanyOne.setChecked(true);

        }


    }


    @Override
    public void setListeners() {

        binding.checkbox.setOnCheckedChangeListener((compoundButton, b) -> binding.guestInformationLayout.setVisibility(b ? View.VISIBLE : View.GONE));

        binding.nationalityText.setOnClickListener(view -> openPickerSheet());

        binding.countryCode.setOnCountryChangeListener(() -> {
            String selectedCountryName = binding.countryCode.getSelectedCountryName();
            binding.nationalityText.setText(selectedCountryName);
            binding.nationalityText.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
        });

        binding.dreesodeEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String enteredText = binding.dreesodeEditText.getText().toString().trim();
                if (!enteredText.isEmpty()) {
                    addChip(enteredText);
                    binding.dreesodeEditText.setVisibility(View.GONE);
                    binding.chipGroup.setVisibility(View.VISIBLE);
                }
                return true;
            }
            return false;
        });

        binding.seekBarAge.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            minAgeValue = Math.round(values.get(0));
            maxAgeValue = Math.round(values.get(1));
            binding.tvMinAge.setText("Min Age :" + " " + minAgeValue);
            binding.tvMaxAge.setText("Max Age :" + " " + maxAgeValue);
        });

        binding.anyOneRadioGroup.setOnCheckedChangeListener((group, checkedId) -> binding.specificationLayout.setVisibility(checkedId == binding.radioanyOne.getId() ? View.GONE : View.VISIBLE));

        binding.genderSelectLayout.setOnCheckedChangeListener((group, checkedId) -> binding.allocationPreferncesLayout.setVisibility(
                checkedId == R.id.radioBoth ? View.VISIBLE : View.GONE
        ));


        binding.allcationPreferGroup.setOnCheckedChangeListener((group, checkedId) -> binding.specificSeatsLayout.setVisibility(
                checkedId == R.id.specificPreferences ? View.VISIBLE : View.GONE
        ));


        binding.paidPassTypeSelectLayout.setOnCheckedChangeListener((group, checkedId) -> binding.eventPaidPadLayout.setVisibility(
                checkedId == R.id.radioEvent ? View.VISIBLE : View.GONE
        ));


        binding.numberOfExtraGuest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String text = charSequence.toString();
                totalAvailableSeats = 0;

                if (!text.isEmpty()) {
                    try {
                        totalAvailableSeats = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        totalAvailableSeats = 0;
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        final boolean[] isUpdating = {false};

        binding.maleSeatsCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && !isUpdating[0]) {
                    int maleSeats = Integer.parseInt(charSequence.toString());

                    isUpdating[0] = true;

                    if (maleSeats > totalAvailableSeats) {
                        binding.maleSeatsCount.setText(String.valueOf(totalAvailableSeats));
                        binding.femaleSeatsCount.setText("0");
                    } else {
                        int femaleSeats = totalAvailableSeats - maleSeats;
                        binding.femaleSeatsCount.setText(String.valueOf(femaleSeats));
                    }

                    isUpdating[0] = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.femaleSeatsCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && !isUpdating[0]) {
                    int femaleSeats = Integer.parseInt(charSequence.toString());

                    isUpdating[0] = true;

                    if (femaleSeats > totalAvailableSeats) {
                        binding.femaleSeatsCount.setText(String.valueOf(totalAvailableSeats));
                        binding.maleSeatsCount.setText("0");
                    } else {
                        int maleSeats = totalAvailableSeats - femaleSeats;
                        binding.maleSeatsCount.setText(String.valueOf(maleSeats));
                    }

                    isUpdating[0] = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.editFaq.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && binding.editFaq.getText().length() > 0) {
                binding.editFaq.setSelection(binding.editFaq.getSelectionStart());
            }
        });

        binding.editFaq.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && promoterEventModel != null) {
                    faq = promoterEventModel.getFaq() + editable;
                }
            }
        });

        binding.editFaq.setOnTouchListener((v, event) -> {
            if (v.getId() == R.id.editFaq) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
            return false;
        });


        binding.eventPaidPadLayout.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            PaidPassSelectBottomSheet bottomSheet = new PaidPassSelectBottomSheet();
            if (!TextUtils.isEmpty(paidPassTitle))
                bottomSheet.selectedPaidPassTitle = paidPassTitle;
            bottomSheet.callback = data -> {
                if (!TextUtils.isEmpty(data)) {
                    paidPassTitle = data;
                    binding.tvPaidPassText.setText(data);
                    binding.tvPaidPassText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            };
            bottomSheet.show(getChildFragmentManager(), "");
        });


    }


    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_promoter_plus_one;
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();

        map.put(binding.repeatEventTitle, "number_of_extra_guest");
        map.put(binding.numberOfExtraGuest, "guest_count");
        map.put(binding.genderTv, "guest_gender");
        map.put(binding.allocationPreferncesTv, "spots_allocation");
        map.put(binding.maleSeatsCount, "male_seats");
        map.put(binding.femaleSeatsCount, "female_seats");
        map.put(binding.ageTv, "guest_age");
        map.put(binding.dressCodeTv, "guest_dress_code");
        map.put(binding.dreesodeEditText, "enter_dress_code");
        map.put(binding.nationalityTv, "guest_nationality");
        map.put(binding.nationalityText, "select_nationality");
        map.put(binding.tvAddFAQTitle, "add_faq");
        map.put(binding.editFaq, "write_faq");
        map.put(binding.tvPaidPassType, "paid_pass_type");
        map.put(binding.tvPaidPassText, "select_paid_pass");

        binding.checkbox.setText(getValue("allow_extra_guest_for_this_event"));
        binding.plusOneRequire.setText(getValue("require_plus_one"));

//        binding.radioBoth.setText(getValue("both"));
//        binding.radioMale.setText(getValue("male"));
//        binding.randomPreferences.setText(getValue("random"));
//        binding.specificPreferences.setText(getValue("specific"));
//        binding.radioanyOne.setText(getValue("random_guest"));
//        binding.radioSpecific.setText(getValue("specific_guest"));


        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void openPickerSheet() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Note Specified");
        data.add("Select Nationality");
        Graphics.showActionSheet(getContext(), "Select Option", data, (data1, position) -> {
            switch (position) {
                case 0:
                    binding.nationalityText.setText("Note Specified");
                    break;
                case 1:
                    binding.countryCode.launchCountrySelectionDialog();
                    break;
            }
        });
    }

    private void addChip(String text) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            binding.chipGroup.removeView(chip);
            binding.dreesodeEditText.setVisibility(View.VISIBLE);
            binding.chipGroup.setVisibility(View.GONE);
        });

        chip.setOnClickListener(v -> {
            binding.chipGroup.removeView(chip);
            binding.dreesodeEditText.setVisibility(View.VISIBLE);
            binding.chipGroup.setVisibility(View.GONE);
        });

        chip.setChipBackgroundColorResource(R.color.brand_pink);

        binding.chipGroup.addView(chip);
        binding.chipGroup.setVisibility(View.VISIBLE);
        binding.dreesodeEditText.setVisibility(View.GONE);
    }

    private void UpdateLayoutVisibility() {
        binding.specificSeatsLayout.setVisibility(View.GONE);
        binding.specificationLayout.setVisibility(View.GONE);
        binding.allocationPreferncesLayout.setVisibility(View.VISIBLE);

        binding.plusOneRequire.setChecked(promoterEventModel.isPlusOneMandatory());

        if (!TextUtils.isEmpty(promoterEventModel.getExtraGuestGender())) {
            if (promoterEventModel.getExtraGuestGender().equalsIgnoreCase("both")) {
                binding.radioBoth.setChecked(true);

                if (!TextUtils.isEmpty(promoterEventModel.getExtraSeatPreference())) {
                    if (promoterEventModel.getExtraSeatPreference().equalsIgnoreCase("random")) {
                        binding.randomPreferences.setChecked(true);
                        binding.specificSeatsLayout.setVisibility(View.GONE);
                    } else {
                        binding.specificPreferences.setChecked(true);
                        binding.specificSeatsLayout.setVisibility(View.VISIBLE);
                        binding.maleSeatsCount.setText(String.valueOf(promoterEventModel.getExtraGuestMaleSeats()));
                        binding.femaleSeatsCount.setText(String.valueOf(promoterEventModel.getExtraGuestFemaleSeats()));
                    }
                } else {
                    binding.randomPreferences.setChecked(true);
                }
            } else if (promoterEventModel.getExtraGuestGender().equalsIgnoreCase("male")) {
                binding.radioMale.setChecked(true);
                binding.allocationPreferncesLayout.setVisibility(View.GONE);
            } else {
                binding.radioFemale.setChecked(true);
                binding.allocationPreferncesLayout.setVisibility(View.GONE);
            }
        } else {
            binding.radioBoth.setChecked(true);
            binding.randomPreferences.setChecked(true);
        }

        if (!TextUtils.isEmpty(promoterEventModel.getExtraGuestType())) {
            if (promoterEventModel.getExtraGuestType().equalsIgnoreCase("specific")) {
                binding.specificationLayout.setVisibility(View.VISIBLE);
                binding.radioSpecific.setChecked(true);

                String ageRange = promoterEventModel.getExtraGuestAge();
                minAgeValue = Integer.parseInt(ageRange.split("-")[0]);
                maxAgeValue = Integer.parseInt(ageRange.split("-")[1]);

                binding.seekBarAge.setValues((float) minAgeValue, (float) maxAgeValue);

                binding.tvMinAge.setText(String.valueOf(minAgeValue));
                binding.tvMaxAge.setText(String.valueOf(maxAgeValue));

                binding.dreesodeEditText.setText(promoterEventModel.getExtraGuestDressCode());
                binding.nationalityText.setText(promoterEventModel.getExtraGuestNationality());

                if (!TextUtils.isEmpty(promoterEventModel.getExtraGuestDressCode())) {
                    addChip(promoterEventModel.getExtraGuestDressCode());
                    binding.dreesodeEditText.setVisibility(View.GONE);
                    binding.chipGroup.setVisibility(View.VISIBLE);
                }

                if (!TextUtils.isEmpty(promoterEventModel.getExtraGuestNationality())) {
                    binding.nationalityText.setText(promoterEventModel.getExtraGuestNationality());
                }

            } else {
                binding.radioanyOne.setChecked(true);
            }
        } else {
            binding.tvMinAge.setText("Min Age :" + " " + minAgeValue);
            binding.tvMaxAge.setText("Max Age :" + " " + maxAgeValue);

            binding.radioanyOne.setChecked(true);
        }

    }

    private void paidPassLayoutVisibility() {
        if (!TextUtils.isEmpty(promoterEventModel.getPaidPassType())) {
            if (promoterEventModel.getPaidPassType().equals("override")) {
                binding.radioEvent.setChecked(true);
                binding.eventPaidPadLayout.setVisibility(View.VISIBLE);

                 if (!TextUtils.isEmpty(PromoterProfileManager.shared.getPaidPassString(promoterEventModel.getPaidPassId()))) {
                    String tmp = PromoterProfileManager.shared.getPaidPassString(promoterEventModel.getPaidPassId());
                    paidPassTitle = tmp;
                    binding.tvPaidPassText.setText(tmp);
                    binding.tvPaidPassText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else {
                    binding.tvPaidPassText.setText("");
                    binding.tvPaidPassText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_drop_down_with_below_arrow, 0);
                }
            } else {
                binding.radioVenue.setChecked(true);
                binding.eventPaidPadLayout.setVisibility(View.GONE);
            }
        } else {
            binding.eventPaidPadLayout.setVisibility(View.GONE);
            binding.radioVenue.setChecked(true);
        }
    }

    private void getEventPassJsonValue() {
        removeJsonKey("paidPassType");
        removeJsonKey("paidPassId");

        if (paidPassType().equalsIgnoreCase("Venue")) {
            promoterEventObject.addProperty("paidPassType", "default");
            promoterEventObject.addProperty("paidPassId", "");
        } else if (paidPassType().equalsIgnoreCase("Event")) {
            promoterEventObject.addProperty("paidPassType", "override");
            promoterEventObject.addProperty("paidPassId", PromoterProfileManager.shared.getPaidPassId(paidPassTitle));
        }

    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------


    public boolean isDataValid() {

        if (binding.checkbox.isChecked()) {
            if (TextUtils.isEmpty(binding.numberOfExtraGuest.getText().toString())) {
                Toast.makeText(context, getValue("enter_number_of_guest_count"), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (binding.genderSelectLayout.getCheckedRadioButtonId() == -1) {
                Toast.makeText(context, getValue("select_guest_gender"), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (guestGender().equals("Both") && binding.allcationPreferGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(context, getValue("select_allocation_preferences"), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (guestGender().equals("Both") && guestAllocationPreferences().equalsIgnoreCase("Specific")) {

                if (TextUtils.isEmpty(binding.maleSeatsCount.getText().toString())) {
                    Toast.makeText(requireActivity(), getValue("enter_number_available_seats"), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (TextUtils.isEmpty(binding.femaleSeatsCount.getText().toString())) {
                    Toast.makeText(requireActivity(), getValue("enter_number_available_female_seats"), Toast.LENGTH_SHORT).show();
                    return false;
                }

                int availableSportCount = Integer.parseInt(binding.numberOfExtraGuest.getText().toString());
                int maleSeatsCount = Integer.parseInt(binding.maleSeatsCount.getText().toString());
                int femaleSeatsCount = Integer.parseInt(binding.femaleSeatsCount.getText().toString());

                if (availableSportCount != (maleSeatsCount + femaleSeatsCount)) {
                    Toast.makeText(requireActivity(), getValue("total_male_female_seats"), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            if (binding.anyOneRadioGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(context, getValue("please_select_guest_type"), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (guestType().equalsIgnoreCase("Specific guest")) {

                if (TextUtils.isEmpty(binding.dreesodeEditText.getText().toString())) {
                    Toast.makeText(context, getValue("please_enter_guest_dress_code"), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            if (paidPassType().equalsIgnoreCase("Event") && TextUtils.isEmpty(paidPassTitle)){
                Toast.makeText(context, getValue("please_select_paid_pass"), Toast.LENGTH_SHORT).show();
                return false;
            }
            addAllValuesInJsonObject();
            return true;
        }

        if (paidPassType().equalsIgnoreCase("Event") && TextUtils.isEmpty(paidPassTitle)){
            Toast.makeText(context,  getValue("please_select_paid_pass"), Toast.LENGTH_SHORT).show();
            return false;
        }

        promoterEventObject.addProperty("plusOneAccepted", binding.checkbox.isChecked());
        promoterEventObject.addProperty("faq", faq);
        getEventPassJsonValue();
        return true;

    }

    private void addAllValuesInJsonObject() {

        getEventPassJsonValue();

        promoterEventObject.addProperty("plusOneAccepted", binding.checkbox.isChecked());

        removeJsonKey("faq");
        promoterEventObject.addProperty("faq", faq);


        if (!binding.checkbox.isChecked()) {
            String[] keysToRemove = {
                    "plusOneQty", "extraGuestType", "extraGuestAge", "extraGuestDressCode",
                    "extraGuestGender", "extraGuestNationality", "extraGuestMaleSeats", "extraGuestFemaleSeats", "extraSeatPreference"
            };

            for (String key : keysToRemove) {
                removeJsonKey(key);
            }

        } else {

            String plusOneCount = binding.numberOfExtraGuest.getText().toString();
            int plusOneQty = TextUtils.isEmpty(plusOneCount) ? 0 : Integer.parseInt(plusOneCount);
            promoterEventObject.addProperty("plusOneQty", plusOneQty);
            promoterEventObject.addProperty("extraGuestGender", guestGender().toLowerCase());
            promoterEventObject.addProperty("plusOneMandatory", binding.plusOneRequire.isChecked());


            if (guestGender().equalsIgnoreCase("Both") && guestAllocationPreferences().equalsIgnoreCase("Specific")) {
                String femaleSeatsText = binding.femaleSeatsCount.getText().toString();
                String maleSeatsText = binding.maleSeatsCount.getText().toString();

                int femaleSeats = TextUtils.isEmpty(femaleSeatsText) ? 0 : Integer.parseInt(femaleSeatsText);
                int maleSeats = TextUtils.isEmpty(maleSeatsText) ? 0 : Integer.parseInt(maleSeatsText);

                promoterEventObject.addProperty("extraGuestMaleSeats", maleSeats);
                promoterEventObject.addProperty("extraGuestFemaleSeats", femaleSeats);
            } else {
                removeJsonKey("extraGuestMaleSeats");
                removeJsonKey("extraGuestFemaleSeats");
            }

            if (guestGender().equalsIgnoreCase("Both")) {
                promoterEventObject.addProperty("extraSeatPreference", guestAllocationPreferences().toLowerCase());
            } else {
                removeJsonKey("extraSeatPreference");
            }

            if (guestType().equalsIgnoreCase("Specific guest")) {
                promoterEventObject.addProperty("extraGuestDressCode", binding.dreesodeEditText.getText().toString());
                promoterEventObject.addProperty("extraGuestNationality", binding.nationalityText.getText().toString().toLowerCase());
                String ageRange = minAgeValue + "-" + maxAgeValue;
                promoterEventObject.addProperty("extraGuestAge", ageRange);
                promoterEventObject.addProperty("extraGuestType", "specific");
            } else {
                promoterEventObject.addProperty("extraGuestType", "anyone");
                removeJsonKey("extraGuestDressCode");
                removeJsonKey("extraGuestNationality");
                removeJsonKey("extraGuestAge");
            }
        }
    }


    public void saveToDraft() {
        addAllValuesInJsonObject();
    }

    private String guestGender() {
        int selectedId = binding.genderSelectLayout.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = binding.getRoot().findViewById(selectedId);
        return selectedRadioButton.getText().toString();
    }

    private String guestAllocationPreferences() {
        int selectedId = binding.allcationPreferGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = binding.getRoot().findViewById(selectedId);
        return selectedRadioButton.getText().toString();
    }

    private String guestType() {
        int selectedId = binding.anyOneRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = binding.getRoot().findViewById(selectedId);
        return selectedRadioButton.getText().toString();
    }

    private String paidPassType() {
        int selectedId = binding.paidPassTypeSelectLayout.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = binding.getRoot().findViewById(selectedId);
        return selectedRadioButton.getText().toString();
    }

    private void removeJsonKey(String key) {
        if (promoterEventObject.has(key)) promoterEventObject.remove(key);
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