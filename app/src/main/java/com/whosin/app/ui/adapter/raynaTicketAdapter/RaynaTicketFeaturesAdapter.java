package com.whosin.app.ui.adapter.raynaTicketAdapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemRaynaTicketsFeaturesBinding;
import com.whosin.app.service.models.YachtFeatureModel;


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
