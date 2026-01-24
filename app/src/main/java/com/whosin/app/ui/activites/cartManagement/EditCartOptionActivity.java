package com.whosin.app.ui.activites.cartManagement;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
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
import com.whosin.app.service.models.BigBusModels.BigBusOptionsItemModel;
import com.whosin.app.service.models.BigBusModels.BigBusPricingFromItemModel;
import com.whosin.app.service.models.BigBusModels.BigBusUnitsItemModel;
import com.whosin.app.service.models.BigBusModels.OctaTourAvailabilityModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskCancellationPolicyModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskHeroImageModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskOptionDataModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskPriceModel;
import com.whosin.app.service.models.myCartModels.MyCartItemsModel;
import com.whosin.app.service.models.myCartModels.MyCartTourDetailsModel;
import com.whosin.app.service.models.rayna.RaynaPassengerModel;
import com.whosin.app.service.models.rayna.RaynaTicketBookingModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.models.rayna.RaynaTimeSlotModel;
import com.whosin.app.service.models.rayna.TourOptionDetailModel;
import com.whosin.app.service.models.rayna.TourOptionsModel;
import com.whosin.app.service.models.whosinTicketModel.RaynaWhosinBookingRulesModel;
import com.whosin.app.service.models.whosinTicketModel.WhosinAvailabilityModel;
import com.whosin.app.service.models.whosinTicketModel.WhosinTicketTourOptionModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.ProfileFullScreenImageActivity;
import com.whosin.app.ui.activites.auth.AuthenticationActivity;
import com.whosin.app.ui.activites.bigBusTicket.BigBusDateTimePickerSheet;
import com.whosin.app.ui.activites.bigBusTicket.BigBusPickUpListSheet;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.RaynaMoreInfoBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.ReadMoreBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.SelectDateAndTimeSheet;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketTourOptionActivity;
import com.whosin.app.ui.activites.travelDeskTicket.TravelDeskDateTimePickerSheet;
import com.whosin.app.ui.activites.whosinTicket.WhosinCustomTicketDateAndTimeSheet;
import com.whosin.app.ui.adapter.AddOnAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EditCartOptionActivity extends BaseActivity {

    private ActivityRaynaTicketTourOptionBinding binding;

    private final TicketTourOptionListAdapter<TourOptionsModel> ticketTourOptionListAdapter = new TicketTourOptionListAdapter<>();

    private final WhosinTicketTourOptionListAdapter<TourOptionsModel> whosinTicketTourOptionListAdapter = new WhosinTicketTourOptionListAdapter<>();

    private final TravelDeskTicketTourOptionListAdapter<TravelDeskOptionDataModel> travelDeskTicketTourOptionListAdapter = new TravelDeskTicketTourOptionListAdapter<>();

    private final WhosinCustomTourOptionListForAdapter<WhosinTicketTourOptionModel> whosinCustomTourOptionListForAdapter = new WhosinCustomTourOptionListForAdapter<>();

    private final BigBusTourOptionListAdapter<BigBusOptionsItemModel> bigBusTourOptionListAdapter = new BigBusTourOptionListAdapter<>();

    private RaynaTicketDetailModel raynaTicketDetailModel = null;

    private MyCartItemsModel myCartItemsModel = null;

    private String tourOptionId = "";

    private List<TourOptionsModel> originalTourOptionsList = new ArrayList<>();

    private List<String> positionsOfAdapter = new ArrayList<>();

    private boolean isFirestTime = true;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        RaynaTicketManager.shared.clearManager();
        binding.tvNext.setText("Update");

        binding.constraintHeader.tvTitle.setText(getValue("Edit cart"));

        ((SimpleItemAnimator) Objects.requireNonNull(binding.tourOptionRecyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        binding.tourOptionRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));


        Gson gson = new Gson();
        String myCartJson = getIntent().getStringExtra("myCartDetailModel");
        tourOptionId = getIntent().getStringExtra("tourOptionId");

        myCartItemsModel = gson.fromJson(myCartJson, MyCartItemsModel.class);


        if (myCartItemsModel != null) {
            RecyclerView.Adapter<?> adapter;

            switch (myCartItemsModel.getBookingType()) {
                case "rayna":
                    adapter = ticketTourOptionListAdapter;
                    break;

                case "travel-desk":
                    adapter = travelDeskTicketTourOptionListAdapter;
                    break;

                case "whosin-ticket":
                    adapter = whosinCustomTourOptionListForAdapter;
                    break;

                case "octo":
                case "hero-balloon":
                case "big-bus":
                    adapter = bigBusTourOptionListAdapter;
                    break;

                default:
                    adapter = whosinTicketTourOptionListAdapter;
                    break;
            }

            binding.tourOptionRecyclerView.setAdapter(adapter);
            requestTicketDetail(myCartItemsModel.getCustomTicketId());
        }



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
            if (raynaTicketDetailModel.getBookingType().equals("travel-desk")){
                for (TravelDeskOptionDataModel p : RaynaTicketManager.shared.selectTravelDeskOptionDataModels) {
                    int total =  p.getTmpAdultValue() + p.getTmpChildValue() + p.getTmpInfantValue();
                    if (total == 0) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("pax_required_alert",p.getName()));
                        return;
                    }
                    if (TextUtils.isEmpty(p.getTourOptionSelectDate())) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("date_required_alert",p.getName()));
                        return;
                    }

                    if (p.getTravelDeskAvailabilityModel() == null){
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("time_required_alert",p.getName()));
                        return;
                    }

                    if (total < p.getMinNumOfPeople()) {
                        String unit = p.getUnit();
                        if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("min_pax_alert",String.valueOf(p.getMinNumOfPeople()),unit,p.getName()));
                        return;
                    }
                }

                requestTravelDeskTourPolicy(RaynaTicketManager.shared.selectTravelDeskOptionDataModels.get(0));

            } else if (myCartItemsModel.getBookingType().equals("octo") || myCartItemsModel.getBookingType().equals("hero-balloon") || myCartItemsModel.getBookingType().equals("big-bus")) {
                for (BigBusOptionsItemModel p : RaynaTicketManager.shared.selectedTourModelForBigBus) {
                    int total =  p.getTmpAdultValue() + p.getTmpChildValue() + p.getTmpInfantValue();
                    String optionName =  p.getTitle();
                    if (total == 0) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("pax_required_alert",optionName));
                        return;
                    }
                    if (TextUtils.isEmpty(p.getTourOptionSelectDate())) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("date_required_alert",optionName));
                        return;
                    }

                    if (p.getTimeModel() == null) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("time_required_alert",optionName));
                        return;
                    }
                    if (total < p.getMinNumOfPeople()){
                        String unit = p.getUnit();
                        if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("min_pax_alert",String.valueOf(p.getMinNumOfPeople()),unit,optionName));
                        return;
                    }
                }
                requestCartOptionUpdate();
            } else if (raynaTicketDetailModel.getBookingType().equals("whosin-ticket")) {
                for (WhosinTicketTourOptionModel p : RaynaTicketManager.shared.selectedTourModelForWhosin) {
                    int total =  p.getTmpAdultValue() + p.getTmpChildValue() + p.getTmpInfantValue();
                    String title = p.getDisplayName();
                    if (total == 0) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("pax_required_alert",title));
                        return;
                    }
                    if (TextUtils.isEmpty(p.getTourOptionSelectDate())){
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("date_required_alert",title));
                        return;
                    }

                    if (p.getIsSlot() && p.getRaynaTimeSlotModel() == null){
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("time_required_alert",title));
                        return;
                    }

                    if (p.isWhosinMinPax()){
                        String unit = p.getUnit();
                        if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("min_pax_alert",String.valueOf(p.getTmpMinPax()),unit,title));
                        return;
                    }

                }

                requestWhosinAvailability(RaynaTicketManager.shared.selectedTourModelForWhosin.get(0));
            } else {
                for (TourOptionsModel p : RaynaTicketManager.shared.selectedTourModel) {
                    int total =  p.getTmpAdultValue() + p.getTmpChildValue() + p.getTmpInfantValue();
                    if (!raynaTicketDetailModel.getBookingType().equals("whosin")){
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
                            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("min_pax_alert",String.valueOf(RaynaTicketManager.shared.raynaTicketDetailModel.getTmpMinPax()),unit,p.getOptionDetail().getOptionName()));
                            return;
                        }
                    }else {
                        if (total == 0) {
                            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("pax_required_alert",p.getTitle()));
                            return;
                        }
                        if (TextUtils.isEmpty(p.getTourOptionSelectDate())){
                            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("date_required_alert",p.getTitle()));
                            return;
                        }
                        if (p.isWhosinMinPax()){
                            String unit = p.getUnit();
                            if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("min_pax_alert",String.valueOf(p.getTmpMinPax()),unit,p.getTitle()));
                            return;
                        }
                    }

                }

                RaynaTicketManager.shared.cancellationObject = new JsonArray();
                if (raynaTicketDetailModel.getBookingType().equals("whosin")) {
                    requestRaynaTourPolicyForWhosinType(RaynaTicketManager.shared.selectedTourModel.get(0));
                } else {
                    requestRaynaTourPolicy(RaynaTicketManager.shared.selectedTourModel.get(0));
                }
            }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RaynaTicketManager.shared.clearManager();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void updateLangValue(ItemRaynaTicketOptionViewBinding binding){
        binding.selectTourDateLayout.setHint(getValue("date_time_placeHolder"));
        binding.tourTimeSlotTv.setHint(getValue("time_slot"));
        binding.btnMoreInfoView.setText(getValue("Inclusions & Details"));
        binding.tvAdultsTitle.setText(getValue("adults_title"));
        binding.tvChildTitle.setText(getValue("children_title"));
        binding.tvInfantsTitle.setText(getValue("infant_title"));
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
                .collect(Collectors.toList());

        finalList.forEach( q -> {
            if (q.getTourOptionId().toString().equals(tourOptionId)){
                Optional<MyCartTourDetailsModel> tmpMpdel = myCartItemsModel.getTourDetails().stream().filter(p -> p.getOptionId().equals(tourOptionId)).findFirst();
                tmpMpdel.ifPresent(model -> {
                    q.updateValueForCart(model, false);
                    if (tmpMpdel.get().getTransferId() != 0){
//                        String transferName = Utils.getTransferName(tmpMpdel.get().getTransferId());
                        String transferName = data.stream().filter(p -> p.getTransferId() == tmpMpdel.get().getTransferId()).map(TourOptionsModel::getTransferName).findFirst().orElse("");
                        if (TextUtils.isEmpty(transferName)) return;
                        int position = IntStream.range(0, q.getTransTypeList().size())
                                .filter(i -> q.getTransTypeList().get(i).equalsIgnoreCase(transferName))
                                .findFirst()
                                .orElse(-1);
                        if (position == -1) return;
                        q.setSelectedTransType(position);
                        q.setSelectedTransferId(tmpMpdel.get().getTransferId());
                        TourOptionsModel optionalModel = getOptionalModel(q, q.getSelectedTransType());
                        if (optionalModel != null) {
                            q.updateValueOnTransType(optionalModel);
                        }
                    }
                });
            }
        });
        RaynaTicketManager.shared.selectedTourModel.addAll(finalList);
        positionsOfAdapter.add("0");

        if (!isFirestTime && !RaynaTicketManager.shared.selectedTourModel.isEmpty()) {
            List<Integer> selectedIds = RaynaTicketManager.shared.selectedTourModel.stream().map(TourOptionsModel::getTourOptionId).collect(Collectors.toList());
            finalList.forEach(q -> {
                RaynaTicketManager.shared.selectedTourModel.stream()
                        .filter(p -> Objects.equals(p.getTourOptionId(), q.getTourOptionId()))
                        .findFirst()
                        .ifPresent(model -> q.updateValueForTourOption(model, false));

            });

            RaynaTicketManager.shared.selectedTourModel.clear();
            RaynaTicketManager.shared.selectedTourModel = finalList.stream().filter(model -> selectedIds.contains(model.getTourOptionId())).collect(Collectors.toList());
        }
        isFirestTime = false;
        finalList = finalList.stream().sorted(Comparator.comparing(TourOptionsModel::getAdultPrice, Comparator.nullsLast(Float::compareTo))).collect(Collectors.toList());
        updateButtonValue();
        ticketTourOptionListAdapter.updateData(finalList);
    }

    private List<TourOptionsModel> mapTheDataForOtherTourList(List<TourOptionsModel> data) {
        // Deep copy using reflection
        List<TourOptionsModel> dataCopy = data.stream().map(RaynaTicketTourOptionActivity::cloneObject).filter(Objects::nonNull).collect(Collectors.toList());
        List<TourOptionsModel> finalList = dataCopy.stream().collect(Collectors.groupingBy(TourOptionsModel::getTourOptionId, Collectors.mapping(TourOptionsModel::getTransferName, Collectors.toList())))
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
                .sorted(Comparator.comparing(model1 -> model1 != null ? model1.getFinalAmount() : null))
                .collect(Collectors.toList());

        finalList.forEach( q -> {
            Optional<MyCartTourDetailsModel> tmpMpdel = myCartItemsModel.getTourDetails().stream().filter(p -> p.getOptionId().equals(q.getTourOptionId().toString())).findFirst();
            tmpMpdel.ifPresent(model -> q.updateValueForCart(model, false));
        });

        return finalList;
    }

    private void updateButtonColor(boolean isForTravelDeskTicket) {
        boolean isEmpty = false;
        if (isForTravelDeskTicket){
            isEmpty = !RaynaTicketManager.shared.selectTravelDeskOptionDataModels.isEmpty();
        } else if (myCartItemsModel.getBookingType().equals("whosin-ticket")) {
            isEmpty = !RaynaTicketManager.shared.selectedTourModelForWhosin.isEmpty();
        }else if (myCartItemsModel.getBookingType().equals("octo") || myCartItemsModel.getBookingType().equals("hero-balloon") || myCartItemsModel.getBookingType().equals("big-bus")) {
            isEmpty = !RaynaTicketManager.shared.selectedTourModelForBigBus.isEmpty();
        } else {
            isEmpty = !RaynaTicketManager.shared.selectedTourModel.isEmpty();
        }
        int color = ContextCompat.getColor(activity, isEmpty  ? R.color.brand_pink : R.color.gray);
        binding.nextButton.setBackgroundColor(color);
    }

    private int getTransferId(int tourOptionId, String transType) {
        return originalTourOptionsList.stream()
                .filter(p -> p.getTourOptionId() == tourOptionId && transType.equals(p.getTransferName()))
                .map(TourOptionsModel::getTransferId)
                .findFirst()
                .orElse(0);
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

    private boolean isTimeSlot(TourOptionsModel model) {
        return originalTourOptionsList.stream()
                .filter(p -> Objects.equals(p.getTourOptionId(), model.getTourOptionId()) && model.getTransType().equals(p.getTransferName()))
                .map(TourOptionsModel::getIsSlot)
                .findFirst()
                .orElse(false);
    }

    private void updateButtonValue() {
        if (myCartItemsModel.getBookingType().equals("octo") || myCartItemsModel.getBookingType().equals("hero-balloon") || myCartItemsModel.getBookingType().equals("big-bus")){
            if (RaynaTicketManager.shared.selectedTourModelForBigBus.isEmpty()) {
                binding.tvPrice.setVisibility(View.GONE);
            } else {
                binding.tvPrice.setVisibility(View.VISIBLE);
                float adultAmount = 0f;
                float childAmount = 0f;
                float infantAmount = 0f;
                float addOnAmount = 0f;

                for (BigBusOptionsItemModel q : RaynaTicketManager.shared.selectedTourModelForBigBus) {
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
        }else {
            if (RaynaTicketManager.shared.selectedTourModel.isEmpty()) {
                binding.tvPrice.setVisibility(View.GONE);
            } else {
                binding.tvPrice.setVisibility(View.VISIBLE);
                float adultAmount = 0f;
                float childAmount = 0f;
                float infantAmount = 0f;
                float addOnAmount = 0f;

                for (TourOptionsModel q : RaynaTicketManager.shared.selectedTourModel) {
                    adultAmount += q.updateAdultPrices();
                    childAmount += q.updateChildPrices();
                    infantAmount += q.updateInfantPrices();
                    addOnAmount += q.updateAddOnPrices();
                }

                float total = adultAmount + childAmount + infantAmount + addOnAmount;
                Utils.setStyledText(activity,binding.tvPrice,Utils.roundFloatValue(total));
                if (total == 0.0){
                    binding.tvPrice.setVisibility(View.GONE);
                }else {
                    binding.tvPrice.setVisibility(View.VISIBLE);
                }
            }
        }


        updateButtonColor(false);
    }

    private void updateButtonValueForWhosinCustom() {
        if (RaynaTicketManager.shared.selectedTourModelForWhosin.isEmpty()) {
            binding.tvPrice.setVisibility(View.GONE);
        } else {
            binding.tvPrice.setVisibility(View.VISIBLE);
            float adultAmount = 0f;
            float childAmount = 0f;
            float infantAmount = 0f;

            for (WhosinTicketTourOptionModel q : RaynaTicketManager.shared.selectedTourModelForWhosin) {
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

        updateButtonColor(false);
    }

    private void updateButtonValueForTravelDesk() {
        List<TravelDeskOptionDataModel> selectedOptions = RaynaTicketManager.shared.selectTravelDeskOptionDataModels;

        if (selectedOptions.isEmpty()) {
            binding.tvPrice.setVisibility(View.GONE);
            updateButtonColor(true);
            return;
        }

        float total = 0f;
        for (TravelDeskOptionDataModel q : selectedOptions) {
            total += q.updateAdultPrices() + q.updateChildPrices() + q.updateInfantPrices() + q.getPricePerTrip();
        }

        if (total == 0f) {
            binding.tvPrice.setVisibility(View.GONE);
        } else {
            binding.tvPrice.setVisibility(View.VISIBLE);
            Utils.setStyledText(activity, binding.tvPrice, Utils.roundFloatValue(total));
        }

        updateButtonColor(true);
    }

    private String formatPlainAmount(float value) {
        return new java.math.BigDecimal(String.valueOf(value)).stripTrailingZeros().toPlainString();
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

    private void handleTicketOption(List<TourOptionsModel> data) {
        List<TourOptionsModel> tourOptionsModels = new ArrayList<>(data);

        List<TourOptionsModel> tourOptionsModelsTmp = tourOptionsModels.stream().filter(p -> p.get_id().equals(tourOptionId)).collect(Collectors.toList());
        tourOptionsModelsTmp.forEach( q -> {
            if (q.get_id().equals(tourOptionId)){
                Optional<MyCartTourDetailsModel> tmpMpdel = myCartItemsModel.getTourDetails().stream().filter(p -> p.getOptionId().equals(tourOptionId)).findFirst();
                tmpMpdel.ifPresent(h -> q.updateValueForCart(h, true));
            }
        });


        RaynaTicketManager.shared.selectedTourModel.clear();
        RaynaTicketManager.shared.selectedTourModel.addAll(tourOptionsModelsTmp);
        positionsOfAdapter.add("0");
        updateButtonValue();


        if (RaynaTicketManager.shared.selectedTourModel != null && !RaynaTicketManager.shared.selectedTourModel.isEmpty()) {
            List<String> selectedIds = RaynaTicketManager.shared.selectedTourModel.stream().map(TourOptionsModel::get_id).collect(Collectors.toList());
            tourOptionsModels.forEach(q -> {
                RaynaTicketManager.shared.selectedTourModel.stream()
                        .filter(p -> Objects.equals(p.get_id(), q.get_id()))
                        .findFirst()
                        .ifPresent(model -> q.updateValueForTourOption(model, true));

            });

            RaynaTicketManager.shared.selectedTourModel.clear();
            RaynaTicketManager.shared.selectedTourModel = data.stream().filter(model -> selectedIds.contains(model.get_id())).collect(Collectors.toList());
            updateButtonValue();
        }
        whosinTicketTourOptionListAdapter.updateData(RaynaTicketManager.shared.selectedTourModel);

    }

    private JsonObject getTimeSlotJsonObject(BigBusOptionsItemModel tourOptionsModel,String selectedDate) {
        JsonObject jsonObject = new JsonObject();
        if (tourOptionsModel != null) {
            jsonObject.addProperty("tourId", tourOptionsModel.getTourId());
            jsonObject.addProperty("optionId", tourOptionsModel.getId());
            jsonObject.addProperty("fromDate", tourOptionsModel.getTourOptionSelectDate());
            jsonObject.addProperty("toDate", tourOptionsModel.getTourOptionSelectDate());

            JsonArray unitArray = new JsonArray();
            if (tourOptionsModel.getTmpAdultValue() != 0) {
                BigBusUnitsItemModel unit = tourOptionsModel.getUnitByType(AppConstants.ADULTS);
                if (unit != null) {
                    JsonObject jsonObject1 = new JsonObject();
                    jsonObject1.addProperty("id", unit.getId());
                    jsonObject1.addProperty("quantity", tourOptionsModel.getTmpAdultValue());
                    unitArray.add(jsonObject1);
                }
            }

            if (tourOptionsModel.getTmpChildValue() != 0) {
                BigBusUnitsItemModel unit = tourOptionsModel.getUnitByType(AppConstants.CHILD);
                if (unit != null) {
                    JsonObject jsonObject1 = new JsonObject();
                    jsonObject1.addProperty("id", unit.getId());
                    jsonObject1.addProperty("quantity", tourOptionsModel.getTmpChildValue());
                    unitArray.add(jsonObject1);
                }
            }

            if (tourOptionsModel.getTmpInfantValue() != 0) {
                BigBusUnitsItemModel unit = tourOptionsModel.getUnitByType(AppConstants.INFANT);
                if (unit != null) {
                    JsonObject jsonObject1 = new JsonObject();
                    jsonObject1.addProperty("id", unit.getId());
                    jsonObject1.addProperty("quantity", tourOptionsModel.getTmpInfantValue());
                    unitArray.add(jsonObject1);
                }
            }

            jsonObject.add("units", unitArray);
            jsonObject.addProperty("pickupRequested", tourOptionsModel.isPickupRequired());

            if (tourOptionsModel.isPickupRequired() && tourOptionsModel.getPickupPointsModel() != null) {
                jsonObject.addProperty("pickupPointId", tourOptionsModel.getPickupPointsModel().getId());
            } else {
                jsonObject.addProperty("pickupPointId", "");
            }
        }
        return jsonObject;
    }

    // endregion
    // --------------------------------------
    // region Private Methods For Update Cart
    // --------------------------------------

    private JsonObject getJsonObject() {
        myCartItemsModel.getCancellationPolicy().removeIf( q -> tourOptionId.equals(String.valueOf(q.getOptionId())));

        JsonObject object = new JsonObject();
        object.addProperty("currency", Utils.getCurrency());
        object.addProperty("bookingType", raynaTicketDetailModel.getBookingType());
        object.addProperty("cartId", myCartItemsModel.get_id());

        AtomicReference<Float> totalAmount = new AtomicReference<>(0f);
        AtomicReference<Float> discountAmount = new AtomicReference<>(0f);
        AtomicReference<Float> finalAmount = new AtomicReference<>(0f);

        JsonArray allAddons = null;
        if (RaynaTicketManager.shared.selectedAddonModels != null && !RaynaTicketManager.shared.selectedAddonModels.isEmpty()) {
            allAddons = new JsonArray();
            for (TourOptionsModel model : RaynaTicketManager.shared.selectedAddonModels) {
                JsonObject addonObj = new JsonObject();
                addonObj.addProperty("tourId", tourOptionId);
                addonObj.addProperty("optionId", model.get_id());
                addonObj.addProperty("_id", model.get_id());
                addonObj.addProperty("departureTime", "");
                addonObj.addProperty("tourDate", !TextUtils.isEmpty(model.getTourOptionSelectDate()) ? model.getTourOptionSelectDate() : model.getBookingDate());
                if (model.getRaynaTimeSlotModel() != null) {
                    addonObj.addProperty("startTime", model.getRaynaTimeSlotModel().getAvailabilityTime());
                    addonObj.addProperty("timeSlot", model.getRaynaTimeSlotModel().getAvailabilityTime());
                    addonObj.addProperty("timeSlotId", model.getRaynaTimeSlotModel().getTimeSlotId());
                } else {
                    addonObj.addProperty("startTime", model.getAvailabilityTime());
                    addonObj.addProperty("timeSlot", model.getAvailabilityTime());
                    addonObj.addProperty("timeSlotId", "");
                }
                addonObj.addProperty("addOnTitle", model.getTitle());
                if (model.getImages() != null && !model.getImages().isEmpty() && !TextUtils.isEmpty(model.getImages().get(0))) {
                    addonObj.addProperty("addOnImage", model.getImages().get(0));
                } else {
                    addonObj.addProperty("addOnImage", "");
                }
                addonObj.addProperty("addOndesc", model.getSortDescription());
                addonObj.addProperty("adult_title", model.getAdultTitle());
                addonObj.addProperty("child_title", model.getChildTitle());
                addonObj.addProperty("infant_title", model.getInfantTitle());
                addonObj.addProperty("adult_description", model.getAdultDescription());
                addonObj.addProperty("child_description", model.getChildDescription());
                addonObj.addProperty("infant_description", model.getInfantDescription());
                addonObj.addProperty("endTime", "");
                addonObj.addProperty("pickup", "");
                addonObj.addProperty("message", "");
                addonObj.add("transferId", null);
                addonObj.addProperty("adultRate", model.getAdultPrice());
                addonObj.addProperty("adult", model.getTmpAdultValue());
                addonObj.addProperty("childRate", model.getChildPrice());
                addonObj.addProperty("child", model.getTmpChildValue());
                addonObj.addProperty("infantRate", model.getInfantPrice());
                addonObj.addProperty("infant", model.getTmpInfantValue());
                addonObj.addProperty("whosinTotal", model.updatePrice());
                addonObj.addProperty("serviceTotal", model.updatePrice());
                allAddons.add(addonObj);
            }
        } else if (RaynaTicketManager.shared.object.has("Addons")) {
            allAddons = RaynaTicketManager.shared.object.getAsJsonArray("Addons");
        }

        RaynaTicketManager.shared.selectedTourModel.forEach(q->{
            totalAmount.set(totalAmount.get() + getWithoutDiscountPrice(q));
            discountAmount.set(discountAmount.get() + getWhosinTotal(q));
            finalAmount.set(finalAmount.get() + getWhosinTotal(q));
        });

        if (myCartItemsModel.getTourDetails().size() > 1) {
            if (raynaTicketDetailModel.getBookingType().equals("whosin")){
                Set<String> filteredOptionIds = myCartItemsModel.getTourDetails().stream().map(MyCartTourDetailsModel::getOptionId).filter(id -> !id.equals(tourOptionId)).collect(Collectors.toSet());
                List<TourOptionsModel> matchedTourOptions = raynaTicketDetailModel.getOptionData().stream().filter(option -> filteredOptionIds.contains(option.get_id())).collect(Collectors.toList());
                matchedTourOptions.forEach( q -> {
                    Optional<MyCartTourDetailsModel> tmpMpdel = myCartItemsModel.getTourDetails().stream().filter(p -> p.getOptionId().equals(q.get_id())).findFirst();
                    tmpMpdel.ifPresent(model -> q.updateValueForCart(model, false));
                });
                matchedTourOptions.forEach( q -> {
                    totalAmount.set(totalAmount.get() + getWithoutDiscountPrice(q));
                    discountAmount.set(discountAmount.get() + getWhosinTotal(q));
                    finalAmount.set(finalAmount.get() + getWhosinTotal(q));
                });
            }else {
                Set<String> filteredOptionIds = myCartItemsModel.getTourDetails().stream().map(MyCartTourDetailsModel::getOptionId).filter(id -> !id.equals(tourOptionId)).collect(Collectors.toSet());
                List<TourOptionsModel> matchedTourOptions = originalTourOptionsList.stream().filter(option -> filteredOptionIds.contains(option.getTourOptionId().toString())).collect(Collectors.toList());
                List<TourOptionsModel> tmpList = mapTheDataForOtherTourList(matchedTourOptions);
                tmpList.forEach( q -> {
                    totalAmount.set(totalAmount.get() + getWithoutDiscountPrice(q));
                    discountAmount.set(discountAmount.get() + getWhosinTotal(q));
                    finalAmount.set(finalAmount.get() + getWhosinTotal(q));
                });
            }
        }



        float discount = totalAmount.get() - discountAmount.get();
        if (discount < 0){
            discount = 0;
        }

        totalAmount.set(Utils.changeCurrencyToAED(totalAmount.get()));
        finalAmount.set(Utils.changeCurrencyToAED(finalAmount.get()));

        object.addProperty("totalAmount", formatPlainAmount(totalAmount.get()));
        object.addProperty("discount", formatPlainAmount(discount));
        object.addProperty("amount", formatPlainAmount(finalAmount.get()));
        object.addProperty("customTicketId", raynaTicketDetailModel.getId());


        List<RaynaPassengerModel> passengers = myCartItemsModel.getPassengerModel();
        for (int i = 0; i < passengers.size(); i++) {
            passengers.get(i).setLeadPassenger(i == 0 ? 1 : 0);
        }
        JsonArray passengerArray = new Gson().toJsonTree(passengers).getAsJsonArray();
        object.add("passengers", passengerArray);


        JsonArray tourDetails = new JsonArray();
        if (raynaTicketDetailModel.getBookingType().equals("whosin")){
            for (TourOptionsModel p : RaynaTicketManager.shared.selectedTourModel) {
                JsonObject tourDetailObject = new JsonObject();

                tourDetailObject.addProperty("tourId", p.getCustomTicketId());
                tourDetailObject.addProperty("optionId", p.get_id());
                tourDetailObject.addProperty("pickup","None");
                tourDetailObject.addProperty("tourDate", p.getTourOptionSelectDate());

                tourDetailObject.addProperty("serviceTotal", getServiceTotalForWhosinType(p));
                tourDetailObject.addProperty("whosinTotal",Utils.changeCurrencyToAED(getWhosinTotalForWhosinType(p)));

                tourDetailObject.addProperty("adult", p.getTmpAdultValue());
                if (p.getTmpAdultValue() != 0){
                    tourDetailObject.addProperty("adultRate", p.getWithoutDiscountAdultPrice());
                }else {
                    tourDetailObject.addProperty("adultRate", 0);
                }

                tourDetailObject.addProperty("child", p.getTmpChildValue());
                if (p.getTmpChildValue() != 0) {
                    tourDetailObject.addProperty("childRate",p.getWithoutDiscountChildPrice());
                }else {
                    tourDetailObject.addProperty("childRate",0);
                }
                tourDetailObject.addProperty("infant", p.getTmpInfantValue());
                tourDetailObject.addProperty("transferId", 0);

                if (p.getAvailabilityType().equals("slot") && p.getRaynaTimeSlotModel() != null){
                    tourDetailObject.addProperty("timeSlotId", p.getRaynaTimeSlotModel().getId());
                    tourDetailObject.addProperty("timeSlot", p.getRaynaTimeSlotModel().getAvailabilityTime());
                    tourDetailObject.addProperty("startTime", p.getRaynaTimeSlotModel().getAvailabilityTime());
                }else {
                    tourDetailObject.addProperty("timeSlotId", 0);
                    tourDetailObject.addProperty("timeSlot", p.getAvailabilityTime());
                    tourDetailObject.addProperty("startTime", p.getAvailabilityTime());
                }

                tourDetailObject.addProperty("adult_title", p.getAdultTitle());
                tourDetailObject.addProperty("child_title", p.getChildTitle());
                tourDetailObject.addProperty("infant_title", p.getInfantTitle());

                JsonArray optionAddons = new JsonArray();
                if (allAddons != null && p.getAddons() != null && !p.getAddons().isEmpty()) {
                    List<String> optionAddonIds = p.getAddons().stream()
                            .map(TourOptionsModel::get_id)
                            .filter(id -> !TextUtils.isEmpty(id))
                            .collect(Collectors.toList());
                    for (int i = 0; i < allAddons.size(); i++) {
                        JsonObject addonObj = allAddons.get(i).getAsJsonObject();
                        if (addonObj.has("optionId") && optionAddonIds.contains(addonObj.get("optionId").getAsString())) {
                            optionAddons.add(addonObj);
                        }
                    }
                }
                if (optionAddons.size() > 0) {
                    tourDetailObject.add("Addons", optionAddons);
                }
                tourDetails.add(tourDetailObject);
            }

        }else {
            for (TourOptionsModel p : RaynaTicketManager.shared.selectedTourModel) {
                JsonObject tourDetailObject = new JsonObject();

                tourDetailObject.addProperty("transferId", p.getSelectedTransferId());
                tourDetailObject.addProperty("optionId", p.getTourOptionId());
                tourDetailObject.addProperty("pickup", p.getPickUpLocation());
                tourDetailObject.addProperty("startTime", p.getStartTime());
                tourDetailObject.addProperty("tourDate", p.getTourOptionSelectDate());
                tourDetailObject.addProperty("serviceTotal", getServiceTotal(p));
                tourDetailObject.addProperty("whosinTotal",Utils.changeCurrencyToAED(getWhosinTotal(p)));
                tourDetailObject.addProperty("tourId", p.getTourId());

                tourDetailObject.addProperty("adult", p.getTmpAdultValue());
                if (p.getTmpAdultValue() != 0){
                    tourDetailObject.addProperty("adultRate", p.getAdultPriceRayna());
                }else {
                    tourDetailObject.addProperty("adultRate", 0);

                }

                tourDetailObject.addProperty("child", p.getTmpChildValue());
                if (p.getTmpChildValue() != 0) {
                    tourDetailObject.addProperty("childRate",p.getChildPriceRayna());
                }else {
                    tourDetailObject.addProperty("childRate",0);
                }
                tourDetailObject.addProperty("infant", p.getTmpInfantValue());
                if (p.getRaynaTimeSlotModel() != null) {
                    tourDetailObject.addProperty("timeSlotId", p.getRaynaTimeSlotModel().getTimeSlotId());
                    tourDetailObject.addProperty("timeSlot", p.getRaynaTimeSlotModel().getTimeSlot());
                } else {
                    tourDetailObject.addProperty("timeSlotId", 0);
                    tourDetailObject.addProperty("timeSlot", p.getSlotText());
                }
                tourDetailObject.addProperty("departureTime",p.getDepartureTime());


                tourDetailObject.addProperty("adult_title", p.getAdultTitle());
                tourDetailObject.addProperty("child_title", p.getChildTitle());
                tourDetailObject.addProperty("infant_title", p.getInfantTitle());

                JsonArray optionAddons = new JsonArray();
                if (allAddons != null && p.getAddons() != null && !p.getAddons().isEmpty()) {
                    List<String> optionAddonIds = p.getAddons().stream()
                            .map(TourOptionsModel::get_id)
                            .filter(id -> !TextUtils.isEmpty(id))
                            .collect(Collectors.toList());
                    for (int i = 0; i < allAddons.size(); i++) {
                        JsonObject addonObj = allAddons.get(i).getAsJsonObject();
                        if (addonObj.has("optionId") && optionAddonIds.contains(addonObj.get("optionId").getAsString())) {
                            optionAddons.add(addonObj);
                        }
                    }
                }
                if (optionAddons.size() > 0) {
                    tourDetailObject.add("Addons", optionAddons);
                }

                tourDetails.add(tourDetailObject);
            }
        }

        if (myCartItemsModel.getTourDetails().size() > 1) {
            List<MyCartTourDetailsModel> list = myCartItemsModel.getTourDetails();
            list.removeIf(p -> p.getOptionId().equals(tourOptionId));
            for (MyCartTourDetailsModel item : list) {
                JsonObject jsonObject = new Gson().toJsonTree(item).getAsJsonObject();
                if (jsonObject.has("_id")) jsonObject.remove("_id");
                if (jsonObject.has("endTime")) jsonObject.remove("endTime");
                tourDetails.add(jsonObject);
            }
        }

        object.add("TourDetails", tourDetails);

        if (allAddons != null && allAddons.size() > 0) {
            object.add("Addons", allAddons);
        }

//        JsonArray combinedCancellationArray = new JsonArray();
//        combinedCancellationArray.add(RaynaTicketManager.shared.cancellationObject);
//        if (myCartItemsModel.getCancellationPolicy() != null && !myCartItemsModel.getCancellationPolicy().isEmpty()){
//            combinedCancellationArray.add(new Gson().toJsonTree(myCartItemsModel.getCancellationPolicy()).getAsJsonArray());
//        }

        JsonArray combinedCancellationArray = new JsonArray();
        if (RaynaTicketManager.shared.cancellationObject != null) {
            for (JsonElement element : RaynaTicketManager.shared.cancellationObject) {
                combinedCancellationArray.add(element);
            }
        }
        if (myCartItemsModel.getCancellationPolicy() != null && !myCartItemsModel.getCancellationPolicy().isEmpty()) {
            JsonArray cancellationArray = new Gson().toJsonTree(myCartItemsModel.getCancellationPolicy()).getAsJsonArray();
            for (JsonElement element : cancellationArray) {
                combinedCancellationArray.add(element);
            }
        }
        object.add("cancellationPolicy", combinedCancellationArray);


        return object;
    }

    private JsonObject getJsonObjectForTravelDesk() {
        myCartItemsModel.getCancellationPolicy().removeIf( q -> tourOptionId.equals(String.valueOf(q.getOptionId())));
        JsonObject object = new JsonObject();
        object.addProperty("currency", Utils.getCurrency());
        object.addProperty("bookingType", raynaTicketDetailModel.getBookingType());
        object.addProperty("cartId", myCartItemsModel.get_id());

        AtomicReference<Float> totalAmount = new AtomicReference<>(0f);
        AtomicReference<Float> discountAmount = new AtomicReference<>(0f);
        AtomicReference<Float> finalAmount = new AtomicReference<>(0f);
        AtomicReference<Float> pricePerTrip = new AtomicReference<>(0f);

        RaynaTicketManager.shared.selectTravelDeskOptionDataModels.forEach(q->{
            totalAmount.set(totalAmount.get() + getWithoutDiscountPriceForReavelDesk(q));
            discountAmount.set(discountAmount.get() + getWhosinTotalForTravelDesk(q));
            finalAmount.set(finalAmount.get() + getWhosinTotalForTravelDesk(q));
            pricePerTrip.set(pricePerTrip.get() + q.getPricePerTrip());
        });

        if (myCartItemsModel.getTourDetails().size() > 1) {
            if (raynaTicketDetailModel.getTravelDeskTourDataModelList().get(0).getOptionDataModel() != null && !raynaTicketDetailModel.getTravelDeskTourDataModelList().get(0).getOptionDataModel().isEmpty()) {
                Set<String> filteredOptionIds = myCartItemsModel.getTourDetails().stream().map(MyCartTourDetailsModel::getOptionId).filter(id -> !id.equals(tourOptionId)).collect(Collectors.toSet());
                List<TravelDeskOptionDataModel> list = raynaTicketDetailModel.getTravelDeskTourDataModelList().get(0).getOptionDataModel().stream().filter(p -> filteredOptionIds.contains(String.valueOf(p.getId()))).collect(Collectors.toList());
                list.forEach(q -> {
                    Optional<MyCartTourDetailsModel> tmpMpdel = myCartItemsModel.getTourDetails().stream().filter(p -> p.getOptionId().equals(String.valueOf(q.getId()))).findFirst();
                    tmpMpdel.ifPresent(q::updateValueForCart);
                });
                list.forEach(q -> {
                    totalAmount.set(totalAmount.get() + getWithoutDiscountPriceForReavelDesk(q));
                    discountAmount.set(discountAmount.get() + getWhosinTotalForTravelDesk(q));
                    finalAmount.set(finalAmount.get() + getWhosinTotalForTravelDesk(q));
                    pricePerTrip.set(pricePerTrip.get() + q.getPricePerTrip());
                });
            }
        }

        float discount = totalAmount.get() - discountAmount.get();
        if (discount < 0){
            discount = 0;
        }

        totalAmount.set(Utils.changeCurrencyToAED(totalAmount.get() + pricePerTrip.get()));
        finalAmount.set(Utils.changeCurrencyToAED(finalAmount.get() + pricePerTrip.get()));



//        totalAmount.set(totalAmount.get() + pricePerTrip.get());
//        finalAmount.set(finalAmount.get() + pricePerTrip.get());

        object.addProperty("customTicketId", raynaTicketDetailModel.getId());
        object.addProperty("totalAmount", formatPlainAmount(totalAmount.get()));
        object.addProperty("discount", formatPlainAmount(discount));
        object.addProperty("amount", formatPlainAmount(finalAmount.get()));


        List<RaynaPassengerModel> passengers = myCartItemsModel.getPassengerModel();
        for (int i = 0; i < passengers.size(); i++) {
            passengers.get(i).setLeadPassenger(i == 0 ? 1 : 0);
        }
        JsonArray passengerArray = new Gson().toJsonTree(passengers).getAsJsonArray();
        object.add("passengers", passengerArray);

        JsonArray tourDetails = new JsonArray();

        RaynaTicketManager.shared.selectTravelDeskOptionDataModels.forEach( p -> {
            JsonObject tourDetailObject = new JsonObject();
            tourDetailObject.addProperty("tourId", String.valueOf(p.getTourId()));
            tourDetailObject.addProperty("optionId", String.valueOf(p.getId()));
            tourDetailObject.addProperty("tourDate", p.getTourOptionSelectDate());

            tourDetailObject.addProperty("serviceTotal", getServiceTotalForTravelDesk(p) + p.getPricePerTripTravelDesk());
//            tourDetailObject.addProperty("whosinTotal", getWhosinTotalForTravelDesk(p) + p.getPricePerTrip());
            tourDetailObject.addProperty("whosinTotal", Utils.changeCurrencyToAED(getWhosinTotalForTravelDesk(p) + p.getPricePerTrip()));

            tourDetailObject.addProperty("adult", p.getTmpAdultValue());
            if (p.getTmpAdultValue() != 0){
                tourDetailObject.addProperty("adultRate", p.getAdultPrice());
            }else {
                tourDetailObject.addProperty("adultRate", 0);

            }

            tourDetailObject.addProperty("child", p.getTmpChildValue());
            if (p.getTmpChildValue() != 0) {
                tourDetailObject.addProperty("childRate",p.getChildPrice());
            }else {
                tourDetailObject.addProperty("childRate",0);
            }
            tourDetailObject.addProperty("infant", p.getTmpInfantValue());
            tourDetailObject.addProperty("transferId", 0);
            tourDetailObject.addProperty("departureTime", "");

            if (p.getTravelDeskPickUpListModel() != null){
                tourDetailObject.addProperty("pickup",p.getTravelDeskPickUpListModel().getName());
                tourDetailObject.addProperty("hotelId",p.getTravelDeskPickUpListModel().getId());
            }

            if (p.getTravelDeskAvailabilityModel() != null){
                tourDetailObject.addProperty("timeSlotId", String.valueOf(p.getTravelDeskAvailabilityModel().getAvailability().getTimeSlotId()));
                tourDetailObject.addProperty("timeSlot", p.getTravelDeskAvailabilityModel().getSlotText());
                tourDetailObject.addProperty("startTime", p.getTravelDeskAvailabilityModel().getAvailability().getStartTime());
                tourDetailObject.addProperty("endTime", p.getTravelDeskAvailabilityModel().getAvailability().getEndTime());
            }
            tourDetailObject.addProperty("message", p.getMessage());

            tourDetailObject.addProperty("adult_title", p.getAdultTitle());
            tourDetailObject.addProperty("child_title", p.getChildTitle());
            tourDetailObject.addProperty("infant_title", p.getInfantTitle());

            tourDetails.add(tourDetailObject);

        });

        if (myCartItemsModel.getTourDetails().size() > 1) {
            List<MyCartTourDetailsModel> list = myCartItemsModel.getTourDetails();
            list.removeIf(p -> p.getOptionId().equals(tourOptionId));
            for (MyCartTourDetailsModel item : list) {
                JsonObject jsonObject = new Gson().toJsonTree(item).getAsJsonObject();
                if (jsonObject.has("_id")) jsonObject.remove("_id");
                tourDetails.add(jsonObject);
            }
        }


        object.add("TourDetails", tourDetails);

//        Gson gson = new Gson();
//        if (!raynaTicketDetailModel.getTravelDeskTourDataModelList().isEmpty()) {
//            List<TravelDeskCancellationPolicyModel> cancellationPolicyList = raynaTicketDetailModel.getTravelDeskTourDataModelList().get(0).getCancellationPolicy();
//            if (!cancellationPolicyList.isEmpty()) {
//                JsonElement jsonCancellationPolicy = gson.toJsonTree(cancellationPolicyList);
//                object.add("cancellationPolicy", jsonCancellationPolicy);
//            }
//        }
//        JsonArray combinedCancellationArray = new JsonArray();
//        combinedCancellationArray.add(RaynaTicketManager.shared.cancellationObject);
//        if (myCartItemsModel.getCancellationPolicy() != null && !myCartItemsModel.getCancellationPolicy().isEmpty()){
//            combinedCancellationArray.add(new Gson().toJsonTree(myCartItemsModel.getCancellationPolicy()).getAsJsonArray());
//        }

        JsonArray combinedCancellationArray = new JsonArray();
        if (RaynaTicketManager.shared.cancellationObject != null) {
            for (JsonElement element : RaynaTicketManager.shared.cancellationObject) {
                combinedCancellationArray.add(element);
            }
        }
        if (myCartItemsModel.getCancellationPolicy() != null && !myCartItemsModel.getCancellationPolicy().isEmpty()) {
            JsonArray cancellationArray = new Gson().toJsonTree(myCartItemsModel.getCancellationPolicy()).getAsJsonArray();
            for (JsonElement element : cancellationArray) {
                combinedCancellationArray.add(element);
            }
        }
        object.add("cancellationPolicy", combinedCancellationArray);

        return object;
    }

    private JsonObject getJsonObjectForWhsoinCustomTicket() {
        myCartItemsModel.getCancellationPolicy().removeIf( q -> tourOptionId.equals(String.valueOf(q.getOptionId())));
        JsonObject object = new JsonObject();
        object.addProperty("currency", Utils.getCurrency());
        object.addProperty("bookingType", raynaTicketDetailModel.getBookingType());
        object.addProperty("cartId", myCartItemsModel.get_id());

        AtomicReference<Float> totalAmount = new AtomicReference<>(0f);
        AtomicReference<Float> discountAmount = new AtomicReference<>(0f);
        AtomicReference<Float> finalAmount = new AtomicReference<>(0f);

        RaynaTicketManager.shared.selectedTourModelForWhosin.forEach(q->{
            totalAmount.set(totalAmount.get() + getWhosinCustomWithoutDiscountPrice(q));
            discountAmount.set(discountAmount.get() + getWhosinCustomTicketTotal(q));
            finalAmount.set(finalAmount.get() + getWhosinCustomTicketTotal(q));
        });



        if (myCartItemsModel.getTourDetails().size() > 1) {
            Set<String> filteredOptionIds = myCartItemsModel.getTourDetails().stream().map(MyCartTourDetailsModel::getOptionId).filter(id -> !id.equals(tourOptionId)).collect(Collectors.toSet());
            List<WhosinTicketTourOptionModel> list  = raynaTicketDetailModel.getWhosinTicketTourDataList().get(0).getOptionData().stream().filter(option -> filteredOptionIds.contains(String.valueOf(option.getTourOptionId()))).collect(Collectors.toList());
            list.forEach( q -> {
                Optional<MyCartTourDetailsModel> tmpMpdel = myCartItemsModel.getTourDetails().stream().filter(p -> p.getOptionId().equals(tourOptionId)).findFirst();
                tmpMpdel.ifPresent(q::updateValueForCart);
            });
            list.forEach( q -> {
                totalAmount.set(totalAmount.get() + getWhosinCustomWithoutDiscountPrice(q));
                discountAmount.set(discountAmount.get() + getWhosinCustomTicketTotal(q));
                finalAmount.set(finalAmount.get() + getWhosinCustomTicketTotal(q));
            });
        }


        float discount = totalAmount.get() - discountAmount.get();
        if (discount < 0){
            discount = 0;
        }

        totalAmount.set(Utils.changeCurrencyToAED(totalAmount.get()));
        finalAmount.set(Utils.changeCurrencyToAED(finalAmount.get()));

        object.addProperty("customTicketId", raynaTicketDetailModel.getId());
        object.addProperty("totalAmount", formatPlainAmount(totalAmount.get()));
        object.addProperty("discount", formatPlainAmount(discount));
        object.addProperty("amount", formatPlainAmount(finalAmount.get()));



        List<RaynaPassengerModel> passengers = myCartItemsModel.getPassengerModel();
        for (int i = 0; i < passengers.size(); i++) {
            passengers.get(i).setLeadPassenger(i == 0 ? 1 : 0);
        }
        JsonArray passengerArray = new Gson().toJsonTree(passengers).getAsJsonArray();
        object.add("passengers", passengerArray);

        JsonArray tourDetails = new JsonArray();

        RaynaTicketManager.shared.selectedTourModelForWhosin.forEach( p -> {
            JsonObject tourDetailObject = new JsonObject();

            tourDetailObject.addProperty("transferId", 0);
            tourDetailObject.addProperty("optionId", p.getTourOptionId());
            tourDetailObject.addProperty("tourId", p.getTourId());
            tourDetailObject.addProperty("pickup", p.getPickUpLocation());
            tourDetailObject.addProperty("startTime", p.getStartTime());
            tourDetailObject.addProperty("tourDate", p.getTourOptionSelectDate());
//            tourDetailObject.addProperty("serviceTotal", getServiceTotalForWhosinCustomType(p));
//            tourDetailObject.addProperty("whosinTotal", getWhosinCustomTicketTotal(p));

            tourDetailObject.addProperty("serviceTotal", Utils.changeCurrencyToAED(getServiceTotalForWhosinCustomType(p)));
            tourDetailObject.addProperty("whosinTotal", Utils.changeCurrencyToAED(getWhosinCustomTicketTotal(p)));
            tourDetailObject.addProperty("tourId", p.getTourId());

            tourDetailObject.addProperty("adult", p.getTmpAdultValue());
            if (p.getTmpAdultValue() != 0){
                tourDetailObject.addProperty("adultRate", p.getWithoutDiscountAdultPrice());
            }else {
                tourDetailObject.addProperty("adultRate", 0);

            }

            tourDetailObject.addProperty("child", p.getTmpChildValue());
            if (p.getTmpChildValue() != 0) {
                tourDetailObject.addProperty("childRate",p.getWithoutDiscountChildPrice());
            }else {
                tourDetailObject.addProperty("childRate",0);
            }
            tourDetailObject.addProperty("infant", p.getTmpInfantValue());
            if (p.getIsSlot() && p.getRaynaTimeSlotModel() != null) {
                tourDetailObject.addProperty("timeSlotId", p.getRaynaTimeSlotModel().getSlotId());
                tourDetailObject.addProperty("timeSlot", p.getRaynaTimeSlotModel().getTimeSlot());
            } else {
                tourDetailObject.addProperty("timeSlotId", 0);
                tourDetailObject.addProperty("timeSlot", p.getSlotText());
            }

            tourDetailObject.addProperty("departureTime",p.getDepartureTime());

            tourDetailObject.addProperty("adult_title", p.getAdultTitle());
            tourDetailObject.addProperty("child_title", p.getChildTitle());
            tourDetailObject.addProperty("infant_title", p.getInfantTitle());

            tourDetails.add(tourDetailObject);

        });

        if (myCartItemsModel.getTourDetails().size() > 1) {
            List<MyCartTourDetailsModel> list = myCartItemsModel.getTourDetails();
            list.removeIf( q -> tourOptionId.equals(String.valueOf(q.getOptionId())));
            for (MyCartTourDetailsModel item : list) {
                JsonObject jsonObject = new Gson().toJsonTree(item).getAsJsonObject();
                if (jsonObject.has("_id")) jsonObject.remove("_id");
                if (jsonObject.has("endTime")) jsonObject.remove("endTime");
                tourDetails.add(jsonObject);
            }
        }
        object.add("TourDetails", tourDetails);
//        JsonArray combinedCancellationArray = new JsonArray();
//        combinedCancellationArray.add(RaynaTicketManager.shared.cancellationObject);
//        if (myCartItemsModel.getCancellationPolicy() != null && !myCartItemsModel.getCancellationPolicy().isEmpty()){
//            combinedCancellationArray.add(new Gson().toJsonTree(myCartItemsModel.getCancellationPolicy()).getAsJsonArray());
//        }
//        object.add("cancellationPolicy", combinedCancellationArray);

        JsonArray combinedCancellationArray = new JsonArray();
        if (RaynaTicketManager.shared.cancellationObject != null) {
            for (JsonElement element : RaynaTicketManager.shared.cancellationObject) {
                combinedCancellationArray.add(element);
            }
        }
        if (myCartItemsModel.getCancellationPolicy() != null && !myCartItemsModel.getCancellationPolicy().isEmpty()) {
            JsonArray cancellationArray = new Gson().toJsonTree(myCartItemsModel.getCancellationPolicy()).getAsJsonArray();
            for (JsonElement element : cancellationArray) {
                combinedCancellationArray.add(element);
            }
        }
        object.add("cancellationPolicy", combinedCancellationArray);


        return object;
    }

    private JsonObject getJsonObjectForBigBus() {
        JsonObject object = new JsonObject();
        object.addProperty("currency", Utils.getCurrency());
        object.addProperty("bookingType", "octo");
        object.addProperty("cartId", myCartItemsModel.get_id());

        AtomicReference<Float> totalAmount = new AtomicReference<>(0f);
        AtomicReference<Float> discountAmount = new AtomicReference<>(0f);
        AtomicReference<Float> finalAmount = new AtomicReference<>(0f);

        RaynaTicketManager.shared.selectedTourModelForBigBus.forEach(q->{
            totalAmount.set(totalAmount.get() + getBigBusWithoutDiscountPrice(q));
            discountAmount.set(discountAmount.get() + getWhosinTotalForBigBus(q));
            finalAmount.set(finalAmount.get() + getWhosinTotalForBigBus(q));
        });


        float discount = totalAmount.get() - discountAmount.get();
        if (discount < 0){
            discount = 0;
        }

        totalAmount.set(Utils.changeCurrencyToAED(totalAmount.get()));
        finalAmount.set(Utils.changeCurrencyToAED(finalAmount.get()));

        object.addProperty("customTicketId", raynaTicketDetailModel.getId());


        object.addProperty("totalAmount",Utils.roundFloatValue(totalAmount.get()));

        object.addProperty("discount", Utils.roundFloatValue(discount));
        object.addProperty("amount", Utils.roundFloatValue(finalAmount.get()));


        List<RaynaPassengerModel> passengers = myCartItemsModel.getPassengerModel();
        for (int i = 0; i < passengers.size(); i++) {
            passengers.get(i).setLeadPassenger(i == 0 ? 1 : 0);
        }
        JsonArray passengerArray = new Gson().toJsonTree(passengers).getAsJsonArray();
        object.add("passengers", passengerArray);

        JsonArray tourDetails = new JsonArray();

        RaynaTicketManager.shared.selectedTourModelForBigBus.forEach( p -> {
            JsonObject tourDetailObject = new JsonObject();
            tourDetailObject.addProperty("tourId", p.getTourId());
            tourDetailObject.addProperty("optionId", p.getId());


//            tourDetailObject.addProperty("serviceTotal", getBigBusWithoutDiscountPrice(p));
//            tourDetailObject.addProperty("whosinTotal", getWhosinTotalForBigBus(p));

            tourDetailObject.addProperty("serviceTotal", Utils.changeCurrencyToAED(getBigBusWithoutDiscountPrice(p)));
            tourDetailObject.addProperty("whosinTotal", Utils.changeCurrencyToAED(getWhosinTotalForBigBus(p)));

            tourDetailObject.addProperty("adult", p.getTmpAdultValue());
            if (p.getTmpAdultValue() != 0) {
                tourDetailObject.addProperty("adultRate", p.getAdultPrice());
                BigBusUnitsItemModel unit = p.getUnitByType(AppConstants.ADULTS);
                if (unit != null) {
                    tourDetailObject.addProperty("adultId", unit.getId());
                } else {
                    tourDetailObject.addProperty("adultId", "");
                }

            } else {
                tourDetailObject.addProperty("adultRate", 0);
                tourDetailObject.addProperty("adultId", "");
            }

            tourDetailObject.addProperty("child", p.getTmpChildValue());
            if (p.getTmpChildValue() != 0) {
                tourDetailObject.addProperty("childRate", p.getChildPrice());
                BigBusUnitsItemModel unit = p.getUnitByType(AppConstants.CHILD);
                if (unit != null) {
                    tourDetailObject.addProperty("childId", unit.getId());
                } else {
                    tourDetailObject.addProperty("childId", "");
                }
            } else {
                tourDetailObject.addProperty("childRate", 0);
                tourDetailObject.addProperty("childId", "");
            }

            tourDetailObject.addProperty("infant", p.getTmpInfantValue());
            if (p.getTmpChildValue() != 0) {
                BigBusUnitsItemModel unit = p.getUnitByType(AppConstants.INFANT);
                if (unit != null) {
                    tourDetailObject.addProperty("infantId", unit.getId());
                } else {
                    tourDetailObject.addProperty("infantId", "");
                }
            } else {
                tourDetailObject.addProperty("infantId", "");
            }


            tourDetailObject.addProperty("transferId", 0);
            if (raynaTicketDetailModel.getBookingType().equals("hero-balloon") && p.getPickupPointsModel() != null) {
                tourDetailObject.addProperty("pickup", p.getPickUpPoint());
            } else {
                tourDetailObject.addProperty("pickup", "");
            }

            tourDetailObject.addProperty("timeSlotId", 0);
            tourDetailObject.addProperty("hotelId", 0);

            if (p.getTimeModel() != null){
                tourDetailObject.addProperty("startTime", p.getTimeModel().getOpeningHours().get(0).getFrom());
                tourDetailObject.addProperty("tourDate", p.getTimeModel().getId());
                tourDetailObject.addProperty("timeSlot", p.getTimeModel().getOpeningHours().get(0).getFrom() + " - " + p.getTimeModel().getOpeningHours().get(0).getTo());
            }


            tourDetailObject.addProperty("adult_title", p.getAdultTitle());
            tourDetailObject.addProperty("child_title", p.getChildTitle());
            tourDetailObject.addProperty("infant_title", p.getInfantTitle());

            tourDetails.add(tourDetailObject);

        });

        object.add("TourDetails", tourDetails);
        return object;
    }

    private float getServiceTotal(TourOptionsModel model) {
        float adult = model.getTmpAdultValue() * model.getAdultPriceRayna();
        float child = model.getTmpChildValue() * model.getChildPriceRayna();
        float infants = model.getTmpInfantValue() * model.getInfantPriceRayna();

        return Utils.roundFloatToFloat(adult + child + infants);
    }

    private float getServiceTotalForWhosinType(TourOptionsModel model) {
        float adult = model.getTmpAdultValue() * model.getWithoutDiscountAdultPrice();
        float child = model.getTmpChildValue() * model.getWithoutDiscountChildPrice();
        float infants = model.getTmpInfantValue() * model.getWithoutDiscountInfantPrice();
        return Utils.roundFloatToFloat(adult + child + infants);
    }

    private float getWhosinTotalForWhosinType(TourOptionsModel model) {
        float adult = model.getTmpAdultValue() * Utils.roundFloatToFloat(model.getAdultPrice());
        float child = model.getTmpChildValue() * Utils.roundFloatToFloat(model.getChildPrice());
        float infants = model.getTmpInfantValue() * Utils.roundFloatToFloat(model.getInfantPrice());
        return Utils.roundFloatToFloat(adult + child + infants);
    }

    private float getWhosinTotal(TourOptionsModel model) {
        float adult = model.getTmpAdultValue() * Utils.roundFloatToFloat(model.getAdultPrice());
        float child = model.getTmpChildValue() * Utils.roundFloatToFloat(model.getChildPrice());
        float infants = model.getTmpInfantValue() * Utils.roundFloatToFloat(model.getInfantPrice());
        float addonTotal = 0f;
        if ("whosin".equals(raynaTicketDetailModel.getBookingType())
                && model.get_id().equals(tourOptionId)
                && RaynaTicketManager.shared.selectedAddonModels != null
                && !RaynaTicketManager.shared.selectedAddonModels.isEmpty()) {
            for (TourOptionsModel addon : RaynaTicketManager.shared.selectedAddonModels) {
                addonTotal += addon.updatePrice();
            }
        }
        return Utils.roundFloatToFloat(adult + child + infants + addonTotal);
    }

    private float getWithoutDiscountPrice(TourOptionsModel model) {
        float adult = model.getTmpAdultValue() * Utils.roundFloatToFloat(model.getWithoutDiscountAdultPrice());
        float child = model.getTmpChildValue() * Utils.roundFloatToFloat(model.getWithoutDiscountChildPrice());
        float infants = model.getTmpInfantValue() * Utils.roundFloatToFloat(model.getWithoutDiscountInfantPrice());
        return adult + child + infants;
    }

    private float getWhosinTotalForTravelDesk(TravelDeskOptionDataModel model) {
        float adult = model.getTmpAdultValue() *  Utils.roundFloatToFloat(model.getAdultPrice());
        float child = model.getTmpChildValue() *  Utils.roundFloatToFloat(model.getChildPrice());
        float infants = model.getTmpInfantValue() *  Utils.roundFloatToFloat(model.getInfantPrice());
        return Utils.roundFloatToFloat(adult + child + infants);
    }

    private float getWithoutDiscountPriceForReavelDesk(TravelDeskOptionDataModel model) {
        float adult = model.getTmpAdultValue() * Utils.roundFloatToFloat(model.getWithoutDiscountAdultPrice());
        float child = model.getTmpChildValue() * Utils.roundFloatToFloat(model.getWithoutDiscountChildPrice());
        float infants = model.getTmpInfantValue() * Utils.roundFloatToFloat(model.getWithoutDiscountInfantPrice());
        return adult + child + infants;
    }

    private float getServiceTotalForTravelDesk(TravelDeskOptionDataModel model) {
        float adult = model.getTmpAdultValue() *  model.getPricePerAdultTravelDesk();
        float child = model.getTmpChildValue() *  model.getPricePerChildTravelDesk();
        float infants = model.getTmpInfantValue() *  model.getPricePerTripTravelDesk();

        return Utils.roundFloatToFloat(adult + child + infants);
    }

    private float getWhosinCustomWithoutDiscountPrice(WhosinTicketTourOptionModel model) {
        float adult = model.getTmpAdultValue() * Utils.roundFloatToFloat(model.getWithoutDiscountAdultPrice());
        float child = model.getTmpChildValue() * Utils.roundFloatToFloat(model.getWithoutDiscountChildPrice());
        float infants = model.getTmpInfantValue() * Utils.roundFloatToFloat(model.getWithoutDiscountInfantPrice());

        return adult + child + infants;

    }

    private float getWhosinCustomTicketTotal(WhosinTicketTourOptionModel model) {
        float adult = model.getTmpAdultValue() *  Utils.roundFloatToFloat(model.getAdultPrice());
        float child = model.getTmpChildValue() *  Utils.roundFloatToFloat(model.getChildPrice());
        float infants = model.getTmpInfantValue() *  Utils.roundFloatToFloat(model.getInfantPrice());

        return Utils.roundFloatToFloat(adult + child + infants);
    }

    private float getServiceTotalForWhosinCustomType(WhosinTicketTourOptionModel model) {
        float adult = model.getTmpAdultValue() * model.getWithoutDiscountAdultPrice();
        float child = model.getTmpChildValue() * model.getWithoutDiscountChildPrice();
        float infants = model.getTmpInfantValue() * model.getWithoutDiscountInfantPrice();

        return Utils.roundFloatToFloat(adult + child + infants);
    }

    private float getBigBusWithoutDiscountPrice(BigBusOptionsItemModel model) {
        float adult = model.getTmpAdultValue() * Utils.roundFloatToFloat(model.getWithoutDiscountAdultPrice());
        float child = model.getTmpChildValue() * Utils.roundFloatToFloat(model.getWithoutDiscountChildPrice());
        float infants = model.getTmpInfantValue() * Utils.roundFloatToFloat(model.getWithoutDiscountInfantPrice());

        return adult + child + infants;

    }

    private float getWhosinTotalForBigBus(BigBusOptionsItemModel model) {
        float adult = model.getTmpAdultValue() *  Utils.roundFloatToFloat(model.getAdultPrice());
        float child = model.getTmpChildValue() *  Utils.roundFloatToFloat(model.getChildPrice());
        float infants = model.getTmpInfantValue() *  Utils.roundFloatToFloat(model.getInfantPrice());

        return Utils.roundFloatToFloat(adult + child + infants);
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestRaynaTourOptions(String tourDate) {
        JsonObject object = new JsonObject();
        object.addProperty("tourId", RaynaTicketManager.shared.getTourId());
        object.addProperty("contractId", RaynaTicketManager.shared.getContractId());
        object.addProperty("date", tourDate);
        object.addProperty("noOfAdult", 1);
        object.addProperty("noOfChild", 0);
        object.addProperty("noOfInfant", 0);
        Log.d("raynaTicket", "requestRaynaTourOptions: " + object);
        showProgress();
        DataService.shared(activity).requestRaynaTourOptions(object, new RestCallback<ContainerListModel<TourOptionsModel>>(this) {
            @Override
            public void result(ContainerListModel<TourOptionsModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    if (error.contains("Session expired, please login again!")){
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), "Session expired, please login again!", aBoolean -> {
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
                    }

                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    originalTourOptionsList.clear();
                    originalTourOptionsList.addAll(model.data);
                    List<TourOptionsModel> tourOptionsModels = model.data.stream().filter(p -> p.getTourOptionId().toString().equals(tourOptionId)).collect(Collectors.toList());

                    mapTheData(new ArrayList<>(tourOptionsModels));
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    binding.tourOptionRecyclerView.setVisibility(View.VISIBLE);

                } else {
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    binding.tourOptionRecyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void requestRaynaTourOptionsForWhosin(String selectedDate) {
        JsonObject object = new JsonObject();
        object.addProperty("ticketId", raynaTicketDetailModel.getId());
        object.addProperty("date", selectedDate);
        object.addProperty("adults", 1);
        object.addProperty("childs", 0);
        object.addProperty("infants", 0);
        showProgress();
        DataService.shared(activity).requestRaynaWhosinAvailability(object, new RestCallback<ContainerListModel<TourOptionsModel>>(this) {
            @Override
            public void result(ContainerListModel<TourOptionsModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    if (error.contains("Session expired, please login again!")){
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), "Session expired, please login again!", aBoolean -> {
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
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    binding.tourOptionRecyclerView.setVisibility(View.VISIBLE);
                    handleTicketOption(model.data);
                } else {
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    binding.tourOptionRecyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void requestRaynaTourPolicyForWhosinType(TourOptionsModel tourOptionsModel) {

        showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ticketId", tourOptionsModel.getCustomTicketId());
        jsonObject.addProperty("optionId", tourOptionsModel.get_id());
        jsonObject.addProperty("date", tourOptionsModel.getTourOptionSelectDate());
        jsonObject.addProperty("time", tourOptionsModel.getAvailabilityTime());
        jsonObject.addProperty("adults", tourOptionsModel.getTmpAdultValue());
        jsonObject.addProperty("childs", tourOptionsModel.getTmpChildValue());
        jsonObject.addProperty("infants", tourOptionsModel.getTmpInfantValue());
        DataService.shared(activity).requestWhosinTicketTourPolicy(jsonObject, new RestCallback<ContainerListModel<RaynaWhosinBookingRulesModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaWhosinBookingRulesModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    String tmpError = "";
                    if (error.contains("You cannot book this tour on selected date due to cutoff time.")) {
                        tmpError = error + " for " + tourOptionsModel.getTitle();
                    } else {
                        tmpError = error;
                    }
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), tmpError);
                    RaynaTicketManager.shared.cancellationObject = new JsonArray();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    model.data.forEach(model1 -> {
                        JsonObject object = new JsonObject();
                        object.addProperty("ticketId", model1.getTicketId());
                        object.addProperty("optionId", model1.getOptionId());
                        object.addProperty("fromDate", model1.getFromDate());
                        object.addProperty("toDate", model1.getToDate());
                        object.addProperty("percentage", model1.getPercentage());
                        RaynaTicketManager.shared.cancellationObject.add(object);
                    });
                }

                requestCartOptionUpdate();

            }
        });
    }

    private void requestRaynaTourPolicy(TourOptionsModel tourOptionsModel) {
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
                requestCartOptionUpdate();
            }
        });
    }

    private void requestTravelDeskTourPolicy(TravelDeskOptionDataModel travelDeskOptionDataModel) {
        showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject = new JsonObject();
        jsonObject.addProperty("tourId", travelDeskOptionDataModel.getTourId());
        jsonObject.addProperty("optionId", travelDeskOptionDataModel.getId());
        if (TextUtils.isEmpty(travelDeskOptionDataModel.getTourOptionSelectDate())){
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
            jsonObject.addProperty("date", date);
        }else {
            jsonObject.addProperty("date", travelDeskOptionDataModel.getTourOptionSelectDate());
        }
        jsonObject.addProperty("adults", travelDeskOptionDataModel.getTmpAdultValue());
        jsonObject.addProperty("childs", travelDeskOptionDataModel.getTmpChildValue());
        jsonObject.addProperty("infant", travelDeskOptionDataModel.getTmpInfantValue());
        DataService.shared(activity).requestTravelDeskTicketTourPolicy(jsonObject, new RestCallback<ContainerListModel<TravelDeskCancellationPolicyModel>>(this) {
            @Override
            public void result(ContainerListModel<TravelDeskCancellationPolicyModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    String tmpError = "";
                    if (error.contains("You cannot book this tour on selected date due to cutoff time.") || error.contains("You cannot book this tour on the selected date due to cutoff time.")){
                        tmpError = error + " for " + travelDeskOptionDataModel.getName();
                    }else {
                        tmpError = error;
                    }
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), tmpError);
                    RaynaTicketManager.shared.cancellationObject = new JsonArray();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    model.data.forEach(model1 -> {
                        JsonObject object = new JsonObject();
                        object.addProperty("tourId", model1.getTourId());
                        object.addProperty("optionId",model1.getOptionId());
                        object.addProperty("fromDate", model1.getFromDate());
                        object.addProperty("toDate", model1.getToDate());
                        object.addProperty("percentage", model1.getPercentage());
                        RaynaTicketManager.shared.cancellationObject.add(object);
                    });
                }

                requestCartOptionUpdate();
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

    private void requestTicketDetail(String ticketId) {
        showProgress();
        DataService.shared(activity).requestRaynaCustomUserDetail(ticketId, new RestCallback<ContainerModel<RaynaTicketDetailModel>>(this) {
            @Override
            public void result(ContainerModel<RaynaTicketDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    model.getData().assignTourObject();
                    raynaTicketDetailModel = model.getData();
                    RaynaTicketManager.shared.raynaTicketDetailModel = model.getData();
                    if (myCartItemsModel.getBookingType().equals("travel-desk")){
                        if (raynaTicketDetailModel.getTravelDeskTourDataModelList() != null && !raynaTicketDetailModel.getTravelDeskTourDataModelList().isEmpty()) {
                            if (raynaTicketDetailModel.getTravelDeskTourDataModelList().get(0).getOptionDataModel() != null && !raynaTicketDetailModel.getTravelDeskTourDataModelList().get(0).getOptionDataModel().isEmpty()) {
                                List<TravelDeskOptionDataModel> list = raynaTicketDetailModel.getTravelDeskTourDataModelList().get(0).getOptionDataModel().stream().filter(p -> tourOptionId.equals(String.valueOf(p.getId()))).collect(Collectors.toList());
                                list.forEach( q -> {
                                    if (tourOptionId.equals(String.valueOf(q.getId()))){
                                        Optional<MyCartTourDetailsModel> tmpMpdel = myCartItemsModel.getTourDetails().stream().filter(p -> p.getOptionId().equals(tourOptionId)).findFirst();
                                        tmpMpdel.ifPresent(q::updateValueForCart);
                                    }
                                });
                                RaynaTicketManager.shared.selectTravelDeskOptionDataModels.addAll(list);
                                positionsOfAdapter.add("0");
                                travelDeskTicketTourOptionListAdapter.updateData(list);
                                updateButtonValueForTravelDesk();
                            }
                        }
                    } else if (myCartItemsModel.getBookingType().equals("whosin-ticket")) {
                        if (raynaTicketDetailModel.getWhosinTicketTourDataList().get(0).getOptionData() != null && !raynaTicketDetailModel.getWhosinTicketTourDataList().get(0).getOptionData().isEmpty()) {
                            List<WhosinTicketTourOptionModel> list = raynaTicketDetailModel.getWhosinTicketTourDataList().get(0).getOptionData().stream().filter(p -> tourOptionId.equals(String.valueOf(p.getTourOptionId()))).collect(Collectors.toList());
                            list.forEach( q -> {
                                if (tourOptionId.equals(String.valueOf(q.getTourOptionId()))){
                                    Optional<MyCartTourDetailsModel> tmpMpdel = myCartItemsModel.getTourDetails().stream().filter(p -> p.getOptionId().equals(tourOptionId)).findFirst();
                                    tmpMpdel.ifPresent(q::updateValueForCart);
                                }
                            });
                            RaynaTicketManager.shared.selectedTourModelForWhosin.addAll(list);
                            positionsOfAdapter.add("0");
                            whosinCustomTourOptionListForAdapter.updateData(list);
                            updateButtonValueForWhosinCustom();
                        }
                    } else if (myCartItemsModel.getBookingType().equals("octo") || myCartItemsModel.getBookingType().equals("hero-balloon") || myCartItemsModel.getBookingType().equals("big-bus")) {
                        if (raynaTicketDetailModel.getBigBusTourDataModels() != null && !raynaTicketDetailModel.getBigBusTourDataModels().isEmpty()) {
                            List<BigBusOptionsItemModel> list = raynaTicketDetailModel.getBigBusTourDataModels().get(0).getOptions().stream().filter(p -> tourOptionId.equals(p.getId())).collect(Collectors.toList());
                            list.forEach( q -> {
                                if (tourOptionId.equals(q.getId())){
                                    Optional<MyCartTourDetailsModel> tmpMpdel = myCartItemsModel.getTourDetails().stream().filter(p -> p.getOptionId().equals(tourOptionId)).findFirst();
                                    tmpMpdel.ifPresent(q::updateValueForCart);
                                }
                            });
                            RaynaTicketManager.shared.selectedTourModelForBigBus.addAll(list);
                            requestBigBusTimeSlot(RaynaTicketManager.shared.selectedTourModelForBigBus.get(0), data -> {
                                positionsOfAdapter.add("0");
                                bigBusTourOptionListAdapter.updateData(RaynaTicketManager.shared.selectedTourModelForBigBus);
                                updateButtonValue();
                            });

                        }
                    } else if (raynaTicketDetailModel.getBookingType().equals("rayna")) {
                        String date = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH).format(new Date());
                        requestRaynaTourOptions(date);
                    } else {
                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                        requestRaynaTourOptionsForWhosin(date);

//                        if (raynaTicketDetailModel.getOptionData() != null && !raynaTicketDetailModel.getOptionData().isEmpty()) {
//
//                        }

                    }

                }
            }
        });
    }

    private void requestCartOptionUpdate() {
        JsonObject object;
        switch (raynaTicketDetailModel.getBookingType()) {
            case "travel-desk":
                object = getJsonObjectForTravelDesk();
                break;
            case "whosin-ticket":
                object = getJsonObjectForWhsoinCustomTicket();
                break;
            case "big-bus":
            case "hero-balloon":
                object = getJsonObjectForBigBus();
                object.addProperty("bookingType", raynaTicketDetailModel.getBookingType());
                break;
            default:
                object = getJsonObject();
                break;
        }
        showProgress();
        DataService.shared(activity).requestCartOptionUpdate(object, new RestCallback<ContainerModel<RaynaTicketBookingModel>>(this) {
            @Override
            public void result(ContainerModel<RaynaTicketBookingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void requestWhosinCustomTicketTourPolicy(WhosinTicketTourOptionModel whosinTicketTourOptionModel) {
        showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("tourId", whosinTicketTourOptionModel.getTourId());
        jsonObject.addProperty("tourOptionId", whosinTicketTourOptionModel.getTourOptionId());
        if (TextUtils.isEmpty(whosinTicketTourOptionModel.getTourOptionSelectDate())){
            jsonObject.addProperty("date", whosinTicketTourOptionModel.getBookingDate());
        }else {
            jsonObject.addProperty("date", whosinTicketTourOptionModel.getTourOptionSelectDate());
        }
        if (whosinTicketTourOptionModel.getIsSlot() && whosinTicketTourOptionModel.getRaynaTimeSlotModel() != null && !TextUtils.isEmpty(whosinTicketTourOptionModel.getRaynaTimeSlotModel().getTimeSlot())){
            jsonObject.addProperty("time", whosinTicketTourOptionModel.getRaynaTimeSlotModel().getTimeSlot());
        }else {
            jsonObject.addProperty("time", whosinTicketTourOptionModel.getSlotText());
        }

        jsonObject.addProperty("adults", whosinTicketTourOptionModel.getTmpAdultValue());
        jsonObject.addProperty("childs", whosinTicketTourOptionModel.getTmpChildValue());
        Log.d("requestRaynaTourPolicy", "requestRaynaTourPolicy: " + jsonObject);
        DataService.shared(activity).requestWhosinCustomTicketTourPolicy(jsonObject, new RestCallback<ContainerListModel<RaynaWhosinBookingRulesModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaWhosinBookingRulesModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    String tmpError = "";
                    if (error.contains("You cannot book this tour on selected date due to cutoff time.")){
                        tmpError = error + " for " + whosinTicketTourOptionModel.getDisplayName();
                    }else {
                        tmpError = error;
                    }
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), tmpError);
                    RaynaTicketManager.shared.cancellationObject = new JsonArray();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    model.data.forEach(model1 -> {
                        JsonObject object = new JsonObject();
                        object.addProperty("tourId", model1.getTourId());
                        object.addProperty("optionId", model1.getOptionId());
                        object.addProperty("fromDate", model1.getFromDate());
                        object.addProperty("toDate", model1.getToDate());
                        object.addProperty("percentage", model1.getPercentage());
                        RaynaTicketManager.shared.cancellationObject.add(object);
                    });
                }

                requestCartOptionUpdate();

            }
        });
    }

    private void requestWhosinAvailability(WhosinTicketTourOptionModel whosinTicketTourOptionModel) {
        showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("tourId", whosinTicketTourOptionModel.getTourId());
        jsonObject.addProperty("tourOptionId", whosinTicketTourOptionModel.getTourOptionId());
        if (TextUtils.isEmpty(whosinTicketTourOptionModel.getTourOptionSelectDate())) {
            jsonObject.addProperty("date", whosinTicketTourOptionModel.getBookingDate());
        } else {
            jsonObject.addProperty("date", whosinTicketTourOptionModel.getTourOptionSelectDate());
        }
        if (whosinTicketTourOptionModel.getIsSlot() && whosinTicketTourOptionModel.getRaynaTimeSlotModel() != null && !TextUtils.isEmpty(whosinTicketTourOptionModel.getRaynaTimeSlotModel().getTimeSlot())) {
            jsonObject.addProperty("slotId", whosinTicketTourOptionModel.getRaynaTimeSlotModel().getSlotId());
        } else {
            jsonObject.addProperty("slotId", "0");
        }

        jsonObject.addProperty("adults", whosinTicketTourOptionModel.getTmpAdultValue());
        jsonObject.addProperty("childs", whosinTicketTourOptionModel.getTmpChildValue());
        Log.d("requestRaynaTourPolicy", "requestRaynaTourPolicy: " + jsonObject);
        DataService.shared(activity).requestCheckWhosinAvailability(jsonObject, new RestCallback<ContainerModel<WhosinAvailabilityModel>>(this) {
            @Override
            public void result(ContainerModel<WhosinAvailabilityModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    String tmpError = error + " for " + whosinTicketTourOptionModel.getDisplayName();
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), tmpError);
                    RaynaTicketManager.shared.cancellationObject = new JsonArray();
                    return;
                }
                if (model.getData() != null && model.getData().isAvailable()) {
                    requestWhosinCustomTicketTourPolicy(whosinTicketTourOptionModel);
                } else {
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), model.message + " for " + whosinTicketTourOptionModel.getDisplayName());
                }


            }
        });
    }

    private String getCleanOptionId(Object optionId) {
        if (optionId instanceof Number) {
            double val = ((Number) optionId).doubleValue();
            return (val == Math.floor(val)) ? String.valueOf((int) val) : String.valueOf(val);
        } else {
            return String.valueOf(optionId);
        }
    }

    private void requestBigBusTimeSlot(BigBusOptionsItemModel bigBusOptionsItemModel,CommanCallback<Boolean> callback) {
        showProgress();
        JsonObject jsonObject = getTimeSlotJsonObject(bigBusOptionsItemModel,"");
        DataService.shared(activity).requestOctoTourAvailability(jsonObject, new RestCallback<ContainerListModel<OctaTourAvailabilityModel>>(this) {
            @Override
            public void result(ContainerListModel<OctaTourAvailabilityModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    callback.onReceive(false);
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    for (BigBusPricingFromItemModel unit : model.data.get(0).getUnitPricing()) {
                        int currencyPrecision = (int) Math.pow(10, unit.getCurrencyPrecision());
                        int newNetValue = (int) Math.ceil((double) unit.getNet() / currencyPrecision);
                        int newNetBeforeDiscount = (int) Math.ceil((double) unit.getNetBeforeDiscount() / currencyPrecision);
                        for (BigBusUnitsItemModel bigBusOptionsItemModel : bigBusOptionsItemModel.getUnits()) {
                            if (bigBusOptionsItemModel.getId().equals(unit.getUnitId())) {
                                List<BigBusPricingFromItemModel> pricingForm = bigBusOptionsItemModel.getPricingFrom();
                                if (!pricingForm.isEmpty()) {
                                    pricingForm.get(0).setNet(newNetValue);
                                    pricingForm.get(0).setWithoutDiscountNet(newNetBeforeDiscount);
                                }
                                bigBusOptionsItemModel.setPricingFrom(pricingForm);
                            }
                        }
                    }

                    String targetId = bigBusOptionsItemModel.getId();
                    for (int i = 0; i < RaynaTicketManager.shared.selectedTourModelForBigBus.size(); i++) {
                        if (RaynaTicketManager.shared.selectedTourModelForBigBus.get(i).getId().equals(targetId)) {
                            RaynaTicketManager.shared.selectedTourModelForBigBus.set(i, bigBusOptionsItemModel);
                            break;
                        }
                    }

                    callback.onReceive(true);
                }else {
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

            Utils.updateNoteText(raynaTicketDetailModel.getTmpMinPax(),raynaTicketDetailModel.getTmpMaxPax(),viewHolder.binding.tvNote,model.getNotes());

            viewHolder.setUpAdultChildData(model);
            viewHolder.updateAddOns(model.getAddons(), model);

            viewHolder.setOnClickListeners(model);

            viewHolder.binding.tvOptionName.setText(model.getOptionDetail().getOptionName());
//            viewHolder.binding.tvOptionDescription.setText(model.getOptionDetail().getOptionDescription());

            viewHolder.binding.selectTourDateLayout.setText(model.getTourOptionSelectDate());
            if (model.getRaynaTimeSlotModel() == null && !TextUtils.isEmpty(model.getTourOptionSelectDate()) && !TextUtils.isEmpty(model.getSlotText())) {
                viewHolder.binding.viewLine1.setVisibility(View.VISIBLE);
                viewHolder.binding.timeSlotLayout.setVisibility(View.VISIBLE);
                viewHolder.binding.tourTimeSlotTv.setText(model.getSlotText());
            }else if (model.getRaynaTimeSlotModel() != null){
                viewHolder.binding.viewLine1.setVisibility(View.VISIBLE);
                viewHolder.binding.timeSlotLayout.setVisibility(View.VISIBLE);
                String ts = model.getRaynaTimeSlotModel().getAvailabilityTime();
                if (TextUtils.isEmpty(ts)) ts = model.getRaynaTimeSlotModel().getTimeSlot();
                viewHolder.binding.tourTimeSlotTv.setText(ts);
            }else {
                if (TextUtils.isEmpty(model.getTourOptionSelectDate())) {
                    viewHolder.binding.viewLine1.setVisibility(View.GONE);
                    viewHolder.binding.timeSlotLayout.setVisibility(View.GONE);
                } else {
                    viewHolder.binding.viewLine1.setVisibility(View.VISIBLE);
                    viewHolder.binding.timeSlotLayout.setVisibility(View.VISIBLE);
                    if (model.getRaynaTimeSlotModel() != null){
                        String ts = model.getRaynaTimeSlotModel().getAvailabilityTime();
                        if (TextUtils.isEmpty(ts)) ts = model.getRaynaTimeSlotModel().getTimeSlot();
                        viewHolder.binding.tourTimeSlotTv.setText(ts);
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


            viewHolder.binding.expandedArrow.setVisibility(View.GONE);
            viewHolder.updatePaxBg(model);


            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.10f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemRaynaTicketOptionViewBinding binding;
            private final AddOnAdapter<TourOptionsModel> addOnAdapter = new AddOnAdapter<>();

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemRaynaTicketOptionViewBinding.bind(itemView);
                binding.recyclerViewAddOns.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                binding.recyclerViewAddOns.setAdapter(addOnAdapter);
                binding.recyclerViewAddOns.setNestedScrollingEnabled(false);
                updateLangValue(binding);
            }

            private void updateAddOns(List<TourOptionsModel> addOns, TourOptionsModel selectedModel) {
                if (addOns == null || addOns.isEmpty()) {
                    addOnAdapter.updateData(new ArrayList<>());
                } else {
                    addOnAdapter.whosinTicketTourOptionModel = selectedModel == null ? new TourOptionsModel() : selectedModel;
                    addOnAdapter.updateData(addOns);
                    // When an addon is updated via the AddOnAdapter, refresh totals and UI
                    addOnAdapter.onAddonUpdated = updatedModel -> {
                        updateButtonValue();
                        notifyItemChanged(getAdapterPosition());
                    };
                }
            }


            private void loadOptionImage(TourOptionDetailModel model){

                RaynaTicketDetailModel ticketDetailModel = raynaTicketDetailModel;

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
                    binding.addQuantityAdult.setVisibility(View.VISIBLE);
                    binding.addQuantityChild.setVisibility(!model.getDisableChild() ? View.VISIBLE : View.GONE);
                    binding.addQuantityInfants.setVisibility(!model.getDisableInfant() ? View.VISIBLE : View.GONE);

                    binding.tvTotalAdult.setText(String.valueOf(model.getTmpAdultValue()));
                    binding.tvTotalChild.setText(String.valueOf(model.getTmpChildValue()));
                    binding.tvTotalInfants.setText(String.valueOf(model.getTmpInfantValue()));

                    updateAdultChildInfantValue(model);
                    if (model.getOptionDetail().getAdultAge() != null && !model.getOptionDetail().getAdultAge().isEmpty()) {
                        binding.adultAge.setText( "(" +model.getOptionDetail().getAdultAge() + ")");
                    } else {
                        binding.adultAge.setText("");
                    }
                    if (model.getOptionDetail().getChildAge() != null && !model.getOptionDetail().getChildAge().isEmpty()) {
                        binding.childrenAge.setText("(" + model.getOptionDetail().getChildAge() + ")");
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

                int currentValue = Utils.getNumericValue(textView);

                if (isIncrement) {
                    if (model.isFirestTimeUpdate() && raynaTicketDetailModel.getTmpMinPax() != 0) {
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
                            String oldSlotId = oldSlot != null ? oldSlot.getTimeSlotId() : "";
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
                                String ts = model.getRaynaTimeSlotModel().getAvailabilityTime();
                                if (TextUtils.isEmpty(ts)) ts = model.getRaynaTimeSlotModel().getTimeSlot();
                                binding.tourTimeSlotTv.setText(ts);
                            }

                            boolean isPresent = positionsOfAdapter.stream().anyMatch(item -> item.equalsIgnoreCase(String.valueOf(getAdapterPosition())));
                            if (!isPresent && model.hasAtLeastOneMember()) {
                                positionsOfAdapter.add(String.valueOf(getAdapterPosition()));
                                RaynaTicketManager.shared.selectedTourModel.add(model);
                                notifyItemChanged(getAdapterPosition());
                                updateButtonValue();
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
                                        java.util.Optional<MyCartTourDetailsModel> cartItem = myCartItemsModel.getTourDetails().stream().filter(p -> tourOptionId.equals(p.getOptionId())).findFirst();
                                        cartItem.ifPresent(ci -> {
                                            ci.setAddons(new java.util.ArrayList<>());
                                        });
                                        if (RaynaTicketManager.shared.object.has("Addons")) {
                                            com.google.gson.JsonArray arr = RaynaTicketManager.shared.object.getAsJsonArray("Addons");
                                            com.google.gson.JsonArray newArr = new com.google.gson.JsonArray();
                                            for (com.google.gson.JsonElement el : arr) {
                                                com.google.gson.JsonObject obj = el.getAsJsonObject();
                                                String tourId = obj.has("tourId") ? obj.get("tourId").getAsString() : "";
                                                // Drop all addons belonging to the current option being edited
                                                if (!tourOptionId.equals(tourId)) {
                                                    newArr.add(obj);
                                                }
                                            }
                                            RaynaTicketManager.shared.object.add("Addons", newArr);
                                        }
                                    }
                                }
                            }
                            updateAddOns(model.getAddons(), model);
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
                binding.detailLayout.setBackground(ContextCompat.getDrawable(activity, drawableRes));

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

    }

    private class WhosinTicketTourOptionListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

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

            int maxTotalAllowed = 0;
            if (model.getAvailabilityType().equals("slot")) {
                OptionalInt tmpTotalAllowed = model.getAvailabilityTimeSlot().stream().mapToInt(RaynaTimeSlotModel::getTotalSeats).max();
                if (tmpTotalAllowed.isPresent()) maxTotalAllowed = tmpTotalAllowed.getAsInt();
            } else {
                maxTotalAllowed = model.getTotalSeats();
            }
            Utils.updateNoteText(model.getTmpMinPax(),maxTotalAllowed,viewHolder.binding.tvNote,model.getNotes());

            viewHolder.setUpAdultChildData(model);

            viewHolder.setOnClickListeners(model);

            viewHolder.binding.tvOptionName.setText(model.getTitle());
//            viewHolder.binding.tvOptionDescription.setText(model.getDescription());

            viewHolder.binding.selectTourDateLayout.setText(model.getTourOptionSelectDate());

            if (TextUtils.isEmpty(model.getTourOptionSelectDate())) {
                viewHolder.binding.viewLine1.setVisibility(View.GONE);
                viewHolder.binding.timeSlotLayout.setVisibility(View.GONE);
            } else {
                viewHolder.binding.viewLine1.setVisibility(View.VISIBLE);
                viewHolder.binding.timeSlotLayout.setVisibility(View.VISIBLE);
                if (model.getAvailabilityType().equals("regular")) {
                    viewHolder.binding.tourTimeSlotTv.setText(model.getAvailabilityTime());
                }else if (model.getAvailabilityType().equals("slot")){
                    if (model.getSlotModelForWhosinTicket() != null){
                        viewHolder.binding.tourTimeSlotTv.setText(model.getSlotModelForWhosinTicket().getAvailabilityTime());
                    }
                }
            }


//            if (!TextUtils.isEmpty(model.getDescription())) {
//                viewHolder.binding.tvOptionDescription.setVisibility(View.VISIBLE);
//                Utils.addSeeMore(viewHolder.binding.tvOptionDescription, Html.fromHtml(model.getDescription()), 1, "... " + getValue("see_more"), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ReadMoreBottomSheet bottomSheet = new ReadMoreBottomSheet();
//                        bottomSheet.title = getValue("description");
//                        bottomSheet.formattedDescription = model.getDescription();
//                        bottomSheet.show(getSupportFragmentManager(),"");
//                    }
//                });
//            } else {
//                viewHolder.binding.tvOptionDescription.setVisibility(View.GONE);
//            }

            if (model.getAddons() == null || model.getAddons().isEmpty()) {
                viewHolder.binding.addOnLayout.setVisibility(View.GONE);
                viewHolder.binding.dividerWithTextLayout.setVisibility(GONE);
                viewHolder.updateAddOns(null, null);
            } else {
                viewHolder.binding.addOnLayout.setVisibility(View.VISIBLE);
                viewHolder.binding.dividerWithTextLayout.setVisibility(View.VISIBLE);
                viewHolder.updateAddOns(model.getAddons(), model);
            }


            viewHolder.loadOptionImage(model);


            viewHolder.binding.selectSpinnerLayout.setVisibility(View.GONE);

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
            viewHolder.binding.expandedArrow.setVisibility(View.GONE);

            viewHolder.updatePaxBg(model);

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.10f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemRaynaTicketOptionViewBinding binding;
            private final AddOnAdapter<TourOptionsModel> addOnAdapter = new AddOnAdapter<>();

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemRaynaTicketOptionViewBinding.bind(itemView);
                binding.recyclerViewAddOns.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                binding.recyclerViewAddOns.setAdapter(addOnAdapter);
                binding.recyclerViewAddOns.setNestedScrollingEnabled(false);
                updateLangValue(binding);
            }

            private void updateAddOns(List<TourOptionsModel> addOns, TourOptionsModel selectedModel) {
                if (addOns == null || addOns.isEmpty()) {
                    addOnAdapter.updateData(new ArrayList<>());
                } else {
                    addOnAdapter.whosinTicketTourOptionModel = selectedModel == null ? new TourOptionsModel() : selectedModel;
                    addOnAdapter.updateData(addOns);
                    // When an addon is updated via the AddOnAdapter, refresh totals and UI
                    addOnAdapter.onAddonUpdated = updatedModel -> {
                        updateButtonValue();
                        notifyItemChanged(getAdapterPosition());
                    };
                }
            }


            private void loadOptionImage(TourOptionsModel model){

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
                    binding.addQuantityAdult.setVisibility(View.VISIBLE);
                    binding.addQuantityChild.setVisibility(!model.getDisableChild() ? View.VISIBLE : View.GONE);
                    binding.addQuantityInfants.setVisibility(!model.getDisableInfant() ? View.VISIBLE : View.GONE);

                    binding.tvTotalAdult.setText(String.valueOf(model.getTmpAdultValue()));
                    binding.tvTotalChild.setText(String.valueOf(model.getTmpChildValue()));
                    binding.tvTotalInfants.setText(String.valueOf(model.getTmpInfantValue()));

                    updateAdultChildInfantValue(model);
                    if (model.getAdultAge() != null && !model.getAdultAge().isEmpty()) {
                        binding.adultAge.setText( "(" +model.getAdultAge() + ")");
                    } else {
                        binding.adultAge.setText("");
                    }
                    if (model.getChildAge() != null && !model.getChildAge().isEmpty()) {
                        binding.childrenAge.setText("(" + model.getChildAge() + ")");
                    } else {
                        binding.childrenAge.setText("");
                    }
                    if (model.getInfantAge() != null && !model.getInfantAge().isEmpty()) {
                        binding.infantAge.setText("(" + model.getInfantAge() + ")");
                    } else {
                        binding.infantAge.setText("");
                    }
//                    binding.adultAge.setText( "(" +model.getAdultAge() + ")");
//                    binding.childrenAge.setText("(" +model.getChildAge()+ ")");
//                    binding.infantAge.setText("(" +model.getInfantAge()+ ")");

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

            private void updateCount(TextView textView, boolean isIncrement, LinearLayout linearLayout,TourOptionsModel model) {
                String unit = model.getUnit();
                if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                if (isIncrement){
//                    if (model.isWhosinMaxPax()){
//                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("max_pax_alert",String.valueOf(model.getTmpMaxPax()),unit));
//                        return;
//                    }

                    int total = model.getTmpAdultValue() + model.getTmpChildValue() + model.getTmpInfantValue();
                    int maxTotalAllowed = 0;
                    if (model.getAvailabilityType().equals("slot")) {
                        OptionalInt tmpTotalAllowed = model.getAvailabilityTimeSlot().stream().mapToInt(RaynaTimeSlotModel::getTotalSeats).max();
                        if (tmpTotalAllowed.isPresent()) maxTotalAllowed = tmpTotalAllowed.getAsInt();
                    } else {
                        maxTotalAllowed = model.getTotalSeats();
                    }

                    if (total + 1 > maxTotalAllowed) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), "A maximum of " + maxTotalAllowed + " passenger can be selected.");
                        return;
                    }
                }

                int currentValue = Utils.getNumericValue(textView);

                if (isIncrement) {
                    if (model.isFirestTimeUpdate() && model.getTmpMinPax() != 0) {
                        currentValue = model.getTmpMinPax();
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

            private void updateCountForInfant(TextView textView, boolean isIncrement,TourOptionsModel tourOptionsModel) {
                String unit = tourOptionsModel.getUnit();
                if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                if (isIncrement){
//                    if (tourOptionsModel.isWhosinMaxPax()){
//                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("max_pax_alert",String.valueOf(tourOptionsModel.getTmpMaxPax()),unit));
//                        return;
//                    }

                    int total = tourOptionsModel.getTmpAdultValue() + tourOptionsModel.getTmpChildValue() + tourOptionsModel.getTmpInfantValue();
                    int maxTotalAllowed = 0;
                    if (tourOptionsModel.getAvailabilityType().equals("slot")) {
                        OptionalInt tmpTotalAllowed = tourOptionsModel.getAvailabilityTimeSlot().stream().mapToInt(RaynaTimeSlotModel::getTotalSeats).max();
                        if (tmpTotalAllowed.isPresent()) maxTotalAllowed = tmpTotalAllowed.getAsInt();
                    } else {
                        maxTotalAllowed = tourOptionsModel.getTotalSeats();
                    }

                    if (total + 1 > maxTotalAllowed) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), "A maximum of " + maxTotalAllowed + " passenger can be selected.");
                        return;
                    }
                }

                int currentValue = Utils.getNumericValue(textView);
                int adultCount = Utils.getNumericValue(binding.tvTotalAdult);
                if (adultCount == 0) return;
                int maxInfantsAllowed = (int) Math.ceil(adultCount / 4.0);

                if (isIncrement && currentValue < maxInfantsAllowed) {
                    currentValue++;
                } else if (!isIncrement && currentValue > 0) {
                    currentValue--;
                }

                textView.setText(String.valueOf(currentValue));

                TourOptionsModel model = (TourOptionsModel) getItem(getAdapterPosition());
                if (model != null) {
                    model.setTmpInfantValue(currentValue);
                    updateAdultChildInfantValue(model);
                }

                boolean isPresent = positionsOfAdapter.stream().anyMatch(item -> item.equalsIgnoreCase(String.valueOf(getAdapterPosition())));
                if (!isPresent) {
                    positionsOfAdapter.add(String.valueOf(getAdapterPosition()));
                    RaynaTicketManager.shared.selectedTourModel.add(model);
                    notifyItemChanged(getAdapterPosition());
                }

                if (model != null && !model.hasAtLeastOneMember()){
                    refreshModel(model);
                    positionsOfAdapter.remove(String.valueOf(getAdapterPosition()));
                    RaynaTicketManager.shared.selectedTourModel.remove(model);
                    notifyItemChanged(getAdapterPosition());
                }

                updateButtonValue();
                hapticFeedback();
                updatePaxBg(model);
            }

            private void setOnClickListeners(TourOptionsModel model){
                // Adults
                binding.ivMinusAdult.setOnClickListener(view -> updateCount(binding.tvTotalAdult, false,binding.addQuantityAdult,model));
                binding.ivPlusAdult.setOnClickListener(view -> updateCount(binding.tvTotalAdult, true,binding.addQuantityAdult,model));

                // Children
                binding.ivMinusChild.setOnClickListener(view -> updateCount(binding.tvTotalChild, false,binding.addQuantityChild,model));
                binding.ivPlusChild.setOnClickListener(view -> updateCount(binding.tvTotalChild, true,binding.addQuantityChild,model));

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
                    selectDateTimeDialog.isWhosinTicketType = true;
                    selectDateTimeDialog.callback = data -> {
                        if (data != null) {
                            String oldDate = model.getTourOptionSelectDate();
                            int oldSlotPosition = model.whosinTypeTicketSlotPosition;
                            binding.dateTimeLayout.setBackground(ContextCompat.getDrawable(activity,R.drawable.selected_tour_option_people_stock_bg));


                            model.setTourOptionSelectDate(data.getTourOptionSelectDate());
                            binding.selectTourDateLayout.setText(data.getTourOptionSelectDate());

                            binding.viewLine1.setVisibility(View.VISIBLE);
                            binding.timeSlotLayout.setVisibility(View.VISIBLE);
                            if (model.getAvailabilityType().equals("regular")){
                                binding.tourTimeSlotTv.setText(model.getAvailabilityTime());
                            }else if (model.getAvailabilityType().equals("slot")){
                                model.whosinTypeTicketSlotPosition = data.whosinTypeTicketSlotPosition;
                                if (model.getSlotModelForWhosinTicket() != null){
                                    binding.tourTimeSlotTv.setText(model.getSlotModelForWhosinTicket().getAvailabilityTime());
                                }
                            }

                            boolean isPresent = positionsOfAdapter.stream().anyMatch(item -> item.equalsIgnoreCase(String.valueOf(getAdapterPosition())));
                            if (!isPresent && model.hasAtLeastOneMember()) {
                                positionsOfAdapter.add(String.valueOf(getAdapterPosition()));
                                RaynaTicketManager.shared.selectedTourModel.add(model);
                                notifyItemChanged(getAdapterPosition());
                                updateButtonValue();
                            }
                            boolean isDateChanged = !TextUtils.equals(oldDate, data.getTourOptionSelectDate());
                            boolean isSlotChanged = false;
                            if (model.getAvailabilityType().equals("slot")) {
                                isSlotChanged = oldSlotPosition != data.whosinTypeTicketSlotPosition;
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
                                        java.util.Optional<MyCartTourDetailsModel> cartItem = myCartItemsModel.getTourDetails().stream().filter(p -> tourOptionId.equals(p.getOptionId())).findFirst();
                                        cartItem.ifPresent(ci -> {
                                            ci.setAddons(new java.util.ArrayList<>());
                                        });
                                        if (RaynaTicketManager.shared.object.has("Addons")) {
                                            com.google.gson.JsonArray arr = RaynaTicketManager.shared.object.getAsJsonArray("Addons");
                                            com.google.gson.JsonArray newArr = new com.google.gson.JsonArray();
                                            for (com.google.gson.JsonElement el : arr) {
                                                com.google.gson.JsonObject obj = el.getAsJsonObject();
                                                String tourId = obj.has("tourId") ? obj.get("tourId").getAsString() : "";
                                                if (!tourOptionId.equals(tourId)) {
                                                    newArr.add(obj);
                                                }
                                            }
                                            RaynaTicketManager.shared.object.add("Addons", newArr);
                                        }
                                    }
                                }
                            }
                            updateAddOns(model.getAddons(), model);
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
                    bottomSheet.isFromRaynaWhosinTicket = true;
                    bottomSheet.isNonRefundable = !TextUtils.isEmpty(model.getCancellationPolicy()) && model.getCancellationPolicy().equalsIgnoreCase("Non Refundable");
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

                if (model.updateAdultPrices() == 0) {
                    binding.adultsPrice.setVisibility(GONE);
                } else {
                    binding.adultsPrice.setVisibility(View.VISIBLE);
                    Utils.setStyledText(activity, binding.adultsPrice, Utils.roundFloatValue(model.updateAdultPrices()));
                }

                if (model.updateChildPrices() == 0) {
                    binding.childPrice.setVisibility(GONE);
                } else {
                    binding.childPrice.setVisibility(View.VISIBLE);
                    Utils.setStyledText(activity, binding.childPrice, Utils.roundFloatValue(model.updateChildPrices()));
                }

                if (model.updateInfantPrices() == 0) {
                    binding.infantsPrice.setVisibility(GONE);
                } else {
                    binding.infantsPrice.setVisibility(View.VISIBLE);
                    Utils.setStyledText(activity, binding.infantsPrice, Utils.roundFloatValue(model.updateInfantPrices()));
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
                model.setTourOptionSelectDate("");
                model.setFirestTimeUpdate(true);
            }

        }

    }

    private class TravelDeskTicketTourOptionListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_rayna_ticket_option_view));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            TravelDeskOptionDataModel model = (TravelDeskOptionDataModel) getItem(position);
            boolean isLastItem = position == getItemCount() - 1;
            if (model == null) return;

            Utils.updateNoteText(model.getMinNumOfPeople(),model.getMaxNumOfPeople(),viewHolder.binding.tvNote,model.getNotes());

            viewHolder.setUpAdultChildData(model);

            viewHolder.setOnClickListeners(model);

            viewHolder.binding.tvOptionName.setText(model.getName());

            viewHolder.binding.selectSpinnerLayout.setVisibility(GONE);

            viewHolder.binding.selectTourDateLayout.setText(model.getTourOptionSelectDate());

            if (TextUtils.isEmpty(model.getTourOptionSelectDate()) || model.getTravelDeskAvailabilityModel() == null) {
                viewHolder.binding.viewLine1.setVisibility(View.GONE);
                viewHolder.binding.timeSlotLayout.setVisibility(View.GONE);
            } else {
                viewHolder.binding.viewLine1.setVisibility(View.VISIBLE);
                viewHolder.binding.timeSlotLayout.setVisibility(View.VISIBLE);
                viewHolder.binding.tourTimeSlotTv.setText(model.getTravelDeskAvailabilityModel().getSlotText());
            }



//            if (!TextUtils.isEmpty(model.getDescription())) {
//                viewHolder.binding.tvOptionDescription.setVisibility(View.VISIBLE);
//                Utils.addSeeMore(viewHolder.binding.tvOptionDescription, Html.fromHtml(model.getDescription()), 1, "... " + getValue("see_more"), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ReadMoreBottomSheet bottomSheet = new ReadMoreBottomSheet();
//                        bottomSheet.title = getValue("description");
//                        bottomSheet.formattedDescription = model.getDescription();
//                        bottomSheet.show(getSupportFragmentManager(),"");
//                    }
//                });
//            } else {
//                viewHolder.binding.tvOptionDescription.setVisibility(View.GONE);
//            }


            viewHolder.loadOptionImage(model);

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
            viewHolder.binding.expandedArrow.setVisibility(View.GONE);
            viewHolder.updatePaxBg(model);

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.10f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemRaynaTicketOptionViewBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemRaynaTicketOptionViewBinding.bind(itemView);
                updateLangValue(binding);
            }


            private void loadOptionImage(TravelDeskOptionDataModel model) {
                if (model == null || binding == null) return;

                RaynaTicketDetailModel raynaModel = RaynaTicketManager.shared.raynaTicketDetailModel;
                String tmpImage = null;

                TravelDeskHeroImageModel heroImage = model.getHeroImage();

                if (heroImage == null && raynaModel != null &&
                        raynaModel.getTravelDeskTourDataModelList() != null &&
                        !raynaModel.getTravelDeskTourDataModelList().isEmpty()) {

                    heroImage = raynaModel.getTravelDeskTourDataModelList().get(0).getHeroImage();
                }

                if (heroImage != null && heroImage.getSrcSet() != null && !heroImage.getSrcSet().isEmpty() && heroImage.getSrcSet().get(0) != null
                        && heroImage.getSrcSet().get(0).getSizes() != null
                        && !heroImage.getSrcSet().get(0).getSizes().isEmpty()
                        && heroImage.getSrcSet().get(0).getSizes().get(0) != null
                        && !TextUtils.isEmpty(heroImage.getSrcSet().get(0).getSizes().get(0).getSrc())) {
                    tmpImage = heroImage.getSrcSet().get(0).getSizes().get(0).getSrc();
                    Graphics.loadImage(tmpImage, binding.ticketOptionImage);

                } else if (raynaModel != null && raynaModel.getImages() != null && !raynaModel.getImages().isEmpty()) {
                    for (String image : raynaModel.getImages()) {
                        if (!TextUtils.isEmpty(image) && !Utils.isVideo(image)) {
                            tmpImage = image;
                            Graphics.loadImage(image, binding.ticketOptionImage);
                            break;
                        }
                    }
                }

                String finalTmpImage = tmpImage;
                binding.ticketOptionImage.setOnClickListener(v -> {
                    if (!TextUtils.isEmpty(finalTmpImage)) {
                        Intent intent = new Intent(activity, ProfileFullScreenImageActivity.class);
                        intent.putExtra(ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, finalTmpImage);
                        activity.startActivity(intent);
                    }
                });
            }


            private void setUpAdultChildData(TravelDeskOptionDataModel model) {

                activity.runOnUiThread(() -> {
                    binding.addQuantityAdult.setVisibility(View.VISIBLE);
                    binding.addQuantityChild.setVisibility(model.isChildrenAllowed() ? View.VISIBLE : View.GONE);
                    binding.addQuantityInfants.setVisibility(model.isInfantsAllowed() ? View.VISIBLE : View.GONE);

                    binding.tvTotalAdult.setText(String.valueOf(model.getTmpAdultValue()));
                    binding.tvTotalChild.setText(String.valueOf(model.getTmpChildValue()));
                    binding.tvTotalInfants.setText(String.valueOf(model.getTmpInfantValue()));

                    updateAdultChildInfantValue(model);

                    binding.childrenAge.setText("(" +model.getChildAge()+ ")");
                    binding.infantAge.setText("(" +model.getInfantAge()+ ")");

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

            private void updateCount(TextView textView, boolean isIncrement, TravelDeskOptionDataModel model) {
                String unit = model.getUnit();
                if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                if (isIncrement){
                    int total = model.getTmpAdultValue() + model.getTmpChildValue() + model.getTmpInfantValue();
                    if (total + 1 > model.getMaxNumOfPeople()) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("max_pax_alert",String.valueOf(model.getMaxNumOfPeople()),unit));
                        return;
                    }
                }

                int currentValue = Utils.getNumericValue(textView);

                if (isIncrement) {
                    if (model.isFirestTimeUpdate() && model.getMinNumOfPeople() != 0) {
                        currentValue = model.getMinNumOfPeople();
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
                    RaynaTicketManager.shared.selectTravelDeskOptionDataModels.add(model);
                    notifyItemChanged(getAdapterPosition());
                }

                if (!model.hasAtLeastOneMember()){
                    refreshModel(model);
                    positionsOfAdapter.remove(String.valueOf(getAdapterPosition()));
                    RaynaTicketManager.shared.selectTravelDeskOptionDataModels.remove(model);
                    model.setTourOptionSelectDate("");
                    binding.dateTimeLayout.setBackground(ContextCompat.getDrawable(activity,R.drawable.tour_option_spinner_stock_bg));
                    notifyItemChanged(getAdapterPosition());
                }

                updateAdultChildInfantValue(model);
                updateButtonValueForTravelDesk();
                hapticFeedback();
                updatePaxBg(model);
            }

            private void updateCountForInfant(TextView textView, boolean isIncrement,TravelDeskOptionDataModel tourOptionsModel) {
                String unit = tourOptionsModel.getUnit();
                if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                if (isIncrement){
                    int total = tourOptionsModel.getTmpAdultValue() + tourOptionsModel.getTmpChildValue() + tourOptionsModel.getTmpInfantValue();
                    if (total + 1 > tourOptionsModel.getMaxNumOfPeople()) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("max_pax_alert",String.valueOf(tourOptionsModel.getMaxNumOfPeople()),unit));
                        return;
                    }
                }

                int currentValue = Utils.getNumericValue(textView);
                int adultCount = Utils.getNumericValue(binding.tvTotalAdult);
                if (adultCount == 0) return;
                int maxInfantsAllowed = (int) Math.ceil(adultCount / 4.0);

                if (isIncrement && currentValue < maxInfantsAllowed) {
                    currentValue++;
                } else if (!isIncrement && currentValue > 0) {
                    currentValue--;
                }

                textView.setText(String.valueOf(currentValue));

                TravelDeskOptionDataModel model = (TravelDeskOptionDataModel) getItem(getAdapterPosition());
                if (model != null) {
                    model.setTmpInfantValue(currentValue);
                }

                boolean isPresent = positionsOfAdapter.stream().anyMatch(item -> item.equalsIgnoreCase(String.valueOf(getAdapterPosition())));
                if (!isPresent) {
                    positionsOfAdapter.add(String.valueOf(getAdapterPosition()));
                    RaynaTicketManager.shared.selectTravelDeskOptionDataModels.add(tourOptionsModel);
                    notifyItemChanged(getAdapterPosition());
                }

                if (tourOptionsModel != null && !tourOptionsModel.hasAtLeastOneMember()){
                    refreshModel(tourOptionsModel);
                    positionsOfAdapter.remove(String.valueOf(getAdapterPosition()));
                    RaynaTicketManager.shared.selectTravelDeskOptionDataModels.remove(tourOptionsModel);
                    notifyItemChanged(getAdapterPosition());
                }

                updateButtonValueForTravelDesk();
                hapticFeedback();
                updatePaxBg(tourOptionsModel);
            }

            private void setOnClickListeners(TravelDeskOptionDataModel model){
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
                    TravelDeskDateTimePickerSheet selectDateTimeDialog = new TravelDeskDateTimePickerSheet(activity);
                    selectDateTimeDialog.tourOptionsModel = model;
                    selectDateTimeDialog.callback = data -> {
                        if (data != null) {

                            binding.dateTimeLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.selected_tour_option_people_stock_bg));


                            model.setTourOptionSelectDate(data.getTourOptionSelectDate());
                            binding.selectTourDateLayout.setText(data.getTourOptionSelectDate());


                            if (data.getTravelDeskAvailabilityModel() != null){
                                binding.viewLine1.setVisibility(View.VISIBLE);
                                binding.timeSlotLayout.setVisibility(View.VISIBLE);
                                model.setTravelDeskAvailabilityModel(data.getTravelDeskAvailabilityModel());
                                binding.tourTimeSlotTv.setText(model.getTravelDeskAvailabilityModel().getSlotText());

                                int newOfferId = data.getTravelDeskAvailabilityModel().getPrice().getOfferId();
                                TravelDeskPriceModel newPeriod = data.getTravelDeskAvailabilityModel().getPrice();

                                model.setPricingPeriods(model.getPricingPeriods().stream().map(p -> p.getOfferId() == newOfferId ? newPeriod : p).collect(Collectors.toList()));

                                String targetId = model.get_id();
                                for (int i = 0; i < RaynaTicketManager.shared.selectTravelDeskOptionDataModels.size(); i++) {
                                    if (RaynaTicketManager.shared.selectTravelDeskOptionDataModels.get(i).get_id().equals(targetId)) {
                                        RaynaTicketManager.shared.selectTravelDeskOptionDataModels.set(i, model);
                                        notifyItemChanged(getAdapterPosition());
                                        updateButtonValueForTravelDesk();
                                        break;
                                    }
                                }
                            }else {
                                binding.viewLine1.setVisibility(View.GONE);
                                binding.timeSlotLayout.setVisibility(View.VISIBLE);
                            }

                            boolean isPresent = positionsOfAdapter.stream().anyMatch(item -> item.equalsIgnoreCase(String.valueOf(getAdapterPosition())));
                            if (!isPresent && model.hasAtLeastOneMember()) {
                                positionsOfAdapter.add(String.valueOf(getAdapterPosition()));
                                notifyItemChanged(getAdapterPosition());
                                updateButtonValueForTravelDesk();
                            }


                        }
                    };
                    selectDateTimeDialog.show(getSupportFragmentManager(), "1");
                });

                // More Info
                binding.btnMoreInfo.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    RaynaMoreInfoBottomSheet bottomSheet = new RaynaMoreInfoBottomSheet();
                    bottomSheet.travelDeskOptionDataModel = model;
                    bottomSheet.activity = activity;
                    bottomSheet.isFromTravelDeskTicket = true;
                    bottomSheet.isNonRefundable = !TextUtils.isEmpty(raynaTicketDetailModel.getCancellationPolicy()) && raynaTicketDetailModel.getCancellationPolicy().equalsIgnoreCase("Non Refundable");
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

                if (finalAmount == 0){
                    mainTextView.setVisibility(View.GONE);
                }
            }

            private void updateAdultChildInfantValue(TravelDeskOptionDataModel model){
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

            private void updatePaxBg(TravelDeskOptionDataModel model){
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

            private void refreshModel(TravelDeskOptionDataModel model){
                model.setTmpAdultValue(0);
                model.setTmpChildValue(0);
                model.setTmpInfantValue(0);
                model.setTravelDeskAvailabilityModel(null);
                model.setTourOptionSelectDate("");
                model.setFirestTimeUpdate(true);

            }

        }

    }

    private class WhosinCustomTourOptionListForAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_rayna_ticket_option_view));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            WhosinTicketTourOptionModel model = (WhosinTicketTourOptionModel) getItem(position);
            boolean isLastItem = position == getItemCount() - 1;
            if (model == null) return;

            Utils.updateNoteText(model.getTmpMinPax(),model.getTmpMaxPax(),viewHolder.binding.tvNote,model.getNotes());

            viewHolder.setUpAdultChildData(model);

            viewHolder.setOnClickListeners(model);

            viewHolder.binding.tvOptionName.setText(model.getDisplayName());

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


//            if (!TextUtils.isEmpty(model.getOptionDescription())) {
//                viewHolder.binding.tvOptionDescription.setVisibility(View.VISIBLE);
//                Utils.addSeeMore(viewHolder.binding.tvOptionDescription, Html.fromHtml(model.getOptionDescription()), 1, "... " + getValue("see_more"), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ReadMoreBottomSheet bottomSheet = new ReadMoreBottomSheet();
//                        bottomSheet.title = getValue("description");
//                        bottomSheet.formattedDescription = model.getOptionDescription();
//                        bottomSheet.show(getSupportFragmentManager(),"");
//                    }
//                });
//            } else {
//                viewHolder.binding.tvOptionDescription.setVisibility(View.GONE);
//            }


            viewHolder.loadOptionImage(model);


            viewHolder.binding.selectSpinnerLayout.setVisibility(View.GONE);

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
            viewHolder.binding.expandedArrow.setVisibility(View.GONE);

            viewHolder.updatePaxBg(model);

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.10f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemRaynaTicketOptionViewBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemRaynaTicketOptionViewBinding.bind(itemView);
                updateLangValue(binding);
            }


            private void loadOptionImage(WhosinTicketTourOptionModel model){

                RaynaTicketDetailModel ticketDetailModel = RaynaTicketManager.shared.raynaTicketDetailModel;

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

            private void setUpAdultChildData(WhosinTicketTourOptionModel model) {

                activity.runOnUiThread(() -> {
                    binding.addQuantityAdult.setVisibility(View.VISIBLE);
                    binding.addQuantityChild.setVisibility(!model.getDisableChild() ? View.VISIBLE : View.GONE);
                    binding.addQuantityInfants.setVisibility(!model.getDisableInfant() ? View.VISIBLE : View.GONE);

                    binding.tvTotalAdult.setText(String.valueOf(model.getTmpAdultValue()));
                    binding.tvTotalChild.setText(String.valueOf(model.getTmpChildValue()));
                    binding.tvTotalInfants.setText(String.valueOf(model.getTmpInfantValue()));

                    updateAdultChildInfantValue(model);

                    if (model.getAdultAge() != null && !model.getAdultAge().isEmpty()) {
                        binding.adultAge.setText("(" + model.getAdultAge() + ")");
                    } else {
                        binding.adultAge.setText("");
                    }
                    if (model.getChildAge() != null && !model.getChildAge().isEmpty()) {
                        binding.childrenAge.setText("(" + model.getChildAge() + ")");
                    } else {
                        binding.childrenAge.setText("");
                    }
                    if (model.getInfantAge() != null && !model.getInfantAge().isEmpty()) {
                        binding.infantAge.setText("(" + model.getInfantAge() + ")");
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

            private void updateCount(TextView textView, boolean isIncrement, LinearLayout linearLayout, WhosinTicketTourOptionModel model) {
                String unit = model.getUnit();
                if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                if (isIncrement){
//                    if (model.isWhosinMaxPax()){
//                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), "A maximum of " + model.getTmpMaxPax() + " passenger can be selected.");
//                        return;
//                    }

                    int total = model.getTmpAdultValue() + model.getTmpChildValue() + model.getTmpInfantValue();
                    int maxTotalAllowed = model.getTmpMaxPax();
                    if (total + 1 > maxTotalAllowed) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("max_pax_alert",String.valueOf(maxTotalAllowed),unit));
                        return;
                    }
                }

                int currentValue = Utils.getNumericValue(textView);

                if (isIncrement) {
                    if (model.isFirestTimeUpdate() && model.getTmpMinPax() != 0) {
                        currentValue = model.getTmpMinPax();
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
                    RaynaTicketManager.shared.selectedTourModelForWhosin.add(model);
                    notifyItemChanged(getAdapterPosition());
                }

                if (!model.hasAtLeastOneMember()){
                    refreshModel(model);
                    positionsOfAdapter.remove(String.valueOf(getAdapterPosition()));
                    RaynaTicketManager.shared.selectedTourModelForWhosin.remove(model);
                    model.setTourOptionSelectDate("");
                    binding.dateTimeLayout.setBackground(ContextCompat.getDrawable(activity,R.drawable.tour_option_spinner_stock_bg));
                    notifyItemChanged(getAdapterPosition());
                }

                updateAdultChildInfantValue(model);
                updateButtonValueForWhosinCustom();
                hapticFeedback();
                updatePaxBg(model);

            }

            private void updateCountForInfant(TextView textView, boolean isIncrement,WhosinTicketTourOptionModel tourOptionsModel) {
                String unit = tourOptionsModel.getUnit();
                if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                if (isIncrement){
                    int total = tourOptionsModel.getTmpAdultValue() + tourOptionsModel.getTmpChildValue() + tourOptionsModel.getTmpInfantValue();
                    int maxTotalAllowed = tourOptionsModel.getTmpMaxPax();
                    if (total + 1 > maxTotalAllowed) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("max_pax_alert",String.valueOf(maxTotalAllowed),unit));
                        return;
                    }
                }

                int currentValue = Utils.getNumericValue(textView);
                int adultCount = Utils.getNumericValue(binding.tvTotalAdult);
                if (adultCount == 0) return;
                int maxInfantsAllowed = (int) Math.ceil(adultCount / 4.0);

                if (isIncrement && currentValue < maxInfantsAllowed) {
                    currentValue++;
                } else if (!isIncrement && currentValue > 0) {
                    currentValue--;
                }

                textView.setText(String.valueOf(currentValue));

                WhosinTicketTourOptionModel model = (WhosinTicketTourOptionModel) getItem(getAdapterPosition());
                if (model != null) {
                    model.setTmpInfantValue(currentValue);
                    updateAdultChildInfantValue(model);
                }

                boolean isPresent = positionsOfAdapter.stream().anyMatch(item -> item.equalsIgnoreCase(String.valueOf(getAdapterPosition())));
                if (!isPresent) {
                    positionsOfAdapter.add(String.valueOf(getAdapterPosition()));
                    RaynaTicketManager.shared.selectedTourModelForWhosin.add(model);
                    notifyItemChanged(getAdapterPosition());
                }

                if (model != null && !model.hasAtLeastOneMember()){
                    refreshModel(model);
                    positionsOfAdapter.remove(String.valueOf(getAdapterPosition()));
                    RaynaTicketManager.shared.selectedTourModelForWhosin.remove(model);
                    notifyItemChanged(getAdapterPosition());
                }

                updateButtonValueForWhosinCustom();
                hapticFeedback();
                updatePaxBg(model);
            }

            private void setOnClickListeners(WhosinTicketTourOptionModel model){
                // Adults
                binding.ivMinusAdult.setOnClickListener(view -> updateCount(binding.tvTotalAdult, false,binding.addQuantityAdult,model));
                binding.ivPlusAdult.setOnClickListener(view -> updateCount(binding.tvTotalAdult, true,binding.addQuantityAdult,model));

                // Children
                binding.ivMinusChild.setOnClickListener(view -> updateCount(binding.tvTotalChild, false,binding.addQuantityChild,model));
                binding.ivPlusChild.setOnClickListener(view -> updateCount(binding.tvTotalChild, true,binding.addQuantityChild,model));

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
                    WhosinCustomTicketDateAndTimeSheet selectDateTimeDialog = new WhosinCustomTicketDateAndTimeSheet();
                    selectDateTimeDialog.tourOptionsModel = model;
                    selectDateTimeDialog.callback = data -> {
                        if (data != null) {

                            binding.dateTimeLayout.setBackground(ContextCompat.getDrawable(activity,R.drawable.selected_tour_option_people_stock_bg));


                            model.setTourOptionSelectDate(data.getTourOptionSelectDate());
                            binding.selectTourDateLayout.setText(data.getTourOptionSelectDate());

                            binding.viewLine1.setVisibility(View.VISIBLE);
                            binding.timeSlotLayout.setVisibility(View.VISIBLE);

                            if (model.getIsSlot()){
                                model.setRaynaTimeSlotModel(data.getRaynaTimeSlotModel());
                            }
//                            if (model.getAvailabilityType().equals("regular")){
//                                binding.tourTimeSlotTv.setText(model.getAvailabilityTime());
//                            }else if (model.getAvailabilityType().equals("slot")){
//                                model.whosinTypeTicketSlotPosition = data.whosinTypeTicketSlotPosition;
//                                if (model.getSlotModelForWhosinTicket() != null){
//                                    binding.tourTimeSlotTv.setText(model.getSlotModelForWhosinTicket().getAvailabilityTime());
//                                }
//                            }

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
                                RaynaTicketManager.shared.selectedTourModelForWhosin.add(model);
                                notifyItemChanged(getAdapterPosition());
                                updateButtonValueForWhosinCustom();
                            }
                        }
                    };
                    selectDateTimeDialog.show(getSupportFragmentManager(), "1");
                });

                // More Info
                binding.btnMoreInfo.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    RaynaMoreInfoBottomSheet bottomSheet = new RaynaMoreInfoBottomSheet();
                    bottomSheet.whosinTicketTourOptionModel = model;
                    bottomSheet.activity = activity;
                    bottomSheet.isFromRaynaWhosinCustomTicket = true;
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

            private void updateAdultChildInfantValue(WhosinTicketTourOptionModel model){
//                Utils.setStyledText(activity,binding.adultsPrice, Utils.roundFloatValue(model.updateAdultPrices()));
//                Utils.setStyledText(activity,binding.childPrice, Utils.roundFloatValue(model.updateChildPrices()));
//                Utils.setStyledText(activity,binding.infantsPrice, Utils.roundFloatValue(model.updateInfantPrices()));

                if (model.updateAdultPrices() == 0) {
                    binding.adultsPrice.setVisibility(GONE);
                } else {
                    binding.adultsPrice.setVisibility(View.VISIBLE);
                    Utils.setStyledText(activity, binding.adultsPrice, Utils.roundFloatValue(model.updateAdultPrices()));
                }

                if (model.updateChildPrices() == 0) {
                    binding.childPrice.setVisibility(GONE);
                } else {
                    binding.childPrice.setVisibility(View.VISIBLE);
                    Utils.setStyledText(activity, binding.childPrice, Utils.roundFloatValue(model.updateChildPrices()));
                }

                if (model.updateInfantPrices() == 0) {
                    binding.infantsPrice.setVisibility(GONE);
                } else {
                    binding.infantsPrice.setVisibility(View.VISIBLE);
                    Utils.setStyledText(activity, binding.infantsPrice, Utils.roundFloatValue(model.updateInfantPrices()));
                }

                String unit = model.getUnit();
                if (!TextUtils.isEmpty(unit)) {
                    binding.tvTotalAdult.setText(model.getTmpAdultValue() + " " + unit);
                    binding.tvTotalChild.setText(model.getTmpChildValue() + " " + unit);
                    binding.tvTotalInfants.setText(model.getTmpInfantValue() + " " + unit);
                }
            }

            private void updatePaxBg(WhosinTicketTourOptionModel model){
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

            private void refreshModel(WhosinTicketTourOptionModel model){
                model.setTmpAdultValue(0);
                model.setTmpChildValue(0);
                model.setTmpInfantValue(0);
                model.setRaynaTimeSlotModel(null);
                model.setTourOptionSelectDate("");
                model.setFirestTimeUpdate(true);

            }

        }

    }

    private class BigBusTourOptionListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_rayna_ticket_option_view));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            BigBusOptionsItemModel model = (BigBusOptionsItemModel) getItem(position);
            boolean isLastItem = position == getItemCount() - 1;
            if (model == null) return;

            Utils.updateNoteText(model.getMinNumOfPeople(),model.getMaxNumOfPeople(),viewHolder.binding.tvNote,model.getNotes());

            viewHolder.setUpAdultChildData(model);

            viewHolder.setOnClickListeners(model);

            viewHolder.binding.tvOptionName.setText(model.getTitle());

            viewHolder.binding.selectSpinnerLayout.setVisibility(GONE);

            viewHolder.binding.selectTourDateLayout.setText(model.getTourOptionSelectDate());

            String slotText = model.getSlotText();
            boolean hasSlot = !TextUtils.isEmpty(slotText);

            viewHolder.binding.viewLine1.setVisibility(hasSlot ? View.VISIBLE : View.GONE);
            viewHolder.binding.timeSlotLayout.setVisibility(hasSlot ? View.VISIBLE : View.GONE);

            if (hasSlot) {
                viewHolder.binding.tourTimeSlotTv.setText(slotText);
            }

//            if (!model.isDescriptionProcessed()){
//                if (!TextUtils.isEmpty(model.getShortDescription())) {
//                    viewHolder.binding.tvOptionDescription.setVisibility(View.VISIBLE);
//                    Utils.addSeeMore(viewHolder.binding.tvOptionDescription, Html.fromHtml(model.getShortDescription()), 1, "... " + getValue("see_more"), v -> {
//                        ReadMoreBottomSheet bottomSheet = new ReadMoreBottomSheet();
//                        bottomSheet.title = getValue("description");
//                        bottomSheet.formattedDescription = model.getShortDescription();
//                        bottomSheet.show(getSupportFragmentManager(),"");
//                    });
//                } else {
//                    viewHolder.binding.tvOptionDescription.setVisibility(View.GONE);
//                }
//                model.setDescriptionProcessed(true);
//            }


            if (model.isPickupAvailable() && model.isPickupRequired()) {
                viewHolder.binding.selectPickupLayout.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(model.getPickUpPoint())) {
                    viewHolder.binding.tvSelectPickUp.setText(model.getPickUpPoint());
                } else {
                    viewHolder.binding.tvSelectPickUp.setText("");
                }
            } else {
                viewHolder.binding.selectPickupLayout.setVisibility(GONE);
            }

            viewHolder.loadOptionImage(model);

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
            viewHolder.binding.expandedArrow.setVisibility(View.GONE);
            viewHolder.updatePaxBg(model);

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.10f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemRaynaTicketOptionViewBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemRaynaTicketOptionViewBinding.bind(itemView);
                updateLangValue(binding);
            }

            private void loadOptionImage(BigBusOptionsItemModel model) {
                if (model == null || binding == null) return;

                RaynaTicketDetailModel raynaModel = RaynaTicketManager.shared.raynaTicketDetailModel;
                String tmpImage = null;

                if (!TextUtils.isEmpty(model.getCoverImageUrl())){
                    tmpImage = model.getCoverImageUrl();
                }

                if (!TextUtils.isEmpty(tmpImage)) {
                    Graphics.loadImage(tmpImage, binding.ticketOptionImage);
                } else if (raynaModel != null && raynaModel.getImages() != null && !raynaModel.getImages().isEmpty()) {
                    for (String image : raynaModel.getImages()) {
                        if (!TextUtils.isEmpty(image) && !Utils.isVideo(image)) {
                            tmpImage = image;
                            Graphics.loadImage(image, binding.ticketOptionImage);
                            break;
                        }
                    }
                }

                String finalTmpImage = tmpImage;
                binding.ticketOptionImage.setOnClickListener(v -> {
                    if (!TextUtils.isEmpty(finalTmpImage)) {
                        Intent intent = new Intent(activity, ProfileFullScreenImageActivity.class);
                        intent.putExtra(ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, finalTmpImage);
                        activity.startActivity(intent);
                    }
                });
            }

            private void setUpAdultChildData(BigBusOptionsItemModel model) {

                activity.runOnUiThread(() -> {
                    boolean showAdult   = model.isAdultAllowed();
                    boolean showChild   = model.isChildrenAllowed();
                    boolean showInfant  = model.isInfantsAllowed();

                    binding.addQuantityAdult.setVisibility(showAdult ? View.VISIBLE : View.GONE);
                    binding.addQuantityChild.setVisibility(showChild ? View.VISIBLE : View.GONE);
                    binding.addQuantityInfants.setVisibility(showInfant ? View.VISIBLE : View.GONE);

                    // Adult
                    if (showAdult) {
                        String adultVal = String.valueOf(model.getTmpAdultValue());
                        if (!adultVal.equals(binding.tvTotalAdult.getText().toString())) {
                            binding.tvTotalAdult.setText(adultVal);
                        }
                        binding.adultAge.setText(model.getAdultAge());
                        shouldHideDiscount(binding.adultPriceWithoutDiscount, binding.tvAdultPrice,
                                model.getWithoutDiscountAdultPrice(), model.getAdultPrice());
                    }

                    // Child
                    if (showChild) {
                        String childVal = String.valueOf(model.getTmpChildValue());
                        if (!childVal.equals(binding.tvTotalChild.getText().toString())) {
                            binding.tvTotalChild.setText(childVal);
                        }
                        binding.childrenAge.setText(model.getChildAge());
                        shouldHideDiscount(binding.childPriceWithoutDiscount, binding.tvChildPrice,
                                model.getWithoutDiscountChildPrice(), model.getChildPrice());
                    }

                    // Infant
                    if (showInfant) {
                        String infantVal = String.valueOf(model.getTmpInfantValue());
                        if (!infantVal.equals(binding.tvTotalInfants.getText().toString())) {
                            binding.tvTotalInfants.setText(infantVal);
                        }
                        binding.infantAge.setText(model.getInfantAge());
                        shouldHideDiscount(binding.infantPriceWithoutDiscount, binding.tvInfantPrice,
                                model.getWithoutDiscountInfantPrice(), model.getInfantPrice());
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


                    updateAdultChildInfantValue(model);
                });


            }

            private void updateCount(TextView textView, boolean isIncrement, BigBusOptionsItemModel model) {
                String unit = model.getUnit();
                if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                if (isIncrement){
                    int total = model.getTmpAdultValue() + model.getTmpChildValue() + model.getTmpInfantValue();
                    if (total + 1 > model.getMaxNumOfPeople()) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("max_pax_alert",String.valueOf(model.getMaxNumOfPeople()),unit));
                        return;
                    }
                }

                int currentValue = Utils.getNumericValue(textView);

                if (isIncrement) {
                    if (model.isFirestTimeUpdate() && model.getMinNumOfPeople() != 0) {
                        currentValue = model.getMinNumOfPeople();
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
                    RaynaTicketManager.shared.selectedTourModelForBigBus.add(model);
                    notifyItemChanged(getAdapterPosition());
                }

                if (!model.hasAtLeastOneMember()){
                    refreshModel(model);
                    positionsOfAdapter.remove(String.valueOf(getAdapterPosition()));
                    RaynaTicketManager.shared.selectedTourModelForBigBus.remove(model);
                    model.setTourOptionSelectDate("");
                    binding.dateTimeLayout.setBackground(ContextCompat.getDrawable(activity,R.drawable.tour_option_spinner_stock_bg));
                    notifyItemChanged(getAdapterPosition());
                }

                updateAdultChildInfantValue(model);
                updateButtonValue();
                hapticFeedback();
                updatePaxBg(model);
            }

            private void updateCountForInfant(TextView textView, boolean isIncrement,BigBusOptionsItemModel tourOptionsModel) {
                if (tourOptionsModel == null) return;
                String unit = tourOptionsModel.getUnit();
                if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                if (isIncrement){
                    int total = tourOptionsModel.getTmpAdultValue() + tourOptionsModel.getTmpChildValue() + tourOptionsModel.getTmpInfantValue();
                    if (total + 1 > tourOptionsModel.getMaxNumOfPeople()) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("max_pax_alert",String.valueOf(tourOptionsModel.getMaxNumOfPeople()),unit));
                        return;
                    }
                }

                int currentValue = Utils.getNumericValue(textView);
                int adultCount = Utils.getNumericValue(binding.tvTotalAdult);
                if (adultCount == 0) return;
                int maxInfantsAllowed = (int) Math.ceil(adultCount / 4.0);

                if (isIncrement && currentValue < maxInfantsAllowed) {
                    currentValue++;
                } else if (!isIncrement && currentValue > 0) {
                    currentValue--;
                }

                textView.setText(String.valueOf(currentValue));

                tourOptionsModel.setTmpInfantValue(currentValue);

//                TravelDeskOptionDataModel model = (TravelDeskOptionDataModel) getItem(getAdapterPosition());
//                if (model != null) {
//                    model.setTmpInfantValue(currentValue);
//                }

                boolean isPresent = positionsOfAdapter.stream().anyMatch(item -> item.equalsIgnoreCase(String.valueOf(getAdapterPosition())));
                if (!isPresent) {
                    positionsOfAdapter.add(String.valueOf(getAdapterPosition()));
                    RaynaTicketManager.shared.selectedTourModelForBigBus.add(tourOptionsModel);
                    notifyItemChanged(getAdapterPosition());
                }

                if (!tourOptionsModel.hasAtLeastOneMember()){
                    refreshModel(tourOptionsModel);
                    positionsOfAdapter.remove(String.valueOf(getAdapterPosition()));
                    RaynaTicketManager.shared.selectedTourModelForBigBus.remove(tourOptionsModel);
                    notifyItemChanged(getAdapterPosition());
                }

                updateButtonValue();
                hapticFeedback();
                updatePaxBg(tourOptionsModel);
            }

            private void setOnClickListeners(BigBusOptionsItemModel model){
                // Adults
                binding.ivMinusAdult.setOnClickListener(view -> updateCount(binding.tvTotalAdult, false,model));
                binding.ivPlusAdult.setOnClickListener(view -> updateCount(binding.tvTotalAdult, true,model));

                // Children
                binding.ivMinusChild.setOnClickListener(view -> updateCount(binding.tvTotalChild, false,model));
                binding.ivPlusChild.setOnClickListener(view -> updateCount(binding.tvTotalChild, true,model));

                // Infants
                binding.ivMinusInfants.setOnClickListener(view -> updateCountForInfant(binding.tvTotalInfants, false,model));
                binding.ivPlusInfants.setOnClickListener(view -> updateCountForInfant(binding.tvTotalInfants, true,model));

                //Select Date
                binding.dateTimeLayout.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    if (!model.hasAtLeastOneMember()) {
                        String message = setValue("min_pax_required_alert", String.valueOf(model.getUnit()));
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), message);
                        return;
                    }
                    if (model.isPickupAvailable() && model.isPickupRequired()){
                        if (model.getPickupPointsModel() == null){
                            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("pickup_alert",model.getTitle()));
                            return;
                        }
                    }

                    BigBusDateTimePickerSheet selectDateTimeDialog = new BigBusDateTimePickerSheet(activity);
                    selectDateTimeDialog.tourOptionsModel = model;
                    selectDateTimeDialog.callback = data -> {
                        if (data != null) {

                            binding.dateTimeLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.selected_tour_option_people_stock_bg));


                            model.setTourOptionSelectDate(data.getTourOptionSelectDate());
                            binding.selectTourDateLayout.setText(data.getTourOptionSelectDate());


                            if (data.getTimeModel() != null) {
                                binding.viewLine1.setVisibility(View.VISIBLE);
                                binding.timeSlotLayout.setVisibility(View.VISIBLE);
                                model.setTimeModel(data.getTimeModel());
                                binding.tourTimeSlotTv.setText(model.getSlotText());

                                for (BigBusPricingFromItemModel unit : data.getTimeModel().getUnitPricing()) {
                                    int currencyPrecision = (int) Math.pow(10, unit.getCurrencyPrecision());
                                    int newNetValue = (int) Math.ceil((double) unit.getNet() / currencyPrecision);
                                    int newNetBeforeDiscount = (int) Math.ceil((double) unit.getNetBeforeDiscount() / currencyPrecision);
                                    for (BigBusUnitsItemModel bigBusOptionsItemModel : model.getUnits()) {
                                        if (bigBusOptionsItemModel.getId().equals(unit.getUnitId())) {
                                            List<BigBusPricingFromItemModel> pricingForm = bigBusOptionsItemModel.getPricingFrom();
                                            if (!pricingForm.isEmpty()) {
                                                pricingForm.get(0).setNet(newNetValue);
                                                pricingForm.get(0).setWithoutDiscountNet(newNetBeforeDiscount);
                                            }
                                            bigBusOptionsItemModel.setPricingFrom(pricingForm);
                                        }
                                    }
                                }

                                String targetId = model.getId();
                                for (int i = 0; i < RaynaTicketManager.shared.selectedTourModelForBigBus.size(); i++) {
                                    if (RaynaTicketManager.shared.selectedTourModelForBigBus.get(i).getId().equals(targetId)) {
                                        RaynaTicketManager.shared.selectedTourModelForBigBus.set(i, model);
                                        notifyItemChanged(getAdapterPosition());
                                        updateButtonValue();
                                        break;
                                    }
                                }
                            } else {
                                binding.viewLine1.setVisibility(View.GONE);
                                binding.timeSlotLayout.setVisibility(View.VISIBLE);
                            }

                            boolean isPresent = positionsOfAdapter.stream().anyMatch(item -> item.equalsIgnoreCase(String.valueOf(getAdapterPosition())));
                            if (!isPresent && model.hasAtLeastOneMember()) {
                                positionsOfAdapter.add(String.valueOf(getAdapterPosition()));
                                notifyItemChanged(getAdapterPosition());
                                updateButtonValue();
                            }


                        }
                    };
                    selectDateTimeDialog.show(getSupportFragmentManager(), "1");
                });

                //Pick-up List
                binding.selectPickupLayout.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    String id = "";
                    if (model.getPickupPointsModel() != null) {
                        id = model.getPickupPointsModel().getId();
                    }
                    BigBusPickUpListSheet pickUpListSheet = new BigBusPickUpListSheet(activity,id);
                    pickUpListSheet.pickUpList = model.getPickupPoints();
                    pickUpListSheet.callback = data -> {
                        if (data != null){
                            model.setPickupPointsModel(data);
                            binding.tvSelectPickUp.setText(data.getName());
                        }
                    };
                    pickUpListSheet.show(getSupportFragmentManager(),"");
                });

                // More Info
                binding.btnMoreInfo.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    RaynaMoreInfoBottomSheet bottomSheet = new RaynaMoreInfoBottomSheet();
                    bottomSheet.bigBusOptionsItemModel = model;
                    bottomSheet.activity = activity;
                    bottomSheet.isFromBigBusTicket = true;
                    bottomSheet.isNonRefundable = model.isNonRefundable();
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

                if (finalAmount == 0){
                    mainTextView.setVisibility(View.GONE);
                }
            }

            private void updateAdultChildInfantValue(BigBusOptionsItemModel model){
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

            private void updatePaxBg(BigBusOptionsItemModel model){
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

            private void refreshModel(BigBusOptionsItemModel model){
                model.setTmpAdultValue(0);
                model.setTmpChildValue(0);
                model.setTmpInfantValue(0);
                model.setTimeModel(null);
                model.setPickupPointsModel(null);
                model.setTourOptionSelectDate("");

            }

        }

    }


    // endregion
    // --------------------------------------


}
