package com.whosin.business.ui.adapter.raynaTicketAdapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.ItemRaynaTicketTagsBinding;
import com.whosin.business.service.models.RatingModel;

public class TicketTagesAdapter <T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = UiUtils.getViewBy(parent, R.layout.item_rayna_ticket_tags);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        view.setLayoutParams(params);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;

        RatingModel model = (RatingModel) getItem(position);

        viewHolder.mBinding.tags.setText(model.getImage());

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemRaynaTicketTagsBinding mBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemRaynaTicketTagsBinding.bind(itemView);
        }
    }
}
