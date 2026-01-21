package com.whosin.app.ui.activites.raynaTicket.BottomSheets;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentSelectAddonGuestTimeSheetBinding;
import com.whosin.app.databinding.RaynaTimeSlotNewItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.rayna.RaynaTimeSlotModel;
import com.whosin.app.service.models.rayna.TourOptionsModel;
import com.whosin.app.service.models.whosinTicketModel.WhosinTicketTourOptionModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.service.models.rayna.RaynaOprationDaysModel;
import android.text.Html;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

public class AddOnGuestTimeSheet extends DialogFragment {

    private FragmentSelectAddonGuestTimeSheetBinding binding;
    private final SlotAdapter<RaynaTimeSlotModel> slotAdapter = new SlotAdapter<>();
    public TourOptionsModel tourOptionsModel = null;
    public CommanCallback<TourOptionsModel> callback = null;
    private int selectedSlotPosition = -1;

    public TourOptionsModel whosinTicketTourOptionModel;

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
        binding = FragmentSelectAddonGuestTimeSheetBinding.bind(v);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (tourOptionsModel == null) return;

        binding.timeSlotRecycleview.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.timeSlotRecycleview.setAdapter(slotAdapter);

        requestAvailability();
    }

    private void requestAvailability() {
        Utils.hideViews(binding.hideShowLayout, binding.viewLine1, binding.tvAvailableTimeSlots, binding.timeSlotRecycleview, binding.btnDone, binding.tvAvailableDate, binding.layoutMoreInfo, binding.ivAddOnView);
        binding.progressView.setVisibility(View.VISIBLE);

        JsonObject jsonObject = new JsonObject();

// addonOptionIds as array
        JsonArray addonOptionIds = new JsonArray();
        addonOptionIds.add(tourOptionsModel.get_id());
        jsonObject.add("addonOptionIds", addonOptionIds);

        jsonObject.addProperty("optionId", whosinTicketTourOptionModel.get_id());

        String date = whosinTicketTourOptionModel.getTourOptionSelectDate();
        jsonObject.addProperty("date", date);

        int adults = whosinTicketTourOptionModel.getTmpAdultValue();
        int childs = whosinTicketTourOptionModel.getTmpChildValue();

        jsonObject.addProperty("adults", adults);
        jsonObject.addProperty("childs", childs);


        DataService.shared(requireContext()).requestWhosinAddOnAvailability(jsonObject, new RestCallback<>(this) {
            @Override
            public void result(ContainerListModel<TourOptionsModel> response, String error) {
                if (!isAdded()) return;
                binding.progressView.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(error)) {
                    Graphics.showAlertDialogWithOkButton(requireContext(), getString(R.string.app_name), error);
                    dismiss();
                    return;
                }

                Utils.showViews(binding.hideShowLayout, binding.viewLine1, binding.tvAvailableTimeSlots, binding.timeSlotRecycleview, binding.btnDone, binding.tvAvailableDate, binding.layoutMoreInfo, binding.ivAddOnView);

                if (response != null && response.data != null && !response.data.isEmpty()) {
                    TourOptionsModel responseModel = response.data.get(0);
                    updateModelData(responseModel);
                    setUpAdultChildData(tourOptionsModel);
                    setupSlots();
                } else {
                    Graphics.showAlertDialogWithOkButton(requireContext(), getString(R.string.app_name), "No availability found.");
                    dismiss();
                }
            }
        });
    }

    private void updateModelData(TourOptionsModel responseModel) {
        tourOptionsModel.setAvailabilityTimeSlot(responseModel.getAvailabilityTimeSlot());
        tourOptionsModel.setAvailabilityTime(responseModel.getAvailabilityTime());
        tourOptionsModel.setTotalSeats(responseModel.getTotalSeats());
        tourOptionsModel.setAdultPrice(responseModel.getAdultPrice());
        tourOptionsModel.setChildPrice(responseModel.getChildPrice());
        tourOptionsModel.setInfantPrice(responseModel.getInfantPrice());
        tourOptionsModel.setWithoutDiscountAdultPrice(responseModel.getWithoutDiscountAdultPrice());
        tourOptionsModel.setWithoutDiscountChildPrice(responseModel.getWithoutDiscountChildPrice());
        tourOptionsModel.setWithoutDiscountInfantPrice(responseModel.getWithoutDiscountInfantPrice());
        tourOptionsModel.setTotalSeats(responseModel.getTotalSeats());
        tourOptionsModel.setAvailabilityType(responseModel.getAvailabilityType());
        tourOptionsModel.setIsSlot(responseModel.getIsSlot());
        tourOptionsModel.setRatioPerPax(responseModel.getRatioPerPax());
        tourOptionsModel.setTourId(whosinTicketTourOptionModel.getTourId());

        tourOptionsModel.setInclusion(responseModel.getInclusion());
        tourOptionsModel.setExclusion(responseModel.getExclusion());
        tourOptionsModel.setTourExclusion(responseModel.getTourExclusion());
        tourOptionsModel.setOperationdays(responseModel.getOperationdays());
        tourOptionsModel.setOptionDescription(responseModel.getOptionDescription());
        if (!TextUtils.isEmpty(responseModel.getOptionName())) {
            tourOptionsModel.setOptionName(responseModel.getOptionName());
        }

        if (tourOptionsModel.getTmpAdultValue() == 0 && tourOptionsModel.getTmpChildValue() == 0 && tourOptionsModel.getTmpInfantValue() == 0) {
            tourOptionsModel.setTmpAdultValue(0);
            tourOptionsModel.setTmpChildValue(0);
            tourOptionsModel.setTmpInfantValue(0);
        }
    }

    private void setupSlots() {
        List<RaynaTimeSlotModel> tmpSlot = new ArrayList<>();
        selectedSlotPosition = -1;

        if (tourOptionsModel.getAvailabilityType().equals("regular")) {
            RaynaTimeSlotModel model = new RaynaTimeSlotModel();
            model.setAvailabilityTime(tourOptionsModel.getAvailabilityTime());
            model.setAvailable(tourOptionsModel.getTotalSeats());
            model.setTotalSeats(tourOptionsModel.getTotalSeats());
            tmpSlot.add(model);
            // Default selection removed: selectedSlotPosition = 0;
        } else if (tourOptionsModel.getAvailabilityType().equals("same_as_option")) {
            if (whosinTicketTourOptionModel.getAvailabilityType().equals("slot")) {
                tourOptionsModel.setAvailabilityTime(whosinTicketTourOptionModel.getAvailabilityTimeSlot().get(whosinTicketTourOptionModel.whosinTypeTicketSlotPosition).getAvailabilityTime());
            } else {
                tourOptionsModel.setAvailabilityTime(whosinTicketTourOptionModel.getAvailabilityTime());
            }

            Utils.hideViews(binding.timeSlotRecycleview, binding.tvAvailableTimeSlots);
            Utils.hideViews(binding.emptyPlaceHolderView);

            return;
        }else if (!tourOptionsModel.getAvailabilityTimeSlot().isEmpty()) {
            String selectedTime = null;
            if (tourOptionsModel.getRaynaTimeSlotModel() != null) {
                selectedTime = tourOptionsModel.getRaynaTimeSlotModel().getAvailabilityTime();
            } else if (!TextUtils.isEmpty(tourOptionsModel.getAvailabilityTime())) {
                selectedTime = tourOptionsModel.getAvailabilityTime();
            }

            for (RaynaTimeSlotModel raynaTimeSlotModel : tourOptionsModel.getAvailabilityTimeSlot()) {
                if (raynaTimeSlotModel.getTotalSeats() != 0) {
                    tmpSlot.add(raynaTimeSlotModel);
                    if (selectedTime != null && selectedTime.equals(raynaTimeSlotModel.getAvailabilityTime())) {
                        selectedSlotPosition = tmpSlot.size() - 1;
                    }
                }
            }
        }
        slotAdapter.updateData(tmpSlot);
        updateDoneButtonState();

        if (tmpSlot.isEmpty()) {
            Utils.hideViews(binding.timeSlotRecycleview, binding.tvAvailableTimeSlots, binding.viewLine1);
            Utils.showViews(binding.emptyPlaceHolderView);
        } else {
            Utils.showViews(binding.timeSlotRecycleview, binding.tvAvailableTimeSlots, binding.viewLine1);
            Utils.hideViews(binding.emptyPlaceHolderView);
        }
    }

    private void clearSelectedTimeSlot() {
        selectedSlotPosition = -1;
//        tourOptionsModel.setRaynaTimeSlotModel(null);
//        tourOptionsModel.setAvailabilityTime(null);

        // Refresh slot UI
        if (slotAdapter.getData() != null) {
            slotAdapter.notifyDataSetChanged();
        }
    }


    private int getMaxPax(TourOptionsModel model) {
        int tmpMax = model.getTmpMaxPax();
        if (tmpMax != 1000) {
            return tmpMax;
        }
        return model.getTotalSeats();
    }

    private int getMinPax(TourOptionsModel model) {
        return model.getTmpMinPax();
    }

    private void setListener() {
        binding.btnDone.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);

            int totalPax = tourOptionsModel.getTmpAdultValue() + tourOptionsModel.getTmpChildValue() + tourOptionsModel.getTmpInfantValue();
            if (totalPax == 0) {
                tourOptionsModel.setRaynaTimeSlotModel(null);
                tourOptionsModel.setAvailabilityTime(null);
                selectedSlotPosition = -1;
                if (callback != null) {
                    callback.onReceive(null);
                }
                dismiss();
                return;
            }

            if (!"same_as_option".equals(tourOptionsModel.getAvailabilityType())) {
                if (selectedSlotPosition == -1) {
                    Graphics.showAlertDialogWithOkButton(
                            requireContext(),
                            getString(R.string.app_name),
                            "Please select time slot"
                    );
                    return;
                }
            }

            if (callback != null) {
                if ((tourOptionsModel.getIsSlot() || (slotAdapter.getData() != null && !slotAdapter.getData().isEmpty())) && selectedSlotPosition != -1) {
                    if (selectedSlotPosition < slotAdapter.getData().size()) {
                        RaynaTimeSlotModel timeSlotModel = slotAdapter.getData().get(selectedSlotPosition);
                        if (timeSlotModel != null) tourOptionsModel.setRaynaTimeSlotModel(timeSlotModel);
                    }
                }
                callback.onReceive(tourOptionsModel);
            }
            dismiss();
        });

        binding.ivMinusAdult.setOnClickListener(view -> updateCount(binding.tvTotalAdult, false, tourOptionsModel));
        binding.ivPlusAdult.setOnClickListener(view -> updateCount(binding.tvTotalAdult, true, tourOptionsModel));

        binding.ivMinusChild.setOnClickListener(view -> updateCount(binding.tvTotalChild, false, tourOptionsModel));
        binding.ivPlusChild.setOnClickListener(view -> updateCount(binding.tvTotalChild, true, tourOptionsModel));

        binding.ivMinusInfants.setOnClickListener(view -> updateCountForInfant(binding.tvTotalInfants, false, tourOptionsModel));
        binding.ivPlusInfants.setOnClickListener(view -> updateCountForInfant(binding.tvTotalInfants, true, tourOptionsModel));
    }

    public int getLayoutRes() {
        return R.layout.fragment_select_addon_guest_time_sheet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
                if (layoutParam != null) {
                    layoutParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    bottomSheet.setLayoutParams(layoutParam);
                }
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }

    // --------------------------------------
    // region Logic
    // --------------------------------------

    private void setUpAdultChildData(TourOptionsModel model) {
        if (model == null) return;

        binding.tvAvailableDate.setText(Utils.getLangValue("Select Add-ons")); // Assuming key exists, or fallback

        binding.addOnTitle.setText(model.getTitle());
        binding.addOndDescription.setText(model.getSortDescription());

        String imageUrl = (model.getImages() != null && !model.getImages().isEmpty())
                ? model.getImages().get(0)
                : "";

        Graphics.loadImage(requireContext(), imageUrl, binding.ivAddOnImage);

        binding.addQuantityAdult.setVisibility(View.VISIBLE);
        binding.addQuantityChild.setVisibility(!model.getDisableChild() ? View.VISIBLE : View.GONE);
        binding.addQuantityInfants.setVisibility(!model.getDisableInfant() ? View.VISIBLE : View.GONE);

        binding.tvTotalAdult.setText(String.valueOf(model.getTmpAdultValue()));
        binding.tvTotalChild.setText(String.valueOf(model.getTmpChildValue()));
        binding.tvTotalInfants.setText(String.valueOf(model.getTmpInfantValue()));

        updateAdultChildInfantValue(model);

        if (model.getAdultAge() != null && !model.getAdultAge().isEmpty()) {
            binding.adultAge.setText(String.format("(%s)", model.getAdultAge()));
        } else {
            binding.adultAge.setText("");
        }
        if (model.getChildAge() != null && !model.getChildAge().isEmpty()) {
            binding.childrenAge.setText(String.format("(%s)", model.getChildAge()));
        } else {
            binding.childrenAge.setText("");
        }
        if (model.getInfantAge() != null && !model.getInfantAge().isEmpty()) {
            binding.infantAge.setText(String.format("(%s)", model.getInfantAge()));
        } else {
            binding.infantAge.setText("");
        }

        binding.tvAdultsTitle.setText(Utils.getLangValue("adults_title"));
        binding.tvChildTitle.setText(Utils.getLangValue("children_title"));
        binding.tvInfantsTitle.setText(Utils.getLangValue("infant_title"));

        if (!Utils.isNullOrEmpty(model.getAdultTitle()))
            binding.tvAdultsTitle.setText(model.getAdultTitle());
        if (!Utils.isNullOrEmpty(model.getChildTitle()))
            binding.tvChildTitle.setText(model.getChildTitle());
        if (!Utils.isNullOrEmpty(model.getInfantTitle()))
            binding.tvInfantsTitle.setText(model.getInfantTitle());

        Utils.setTextOrHide(binding.adultDescription, model.getAdultDescription());
        Utils.setTextOrHide(binding.childDescription, model.getChildDescription());
        Utils.setTextOrHide(binding.infantDescription, model.getInfantDescription());

        shouldHideDiscount(binding.adultPriceWithoutDiscount, binding.tvAdultPrice, model.getWithoutDiscountAdultPrice(), model.getAdultPrice());
        shouldHideDiscount(binding.childPriceWithoutDiscount, binding.tvChildPrice, model.getWithoutDiscountChildPrice(), model.getChildPrice());
        shouldHideDiscount(binding.infantPriceWithoutDiscount, binding.tvInfantPrice, model.getWithoutDiscountInfantPrice(), model.getInfantPrice());

        setupAddonInfo(model);
    }

    private boolean canIncreasePax(TourOptionsModel model) {
        Integer maxRatio = getMaxRatioPax(model);
        if (maxRatio == null) return true;
        int currentTotal = model.getTmpAdultValue() + model.getTmpChildValue() + model.getTmpInfantValue();
        if (currentTotal < maxRatio) {
            return true;
        } else {
            showRatioToastIfNeeded(maxRatio);
            return false;
        }
    }

    private Integer getMaxRatioPax(TourOptionsModel model) {
        TourOptionsModel mainOption = whosinTicketTourOptionModel;
        if (mainOption == null && !RaynaTicketManager.shared.selectedTourModel.isEmpty()) {
            mainOption = RaynaTicketManager.shared.selectedTourModel.get(0);
        }

        if (mainOption != null && mainOption.getTmpAdultValue() > 0 && model.getRatioPerPax() != null && model.getRatioPerPax() > 0) {
            return mainOption.getTmpAdultValue() * model.getRatioPerPax();
        }
        return null;
    }

    private void showRatioToastIfNeeded(int limit) {
        Toast.makeText( requireContext(), "You can select maximum " + limit + " passenger(s) for this add-on", Toast.LENGTH_SHORT ).show();
    }

    private void updateCount(TextView textView, boolean isIncrement, TourOptionsModel model) {
        if (isIncrement) {
            if (!canIncreasePax(model)) return;

            int total = model.getTmpAdultValue() + model.getTmpChildValue() + model.getTmpInfantValue();

            int maxTotalAllowed = getMaxPax(model);
            if (!model.getAvailabilityTimeSlot().isEmpty()) {
                OptionalInt tmpTotalAllowed = model.getAvailabilityTimeSlot().stream().mapToInt(RaynaTimeSlotModel::getTotalSeats).max();
                if (tmpTotalAllowed.isPresent()) {
                    int slotSeats = tmpTotalAllowed.getAsInt();
                    if (maxTotalAllowed == 0 || slotSeats < maxTotalAllowed) {
                        maxTotalAllowed = slotSeats;
                    }
                }
            }

            if (total + 1 > maxTotalAllowed) {
                Graphics.showAlertDialogWithOkButton(requireContext(), getString(R.string.app_name), "A maximum of " + maxTotalAllowed + " passenger can be selected.");
                return;
            }
        }

        int currentValue = Utils.getNumericValue(textView);

        if (isIncrement) {
            currentValue++;
        } else if (currentValue > 0) {
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

        updateAdultChildInfantValue(model);
        hapticFeedback();
    }

    private void updateCountForInfant(TextView textView, boolean isIncrement, TourOptionsModel tourOptionsModel) {
        if (isIncrement) {
            if (!canIncreasePax(tourOptionsModel)) return;

            int total = tourOptionsModel.getTmpAdultValue() + tourOptionsModel.getTmpChildValue() + tourOptionsModel.getTmpInfantValue();

            int maxTotalAllowed = getMaxPax(tourOptionsModel);
            if (!tourOptionsModel.getAvailabilityTimeSlot().isEmpty()) {
                OptionalInt tmpTotalAllowed = tourOptionsModel.getAvailabilityTimeSlot().stream().mapToInt(RaynaTimeSlotModel::getTotalSeats).max();
                if (tmpTotalAllowed.isPresent()) {
                    int slotSeats = tmpTotalAllowed.getAsInt();
                    if (maxTotalAllowed == 0 || slotSeats < maxTotalAllowed) {
                        maxTotalAllowed = slotSeats;
                    }
                }
            }

            if (total + 1 > maxTotalAllowed) {
                Graphics.showAlertDialogWithOkButton(requireContext(), getString(R.string.app_name), "A maximum of " + maxTotalAllowed + " passenger can be selected.");
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
        updateAdultChildInfantValue(tourOptionsModel);
        hapticFeedback();
    }

    private void updateDoneButtonState() {
        if (tourOptionsModel == null) return;

        int totalPax = tourOptionsModel.getTmpAdultValue() + tourOptionsModel.getTmpChildValue() + tourOptionsModel.getTmpInfantValue();
        boolean isPaxSelected = totalPax > 0;

        boolean isSlotSelected = true;
        if (tourOptionsModel.getIsSlot()) {
            isSlotSelected = selectedSlotPosition != -1;
        }

        boolean enable = isPaxSelected && isSlotSelected;

        binding.btnDone.setEnabled(true);
        binding.btnDone.setBackgroundResource(R.drawable.selected_btn_bg);
//        if (enable) {
//            binding.btnDone.setBackgroundResource(R.drawable.selected_btn_bg);
//        } else {
//            binding.btnDone.setBackgroundResource(R.drawable.not_select_btn_bg);
//        }
    }

    private void updateAdultChildInfantValue(TourOptionsModel model) {
        int adult = model.getTmpAdultValue();
        int child = model.getTmpChildValue();
        int infant = model.getTmpInfantValue();

        String unit = model.getUnit();
        if (!TextUtils.isEmpty(unit)) {
            binding.tvTotalAdult.setText(adult + " " + unit);
            binding.tvTotalChild.setText(child + " " + unit);
            binding.tvTotalInfants.setText(infant + " " + unit);
        } else {
            binding.tvTotalAdult.setText(String.valueOf(adult));
            binding.tvTotalChild.setText(String.valueOf(child));
            binding.tvTotalInfants.setText(String.valueOf(infant));
        }

        int totalPax = adult + child + infant;

        if (totalPax == 0) {
            clearSelectedTimeSlot();
        }
        updateDoneButtonState();
        if (model.getTmpAdultValue() > 0) {
            binding.addQuantityAdult.setBackgroundResource(R.drawable.tour_option_pax_selected_bg);
        } else {
            binding.addQuantityAdult.setBackgroundResource(R.drawable.tour_option_spinner_stock_bg);
        }

        if (model.getTmpChildValue() > 0) {
            binding.addQuantityChild.setBackgroundResource(R.drawable.tour_option_pax_selected_bg);
        } else {
            binding.addQuantityChild.setBackgroundResource(R.drawable.tour_option_spinner_stock_bg);
        }

        if (model.getTmpInfantValue() > 0) {
            binding.addQuantityInfants.setBackgroundResource(R.drawable.tour_option_pax_selected_bg);
        } else {
            binding.addQuantityInfants.setBackgroundResource(R.drawable.tour_option_spinner_stock_bg);
        }

        if (model.updateAdultPrices() == 0) {
            binding.adultsPrice.setVisibility(View.GONE);
        } else {
            binding.adultsPrice.setVisibility(View.VISIBLE);
            Utils.setStyledText(requireActivity(), binding.adultsPrice, Utils.roundFloatValue(model.updateAdultPrices()));
        }

        if (model.updateChildPrices() == 0) {
            binding.childPrice.setVisibility(View.GONE);
        } else {
            binding.childPrice.setVisibility(View.VISIBLE);
            Utils.setStyledText(requireActivity(), binding.childPrice, Utils.roundFloatValue(model.updateChildPrices()));
        }

        if (model.updateInfantPrices() == 0) {
            binding.infantsPrice.setVisibility(View.GONE);
        } else {
            binding.infantsPrice.setVisibility(View.VISIBLE);
            Utils.setStyledText(requireActivity(), binding.infantsPrice, Utils.roundFloatValue(model.updateInfantPrices()));
        }
    }

    private void shouldHideDiscount(TextView textView, TextView mainTextView, float withoutDiscountAmount, float finalAmount) {
        if (finalAmount >= withoutDiscountAmount) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        Utils.setStyledText(requireActivity(), textView, String.valueOf(Utils.roundFloatValue(withoutDiscountAmount)));
        Utils.setStyledText(requireActivity(), mainTextView, Utils.roundFloatValue(finalAmount));

        if (withoutDiscountAmount == 0) {
            textView.setVisibility(View.GONE);
        }
    }

    private void hapticFeedback() {
        if (requireActivity().getWindow() != null) {
            requireActivity().getWindow().getDecorView().performHapticFeedback(android.view.HapticFeedbackConstants.CONTEXT_CLICK);
        }
    }

    private void setupAddonInfo(TourOptionsModel model) {
        if (model == null) return;

        String optionName = Utils.isNullOrEmpty(model.getOptionName()) ? model.getTitle() : model.getOptionName();
        if (TextUtils.isEmpty(optionName)) {
            binding.tvAddonNameTitle.setVisibility(View.GONE);
            binding.tvAddonName.setVisibility(View.GONE);
        } else {
            binding.tvAddonNameTitle.setVisibility(View.VISIBLE);
            binding.tvAddonName.setVisibility(View.VISIBLE);
            binding.tvAddonName.setText(optionName);
        }

        String description = Utils.isNullOrEmpty(model.getOptionDescription()) ? model.getSortDescription() : model.getOptionDescription();
        if (TextUtils.isEmpty(description)) {
            binding.tvAddonDescriptionTitle.setVisibility(View.GONE);
            binding.tvAddonDescription.setVisibility(View.GONE);
        } else {
            binding.tvAddonDescriptionTitle.setVisibility(View.VISIBLE);
            binding.tvAddonDescription.setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                binding.tvAddonDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
            } else {
                binding.tvAddonDescription.setText(Html.fromHtml(description));
            }
        }

        String inclusion = Utils.notNullString(model.getInclusion());
        if (TextUtils.isEmpty(inclusion)) {
            binding.tvAddonInclusionTitle.setVisibility(View.GONE);
            binding.tvAddonInclusion.setVisibility(View.GONE);
        } else {
            binding.tvAddonInclusionTitle.setVisibility(View.VISIBLE);
            binding.tvAddonInclusion.setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                binding.tvAddonInclusion.setText(Html.fromHtml(inclusion, Html.FROM_HTML_MODE_COMPACT));
            } else {
                binding.tvAddonInclusion.setText(Html.fromHtml(inclusion));
            }
        }

        String exclusion = Utils.isNullOrEmpty(model.getExclusion()) ? model.getTourExclusion() : model.getExclusion();
        if (TextUtils.isEmpty(exclusion)) {
            binding.tvAddonExclusionTitle.setVisibility(View.GONE);
            binding.tvAddonExclusion.setVisibility(View.GONE);
        } else {
            binding.tvAddonExclusionTitle.setVisibility(View.VISIBLE);
            binding.tvAddonExclusion.setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                binding.tvAddonExclusion.setText(Html.fromHtml(exclusion, Html.FROM_HTML_MODE_COMPACT));
            } else {
                binding.tvAddonExclusion.setText(Html.fromHtml(exclusion));
            }
        }

        StringBuilder days = new StringBuilder();
        RaynaOprationDaysModel opDays = model.getOperationdays();
        if (opDays != null) {
            ArrayList<String> dayList = new ArrayList<>();
            if (opDays.getMonday() == 1) dayList.add("Mon");
            if (opDays.getTuesday() == 1) dayList.add("Tue");
            if (opDays.getWednesday() == 1) dayList.add("Wed");
            if (opDays.getThursday() == 1) dayList.add("Thu");
            if (opDays.getFriday() == 1) dayList.add("Fri");
            if (opDays.getSaturday() == 1) dayList.add("Sat");
            if (opDays.getSunday() == 1) dayList.add("Sun");

            days.append(TextUtils.join(", ", dayList));
        }

        if (days.length() == 0) {
            binding.tvAddonOperationDaysTitle.setVisibility(View.GONE);
            binding.tvAddonOperationDays.setVisibility(View.GONE);
        } else {
            binding.tvAddonOperationDaysTitle.setVisibility(View.VISIBLE);
            binding.tvAddonOperationDays.setVisibility(View.VISIBLE);
            binding.tvAddonOperationDays.setText(days.toString());
        }
    }


    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class SlotAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.rayna_time_slot_new_item));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (!(holder instanceof ViewHolder viewHolder)) return;
            RaynaTimeSlotModel model = (RaynaTimeSlotModel) getData().get(position);

            if (model != null) {
                viewHolder.mBinding.tvTimeSlot.setText(model.getAvailabilityTime());
                if (model.getAvailable() != 0){
                    viewHolder.mBinding.tvAvailableTimeSlots.setText("(" + model.getAvailable() + " Available)");
                    viewHolder.mBinding.tvAvailableTimeSlots.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.mBinding.tvAvailableTimeSlots.setVisibility(View.GONE);
                }
                if (selectedSlotPosition == position) {
                    viewHolder.mBinding.getRoot().setBackgroundResource(R.drawable.time_slot_selected_bg);
                    viewHolder.mBinding.tvTimeSlot.setTextColor(Color.WHITE);
                    viewHolder.mBinding.btnSelectTimeSlot.setChecked(true);
                    viewHolder.mBinding.btnSelectTimeSlot.setButtonTintList(ContextCompat.getColorStateList(requireContext(), R.color.ticket_selected_colour));
                } else {
                    viewHolder.mBinding.getRoot().setBackgroundResource(R.drawable.time_slot_unselected_bg);
                    viewHolder.mBinding.tvTimeSlot.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text));
                    viewHolder.mBinding.btnSelectTimeSlot.setChecked(false);
                    viewHolder.mBinding.btnSelectTimeSlot.setButtonTintList(ContextCompat.getColorStateList(requireContext(), R.color.white_70));
                }

                View.OnClickListener clickListener = v -> {
                    Utils.preventDoubleClick(v);
                    int pos = viewHolder.getBindingAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;
                    int prev = selectedSlotPosition;
                    selectedSlotPosition = pos;
                    if (prev >= 0) notifyItemChanged(prev);
                    notifyItemChanged(pos);
                    updateDoneButtonState();
                };

                viewHolder.mBinding.getRoot().setOnClickListener(clickListener);
                viewHolder.mBinding.btnSelectTimeSlot.setOnClickListener(clickListener);
            }
        }

        private static class ViewHolder extends RecyclerView.ViewHolder {
            RaynaTimeSlotNewItemBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = RaynaTimeSlotNewItemBinding.bind(itemView);
            }
        }
    }
}
