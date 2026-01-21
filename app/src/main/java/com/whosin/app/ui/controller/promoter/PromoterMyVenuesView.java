package com.whosin.app.ui.controller.promoter;


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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemMyVenueViewBinding;
import com.whosin.app.databinding.LayoutMyVenueViewBinding;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.ui.activites.Promoter.PromoterVenuesActivity;
import com.whosin.app.ui.activites.venue.VenueActivity;

import java.util.List;

public class PromoterMyVenuesView extends ConstraintLayout {
    private LayoutMyVenueViewBinding binding;
    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;
    private List<VenueObjectModel> venueList;
    private PromoterMyVenueAdapter<VenueObjectModel> promoterMyVenueAdapter ;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public PromoterMyVenuesView(Context context) {
        this(context, null);
    }

    public PromoterMyVenuesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PromoterMyVenuesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.layout_my_venue_view, this, (view, resid, parent) -> {

            binding = LayoutMyVenueViewBinding.bind(view);

            binding.tvTitle.setText(Utils.getLangValue("my_venues"));
            binding.seeAll.setText(Utils.getLangValue("see_all"));
            binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(Utils.getLangValue("my_venues_looking_bit_empty"));

            promoterMyVenueAdapter = new PromoterMyVenueAdapter<>();

            binding.myCircalRecycler.setLayoutManager(new GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false));

            binding.myCircalRecycler.setAdapter(promoterMyVenueAdapter);
            if (venueList != null && !venueList.isEmpty()) {
                activity.runOnUiThread(() -> promoterMyVenueAdapter.updateData(venueList));
            }


            if (venueList != null && !venueList.isEmpty()) {
                binding.tvVenueCount.setText(String.format("(%s", venueList.size() + ")"));
            }else {
                binding.tvVenueCount.setText(String.format("(%s", "0" + ")"));
            }


            binding.roundLinear.setOnClickListener(v -> {
                activity.startActivity(new Intent(activity, PromoterVenuesActivity.class));
            });

            if(venueList != null){
                setUpData( venueList,activity,supportFragmentManager );
            }
            PromoterMyVenuesView.this.removeAllViews();
            PromoterMyVenuesView.this.addView(view);

        });


    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------


    public void setUpData(List<VenueObjectModel> list, Activity activity, FragmentManager supportFragmentManager) {
        this.venueList = list;
        this.activity = activity;
        this.supportFragmentManager = supportFragmentManager;


        if (binding == null) {
            return;
        }

        if(venueList != null && !venueList.isEmpty()){
            promoterMyVenueAdapter.updateData(venueList);
            binding.myCircalRecycler.setVisibility(View.VISIBLE);
            binding.tvVenueCount.setText(String.format("(%s", venueList.size() + ")"));
            binding.emptyPlaceHolderView.setVisibility(View.GONE);

        }else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            binding.roundLinear.setVisibility(View.GONE);
            binding.myCircalRecycler.setVisibility(View.GONE);
        }
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class PromoterMyVenueAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_my_venue_view));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            VenueObjectModel model = (VenueObjectModel) getItem(position);
            Graphics.loadImageWithFirstLetter(model.getLogo(), viewHolder.mBinding.image, model.getName());
            viewHolder.mBinding.tvName.setText(model.getName());

            viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                activity.startActivity(new Intent(activity, VenueActivity.class).putExtra("venueId", model.getId()));

            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemMyVenueViewBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemMyVenueViewBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------
}
