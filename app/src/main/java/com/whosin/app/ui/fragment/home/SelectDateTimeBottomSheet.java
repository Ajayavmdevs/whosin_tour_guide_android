package com.whosin.app.ui.fragment.home;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.LayourTimeSlotBinding;
import com.whosin.app.databinding.SelectDateTimeBottomSheetBinding;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.TimeSlotModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.VenueTimingModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class SelectDateTimeBottomSheet extends DialogFragment {

    private SelectDateTimeBottomSheetBinding binding;
    public boolean isCustomVenue = false;
    private CommanCallback<TimeSlotModel> listener;
    public TimeSlotModel selectedTimeSlotModel;
    private TimeSlotAdapter<TimeSlotModel> timeSlotAdapter = new TimeSlotAdapter<>();
    public OffersModel offersModel;
    public VenueObjectModel venueObjectModel;

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

    public void initUi(View view) {
        binding = SelectDateTimeBottomSheetBinding.bind(view);
        binding.timeSlotRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.timeSlotRecycler.setAdapter(timeSlotAdapter);

        Date startDate = getStartDate();
        Date endDate = getEndDate();
        String[] days = getDaysToDisable();
        if (new Date().after(startDate)) {
            startDate = new Date();
        }
        Calendar todayCalender = Calendar.getInstance();

        if (isCustomVenue){
            Calendar minDateCalender = getCalender(startDate);
            minDateCalender.add(Calendar.DAY_OF_MONTH, -1);
            if (minDateCalender.before(todayCalender)) {
                minDateCalender = todayCalender;
            }
            binding.materialCalenderView.setMinimumDate(minDateCalender);
        }else {
            Calendar minDateCalender = getCalender(startDate);
            minDateCalender.add(Calendar.DAY_OF_MONTH, -1);
            binding.materialCalenderView.setMinimumDate(minDateCalender);
            binding.materialCalenderView.setMaximumDate(getCalender(endDate));
            List<Calendar> disableDates = generateDisabledDates(startDate, endDate, days);
            if (!disableDates.isEmpty()) {
                binding.materialCalenderView.setDisabledDays(disableDates);
            }
        }


        handleTimeSlotModel(selectedTimeSlotModel);



    }

    public int getLayoutRes() {
        return R.layout.select_date_time_bottom_sheet;
    }


    public void setListener() {

        binding.materialCalenderView.setOnDayClickListener(eventDay -> {
            if (!eventDay.isEnabled()) {
                return;
            }
            selectedTimeSlotModel = null;
            binding.txtDate.setVisibility(View.VISIBLE);
            binding.txtDateTimeTitle.setVisibility(View.VISIBLE);
            binding.timeSlotRecycler.setVisibility(View.VISIBLE);

            Calendar newObject = eventDay.getCalendar();//Calendar.getInstance();
            Calendar selectedDate = Calendar.getInstance();

            selectedDate.setTimeInMillis(newObject.getTimeInMillis());
//            binding.materialCalenderView.setSelectedDates(Collections.singletonList(newObject));
//            AppExecutors.get().mainThread().execute(() -> {
//                binding.materialCalenderView.setSelectedDates(Collections.singletonList(selectedDate));
//            });

            binding.txtDate.setText(Utils.formatDate(selectedDate.getTime(),"E, d MMM yyyy"));
            String dayName = Utils.formatDate(selectedDate.getTime(),"E");
            Date startDateTime = getStartTime(selectedDate.getTime(), dayName.toLowerCase());
            Date endDateTime = getEndTime(selectedDate.getTime(), dayName.toLowerCase());

            if (endDateTime.before(startDateTime)) {
                selectedDate.add(Calendar.DAY_OF_MONTH, 1);
                endDateTime = getEndTime(selectedDate.getTime(), dayName.toLowerCase());
            }
            List<TimeSlotModel> timeSlotList = new ArrayList<>();
            if (isCustomVenue){
                timeSlotList = generateTimeSlotsForCustomVenue(startDateTime,  90);
            }else {
                timeSlotList = generateTimeSlots(startDateTime, endDateTime, 90);
            }


            List<TimeSlotModel> finalTimeSlotList = timeSlotList;
            AppExecutors.get().mainThread().execute(() -> {
                timeSlotAdapter.updateData(finalTimeSlotList);
                timeSlotAdapter.notifyDataSetChanged();
            });

        });

        binding.btnDone.setOnClickListener(view -> {
            if (selectedTimeSlotModel == null) {
                Toast.makeText(getContext(), "Please select time slot", Toast.LENGTH_SHORT).show();
                return;
            }
            listener.onReceive(selectedTimeSlotModel);
            dismiss();
        });
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

    public void setShareListener(CommanCallback<TimeSlotModel> listener) {
        this.listener = listener;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    public void handleTimeSlotModel(TimeSlotModel selectedTimeSlotModel) {
        if (selectedTimeSlotModel != null) {
            String dateString = selectedTimeSlotModel.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            try {
                Date date = dateFormat.parse(dateString);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                binding.materialCalenderView.setDate(calendar.getTime());

                if (isCustomVenue) {
                    Date selectedDate = calendar.getTime();

                    List<TimeSlotModel> timeSlotList = generateTimeSlotsForCustomVenue(selectedDate, 90);
                    AppExecutors.get().mainThread().execute(() -> {
                        timeSlotAdapter.updateData(timeSlotList);
                        timeSlotAdapter.notifyDataSetChanged();
                    });
                } else {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTimeInMillis(calendar.getTimeInMillis());

                    binding.txtDate.setText(Utils.formatDate(selectedDate.getTime(), "E, d MMM yyyy"));
                    String dayName = Utils.formatDate(selectedDate.getTime(), "E");

                    Date startDateTime = getStartTime(selectedDate.getTime(), dayName.toLowerCase());
                    Date endDateTime = getEndTime(selectedDate.getTime(), dayName.toLowerCase());
                    if (endDateTime.before(startDateTime)) {
                        selectedDate.add(Calendar.DAY_OF_MONTH, 1);
                        endDateTime = getEndTime(selectedDate.getTime(), dayName.toLowerCase());
                    }
                    List<TimeSlotModel> timeSlotList = generateTimeSlots(startDateTime, endDateTime, 90);

                    AppExecutors.get().mainThread().execute(() -> {
                        timeSlotAdapter.updateData(timeSlotList);
                        timeSlotAdapter.notifyDataSetChanged();
                    });
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    private static Calendar getCalender(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private Date getStartDate() {
        if (offersModel != null) {
            try {
                return Utils.stringToDate(offersModel.getStartTime(), AppConstants.DATEFORMAT_LONG_TIME);
            } catch (Exception e) {
            }
        }
        return new Date();
    }

    private Date getEndDate() {
        if (offersModel != null) {
            try {
                return Utils.stringToDate(offersModel.getEndTime(), AppConstants.DATEFORMAT_LONG_TIME);
            } catch (Exception e) {
            }
        }
        Calendar currentDate = Calendar.getInstance();
        currentDate.add(Calendar.YEAR, 1);
        return currentDate.getTime();
    }

    private Date getStartTime(Date originalDate,String day) {
        String fixedTimeString = "00:00";
        if (offersModel != null) {
            Log.d("TAG", "getStartTime: "+offersModel.getStartTime());
            fixedTimeString = Utils.convertDateFormat(offersModel.getStartTime(), AppConstants.DATEFORMAT_LONG_TIME,"HH:mm");
        } else if (venueObjectModel != null) {

            Optional<VenueTimingModel> timeModel = venueObjectModel.getTiming().stream().filter(p -> p.getDay().equalsIgnoreCase(day)).findFirst();
            if (timeModel.isPresent()) {
                fixedTimeString = timeModel.get().getOpeningTime();
                Log.d("TAG", "getOpeningTime: "+fixedTimeString);
            }
        }
        try {
            Date timeDate = Utils.stringToDate(fixedTimeString, "HH:mm");
            originalDate.setHours(timeDate.getHours());
            originalDate.setMinutes(timeDate.getMinutes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return originalDate;
    }

    private Date getEndTime(Date originalDate, String day) {
        String fixedTimeString = "00:00";
        if (offersModel != null) {
            Log.d("TAG", "getEndTime: "+offersModel.getEndTime());
            fixedTimeString = Utils.convertDateFormat(offersModel.getEndTime(), AppConstants.DATEFORMAT_LONG_TIME,"HH:mm");
        } else if (venueObjectModel != null) {
            Optional<VenueTimingModel> timeModel = venueObjectModel.getTiming().stream().filter(p -> p.getDay().equalsIgnoreCase(day)).findFirst();
            if (timeModel.isPresent()) {
                fixedTimeString = timeModel.get().getClosingTime();
            }
        }
        try {
            Date timeDate = Utils.stringToDate(fixedTimeString, "HH:mm");
            originalDate.setHours(timeDate.getHours());
            originalDate.setMinutes(timeDate.getMinutes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return originalDate;
    }

    private List<TimeSlotModel> generateTimeSlots(Date startDate, Date endDate, int timeSlotInMinutes) {

        List<TimeSlotModel> timeSlots = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (calendar.getTime().compareTo(endDate) <= 0) {
            Calendar endTime = (Calendar) calendar.clone();
            endTime.add(Calendar.MINUTE, timeSlotInMinutes);

            if (endDate.compareTo(endTime.getTime()) >= 0) {
                timeSlots.add(new TimeSlotModel(formatDate(calendar.getTime(),AppConstants.DATEFORMAT_SHORT),
                        formatDate(calendar.getTime(), "HH:mm"),
                        formatDate(endTime.getTime(), "HH:mm")
                ));
            } else if (!calendar.getTime().equals(endDate)) {
                timeSlots.add(new TimeSlotModel(
                        formatDate(calendar.getTime(), AppConstants.DATEFORMAT_SHORT),
                        formatDate(calendar.getTime(), "HH:mm"),
                        formatDate(endDate, "HH:mm")
                ));
            }
            calendar = endTime;
        }

        return timeSlots;
    }


    private List<TimeSlotModel> generateTimeSlotsForCustomVenue(Date selectedDate, int timeSlotInMinutes) {
        List<TimeSlotModel> timeSlots = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date currentTime = new Date();
        boolean isToday = selectedDate.getYear() == currentTime.getYear()
                && selectedDate.getMonth() == currentTime.getMonth()
                && selectedDate.getDate() == currentTime.getDate();

        while (calendar.getTime().before(new Date(selectedDate.getTime() + TimeUnit.DAYS.toMillis(1)))) {
            Calendar endTime = (Calendar) calendar.clone();
            endTime.add(Calendar.MINUTE, timeSlotInMinutes);

            if (isToday && calendar.getTime().before(currentTime)) {
                // If the current time slot is in the past and it's today's date, skip it
                calendar.add(Calendar.MINUTE, timeSlotInMinutes);
                continue;
            }

            timeSlots.add(new TimeSlotModel(
                    formatDate(calendar.getTime(), AppConstants.DATEFORMAT_SHORT),
                    formatDate(calendar.getTime(), "HH:mm"),
                    formatDate(endTime.getTime(), "HH:mm")
            ));

            calendar.add(Calendar.MINUTE, timeSlotInMinutes);
        }

        return timeSlots;
    }

    private String formatDate(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        return dateFormat.format(date);
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

    private String[] getDaysToDisable() {
        List<String> disabledDays = new ArrayList<>(Arrays.asList("mon", "tue", "wed", "thu", "fri", "sat", "sun"));
        List<String> enabledDays;
        if (offersModel != null) {
            enabledDays = Arrays.asList(offersModel.getDays().toLowerCase().split(","));
        } else if (venueObjectModel != null) {
            enabledDays = venueObjectModel.getTiming().stream().map(p -> p.getDay().toLowerCase()).collect(Collectors.toList());
        } else {
            enabledDays = new ArrayList<>();
        }
        if (enabledDays.contains("all days")) {
            return new String[0]; // Return an empty array to disable all days
        }
        //disabledDays.removeIf(enabledDays::contains);
//        disabledDays.removeIf( p -> {
//            return enabledDays.contains(p);
//        });
        enabledDays.forEach( p -> {
            disabledDays.removeIf( v -> v.equalsIgnoreCase(p.trim()));
        });

        return disabledDays.toArray(new String[0]);
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------
    public class TimeSlotAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.layour_time_slot));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            TimeSlotModel model = (TimeSlotModel) getItem(position);
            viewHolder.binding.txtStarTime.setText(model.getStartTime());
            viewHolder.binding.txtEndTime.setText(" - " + model.getEndTime());

            viewHolder.binding.getRoot().setOnClickListener(v -> {
                selectedTimeSlotModel = model;
                notifyDataSetChanged();
            });
            if (selectedTimeSlotModel == null) {
                viewHolder.binding.txtEndTime.setTextColor(getResources().getColor(R.color.white));
                viewHolder.binding.txtStarTime.setTextColor(getResources().getColor(R.color.white));
                viewHolder.binding.layoutTimeSlot.setBackgroundResource(R.drawable.time_stroke);
                return;
            }
            if (selectedTimeSlotModel.getStartTime().equalsIgnoreCase(model.getStartTime()) && selectedTimeSlotModel.getEndTime().equalsIgnoreCase(model.getEndTime())) {
                viewHolder.binding.txtEndTime.setTextColor(getResources().getColor(R.color.black));
                viewHolder.binding.txtStarTime.setTextColor(getResources().getColor(R.color.black));
                viewHolder.binding.layoutTimeSlot.setBackgroundResource(R.drawable.selected_time_stroke);
            } else {
                viewHolder.binding.txtEndTime.setTextColor(getResources().getColor(R.color.white));
                viewHolder.binding.txtStarTime.setTextColor(getResources().getColor(R.color.white));
                viewHolder.binding.layoutTimeSlot.setBackgroundResource(R.drawable.time_stroke);
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final LayourTimeSlotBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = LayourTimeSlotBinding.bind(itemView);
            }
        }
    }


}