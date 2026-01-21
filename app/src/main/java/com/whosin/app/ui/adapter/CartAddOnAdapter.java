package com.whosin.app.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemAddOnPreviewBinding;
import com.whosin.app.service.models.myCartModels.MyCartTourDetailsModel;

public class CartAddOnAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_add_on_preview));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        MyCartTourDetailsModel model = (MyCartTourDetailsModel) getItem(position);
        if (model == null) return;

        viewHolder.binding.addOnTitle.setText(model.getAddOnTitle());
        Utils.setTextOrHide(viewHolder.binding.addOndDescription, model.getAddOndesc());
        Utils.setStyledText(viewHolder.itemView.getContext(), viewHolder.binding.addOnPrice, Utils.roundFloatValue(model.getWhosinTotal()));
        Graphics.loadImage(viewHolder.itemView.getContext(), model.getAddOnImage(), viewHolder.binding.ivAddOnImage);

        StringBuilder guestBuilder = new StringBuilder();

        if (model.getAdult() > 0) {
            guestBuilder.append(model.getAdult())
                    .append("x ")
                    .append(model.getAdultTitle());
        }

        if (model.getChild() > 0) {
            if (guestBuilder.length() > 0) guestBuilder.append(", ");
            guestBuilder.append(model.getChild())
                    .append("x ")
                    .append(model.getChildTitle());
        }

        if (model.getInfant() > 0) {
            if (guestBuilder.length() > 0) guestBuilder.append(", ");
            guestBuilder.append(model.getInfant())
                    .append("x ")
                    .append(model.getInfantTitle());
        }

        viewHolder.binding.tvGuestDetails.setText(guestBuilder.toString());


        Utils.setTextOrHide(viewHolder.binding.tvTime, model.getTimeSlot());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAddOnPreviewBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemAddOnPreviewBinding.bind(itemView);
        }
    }
}
