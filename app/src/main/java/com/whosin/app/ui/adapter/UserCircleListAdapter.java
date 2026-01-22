package com.whosin.app.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemImageSlideRecyclerBinding;
import com.whosin.app.service.models.PromoterCirclesModel;

public class UserCircleListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    private Activity activity;

    public  UserCircleListAdapter(Activity activity){
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = UiUtils.getViewBy(parent, R.layout.item_image_slide_recycler);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        PromoterCirclesModel model = (PromoterCirclesModel) getItem(position);
        if (model == null) { return; }

        if (position == getItemCount() - 1) {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
            layoutParams.rightMargin = 0;
            viewHolder.itemView.setLayoutParams(layoutParams);
        } else {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
            layoutParams.rightMargin = viewHolder.itemView.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._minus2sdp);
            viewHolder.itemView.setLayoutParams(layoutParams);
        }

        int newSize = (int) activity.getResources().getDimension(R.dimen.home_Cm_margin);
        ViewGroup.LayoutParams params = viewHolder.binding.image.getLayoutParams();
        params.width = newSize;
        params.height = newSize;
        viewHolder.binding.image.setLayoutParams(params);

        Graphics.loadImageWithFirstLetter(model.getAvatar(), viewHolder.binding.image, model.getTitle());
        viewHolder.binding.image.setBorderColor(ContextCompat.getColor(activity, R.color.white_70));

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemImageSlideRecyclerBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemImageSlideRecyclerBinding.bind(itemView);
        }
    }
}

