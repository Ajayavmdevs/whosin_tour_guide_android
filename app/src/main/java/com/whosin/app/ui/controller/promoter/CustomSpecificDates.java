package com.whosin.app.ui.controller.promoter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemSpecificDateBinding;
import com.whosin.app.databinding.LayoutSpecificDateBinding;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.PromoterSpecificDateModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.VenueTimingModel;
import com.whosin.app.ui.fragment.PromoterCreateEvent.SelectEventDateBottomSheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CustomSpecificDates extends ConstraintLayout {

    private ItemSpecificDateBinding binding;

    public Activity activity;

    private final DateListsAdapter<PromoterSpecificDateModel> dayListAdapter = new DateListsAdapter<>();

    public FragmentManager fragmentManager;

    public String repeatStartDate = "";

    public String repeatEndDate = "";

    public List<PromoterSpecificDateModel> dateList = new ArrayList<>();

    public List<String> weekDays = new ArrayList<>();

    public CommanCallback<Boolean> callbackFormDate;

    public CommanCallback<Boolean> callbackFormHideAndShow;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public CustomSpecificDates(Context context) {
        this(context, null);
    }

    public CustomSpecificDates(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSpecificDates(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);

        View view = LayoutInflater.from(context).inflate(R.layout.item_specific_date, this);
        binding = ItemSpecificDateBinding.bind(view);

        binding.tvTitle.setText(Utils.getLangValue("dates_summary"));
        binding.tvClearAllTitle.setText(Utils.getLangValue("clear_all"));
        binding.addMoreOptionsTitle.setText(Utils.getLangValue("add_mores_options"));

        binding.weekDaysRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.weekDaysRecycleView.setAdapter(dayListAdapter);


        binding.addMoreLayout.setOnClickListener(v -> {
            addDataInModel();
            callbackFormDate.onReceive(true);
        });

        binding.clearAllBtn.setOnClickListener(view1 -> {
            Utils.preventDoubleClick(view1);
            Graphics.showAlertDialogWithOkCancel(activity,activity.getString(R.string.app_name), Utils.getLangValue("clear_all_date_alert"), Utils.getLangValue("yes"), Utils.getLangValue("cancel"), isConfirmed -> {
                if (isConfirmed) {
                    List<PromoterSpecificDateModel> tmp = new ArrayList<>();
                    tmp.add(new PromoterSpecificDateModel("","",""));
                    dayListAdapter.updateData(tmp);
                    callbackFormDate.onReceive(true);
                }
            });
        });

    }


    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public List<PromoterSpecificDateModel> getSelectedDateList(){
        if (dayListAdapter.getData() == null && dayListAdapter.getData().isEmpty()) return new ArrayList<>();
        return dayListAdapter.getData();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setData(String type){
        if (TextUtils.isEmpty(type)) return;

        dayListAdapter.updateData(new ArrayList<>());
        if (type.equalsIgnoreCase("specific-dates")){
            List<PromoterSpecificDateModel> tmpList = dateList.stream()
                    .map(tmpModel -> {
                        PromoterSpecificDateModel model = new PromoterSpecificDateModel();
                        model.setDate(Utils.changeDateFormat(tmpModel.getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_EEE_d_MMM_yyyy));
                        model.setStartTime(convertTime(tmpModel.getStartTime()));
                        model.setEndTime(convertTime(tmpModel.getEndTime()));
                        return model;
                    })
                    .collect(Collectors.toList());

            if (!tmpList.isEmpty()) {
                dayListAdapter.updateData(tmpList);
            }else {
                List<PromoterSpecificDateModel> tmp = new ArrayList<>();
                tmp.add(new PromoterSpecificDateModel("","",""));
                dayListAdapter.updateData(tmp);
            }

        } else if (type.equalsIgnoreCase("daily")) {
            if (TextUtils.isEmpty(repeatStartDate) && TextUtils.isEmpty(repeatEndDate)) return;

            LocalDate startDate = LocalDate.parse(repeatStartDate);
            LocalDate endDate = LocalDate.parse(repeatEndDate);

            List<PromoterSpecificDateModel> tmpList = getDateRangeList(startDate, endDate).stream()
                    .map(date -> {
                        PromoterSpecificDateModel model = new PromoterSpecificDateModel();
                        model.setDate(Utils.changeDateFormat(date, AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_EEE_d_MMM_yyyy));
                        model.setStartTime(convertTime(PromoterProfileManager.shared.promoterEventObject.get("startTime").getAsString()));
                        model.setEndTime(convertTime(PromoterProfileManager.shared.promoterEventObject.get("endTime").getAsString()));
                        return model;
                    })
                    .collect(Collectors.toList());

            if (!tmpList.isEmpty()) {
                dayListAdapter.updateData(tmpList);
            }else {
                callbackFormHideAndShow.onReceive(true);
            }

        } else if (type.equalsIgnoreCase("weekly")) {
            if (TextUtils.isEmpty(repeatStartDate) && TextUtils.isEmpty(repeatEndDate)) return;

            LocalDate startDate = LocalDate.parse(repeatStartDate);
            LocalDate endDate = LocalDate.parse(repeatEndDate);

            List<PromoterSpecificDateModel> tmpList = getDateRangeListOfWeek(startDate, endDate,weekDays).stream()
                    .map(date -> {
                        PromoterSpecificDateModel model = new PromoterSpecificDateModel();
                        model.setDate(Utils.changeDateFormat(date, AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_EEE_d_MMM_yyyy));
                        model.setStartTime(convertTime(PromoterProfileManager.shared.promoterEventObject.get("startTime").getAsString()));
                        model.setEndTime(convertTime(PromoterProfileManager.shared.promoterEventObject.get("endTime").getAsString()));
                        return model;
                    })
                    .collect(Collectors.toList());

            if (!tmpList.isEmpty()) {
                dayListAdapter.updateData(tmpList);
            }else {
                callbackFormHideAndShow.onReceive(true);
            }
        }
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    public void addDataInModel(){
        dayListAdapter.addItem(new PromoterSpecificDateModel("","",""));
        dayListAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void openCalenderDialog(int position, String date) {
        SelectEventDateBottomSheet selectDateTimeDialog = new SelectEventDateBottomSheet();
        if (!TextUtils.isEmpty(date)){
            selectDateTimeDialog.AlreadTSelectedDate = date;
        }
        selectDateTimeDialog.eventStartDate = repeatStartDate;
        selectDateTimeDialog.eventEndDate = repeatEndDate;
        selectDateTimeDialog.isEventRepeat = true;

//        if (getTiming() != null){
//          selectDateTimeDialog.venueObjectModel = getTiming();
//        }else {
//            selectDateTimeDialog.isCustomVenue = true;
//        }

        selectDateTimeDialog.callback = data -> {
            if (!Utils.isNullOrEmpty(data)) {
                List<PromoterSpecificDateModel> dataList = dayListAdapter.getData();
                if (position >= 0 && position < dataList.size()) {
                    PromoterSpecificDateModel item = dataList.get(position);
                    item.setDate(data);
                    dayListAdapter.notifyItemChanged(position);
                    callbackFormDate.onReceive(true);
                }

//                binding.editGetSocialInformation.setText(data);
            }
        };
        if (fragmentManager != null) {
            selectDateTimeDialog.show(fragmentManager, "1");
        }
    }

    private void openTimeDialog(int position) {
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
            minTillCalendar.add(Calendar.HOUR_OF_DAY, 1);

            TimePickerDialog tillTimePickerDialog = new TimePickerDialog(getContext(), R.style.OrangeDialogTheme, (view1, tillHourOfDay, tillMinute) -> {
                Calendar tillCalendar = Calendar.getInstance();
                tillCalendar.set(Calendar.HOUR_OF_DAY, tillHourOfDay);
                tillCalendar.set(Calendar.MINUTE, tillMinute);
                String tillTime = timeFormat.format(tillCalendar.getTime());


                List<PromoterSpecificDateModel> dataList = dayListAdapter.getData();
                if (position >= 0 && position < dataList.size()) {
                    PromoterSpecificDateModel item = dataList.get(position);
                    item.setStartTime(fromTime);
                    item.setEndTime(tillTime);
                    dayListAdapter.notifyItemChanged(position);
                    callbackFormDate.onReceive(true);
                }

            }, minTillCalendar.get(Calendar.HOUR_OF_DAY), minTillCalendar.get(Calendar.MINUTE), false);

            tillTimePickerDialog.setTitle("Select Till Time");
            tillTimePickerDialog.show();

        }, currentHour, currentMinute, false);

        fromTimePickerDialog.setTitle("Select From Time");
        fromTimePickerDialog.show();
    }

    private static String convertTime(String time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");

        try {
            Date startDate = _24HourSDF.parse(time);

            assert startDate != null;

            return _12HourSDF.format(startDate).toLowerCase();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getDateRangeList(LocalDate start, LocalDate end) {
        List<String> dates = new ArrayList<>();
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            while (!start.isAfter(end)) {
                dates.add(start.format(formatter));
                start = start.plusDays(1);
            }
        }

        return dates;
    }

    public static List<String> getDateRangeListOfWeek(LocalDate start, LocalDate end, List<String> daysOfWeekStrings) {
        List<String> dates = new ArrayList<>();
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }

        List<DayOfWeek> daysOfWeek = new ArrayList<>();
        for (String day : daysOfWeekStrings) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                daysOfWeek.add(DayOfWeek.valueOf(day.toUpperCase(Locale.ENGLISH)));
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            while (!start.isAfter(end)) {
                if (daysOfWeek.contains(start.getDayOfWeek())) {
                    dates.add(start.format(formatter));
                }
                start = start.plusDays(1);
            }
        }

        return dates;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private VenueObjectModel getTiming(){
        LocalDate startDate = LocalDate.parse(repeatStartDate);
        LocalDate endDate = LocalDate.parse(repeatEndDate);
        List<String> getDates = getDateRangeList(startDate,endDate);
        List<VenueTimingModel> venueTimingModel = new ArrayList<>();
        for (String date : getDates) {
           VenueTimingModel model = new VenueTimingModel();
           model.setDay(getDayOfWeek(date));
           venueTimingModel.add(model);

        }
        if (!venueTimingModel.isEmpty()){
            VenueObjectModel venueObjectModel = new VenueObjectModel();
            venueObjectModel.setTiming(venueTimingModel);
            return venueObjectModel;
        }else {
            return null;
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDayOfWeek(String date) {
        LocalDate localDate = LocalDate.parse(date);
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        switch (dayOfWeek) {
            case MONDAY:
                return "mon";
            case TUESDAY:
                return "tue";
            case WEDNESDAY:
                return "wed";
            case THURSDAY:
                return "thu";
            case FRIDAY:
                return "fri";
            case SATURDAY:
                return "sat";
            case SUNDAY:
                return "sun";
            default:
                return "";
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

    private class DateListsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.layout_specific_date));
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            PromoterSpecificDateModel model = (PromoterSpecificDateModel) getItem(position);

            if (!TextUtils.isEmpty(model.getDate())){
                viewHolder.binding.selectDate.setText(model.getDate());
            }else {
                viewHolder.binding.selectDate.setText("");
            }

            if (!TextUtils.isEmpty(model.getStartTime()) && !TextUtils.isEmpty(model.getEndTime())){
                String formattedTimes = String.format("From %s Till %s", model.getStartTime(), model.getEndTime());
                viewHolder.binding.selectTime.setText(formattedTimes);
            }else {
                viewHolder.binding.selectTime.setText("");
            }


            viewHolder.binding.timeConstraint.setOnClickListener(view -> {
                openTimeDialog(position);
            });

            viewHolder.binding.dateConstraint.setOnClickListener(v -> {
                openCalenderDialog(position, model.getDate());
            });

            viewHolder.binding.deleteDates.setOnClickListener(view -> {
                Utils.preventDoubleClick(view);
                if (activity == null) return;
                Graphics.showAlertDialogWithOkCancel(activity,activity.getString(R.string.app_name), "Are you sure want to remove?", "Yes", "No", isConfirmed -> {
                    if (isConfirmed) {
                        dayListAdapter.removeItem(position);
                        dayListAdapter.notifyDataSetChanged();
                        callbackFormDate.onReceive(true);
                    }
                });

            });


        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final LayoutSpecificDateBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = LayoutSpecificDateBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------
}
