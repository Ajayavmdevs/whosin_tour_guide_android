package com.whosin.business.ui.adapter.raynaTicketAdapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.ItemRaynaTicketsFeaturesBinding;
import com.whosin.business.service.models.YachtFeatureModel;


public class RaynaTicketFeaturesAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    private final Activity activity;

    public RaynaTicketFeaturesAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_rayna_tickets_features));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        YachtFeatureModel model = (YachtFeatureModel) getItem(position);
        if (model == null) return;

        viewHolder.binding.iconText.setText(model.getFeature());

        if (model.getIcon() != null && !model.getIcon().isEmpty()) {
            viewHolder.binding.ticketsIcon.setVisibility(View.VISIBLE);
            Graphics.loadRoundImage(model.getIcon(), viewHolder.binding.ticketsIcon);
        } else {
            viewHolder.binding.ticketsIcon.setVisibility(View.GONE);
            viewHolder.binding.iconText.setText(String.format("%s %s", model.getEmoji(), model.getFeature()));

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemRaynaTicketsFeaturesBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRaynaTicketsFeaturesBinding.bind(itemView);
        }
    }
}
