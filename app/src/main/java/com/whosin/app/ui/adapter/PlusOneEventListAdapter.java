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
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemCmEventsNewDesignBinding;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.ui.activites.Promoter.ComplementaryEventDetailActivity;

public class PlusOneEventListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    private Activity activity;

    public PlusOneEventListAdapter(Activity activity){
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = UiUtils.getViewBy(parent, R.layout.item_cm_events_new_design);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        int itemCount = getItemCount();
        params.width = (int) (Graphics.getScreenWidth(activity) * (itemCount == 1 ? 0.94 : 0.90));
        view.setLayoutParams(params);
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
        viewHolder.binding.interestConstraint.setVisibility(GONE);
        viewHolder.binding.newEventTv.setVisibility(View.GONE);
        viewHolder.binding.memberRecyclerView.setVisibility(View.GONE);
        viewHolder.binding.plusOneTv.setVisibility(View.GONE);
        viewHolder.binding.constraint.setBackgroundTintList(ContextCompat.getColorStateList(activity,  R.color.brand_pink));

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

        switch (model.getStatus()) {
            case "completed":
                viewHolder.binding.tvForStatus.setText("Event Completed");
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
                    viewHolder.binding.tvForStatus.setText("Event has started");
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
            if (model.isSpotClosed() && !model.getInvite().getInviteStatus().equals("in")) {
                isEventFull = true;
            }

        }

        if (isEventFull) {
            viewHolder.binding.eventTimerView.setVisibility(View.GONE);
            viewHolder.binding.tvForStatus.setVisibility(View.VISIBLE);
            viewHolder.binding.forStatusConstraint.setVisibility(View.VISIBLE);
            String showText = model.isSpotClosed() && !model.getInvite().getInviteStatus().equals("in") ? "Sorry, Event is Full" : "Event is Full";
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

    public void setTopMargin(View view, int marginTop) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.topMargin = marginTop;
        view.setLayoutParams(layoutParams);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemCmEventsNewDesignBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCmEventsNewDesignBinding.bind(itemView);
        }

        private void setListeners(PromoterEventModel model) {

            binding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                Intent intent = new Intent(activity, ComplementaryEventDetailActivity.class);
                intent.putExtra("eventId", model.getId());
                intent.putExtra("type", "PlusOneEvent");
                activity.startActivity(intent);

            });
        }

        private void handleBadge(PromoterEventModel model) {
            int colorResId;
            boolean isInviteIn = "in".equals(model.getInvite().getInviteStatus());
            boolean isPromoterAccepted = "accepted".equals(model.getInvite().getPromoterStatus());

            if (isInviteIn && isPromoterAccepted) {
                colorResId = R.color.im_in;
            } else if (isInviteIn) {
                colorResId = R.color.amber_color;
            } else {
                colorResId = R.color.brand_pink;
            }

            binding.constraint.setBackgroundTintList(ContextCompat.getColorStateList(activity, colorResId));
        }


    }

}
