package com.whosin.app.ui.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.DiscountCalculator;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemSubVenueBinding;
import com.whosin.app.service.models.PackageModel;


public class OfferPackagesAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    private String offerId = "";

    private FragmentManager fragmentManager;

    public OfferPackagesAdapter(String id, FragmentManager fragmentManager) {
        this.offerId = id;
        this.fragmentManager = fragmentManager;
    }

    public OfferPackagesAdapter() {}

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_sub_venue));
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        PackageModel model = (PackageModel) getItem(position);
        if (model == null) {
            return;
        }

        viewHolder.mBinding.tvTitle.setText(Utils.notNullString(model.getTitle()));
        Log.d("TAG", "onBindViewHolder: "+model.getDescription());
        if (!model.getDescription().isEmpty()) {
            viewHolder.mBinding.tvDescription.setVisibility(View.VISIBLE);
            viewHolder.mBinding.tvDescription.setText(model.getDescription());
        } else {
            viewHolder.mBinding.tvDescription.setVisibility(View.GONE);
        }


        if (!model.getDiscount().equals("0")) {
            String modifiedString = model.getDiscount().contains("%") ? model.getDiscount() : model.getDiscount() + "%";
            viewHolder.mBinding.tvDiscount.setVisibility(View.VISIBLE);
            viewHolder.mBinding.tvDiscount.setText(Utils.notNullString(modifiedString));
        } else {
            viewHolder.mBinding.tvDiscount.setVisibility(View.GONE);
        }

//        viewHolder.mBinding.tvDiscountPrice.setText("AED " + DiscountCalculator.calculateDiscount(model.getDiscount(),model.getAmount()));

        Utils.setStyledText(Graphics.context,viewHolder.mBinding.tvDiscountPrice,String.valueOf(DiscountCalculator.calculateDiscount(model.getDiscount(),model.getAmount())));




        viewHolder.mBinding.getRoot().setOnClickListener(view -> {
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemSubVenueBinding mBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemSubVenueBinding.bind(itemView);
        }
    }
}

