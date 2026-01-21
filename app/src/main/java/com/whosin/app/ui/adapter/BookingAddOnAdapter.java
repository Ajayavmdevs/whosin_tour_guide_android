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
import com.whosin.app.service.models.rayna.RaynaTourDetailModel;

import java.util.List;

public class BookingAddOnAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_add_on_preview));
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position
    ) {
        ViewHolder viewHolder = (ViewHolder) holder;
        RaynaTourDetailModel model =
                (RaynaTourDetailModel) getItem(position);

        if (model == null) return;

        String addonTitle;
        if (!Utils.isNullOrEmpty(model.getAddonTitle())) {
            addonTitle = model.getAddonTitle();
        } else if (model.getAddonOption() != null) {
            addonTitle = model.getAddonOption().getTitle();
        } else {
            addonTitle = "";
        }

        String addonDesc;
        if (!Utils.isNullOrEmpty(model.getAddOndesc())) {
            addonDesc = model.getAddOndesc();
        } else if (model.getAddonOption() != null) {
            addonDesc = model.getAddonOption().getSortDescription();
        } else {
            addonDesc = "";
        }

        viewHolder.binding.addOnTitle.setText(addonTitle);
        Utils.setTextOrHide(viewHolder.binding.addOndDescription, addonDesc);

        Utils.setStyledText(
                viewHolder.itemView.getContext(),
                viewHolder.binding.addOnPrice,
                Utils.roundFloatValue(Float.valueOf(model.getWhosinTotal()))
        );

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

        String image = null;

        if (model.getAddonOption() != null
                && model.getAddonOption().getImages() != null
                && !model.getAddonOption().getImages().isEmpty()
                && !Utils.isNullOrEmpty(model.getAddonOption().getImages().get(0))) {

            image = model.getAddonOption().getImages().get(0);

        } else if (!Utils.isNullOrEmpty(model.getAddonImage())) {
            image = model.getAddonImage();
        }

        Graphics.loadImage(
                viewHolder.itemView.getContext(),
                image == null ? "" : image,
                viewHolder.binding.ivAddOnImage
        );

        Utils.setTextOrHide(
                viewHolder.binding.tvTime,
                model.getTimeSlot()
        );
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAddOnPreviewBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemAddOnPreviewBinding.bind(itemView);
        }
    }
}
