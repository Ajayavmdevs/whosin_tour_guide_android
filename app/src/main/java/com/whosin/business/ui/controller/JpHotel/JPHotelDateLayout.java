package com.whosin.business.ui.controller.JpHotel;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.JpHotelDateLayoutBinding;
import com.whosin.business.databinding.RaynaDateSelectItemBinding;
import com.whosin.business.ui.activites.raynaTicket.BottomSheets.SelectRaynaDateCalenderSheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JPHotelDateLayout extends ConstraintLayout {

    private JpHotelDateLayoutBinding binding;

    private DaysListAdapter daysListAdapter;

    public Activity activity;

    private String selectedDate = "";

    private List<String> dates = new ArrayList<>();

    public CommanCallback<String> fromDateCallBack;

    public CommanCallback<String> toDateCallBack;

    public FragmentManager fragmentManager;



    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public JPHotelDateLayout(Context context) {
        this(context, null);
    }

    public JPHotelDateLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JPHotelDateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);

        View view = LayoutInflater.from(context).inflate(R.layout.jp_hotel_date_layout, this);
        binding = JpHotelDateLayoutBinding.bind(view);


        binding.btnDateChange.setText(Utils.getLangValue("change"));

        binding.changeDateLayout.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            openDateSelectSheet();
        });

    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public void setUpData(String startDateStr) {
        List<Date> tmpDateList = new ArrayList<>();
        dates.clear();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();

        try {
            // startDate parse karo
            Date startDate = dateFormat.parse(startDateStr);
            if (startDate == null) return;

            calendar.setTime(startDate);

            // 1 year (365 din) ki string dates list banao
            for (int i = 0; i < 365; i++) {
                dates.add(dateFormat.format(calendar.getTime()));
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // String → Date convert karo aur pehli 4 dates lo
        if (!dates.isEmpty()) {
            List<Date> dateList = new ArrayList<>();
            for (int i = 0; i < Math.min(4, dates.size()); i++) {
                try {
                    Date date = dateFormat.parse(dates.get(i));
                    if (date != null) {
                        dateList.add(date);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            tmpDateList = dateList;
        }

        // pehli date ko selectedDate me set karo
        if (!tmpDateList.isEmpty()) {
            selectedDate = dateFormat.format(tmpDateList.get(0));
        }

        if (tmpDateList.size() == 4) {
            tmpDateList.add(null);
        } else if (!tmpDateList.isEmpty() && tmpDateList.size() > 4) {
            tmpDateList = tmpDateList.subList(0, 4);
            tmpDateList.add(null);
        }

        Utils.showViews(binding.dateRecycleview);
        Utils.hideViews(binding.changeDateLayout);

        if (fromDateCallBack != null) {
            fromDateCallBack.onReceive(selectedDate);
        }
        if (toDateCallBack != null) {
            toDateCallBack.onReceive(selectedDate);
        }

        // RecyclerView setup
        daysListAdapter = new DaysListAdapter(tmpDateList);
        binding.dateRecycleview.setLayoutManager(
                new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        );
        binding.dateRecycleview.setAdapter(daysListAdapter);
        daysListAdapter.updateData(tmpDateList);
    }

    public void setConfig(Activity activity,FragmentManager fragmentManager,String title,boolean showSeparator){
        this.activity = activity;
        this.fragmentManager = fragmentManager;

        if (binding != null){
            binding.tvAvailableDate.setText(title);
            binding.viewLine1.setVisibility(showSeparator ? View.VISIBLE : View.GONE);
        }
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void openDateSelectSheet() {
        SelectRaynaDateCalenderSheet selectDateTimeDialog = new SelectRaynaDateCalenderSheet();
        selectDateTimeDialog.dates = dates;
        selectDateTimeDialog.callback = data -> {
            if (!Utils.isNullOrEmpty(data)) {
                Utils.hideViews(binding.dateRecycleview);
                Utils.showViews(binding.changeDateLayout);
                selectedDate = Utils.changeDateFormat(data, "EEE, dd MMM yyyy", "yyyy-MM-dd");
                binding.tvChangeSelectedDate.setText(data);
                if (fromDateCallBack != null) {
                    fromDateCallBack.onReceive(selectedDate);
                }
                if (toDateCallBack != null) {
                    toDateCallBack.onReceive(selectedDate);
                }

//                updateButtonColour();
            }
        };

        if (fragmentManager != null){
            selectDateTimeDialog.show(fragmentManager, "");
        }

    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class DaysListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private List<Date> items;

        public DaysListAdapter(List<Date> items) {
            this.items = items;
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DaysListAdapter.ViewHolder(UiUtils.getViewBy(parent, R.layout.rayna_date_select_item));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            DaysListAdapter.ViewHolder viewHolder = (DaysListAdapter.ViewHolder) holder;

            Date model = items.get(position);

//            viewHolder.mBinding.tvMoreDate.setText(Utils.getLangValue("more_dates"));

            if (model != null) {

                Utils.hideViews(viewHolder.mBinding.moreDateLayout);
                Utils.showViews(viewHolder.mBinding.dateSetLayout, viewHolder.mBinding.tvDays);

                SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);

                viewHolder.mBinding.tvDays.setText(dayFormat.format(model).toUpperCase());
                viewHolder.mBinding.tvDate.setText(dateFormat.format(model));
                viewHolder.mBinding.tvMonth.setText(monthFormat.format(model).toUpperCase());

                String thisItemFormatted = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(model);


                if (thisItemFormatted.equals(selectedDate)) {
                    viewHolder.mBinding.getRoot().setBackgroundResource(R.drawable.selected_date_bg);
                } else {
                    viewHolder.mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(activity, R.color.card_color));
                }


                viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                    Date tmpSelectedDate = items.get(position);
                    SimpleDateFormat selectedFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    selectedDate = selectedFormat.format(tmpSelectedDate);
                    if (fromDateCallBack != null){
                        fromDateCallBack.onReceive(selectedDate);
                    }
                    if (toDateCallBack != null){
                        toDateCallBack.onReceive(selectedDate);
                    }
                    notifyDataSetChanged();
                });
            } else {
                Utils.showViews(viewHolder.mBinding.moreDateLayout);
                Utils.hideViews(viewHolder.mBinding.dateSetLayout, viewHolder.mBinding.tvDays);

                viewHolder.mBinding.moreDateLayout.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    openDateSelectSheet();
                });
            }


//            updateButtonColour();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final RaynaDateSelectItemBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = RaynaDateSelectItemBinding.bind(itemView);
            }

            public void updateLayout(){
                Utils.hideViews(mBinding.moreDateLayout);
                Utils.showViews(mBinding.dateSetLayout,mBinding.tvDays);
            }
        }
    }

    // endregion
    // --------------------------------------
}
