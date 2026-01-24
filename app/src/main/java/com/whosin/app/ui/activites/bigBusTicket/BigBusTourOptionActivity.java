package com.whosin.app.ui.activites.bigBusTicket;

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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityRaynaTicketTourOptionBinding;
import com.whosin.app.databinding.ItemRaynaTicketOptionViewBinding;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.LogManager;
import com.whosin.app.service.models.BigBusModels.BigBusOptionsItemModel;
import com.whosin.app.service.models.BigBusModels.BigBusPricingFromItemModel;
import com.whosin.app.service.models.BigBusModels.BigBusUnitsItemModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.models.whosinTicketModel.WhosinTicketTourOptionModel;
import com.whosin.app.ui.activites.Profile.ProfileFullScreenImageActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.RaynaMoreInfoBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.ReadMoreBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.RaynaParticipantDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BigBusTourOptionActivity extends BaseActivity {

    private ActivityRaynaTicketTourOptionBinding binding;

    private final TicketTourOptionListAdapter<BigBusOptionsItemModel> ticketTourOptionListAdapter = new TicketTourOptionListAdapter<>();

    private List<String> positionsOfAdapter = new ArrayList<>();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        RaynaTicketManager.shared.selectedTourModelForBigBus.clear();

        RaynaTicketManager.shared.activityList.add(activity);

        binding.constraintHeader.tvTitle.setText(getValue("tour_options"));
        binding.tvNext.setText(getValue("next"));

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setSupportsChangeAnimations(false);
        binding.tourOptionRecyclerView.setItemAnimator(animator);
        binding.tourOptionRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.tourOptionRecyclerView.setAdapter(ticketTourOptionListAdapter);

        if (RaynaTicketManager.shared.bigBusTicketTourOption != null && !RaynaTicketManager.shared.bigBusTicketTourOption.isEmpty()){
            List<BigBusOptionsItemModel> tourOptionsModels = new ArrayList<>();
            tourOptionsModels.addAll(RaynaTicketManager.shared.bigBusTicketTourOption);
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

            startActivity(new Intent(activity, RaynaParticipantDetailActivity.class));

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
        if (RaynaTicketManager.shared.bigBusTicketTourOption != null && !RaynaTicketManager.shared.bigBusTicketTourOption.isEmpty()){
            RaynaTicketManager.shared.bigBusTicketTourOption.clear();
        }
        RaynaTicketManager.shared.activityList.remove(activity);
        RaynaTicketManager.shared.selectedTourModelForBigBus = new ArrayList<>();
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
        if (RaynaTicketManager.shared.selectedTourModelForBigBus.isEmpty()) {
            binding.tvNext.setText(getValue("next"));
            binding.tvPrice.setVisibility(View.GONE);
        } else {
            binding.tvPrice.setVisibility(View.VISIBLE);
            float adultAmount = 0f;
            float childAmount = 0f;
            float infantAmount = 0f;

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
            BigBusOptionsItemModel model = (BigBusOptionsItemModel) getItem(position);
            boolean isLastItem = position == getItemCount() - 1;
            if (model == null) return;

            if (!isInitialStateApplied) {
                for (int i = 0; i < getItemCount(); i++) {
                    BigBusOptionsItemModel m = (BigBusOptionsItemModel) getItem(i);
                    if (m != null) {
                        m.setExpanded(i == 0);
                    }
                }
                isInitialStateApplied = true;
            }

            viewHolder.binding.selectTourDateLayout.setHint(getValue("date_time_placeHolder"));
            viewHolder.binding.tourTimeSlotTv.setHint(getValue("time_slot"));
            viewHolder.binding.btnMoreInfoView.setText(getValue("Inclusions & Details"));
            viewHolder.binding.tvSelectPickUp.setHint(getValue("select_pickup_location"));

            viewHolder.binding.horizontalContainer.setOnClickListener(v -> {
                model.setExpanded(!model.isExpanded());
                notifyItemChanged(position);
            });

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

//                activity.runOnUiThread(() -> {
//
//                    binding.addQuantityChild.setVisibility(model.isChildrenAllowed() ? View.VISIBLE : View.GONE);
//                    binding.addQuantityInfants.setVisibility(model.isInfantsAllowed() ? View.VISIBLE : View.GONE);
//                    binding.addQuantityAdult.setVisibility(model.isAdultAllowed() ? View.VISIBLE : View.GONE);
//
//                    binding.tvTotalAdult.setText(String.valueOf(model.getTmpAdultValue()));
//                    binding.tvTotalChild.setText(String.valueOf(model.getTmpChildValue()));
//                    binding.tvTotalInfants.setText(String.valueOf(model.getTmpInfantValue()));
//
//                    updateAdultChildInfantValue(model);
//
//                    binding.adultAge.setText(model.getAdultAge());
//                    binding.childrenAge.setText(model.getChildAge());
//                    binding.infantAge.setText(model.getInfantAge());
//
//
//                    shouldHideDiscount(binding.adultPriceWithoutDiscount, binding.tvAdultPrice,model.getWithoutDiscountAdultPrice(), model.getAdultPrice());
//                    shouldHideDiscount(binding.childPriceWithoutDiscount, binding.tvChildPrice,model.getWithoutDiscountChildPrice(), model.getChildPrice());
//                    shouldHideDiscount(binding.infantPriceWithoutDiscount,binding.tvInfantPrice, model.getWithoutDiscountInfantPrice(), model.getInfantPrice());
//
//                });

                activity.runOnUiThread(() -> {

                    binding.tvAdultsTitle.setText(getValue("adults_title"));
                    binding.tvChildTitle.setText(getValue("children_title"));
                    binding.tvInfantsTitle.setText(getValue("infant_title"));

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

//                int currentValue = Integer.parseInt(textView.getText().toString());
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
                model.setFirestTimeUpdate(true);
            }

        }

        private void applyExpandState(ViewHolder holder, BigBusOptionsItemModel model) {

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
