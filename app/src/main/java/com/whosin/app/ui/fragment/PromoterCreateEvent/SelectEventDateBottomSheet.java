package com.whosin.app.ui.fragment.PromoterCreateEvent;

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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class SelectEventDateBottomSheet extends DialogFragment {

    private FragmentSelectEventDateBottomSheetBinding binding;

    private String SelectDate = "";

    public String AlreadTSelectedDate = "";

    public CommanCallback<String> callback;

    public boolean isCustomVenue = false;

    public VenueObjectModel venueObjectModel;

    public String eventStartDate = "";

    public String eventEndDate = "";

    public boolean isEventRepeat = false;

    public boolean isFromRaynaTicket = false;

    public boolean allowTodaysBooking = false;


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

        List<Calendar> disableDates = new ArrayList<>();

        Date startDate = getStartDate();
        Date endDate = getEndDate();
        String[] days = getDaysToDisable();
        if (new Date().after(startDate)) {
            startDate = new Date();
        }



        Calendar minDateCalender = getCalender(startDate);
        minDateCalender.add(Calendar.DAY_OF_MONTH, -1);
        binding.materialCalenderView.setMinimumDate(minDateCalender);

        if (isFromRaynaTicket && !allowTodaysBooking) {
            Calendar today = Calendar.getInstance();
            disableDates.add(today);
        }

        if (isEventRepeat){
            binding.materialCalenderView.setMaximumDate(getCalender(endDate));
            disableDates = generateDisabledDates(eventStartDate, eventEndDate);
            if (!disableDates.isEmpty()) {
                binding.materialCalenderView.setDisabledDays(disableDates);
                if (!disableDates.isEmpty() && !disableDates.contains(minDateCalender)){
                    binding.materialCalenderView.setMinimumDate(minDateCalender);
                }else {
                    binding.materialCalenderView.setMinimumDate(null);
                }
            }
        }else {
            if (!isCustomVenue) {
                binding.materialCalenderView.setMaximumDate(getCalender(endDate));
                disableDates = generateDisabledDates(startDate, endDate, days);
                if (!disableDates.isEmpty()) {
                    binding.materialCalenderView.setDisabledDays(disableDates);
                    if (!disableDates.isEmpty() && !disableDates.contains(minDateCalender)){
                        binding.materialCalenderView.setMinimumDate(minDateCalender);
                    }else {
                        binding.materialCalenderView.setMinimumDate(null);
                    }
                }
            }
        }


        if (!TextUtils.isEmpty(AlreadTSelectedDate)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(AppConstants.DATEFORMT_EEE_d_MMM_yyyy, Locale.ENGLISH);
            try {
                Date date = dateFormat.parse(AlreadTSelectedDate);
                if (date != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    if (!isCustomVenue && !disableDates.isEmpty() && !disableDates.contains(calendar)) {
                        binding.materialCalenderView.setDate(calendar);
                        showAndSetDate(calendar);
                    } else {
                        if (date.before(calendar.getTime())) {
                            date = calendar.getTime();
                        }
                        binding.materialCalenderView.setDate(date);

                        binding.txtDate.setText(Utils.formatDate(date, AppConstants.DATEFORMT_EEE_d_MMM_yyyy));
                        SelectDate = binding.txtDate.getText().toString();

                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (OutOfDateRangeException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void setListener() {

        binding.materialCalenderView.setOnDayClickListener(eventDay -> {
            if (!eventDay.isEnabled()) {
                return;
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

    private static Calendar getCalender(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private Date getStartDate() {
        return new Date();
    }

    private Date getEndDate() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.add(Calendar.YEAR, 1);
        return currentDate.getTime();
    }

    private String[] getDaysToDisable() {
        List<String> disabledDays = new ArrayList<>(Arrays.asList("mon", "tue", "wed", "thu", "fri", "sat", "sun"));
        List<String> enabledDays;
        if (venueObjectModel != null) {
            enabledDays = venueObjectModel.getTiming().stream().map(p -> p.getDay().toLowerCase()).collect(Collectors.toList());
        } else {
            enabledDays = new ArrayList<>();
        }
        if (enabledDays.contains("all days")) {
            return new String[0];
        }

        enabledDays.forEach(p -> {
            disabledDays.removeIf(v -> v.equalsIgnoreCase(p.trim()));
        });

        return disabledDays.toArray(new String[0]);
    }

    private List<Calendar> generateDisabledDates(Date startDate, Date endDate, String[] disabledDays) {
        List<Calendar> disabledDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        do {
            String dayOfWeek = new SimpleDateFormat("E").format(calendar.getTime());
            if (containsIgnoreCase(disabledDays, dayOfWeek)) {
                disabledDates.add(getCalender(calendar.getTime()));
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        } while (!calendar.getTime().after(endDate));

        String dayOfWeek = new SimpleDateFormat("E").format(endDate);
        if (containsIgnoreCase(disabledDays, dayOfWeek)) {
            disabledDates.add(getCalender(endDate));
        }

        return disabledDates;
    }

    private static boolean containsIgnoreCase(String[] array, String key) {

        for (String element : array) {

            if (element.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    private List<Calendar> generateDisabledDates(String startDateStr, String endDateStr) {
        List<Calendar> disabledDates = new ArrayList<>();

        // Parse the start and end date strings to Date objects
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;

        try {
            startDate = dateFormat.parse(startDateStr);
            endDate = dateFormat.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return disabledDates; // Return empty list if parsing fails
        }

        // Set up calendar to iterate over a reasonable range of dates
        Calendar calendar = Calendar.getInstance();

        // Assuming you want to disable dates for a specific range, let's say a year before and after
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, -1); // Start one year before the start date

        // Loop through all dates for two years (one year before and one year after)
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        endCalendar.add(Calendar.YEAR, 1);

        while (!calendar.after(endCalendar)) {
            // Add the current calendar day if it's outside the specified range
            if (calendar.getTime().before(startDate) || calendar.getTime().after(endDate)) {
                disabledDates.add(getCalendar(calendar.getTime()));
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);  // Move to the next day
        }

        return disabledDates;
    }

    private Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }



    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    // endregion
    // --------------------------------------
}