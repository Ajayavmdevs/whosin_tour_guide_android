package com.whosin.app.ui.activites.raynaTicket.BottomSheets;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentRaynaTimeSlotBottomShetBinding;
import com.whosin.app.databinding.ItemRaynaTimeSlotBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.rayna.RaynaTimeSlotModel;
import com.whosin.app.service.models.rayna.TourOptionsModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.raynaTicket.RaynaParticipantDetailActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class RaynaTimeSlotBottomSheet extends DialogFragment {

    private FragmentRaynaTimeSlotBottomShetBinding binding;

    private final SlotAdapter<RaynaTimeSlotModel> slotAdapter = new SlotAdapter<>();

    public TourOptionsModel tourOptionsModel = null;

    public CommanCallback<RaynaTimeSlotModel> callback;

    private RaynaTimeSlotModel raynaTimeSlotModel;

    private int selectedSlotPosition = -1;

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

    @SuppressLint("SetTextI18n")
    private void initUi(View v) {
        binding = FragmentRaynaTimeSlotBottomShetBinding.bind(v);

        Glide.with(requireActivity()).load(R.drawable.icon_close).into(binding.ivClose);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (!TextUtils.isEmpty(RaynaTicketManager.shared.object.get(AppConstants.DATE).getAsString())) {
            binding.txtSelectedDate.setVisibility(View.VISIBLE);
        } else {
            binding.txtSelectedDate.setVisibility(View.GONE);
        }


        if (tourOptionsModel != null && !TextUtils.isEmpty(tourOptionsModel.getTourOptionSelectDate())){
            binding.txtSelectedDate.setText("Date : " + tourOptionsModel.getTourOptionSelectDate());
        }else {
            binding.txtSelectedDate.setText("Date : " +Utils.changeDateFormat(String.valueOf(new Date()), "EEE, dd MMM yyyy", "yyyy-MM-dd"));
        }

        updateButtonColor();

        binding.raynaTimeSlotRecyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false));
        binding.raynaTimeSlotRecyclerView.setAdapter(slotAdapter);

        JsonObject jsonObject = new JsonObject();

        if (tourOptionsModel != null){
            jsonObject.addProperty("tourId", tourOptionsModel.getTourId());
            jsonObject.addProperty("tourOptionId", tourOptionsModel.getTourOptionId());
            jsonObject.addProperty("contractId", RaynaTicketManager.shared.getContractId());
            jsonObject.addProperty("transferId", tourOptionsModel.getSelectedTransferId());
            jsonObject.addProperty("date", tourOptionsModel.getTourOptionSelectDate());
            jsonObject.addProperty("noOfAdult",tourOptionsModel.getTmpAdultValue());
            jsonObject.addProperty("noOfChild", tourOptionsModel.getTmpChildValue());
            jsonObject.addProperty("noOfInfant", tourOptionsModel.getTmpInfantValue());
        }else {
            jsonObject.addProperty("tourId", RaynaTicketManager.shared.getTourId());
            jsonObject.addProperty("tourOptionId", RaynaTicketManager.shared.tourOptionsModel.getTourOptionId());
            jsonObject.addProperty("contractId", RaynaTicketManager.shared.getContractId());
            jsonObject.addProperty("transferId", RaynaTicketManager.shared.tourOptionsModel.getTransferId());
            jsonObject.addProperty("date", Utils.changeDateFormat(RaynaTicketManager.shared.object.get(AppConstants.DATE).getAsString(), "EEE, dd MMM yyyy", "yyyy-MM-dd"));
            jsonObject.addProperty("noOfAdult", getIntOrDefault(AppConstants.ADULTS));
            jsonObject.addProperty("noOfChild", getIntOrDefault(AppConstants.CHILD));
            jsonObject.addProperty("noOfInfant", getIntOrDefault(AppConstants.INFANT));
        }



        requestRaynaTourTimeSlot(jsonObject);

    }

    private void setListener() {

        binding.ivClose.setOnClickListener(view -> dismiss());

        binding.cancelButton.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            dismiss();
        });


        binding.nextButton.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (selectedSlotPosition == -1) return;
            if (callback != null){
                callback.onReceive(raynaTimeSlotModel);
            }
            dismiss();
        });


    }

    private int getLayoutRes() {
        return R.layout.fragment_rayna_time_slot_bottom_shet;
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

    private int getIntOrDefault(String key) {
        JsonElement element = RaynaTicketManager.shared.object.get(key);
        if (element == null || element.isJsonNull() || !element.isJsonPrimitive()) {
            return 0;
        }
        return element.getAsInt();
    }


    private void updateButtonColor() {
        int color = ContextCompat.getColor(requireActivity(), selectedSlotPosition != -1 ? R.color.brand_pink : R.color.gray);
        binding.nextButton.setBackgroundColor(color);
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestRaynaTourTimeSlot(JsonObject jsonObject) {
        binding.progressView.setVisibility(View.VISIBLE);
        DataService.shared(requireActivity()).requestRaynaTourTimeSlot(jsonObject, new RestCallback<ContainerListModel<RaynaTimeSlotModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaTimeSlotModel> model, String error) {
                binding.progressView.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    Utils.hideViews(binding.raynaTimeSlotRecyclerView);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    return;
                }

                List<RaynaTimeSlotModel> timeSlotList = new ArrayList<>();
                if (model.data !=null && !model.data.isEmpty()){
                    timeSlotList.addAll(model.data);
                    timeSlotList.removeIf(q -> q.getAvailable() == 0);
                }

                if (!timeSlotList.isEmpty()) {
                    slotAdapter.updateData(timeSlotList);
                    Utils.showViews(binding.raynaTimeSlotRecyclerView);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                }else {
                    Utils.hideViews(binding.raynaTimeSlotRecyclerView);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class SlotAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_rayna_time_slot));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            RaynaTimeSlotModel model = (RaynaTimeSlotModel) getItem(position);
            viewHolder.binding.tvTime.setText(model.getTimeSlot());
            viewHolder.binding.tvSlotLeft.setText(model.getAvailable() + " slot left");

            viewHolder.binding.timeSlotConstraints.setBackground( getResources().getDrawable(position == selectedSlotPosition ? R.drawable.rayna_selected_time_slot_bg : R.drawable.rayna_slot_border_line));



            viewHolder.itemView.setOnClickListener(v -> {
                 Utils.preventDoubleClick(v);
                 raynaTimeSlotModel = model;
                 selectedSlotPosition = position;
                 notifyDataSetChanged();
                 updateButtonColor();
            });
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemRaynaTimeSlotBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemRaynaTimeSlotBinding.bind(itemView);
            }
        }
    }



    // endregion
    // --------------------------------------



}