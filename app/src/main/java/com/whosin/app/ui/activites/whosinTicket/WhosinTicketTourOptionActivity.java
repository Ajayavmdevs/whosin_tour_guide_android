package com.whosin.app.ui.activites.whosinTicket;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityRaynaTicketTourOptionBinding;
import com.whosin.app.databinding.ItemRaynaTicketOptionViewBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.myCartModels.MyCartItemsModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.models.rayna.RaynaTimeSlotModel;
import com.whosin.app.service.models.rayna.TourOptionsModel;
import com.whosin.app.service.models.whosinTicketModel.RaynaWhosinBookingRulesModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.ProfileFullScreenImageActivity;
import com.whosin.app.ui.activites.auth.AuthenticationActivity;
import com.whosin.app.ui.activites.cartManagement.TicketCartActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.CancellationPolicyBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.RaynaMoreInfoBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.ReadMoreBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.SelectDateAndTimeSheet;
import com.whosin.app.ui.activites.raynaTicket.RaynaParticipantDetailActivity;
import com.whosin.app.ui.adapter.AddOnAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class WhosinTicketTourOptionActivity extends BaseActivity {

    private ActivityRaynaTicketTourOptionBinding binding;

    private final TicketTourOptionListAdapter<TourOptionsModel> ticketTourOptionListAdapter = new TicketTourOptionListAdapter<>();

    private List<String> positionsOfAdapter = new ArrayList<>();

    private AtomicInteger completedCount = new AtomicInteger(0);

    private AtomicBoolean hasAnyFailed = new AtomicBoolean(false);

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        RaynaTicketManager.shared.selectedTourModel.clear();

        RaynaTicketManager.shared.activityList.add(activity);

        binding.constraintHeader.tvTitle.setText(getValue("tour_options"));
        binding.tvNext.setText(getValue("next"));

        ((SimpleItemAnimator) Objects.requireNonNull(binding.tourOptionRecyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
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
//        RaynaTicketManager.shared.activityList.remove(activity);
//        RaynaTicketManager.shared.selectedTourModel = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RaynaTicketManager.shared.raynaWhosinModels != null && !RaynaTicketManager.shared.raynaWhosinModels.isEmpty()){
            RaynaTicketManager.shared.raynaWhosinModels.clear();
        }
        RaynaTicketManager.shared.activityList.remove(activity);
        RaynaTicketManager.shared.object = new JsonObject();
        RaynaTicketManager.shared.selectedTourModel = new ArrayList<>();
        RaynaTicketManager.shared.selectedAddonModels = new ArrayList<>();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void handleTicketOption(List<TourOptionsModel> data) {
        List<TourOptionsModel> tourOptionsModels = new ArrayList<>(data);
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
        ticketTourOptionListAdapter.updateData(tourOptionsModels);

    }


    private void updateButtonColor() {
        int color = ContextCompat.getColor(activity, !positionsOfAdapter.isEmpty()  ? R.color.brand_pink : R.color.gray);
        binding.nextButton.setBackgroundColor(color);
    }

    private void syncSelectedAddonsWithSelectedTours() {
        if (RaynaTicketManager.shared.selectedTourModel.isEmpty()) return;

        List<TourOptionsModel> selectedAddons = RaynaTicketManager.shared.selectedAddonModels;

        for (TourOptionsModel tour : RaynaTicketManager.shared.selectedTourModel) {
            if (tour.getAddons() != null && !tour.getAddons().isEmpty()) {
                for (TourOptionsModel tourAddon : tour.getAddons()) {
                    boolean isSelected = false;
                    for (TourOptionsModel selectedAddon : selectedAddons) {
                        if (selectedAddon.get_id().equals(tourAddon.get_id())) {
                            tourAddon.setTmpAdultValue(selectedAddon.getTmpAdultValue());
                            tourAddon.setTmpChildValue(selectedAddon.getTmpChildValue());
                            tourAddon.setTmpInfantValue(selectedAddon.getTmpInfantValue());
                            tourAddon.setRaynaTimeSlotModel(selectedAddon.getRaynaTimeSlotModel());
                            isSelected = true;
                            break;
                        }
                    }
                    if (!isSelected) {
                        tourAddon.setTmpAdultValue(0);
                        tourAddon.setTmpChildValue(0);
                        tourAddon.setTmpInfantValue(0);
                        tourAddon.setRaynaTimeSlotModel(null);
                    }
                }
            }
        }
    }

    private void updateButtonValue() {
        if (RaynaTicketManager.shared.selectedTourModel.isEmpty()) {
            binding.tvNext.setText(getValue("next"));
            binding.tvPrice.setVisibility(View.GONE);
        } else {
            syncSelectedAddonsWithSelectedTours();
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

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestRaynaTourOptions(String selectedDate) {
        JsonObject object = new JsonObject();
        object.addProperty("ticketId", RaynaTicketManager.shared.raynaTicketDetailModel.getId());
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
                    model.data.sort(Comparator.comparing(
                            TourOptionsModel::getOrder,
                            Comparator.nullsLast(Comparator.reverseOrder())
                    ));
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

    private void requestRaynaTourPolicy(TourOptionsModel tourOptionsModel,final int currentIndex) {
        showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ticketId", tourOptionsModel.getCustomTicketId());
        jsonObject.addProperty("optionId", tourOptionsModel.get_id());
        jsonObject.addProperty("date", tourOptionsModel.getTourOptionSelectDate());
        jsonObject.addProperty("time", tourOptionsModel.getAvailabilityTime());
        jsonObject.addProperty("adults", tourOptionsModel.getTmpAdultValue());
        jsonObject.addProperty("childs", tourOptionsModel.getTmpChildValue());
        jsonObject.addProperty("infants", tourOptionsModel.getTmpInfantValue());
        Log.d("requestRaynaTourPolicy", "requestRaynaTourPolicy: " + jsonObject);
        DataService.shared(activity).requestWhosinTicketTourPolicy(jsonObject, new RestCallback<ContainerListModel<RaynaWhosinBookingRulesModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaWhosinBookingRulesModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    String tmpError = "";
                    if (error.contains("You cannot book this tour on selected date due to cutoff time.")){
                        tmpError = error + " for " + tourOptionsModel.getTitle();
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
                        object.addProperty("ticketId", model1.getTicketId());
                        object.addProperty("optionId", model1.getOptionId());
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

            viewHolder.binding.selectTourDateLayout.setHint(getValue("date_time_placeHolder"));
            viewHolder.binding.tourTimeSlotTv.setHint(getValue("time_slot"));
            viewHolder.binding.btnMoreInfoView.setText(getValue("more_info"));

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
            viewHolder.binding.tvOptionDescription.setText(model.getDescription());

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


            if (!TextUtils.isEmpty(model.getDescription())) {
                viewHolder.binding.tvOptionDescription.setVisibility(View.VISIBLE);
                Utils.addSeeMore(viewHolder.binding.tvOptionDescription, Html.fromHtml(model.getDescription()), 1, "... " + getValue("see_more"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReadMoreBottomSheet bottomSheet = new ReadMoreBottomSheet();
                        bottomSheet.title = getValue("description");
                        bottomSheet.formattedDescription = model.getDescription();
                        bottomSheet.show(getSupportFragmentManager(),"");
                    }
                });
            } else {
                viewHolder.binding.tvOptionDescription.setVisibility(View.GONE);
            }

            if (model.getAddons().isEmpty()) {
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


            RaynaTicketDetailModel raynaTicketDetailModel = RaynaTicketManager.shared.raynaTicketDetailModel;

            if (raynaTicketDetailModel.getDiscount() != 0){
                viewHolder.binding.discountTagLayout.setVisibility(View.VISIBLE);
                viewHolder.binding.tvDiscountTag.setText(raynaTicketDetailModel.getDiscount() + " %");
            }else {
                viewHolder.binding.discountTagLayout.setVisibility(View.GONE);
            }


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

//                int currentValue = Integer.parseInt(textView.getText().toString());
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
                assert model != null;
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

//                            if (model.getRaynaTimeSlotModel() == null) {
//                                binding.viewLine1.setVisibility(View.VISIBLE);
//                                binding.timeSlotLayout.setVisibility(View.VISIBLE);
//                                binding.tourTimeSlotTv.setText(model.getSlotText());
//                            } else {
//                                binding.viewLine1.setVisibility(View.VISIBLE);
//                                binding.timeSlotLayout.setVisibility(View.VISIBLE);
//                                binding.tourTimeSlotTv.setText(model.getRaynaTimeSlotModel().getTimeSlot());
//                            }

                            boolean isPresent = positionsOfAdapter.stream().anyMatch(item -> item.equalsIgnoreCase(String.valueOf(getAdapterPosition())));
                            if (!isPresent && model.hasAtLeastOneMember()) {
                                positionsOfAdapter.add(String.valueOf(getAdapterPosition()));
                                RaynaTicketManager.shared.selectedTourModel.add(model);
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
                    RaynaTicketDetailModel raynaTicketDetailModel = RaynaTicketManager.shared.raynaTicketDetailModel;
                    RaynaMoreInfoBottomSheet bottomSheet = new RaynaMoreInfoBottomSheet();
                    bottomSheet.tourOptionsModel = model;
                    bottomSheet.activity = activity;
                    bottomSheet.isFromRaynaWhosinTicket = true;
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
                        ? R.drawable.selected_tour_option_people_stock_bg
                        : R.drawable.tour_option_spinner_stock_bg;
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

    // endregion
    // --------------------------------------
}
