package com.whosin.app.ui.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.roundcornerlayout.CornerType;
import com.whosin.app.databinding.ItemCmEventsNewDesignBinding;
import com.whosin.app.service.models.InvitedUserModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.ui.activites.Promoter.ComplementaryEventDetailActivity;


public class ComplementaryEventsListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    private Activity activity;

    private boolean addSpaceBetweenEvent = true;

    private String eventType = "";

    public ComplementaryEventsListAdapter(Activity activity) {
        this.activity = activity;
    }

    public ComplementaryEventsListAdapter(Activity activity,String type) {
        this.activity = activity;
        this.eventType = type;
    }

    public ComplementaryEventsListAdapter(Activity activity,boolean addSpaceBetweenEvent) {
        this.activity = activity;
        this.addSpaceBetweenEvent = addSpaceBetweenEvent;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = UiUtils.getViewBy(parent, R.layout.item_cm_events_new_design);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (addSpaceBetweenEvent){
            int itemCount = getItemCount();
            params.width = (int) (Graphics.getScreenWidth(activity) * (itemCount == 1 ? 0.94 : 0.90));
            view.setLayoutParams(params);
        }
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        PromoterEventModel model = (PromoterEventModel) getItem(position);
        if (model == null) return;

        viewHolder.binding.eventCategoriLayout.setVisibility(View.GONE);
        viewHolder.binding.remainingEventTv.setText("  " + Utils.getLangValue("new") + "  ");
        viewHolder.binding.plusOneTv.setText(Utils.getLangValue("plus_one_members") );
        viewHolder.binding.limitedSeatslayout.setText(Utils.getLangValue("event_started_limited_seats") );

        // Check Event Type
        if (model.getVenueType().equals("custom")) {
            if (model.getCustomVenue() != null) {
                Graphics.loadImage(model.getCustomVenue().getImage(), viewHolder.binding.imageVenue);
                Graphics.loadImage(model.getCustomVenue().getImage(), viewHolder.binding.image);
                viewHolder.binding.titleText.setText(model.getCustomVenue().getName());
                viewHolder.binding.subTitleText.setText(model.getCustomVenue().getAddress());
            }
        } else {
            if (model.getVenue() != null) {
                if (!TextUtils.isEmpty(model.getImage())) {
                    Graphics.loadImage(model.getImage(), viewHolder.binding.imageVenue);
                } else {
                    Graphics.loadImage(model.getVenue().getCover(), viewHolder.binding.imageVenue);
                }
                Graphics.loadImage(model.getVenue().getLogo(), viewHolder.binding.image);
                viewHolder.binding.titleText.setText(model.getVenue().getName());
                viewHolder.binding.subTitleText.setText(model.getVenue().getAddress());
            }
        }

        viewHolder.handleBadge(model);

        viewHolder.binding.tvForStatus.setText("");
        viewHolder.binding.eventTimerView.setVisibility(View.GONE);
        viewHolder.binding.tvForStatus.setVisibility(View.VISIBLE);
        viewHolder.binding.forStatusConstraint.setVisibility(View.VISIBLE);

        // Plus One Members
        viewHolder.handlePlusOneMember(model);

        switch (model.getStatus()) {
            case "completed":
                viewHolder.binding.tvForStatus.setText(Utils.getLangValue("event_completed"));
                viewHolder.binding.tvForStatus.setTextColor(ContextCompat.getColor(activity, R.color.green_medium));
                viewHolder.binding.forStatusConstraint.setBackground(null);
                viewHolder.binding.constraint.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.green_medium));
                viewHolder.binding.interestConstraint.setVisibility(View.GONE);
                viewHolder.binding.newEventTv.setVisibility(View.GONE);
                break;
            case "in-progress":
                if (!model.isSpotClosed() && model.getSpotCloseType().equals("manual") && !model.getInvite().getPromoterStatus().equals("accepted") && Utils.isSpotOpen(model.getSpotCloseAt())) {
                    viewHolder.binding.forStatusConstraint.setVisibility(GONE);
                    viewHolder.binding.eventTimerView.setVisibility(View.VISIBLE);
                    viewHolder.binding.eventTimerView.setUpData(model.getDate(), model.getSpotCloseAt());
                } else {
                    viewHolder.binding.tvForStatus.setText(Utils.getLangValue("event_started"));
                    viewHolder.binding.tvForStatus.setTextColor(ContextCompat.getColor(activity, R.color.green_medium));
                    viewHolder.binding.forStatusConstraint.setBackground(null);
                }
                viewHolder.binding.constraint.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.green_medium));
                viewHolder.binding.interestConstraint.setVisibility(View.GONE);
                viewHolder.binding.newEventTv.setVisibility(View.GONE);
                viewHolder.binding.limitedSeatslayout.setVisibility(!model.isSpotClosed() && model.getSpotCloseType().equals("manual") && !model.getInvite().getPromoterStatus().equals("accepted") && Utils.isSpotOpen(model.getSpotCloseAt())
                        ? VISIBLE : GONE);
                break;
            default:
                viewHolder.binding.forStatusConstraint.setVisibility(GONE);
                viewHolder.binding.eventTimerView.setVisibility(View.VISIBLE);
                viewHolder.binding.limitedSeatslayout.setVisibility(GONE);
                viewHolder.binding.eventTimerView.setUpData(model.getDate(), model.getStartTime());
                break;
        }

        viewHolder.binding.txtDate.setText(Utils.changeDateFormat(model.getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_DD_MM_DATE));
        viewHolder.binding.txtTime.setText(model.getStartTime() + " - " + model.getEndTime());

        boolean isEventFull = false;
        if (!model.getInvite().getPromoterStatus().equals("accepted")) {
            isEventFull = ("rejected".equals(model.getInvite().getPromoterStatus()) || model.getStatus().equals("cancelled") || model.isEventFull());
            if (TextUtils.isEmpty(eventType)){
                isEventFull = model.getRemainingSeats() <= 0;
            }
            if (model.isSpotClosed() && !model.getInvite().getInviteStatus().equals("in")) {
                isEventFull = true;
            }

        }

        if (isEventFull) {
            viewHolder.binding.eventTimerView.setVisibility(View.GONE);
            viewHolder.binding.tvForStatus.setVisibility(View.VISIBLE);
            viewHolder.binding.forStatusConstraint.setVisibility(View.VISIBLE);
            String showText = model.isSpotClosed() && !model.getInvite().getInviteStatus().equals("in") ? Utils.getLangValue("sorry_event_is_full") : Utils.getLangValue("event_full");
            viewHolder.binding.tvForStatus.setText(showText);
            viewHolder.binding.tvForStatus.setTextColor(ContextCompat.getColor(activity, R.color.event_full_color));
            viewHolder.binding.forStatusConstraint.setBackground(ContextCompat.getDrawable(activity, R.drawable.event_full_background));
            viewHolder.binding.interestConstraint.setVisibility(View.GONE);
            viewHolder.binding.newEventTv.setVisibility(View.GONE);
            viewHolder.binding.limitedSeatslayout.setVisibility(View.GONE);
            viewHolder.binding.constraint.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.event_full_color));
        }


        // Event Category
        if (!TextUtils.isEmpty(model.getCategory()) && !model.getCategory().equalsIgnoreCase("None")) {
            viewHolder.binding.eventCategori.setText(model.getCategory());
            viewHolder.binding.eventCategoriLayout.setVisibility(View.VISIBLE);
            int marginTop = Utils.getMarginTop(holder.itemView.getContext(), 0.02f);
            setTopMargin(viewHolder.binding.eventCategoriLayout, marginTop);
        } else {
            viewHolder.binding.eventCategoriLayout.setVisibility(View.GONE);
        }

        int defaultMarginTop = Utils.getMarginTop(holder.itemView.getContext(), 0.03f);
        if (viewHolder.binding.txtTime.getVisibility() == View.VISIBLE) {
            setTopMargin(viewHolder.binding.txtTime, defaultMarginTop);
        }
        if (viewHolder.binding.plusOneTv.getVisibility() == View.VISIBLE) {
            setTopMargin(viewHolder.binding.plusOneTv, defaultMarginTop);
        }
        if (viewHolder.binding.plusOneTv.getVisibility() == View.VISIBLE &&
                viewHolder.binding.txtTime.getVisibility() == View.VISIBLE &&
                viewHolder.binding.txtDate.getVisibility() == View.VISIBLE &&
                viewHolder.binding.eventCategoriLayout.getVisibility() == View.VISIBLE) {

            int marginTop = Utils.getMarginTop(holder.itemView.getContext(), 0.01f);
            setTopMargin(viewHolder.binding.plusOneTv, marginTop);
            setTopMargin(viewHolder.binding.txtTime, marginTop);
            setTopMargin(viewHolder.binding.txtDate, 0);
            setTopMargin(viewHolder.binding.eventCategoriLayout, marginTop);
        }

        viewHolder.setListeners(model);

    }

    public static void setTopMargin(View view, int marginTop) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.topMargin = marginTop;
        view.setLayoutParams(layoutParams);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemCmEventsNewDesignBinding binding;

        private PlusOneMemberListAdapter<InvitedUserModel> plusMemberAdapter = new PlusOneMemberListAdapter<>(activity);


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCmEventsNewDesignBinding.bind(itemView);
            binding.memberRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            binding.memberRecyclerView.setAdapter(plusMemberAdapter);

        }

        private void handlePlusOneMember(PromoterEventModel model) {
            if (model.isPlusOneAccepted()) {
                if (model.getPlusOneMembers() != null && !model.getPlusOneMembers().isEmpty()) {
                    binding.plusOneTv.setVisibility(VISIBLE);
                    binding.memberRecyclerView.setVisibility(VISIBLE);
                    plusMemberAdapter.updateData(model.getPlusOneMembers());
                } else {
                    binding.plusOneTv.setVisibility(GONE);
                    binding.memberRecyclerView.setVisibility(GONE);
                }
            } else {
                binding.plusOneTv.setVisibility(GONE);
                binding.memberRecyclerView.setVisibility(GONE);
            }
        }

        private void setListeners(PromoterEventModel model) {

            binding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                String type = !TextUtils.isEmpty(eventType) && eventType.equalsIgnoreCase("PlusOneEvent") ? "PlusOneEvent" : "complementary";
                Intent intent = new Intent(activity, ComplementaryEventDetailActivity.class);
                intent.putExtra("eventId", model.getId());
                intent.putExtra("type", type);
                activity.startActivity(intent);

            });
        }

        private void handleBadge(PromoterEventModel model) {
            boolean isInviteIn = model.getInvite().getInviteStatus().equals("in");
            boolean isPromoterAccepted = model.getInvite().getPromoterStatus().equals("accepted");
            binding.newEventTv.setVisibility(View.GONE);
            if (model.isConfirmationRequired() && isInviteIn) {
                binding.interestConstraint.setVisibility(View.VISIBLE);
                updateInterestConstraint(isPromoterAccepted);
            } else if (isInviteIn && isPromoterAccepted) {
                binding.interestConstraint.setVisibility(View.VISIBLE);
                updateInterestConstraint(true);
            } else {
                binding.interestConstraint.setVisibility(View.GONE);
                binding.constraint.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.brand_pink));
                if (!Utils.isNewEvent(model.getCreatedAt(), model.getCloneId())) {
                    if (model.getRemainingSeats() == 0) {
                        binding.newEventTv.setVisibility(View.GONE);
                    } else {
                        if (model.getRemainingSeats() <= 5) {
                            handleNewEventAndSeatRemainingDisplay(R.color.brand_pink);
                            binding.remainingEventTv.setText(Utils.setLangValue("seats_remaining",String.valueOf(model.getRemainingSeats())));
                        } else {
                            binding.newEventTv.setVisibility(View.GONE);
                        }
                    }
                } else {
                    handleNewEventAndSeatRemainingDisplay(R.color.brand_pink);
                }

            }
        }

        private void updateInterestConstraint(boolean isAccepted) {
            String statusText = isAccepted ? Utils.getLangValue("confirmed") : Utils.getLangValue("pending");
            int colorRes = isAccepted ? R.color.im_in : R.color.amber_color;

            binding.interestedTv.setText(statusText);
            binding.interestConstraint.setBackgroundColor(ContextCompat.getColor(activity, colorRes));
            binding.constraint.setBackgroundTintList(ContextCompat.getColorStateList(activity, colorRes));
        }

        // Helper method to handle new event display logic
        private void handleNewEventAndSeatRemainingDisplay(int colorRes) {
            if (binding.interestConstraint.getVisibility() == View.GONE) {
                float cornerRadius = activity.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._5sdp);
                binding.newEventTv.setCornerRadius(cornerRadius, CornerType.BOTTOM_LEFT);
            }
            binding.newEventTv.setVisibility(View.VISIBLE);
            binding.constraint.setBackgroundTintList(ContextCompat.getColorStateList(activity, colorRes));
            binding.newEventTv.setBackgroundTintList(ContextCompat.getColorStateList(activity, colorRes));
        }

    }

}
