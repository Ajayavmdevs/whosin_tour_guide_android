package com.whosin.business.ui.activites.raynaTicket.BottomSheets;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.whosin.business.R;
import com.whosin.business.comman.Utils;
import com.whosin.business.databinding.FragmentCancellationPolicyBottomSheetBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.RaynaTicketManager;
import com.whosin.business.service.models.ContainerListModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.TravelDeskModels.TravelDeskCancellationPolicyModel;
import com.whosin.business.service.models.TravelDeskModels.TravelDeskOptionDataModel;
import com.whosin.business.service.models.rayna.TourOptionsModel;
import com.whosin.business.service.models.whosinTicketModel.RaynaWhosinBookingRulesModel;
import com.whosin.business.service.models.whosinTicketModel.WhosinTicketTourOptionModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.adapter.raynaTicketAdapter.TourCancelPolicyAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CancellationPolicyBottomSheet extends DialogFragment {

    private FragmentCancellationPolicyBottomSheetBinding binding;

    private final TourCancelPolicyAdapter<TourOptionsModel> tourOptionsAdapter = new TourCancelPolicyAdapter<>();
    public TourOptionsModel tourOptionsModel;
    public TravelDeskOptionDataModel travelDeskOptionDataModel;
    public WhosinTicketTourOptionModel whosinTicketTourOptionModel;
    public String policyText = "";
    public JsonObject jsonObject;
    public boolean isFormViewTicket = false;
    public boolean isWhosinTypeTicket = false;
    public boolean isWhosinCustomTypeTicket = false;
    public boolean isTravelDeskTicket = false;
    public boolean isFromBigBusTicket = false;

    public Activity activity;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;


    }

    private void initUi(View v) {
        binding = FragmentCancellationPolicyBottomSheetBinding.bind(v);

        if (activity == null){
            activity = requireActivity();
        }

        Glide.with(requireActivity()).load(R.drawable.icon_close).into(binding.ivClose);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (isWhosinTypeTicket){
            requestWhosinTypePolicy();
        } else if (isTravelDeskTicket) {
            requestTravelDeskTicketTourPolicy();
        } else if (isWhosinCustomTypeTicket) {
            requestWhosinCustomTicketTourPolicy();
        } else if (isFromBigBusTicket) {
           requestOctaTicketTourPolicy();
        } else {
            requestRaynaTourPolicy();
        }


        binding.cancellationView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.cancellationView.setAdapter(tourOptionsAdapter);

//        String formattedDescription = (isFormViewTicket ? policyText : tourOptionsModel.getOptionDetail().getCancellationPolicyDescription())
//                .replace("<li>", "<li>&nbsp;&nbsp;&nbsp;&nbsp;")
//                .replace("</li>", "</li><br>");

        String description = null;

        if (isFormViewTicket) {
            if (policyText != null && !policyText.trim().isEmpty()) {
                description = policyText;
            }
        } else {
            if (tourOptionsModel != null
                    && tourOptionsModel.getOptionDetail() != null
                    && tourOptionsModel.getOptionDetail().getCancellationPolicyDescription() != null
                    && !tourOptionsModel.getOptionDetail().getCancellationPolicyDescription().trim().isEmpty()) {
                description = tourOptionsModel.getOptionDetail().getCancellationPolicyDescription();
            }
        }

        String formattedDescription = "";
        if (description != null && !description.isEmpty()) {
            formattedDescription = description
                    .replace("<li>", "<li>&nbsp;&nbsp;&nbsp;&nbsp;")
                    .replace("</li>", "</li><br>");
        }


        binding.tvDescription.setText(Html.fromHtml(formattedDescription, Html.FROM_HTML_MODE_COMPACT));
    }

    private void setListener() {
        binding.ivClose.setOnClickListener(view -> dismiss());
    }

    public int getLayoutRes() {
        return R.layout.fragment_cancellation_policy_bottom_sheet;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private boolean isHtml(String text) {
        return text != null && (text.contains("<") && text.contains(">"));
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestRaynaTourPolicy() {
        binding.progressView.setVisibility(View.VISIBLE);
        if (!isFormViewTicket){
            jsonObject = new JsonObject();
            jsonObject.addProperty("tourId", tourOptionsModel.getTourId());
            jsonObject.addProperty("tourOptionId", tourOptionsModel.getTourOptionId());
            jsonObject.addProperty("contractId", RaynaTicketManager.shared.getContractId());
            jsonObject.addProperty("transferId", tourOptionsModel.getTransferId());
            if (!TextUtils.isEmpty(tourOptionsModel.getTourOptionSelectDate())){
                jsonObject.addProperty("date", tourOptionsModel.getTourOptionSelectDate());
            }else {
                jsonObject.addProperty("date", tourOptionsModel.getBookingDate());
            }
            jsonObject.addProperty("time", tourOptionsModel.getStartTime());
            jsonObject.addProperty("noOfAdult", tourOptionsModel.getTmpAdultValue());
            jsonObject.addProperty("noOfChild", tourOptionsModel.getTmpChildValue());
            jsonObject.addProperty("noOfInfant", tourOptionsModel.getTmpInfantValue());
        }else {
            if (jsonObject.isEmpty() || jsonObject.isJsonNull()) jsonObject = new JsonObject();
        }
        DataService.shared(activity).requestRaynaTourPolicy(jsonObject, new RestCallback<ContainerListModel<TourOptionsModel>>(this) {
            @Override
            public void result(ContainerListModel<TourOptionsModel> model, String error) {
                binding.progressView.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    tourOptionsAdapter.updateData(model.data);
                    binding.scrollView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void requestWhosinTypePolicy() {
        binding.progressView.setVisibility(View.VISIBLE);
        if (isFormViewTicket){
            if (jsonObject.isEmpty() || jsonObject.isJsonNull()) jsonObject = new JsonObject();
        }else {
            jsonObject = new JsonObject();
            jsonObject.addProperty("ticketId", tourOptionsModel.getCustomTicketId());
            jsonObject.addProperty("optionId", tourOptionsModel.get_id());
            if (TextUtils.isEmpty(tourOptionsModel.getTourOptionSelectDate())){
                jsonObject.addProperty("date", tourOptionsModel.getBookingDate());
            }else {
                jsonObject.addProperty("date", tourOptionsModel.getTourOptionSelectDate());
            }
            if (tourOptionsModel.getAvailabilityType().equals("slot") && tourOptionsModel.getSlotModelForWhosinTicket() != null){
                jsonObject.addProperty("time", tourOptionsModel.getSlotModelForWhosinTicket().getAvailabilityTime());
            }else {
                jsonObject.addProperty("time", tourOptionsModel.getAvailabilityTime());
            }

            jsonObject.addProperty("adults", tourOptionsModel.getTmpAdultValue());
            jsonObject.addProperty("childs", tourOptionsModel.getTmpChildValue());
            jsonObject.addProperty("infants", tourOptionsModel.getTmpInfantValue());
        }

        DataService.shared(activity).requestWhosinTicketTourPolicy(jsonObject, new RestCallback<ContainerListModel<RaynaWhosinBookingRulesModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaWhosinBookingRulesModel> model, String error) {
                binding.progressView.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    List<TourOptionsModel> list = new ArrayList<>();
                    for (RaynaWhosinBookingRulesModel raynaWhosinBookingRulesModel : model.data){
                        TourOptionsModel tmpModel = new TourOptionsModel();
                        tmpModel.setFromDate(raynaWhosinBookingRulesModel.getFromDate());
                        tmpModel.setToDate(raynaWhosinBookingRulesModel.getToDate());
                        tmpModel.setPercentage(raynaWhosinBookingRulesModel.getPercentage());
                        list.add(tmpModel);
                    }
                    tourOptionsAdapter.updateData(list);
                    binding.scrollView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void requestTravelDeskTicketTourPolicy() {
        binding.progressView.setVisibility(View.VISIBLE);
        if (isFormViewTicket){
            if (jsonObject.isEmpty() || jsonObject.isJsonNull()) jsonObject = new JsonObject();
        }else {
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
        }

        DataService.shared(activity).requestTravelDeskTicketTourPolicy(jsonObject, new RestCallback<ContainerListModel<TravelDeskCancellationPolicyModel>>(this) {
            @Override
            public void result(ContainerListModel<TravelDeskCancellationPolicyModel> model, String error) {
                binding.progressView.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    List<TourOptionsModel> list = new ArrayList<>();
                    for (TravelDeskCancellationPolicyModel raynaWhosinBookingRulesModel : model.data){
                        TourOptionsModel tmpModel = new TourOptionsModel();
                        tmpModel.setFromDate(raynaWhosinBookingRulesModel.getFromDate());
                        tmpModel.setToDate(raynaWhosinBookingRulesModel.getToDate());
                        tmpModel.setPercentage(raynaWhosinBookingRulesModel.getPercentage());
                        list.add(tmpModel);
                    }
                    tourOptionsAdapter.updateData(list);
                    binding.scrollView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void requestWhosinCustomTicketTourPolicy() {
        binding.progressView.setVisibility(View.VISIBLE);
        if (isFormViewTicket){
            if (jsonObject.isEmpty() || jsonObject.isJsonNull()) jsonObject = new JsonObject();
        }else {
            jsonObject = new JsonObject();
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

        }

        DataService.shared(activity).requestWhosinCustomTicketTourPolicy(jsonObject, new RestCallback<ContainerListModel<RaynaWhosinBookingRulesModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaWhosinBookingRulesModel> model, String error) {
                binding.progressView.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    List<TourOptionsModel> list = new ArrayList<>();
                    for (RaynaWhosinBookingRulesModel raynaWhosinBookingRulesModel : model.data){
                        TourOptionsModel tmpModel = new TourOptionsModel();
                        tmpModel.setFromDate(raynaWhosinBookingRulesModel.getFromDate());
                        tmpModel.setToDate(raynaWhosinBookingRulesModel.getToDate());
                        tmpModel.setPercentage(raynaWhosinBookingRulesModel.getPercentage());
                        list.add(tmpModel);
                    }
                    tourOptionsAdapter.updateData(list);
                    binding.scrollView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void requestOctaTicketTourPolicy() {
        binding.progressView.setVisibility(View.VISIBLE);
        if (isFormViewTicket){
            if (jsonObject.isEmpty() || jsonObject.isJsonNull()) jsonObject = new JsonObject();
        }
        DataService.shared(activity).requestOctaCancellationPolicy(jsonObject, new RestCallback<ContainerModel<String>>(this) {
            @Override
            public void result(ContainerModel<String> model, String error) {
                binding.progressView.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!TextUtils.isEmpty(model.getData())){
                    binding.cardConstraint.setVisibility(View.GONE);
                    binding.notesSection.setVisibility(View.VISIBLE);
                    binding.scrollView.setVisibility(View.VISIBLE);
                    if (isHtml(model.getData())) {

                        String content = model.getData()
                                .replaceAll("(?i)<p>\\s*</p>", "")
                                .replaceAll("(?i)<ul>\\s*</ul>", "")
                                .replaceAll("(?i)<p><br\\s*/?></p>", "")
                                .replaceAll("(?i)<br\\s*/?>\\s*(<br\\s*/?>\\s*)+", "<br>")
                                .replace("<li>", "<li>&nbsp;&nbsp;");
                        content = content.trim();
                        binding.tvDescription.setText(Html.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY));
                    }else {
                        binding.tvDescription.setText(model.getData());
                    }

                }
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------



    // endregion
    // --------------------------------------

}