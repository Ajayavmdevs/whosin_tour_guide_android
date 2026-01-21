package com.whosin.app.ui.activites.JpHotel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityRaynaTicketTourOptionBinding;
import com.whosin.app.databinding.ItemJpHotelRoomListBinding;
import com.whosin.app.databinding.ItemJpSubHotelRoomBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.JPTicketManager;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.JuniperHotelModels.JPHotelTourAvailabilityModel;
import com.whosin.app.service.models.JuniperHotelModels.JpHotelBookingRuleModel;
import com.whosin.app.service.models.JuniperHotelModels.JpHotelOptionsModel;
import com.whosin.app.service.models.JuniperHotelModels.JpHotelRoomModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.auth.AuthenticationActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.ReadMoreBottomSheet;

import java.util.Objects;

public class JPHotelListActivity extends BaseActivity {

    private ActivityRaynaTicketTourOptionBinding binding;

    private final HotelListAdapter<JpHotelOptionsModel> hotelListAdapter = new HotelListAdapter<>();

    private final JPTicketManager jpTicketManager = JPTicketManager.shared;

    private JPHotelTourAvailabilityModel jpHotelTourAvailabilityModel = null;

    private int selectedPosition = -1;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        binding.constraintHeader.tvTitle.setText(getValue("room_options"));
        binding.tvNext.setText(getValue("next"));

        jpTicketManager.activityList.add(activity);

        ((SimpleItemAnimator) Objects.requireNonNull(binding.tourOptionRecyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        binding.tourOptionRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.tourOptionRecyclerView.setAdapter(hotelListAdapter);

        requestJpHotelAvailability();

        updateButtonColor();

    }

    @Override
    protected void setListeners() {

        binding.constraintHeader.ivClose.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            finish();
        });

        binding.nextButton.setOnClickListener(view -> requestJpHotelBookingRule());

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
    protected void onDestroy() {
        super.onDestroy();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void updateButtonColor() {
        int color = ContextCompat.getColor(activity, selectedPosition != -1  ? R.color.brand_pink : R.color.gray);
        binding.nextButton.setBackgroundColor(color);
        binding.nextButton.setEnabled(selectedPosition != -1);

        if (selectedPosition != -1 && hotelListAdapter.getData() != null) {
            String nett = hotelListAdapter.getData().get(selectedPosition).getPrice() != null ?
                    hotelListAdapter.getData().get(selectedPosition)
                    .getPrice()
                    .getNett()
                    : null;

            if (!TextUtils.isEmpty(nett)) {
                binding.tvPrice.setVisibility(View.VISIBLE);
                Utils.setStyledText(activity, binding.tvPrice, nett);
                return;
            }
        }
        binding.tvPrice.setVisibility(View.GONE);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        jpTicketManager.activityList.remove(activity);
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestJpHotelAvailability() {
        showProgress();
        DataService.shared(activity).requestJpHotelAvailability(jpTicketManager.getHotelRequestList(), new RestCallback<>(this) {
            @Override
            public void result(ContainerModel<JPHotelTourAvailabilityModel> model, String error) {
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
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), error, aBoolean -> {
                            if (aBoolean){
                                finish();
                            }
                        });
                        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                        binding.tourOptionRecyclerView.setVisibility(View.GONE);
                    }

                    return;
                }

                if (model.getData() != null && !model.getData().getHotelOptions().isEmpty()) {
                    jpHotelTourAvailabilityModel = model.getData();
                    int size = model.getData().getHotelOptions().size();
                    if (size == 1){
                        selectedPosition = 0;
                    }

                    binding.tourOptionRecyclerView.setVisibility(View.VISIBLE);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    hotelListAdapter.updateData(model.getData().getHotelOptions());
                    jpTicketManager.checkTime = model.getData().hotelInfo.getCheckTime();
                    updateButtonColor();
                } else {
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    binding.tourOptionRecyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void requestJpHotelBookingRule() {
        JsonObject jsonObject = new JsonObject();
        if (jpTicketManager.hotelRequestModel != null) {
            jsonObject.addProperty("startDate", jpTicketManager.hotelRequestModel.getStartDate());
            jsonObject.addProperty("endDate", jpTicketManager.hotelRequestModel.getEndDate());
            jsonObject.addProperty("hotelCode", jpTicketManager.hotelRequestModel.getHotelCode());
        }
        if (hotelListAdapter != null && hotelListAdapter.getData() != null && selectedPosition != -1) {
            String ratePlanCode = hotelListAdapter.getData().get(selectedPosition).getRatePlanCode();
            jpTicketManager.priceModel = hotelListAdapter.getData().get(selectedPosition).getPrice();
            jpTicketManager.nonRefundable = hotelListAdapter.getData().get(selectedPosition).getNonRefundable();
            if (!TextUtils.isEmpty(ratePlanCode)) {
                jsonObject.addProperty("ratePlanCode", ratePlanCode);
            }
        }
        showProgress();
        DataService.shared(activity).requestJpHotelBookingRule(jsonObject, new RestCallback<>(this) {
            @Override
            public void result(ContainerModel<JpHotelBookingRuleModel> model, String error) {
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
                    }

                    return;
                }

                if (model.getData() != null){
                    jpTicketManager.jpHotelBookingRuleModel = model.getData();
                    startActivity(new Intent(activity, JPHotelGuestListActivity.class));
                }

            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class HotelListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_jp_hotel_room_list));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            JpHotelOptionsModel model = (JpHotelOptionsModel) getItem(position);

            boolean isLastItem = position == getItemCount() - 1;

            if (model == null) return;

            viewHolder.binding.tvPriceTitle.setText(getValue("price"));

            String image = "";
            if (jpHotelTourAvailabilityModel != null && jpHotelTourAvailabilityModel.getHotelInfo() != null && !jpHotelTourAvailabilityModel.getHotelInfo().getImages().isEmpty()){
                image = jpHotelTourAvailabilityModel.getHotelInfo().getImages().get(0);
            }else {
                if (RaynaTicketManager.shared.raynaTicketDetailModel != null && !RaynaTicketManager.shared.raynaTicketDetailModel.getImages().isEmpty()){
                    image = RaynaTicketManager.shared.raynaTicketDetailModel.getImages().get(0);
                }
            }

            viewHolder.binding.hotelRoomImage.setVisibility(TextUtils.isEmpty(image) ? View.GONE : View.VISIBLE);
            Graphics.loadCoilImage(activity,viewHolder.binding.hotelRoomImage,image);


            if (jpHotelTourAvailabilityModel != null && jpHotelTourAvailabilityModel.getHotelInfo() != null) {
                viewHolder.binding.hotelInfoLinear.setVisibility(View.VISIBLE);
                viewHolder.hideAndShowCheckInOutLayout();
            } else {
                viewHolder.binding.hotelInfoLinear.setVisibility(View.GONE);
                viewHolder.binding.hotelCheckInTimeLayout.setVisibility(View.GONE);
            }


            if (!TextUtils.isEmpty(model.getFullBoardName())) {
                viewHolder.binding.tvHotelBoardName.setVisibility(View.VISIBLE);
                viewHolder.binding.tvHotelBoardName.setText(model.getFullBoardName());
            } else {
                viewHolder.binding.tvHotelBoardName.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(model.getNonRefundable())) {
                boolean isNonRefundable = model.getNonRefundable().equalsIgnoreCase("true");
                viewHolder.binding.btnRefundable.setVisibility(View.VISIBLE);
                viewHolder.binding.btnRefundable.setBackgroundColor(
                        ContextCompat.getColor(activity, isNonRefundable ? R.color.non_refund_bg : R.color.brand_pink)
                );
                viewHolder.binding.tvrefundable.setText(
                        getValue(isNonRefundable ? "non_refundable" : "refundable")
                );
            } else {
                viewHolder.binding.btnRefundable.setVisibility(View.GONE);
            }


            if (model.getPrice() != null && !TextUtils.isEmpty(model.getPrice().getNett())){
                Utils.setStyledText(activity,viewHolder.binding.tvHotelPrice, model.getPrice().getNett());
                viewHolder.binding.tvHotelPrice.setVisibility(View.VISIBLE);
            }else {
                viewHolder.binding.tvHotelPrice.setVisibility(View.GONE);
            }


            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.10f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }

            boolean isSelected = (selectedPosition == position);
            int mainBorder = isSelected ? R.color.ticket_selected_colour : R.color.white_30;
            int insideBg   = isSelected ? R.color.ticket_selected_colour_10 : R.color.hotel_list_bg;

            // Runs on UI thread
            activity.runOnUiThread(() -> {
                viewHolder.binding.constraintRound.setBackgroundColor(ContextCompat.getColor(activity, mainBorder));
                viewHolder.binding.hotelInfoLinear.setBackgroundColor(ContextCompat.getColor(activity, insideBg));
            });


            viewHolder.updateData(model);


            viewHolder.itemView.setOnClickListener(v -> {
                int oldPosition = selectedPosition;
                int newPosition = holder.getBindingAdapterPosition();

                if (newPosition == RecyclerView.NO_POSITION) return;

                if (selectedPosition == newPosition) {
                    selectedPosition = RecyclerView.NO_POSITION;
                    notifyItemChanged(oldPosition);
                } else {
                    selectedPosition = newPosition;
                    if (oldPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(oldPosition);
                    }
                    notifyItemChanged(newPosition);
                }
                updateButtonColor();
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemJpHotelRoomListBinding binding;

            private final HotelRoomListAdapter<JpHotelRoomModel> hotelRoomListAdapter;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemJpHotelRoomListBinding.bind(itemView);
                hotelRoomListAdapter = new HotelRoomListAdapter<>(data -> {
                    int oldPosition = selectedPosition;
                    int newPosition = getBindingAdapterPosition();

                    if (newPosition == RecyclerView.NO_POSITION) return;

                    if (selectedPosition == newPosition) {
                        selectedPosition = RecyclerView.NO_POSITION;
                        notifyItemChanged(oldPosition);
                    } else {
                        selectedPosition = newPosition;
                        if (oldPosition != RecyclerView.NO_POSITION) {
                            notifyItemChanged(oldPosition);
                        }
                        notifyItemChanged(newPosition);
                    }
                    updateButtonColor();
                });
                binding.hotelRoomRecycleView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
                binding.hotelRoomRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                binding.hotelRoomRecycleView.setAdapter(hotelRoomListAdapter);
            }

            public void updateData(JpHotelOptionsModel model) {
                binding.hotelRoomRecycleView.setVisibility(model.getHotelRooms().isEmpty() ? View.GONE : View.VISIBLE);
                hotelRoomListAdapter.updateData(model.getHotelRooms());
            }

            public void hideAndShowCheckInOutLayout() {
                if (jpHotelTourAvailabilityModel.getHotelInfo().getCheckTime() == null){
                    binding.hotelCheckInTimeLayout.setVisibility(View.GONE);
                    return;
                }
                String checkIn = jpHotelTourAvailabilityModel.getHotelInfo().getCheckTime().getCheckIn();
                String checkOut = jpHotelTourAvailabilityModel.getHotelInfo().getCheckTime().getCheckOut();

                boolean hasCheckIn = checkIn != null && !checkIn.trim().isEmpty();
                boolean hasCheckOut = checkOut != null && !checkOut.trim().isEmpty();

                if (hasCheckIn || hasCheckOut) {
                    binding.hotelCheckInTimeLayout.setVisibility(View.VISIBLE);

                    if (hasCheckIn) {
                        String checkInTime = setValue("check_in", jpHotelTourAvailabilityModel.getHotelInfo().getCheckTime().getCheckIn());
                        binding.tvHotelCheckIn.setText(checkInTime);
                        binding.tvHotelCheckIn.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvHotelCheckIn.setVisibility(View.GONE);
                    }

                    if (hasCheckOut) {
                        String checkOutTime = setValue("check_out", jpHotelTourAvailabilityModel.getHotelInfo().getCheckTime().getCheckOut());
                        binding.tvHotelCheckOut.setText(checkOutTime);
                        binding.tvHotelCheckOut.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvHotelCheckOut.setVisibility(View.GONE);
                    }
                } else {
                    binding.hotelCheckInTimeLayout.setVisibility(View.GONE);
                }
            }
        }

    }

    private class HotelRoomListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

         private CommanCallback<Boolean> clickCallBack;

        public HotelRoomListAdapter(CommanCallback<Boolean> callback){
           this.clickCallBack = callback;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_jp_sub_hotel_room));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            JpHotelRoomModel model = (JpHotelRoomModel) getItem(position);

            if (model == null) return;

            if (!TextUtils.isEmpty(model.getFullName())) {
                viewHolder.binding.tvHotelName.setVisibility(View.VISIBLE);
                viewHolder.binding.tvHotelName.setText(model.getFullName());
            } else {
                viewHolder.binding.tvHotelName.setVisibility(View.GONE);
            }

            if (model.getRoomOccupancy() != null && !TextUtils.isEmpty(model.getRoomOccupancy().getMaxOccupancy()) && !model.getRoomOccupancy().getMaxOccupancy().equals("0")) {
                viewHolder.binding.tvHotelType.setVisibility(View.VISIBLE);
                String type = setValue("maxOccupancy",model.getRoomOccupancy().getMaxOccupancy());
                viewHolder.binding.tvHotelType.setText(type);
            } else {
                viewHolder.binding.tvHotelType.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(model.getAvailRooms()) && !model.getAvailRooms().equals("0")) {
                viewHolder.binding.tvHotelAvailable.setVisibility(View.VISIBLE);
                String type = setValue("avail_rooms",model.getAvailRooms());
                viewHolder.binding.tvHotelAvailable.setText(type);
            } else {
                viewHolder.binding.tvHotelAvailable.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(model.getPaxCount())) {
                viewHolder.binding.tvHotelAdultChildCount.setVisibility(View.VISIBLE);
                viewHolder.binding.tvHotelAdultChildCount.setText(model.getPaxCount());
            } else {
                viewHolder.binding.tvHotelAdultChildCount.setVisibility(View.GONE);
            }

            viewHolder.binding.infoIcon.setVisibility(model.getFeatures().isEmpty() ? View.GONE : View.VISIBLE);


            viewHolder.binding.infoIcon.setOnClickListener(v -> {
                JPHotelFeaturesSheet jpHotelFeaturesSheet = new JPHotelFeaturesSheet();
                jpHotelFeaturesSheet.activity = activity;
                jpHotelFeaturesSheet.features.addAll(model.getFeatures());
                jpHotelFeaturesSheet.show(getSupportFragmentManager(),"");
            });


            viewHolder.itemView.setOnClickListener(v -> {
                if (clickCallBack != null){
                    clickCallBack.onReceive(true);
                }
            });


        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemJpSubHotelRoomBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemJpSubHotelRoomBinding.bind(itemView);
            }
        }

    }

    // endregion
    // --------------------------------------

}