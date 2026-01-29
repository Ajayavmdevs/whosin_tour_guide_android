package com.whosin.business.ui.activites.JpHotel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.business.R;
import com.whosin.business.comman.CountryCode;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.ActivityJphotelGuestListBinding;
import com.whosin.business.databinding.ItemJpHotelGuestLayoutBinding;
import com.whosin.business.service.manager.JPTicketManager;
import com.whosin.business.service.manager.LogManager;
import com.whosin.business.service.manager.RaynaTicketManager;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.JuniperHotelModels.JPPassengerModel;
import com.whosin.business.service.models.JuniperHotelModels.PaxesItemModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.ui.activites.comman.BaseActivity;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class JPHotelGuestListActivity extends BaseActivity {

    private ActivityJphotelGuestListBinding binding;

    private final GuestListAdapter<JPPassengerModel> guestListAdapter = new GuestListAdapter<>();

    private final JPTicketManager jpTicketManager = JPTicketManager.shared;

    private final ArrayList<String> prefixList = new ArrayList<>(Arrays.asList("Mr", "Ms", "Mrs"));

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        applyTranslations();


        jpTicketManager.activityList.add(activity);

        binding.guestListRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.guestListRecycleView.setAdapter(guestListAdapter);

        List<JPPassengerModel> tmpList = new ArrayList<>();
        AtomicInteger id = new AtomicInteger(1);

        for (PaxesItemModel item : jpTicketManager.hotelRequestModel.getPaxes()) {
            addPassengers(tmpList, item.getAdultCount(), 1, "adult", id);
            addPassengers(tmpList, item.getChildCount(), 0, "child", id);
        }

        tmpList.set(0, getPrimaryGuestDetail());
        guestListAdapter.updateData(tmpList);

        updateButtonColor();
    }

    @Override
    protected void setListeners() {

        binding.constraintHeader.ivClose.setOnClickListener(v -> finish());

        binding.nextButton.setOnClickListener(v -> {
            if (guestListAdapter != null) {
                List<JPPassengerModel> guestList = guestListAdapter.getData();
                if (guestList == null || guestList.isEmpty()) return;
                List<JPPassengerModel> clonedList = new ArrayList<>();
                for (JPPassengerModel original : guestList) {
                    JPPassengerModel copy = new JPPassengerModel(original);
                    copy.setMobile(copy.getTmpCountryCode() + copy.getMobile());
                    copy.setTmpCountryCode("");
                    clonedList.add(copy);
                }
                if (jpTicketManager.guestList != null) {
                    jpTicketManager.guestList.clear();
                }
                jpTicketManager.guestList = clonedList;

                Double price = 0.0;
                try {
                    if (jpTicketManager.priceModel != null) {
                        price = Double.valueOf(jpTicketManager.priceModel.getAmount());
                    }
                } catch (Exception e) {
                   e.printStackTrace();
                }

                String id = "";
                String title = "";
                if (RaynaTicketManager.shared.raynaTicketDetailModel != null) {
                    id = RaynaTicketManager.shared.raynaTicketDetailModel.getId();
                    title = RaynaTicketManager.shared.raynaTicketDetailModel.getTitle();
                }

                LogManager.shared.logTicketEvent(LogManager.LogEventType.addUserInfo, id, title, price, null, "AED");

                startActivity(new Intent(activity, JpHotelCheckOutActivity.class));
            }
        });


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityJphotelGuestListBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.constraintHeader.tvTitle, "guest_details");
        map.put(binding.tvNext, "next");
        return map;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        jpTicketManager.activityList.remove(activity);
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private JPPassengerModel getPrimaryGuestDetail() {
        UserDetailModel model = SessionManager.shared.getUser();
        String age = "";
        if (!TextUtils.isEmpty(model.getDateOfBirth())){
            age = String.valueOf(calculateAge(model.getDateOfBirth()));
        }
        return new JPPassengerModel("1", "Mr.", model.getFirstName(), model.getLastName(), model.getEmail(), model.getPhone(), model.getNationality(), 1, "adult", model.getCountryCode(),age);
    }

    private void addPassengers(List<JPPassengerModel> list, int count, int leadPassenger, String type, AtomicInteger id) {
        UserDetailModel user = SessionManager.shared.getUser();
        String email = user != null && !TextUtils.isEmpty(user.getEmail()) ? user.getEmail() : "";
        String mobile = user != null && !TextUtils.isEmpty(user.getPhone()) ? user.getPhone() : "";
        String nationality = user != null && !TextUtils.isEmpty(user.getNationality()) ? user.getNationality() : "";
        String countryCode = user != null && !TextUtils.isEmpty(user.getCountryCode()) ? user.getCountryCode() : "";

        for (int i = 0; i < count; i++) {
            list.add(new JPPassengerModel(String.valueOf(id.getAndIncrement()), "Mr.", leadPassenger, type, email, mobile, nationality, countryCode));
        }
    }


    private String safeCountryCode(String countryName) {
        try {
            return CountryCode.getCountryCodeByName(countryName);
        } catch (Exception e) {
            return null;
        }
    }

    private void updateButtonColor() {
        List<JPPassengerModel> list = guestListAdapter != null ? guestListAdapter.getData() : null;

        boolean allFilled = true;
        if (list == null || list.isEmpty()) {
            allFilled = false;
        } else {
            for (JPPassengerModel model : list) {
                if (!model.allValueFilled()) {
                    allFilled = false;
                    break;
                }
            }
        }

        int color = ContextCompat.getColor(activity, allFilled ? R.color.brand_pink : R.color.gray);
        binding.nextButton.setEnabled(allFilled);
        binding.nextButton.setBackgroundColor(color);
    }

    public static int calculateAge(String dobString) {
        LocalDate dob = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dob = LocalDate.parse(dobString);
        }
        LocalDate today = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            today = LocalDate.now();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Period.between(dob, today).getYears();
        }
        return 0;
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class GuestListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_jp_hotel_guest_layout));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;

            JPPassengerModel model = (JPPassengerModel) getItem(position);

            boolean isLastItem = position == getItemCount() - 1;


            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.20f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }

            // Header Title
            if (position == 0) {
                viewHolder.mBinding.tvHeaderTitle.setText(getValue("primary_guest_details"));
            } else {
                String header = model.getPaxType().equals("adult") ? setValue("adult_detail", String.valueOf(model.getId())) : setValue("child_details", String.valueOf(model.getId()));
                viewHolder.mBinding.tvHeaderTitle.setText(header);
            }


            // Capitalize pax type
            String paxType = model.getPaxType();
            if (!TextUtils.isEmpty(paxType)) {
                paxType = paxType.substring(0, 1).toUpperCase() + paxType.substring(1);
            }
            viewHolder.mBinding.tvPaxTypeHint.setText(paxType);


            // Country code
            String selectedCode = viewHolder.mBinding.countryCode.getSelectedCountryCodeWithPlus();
            if (Utils.isNullOrEmpty(selectedCode) || !selectedCode.equals(model.getTmpCountryCode())) {
                // only update if different
                if (!Utils.isNullOrEmpty(model.getTmpCountryCode())) {
                    try {
                        int parsedCountryCode = Integer.parseInt(model.getCountryCode().replace("+", ""));
                        viewHolder.mBinding.countryCode.setCountryForPhoneCode(parsedCountryCode);
                    } catch (Exception e) {
                        viewHolder.mBinding.countryCode.setAutoDetectedCountry(true);
                    }
                } else {
                    viewHolder.mBinding.countryCode.setAutoDetectedCountry(true);
                }
            }


            // Nationality
            String nationality = model.getNationality();
            String nationalityCode = !TextUtils.isEmpty(nationality) ? safeCountryCode(nationality) : null;
            String currentNat = viewHolder.mBinding.countryCodeForNationalty.getSelectedCountryName();
            if (!Objects.equals(currentNat, model.getNationality())) {
                if (!TextUtils.isEmpty(nationalityCode)) {
                    viewHolder.mBinding.countryCodeForNationalty.setCountryForNameCode(nationalityCode);
                } else {
                    viewHolder.mBinding.countryCodeForNationalty.setAutoDetectedCountry(true);
                }
            }



            model.setCountryCode(viewHolder.mBinding.countryCode.getSelectedCountryNameCode());
            model.setTmpCountryCode(viewHolder.mBinding.countryCode.getSelectedCountryCodeWithPlus());
            model.setNationality(viewHolder.mBinding.countryCodeForNationalty.getSelectedCountryName());

            // Prefill fields
            viewHolder.mBinding.editFirstName.setText(model.getFirstName());
            viewHolder.mBinding.editLastName.setText(model.getLastName());
            viewHolder.mBinding.editEmail.setText(model.getEmail());
            viewHolder.mBinding.editPhoneNumber.setText(model.getMobile());
            viewHolder.mBinding.editAge.setText(model.getAge());

            // Spinner
            if (viewHolder.mBinding.selectPrefix.getAdapter() == null) {
                CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_item, prefixList);
                spinnerAdapter.setDropDownViewResource(R.layout.rooms_spinner_drop_down_item);
                viewHolder.mBinding.selectPrefix.setAdapter(spinnerAdapter);

                viewHolder.mBinding.selectPrefix.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        model.setPrefix(prefixList.get(position)); // save selection in model
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            int prefixPosition = prefixList.indexOf(model.getPrefix());
            viewHolder.mBinding.selectPrefix.setSelection(prefixPosition == -1 ? 0 : prefixPosition, false);


            viewHolder.mBinding.countryCode.setOnCountryChangeListener(() -> {
                model.setCountryCode(viewHolder.mBinding.countryCode.getSelectedCountryNameCode());
                model.setTmpCountryCode(viewHolder.mBinding.countryCode.getSelectedCountryCodeWithPlus());
                viewHolder.validateField(viewHolder.mBinding.editPhoneNumber,viewHolder.mBinding.editPhoneNumber.getText().toString());
            });

            viewHolder.mBinding.countryCodeForNationalty.setOnCountryChangeListener(() -> {
                model.setNationality(viewHolder.mBinding.countryCodeForNationalty.getSelectedCountryName());
            });


            viewHolder.mBinding.passengerNationalityLayout.setOnClickListener(v -> viewHolder.mBinding.countryCodeForNationalty.launchCountrySelectionDialog());

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemJpHotelGuestLayoutBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemJpHotelGuestLayoutBinding.bind(itemView);
                handleBindingValue();
                attachTextWatcher(mBinding.editFirstName);
                attachTextWatcher(mBinding.editLastName);
                attachTextWatcher(mBinding.editAge);
                attachTextWatcher(mBinding.editEmail);
                attachTextWatcher(mBinding.editPhoneNumber);
            }

            private void attachTextWatcher(EditText editText) {
                editText.addTextChangedListener(new GenericTextWatcher(editText));
            }

            private class GenericTextWatcher implements TextWatcher {

                private final View view;

                GenericTextWatcher(View view) {
                    this.view = view;
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (view.getId() == R.id.editAge) {
                        String text = s.toString();
                        if (text.length() > 3) {
                            String trimmed = text.substring(0, 3);
                            ((EditText) view).setText(trimmed);
                            ((EditText) view).setSelection(trimmed.length());
                            return;
                        }
                    }
                    validateField(view, s.toString());
                }
            }

            @SuppressLint("NonConstantResourceId")
            private void validateField(View view, String text) {
                int red_color = R.color.red;
                int tour_option = R.color.tour_option;

                JPPassengerModel model = (JPPassengerModel) getItem(getPosition());
                if (model == null) return;

                switch (view.getId()) {
                    case R.id.editFirstName:
                        model.setFirstName(text.trim());
                        if (TextUtils.isEmpty(text)) {
                            setEditTextStrokeColor(mBinding.editFirstName, red_color);
                            mBinding.editFirstName.setError(getValue("enter_firstname"));
                        } else {
                            setEditTextStrokeColor(mBinding.editFirstName, tour_option);
                            mBinding.editFirstName.setError(null);
                        }
                        break;

                    case R.id.editLastName:
                        model.setLastName(text.trim());
                        if (TextUtils.isEmpty(text)) {
                            setEditTextStrokeColor(mBinding.editLastName, red_color);
                            mBinding.editLastName.setError(getValue("enter_lastname"));
                        } else {
                            setEditTextStrokeColor(mBinding.editLastName, tour_option);
                            mBinding.editLastName.setError(null);
                        }
                        break;
                    case R.id.editAge:
                        model.setAge(text.trim());
                        if (TextUtils.isEmpty(text)) {
                            setEditTextStrokeColor(mBinding.editAge, red_color);
                            mBinding.editAge.setError(getValue("enter_age"));
                        } else if (!model.isValidAge()) {
                            setEditTextStrokeColor(mBinding.editAge, red_color);
                            String errorMessage = getValue(model.getPaxType().equalsIgnoreCase("adult") ?"invalid_age_adult" : "invalid_age_child");
                            mBinding.editAge.setError(errorMessage);
                        } else {
                            setEditTextStrokeColor(mBinding.editAge, tour_option);
                            mBinding.editAge.setError(null);
                        }
                        break;

                    case R.id.editEmail:
                        model.setEmail(text.trim());
                        if (Utils.isValidEmail(text)) {
                            setEditTextStrokeColor(mBinding.editEmail, tour_option);
                            mBinding.editEmail.setError(null);
                        } else {
                            setEditTextStrokeColor(mBinding.editEmail, red_color);
                            if (TextUtils.isEmpty(text)) {
                                mBinding.editEmail.setError(getValue("enter_email"));
                            } else {
                                mBinding.editEmail.setError(getValue("invalid_email"));
                            }
                        }
                        break;

                    case R.id.editPhoneNumber:
                        model.setMobile(text.trim());
                        if (TextUtils.isEmpty(text)) {
                            mBinding.editPhoneNumber.setError(getValue("enter_phone_number"));
                            setViewStrokeColor(mBinding.inputContainer, red_color);
                        } else if (!Utils.isValidPhoneNumber(mBinding.countryCode.getSelectedCountryNameCode(),text)) {
                            mBinding.editPhoneNumber.setError(getValue("invalid_phone"));
                            setViewStrokeColor(mBinding.inputContainer, red_color);
                        } else {
                            setViewStrokeColor(mBinding.inputContainer, tour_option);
                            mBinding.editPhoneNumber.setError(null);
                        }
                        break;
                }

                updateButtonColor();
            }

            private void setEditTextStrokeColor(EditText editText, int colorResId) {
                GradientDrawable background = (GradientDrawable) editText.getBackground().mutate();
                int strokeWidth = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 0.8f, editText.getResources().getDisplayMetrics());
                background.setStroke(strokeWidth, ContextCompat.getColor(editText.getContext(), colorResId));
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


            private void handleBindingValue(){
                Map<View, String> map = new HashMap<>();
                map.put(mBinding.txtName, "name");
                map.put(mBinding.txtPaxType, "pax_type");
                map.put(mBinding.contactTitle, "please_enter_your_active_whatsapp_number");
                map.put(mBinding.nationalityTitle, "nationality");
                map.put(mBinding.editFirstName, "enter_firstname");
                map.put(mBinding.editLastName, "enter_lastname");
                map.put(mBinding.editEmail, "enter_email");
                map.put(mBinding.editPhoneNumber, "enter_phone_number");
                map.put(mBinding.txtAge, "jp_guest_age");
                map.put(mBinding.editAge, "enter_age");

                String main = getValue("email_id");
                String warning = getValue("make_sure_this_is_your_correct_email");
                SpannableString spannable = new SpannableString(main + warning);

                spannable.setSpan(new TextAppearanceSpan(activity, R.style.txt11Regular), 0, main.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new TextAppearanceSpan(activity, R.style.txt7Regular), main.length(), spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mBinding.txtEmail.setText(spannable);

                langConfig(map);

            }

            private void langConfig(Map<View, String> map) {
                for (Map.Entry<View, String> entry : map.entrySet()) {
                    View view = entry.getKey();
                    String key = entry.getValue();
                    String value = getValue(key); // your method to get translated string

                    if (view instanceof EditText) {
                        ((EditText) view).setHint(value);
                    } else if (view instanceof TextView) {
                        ((TextView) view).setText(value);
                    }
                }
            }


        }
    }

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
}