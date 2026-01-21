package com.whosin.app.ui.adapter.raynaTicketAdapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemRaynaTicketTagsBinding;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;

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
