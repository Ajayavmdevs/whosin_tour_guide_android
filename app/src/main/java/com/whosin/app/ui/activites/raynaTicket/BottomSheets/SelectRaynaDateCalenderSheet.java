package com.whosin.app.ui.activites.raynaTicket.BottomSheets;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentSelectEventDateBottomSheetBinding;
import com.whosin.app.service.models.VenueObjectModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class SelectRaynaDateCalenderSheet extends DialogFragment {

    private FragmentSelectEventDateBottomSheetBinding binding;

    private String SelectDate = "";

    public CommanCallback<String> callback;

    public List<String> dates = new ArrayList<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }

    private void initUi(View v) {
        binding = FragmentSelectEventDateBottomSheetBinding.bind(v);

        // Disable all other dates
        List<Calendar> disableDates = generateDisabledDates(dates);
        if (!disableDates.isEmpty()) {
            binding.materialCalenderView.setDisabledDays(disableDates);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // Select first allowed date by default
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTime(dateFormat.parse(dates.get(0)));
            binding.materialCalenderView.setDate(selectedDate);
            showAndSetDate(selectedDate);
            // Set min date (start of first month)
            Calendar minDate = Calendar.getInstance();
            minDate.setTime(dateFormat.parse(dates.get(0)));
            minDate.set(Calendar.DAY_OF_MONTH, 1);

            // Set max date (end of last month)
            Calendar maxDate = Calendar.getInstance();
            maxDate.setTime(dateFormat.parse(dates.get(dates.size() - 1)));
            maxDate.set(Calendar.DAY_OF_MONTH, maxDate.getActualMaximum(Calendar.DAY_OF_MONTH));

            binding.materialCalenderView.setMinimumDate(minDate);
            binding.materialCalenderView.setMaximumDate(maxDate);

        } catch (ParseException | OutOfDateRangeException e) {
            e.printStackTrace();
        }



    }

    private void setListener() {

//        binding.materialCalenderView.setOnDayClickListener(eventDay -> {
//            if (!eventDay.isEnabled()) {
//                return;
//            }
//
//            showAndSetDate(eventDay.getCalendar());
//        });

        binding.materialCalenderView.setOnDayClickListener(eventDay -> {
            if (eventDay.getCalendar().before(Calendar.getInstance())) {
                return; // past date disable
            }
            showAndSetDate(eventDay.getCalendar());
        });


        binding.btnDone.setOnClickListener(view -> {
            if (Utils.isNullOrEmpty(SelectDate)) {
                Toast.makeText(getContext(), "Please select date", Toast.LENGTH_SHORT).show();
                return;
            }
            if (callback != null) {
                callback.onReceive(SelectDate);
            }
            dismiss();
        });


    }



    private void showAndSetDate(Calendar calendar) {
        binding.txtDate.setVisibility(View.VISIBLE);
        binding.btnDone.setVisibility(View.VISIBLE);

        Calendar selectedDate = Calendar.getInstance();

        selectedDate.setTimeInMillis(calendar.getTimeInMillis());

        binding.txtDate.setText(Utils.formatDate(selectedDate.getTime(),AppConstants.DATEFORMT_EEE_d_MMM_yyyy));

        SelectDate = binding.txtDate.getText().toString();
    }

    private int getLayoutRes() {
        return R.layout.fragment_select_event_date_bottom_sheet;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            getView().post(() -> {
                View parent = (View) getView().getParent();
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
                int peekHeight = parent.getHeight();
                behavior.setPeekHeight(peekHeight);
            });
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private List<Calendar> generateDisabledDates(List<String> allowedDatesStr) {
        List<Calendar> disabledDates = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Set<String> allowedDatesSet = new HashSet<>(allowedDatesStr);

        // Parse all allowed dates into Calendar list
        List<Calendar> allowedCalendars = new ArrayList<>();
        for (String dateStr : allowedDatesStr) {
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(dateStr));
                allowedCalendars.add(cal);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (allowedCalendars.isEmpty()) return disabledDates;

        // Find min and max allowed dates
        Calendar minAllowed = (Calendar) Collections.min(allowedCalendars, Comparator.comparing(Calendar::getTime)).clone();
        Calendar maxAllowed = (Calendar) Collections.max(allowedCalendars, Comparator.comparing(Calendar::getTime)).clone();

        // Set minAllowed to 1st day of month
        minAllowed.set(Calendar.DAY_OF_MONTH, 1);

        // Set maxAllowed to last day of month
        maxAllowed.set(Calendar.DAY_OF_MONTH, maxAllowed.getActualMaximum(Calendar.DAY_OF_MONTH));

        // Start from minAllowed and go till maxAllowed
        Calendar current = (Calendar) minAllowed.clone();

        while (!current.after(maxAllowed)) {
            String currentDateStr = dateFormat.format(current.getTime());
            if (!allowedDatesSet.contains(currentDateStr)) {
                disabledDates.add((Calendar) current.clone());
            }
            current.add(Calendar.DAY_OF_MONTH, 1);
        }

        return disabledDates;
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    // endregion
    // --------------------------------------
}
