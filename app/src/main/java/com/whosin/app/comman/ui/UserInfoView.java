package com.whosin.app.comman.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.asynclayoutinflater.view.AsyncLayoutInflater;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.LayoutUserInfoItemBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.HomeObjectModel;
import com.whosin.app.service.models.StorySeenEventModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.ui.activites.Story.StoryViewActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.stream.Collectors;


public class UserInfoView extends LinearLayout {

    private LayoutUserInfoItemBinding binding;
    public boolean isOpenVenue = true;
    public boolean isOpenStory = true;
    private Context context;

    private VenueObjectModel venueObjectModel;
    private CommanCallback<Boolean> callback;

    public UserInfoView(Context context) {
        this(context, null);
    }

    public UserInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UserInfoViewStyle, 0, 0);
        boolean isAsyncInflate = a.getBoolean(R.styleable.UserInfoViewStyle_isAsyncInflate, true);
        if (!isAsyncInflate) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_user_info_item, this);
            binding = LayoutUserInfoItemBinding.bind(view);
        } else {
            LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);
            AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
            asyncLayoutInflater.inflate(R.layout.layout_user_info_item, this, (view, resid, parent) -> {
                binding = LayoutUserInfoItemBinding.bind(view);
                if (venueObjectModel != null) {
                    setupData();
                }
                UserInfoView.this.removeAllViews();
                UserInfoView.this.addView(view);
            });
        }
    }

    private boolean hasStory(VenueObjectModel venueObjectModel) {
        if (!venueObjectModel.getStories().isEmpty()) {
            return true;
        }
        HomeObjectModel homeObjectModel = SessionManager.shared.geHomeBlockData();
        if (homeObjectModel != null) {
            return homeObjectModel.getStories().stream().anyMatch(p -> p.getId().equals(venueObjectModel.getId()));
        }
        return false;
    }

    private void openStory(VenueObjectModel venueObjectModel) {
        if (isOpenStory) {
            HomeObjectModel homeObjectModel = SessionManager.shared.geHomeBlockData();
            List<VenueObjectModel> matchingStories = homeObjectModel.getStories().stream().filter(model1 -> venueObjectModel.getId().equals(model1.getId())).collect(Collectors.toList());
            if (!matchingStories.isEmpty()) {
                Intent intent = new Intent(context, StoryViewActivity.class);
                intent.putExtra("stories", new Gson().toJson(matchingStories));
                intent.putExtra("selectedPosition", 0);
                context.startActivity(intent);
            }
        }
    }

    private void setStoryOnLogo(VenueObjectModel venueObjectModel) {
        Thread backgroundThread = new Thread(() -> {
            if (hasStory(venueObjectModel)) {
                AppExecutors.get().mainThread().execute(() -> Graphics.setStoryRing(venueObjectModel.getId(), binding.roundLinear));
            } else {
                AppExecutors.get().mainThread().execute(() -> binding.roundLinear.setBackground(null));
            }
        });
        backgroundThread.start();
    }

    private void setupData() {
        if (binding == null) { return; }
        if (venueObjectModel == null) return;
        Graphics.loadRoundImage(venueObjectModel.getSmallLogo(), binding.image);
        binding.tvTitle.setText(Utils.notNullString(venueObjectModel.getName()));
        if (!TextUtils.isEmpty(venueObjectModel.getAddress())){
            binding.tvAddress.setText(Utils.notNullString(venueObjectModel.getAddress()));
            binding.tvAddress.setVisibility(View.VISIBLE);
        }else {
            binding.tvAddress.setVisibility(GONE);
        }

        if (venueObjectModel.getType().equals("ticket")){
            binding.roundLinear.setBackground(null);
        }
        setStoryOnLogo(venueObjectModel);

        binding.venueTitleContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (venueObjectModel != null && venueObjectModel.getType().equals("ticket")){
                    Intent intent = new Intent(Graphics.context, RaynaTicketDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("ticketId", venueObjectModel.getId());
                    Graphics.context.startActivity(intent);
                }
            }
        });

        binding.roundLinear.setOnClickListener(v -> {
            if (hasStory(venueObjectModel)) {
                openStory(venueObjectModel);
            } else {
                if (venueObjectModel != null && venueObjectModel.getType().equals("ticket")){
                    Intent intent = new Intent(Graphics.context, RaynaTicketDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("ticketId", venueObjectModel.getId());
                    Graphics.context.startActivity(intent);
                }

            }

        });
    }

    public void setVenueDetail(VenueObjectModel venueDetail) {
        if (venueDetail == null) return;
        venueObjectModel = venueDetail;
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (binding == null) { return; }
        setupData();
    }

    public void setVenueDetail(VenueObjectModel venueDetail, CommanCallback<Boolean> callback) {
        if (venueDetail == null) return;
        venueObjectModel = venueDetail;
        this.callback = callback;
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (binding == null) { return; }
        setupData();
    }

    public void setOfferVenueDetail(VenueObjectModel venueDetail) {
        if (venueDetail == null) return;
        Graphics.loadRoundImage(venueDetail.getSmallLogo(), binding.image);
        binding.tvTitle.setText(Utils.notNullString(venueDetail.getName()));
        binding.tvAddress.setText(Utils.notNullString(venueDetail.getAddress()));
        setStoryOnLogo(venueDetail);

        binding.roundLinear.setOnClickListener(v -> {
            if (hasStory(venueDetail)) {
                openStory(venueDetail);
            } else {
                if (venueObjectModel != null && venueObjectModel.getType().equals("ticket")){
                    Intent intent = new Intent(Graphics.context, RaynaTicketDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("ticketId", venueObjectModel.getId());
                    Graphics.context.startActivity(intent);
                }

            }

        });
    }
//    StorySeenEventModel

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StorySeenEventModel model) {
        if (model == null) { return; }
        if (venueObjectModel == null) { return; }
        if (binding == null) { return; }
        if (venueObjectModel.getId().equals(model.venueId)) {
            setStoryOnLogo(venueObjectModel);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }


    public void setTicketDetail(RaynaTicketDetailModel ticketDetail) {
        if (ticketDetail == null) return;
        if (binding == null) { return; }
        binding.tvTitle.setText(ticketDetail.getTitle());


        binding.venueTitleContainer.setOnClickListener(v -> {
            Intent intent = new Intent(Graphics.context, RaynaTicketDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("ticketId", ticketDetail.getId());
            Graphics.context.startActivity(intent);
        });


        String description = ticketDetail.getDescription();
        String text = description != null ? description.replaceAll("<[^>]*>", "").trim() : "";

        if (!TextUtils.isEmpty(text)) {
            binding.tvAddress.setVisibility(View.VISIBLE);
            binding.tvAddress.setText(text);
        } else {
            binding.tvAddress.setVisibility(View.GONE);
        }

        if (ticketDetail.getImages() != null && !ticketDetail.getImages().isEmpty()) {
            for (String image : ticketDetail.getImages()) {
                if (!Utils.isVideo(image)) {
                    Graphics.loadRoundImage(image, binding.image);
                    break;
                }
            }
        }

    }
}
