package com.whosin.business.ui.controller;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.business.R;
import com.whosin.business.comman.AppConstants;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.ItemCustomWeekdaysDesignBinding;
import com.whosin.business.databinding.LayoutWeekdaysFieldBinding;
import com.whosin.business.service.models.DateListModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CustomWeekdaysView extends ConstraintLayout {

    private ItemCustomWeekdaysDesignBinding binding;

    private final DateListsAdapter<DateListModel> dayListAdapter = new DateListsAdapter<>();

    private int modelId = 0;

    public List<DateListModel> dateList = new ArrayList<>();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    public CustomWeekdaysView(Context context) {
        this(context, null);
    }

    public CustomWeekdaysView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomWeekdaysView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);

        View view = LayoutInflater.from(context).inflate(R.layout.item_custom_weekdays_design, this);
        binding = ItemCustomWeekdaysDesignBinding.bind(view);

        binding.tvTitle.setText(Utils.getLangValue("im_available_on_the_following_dates"));
        binding.tvAddMoreOptionTitle.setText(Utils.getLangValue("add_mores_options"));

        binding.weekDaysRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.weekDaysRecycleView.setAdapter(dayListAdapter);

        addDataInModel();


        binding.addMoreLayout.setOnClickListener(v -> {
            addDataInModel();
        });

    }


    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public void updateDateAdapter() {
        dayListAdapter.updateData(dateList);
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void addDataInModel(){
        DateListModel model = new DateListModel();
        model.setId(modelId);
        dateList.add(model);
        modelId++;
        updateDateAdapter();
    }


    private void openDateAndTimeDialog(boolean isFromDate , int position , boolean isOpenDialog) {
        Calendar c = Calendar.getInstance();

        long minDate = System.currentTimeMillis() - 1000;

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.OrangeDialogTheme,
                (view, year, monthOfYear, dayOfMonth) -> {

                    DateTimeFormatter formatter = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        formatter = DateTimeFormatter.ofPattern(AppConstants.DATEFORMAT_DD_MM_YYYY, Locale.ENGLISH);
                    }
                    LocalDate selectedLocalDate = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        selectedLocalDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                    }
                    String formattedSelectedDate = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        formattedSelectedDate = formatter.format(selectedLocalDate);
                    }

                    String date = formattedSelectedDate;

                    if (isFromDate){
                        dateList.stream().filter(model -> model.getId() == position).forEach(model -> model.setFromDate(date));
                    }else {
                        dateList.stream().filter(model -> model.getId() == position).forEach(model -> model.setTillDate(date));
                    }

                    updateDateAdapter();


                    if (isOpenDialog){
                        openDateAndTimeDialog(false,position,false);
                    }


                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(minDate);

        datePickerDialog.setOnCancelListener(dialog -> {

        });

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

    private class DateListsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.layout_weekdays_field));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            DateListModel model = (DateListModel) getItem(position);

            viewHolder.binding.fromTitle.setText(Utils.getLangValue("from"));
            viewHolder.binding.tillTitle.setText(Utils.getLangValue("till"));

            viewHolder.binding.tvFromDate.setText(model.getFromDate());
            viewHolder.binding.tvTillDate.setText(model.getTillDate());

            viewHolder.binding.tvFromDate.setOnClickListener(v -> {
                openDateAndTimeDialog(true, position, true);
            });

            viewHolder.binding.tvTillDate.setOnClickListener(v -> {
                openDateAndTimeDialog(false, position, false);
            });

        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final LayoutWeekdaysFieldBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = LayoutWeekdaysFieldBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------

}
