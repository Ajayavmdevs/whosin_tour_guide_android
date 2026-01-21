package com.whosin.app.ui.activites.raynaTicket.BottomSheets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.CustomTypefaceSpan;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentRaynaMoreInfoBottomSheetBinding;
import com.whosin.app.databinding.ItemMoreInfoDesignBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.models.BigBusModels.BigBusOptionsItemModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskCancellationPolicyModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskOptionDataModel;
import com.whosin.app.service.models.rayna.RaynaOprationDaysModel;
import com.whosin.app.service.models.rayna.TourOptionsModel;
import com.whosin.app.service.models.whosinTicketModel.RaynaWhosinBookingRulesModel;
import com.whosin.app.service.models.whosinTicketModel.RaynaWhosinInfoModel;
import com.whosin.app.service.models.whosinTicketModel.RaynaWhosinMoreInfoModel;
import com.whosin.app.service.models.whosinTicketModel.WhosinTicketTourOptionModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.adapter.raynaTicketAdapter.TourCancelPolicyAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RaynaMoreInfoBottomSheet extends DialogFragment {

    private FragmentRaynaMoreInfoBottomSheetBinding binding;
    public TourOptionsModel tourOptionsModel = null;
    public TravelDeskOptionDataModel travelDeskOptionDataModel = null;
    public WhosinTicketTourOptionModel whosinTicketTourOptionModel = null;
    public BigBusOptionsItemModel bigBusOptionsItemModel = null;
    public Boolean isFromTicketDetail = false;
    public Activity activity = null;
    public boolean isFromRaynaWhosinTicket = false;
    public boolean isFromRaynaWhosinCustomTicket = false;
    public boolean isFromTravelDeskTicket = false;
    public boolean isFromBigBusTicket = false;
    private final MoreInfoAdapter<RaynaWhosinInfoModel> moreInfoAdapter = new MoreInfoAdapter<>();

    private final TourCancelPolicyAdapter<TourOptionsModel> tourOptionsAdapter = new TourCancelPolicyAdapter<>();
    public String policyText = "";
    public JsonObject jsonObject;
    public boolean isFormViewTicket = false;
    public boolean isloadedPolicy = false;
    public boolean isNonRefundable = false;
//    public boolean isWhosinTypeTicket = false;
//    public boolean isWhosinCustomTypeTicket = false;
//    public boolean isTravelDeskTicket = false;

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

        binding = FragmentRaynaMoreInfoBottomSheetBinding.bind(v);

        binding.tvTitle.setText(Utils.getLangValue("info"));
        binding.tvCancellationTimeTitle.setText(Utils.getLangValue("cancellation_time"));
        binding.tvRefundTitle.setText(Utils.getLangValue("refund"));

        if (activity == null) activity = requireActivity();

        Glide.with(requireActivity()).load(R.drawable.icon_close).into(binding.ivClose);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.moreInfoRecycleview.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
        binding.moreInfoRecycleview.setAdapter(moreInfoAdapter);

        requestRaynaMoreInfo();

        if (!isNonRefundable){

            setTabForInfo();

            binding.cancellationView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
            binding.cancellationView.setAdapter(tourOptionsAdapter);

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
        }else {
            binding.tabContainer.setVisibility(View.GONE);
        }



    }

    private void setListener() {

        binding.ivClose.setOnClickListener(view -> dismiss());

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if (position == 0) {
                    binding.moreInfoRecycleview.setVisibility(View.VISIBLE);
                    binding.cancellationConstraintLayoutView.setVisibility(View.GONE);
                } else if (position == 1) {
                    if (!isloadedPolicy){
                        if (isFromRaynaWhosinTicket){
                            requestWhosinTypePolicy();
                        } else if (isFromTravelDeskTicket) {
                            requestTravelDeskTicketTourPolicy();
                        } else if (isFromRaynaWhosinCustomTicket) {
                            requestWhosinCustomTicketTourPolicy();
                        } else if (isFromBigBusTicket) {
                            requestOctaTicketTourPolicy();
                        } else {
                            requestRaynaTourPolicy();
                        }
                        isloadedPolicy = true;
                    }else {
                        binding.cancellationConstraintLayoutView.setVisibility(View.VISIBLE);
                        if (isFromBigBusTicket){
                            binding.cardConstraint.setVisibility(View.GONE);
                        }
                    }

                    binding.moreInfoRecycleview.setVisibility(View.GONE);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }

    public int getLayoutRes() {
        return R.layout.fragment_rayna_more_info_bottom_sheet;
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

    private void setTabForInfo() {

        TabLayout.Tab tab1 = binding.tabLayout.newTab();
        tab1.setCustomView(R.layout.custom_tab_layout);
        binding.tabLayout.addTab(tab1);

        TabLayout.Tab tab2 = binding.tabLayout.newTab();
        tab2.setCustomView(R.layout.custom_tab_layout);
        binding.tabLayout.addTab(tab2);


        for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = binding.tabLayout.getTabAt(i);
            View customTabView = tab.getCustomView();
            if (customTabView != null) {
                TextView tvTabText = customTabView.findViewById(R.id.tvTabText);
                ImageView imageNotificationCount = customTabView.findViewById(R.id.imageNotificationCount);
                if (i == 0) {
                    tvTabText.setText(Utils.getLangValue("information"));
                } else if (i == 1) {
                    tvTabText.setText(Utils.getLangValue("cancellation_policy"));
                }
                imageNotificationCount.setVisibility(View.GONE);

            }
        }


    }

    private void setHtmlTextOrHide(TextView titleView, TextView contentView, String htmlContent) {
        if (!TextUtils.isEmpty(htmlContent)) {

            if (isHtml(htmlContent)) {
                htmlContent = htmlContent
                        .replaceAll("(?i)<p>\\s*</p>", "")
                        .replaceAll("(?i)<ul>\\s*</ul>", "")
                        .replaceAll("(?i)<p><br\\s*/?></p>", "")
                        .replaceAll("(?i)<br\\s*/?>\\s*(<br\\s*/?>\\s*)+", "<br>")
                        .replace("<li>", "<li>&nbsp;&nbsp;");
                htmlContent = htmlContent.trim();
                contentView.setText(Html.fromHtml(htmlContent, HtmlCompat.FROM_HTML_MODE_LEGACY));
            }else {
                contentView.setText(htmlContent);
            }

            contentView.setMovementMethod(LinkMovementMethod.getInstance());


            titleView.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.VISIBLE);
        } else {
            titleView.setVisibility(View.GONE);
            contentView.setVisibility(View.GONE);
        }
    }

    private boolean isHtml(String text) {
        return text != null && (text.contains("<") && text.contains(">"));
    }

    private String getActiveDays(RaynaOprationDaysModel op) {
        if (op == null) {
            return "";
        }
        Map<String, Integer> daysMap = new LinkedHashMap<>();
        daysMap.put("Monday", op.getMonday());
        daysMap.put("Tuesday", op.getTuesday());
        daysMap.put("Wednesday", op.getWednesday());
        daysMap.put("Thursday", op.getThursday());
        daysMap.put("Friday", op.getFriday());
        daysMap.put("Saturday", op.getSaturday());
        daysMap.put("Sunday", op.getSaturday());

        return daysMap.entrySet().stream()
                .filter(entry -> entry.getValue() == 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestRaynaMoreInfo() {
        binding.progressView.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("customTicketId", RaynaTicketManager.shared.raynaTicketDetailModel.getId());
        DataService.shared(activity).requestRaynaMoreInfo(jsonObject, new RestCallback<ContainerListModel<RaynaWhosinMoreInfoModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaWhosinMoreInfoModel> model, String error) {
                binding.progressView.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()){
                    String id = "";
                    if (isFromRaynaWhosinTicket) {
                        id = tourOptionsModel.get_id();
                    } else if (isFromTravelDeskTicket) {
                       id = String.valueOf(travelDeskOptionDataModel.getId());
                    } else if (isFromRaynaWhosinCustomTicket) {
                      id = whosinTicketTourOptionModel.getTourOptionId();
                    } else if (isFromBigBusTicket) {
                        id = bigBusOptionsItemModel.getId();
                    } else {
                        id = String.valueOf(tourOptionsModel.getTourOptionId());
                    }

                    String finalId = id;
                    Optional<RaynaWhosinMoreInfoModel> infoModel = model.data.stream().filter(p -> p.getTourOptionId().equals(finalId)).findFirst();
                    infoModel.ifPresent(raynaModel -> {
                        List<RaynaWhosinInfoModel> infoList = new ArrayList<>();

                        if (isNonRefundable) {
                            RaynaWhosinInfoModel model1 = new RaynaWhosinInfoModel();
//                            model1.setKey("Cancellation policy");
                            model1.setValue(Utils.getLangValue("non_refundable"));
                            infoList.add(model1);
                        }

                        infoList.addAll(raynaModel.getInfoList());
                        moreInfoAdapter.updateData(infoList);
                    });

//                    infoModel.ifPresent(raynaWhosinMoreInfoModel -> moreInfoAdapter.updateData(raynaWhosinMoreInfoModel.getInfoList()));
                }

            }
        });
    }

    private void requestRaynaTourPolicy() {
        binding.cancellationConstraintLayoutView.setVisibility(View.GONE);
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
            jsonObject.addProperty("noOfAdult", tourOptionsModel.getTmpAdultValue() == 0 ? 1 : tourOptionsModel.getTmpAdultValue());
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
                    binding.cancellationConstraintLayoutView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void requestWhosinTypePolicy() {
        binding.cancellationConstraintLayoutView.setVisibility(View.GONE);
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

            jsonObject.addProperty("adults", tourOptionsModel.getTmpAdultValue() == 0 ? 1 : tourOptionsModel.getTmpAdultValue());
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
                    binding.cancellationConstraintLayoutView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void requestTravelDeskTicketTourPolicy() {
        binding.cancellationConstraintLayoutView.setVisibility(View.GONE);
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
            jsonObject.addProperty("adults", travelDeskOptionDataModel.getTmpAdultValue() == 0 ? 1 : travelDeskOptionDataModel.getTmpAdultValue());
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
                    binding.cancellationConstraintLayoutView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void requestWhosinCustomTicketTourPolicy() {
        binding.cancellationConstraintLayoutView.setVisibility(View.GONE);
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
            jsonObject.addProperty("adults", whosinTicketTourOptionModel.getTmpAdultValue() == 0 ? 1 : whosinTicketTourOptionModel.getTmpAdultValue());
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
                    binding.cancellationConstraintLayoutView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void requestOctaTicketTourPolicy() {
        binding.cancellationConstraintLayoutView.setVisibility(View.GONE);
        binding.progressView.setVisibility(View.VISIBLE);
        jsonObject = new JsonObject();
        jsonObject.addProperty("tourId", bigBusOptionsItemModel.getTourId());
        jsonObject.addProperty("optionId", bigBusOptionsItemModel.getId());
        if (TextUtils.isEmpty(bigBusOptionsItemModel.getTourOptionSelectDate())){
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
            jsonObject.addProperty("date", date);
        }else {
            jsonObject.addProperty("date", bigBusOptionsItemModel.getTourOptionSelectDate());
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
                    binding.cancellationConstraintLayoutView.setVisibility(View.VISIBLE);
                    binding.cardConstraint.setVisibility(View.GONE);
                    binding.notesSection.setVisibility(View.VISIBLE);
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

    private class MoreInfoAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_more_info_design));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;

            RaynaWhosinInfoModel model = (RaynaWhosinInfoModel) getItem(position);

            String key = model.getKey();
            if (key != null && !key.isEmpty()) {
                String capitalized = key.substring(0, 1).toUpperCase() + key.substring(1);
                viewHolder.binding.infoTitle.setText(capitalized);
            } else {
                viewHolder.binding.infoTitle.setText("");
            }

            if (model.getValue() instanceof String) {
                String strValue = (String) model.getValue();
                if (isNonRefundable && strValue.equals("Non Refundable")){
                    viewHolder.binding.infoValue.setVisibility(View.GONE);
                    viewHolder.binding.cancellationPolicyLayout.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.binding.infoValue.setVisibility(View.VISIBLE);
                    viewHolder.binding.cancellationPolicyLayout.setVisibility(View.GONE);
                    viewHolder.binding.infoValue.setText(strValue);
                    setHtmlTextOrHide(viewHolder.binding.infoTitle,viewHolder.binding.infoValue,strValue);
                }

            } else if (model.getValue() instanceof Map) {
                Gson gson = new Gson();
                RaynaOprationDaysModel schedule = gson.fromJson(gson.toJson(model.getValue()), RaynaOprationDaysModel.class);
                if (schedule != null){
                    viewHolder.binding.infoValue.setText(getActiveDays(schedule));
                }
            }else if (model.getValue() instanceof List) {
                List<?> list = (List<?>) model.getValue();
                if (!list.isEmpty()) {
                    String joined = TextUtils.join("\n", list);
                    viewHolder.binding.infoValue.setVisibility(View.VISIBLE);
                    viewHolder.binding.cancellationPolicyLayout.setVisibility(View.GONE);
                    viewHolder.binding.infoValue.setText(joined);
                } else {
                    viewHolder.binding.infoValue.setVisibility(View.GONE);
                }
            }

            boolean isLastItem = position == getItemCount() - 1;
            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.18f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }


        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemMoreInfoDesignBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemMoreInfoDesignBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------


}


