package com.whosin.app.ui.activites.whosinTicket;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentSelectDateAndTimeSheetBinding;
import com.whosin.app.databinding.RaynaDateSelectItemBinding;
import com.whosin.app.databinding.RaynaTimeSlotNewItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.rayna.RaynaOprationDaysModel;
import com.whosin.app.service.models.rayna.RaynaTimeSlotModel;
import com.whosin.app.service.models.rayna.TourOptionsModel;
import com.whosin.app.service.models.whosinTicketModel.WhosinTicketTourOptionModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.SelectDateAndTimeSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.SelectRaynaDateCalenderSheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;

public class WhosinCustomTicketDateAndTimeSheet extends DialogFragment {

    private FragmentSelectDateAndTimeSheetBinding binding;

    private final SlotAdapter<RaynaTimeSlotModel> slotAdapter = new SlotAdapter<>();

    private DaysListAdapter daysListAdapter;

    public WhosinTicketTourOptionModel tourOptionsModel = null;

    private String selectedDate = "";

    private int selectedSlotPosition = -1;

    public CommanCallback<TourOptionsModel> callback = null;

    private List<String> dates = new ArrayList<>();

    private Call<ContainerListModel<RaynaTimeSlotModel>> slotApiCall;

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
        binding = FragmentSelectDateAndTimeSheetBinding.bind(v);

        binding.tvAvailableDate.setText(Utils.getLangValue("available_dates"));
        binding.tvMoreDate.setText(Utils.getLangValue("more_dates"));
        binding.btnDateChange.setText(Utils.getLangValue("change"));
        binding.tvOptionPickupTime.setText(Utils.getLangValue("available_time_Slots"));
        binding.tvAvailableTimeSlots.setText(Utils.getLangValue("available_time_Slots"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(Utils.getLangValue("no_time_slots_available"));
        binding.btnDone.setText(Utils.getLangValue("done"));

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (tourOptionsModel != null) {
            dates = RaynaTicketManager.shared.raynaTicketDetailModel.getAllDatesFromBookingDates(true, getValidDays(tourOptionsModel.getOperationdays()), null);
        }

        List<Date> tmpDateList = new ArrayList<>();

        if (!dates.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            List<Date> dateList = new ArrayList<>();

            for (String dateStr : dates) {
                try {
                    Date date = dateFormat.parse(dateStr);
                    if (date != null) {
                        dateList.add(date);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            tmpDateList = dateList;

        }

        if (!tmpDateList.isEmpty()){
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tmpDateList.get(0));
        }


        if (tmpDateList.size() == 4) {
            tmpDateList.add(null);
        } else if (!tmpDateList.isEmpty() && tmpDateList.size() > 4) {
            tmpDateList = tmpDateList.subList(0, 4);
            tmpDateList.add(null);
        }

        daysListAdapter = new DaysListAdapter(tmpDateList);
        binding.dateRecycleview.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.dateRecycleview.setAdapter(daysListAdapter);
        daysListAdapter.updateData(tmpDateList);


        binding.timeSlotRecycleview.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.timeSlotRecycleview.setAdapter(slotAdapter);

        if (tourOptionsModel != null && tourOptionsModel.getIsSlot()) {
            requestRaynaTourTimeSlot(getTimeSlotJsonObject());
        }else {
            if (tourOptionsModel != null){
                RaynaTimeSlotModel model = new RaynaTimeSlotModel(tourOptionsModel.getSlotText());
                List<RaynaTimeSlotModel> tmpSlot = new ArrayList<>();
                tmpSlot.add(model);
                selectedSlotPosition = 0;
                slotAdapter.updateData(tmpSlot);

                if (dates.isEmpty()){
                    Utils.hideViews(binding.timeSlotRecycleview,binding.tvAvailableTimeSlots,binding.dateRecycleview,binding.viewLine1);
                    Utils.showViews(binding.emptyPlaceHolderView);
                }

            }
        }



    }

    private void setListener() {

        binding.changeDateLayout.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            openDateSelectSheet();
        });

        binding.btnDone.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);

            if (TextUtils.isEmpty(selectedDate)) {
                return;
            }

            if (tourOptionsModel.getIsSlot()) {
                if (selectedSlotPosition != -1) {
                    setCallback();
                    dismiss();
                }
            } else {
                setCallback();
                dismiss();
            }
        });

        binding.timeSlotRecycleview.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                rv.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


    }

    public int getLayoutRes() {
        return R.layout.fragment_select_date_and_time_sheet;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (slotApiCall != null && !slotApiCall.isCanceled()) {
            slotApiCall.cancel();
        }
    }



    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private JsonObject getTimeSlotJsonObject() {
        JsonObject jsonObject = new JsonObject();
        if (tourOptionsModel != null) {
            jsonObject.addProperty("tourId", tourOptionsModel.getTourId());
            jsonObject.addProperty("tourOptionId", tourOptionsModel.getTourOptionId());
            jsonObject.addProperty("date", selectedDate);
            jsonObject.addProperty("adults", tourOptionsModel.getTmpAdultValue());
            jsonObject.addProperty("childs", tourOptionsModel.getTmpChildValue());
        }
        return jsonObject;
    }

    private void openDateSelectSheet(){
        SelectRaynaDateCalenderSheet selectDateTimeDialog = new SelectRaynaDateCalenderSheet();
        selectDateTimeDialog.dates =  dates;
        selectDateTimeDialog.callback = data -> {
            if (!Utils.isNullOrEmpty(data)) {
                Utils.hideViews(binding.dateRecycleview,binding.moreDateLayout);
                Utils.showViews(binding.changeDateLayout);
                selectedDate = Utils.changeDateFormat(data, "EEE, dd MMM yyyy", "yyyy-MM-dd");
                binding.tvChangeSelectedDate.setText(data);
                requestRaynaTourTimeSlot(getTimeSlotJsonObject());
                updateButtonColour();
            }
        };
        selectDateTimeDialog.show(getChildFragmentManager(), "");
    }

    private void updateButtonColour() {
        if (tourOptionsModel == null) return;
        boolean isDateSelected = !TextUtils.isEmpty(selectedDate);
        boolean isSlotSelected = selectedSlotPosition != -1;

        boolean isSlotRequired = tourOptionsModel.getIsSlot();
        if (isDateSelected && (!isSlotRequired || isSlotSelected)) {
            binding.btnDone.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), R.color.ticket_selected_colour)
            );
        } else {
            binding.btnDone.setBackgroundTintList(null);
            binding.btnDone.setBackground(
                    ContextCompat.getDrawable(requireContext(), R.drawable.not_select_btn_bg)
            );
        }

    }

    private void setCallback() {
        if (callback != null) {
            TourOptionsModel model = new TourOptionsModel();
            model.setTourOptionSelectDate(selectedDate);
            if (tourOptionsModel.getIsSlot() && slotAdapter.getData() != null && !slotAdapter.getData().isEmpty()){
                RaynaTimeSlotModel timeSlotModel = slotAdapter.getData().get(selectedSlotPosition);
                if (timeSlotModel != null) model.setRaynaTimeSlotModel(timeSlotModel);
            }
            callback.onReceive(model);
        }
    }

    private static List<String> getValidDays(RaynaOprationDaysModel operationDays) {
        List<String> validDays = new ArrayList<>();
        if (operationDays != null) {
            if (operationDays.getSunday() == 1) validDays.add("sun");
            if (operationDays.getMonday() == 1) validDays.add("mon");
            if (operationDays.getTuesday() == 1) validDays.add("tue");
            if (operationDays.getWednesday() == 1) validDays.add("wed");
            if (operationDays.getThursday() == 1) validDays.add("thu");
            if (operationDays.getFriday() == 1) validDays.add("fri");
            if (operationDays.getSaturday() == 1) validDays.add("sat");
        } else {
            validDays.addAll(Arrays.asList("sun", "mon", "tue", "wed", "thu", "fri", "sat"));
        }
        return validDays;
    }



    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestRaynaTourTimeSlot(JsonObject jsonObject) {
        if (tourOptionsModel !=null && !tourOptionsModel.getIsSlot()) return;
        selectedSlotPosition = -1;
        updateButtonColour();
        binding.progressView.setVisibility(View.VISIBLE);
        Utils.hideViews(binding.emptyPlaceHolderView, binding.timeSlotRecycleview);
        slotApiCall = DataService.shared(requireActivity()).requestWhosinTicketTourTimeSlotForBottomSheet(jsonObject, new RestCallback<ContainerListModel<RaynaTimeSlotModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaTimeSlotModel> model, String error) {
                binding.progressView.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Graphics.showAlertDialogWithOkButton(requireContext(), getString(R.string.app_name), error);
                    Utils.hideViews(binding.timeSlotRecycleview);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    return;
                }

                List<RaynaTimeSlotModel> timeSlotList = new ArrayList<>();
                if (model.data != null && !model.data.isEmpty()) {
                    timeSlotList.addAll(model.data);
//                    timeSlotList.removeIf(q -> q.getAvailable() == 0);
                }

                if (!timeSlotList.isEmpty()) {
                    slotAdapter.updateData(timeSlotList);
                    Utils.showViews(binding.timeSlotRecycleview);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                } else {
                    Utils.hideViews(binding.timeSlotRecycleview);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }
            }
        });
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
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.rayna_date_select_item));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            Date model = items.get(position);

            if (model != null){

                Utils.hideViews(viewHolder.mBinding.moreDateLayout);
                Utils.showViews(viewHolder.mBinding.dateSetLayout,viewHolder.mBinding.tvDays);

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
                    viewHolder.mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.card_color));
                }


                viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                    Date tmpSelectedDate = items.get(position);
                    SimpleDateFormat selectedFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    selectedDate = selectedFormat.format(tmpSelectedDate);
                    requestRaynaTourTimeSlot(getTimeSlotJsonObject());
                    notifyDataSetChanged();
                });
            }else {
                Utils.showViews(viewHolder.mBinding.moreDateLayout);
                Utils.hideViews(viewHolder.mBinding.dateSetLayout,viewHolder.mBinding.tvDays);

                viewHolder.mBinding.moreDateLayout.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    openDateSelectSheet();
                });
            }


            updateButtonColour();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final RaynaDateSelectItemBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = RaynaDateSelectItemBinding.bind(itemView);
            }
        }
    }

    private class SlotAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.rayna_time_slot_new_item));
        }

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            RaynaTimeSlotModel model = (RaynaTimeSlotModel) getItem(position);

            if (!TextUtils.isEmpty(model.getTimeSlot())){
                viewHolder.binding.tvTimeSlot.setText(model.getTimeSlot());
            }

            viewHolder.binding.tvAvailableTimeSlots.setVisibility(View.GONE);
//            if (model.getAvailable() != 0){
//                viewHolder.binding.tvAvailableTimeSlots.setText("(" + model.getAvailable() + " Available)");
//                viewHolder.binding.tvAvailableTimeSlots.setVisibility(View.VISIBLE);
//            }else {
//                viewHolder.binding.tvAvailableTimeSlots.setVisibility(View.GONE);
//            }

            viewHolder.binding.btnSelectTimeSlot.setOnCheckedChangeListener(null);
            viewHolder.binding.btnSelectTimeSlot.setChecked(selectedSlotPosition == position);

            if (position == selectedSlotPosition) {
                viewHolder.binding.btnSelectTimeSlot.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(viewHolder.binding.btnSelectTimeSlot.getContext(), R.color.ticket_selected_colour)));
            } else {
                viewHolder.binding.btnSelectTimeSlot.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(viewHolder.binding.btnSelectTimeSlot.getContext(), R.color.white)));
            }

            viewHolder.binding.getRoot().setBackground(getResources().getDrawable(position == selectedSlotPosition ? R.drawable.time_slot_select_bg : R.drawable.time_slot_unselected_bg));

            viewHolder.itemView.setOnClickListener(v -> {
                if (tourOptionsModel.getIsSlot()){
                    Utils.preventDoubleClick(v);
                    selectedSlotPosition = position;
                    notifyDataSetChanged();
                    updateButtonColour();
                }
            });

            viewHolder.binding.btnSelectTimeSlot.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked && (tourOptionsModel.getIsSlot())) {
                    selectedSlotPosition = position;
                    notifyDataSetChanged();
                    updateButtonColour();
                }
            });


        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final RaynaTimeSlotNewItemBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = RaynaTimeSlotNewItemBinding.bind(itemView);
            }
        }
    }


    // endregion
    // --------------------------------------

}
