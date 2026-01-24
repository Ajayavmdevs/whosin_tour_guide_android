package com.whosin.app.ui.activites.raynaTicket;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;

import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityRaynaTicketTourOptionBinding;
import com.whosin.app.databinding.ItemRaynaTicketOptionViewBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.manager.LogManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.myCartModels.MyCartTourDetailsModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.models.rayna.RaynaTimeSlotModel;
import com.whosin.app.service.models.rayna.TourOptionDetailModel;
import com.whosin.app.service.models.rayna.TourOptionsModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.ProfileFullScreenImageActivity;
import com.whosin.app.ui.activites.auth.AuthenticationActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.CancellationPolicyBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.RaynaMoreInfoBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.ReadMoreBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.SelectDateAndTimeSheet;
import com.whosin.app.ui.activites.whosinTicket.WhosinTicketTourOptionActivity;


import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RaynaTicketTourOptionActivity extends BaseActivity {

    private ActivityRaynaTicketTourOptionBinding binding;

    private final TicketTourOptionListAdapter<TourOptionsModel> ticketTourOptionListAdapter = new TicketTourOptionListAdapter<>();

    private List<TourOptionsModel> originalTourOptionsList = new ArrayList<>();

    private List<String> positionsOfAdapter = new ArrayList<>();

    private AtomicInteger completedCount = new AtomicInteger(0);

    private AtomicBoolean hasAnyFailed = new AtomicBoolean(false);

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        RaynaTicketManager.shared.activityList.add(activity);
        RaynaTicketManager.shared.selectedTourModel.clear();

        binding.constraintHeader.tvTitle.setText(getValue("tour_options"));
        binding.tvNext.setText(getValue("next"));

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setSupportsChangeAnimations(false);
        binding.tourOptionRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.tourOptionRecyclerView.setAdapter(ticketTourOptionListAdapter);

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
        requestRaynaTourOptions(date);
    }

    @Override
    protected void setListeners() {

        binding.constraintHeader.ivClose.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            finish();
        });


        binding.nextButton.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (positionsOfAdapter.isEmpty()) return;
            for (TourOptionsModel p : RaynaTicketManager.shared.selectedTourModel) {
                int total =  p.getTmpAdultValue() + p.getTmpChildValue() + p.getTmpInfantValue();
                if (total == 0) {
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("pax_required_alert",p.getOptionDetail().getOptionName()));
                    return;
                }
                if (TextUtils.isEmpty(p.getTourOptionSelectDate())){
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("date_required_alert",p.getOptionDetail().getOptionName()));
                    return;
                }
                if (isTimeSlot(p) && p.getRaynaTimeSlotModel() == null){
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("time_required_alert",p.getOptionDetail().getOptionName()));
                    return;
                }

                if (p.isMinPax()){
                    String unit = p.getUnit();
                    if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name),
                            setValue("min_pax_alert",String.valueOf(RaynaTicketManager.shared.raynaTicketDetailModel.getTmpMinPax()),unit,
                                    p.getOptionDetail().getOptionName()));
                    return;
                }
            }

            completedCount.set(0);
            hasAnyFailed.set(false);
            RaynaTicketManager.shared.cancellationObject = new JsonArray();
            processTourPolicies(0);

        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityRaynaTicketTourOptionBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RaynaTicketManager.shared.activityList.remove(activity);
        RaynaTicketManager.shared.selectedTourModel = new ArrayList<>();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    public static <T> T cloneObject(T original) {
        try {
            T copy = (T) original.getClass().getDeclaredConstructor().newInstance();
            for (Field field : original.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(copy, field.get(original)); // shallow copy
            }
            return copy;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void mapTheData(List<TourOptionsModel> data) {
        // Deep copy using reflection
        List<TourOptionsModel> dataCopy = data.stream()
                .map(RaynaTicketTourOptionActivity::cloneObject)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<TourOptionsModel> finalList = dataCopy.stream()
                .collect(Collectors.groupingBy(
                        TourOptionsModel::getTourOptionId,
                        Collectors.mapping(TourOptionsModel::getTransferName, Collectors.toList())
                ))
                .entrySet().stream()
                .map(entry -> {
                    TourOptionsModel model = dataCopy.stream()
                            .filter(p -> p.getTourOptionId().equals(entry.getKey()))
                            .findFirst()
                            .orElse(null);
                    if (model != null) {
                        model.setTransTypeList(new ArrayList<>(entry.getValue())); // replace list
                    }
                    return model;
                })
                .filter(Objects::nonNull)
//                .sorted(Comparator.comparing(model1 -> model1 != null ? model1.getFinalAmount() : null))
                .collect(Collectors.toList());

        if (!RaynaTicketManager.shared.selectedTourModel.isEmpty()) {
            List<Integer> selectedIds = RaynaTicketManager.shared.selectedTourModel.stream().map(TourOptionsModel::getTourOptionId).collect(Collectors.toList());
            finalList.forEach(q -> {
                RaynaTicketManager.shared.selectedTourModel.stream()
                        .filter(p -> Objects.equals(p.getTourOptionId(), q.getTourOptionId()))
                        .findFirst()
                        .ifPresent(model -> {
                            q.updateValueForTourOption(model, false);
                            if (q.getSelectedTransType() != 0){
                                TourOptionsModel optionalModel = getOptionalModel(q, q.getSelectedTransType());
                                if (optionalModel != null) {
                                    q.updateValueOnTransType(optionalModel);
                                }
                            }
                        });

            });

            RaynaTicketManager.shared.selectedTourModel.clear();
            RaynaTicketManager.shared.selectedTourModel = finalList.stream().filter(model -> selectedIds.contains(model.getTourOptionId())).collect(Collectors.toList());
        }

        finalList = finalList.stream().sorted(Comparator.comparing(TourOptionsModel::getAdultPrice, Comparator.nullsLast(Float::compareTo))).collect(Collectors.toList());

        ticketTourOptionListAdapter.updateData(finalList);
        updateButtonValue();
    }

    private void updateButtonColor() {
        int color = ContextCompat.getColor(activity, !positionsOfAdapter.isEmpty()  ? R.color.brand_pink : R.color.gray);
        binding.nextButton.setBackgroundColor(color);
    }

    private int getTransferId(int tourOptionId, String transType) {
        return originalTourOptionsList.stream()
                .filter(p -> p.getTourOptionId() == tourOptionId && transType.equals(p.getTransferName()))
                .map(TourOptionsModel::getTransferId)
                .findFirst()
                .orElse(0);
    }

    private boolean isTimeSlot(TourOptionsModel model) {
        return originalTourOptionsList.stream()
                .filter(p -> Objects.equals(p.getTourOptionId(), model.getTourOptionId()) && model.getTransType().equals(p.getTransferName()))
                .map(TourOptionsModel::getIsSlot)
                .findFirst()
                .orElse(false);
    }

    private TourOptionsModel getOptionalModel(TourOptionsModel model,int pos) {
        String type = model.getTransTypeList().get(pos);
        List<TourOptionsModel> touList = originalTourOptionsList.stream().filter(p -> Objects.equals(p.getTourOptionId(), model.getTourOptionId())).collect(Collectors.toList());
        for (TourOptionsModel tmpModel : touList){
            if (tmpModel.getTransferName().equals(type)){
                return tmpModel;
            }
        }
        return null;
    }

    private void updateButtonValue() {
        if (RaynaTicketManager.shared.selectedTourModel.isEmpty()) {
            binding.tvNext.setText(getValue("next"));
            binding.tvPrice.setVisibility(View.GONE);
        } else {
            binding.tvPrice.setVisibility(View.VISIBLE);
            float adultAmount = 0f;
            float childAmount = 0f;
            float infantAmount = 0f;

            for (TourOptionsModel q : RaynaTicketManager.shared.selectedTourModel) {
                adultAmount += q.updateAdultPrices();
                childAmount += q.updateChildPrices();
                infantAmount += q.updateInfantPrices();
            }

            float total = adultAmount + childAmount + infantAmount;
            Utils.setStyledText(activity,binding.tvPrice,Utils.roundFloatValue(total));
            if (total == 0.0){
                binding.tvPrice.setVisibility(View.GONE);
            }else {
                binding.tvPrice.setVisibility(View.VISIBLE);
            }
        }

        updateButtonColor();
    }

    private void processTourPolicies(final int index) {
        List<TourOptionsModel> selectedTours = RaynaTicketManager.shared.selectedTourModel;
        if (index >= selectedTours.size() || hasAnyFailed.get()) {
            if (!hasAnyFailed.get() && completedCount.get() == selectedTours.size()) {
                startActivity(new Intent(activity, RaynaParticipantDetailActivity.class));
            }
            return;
        }

        // Process the current tour
        TourOptionsModel tour = selectedTours.get(index);
        requestRaynaTourPolicy(tour, index);
    }

    private boolean isSelectedAnyAdult() {
        if (RaynaTicketManager.shared.selectedTourModel.isEmpty()) {
            return false;
        } else {
            int total = 0;
            for (TourOptionsModel p : RaynaTicketManager.shared.selectedTourModel) {
                total = total + (p.getTmpAdultValue() + p.getTmpChildValue() + p.getTmpInfantValue());

            }
            return total != 0;
        }
    }

    private void hapticFeedback() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }

    private JsonObject getTimeSlotJsonObject(TourOptionsModel tourOptionsModel) {
        JsonObject jsonObject = new JsonObject();
        if (tourOptionsModel != null){
            jsonObject.addProperty("tourId", tourOptionsModel.getTourId());
            jsonObject.addProperty("tourOptionId", tourOptionsModel.getTourOptionId());
            jsonObject.addProperty("contractId", RaynaTicketManager.shared.getContractId());
            jsonObject.addProperty("transferId", tourOptionsModel.getSelectedTransferId());
            jsonObject.addProperty("date", tourOptionsModel.getTourOptionSelectDate());
            jsonObject.addProperty("noOfAdult", tourOptionsModel.getTmpAdultValue());
            jsonObject.addProperty("noOfChild", tourOptionsModel.getTmpChildValue());
            jsonObject.addProperty("noOfInfant", tourOptionsModel.getTmpInfantValue());
        }
        return jsonObject;
    }

    private String getCleanOptionId(Object optionId) {
        if (optionId instanceof Number) {
            double val = ((Number) optionId).doubleValue();
            return (val == Math.floor(val)) ? String.valueOf((int) val) : String.valueOf(val);
        } else {
            return String.valueOf(optionId);
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestRaynaTourOptions(String selectedDate) {
        JsonObject object = new JsonObject();
        object.addProperty("tourId", RaynaTicketManager.shared.getTourId());
        object.addProperty("contractId", RaynaTicketManager.shared.getContractId());
        object.addProperty("date", selectedDate);
        object.addProperty("noOfAdult", 1);
        object.addProperty("noOfChild", 0);
        object.addProperty("noOfInfant", 0);
        showProgress();
        DataService.shared(activity).requestRaynaTourOptions(object, new RestCallback<ContainerListModel<TourOptionsModel>>(this) {
            @Override
            public void result(ContainerListModel<TourOptionsModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    if (error.contains("Session expired, please login again!")){
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("session_expired"), aBoolean -> {
                            if (aBoolean) {
                                showProgress();
                                SessionManager.shared.logout(activity, (success, log_out_error) -> {
                                    hideProgress();
                                    if (!Utils.isNullOrEmpty(log_out_error)) {
                                        Toast.makeText(activity, log_out_error, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    startActivity( new Intent( activity, AuthenticationActivity.class ).addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK ).addFlags( Intent.FLAG_ACTIVITY_NEW_TASK ) );
                                    finish();
                                });
                            }
                        });
                    }else {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), error);
                        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                        binding.tourOptionRecyclerView.setVisibility(View.GONE);
                    }

                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    model.data.sort(
                            Comparator.comparing(
                                    TourOptionsModel::getOrder,
                                    Comparator.nullsLast(Integer::compareTo)
                            )
                    );
                    originalTourOptionsList.clear();
                    originalTourOptionsList.addAll(model.data);

                    mapTheData(new ArrayList<>(model.data));
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    binding.tourOptionRecyclerView.setVisibility(View.VISIBLE);

                } else {
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    binding.tourOptionRecyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void requestRaynaTourPolicy(TourOptionsModel tourOptionsModel,final int currentIndex) {
        showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("tourId", tourOptionsModel.getTourId());
        jsonObject.addProperty("tourOptionId", tourOptionsModel.getTourOptionId());
        jsonObject.addProperty("contractId", RaynaTicketManager.shared.getContractId());
        jsonObject.addProperty("transferId", tourOptionsModel.getTransferId());
        jsonObject.addProperty("date", tourOptionsModel.getTourOptionSelectDate());
        jsonObject.addProperty("time", tourOptionsModel.getStartTime());
        jsonObject.addProperty("noOfAdult", tourOptionsModel.getTmpAdultValue());
        jsonObject.addProperty("noOfChild", tourOptionsModel.getTmpChildValue());
        jsonObject.addProperty("noOfInfant", tourOptionsModel.getTmpInfantValue());
        DataService.shared(activity).requestRaynaTourPolicy(jsonObject, new RestCallback<ContainerListModel<TourOptionsModel>>(this) {
            @Override
            public void result(ContainerListModel<TourOptionsModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    String tmpError = "";
                    if (error.contains("You cannot book this tour on selected date due to cutoff time.")){
                        tmpError = error + " for " + tourOptionsModel.getOptionDetail().getOptionName();
                    }else {
                        tmpError = error;
                    }
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), tmpError);
                    RaynaTicketManager.shared.cancellationObject = new JsonArray();
                    hasAnyFailed.set(true);
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    model.data.forEach(model1 -> {
                        JsonObject object = new JsonObject();
                        object.addProperty("tourId", model1.getTourId());
                        object.addProperty("optionId", getCleanOptionId(model1.getOptionId()));
                        object.addProperty("fromDate", model1.getFromDate());
                        object.addProperty("toDate", model1.getToDate());
                        object.addProperty("percentage", model1.getPercentage());
                        RaynaTicketManager.shared.cancellationObject.add(object);
                    });
                }

                completedCount.incrementAndGet();
                processTourPolicies(currentIndex + 1);

            }
        });
    }

    private void requestRaynaTourTimeSlot(TourOptionsModel tourOptionsModel,JsonObject jsonObject, CommanCallback<Boolean> callback) {
        DataService.shared(activity).requestRaynaTourTimeSlot(jsonObject, new RestCallback<ContainerListModel<RaynaTimeSlotModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaTimeSlotModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    callback.onReceive(false);
                    return;
                }

                List<RaynaTimeSlotModel> timeSlotList = new ArrayList<>();
                if (model.data != null && !model.data.isEmpty()) {
                    timeSlotList.addAll(model.data);
                    timeSlotList.removeIf(q -> q.getAvailable() == 0);
                }

                if (!timeSlotList.isEmpty()) {
                    if (tourOptionsModel.getRaynaTimeSlotModel() != null){
                        boolean isAnyMatch = timeSlotList.stream().anyMatch(p ->
                                p.getTourOptionId() == tourOptionsModel.getTourOptionId() &&
                                        p.getTimeSlotId().equals(tourOptionsModel.getRaynaTimeSlotModel().getTimeSlotId())
                        );

                        callback.onReceive(isAnyMatch);
                    }else {
                        callback.onReceive(false);
                    }
                }else{
                    callback.onReceive(false);
                }
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class TicketTourOptionListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private boolean isInitialStateApplied = false;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_rayna_ticket_option_view));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            TourOptionsModel model = (TourOptionsModel) getItem(position);
            boolean isLastItem = position == getItemCount() - 1;
            if (model == null) return;

            if (!isInitialStateApplied) {
                for (int i = 0; i < getItemCount(); i++) {
                    TourOptionsModel m = (TourOptionsModel) getItem(i);
                    if (m != null) {
                        m.setExpanded(i == 0);
                    }
                }
                isInitialStateApplied = true;
            }

            viewHolder.binding.selectTourDateLayout.setHint(getValue("date_time_placeHolder"));
            viewHolder.binding.tourTimeSlotTv.setHint(getValue("time_slot"));
            viewHolder.binding.btnMoreInfoView.setText(getValue("Inclusions & Details"));

            viewHolder.binding.horizontalContainer.setOnClickListener(v -> {
                model.setExpanded(!model.isExpanded());
                notifyItemChanged(position);
            });

            viewHolder.setUpAdultChildData(model);

            viewHolder.setOnClickListeners(model);

            Utils.updateNoteText(RaynaTicketManager.shared.raynaTicketDetailModel.getTmpMinPax(),RaynaTicketManager.shared.raynaTicketDetailModel.getTmpMaxPax(),viewHolder.binding.tvNote,model.getNotes());

            viewHolder.binding.tvOptionName.setText(model.getOptionDetail().getOptionName());
//            viewHolder.binding.tvOptionDescription.setText(model.getOptionDetail().getOptionDescription());

            viewHolder.binding.selectTourDateLayout.setText(model.getTourOptionSelectDate());
            if (model.getRaynaTimeSlotModel() == null && !TextUtils.isEmpty(model.getTourOptionSelectDate()) && !TextUtils.isEmpty(model.getSlotText())) {
                viewHolder.binding.viewLine1.setVisibility(View.VISIBLE);
                viewHolder.binding.timeSlotLayout.setVisibility(View.VISIBLE);
                viewHolder.binding.tourTimeSlotTv.setText(model.getSlotText());
            } else {
                if (TextUtils.isEmpty(model.getTourOptionSelectDate())) {
                    viewHolder.binding.viewLine1.setVisibility(View.GONE);
                    viewHolder.binding.timeSlotLayout.setVisibility(View.GONE);
                } else {
                    viewHolder.binding.viewLine1.setVisibility(View.VISIBLE);
                    viewHolder.binding.timeSlotLayout.setVisibility(View.VISIBLE);
                    if (model.getRaynaTimeSlotModel() != null){
                        viewHolder.binding.tourTimeSlotTv.setText(model.getRaynaTimeSlotModel().getTimeSlot());
                    }

                }
            }

//            if (model.getOptionDetail() != null && !TextUtils.isEmpty(model.getOptionDetail().getOptionDescription())) {
//                viewHolder.binding.tvOptionDescription.setVisibility(View.VISIBLE);
//                Utils.addSeeMore(viewHolder.binding.tvOptionDescription, Html.fromHtml(model.getOptionDetail().getOptionDescription()), 1, "... " + getValue("see_more"), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ReadMoreBottomSheet bottomSheet = new ReadMoreBottomSheet();
//                        bottomSheet.title = getValue("description");
//                        bottomSheet.formattedDescription = model.getOptionDetail().getOptionDescription();
//                        bottomSheet.show(getSupportFragmentManager(),"");
//                    }
//                });
//            } else {
//                viewHolder.binding.tvOptionDescription.setVisibility(View.GONE);
//            }


            viewHolder.loadOptionImage(model.getOptionDetail());

            if (model.getTransTypeList() != null && !model.getTransTypeList().isEmpty() && model.getTransTypeList().size() > 1){
                viewHolder.binding.selectSpinnerLayout.setVisibility(View.VISIBLE);

                // Drop - Drown
                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.item_spinner_text_for_rayna, model.transTypeList);

                adapter.setDropDownViewResource(R.layout.item_spinner_text_for_rayna);
                viewHolder.binding.spinnerOptions.setAdapter(adapter);

                viewHolder.binding.spinnerOptions.setOnItemSelectedListener(null);

                viewHolder.binding.spinnerOptions.setSelection(model.getSelectedTransType());


                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    viewHolder.binding.spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                            model.setSelectedTransferId(getTransferId(model.getTourOptionId(),model.getTransType()));
                            model.setSelectedTransType(pos);
                            model.setMessage("");
                            model.setPickUpLocation("");

                            TourOptionsModel optionalModel = getOptionalModel(model, pos);
                            if (optionalModel != null) {
                                model.updateValueOnTransType(optionalModel);
                                if (model.getIsSlot()){
                                    requestRaynaTourTimeSlot(model, getTimeSlotJsonObject(model), data -> {
                                        if (!data) {
                                            model.setRaynaTimeSlotModel(null);
                                            viewHolder.binding.viewLine1.setVisibility(View.GONE);
                                            viewHolder.binding.timeSlotLayout.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }

                            viewHolder.setUpAdultChildData(model);
                            updateButtonValue();
                            viewHolder.setOnClickListeners(model);

                            boolean isPresent = positionsOfAdapter.stream().anyMatch(item -> item.equalsIgnoreCase(String.valueOf(position)));
                            if (!isPresent) {
                                positionsOfAdapter.add(String.valueOf(position));
                                RaynaTicketManager.shared.selectedTourModel.add(model);
                                notifyItemChanged(position);
                                updateButtonValue();
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }, 1000);

            }else {
                viewHolder.binding.selectSpinnerLayout.setVisibility(View.GONE);
            }


            if (model.getSelectedTransferId() == 0){
                model.setSelectedTransferId(model.getTransferId());
            }

            if (model.getDiscount() != null && model.getDiscount() > 0){
                viewHolder.binding.discountTagLayout.setVisibility(View.VISIBLE);
                if ("flat".equalsIgnoreCase(model.getDiscountType())) {
                    Utils.setStyledText(activity, viewHolder.binding.tvDiscountTag, model.getDiscountText());
                } else {
                    viewHolder.binding.tvDiscountTag.setText(model.getDiscountText());
                }
            }else {
                viewHolder.binding.discountTagLayout.setVisibility(View.GONE);
                viewHolder.binding.tvDiscountTag.setText("");
            }


            viewHolder.updatePaxBg(model);

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.10f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }
            applyExpandState(viewHolder, model);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemRaynaTicketOptionViewBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemRaynaTicketOptionViewBinding.bind(itemView);
            }


            private void loadOptionImage(TourOptionDetailModel model){

                RaynaTicketDetailModel ticketDetailModel =   RaynaTicketManager.shared.raynaTicketDetailModel;

                List<String> images = model.getImages();
                if (images == null || images.isEmpty()) {
                    images = ticketDetailModel != null ? ticketDetailModel.getImages() : null;
                }

                if (images != null && !images.isEmpty()) {
                    for (String image : images) {
                        if (!Utils.isVideo(image)) {
                            Graphics.loadImage(image, binding.ticketOptionImage);
                            binding.ticketOptionImage.setOnClickListener(v -> {
                                if (!TextUtils.isEmpty(image)) {
                                    Intent intent = new Intent(activity, ProfileFullScreenImageActivity.class);
                                    intent.putExtra(ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, image);
                                    startActivity(intent);
                                }
                            });
                            break;
                        }
                    }
                }
            }

            private void setUpAdultChildData(TourOptionsModel model) {

                activity.runOnUiThread(() -> {

                    binding.tvAdultsTitle.setText(getValue("adults_title"));
                    binding.tvChildTitle.setText(getValue("children_title"));
                    binding.tvInfantsTitle.setText(getValue("infant_title"));

                    binding.addQuantityAdult.setVisibility(View.VISIBLE);
                    binding.addQuantityChild.setVisibility(!model.getDisableChild() ? View.VISIBLE : View.GONE);
                    binding.addQuantityInfants.setVisibility(!model.getDisableInfant() ? View.VISIBLE : View.GONE);

                    binding.tvTotalAdult.setText(String.valueOf(model.getTmpAdultValue()));
                    binding.tvTotalChild.setText(String.valueOf(model.getTmpChildValue()));
                    binding.tvTotalInfants.setText(String.valueOf(model.getTmpInfantValue()));

                    updateAdultChildInfantValue(model);
                    if (model.getOptionDetail().getAdultAge() != null  && !model.getOptionDetail().getAdultAge().isEmpty()) {
                        binding.adultAge.setText("(" + model.getOptionDetail().getAdultAge() + ")");
                    } else {
                        binding.adultAge.setText("");
                    }
                    if (model.getOptionDetail().getChildAge() != null && !model.getOptionDetail().getChildAge().isEmpty()) {
                        binding.childrenAge.setText("(" +model.getOptionDetail().getChildAge()+ ")");
                    } else {
                        binding.childrenAge.setText("");
                    }
                    if (model.getOptionDetail().getInfantAge() != null && !model.getOptionDetail().getInfantAge().isEmpty()) {
                        binding.infantAge.setText("(" + model.getOptionDetail().getInfantAge() + ")");
                    } else {
                        binding.infantAge.setText("");
                    }

                    if (!Utils.isNullOrEmpty(model.getAdultTitle()))
                        binding.tvAdultsTitle.setText(model.getAdultTitle());
                    if (!Utils.isNullOrEmpty(model.getChildTitle()))
                        binding.tvChildTitle.setText(model.getChildTitle());
                    if (!Utils.isNullOrEmpty(model.getInfantTitle()))
                        binding.tvInfantsTitle.setText(model.getInfantTitle());

                    Utils.setTextOrHide(binding.adultDescription, model.getAdultDescription());
                    Utils.setTextOrHide(binding.childDescription, model.getChildDescription());
                    Utils.setTextOrHide(binding.infantDescription, model.getInfantDescription());



                    shouldHideDiscount(binding.adultPriceWithoutDiscount, binding.tvAdultPrice,model.getWithoutDiscountAdultPrice(), model.getAdultPrice());
                    shouldHideDiscount(binding.childPriceWithoutDiscount, binding.tvChildPrice,model.getWithoutDiscountChildPrice(), model.getChildPrice());
                    shouldHideDiscount(binding.infantPriceWithoutDiscount,binding.tvInfantPrice, model.getWithoutDiscountInfantPrice(), model.getInfantPrice());

                });

            }

            private void updateCount(TextView textView, boolean isIncrement,TourOptionsModel model) {
                String unit = model.getUnit();
                if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                if (model.isMaxPax() && isIncrement){
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("max_pax_alert",String.valueOf(RaynaTicketManager.shared.raynaTicketDetailModel.getTmpMaxPax()),unit));
                    return;
                }

//                int currentValue = Integer.parseInt(textView.getText().toString());
                int currentValue = Utils.getNumericValue(textView);

                if (isIncrement) {
                    if (model.isFirestTimeUpdate() && RaynaTicketManager.shared.raynaTicketDetailModel.getTmpMinPax() != 0) {
                        currentValue = RaynaTicketManager.shared.raynaTicketDetailModel.getTmpMinPax();
                        model.setFirestTimeUpdate(false);
                    }else {
                        currentValue++;
                    }
                } else if (currentValue >  0){
                    currentValue--;
                }

                textView.setText(String.valueOf(currentValue));

                if (textView == binding.tvTotalAdult) {
                    model.setTmpAdultValue(currentValue);
                } else if (textView == binding.tvTotalChild) {
                    model.setTmpChildValue(currentValue);
                }


                if (textView == binding.tvTotalAdult) {
                    int infantCount = Utils.getNumericValue(binding.tvTotalInfants);
                    int newMax = (int) Math.ceil(currentValue / 4.0);
                    if (infantCount > newMax) {
                        binding.tvTotalInfants.setText(String.valueOf(newMax));
                        model.setTmpInfantValue(newMax);
                    }
                }

                boolean isPresent = positionsOfAdapter.stream().anyMatch(item -> item.equalsIgnoreCase(String.valueOf(getAdapterPosition())));
                if (!isPresent) {
                    positionsOfAdapter.add(String.valueOf(getAdapterPosition()));
                    RaynaTicketManager.shared.selectedTourModel.add(model);
                    notifyItemChanged(getAdapterPosition());
                }

                if (!model.hasAtLeastOneMember()){
                    refreshModel(model);
                    positionsOfAdapter.remove(String.valueOf(getAdapterPosition()));
                    RaynaTicketManager.shared.selectedTourModel.remove(model);
                    model.setTourOptionSelectDate("");
                    binding.dateTimeLayout.setBackground(ContextCompat.getDrawable(activity,R.drawable.tour_option_spinner_stock_bg));
                    notifyItemChanged(getAdapterPosition());
                }

                updateAdultChildInfantValue(model);
                updateButtonValue();
                hapticFeedback();
                updatePaxBg(model);
            }

            private void updateCountForInfant(TextView textView, boolean isIncrement,TourOptionsModel model) {
                String unit = model.getUnit();
                if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                if (model.isMaxPax() && isIncrement){
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("max_pax_alert",String.valueOf(RaynaTicketManager.shared.raynaTicketDetailModel.getTmpMaxPax()),unit));
                    return;
                }
//                int currentValue = Integer.parseInt(textView.getText().toString());
//                int adultCount = Integer.parseInt(binding.tvTotalAdult.getText().toString());
                int currentValue = Utils.getNumericValue(textView);
                int adultCount = Utils.getNumericValue(binding.tvTotalAdult);
                if (adultCount == 0) return;
                int maxInfantsAllowed = (int) Math.ceil(adultCount / 4.0);

                if (isIncrement && currentValue < maxInfantsAllowed) {
                    currentValue++;
                } else if (!isIncrement && currentValue > 0) {
                    currentValue--;
                }

                if (isIncrement && currentValue == 0){
                    return;
                }

                textView.setText(String.valueOf(currentValue));
                model.setTmpInfantValue(currentValue);

                boolean isPresent = positionsOfAdapter.stream().anyMatch(item -> item.equalsIgnoreCase(String.valueOf(getAdapterPosition())));
                if (!isPresent) {
                    positionsOfAdapter.add(String.valueOf(getAdapterPosition()));
                    RaynaTicketManager.shared.selectedTourModel.add(model);
                    notifyItemChanged(getAdapterPosition());
                }

                if (!model.hasAtLeastOneMember()){
                    refreshModel(model);
                    positionsOfAdapter.remove(String.valueOf(getAdapterPosition()));
                    RaynaTicketManager.shared.selectedTourModel.remove(model);
                    notifyItemChanged(getAdapterPosition());
                }

                updateAdultChildInfantValue(model);
                updateButtonValue();
                hapticFeedback();
                updatePaxBg(model);
            }

            private void setOnClickListeners(TourOptionsModel model){
                // Adults
                binding.ivMinusAdult.setOnClickListener(view -> updateCount(binding.tvTotalAdult, false,model));
                binding.ivPlusAdult.setOnClickListener(view -> updateCount(binding.tvTotalAdult, true,model));

                // Children
                binding.ivMinusChild.setOnClickListener(view -> updateCount(binding.tvTotalChild, false,model));
                binding.ivPlusChild.setOnClickListener(view -> updateCount(binding.tvTotalChild, true,model));

                // Infants
                binding.ivMinusInfants.setOnClickListener(view -> updateCountForInfant(binding.tvTotalInfants, false,model));
                binding.ivPlusInfants.setOnClickListener(view -> updateCountForInfant(binding.tvTotalInfants, true,model));

                // Select Date
                binding.dateTimeLayout.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    if (!model.hasAtLeastOneMember()) {
                        String message = setValue("min_pax_required_alert", String.valueOf(model.getUnit()));
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), message);
                        return;
                    }
                    SelectDateAndTimeSheet selectDateTimeDialog = new SelectDateAndTimeSheet();
                    selectDateTimeDialog.tourOptionsModel = model;
                    selectDateTimeDialog.callback = data -> {
                        if (data != null) {

                            String oldDate = model.getTourOptionSelectDate();
                            RaynaTimeSlotModel oldSlot = model.getRaynaTimeSlotModel();
                            String oldSlotId = (oldSlot != null) ? oldSlot.getTimeSlotId() : "";

                            binding.dateTimeLayout.setBackground(ContextCompat.getDrawable(activity,R.drawable.selected_tour_option_people_stock_bg));


                            model.setTourOptionSelectDate(data.getTourOptionSelectDate());
                            binding.selectTourDateLayout.setText(data.getTourOptionSelectDate());


                            if (model.getIsSlot() && data.getRaynaTimeSlotModel() != null){
                                model.setRaynaTimeSlotModel(data.getRaynaTimeSlotModel());
                            }

                            if (model.getRaynaTimeSlotModel() == null) {
                                binding.viewLine1.setVisibility(View.VISIBLE);
                                binding.timeSlotLayout.setVisibility(View.VISIBLE);
                                binding.tourTimeSlotTv.setText(model.getSlotText());
                            } else {
                                binding.viewLine1.setVisibility(View.VISIBLE);
                                binding.timeSlotLayout.setVisibility(View.VISIBLE);
                                binding.tourTimeSlotTv.setText(model.getRaynaTimeSlotModel().getTimeSlot());
                            }

                            boolean isPresent = positionsOfAdapter.stream().anyMatch(item -> item.equalsIgnoreCase(String.valueOf(getAdapterPosition())));
                            if (!isPresent && model.hasAtLeastOneMember()) {
                                positionsOfAdapter.add(String.valueOf(getAdapterPosition()));
                                RaynaTicketManager.shared.selectedTourModel.add(model);
                            }

                            boolean isDateChanged = !TextUtils.equals(oldDate, data.getTourOptionSelectDate());
                            boolean isSlotChanged = false;
                            if (model.getIsSlot() && data.getRaynaTimeSlotModel() != null) {
                                isSlotChanged = !TextUtils.equals(oldSlotId, data.getRaynaTimeSlotModel().getTimeSlotId());
                            }

                            if (isDateChanged || isSlotChanged) {
                                if (RaynaTicketManager.shared.selectedAddonModels != null) {
                                    List<TourOptionsModel> optionAddons = model.getAddons();
                                    if (optionAddons != null && !optionAddons.isEmpty()) {
                                        java.util.Set<String> ids = new java.util.HashSet<>();
                                        for (TourOptionsModel a : optionAddons) {
                                            if (a != null && a.get_id() != null) {
                                                ids.add(a.get_id());
                                                a.setTmpAdultValue(0);
                                                a.setTmpChildValue(0);
                                                a.setTmpInfantValue(0);
                                                a.setRaynaTimeSlotModel(null);
                                                a.setTourOptionSelectDate("");
                                            }
                                        }
                                        java.util.Iterator<TourOptionsModel> it = RaynaTicketManager.shared.selectedAddonModels.iterator();
                                        while (it.hasNext()) {
                                            TourOptionsModel selected = it.next();
                                            if (selected != null && ids.contains(selected.get_id())) {
                                                it.remove();
                                            }
                                        }
                                    }
                                }
                            }
                            notifyItemChanged(getAdapterPosition());
                            updateButtonValue();

                            requestRaynaTourOptions(model.getTourOptionSelectDate());
                        }
                    };
                    selectDateTimeDialog.show(getSupportFragmentManager(), "1");
                });

                // More Info
                binding.btnMoreInfo.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    RaynaMoreInfoBottomSheet bottomSheet = new RaynaMoreInfoBottomSheet();
                    bottomSheet.tourOptionsModel = model;
                    bottomSheet.activity = activity;
                    bottomSheet.isNonRefundable = model.getOptionDetail() != null && !TextUtils.isEmpty(model.getOptionDetail().getCancellationPolicy()) && model.getOptionDetail().getCancellationPolicy().equalsIgnoreCase("Non Refundable");
                    bottomSheet.show(getSupportFragmentManager(),"");
                });

            }

            private void shouldHideDiscount(TextView textView,TextView mainTextView, float withoutDiscountAmount, float finalAmount) {
                if (finalAmount >= withoutDiscountAmount) {
                    textView.setVisibility(View.GONE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

                Utils.setStyledText(activity,textView, String.valueOf(Utils.roundFloatValue(withoutDiscountAmount)));
                Utils.setStyledText(activity,mainTextView, Utils.roundFloatValue(finalAmount));

                if (withoutDiscountAmount == 0) {
                    textView.setVisibility(View.GONE);
                }
            }

            private void updateAdultChildInfantValue(TourOptionsModel model){
//                Utils.setStyledText(activity,binding.adultsPrice, Utils.roundFloatValue(model.updateAdultPrices()));
//                Utils.setStyledText(activity,binding.childPrice, Utils.roundFloatValue(model.updateChildPrices()));
//                Utils.setStyledText(activity,binding.infantsPrice, Utils.roundFloatValue(model.updateInfantPrices()));

                if (model.updateAdultPrices() == 0){
                    binding.adultsPrice.setVisibility(GONE);
                }else {
                    binding.adultsPrice.setVisibility(View.VISIBLE);
                    Utils.setStyledText(activity,binding.adultsPrice, Utils.roundFloatValue(model.updateAdultPrices()));
                }

                if (model.updateChildPrices() == 0){
                    binding.childPrice.setVisibility(GONE);
                }else {
                    binding.childPrice.setVisibility(View.VISIBLE);
                    Utils.setStyledText(activity,binding.childPrice, Utils.roundFloatValue(model.updateChildPrices()));
                }

                if (model.updateInfantPrices() == 0){
                    binding.infantsPrice.setVisibility(GONE);
                }else {
                    binding.infantsPrice.setVisibility(View.VISIBLE);
                    Utils.setStyledText(activity,binding.infantsPrice, Utils.roundFloatValue(model.updateInfantPrices()));
                }

                String unit = model.getUnit();
                if (!TextUtils.isEmpty(unit)) {
                    binding.tvTotalAdult.setText(model.getTmpAdultValue() + " " + unit);
                    binding.tvTotalChild.setText(model.getTmpChildValue() + " " + unit);
                    binding.tvTotalInfants.setText(model.getTmpInfantValue() + " " + unit);
                }
            }

            private void updatePaxBg(TourOptionsModel model){
                int[] values = {model.getTmpAdultValue(), model.getTmpChildValue(), model.getTmpInfantValue()};

                View[] views = {binding.addQuantityAdult, binding.addQuantityChild, binding.addQuantityInfants};

                for (int i = 0; i < views.length; i++) {
                    int drawableRes = values[i] != 0
                            ? R.drawable.selected_tour_option_people_stock_bg
                            : R.drawable.tour_option_spinner_stock_bg;

                    views[i].setBackground(ContextCompat.getDrawable(activity, drawableRes));
                }


                int drawableRes = !TextUtils.isEmpty(model.getTourOptionSelectDate())
                        ? R.drawable.ticket_date_selected_bg
                        : R.drawable.ticket_date_selection_bg;
                binding.dateTimeLayout.setBackground(ContextCompat.getDrawable(activity, drawableRes));

            }

            private void refreshModel(TourOptionsModel model){
                model.setTmpAdultValue(0);
                model.setTmpChildValue(0);
                model.setTmpInfantValue(0);
                model.setRaynaTimeSlotModel(null);
                model.setTourOptionSelectDate(Utils.changeDateFormat(String.valueOf(new Date()), "EEE, dd MMM yyyy", "yyyy-MM-dd"));
                model.setFirestTimeUpdate(true);

            }

        }

        private void applyExpandState(ViewHolder holder, TourOptionsModel model) {

            View content = holder.binding.hideShowLayout;

            if (model.isExpanded()) {
                content.setVisibility(View.VISIBLE);
                content.setAlpha(1f);
            } else {
                content.setAlpha(0f);
                content.setVisibility(View.GONE);
            }

            // Arrow (instant)
            holder.binding.expandedArrow.setRotation(
                    model.isExpanded() ? 90f : 360f
            );
        }

    }

    // endregion
    // --------------------------------------


}
