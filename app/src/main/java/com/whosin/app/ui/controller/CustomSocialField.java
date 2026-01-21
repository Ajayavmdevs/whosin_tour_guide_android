package com.whosin.app.ui.controller;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.LayoutSocialFieldBinding;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.ui.fragment.PromoterCreateEvent.PromoterEventInfoFragment;
import com.whosin.app.ui.fragment.PromoterCreateEvent.SelectEventDateBottomSheet;
import com.whosin.app.ui.fragment.home.SelectDateTimeBottomSheet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CustomSocialField extends ConstraintLayout {

    private LayoutSocialFieldBinding binding;

    public FragmentManager manager;

    public VenueObjectModel venueObjectModel;

    public boolean isCustomVenue = false;

    private boolean isEditTextEdittable = true;

    private PromoterEventInfoFragment fragment;

    public boolean isOpenCalender= false;

    public boolean isOpenTime= false;



    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public CustomSocialField(Context context) {
        this(context, null);
    }

    public CustomSocialField(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSocialField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SocialField, 0, 0);
        String hintText = a.getString(R.styleable.SocialField_socialFieldHintText);
        boolean border_color = a.getBoolean(R.styleable.SocialField_border_color, false);
        Drawable formIcon = a.getDrawable(R.styleable.SocialField_socialFieldIcon);
        String title_name = a.getString(R.styleable.SocialField_title_name);
        boolean show_title = a.getBoolean(R.styleable.SocialField_show_title, false);
        int inputType = a.getInt(R.styleable.SocialField_inputType, InputType.TYPE_CLASS_TEXT);


        View view = LayoutInflater.from(context).inflate(R.layout.layout_social_field, this);
        binding = LayoutSocialFieldBinding.bind(view);

        if (show_title) {
            binding.title.setVisibility(View.VISIBLE);
            binding.title.setText(title_name);
        }
        binding.editGetSocialInformation.setEnabled(true);
//        binding.editGetSocialInformation.setInputType(inputType);

        if (!Utils.isNullOrEmpty(hintText)) {
            binding.editGetSocialInformation.setHint(hintText);
            if (hintText.equals("5 spots(s)")) {
                binding.editGetSocialInformation.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        }
        if (!border_color) {
            binding.editGetSocialInformation.setBackgroundResource(R.drawable.light_black_color_bg);
        } else {
            binding.editGetSocialInformation.setBackgroundResource(R.drawable.form_edittext_background);
        }
        if (formIcon != null) {
            binding.editGetSocialInformation.setCompoundDrawablesWithIntrinsicBounds(formIcon, null, null, null);
        }

        binding.getRoot().setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            if (!isEditTextEdittable) {
                if (isOpenCalender) {
                    if (PromoterProfileManager.shared.isEventEdit) {
                        String date = Utils.changeDateFormat(binding.editGetSocialInformation.getText().toString(), AppConstants.DATEFORMT_EEE_d_MMM_yyyy, AppConstants.DATEFORMAT_SHORT);
                        String time = PromoterProfileManager.shared.promoterEventObject.get("startTime").getAsString();
                        if (Utils.isPastEvent(date, time)) {
                            Toast.makeText(context, "You can't change the date. Event has already started.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        openCalenderDialog();
                    }

                } else if (isOpenTime) {
                    if (PromoterProfileManager.shared.isEventEdit) {
                        String date = Utils.changeDateFormat(binding.editGetSocialInformation.getText().toString(), AppConstants.DATEFORMT_EEE_d_MMM_yyyy, AppConstants.DATEFORMAT_SHORT);
                        String time = PromoterProfileManager.shared.promoterEventObject.get("startTime").getAsString();
                        if (Utils.isPastEvent(date, time)) {
                            Toast.makeText(context, "You can't change the time. Event has already started.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        openTimeDialog();
                    }
                }
            }
        });


        binding.editGetSocialInformation.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            if (!isEditTextEdittable) {
                if (isOpenCalender) {
                    if (PromoterProfileManager.shared.isEventEdit && PromoterProfileManager.shared.promoterEventModel.getStatus().equals("in-progress")) {
                        String date = Utils.changeDateFormat(binding.editGetSocialInformation.getText().toString(), AppConstants.DATEFORMT_EEE_d_MMM_yyyy, AppConstants.DATEFORMAT_SHORT);
                        String time = PromoterProfileManager.shared.promoterEventObject.get("startTime").getAsString();
                        if (Utils.isPastEvent(date, time)) {
                            Toast.makeText(context, "You can't change the date. Event has already started.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        openCalenderDialog();
                    }

                } else if (isOpenTime) {
                    if (PromoterProfileManager.shared.isEventEdit && PromoterProfileManager.shared.promoterEventModel.getStatus().equals("in-progress")) {
                        String date = Utils.changeDateFormat(binding.editGetSocialInformation.getText().toString(), AppConstants.DATEFORMT_EEE_d_MMM_yyyy, AppConstants.DATEFORMAT_SHORT);
                        String time = PromoterProfileManager.shared.promoterEventObject.get("startTime").getAsString();
                        if (Utils.isPastEvent(date, time)) {
                            Toast.makeText(context, "You can't change the time. Event has already started.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        openTimeDialog();
                    }
                }
            }

        });


    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------


    public String getText() {
        return binding.editGetSocialInformation.getText().toString().trim();
    }

    public void setOnAvailableSpotsTextChangedListener(TextWatcher textWatcher) {
        binding.editGetSocialInformation.addTextChangedListener(textWatcher);
    }

    public void setText(String text) {
        binding.editGetSocialInformation.setText(text);
    }

    public void setHintText(String text) {
        binding.editGetSocialInformation.setHint(text);
        if (text.equals("5 spots(s)")) {
            binding.editGetSocialInformation.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    public void setTitleText(String text) {
        binding.title.setText(text);
    }

    public void setUpdata(boolean isEditable, PromoterEventInfoFragment fragment) {

        this.isEditTextEdittable = isEditable;
        this.fragment = fragment;


        if (!isEditable) {
            binding.editGetSocialInformation.setFocusable(false);
            binding.editGetSocialInformation.setClickable(true);
            binding.editGetSocialInformation.setCursorVisible(false);
        }


        binding.editGetSocialInformation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (fragment != null){
                    fragment.checkIfDataIsFilled();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void openCalenderDialog() {
        SelectEventDateBottomSheet selectDateTimeDialog = new SelectEventDateBottomSheet();
        if (isCustomVenue){
            selectDateTimeDialog.isCustomVenue = isCustomVenue;
        }else {
            if (venueObjectModel != null){
                selectDateTimeDialog.venueObjectModel = venueObjectModel;
            }
        }
        if (!TextUtils.isEmpty(binding.editGetSocialInformation.getText())){
            selectDateTimeDialog.AlreadTSelectedDate = binding.editGetSocialInformation.getText().toString();
        }
        selectDateTimeDialog.callback = data -> {
            if (!Utils.isNullOrEmpty(data)) {
                binding.editGetSocialInformation.setText(data);
            }
        };
        if (manager != null) {
            selectDateTimeDialog.show(manager, "1");
        }
    }



    private void openTimeDialog() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog fromTimePickerDialog = new TimePickerDialog(getContext(), R.style.OrangeDialogTheme, (view, fromHourOfDay, fromMinute) -> {
            Calendar fromCalendar = Calendar.getInstance();
            fromCalendar.set(Calendar.HOUR_OF_DAY, fromHourOfDay);
            fromCalendar.set(Calendar.MINUTE, fromMinute);
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            String fromTime = timeFormat.format(fromCalendar.getTime());

            Calendar minTillCalendar = (Calendar) fromCalendar.clone();
            minTillCalendar.add(Calendar.HOUR_OF_DAY, 1);  // Add 1 hour instead of 15 minutes

            TimePickerDialog tillTimePickerDialog = new TimePickerDialog(getContext(), R.style.OrangeDialogTheme, (view1, tillHourOfDay, tillMinute) -> {
                Calendar tillCalendar = Calendar.getInstance();
                tillCalendar.set(Calendar.HOUR_OF_DAY, tillHourOfDay);
                tillCalendar.set(Calendar.MINUTE, tillMinute);
                String tillTime = timeFormat.format(tillCalendar.getTime());

                String formattedTimes = String.format("from %s till %s", fromTime, tillTime);
                binding.editGetSocialInformation.setText(formattedTimes);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PromoterProfileManager.shared.promoterEventObject.addProperty("startTime", Utils.convertTo24HourFormat(fromTime));
                    PromoterProfileManager.shared.promoterEventObject.addProperty("endTime", Utils.convertTo24HourFormat(tillTime));
                }

            }, minTillCalendar.get(Calendar.HOUR_OF_DAY), minTillCalendar.get(Calendar.MINUTE), false);

            tillTimePickerDialog.setTitle("Select Till Time");
            tillTimePickerDialog.show();

        }, currentHour, currentMinute, false);

        fromTimePickerDialog.setTitle("Select From Time");
        fromTimePickerDialog.show();
    }



    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
}
