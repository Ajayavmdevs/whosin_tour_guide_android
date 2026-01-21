package com.whosin.app.ui.activites.travelDeskTicket;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.whosin.app.service.manager.LogManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskCancellationPolicyModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskHeroImageModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskOptionDataModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskPriceModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskTourAvailabilityModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.ProfileFullScreenImageActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.CancellationPolicyBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.RaynaMoreInfoBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.ReadMoreBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.RaynaParticipantDetailActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TravelDeskTourOptionActivity extends BaseActivity {

    private ActivityRaynaTicketTourOptionBinding binding;

    private final TicketTourOptionListAdapter<TravelDeskOptionDataModel> ticketTourOptionListAdapter = new TicketTourOptionListAdapter<>();

    private List<String> positionsOfAdapter = new ArrayList<>();

    private AtomicInteger completedCount = new AtomicInteger(0);

    private AtomicBoolean hasAnyFailed = new AtomicBoolean(false);

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        RaynaTicketManager.shared.selectTravelDeskOptionDataModels.clear();

        RaynaTicketManager.shared.activityList.add(activity);

        binding.constraintHeader.tvTitle.setText(getValue("tour_options"));
        binding.tvNext.setText(getValue("next"));

        ((SimpleItemAnimator) Objects.requireNonNull(binding.tourOptionRecyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        binding.tourOptionRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.tourOptionRecyclerView.setAdapter(ticketTourOptionListAdapter);

        if (RaynaTicketManager.shared.travelDeskOptionDataModels != null && !RaynaTicketManager.shared.travelDeskOptionDataModels.isEmpty()){
            List<TravelDeskOptionDataModel> tourOptionsModels = new ArrayList<>();
            tourOptionsModels.addAll(RaynaTicketManager.shared.travelDeskOptionDataModels);
            ticketTourOptionListAdapter.updateData(tourOptionsModels);
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

//            startActivity(new Intent(activity, RaynaParticipantDetailActivity.class));

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RaynaTicketManager.shared.travelDeskOptionDataModels != null && !RaynaTicketManager.shared.travelDeskOptionDataModels.isEmpty()){
            RaynaTicketManager.shared.travelDeskOptionDataModels.clear();
        }
        RaynaTicketManager.shared.activityList.remove(activity);
        RaynaTicketManager.shared.selectTravelDeskOptionDataModels = new ArrayList<>();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void processTourPolicies(final int index) {
        List<TravelDeskOptionDataModel> selectedTours = RaynaTicketManager.shared.selectTravelDeskOptionDataModels;
        if (index >= selectedTours.size() || hasAnyFailed.get()) {
            if (!hasAnyFailed.get() && completedCount.get() == selectedTours.size()) {
                startActivity(new Intent(activity, RaynaParticipantDetailActivity.class));
            }
            return;
        }

        // Process the current tour
        TravelDeskOptionDataModel tour = selectedTours.get(index);
        requestTravelDeskTourPolicy(tour, index);
    }

    private void updateButtonColor() {
        int color = ContextCompat.getColor(activity, !positionsOfAdapter.isEmpty()  ? R.color.brand_pink : R.color.gray);
        binding.nextButton.setBackgroundColor(color);
    }

    private void updateButtonValue() {
        if (RaynaTicketManager.shared.selectTravelDeskOptionDataModels.isEmpty()) {
            binding.tvNext.setText(getValue("next"));
            binding.tvPrice.setVisibility(View.GONE);
        } else {
            binding.tvPrice.setVisibility(View.VISIBLE);
            float adultAmount = 0f;
            float childAmount = 0f;
            float infantAmount = 0f;
            float pricePerTrip = 0f;

            for (TravelDeskOptionDataModel q : RaynaTicketManager.shared.selectTravelDeskOptionDataModels) {
                adultAmount += q.updateAdultPrices();
                childAmount += q.updateChildPrices();
                infantAmount += q.updateInfantPrices();
                pricePerTrip += q.getPricePerTrip();
            }

            float total = adultAmount + childAmount + infantAmount + pricePerTrip;
            Utils.setStyledText(activity,binding.tvPrice,Utils.roundFloatValue(total));
            if (total == 0.0){
                binding.tvPrice.setVisibility(View.GONE);
            }else {
                binding.tvPrice.setVisibility(View.VISIBLE);
            }
        }

        updateButtonColor();
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

    private void requestTravelDeskTourPolicy(TravelDeskOptionDataModel travelDeskOptionDataModel,final int currentIndex) {
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
                    hasAnyFailed.set(true);
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
            TravelDeskOptionDataModel model = (TravelDeskOptionDataModel) getItem(position);
            boolean isLastItem = position == getItemCount() - 1;
            if (model == null) return;

            viewHolder.binding.selectTourDateLayout.setHint(getValue("date_time_placeHolder"));
            viewHolder.binding.tourTimeSlotTv.setHint(getValue("time_slot"));
            viewHolder.binding.btnMoreInfoView.setText(getValue("more_info"));

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


            if (!model.isDescriptionProcessed()){
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
                model.setDescriptionProcessed(true);
            }




            viewHolder.loadOptionImage(model);

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

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemRaynaTicketOptionViewBinding.bind(itemView);
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

                    binding.tvAdultsTitle.setText(getValue("adults_title"));
                    binding.tvChildTitle.setText(getValue("children_title"));
                    binding.tvInfantsTitle.setText(getValue("infant_title"));


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
                updateButtonValue();
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

                updateButtonValue();
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
                                        updateButtonValue();
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
                                updateButtonValue();
                            }

                        }
                    };
                    selectDateTimeDialog.show(getSupportFragmentManager(), "1");
                });

                // More Info
                binding.btnMoreInfo.setOnClickListener(v -> {
                    RaynaTicketDetailModel raynaTicketDetailModel = RaynaTicketManager.shared.raynaTicketDetailModel;
                    Utils.preventDoubleClick(v);
                    RaynaMoreInfoBottomSheet bottomSheet = new RaynaMoreInfoBottomSheet();
                    bottomSheet.travelDeskOptionDataModel = model;
                    bottomSheet.activity = activity;
                    bottomSheet.isFromTravelDeskTicket = true;
                    bottomSheet.isNonRefundable = !raynaTicketDetailModel.getFreeCancellation();
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
                    int drawableRes = values[i] != 0 ? R.drawable.selected_tour_option_people_stock_bg : R.drawable.tour_option_spinner_stock_bg;
                    views[i].setBackground(ContextCompat.getDrawable(activity, drawableRes));
                }

                int drawableRes = !TextUtils.isEmpty(model.getTourOptionSelectDate()) ? R.drawable.selected_tour_option_people_stock_bg : R.drawable.tour_option_spinner_stock_bg;
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

    // endregion
    // --------------------------------------

}
