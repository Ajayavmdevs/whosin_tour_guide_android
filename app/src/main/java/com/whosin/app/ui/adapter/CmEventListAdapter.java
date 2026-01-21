package com.whosin.app.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
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
import com.whosin.app.comman.ui.roundcornerlayout.CornerType;
import com.whosin.app.databinding.ItemCmEventIminDesignBinding;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.ui.activites.Promoter.ComplementaryEventDetailActivity;
import com.whosin.app.ui.activites.PromoterPublic.PromoterPublicProfileActivity;

public class CmEventListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    private final Activity activity;

    private String type = "";

    public CmEventListAdapter(Activity activity, String type) {
        this.activity = activity;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = UiUtils.getViewBy(parent, R.layout.item_cm_event_imin_design);
        if (!Utils.isNullOrEmpty(type) && (type.equals("profileEventIn") || type.equals("Promoter-events"))) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int itemCount = getItemCount();
            if (itemCount > 1) {
                params.width = (int) (Graphics.getScreenWidth(activity) * 0.80);
            } else {
                params.width = (int) (Graphics.getScreenWidth(activity) * 0.94);
            }
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
        boolean isLastItem = position == getItemCount() - 1;
        if (model == null) {
            return;
        }
        if (model.getUser() != null) {
            Graphics.loadImageWithFirstLetter(model.getUser().getImage(), viewHolder.binding.imageProfile, model.getUser().getFullName());
            viewHolder.binding.tvUserName.setText(model.getUser().getFullName());
        }


        viewHolder.binding.tvTime.setText(Utils.getTimeAgoForEvent(model.getCreatedAt(), activity));

        if (model.getVenueType().equals("custom")) {
            if (model.getCustomVenue() != null) {
                Graphics.loadImage(model.getCustomVenue().getImage(), viewHolder.binding.imageVenue);
                Graphics.loadImage(model.getCustomVenue().getImage(), viewHolder.binding.image);
                viewHolder.binding.titleText.setText(model.getCustomVenue().getName());
                viewHolder.binding.subTitleText.setText(model.getCustomVenue().getAddress());
                Log.d("IMAGE","TITLE" + model.getCustomVenue().getName());
            }
        } else {
            if (model.getVenue() != null) {
                Graphics.loadImage(model.getVenue().getCover(), viewHolder.binding.imageVenue);
                Graphics.loadImage(model.getVenue().getLogo(), viewHolder.binding.image);
                viewHolder.binding.titleText.setText(model.getVenue().getName());
                viewHolder.binding.subTitleText.setText(model.getVenue().getAddress());
                Log.d("IMAGE","TITLE1" + model.getVenue().getName());
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

        if (Utils.isWithinSevenHours(model.getCreatedAt())) {
            if (viewHolder.binding.interestConstraint.getVisibility() == View.GONE) {
                float cornerRadius = activity.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._5sdp);
                viewHolder.binding.newEventTv.setCornerRadius(cornerRadius, CornerType.BOTTOM_LEFT);
            }
            viewHolder.binding.newEventTv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.binding.newEventTv.setVisibility(View.GONE);
        }

        viewHolder.binding.tvForStatus.setText("");
        viewHolder.binding.timeTv.setText("");
        viewHolder.binding.tvForStatus.setTextColor(ContextCompat.getColor(activity, R.color.white));
        viewHolder.binding.timeTv.setVisibility(View.GONE);
        viewHolder.binding.tvForStatus.setVisibility(View.VISIBLE);


        switch (model.getStatus()) {
//            case "cancelled":
//                viewHolder.binding.tvForStatus.setText("Cancelled");
//                viewHolder.binding.tvForStatus.setTextColor(ContextCompat.getColor(activity, R.color.delete_red));
//                break;
            case "completed":
                viewHolder.binding.tvForStatus.setText("Completed");
                viewHolder.binding.tvForStatus.setTextColor(ContextCompat.getColor(activity, R.color.green_medium));
                break;
            case "in-progress":
                viewHolder.binding.tvForStatus.setText("Event has started");
                viewHolder.binding.tvForStatus.setTextColor(ContextCompat.getColor(activity, R.color.green_medium));
                viewHolder.binding.interestConstraint.setVisibility(View.GONE);
                break;
            default:
                viewHolder.binding.timeTv.setVisibility(View.VISIBLE);
                viewHolder.binding.tvForStatus.setVisibility(View.GONE);
                activity.runOnUiThread(() -> Utils.setTimerForDubaiEvent(activity, model.getDate(), model.getStartTime(), viewHolder.binding.timeTv, R.color.white, viewHolder.binding.tvForStatus, model.getInvite().getInviteStatus()));
                break;
        }

        viewHolder.binding.txtDate.setText(Utils.changeDateFormat(model.getDate(), AppConstants.DATEFORMAT_SHORT, "EEE\ndd\nMMM"));
        viewHolder.binding.txtStartTime.setText(String.format(" %s", model.getStartTime()));
        viewHolder.binding.txtendTime.setText(String.format(" %s", model.getEndTime()));

        int count = model.getMaxInvitee() - model.getTotalInMembers();
        if (count < 5 && "in".equals(model.getInvite().getInviteStatus())) {
            viewHolder.binding.remainingSeatsLayout.setVisibility(View.VISIBLE);
            viewHolder.binding.remainingSeatsTv.setText(count == 0 ? "No Seats Remaining" : count + " Seats Remaining");
        } else {
            viewHolder.binding.remainingSeatsLayout.setVisibility(View.GONE);
        }

        if (model.getStatus().equals("cancelled")  || model.getInvite().getPromoterStatus().equals("rejected")) {
            viewHolder.binding.timeTv.setVisibility(View.GONE);
            viewHolder.binding.tvForStatus.setVisibility(View.VISIBLE);
            viewHolder.binding.tvForStatus.setText("EVENT IS FULL");
            viewHolder.binding.tvForStatus.setTextColor(ContextCompat.getColor(activity, R.color.event_full_color));
            viewHolder.binding.interestConstraint.setVisibility(View.GONE);
            viewHolder.binding.remainingSeatsLayout.setVisibility(View.GONE);
        }


        viewHolder.setListeners(model);


        if (!Utils.isNullOrEmpty(type) && (type.equals("profileEventIn") || type.equals("Promoter-events"))) {
            if (getItemCount() > 1) {
                if (isLastItem) {
                    int marginBottom = Utils.getMarginRight(holder.itemView.getContext(), 0.03f);
                    Utils.setRightMargin(holder.itemView, marginBottom);
                } else {
                    Utils.setRightMargin(holder.itemView, 0);
                }
            }
        } else {
            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.12f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemCmEventIminDesignBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemCmEventIminDesignBinding.bind(itemView);
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
                activity.startActivity(new Intent(activity,
                        PromoterPublicProfileActivity.class).
                        putExtra("isPromoterProfilePublic", true).putExtra("id", model.getUserId()));

            });


        }

    }

}
