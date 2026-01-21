package com.whosin.app.ui.controller;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ItemCustomFormTextFieldBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomFromTextField extends ConstraintLayout {

    private boolean isSelectDob = false;

    private String selectedDate = "";

    private Activity activity;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    private ItemCustomFormTextFieldBinding binding;

    public CustomFromTextField(Context context) {
        this(context, null);
    }

    public CustomFromTextField(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFromTextField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FormField, 0, 0);
        String titleText = a.getString(R.styleable.FormField_formTitle);
        String hintText = a.getString(R.styleable.FormField_formHintText);
        boolean showArrow = a.getBoolean(R.styleable.FormField_showArrow,false);
        boolean isHideTitle = a.getBoolean(R.styleable.FormField_isHideTitle,false);

        View view = LayoutInflater.from(context).inflate(R.layout.item_custom_form_text_field, this);
        binding = ItemCustomFormTextFieldBinding.bind(view);

        if (!Utils.isNullOrEmpty(titleText)) {
            binding.title.setText(titleText);
        }


        binding.title.setVisibility(isHideTitle ? View.GONE : View.VISIBLE);

        if (showArrow) {
            binding.editGetInformation.setFocusable(false);
            binding.editGetInformation.setClickable(true);
            binding.ivDropDown.setVisibility(View.VISIBLE);
        } else {
            binding.editGetInformation.setClickable(false);
            binding.editGetInformation.setFocusable(true);
            binding.ivDropDown.setVisibility(View.GONE);
        }

        if (!Utils.isNullOrEmpty(hintText)) {
            binding.editGetInformation.setHint(hintText);
        }

        if (showArrow) {
            binding.editGetInformation.setOnClickListener(v -> {
                if (isSelectDob) {
                    openCalendar();
                } else {
                    showGenderPicker();
                }
            });
        } else {
            binding.editGetInformation.setOnClickListener(null);
        }

        binding.ivDropDown.setOnClickListener(v -> {
            if (isSelectDob) {
                openCalendar();
            } else {
                showGenderPicker();
            }
        });

    }


    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public void setUpdata(Activity activity ,  boolean isSelectDob , String selectedDate){
        this.activity = activity;
        this.isSelectDob = isSelectDob;
        this.selectedDate = selectedDate;

        setText(selectedDate);
    }


    public String getText(){
        return binding.editGetInformation.getText().toString().trim();
    }

    public void setText(String editText){
        binding.editGetInformation.setText(editText);
    }

    public void setTitle(String title){
        binding.title.setText(title);
    }

    public void setHint(String hint){
        binding.editGetInformation.setHint(hint);
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void showGenderPicker() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Male");
        data.add("Female");
        data.add("Prefer not to Say");
        Graphics.showActionSheet(getContext(), Utils.getLangValue("select_gender"), data, (data1, position) -> {
            switch (position) {
                case 0:
                    binding.editGetInformation.setText("Male");
                    break;
                case 1:
                    binding.editGetInformation.setText("Female");
                    break;
                case 2:
                    binding.editGetInformation.setText("Prefer not to Say");
                    break;
            }
        });
    }

    private void openCalendar() {
        Calendar calendar = Calendar.getInstance();

        // Calculate 18 years ago
        Calendar cutoffDate = Calendar.getInstance();
        cutoffDate.add(Calendar.YEAR, -18);

        if (selectedDate != null && !selectedDate.isEmpty()) {
            try {
                // Set selected date in the calendar
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date date = sdf.parse(selectedDate);
                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                // In case of parsing error, default to 18 years ago
                calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -18);
            }
        } else {
            // Default to 18 years ago if no date is selected
            calendar.setTime(cutoffDate.getTime());
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar finalCalendar = calendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, (datePicker, years, monthOfYear, dayOfMonth) -> {
            finalCalendar.set(Calendar.YEAR, years);
            finalCalendar.set(Calendar.MONTH, monthOfYear);
            finalCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            selectedDate = sdf.format(finalCalendar.getTime());
            binding.editGetInformation.setText(Utils.changeDateFormat(selectedDate, "yyyy-MM-dd", "dd MMM yyyy"));
        }, year, month, day);

        // Restrict the date picker to allow only dates before "18 years ago"
        datePickerDialog.getDatePicker().setMaxDate(cutoffDate.getTimeInMillis() - 1);

        // Apply custom button colors if necessary
        Utils.applyButtonColors(datePickerDialog, activity);

        datePickerDialog.show();
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
