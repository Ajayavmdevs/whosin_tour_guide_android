package com.whosin.app.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.BooleanResult;
import com.whosin.app.databinding.OfferInfoViewBinding;
import com.whosin.app.databinding.SuggestedUserInfoViewBinding;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.ui.activites.venue.VenueTimingDialog;
import com.whosin.app.ui.adapter.OfferAdapter;
import com.whosin.app.ui.adapter.SuggestedUserAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.stream.Collectors;

public class SuggestedUserView extends ConstraintLayout {

    private SuggestedUserInfoViewBinding binding;
    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;
    private List<UserDetailModel> suggestedUserList;
    private SuggestedUserAdapter<UserDetailModel> suggestedUserAdapter;
    private BooleanResult callBack;

    public SuggestedUserView(Context context) {
        this(context, null);
    }

    public SuggestedUserView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuggestedUserView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.suggested_user_info_view, this, (view, resid, parent) -> {
            binding = SuggestedUserInfoViewBinding.bind(view);
            setupRecycleHorizontalManager(binding.suggestedUserRecycler);
            suggestedUserAdapter = new SuggestedUserAdapter<>(this.activity);
            binding.suggestedUserRecycler.setAdapter(suggestedUserAdapter);
            if (suggestedUserList != null) {
                setupData();
            }
            SuggestedUserView.this.removeAllViews();
            SuggestedUserView.this.addView(view);
        });
    }

    public void setSuggestedUser(List<UserDetailModel> suggestedUserList, Activity activity, FragmentManager supportFragmentManager, BooleanResult callBack){
        if (suggestedUserList == null){return;}
        this.activity = activity;
        if (suggestedUserAdapter != null) {
            suggestedUserAdapter.activity = activity;
        }
        this.supportFragmentManager = supportFragmentManager;
        this.suggestedUserList = suggestedUserList;
        this.callBack = callBack;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (binding == null){return;}
        setupData();
    }

    private void setupData() {
        if (binding == null) { return; }
        if (suggestedUserList != null && !suggestedUserList.isEmpty() ) {
            binding.btnInviteFriend.setVisibility(View.GONE);
            binding.tvInviteFriend.setVisibility(View.GONE);
            suggestedUserAdapter.updateData(suggestedUserList);
        }
        else {
            binding.btnInviteFriend.setVisibility(View.VISIBLE);
            binding.tvInviteFriend.setVisibility(View.VISIBLE);
            binding.btnInviteFriend.setOnClickListener( view -> Utils.openShareDialog( context ));
        }
    }


    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
//        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.offsetChildrenHorizontal(1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UserDetailModel userDetailModel) {
        if (userDetailModel == null) {
            return;}
        suggestedUserList = suggestedUserList.stream().peek(user -> {
                    if (user.getId().equals(userDetailModel.getId())) {
                        user.setFollow(userDetailModel.getFollow());
                    }
                }).collect(Collectors.toList());
        suggestedUserAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

}
