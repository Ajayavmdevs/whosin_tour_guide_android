package com.whosin.app.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.BooleanResult;
import com.whosin.app.databinding.VenueSuggestionInfoViewBinding;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.ui.adapter.VenueSuggestionAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.stream.Collectors;

public class VenueSuggestionView extends ConstraintLayout {

    private VenueSuggestionInfoViewBinding binding;

    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;
    private List<VenueObjectModel> venueObjectModelList;
    private BooleanResult callBack;
    private VenueSuggestionAdapter<VenueObjectModel> venueSuggestionAdapter;

    public VenueSuggestionView(Context context) {
        this(context, null);
    }

    public VenueSuggestionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VenueSuggestionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.venue_suggestion_info_view, this, (view, resid, parent) -> {
            binding = VenueSuggestionInfoViewBinding.bind(view);
            setupRecycleHorizontalManager(binding.venueSuggestionRecycler);
            venueSuggestionAdapter = new VenueSuggestionAdapter<>(activity, supportFragmentManager);
            binding.venueSuggestionRecycler.setAdapter(venueSuggestionAdapter);
            if (venueObjectModelList != null) {
                setupData();
            }
            VenueSuggestionView.this.removeAllViews();
            VenueSuggestionView.this.addView(view);
        });
    }

    public void setSuggestedVenue(List<VenueObjectModel> venueObjectModelList, Activity activity, FragmentManager supportFragmentManager, BooleanResult callBack){
        if (venueObjectModelList == null){return;}
        this.activity = activity;
        this.supportFragmentManager = supportFragmentManager;
        this.venueObjectModelList = venueObjectModelList;
        this.callBack = callBack;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (binding == null){return;}
        setupData();
    }

    private void setupData() {
        if (venueObjectModelList == null){return;}
        if (!venueObjectModelList.isEmpty() && venueObjectModelList != null) {
            venueSuggestionAdapter.updateData(venueObjectModelList);
        } else {
            binding.btnInviteFriend.setOnClickListener( view -> Utils.openShareDialog( context ));
        }
    }

    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
//        recyclerView.setNestedScrollingEnabled(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(VenueObjectModel event) {
        if (event == null) {
            return;}
        venueObjectModelList = venueObjectModelList.stream()
                .peek(venue -> {
                    if (venue.getId().equals(event.getId())) {
                        venue.setIsFollowing(event.isIsFollowing());
                    }
                }).collect(Collectors.toList());
        venueSuggestionAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }
}
