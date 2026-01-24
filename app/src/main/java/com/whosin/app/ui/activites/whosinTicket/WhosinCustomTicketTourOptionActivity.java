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
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityRaynaTicketTourOptionBinding;
import com.whosin.app.databinding.ItemRaynaTicketOptionViewBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.models.rayna.TourOptionsModel;
import com.whosin.app.service.models.whosinTicketModel.RaynaWhosinBookingRulesModel;
import com.whosin.app.service.models.whosinTicketModel.WhosinAvailabilityModel;
import com.whosin.app.service.models.whosinTicketModel.WhosinTicketTourOptionModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.ProfileFullScreenImageActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.CancellationPolicyBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.RaynaMoreInfoBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.ReadMoreBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.RaynaParticipantDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class WhosinCustomTicketTourOptionActivity extends BaseActivity {

    private ActivityRaynaTicketTourOptionBinding binding;

    private final TicketTourOptionListAdapter<WhosinTicketTourOptionModel> ticketTourOptionListAdapter = new TicketTourOptionListAdapter<>();

    private List<String> positionsOfAdapter = new ArrayList<>();

    private AtomicInteger completedCount = new AtomicInteger(0);

    private AtomicBoolean hasAnyFailed = new AtomicBoolean(false);

    private AtomicInteger completedCountForWhosinCheckTour = new AtomicInteger(0);

    private AtomicBoolean hasAnyFailedForWhosinCheckTour = new AtomicBoolean(false);

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        RaynaTicketManager.shared.selectedTourModelForWhosin.clear();

        RaynaTicketManager.shared.activityList.add(activity);

        binding.constraintHeader.tvTitle.setText(getValue("tour_options"));
        binding.tvNext.setText(getValue("next"));

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setSupportsChangeAnimations(false);
        binding.tourOptionRecyclerView.setItemAnimator(animator);
        binding.tourOptionRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.tourOptionRecyclerView.setAdapter(ticketTourOptionListAdapter);

        if (RaynaTicketManager.shared.whosinCustomTicketTourOption != null && !RaynaTicketManager.shared.whosinCustomTicketTourOption.isEmpty()){
            ticketTourOptionListAdapter.updateData(RaynaTicketManager.shared.whosinCustomTicketTourOption);
        }else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            binding.tourOptionRecyclerView.setVisibility(View.GONE);
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

            completedCountForWhosinCheckTour.set(0);
            hasAnyFailedForWhosinCheckTour.set(false);
            RaynaTicketManager.shared.cancellationObject = new JsonArray();
            processWhosinCheckAvailability(0);

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RaynaTicketManager.shared.raynaWhosinModels != null && !RaynaTicketManager.shared.raynaWhosinModels.isEmpty()){
            RaynaTicketManager.shared.raynaWhosinModels.clear();
        }
        RaynaTicketManager.shared.activityList.remove(activity);
        RaynaTicketManager.shared.selectedTourModelForWhosin = new ArrayList<>();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void updateButtonColor() {
        int color = ContextCompat.getColor(activity, !positionsOfAdapter.isEmpty()  ? R.color.brand_pink : R.color.gray);
        binding.nextButton.setBackgroundColor(color);
    }


    private void updateButtonValue() {
        if (RaynaTicketManager.shared.selectedTourModelForWhosin.isEmpty()) {
            binding.tvNext.setText(getValue("next"));
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

        updateButtonColor();
    }

    private void processTourPolicies(final int index) {
        List<WhosinTicketTourOptionModel> selectedTours = RaynaTicketManager.shared.selectedTourModelForWhosin;
        if (index >= selectedTours.size() || hasAnyFailed.get()) {
            if (!hasAnyFailed.get() && completedCount.get() == selectedTours.size()) {
                startActivity(new Intent(activity, RaynaParticipantDetailActivity.class));
            }
            return;
        }

        // Process the current tour
        WhosinTicketTourOptionModel tour = selectedTours.get(index);
        requestWhosinCustomTicketTourPolicy(tour, index);
    }

    private void processWhosinCheckAvailability(final int index) {
        List<WhosinTicketTourOptionModel> selectedTours = RaynaTicketManager.shared.selectedTourModelForWhosin;
        if (index >= selectedTours.size() || hasAnyFailedForWhosinCheckTour.get()) {
            if (!hasAnyFailedForWhosinCheckTour.get() && completedCountForWhosinCheckTour.get() == selectedTours.size()) {
                completedCount.set(0);
                hasAnyFailed.set(false);
                RaynaTicketManager.shared.cancellationObject = new JsonArray();
                processTourPolicies(0);
            }
            return;
        }

        // Process the current tour
        WhosinTicketTourOptionModel tour = selectedTours.get(index);
        requestWhosinAvailability(tour, index);
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

    private void requestWhosinCustomTicketTourPolicy(WhosinTicketTourOptionModel whosinTicketTourOptionModel,final int currentIndex) {
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
                    hasAnyFailed.set(true);
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

                completedCount.incrementAndGet();
                processTourPolicies(currentIndex + 1);

            }
        });
    }

    private void requestWhosinAvailability(WhosinTicketTourOptionModel whosinTicketTourOptionModel,final int currentIndex) {
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
            jsonObject.addProperty("slotId", whosinTicketTourOptionModel.getRaynaTimeSlotModel().getSlotId());
        }else {
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
                    hasAnyFailedForWhosinCheckTour.set(true);
                    return;
                }
                if (model.getData() != null && model.getData().isAvailable()){
                    completedCountForWhosinCheckTour.incrementAndGet();
                    processWhosinCheckAvailability(currentIndex + 1);
                }else {
                    hasAnyFailedForWhosinCheckTour.set(true);
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), model.message + " for " + whosinTicketTourOptionModel.getDisplayName());
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
            WhosinTicketTourOptionModel model = (WhosinTicketTourOptionModel) getItem(position);
            boolean isLastItem = position == getItemCount() - 1;
            if (model == null) return;

            if (!isInitialStateApplied) {
                for (int i = 0; i < getItemCount(); i++) {
                    WhosinTicketTourOptionModel m = (WhosinTicketTourOptionModel) getItem(i);
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

            private void updateCount(TextView textView, boolean isIncrement, LinearLayout linearLayout, WhosinTicketTourOptionModel model) {
                String unit = model.getUnit();
                if (TextUtils.isEmpty(unit)) unit = getValue("passenger");
                if (isIncrement){
                    int total = model.getTmpAdultValue() + model.getTmpChildValue() + model.getTmpInfantValue();
                    int maxTotalAllowed = model.getTmpMaxPax();
                    if (total + 1 > maxTotalAllowed) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), setValue("max_pax_alert",String.valueOf(maxTotalAllowed),unit));
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
                updateButtonValue();
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

                updateButtonValue();
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
                                updateButtonValue();
                            }
                        }
                    };
                    selectDateTimeDialog.show(getSupportFragmentManager(), "1");
                });

                // More Info
                binding.btnMoreInfo.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    RaynaTicketDetailModel raynaTicketDetailModel = RaynaTicketManager.shared.raynaTicketDetailModel;
                    RaynaMoreInfoBottomSheet bottomSheet = new RaynaMoreInfoBottomSheet();
                    bottomSheet.whosinTicketTourOptionModel = model;
                    bottomSheet.activity = activity;
                    bottomSheet.isFromRaynaWhosinCustomTicket = true;
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

        private void applyExpandState(ViewHolder holder, WhosinTicketTourOptionModel model) {

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

