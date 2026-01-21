package com.whosin.app.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemTestBinding;
import com.whosin.app.service.models.ExploreModel;
import com.whosin.app.ui.fragment.ExploreFragment;

public class TestAdapter <T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_test));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        ExploreModel model = (ExploreModel) getItem(position);
        if (model.getBlockType() == AppConstants.ExploreResultType.OFFER) {
            Graphics.loadImage(model.getOffer().getImage(), viewHolder.mBinding.imageView);
        } else if (model.getBlockType() == AppConstants.ExploreResultType.EVENT) {
            Graphics.loadImage(model.getEvent().getImage(), viewHolder.mBinding.imageView);
        } else if (model.getBlockType() == AppConstants.ExploreResultType.ACTIVITY) {
            Graphics.loadImage(model.getActivity().getCoverImage(), viewHolder.mBinding.imageView);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemTestBinding mBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemTestBinding.bind(itemView);
        }

    }
}
