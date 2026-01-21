package com.whosin.app.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.HomeBlockYachtViewBinding;
import com.whosin.app.databinding.ItemYachtComponentBinding;
import com.whosin.app.service.models.YachtDetailModel;
import com.whosin.app.ui.activites.yacht.YachtClubDetailActivity;
import com.whosin.app.ui.activites.yacht.YachtOfferDetailActivity;
import com.whosin.app.ui.activites.venue.VenueShareActivity;


import java.util.ArrayList;
import java.util.List;

public class HomeBlockYachtView extends ConstraintLayout {
    private HomeBlockYachtViewBinding binding;
    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;
    private List<YachtDetailModel> yachtDetailModelList;
    private HomeBlockYachtAdapter<YachtDetailModel> yachtAdapter;

    private boolean isYachtOffer;

    public HomeBlockYachtView(Context context) {
        this(context, null);
    }

    public HomeBlockYachtView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeBlockYachtView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.home_block_yacht_view, this, (view, resid, parent) -> {
            binding = HomeBlockYachtViewBinding.bind(view);
            setupRecycleHorizontalManager(binding.yachtRecycler);
            yachtAdapter = new HomeBlockYachtAdapter<>(activity, supportFragmentManager);
            binding.yachtRecycler.setAdapter(yachtAdapter);
            if (yachtDetailModelList != null) {
                activity.runOnUiThread(() -> yachtAdapter.updateData(yachtDetailModelList));
            }
            HomeBlockYachtView.this.removeAllViews();
            HomeBlockYachtView.this.addView(view);
        });
    }

    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
    }

    public void setupData(List<YachtDetailModel> yacht, Activity activity, FragmentManager fragmentManager, boolean isYacht) {
        this.yachtDetailModelList = yacht;
        this.activity = activity;
        this.supportFragmentManager = fragmentManager;
        this.isYachtOffer = isYacht;


        if (yachtDetailModelList == null) {
            return;
        }
        if (binding == null) {
            return;
        }
        activity.runOnUiThread(() -> {
            yachtAdapter.updateData(yachtDetailModelList);

        });

    }


    public class HomeBlockYachtAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        private final Activity activity;
        private final FragmentManager fragmentManager;

        public HomeBlockYachtAdapter(Activity activity, FragmentManager fragmentManager) {
            this.activity = activity;
            this.fragmentManager = fragmentManager;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_yacht_component);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (Graphics.getScreenWidth(activity) * (getItemCount() > 1 ? 0.89 : 0.93));
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (activity == null) {
                return;
            }
            ViewHolder viewHolder = (ViewHolder) holder;
            YachtDetailModel model = (YachtDetailModel) getItem(position);
            if (model != null) {
                activity.runOnUiThread(() -> {
                    Graphics.loadImage(model.getYachtClub().getCover(), viewHolder.mBinding.cover);
                    Graphics.loadRoundImage(model.getYachtClub().getLogo(), viewHolder.mBinding.image);
                    viewHolder.mBinding.tvTitle.setText(model.getYachtClub().getName());
                    viewHolder.mBinding.tvAddress.setText(model.getYachtClub().getAddress());
                    viewHolder.mBinding.tvName.setText(model.getName());
                    viewHolder.mBinding.tvSubTitle.setText(model.getAbout());

                    viewHolder.mBinding.featureRecycler.setupData("", model.getFeatures(), activity, fragmentManager, StaggeredGridLayoutManager.HORIZONTAL, 2);
                });
                viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                    if (isYachtOffer) {
                        if (model.getYachtOfferId().isEmpty()){return;}
                        activity.startActivity(new Intent(activity, YachtOfferDetailActivity.class).putExtra("yachtOfferId",model.getYachtOfferId()));
                    }
                });



                viewHolder.mBinding.ivMenu.setOnClickListener( view -> {
                    Utils.preventDoubleClick( view );
                    ArrayList<String> data = new ArrayList<>();
                    data.add("Share");
                    Graphics.showActionSheet(activity, "WhosIn", data, (data1, position1) -> {
                        switch (position1) {
                            case 0:
                                Intent intent = new Intent(activity, VenueShareActivity.class);
                                intent.putExtra( "yacht",new Gson().toJson( model.getYachtClub()) );
                                intent.putExtra( "type","yacht" );
                                activity.startActivity(intent);
                                break;

                        }
                    });
                } );

                viewHolder.mBinding.venueTitleContainer.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    activity.startActivity(new Intent(activity, YachtClubDetailActivity.class).putExtra("yachtClubId", model.getYachtClub().getId()));
                });
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemYachtComponentBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemYachtComponentBinding.bind(itemView);
            }
        }
    }
}
