package com.whosin.app.ui.controller.ComplementryProfileViews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.AppDelegate;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemComplementryProfileBinding;
import com.whosin.app.databinding.LayoutCmEventListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.ComplementaryProfileManager;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.EventInOutPenaltyModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Promoter.ComplementaryEventDetailActivity;
import com.whosin.app.ui.activites.PromoterPublic.PromoterPublicProfileActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class CmEventList extends ConstraintLayout {

    private LayoutCmEventListBinding binding;

    private ComplementaryEventAdapter<PromoterEventModel> complementryEventAdapter;

    private Context context;

    private Activity activity;

    public List<PromoterEventModel> eventList;

    public String type = "";


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public CmEventList(Context context) {
        this(context, null);
    }

    public CmEventList(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CmEventList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CmEventList, 0, 0);
        String titleText = a.getString(R.styleable.CmEventList_eventCmTitleText);
        int titleColor = a.getColor(R.styleable.CmEventList_eventCmTitleTextColor, Color.WHITE);
        Boolean isText = a.getBoolean(R.styleable.CmEventList_eventCmTitleTextStyleBold, true);
        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.layout_cm_event_list, this, (view, resid, parent) -> {
            binding = LayoutCmEventListBinding.bind(view);

            complementryEventAdapter = new ComplementaryEventAdapter<>();
            setupRecycleHorizontalManager(binding.eventRecyclerView);
            binding.eventRecyclerView.setAdapter(complementryEventAdapter);

            if (eventList != null) {
                activity.runOnUiThread(() -> complementryEventAdapter.updateData(eventList));
            }
            if (titleText != null && !titleText.isEmpty()) {
                binding.tvEvents.setText(titleText);

            } else {
                binding.tvEvents.setVisibility(GONE);
            }
            if (isText) {
                binding.tvEvents.setTypeface(null, Typeface.BOLD);
            }
            binding.tvEvents.setTextColor(titleColor);

            if (eventList != null && !eventList.isEmpty()) {
                binding.eventRecyclerView.setVisibility(VISIBLE);
                complementryEventAdapter.updateData(eventList);
                binding.emptyPlaceHolderView.setVisibility(GONE);
            } else {
                binding.emptyPlaceHolderView.setVisibility(VISIBLE);
                binding.eventRecyclerView.setVisibility(GONE);
            }

            CmEventList.this.removeAllViews();
            CmEventList.this.addView(view);


        });
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        if (!Utils.isNullOrEmpty(type) && type.equals("myEventFragmentList")) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            recyclerView.offsetChildrenVertical(1);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        }
    }

    public void setUpData(List<PromoterEventModel> list, Activity activity) {

        this.eventList = list;
        this.activity = activity;

        if (binding == null) {
            return;
        }

        if (eventList != null && !eventList.isEmpty()) {
            binding.eventRecyclerView.setVisibility(VISIBLE);
            complementryEventAdapter.updateData(eventList);
            binding.emptyPlaceHolderView.setVisibility(GONE);
        } else {
            binding.emptyPlaceHolderView.setVisibility(VISIBLE);
            binding.eventRecyclerView.setVisibility(GONE);
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private void updateRecycleItem(PromoterEventModel model, String status, boolean fromWishlisted) {
        if (complementryEventAdapter.getData() != null && !complementryEventAdapter.getData().isEmpty()) {
            complementryEventAdapter.getData().forEach(s -> {
                if (s.getId().equals(model.getId())) {
                    if (fromWishlisted) {
                        s.setWishlisted(!model.isWishlisted());
                    } else {
                        s.getInvite().setInviteStatus(status);
                    }
                }
            });
            complementryEventAdapter.notifyDataSetChanged();
            EventBus.getDefault().post(new ComplimentaryProfileModel());
            EventBus.getDefault().post(new NotificationModel());
            if (!Utils.isNullOrEmpty(type) && type.equals("myEventList")) {
                EventBus.getDefault().post(new PromoterEventModel());
            }

        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    public void requestPromoterUpdateInviteStatus(PromoterEventModel promoterEventModel, String inviteStatus, ItemComplementryProfileBinding binding) {
        if (inviteStatus.equals("in")) {
            binding.eventImIn.startProgress();
        } else {
            binding.btnImOut.startProgress();
        }
        DataService.shared(activity).requestPromoterUpdateInviteStatus(promoterEventModel.getInvite().getId(), inviteStatus, new RestCallback<ContainerModel<EventInOutPenaltyModel>>() {
            @Override
            public void result(ContainerModel<EventInOutPenaltyModel> model, String error) {
                if (inviteStatus.equals("in")) {
                    binding.eventImIn.stopProgress();

                }
                else {
                    binding.btnImOut.stopProgress();
                }
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                String titleMsg = promoterEventModel.isConfirmationRequired()
                        ? "Thank you for showing interest in this event"
                        : "Thank you for joining this event";

                String subtitleMsg =  promoterEventModel.isConfirmationRequired()
                        ? "Admin will review your request to join."
                        : "Please check the details and requirements, and be there on time to enjoy the best experience.";

                Alerter.create(activity).setTitle(titleMsg).setText(subtitleMsg).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                updateRecycleItem(promoterEventModel, inviteStatus, false);

//                ComplementaryProfileManager.shared.requestPromoterUpdateInviteStatus(activity);

            }
        });
    }

    public void requestPromoterToggleWishList(PromoterEventModel promoterEventModel, String type, ItemComplementryProfileBinding mBinding) {
        mBinding.btnWishList.startProgress();
        DataService.shared(activity).requestPromoterToggleWishList(promoterEventModel.getId(), type, new RestCallback<ContainerModel<CommonModel>>() {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                mBinding.btnWishList.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (promoterEventModel.isWishlisted()) {
                    Alerter.create(activity).setText("Item remove from the wishlisted").setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                } else {
                    Alerter.create(activity).setText("Item added to wishlisted").setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                }



                complementryEventAdapter.getData().removeIf( p -> p.getId().equals(promoterEventModel.getId()));

                if (complementryEventAdapter.getData() != null && !complementryEventAdapter.getData().isEmpty()) {
                    binding.eventRecyclerView.setVisibility(VISIBLE);
                    complementryEventAdapter.notifyDataSetChanged();
                    binding.emptyPlaceHolderView.setVisibility(GONE);
                } else {
                    binding.emptyPlaceHolderView.setVisibility(VISIBLE);
                    binding.eventRecyclerView.setVisibility(GONE);
                }

                EventBus.getDefault().post(new ComplimentaryProfileModel());
                EventBus.getDefault().post(new NotificationModel());

            }
        });
    }



    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class ComplementaryEventAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_complementry_profile);

            ViewGroup.LayoutParams params = view.getLayoutParams();
            int itemCount = getItemCount();
            if (!type.equals("myEventFragmentList")) {
                if (itemCount > 1) {
                    params.width = (int) (Graphics.getScreenWidth( AppDelegate.activity) * (getItemCount() > 1 ? 0.92 : 0.93));
                } else {
                    params.width = (int) (Graphics.getScreenWidth(activity) * 0.93);
                }
            }
            view.setLayoutParams(params);

            return new ViewHolder(view);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;


            PromoterEventModel model = (PromoterEventModel) getItem(position);
            if (model == null) {
                return;
            }

            viewHolder.binding.btnWishList.setTxtTitle("Remove from list");


            viewHolder.hideAndShowButtons(model);


            boolean isLastItem = getItemCount() - 1 == position;
            if (getItemCount() > 1) {
                if (isLastItem) {
                    int marginBottom = Utils.getMarginRight(holder.itemView.getContext(), 0.03f);
                    Utils.setRightMargin(holder.itemView, marginBottom);
                } else {
                    Utils.setRightMargin(holder.itemView, 0);
                }
            }

            if (model.isConfirmationRequired() && model.getInvite().getInviteStatus().equals("in")) {
                viewHolder.binding.interestConstraint.setVisibility(View.VISIBLE);
                if (model.getInvite().getPromoterStatus().equals("accepted")) {
                    viewHolder.binding.interestedTv.setText("Confirmed");
                    viewHolder.binding.interestConstraint.setBackgroundColor(ContextCompat.getColor(activity, R.color.im_in));
                } else {
                    viewHolder.binding.interestedTv.setText("Pending");
                    viewHolder.binding.interestConstraint.setBackgroundColor(ContextCompat.getColor(activity, R.color.amber_color));
                }
            } else if (model.getInvite().getInviteStatus().equals("in") && model.getInvite().getPromoterStatus().equals("accepted")) {
                viewHolder.binding.interestConstraint.setVisibility(View.VISIBLE);
                viewHolder.binding.interestedTv.setText("   I'M IN   ");
                viewHolder.binding.interestConstraint.setBackgroundColor(ContextCompat.getColor(activity, R.color.im_in));
            } else {
                viewHolder.binding.interestConstraint.setVisibility(View.GONE);
            }


            if (model.getUser() != null) {
                Graphics.loadImageWithFirstLetter(model.getUser().getImage(), viewHolder.binding.imageProfile, model.getUser().getFullName());
                viewHolder.binding.tvUserName.setText(model.getUser().getFullName());
            }

            viewHolder.binding.tvTime.setText(Utils.getTimeAgoForEvent(model.getCreatedAt(), activity));

            if ("custom".equals(model.getVenueType())) {
                viewHolder.updateVenueDetails(viewHolder, model.getCustomVenue().getAddress(), model.getCustomVenue().getName(), model.getCustomVenue().getImage(), model.getCustomVenue().getImage());
            } else if (model.getVenue() != null) {
                viewHolder.updateVenueDetails(viewHolder, model.getVenue().getAddress(), model.getVenue().getName(), model.getVenue().getLogo(), model.getVenue().getCover());
            }


            viewHolder.binding.txtFromDate.setText(Utils.changeDateFormat(model.getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_MM_DATE));
            viewHolder.binding.txtOfferTime.setText(String.format("%s - %s", model.getStartTime(), model.getEndTime()));
//            viewHolder.binding.txtTillDate.setText(String.format("%d Spot(s)", model.getMaxInvitee()));


            viewHolder.binding.offerDescription.setText(model.getDescription());
            viewHolder.binding.offerDescription.post(() -> {
                int lineCount = viewHolder.binding.offerDescription.getLineCount();
                if (lineCount > 2) {
                    Utils.makeTextViewResizable(viewHolder.binding.offerDescription, 3, 3, ".. See More", true);
                }
            });

            viewHolder.setListeners(model);

        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemComplementryProfileBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemComplementryProfileBinding.bind(itemView);
            }

            private void hideAndShowButtons(PromoterEventModel model) {
                AppExecutors.get().mainThread().execute(() -> {
                    binding.txtEventStatus.setVisibility(GONE);
                    String promoterStatus = model.getInvite().getPromoterStatus();
                    String status = model.getStatus();

//                    if (status.equals("in-progress") || status.equals("completed") || status.equals("cancelled")) {
//                        handleSpecialEventStatus(promoterStatus, model);
//                        return;
//                    }
                    if (status.equals("in-progress") || status.equals("completed")) {
                        handleSpecialEventStatus(promoterStatus, model);
                        return;
                    }

                    binding.buttonLinear.setVisibility(VISIBLE);
                    binding.outFavButtonsLayout.setVisibility(VISIBLE);
                    binding.btnImOut.setVisibility(VISIBLE);

                    updateButtonVisibility(model.getInvite().getInviteStatus(), model.isConfirmationRequired(),model);
                });
            }

            @SuppressLint("SetTextI18n")
            private void handleSpecialEventStatus(String promoterStatus, PromoterEventModel model) {
                binding.txtEventStatus.setVisibility(VISIBLE);
                binding.eventImIn.setVisibility(GONE);
                binding.btnImOut.setVisibility(GONE);
                binding.outFavButtonsLayout.setVisibility(GONE);
                binding.interestConstraint.setVisibility(GONE);

                if (model.getStatus().equals("in-progress")) {
                    binding.txtEventStatus.setText("Event has started");
                    binding.txtEventStatus.setTextColor(ContextCompat.getColor(activity, R.color.green_medium));
                }
//                else if (model.getStatus().equals("cancelled")) {
//                    binding.txtEventStatus.setText("Cancelled");
//                    binding.txtEventStatus.setTextColor(ContextCompat.getColor(activity, R.color.delete_red));
//                }
                else if (model.getStatus().equals("completed")) {
                    binding.txtEventStatus.setText("Completed");
                    binding.txtEventStatus.setTextColor(ContextCompat.getColor(activity, R.color.green_medium));
                }
//                else if (model.isEventFull() || promoterStatus.equals("rejected")) {
//                    binding.txtEventStatus.setVisibility(GONE);
//                    binding.eventImIn.setVisibility(VISIBLE);
//                    binding.eventImIn.setBgColor(getContext().getColor(R.color.dark_purple));
//                    binding.eventImIn.setTxtTitle("EVENT IS FULL");
//                    binding.interestConstraint.setVisibility(GONE);
//                    binding.outFavButtonsLayout.setVisibility(VISIBLE);
//
//                }
//                else if (eventExpired1 && !model.getInvite().getInviteStatus().equals("in")) {
//                    binding.txtEventStatus.setText("Event Expired");
//                    binding.txtEventStatus.setTextColor(ContextCompat.getColor(activity, R.color.delete_red));
//                }
            }

            private void updateButtonVisibility(String inviteStatus, boolean isConfirmationRequired , PromoterEventModel model) {
                boolean isPending = inviteStatus.equals("pending");
                boolean isOut = inviteStatus.equals("out");
                boolean isIn = inviteStatus.equals("in");
                boolean isEventFull = model.isEventFull() || "rejected".equals(model.getInvite().getPromoterStatus()) || model.getMaxInvitee() == 0 || model.getStatus().equals("cancelled");


                binding.eventImIn.setVisibility(isPending || isOut || isEventFull ? VISIBLE : GONE);
                binding.btnImOut.setVisibility(isEventFull ? GONE : ((isIn) ? VISIBLE : GONE));
                binding.btnWishList.setTxtTitle("Remove from list");

                String eventFullText = "rejected".equals(model.getInvite().getPromoterStatus()) ? "Sorry, EVENT IS FULL" : "EVENT IS FULL";
                binding.eventImIn.setTxtTitle(isEventFull ? eventFullText : (model.isConfirmationRequired() ? "Interested" : "I'M IN!"));
                String tmpStatus = model.getInvite().getPromoterStatus().equals("accepted") ? "Confirmed" : "Not Interested";
                binding.btnImOut.setTxtTitle(isConfirmationRequired ? tmpStatus : "Confirmed");
                if (binding.btnImOut.getTxtTitle().equals("Confirmed")){
                    binding.btnImOut.setBgColor(activity.getColor(R.color.im_in));
                }

                int imInColor = model.isConfirmationRequired() ? R.color.intrested_color : R.color.im_in;
                int color = isEventFull ? R.color.event_full_color : imInColor;
                binding.eventImIn.setBgColor(activity.getColor(color));

                if (binding.eventImIn.getTxtTitle().equals("Sorry, EVENT IS FULL")) {
                    binding.eventImIn.setPadding(getResources().getDimensionPixelSize(R.dimen.cancel_confermation_txt_padding));
                    binding.eventImIn.setTitleTextSize(getResources().getDimensionPixelSize(R.dimen.cancel_confermation_txt));
                } else {
                    binding.eventImIn.setPadding(getResources().getDimensionPixelSize(R.dimen.not_interested_padding));
                }

                if (isEventFull) {
                    binding.interestConstraint.setVisibility(GONE);
                }



            }

            private void setListeners(PromoterEventModel model) {

                binding.getRoot().setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    Intent intent = new Intent(activity, ComplementaryEventDetailActivity.class);
                    intent.putExtra("eventId", model.getId());
                    intent.putExtra("type", "complementary");
                    activity.startActivity(intent);

                });

                binding.profileLinear.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    activity.startActivity(new Intent(activity,
                            PromoterPublicProfileActivity.class).
                            putExtra("isPromoterProfilePublic", true).putExtra("id", model.getUserId()));

                });

                binding.btnWishList.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    requestPromoterToggleWishList(model, "event", binding);
                });


                binding.btnImOut.setOnClickListener(new OnClickListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onClick(View view) {
                        Utils.preventDoubleClick(view);

                        if (model.getInvite().getInviteStatus().equals("pending")) {
                            requestPromoterUpdateInviteStatus(model, "out", binding);
                            return;
                        }

                        String tmpString = "";
                        try {
                            tmpString = String.valueOf(Utils.stringToDateWithUTCForEvent(model.getInvite().getUpdatedAt(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        if (!TextUtils.isEmpty(tmpString) && Utils.checkUpdateTimeAndShowAlert(tmpString)) {
                            Graphics.showAlertDialogWithOkButton(activity, "Please Wait", "You need to wait a minute before changing your response.");
                        } else {
                            if (!model.isConfirmationRequired() && Utils.isDateToday(model.getDate())) {
                                if (Utils.isWithinDubaiTimeTwoHours(model.getStartTime())) {
                                    String title = (model.getVenueType().equals("venue") && model.getVenue() != null) ? model.getVenue().getName() : (model.getCustomVenue() != null) ? model.getCustomVenue().getName() : null;
                                    Graphics.showAlertDialogWithOkButton(activity, "⚠️ Cancellation Not Allowed", "You cannot cancel your attendance for " + title + " less than 2 hours before it starts. This may lead to your termination from future events.");
                                } else {
                                    requestPromoterUpdateInviteStatus(model, "out", binding);
                                }
                            } else {
                                String title = "Are you sure you want to out from an event?";
                                if (model.isConfirmationRequired() && binding.btnImOut.getTxtTitle().equals("Confirmed")) {
                                    title = "Are you sure you want to cancel your confirmation for this event?";
                                } else if (binding.btnImOut.getTxtTitle().equals("Not Interested")) {
                                    title = "Are you sure you want to mark yourself as not interested in this event?";
                                }

                                Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name),
                                        title,
                                        "Yes", "Cancel", isConfirmed -> {
                                            if (isConfirmed) {
                                                requestPromoterUpdateInviteStatus(model, "out", binding);
                                            }
                                        });

                            }
                        }
                    }
                });

                binding.eventImIn.setOnClickListener(view -> {
                    if (binding.eventImIn.getTxtTitle().equals("I'M IN!") || binding.eventImIn.getTxtTitle().equals("Interested")) {
//                        if (ComplementaryProfileManager.checkEventInDateTime(model.getDate(),model.getStartTime())) {
//                            Graphics.showAlertDialogWithOkButton(activity, activity.getString(R.string.app_name), "You cannot be in different events at the same time");
//                            return;
//                        }

                        if (model.getInvite().getInviteStatus().equals("pending")) {
                            requestPromoterUpdateInviteStatus(model, "in", binding);
                            return;
                        }

                        String tmpString = "";
                        try {
                            tmpString = String.valueOf(Utils.stringToDateWithUTCForEvent(model.getInvite().getUpdatedAt(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        if (!TextUtils.isEmpty(tmpString) && Utils.checkUpdateTimeAndShowAlert(tmpString)) {
                            Graphics.showAlertDialogWithOkButton(activity, "Please Wait", "You need to wait a minute before changing your response.");
                        } else {
                            requestPromoterUpdateInviteStatus(model, "in", binding);
                        }
                    }
                });

            }

            private void updateVenueDetails(ViewHolder viewHolder, String address, String name, String image, String cover) {
                viewHolder.binding.subTitleText.setText(address);
                viewHolder.binding.titleText.setText(name);
                Graphics.loadRoundImage(image, viewHolder.binding.image);
//                Graphics.loadImage(cover, viewHolder.binding.imgOffer);
                binding.imgCardView.setVisibility(GONE);
            }

        }

    }


    // endregion
    // --------------------------------------
}
