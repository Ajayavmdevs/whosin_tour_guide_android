package com.whosin.app.ui.adapter;

import android.app.Activity;
import android.content.Intent;
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
import com.whosin.app.databinding.ItemSaveToDraftEventDesignBinding;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.ui.activites.Promoter.PromoterCreateEventActivity;


public class SaveDraftEventAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    public Activity activity;

    public SaveDraftEventAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = UiUtils.getViewBy(parent, R.layout.item_save_to_draft_event_design);
        if (getItemCount() > 1 ){
            view.getLayoutParams().width = (int) (Graphics.getScreenWidth(activity) * 0.92);
        }else {
            view.getLayoutParams().width = (int) (Graphics.getScreenWidth(activity) * 0.95);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        PromoterEventModel model = (PromoterEventModel) getItem(position);

        viewHolder.binding.subTitleTv.setText(Utils.getLangValue("draft_continue"));

        if (model.getVenueType().equals( "venue" )) {
            if (model.getVenue() != null) {
                if (Utils.isNullOrEmpty(model.getVenue().getLogo())){
                    viewHolder.binding.imgEvent.setImageResource(R.drawable.icon_empty_venue_edit);
                }else {
                    Graphics.loadImageWithFirstLetter( model.getVenue().getLogo(), viewHolder.binding.imgEvent, model.getVenue().getName() );
                }
                if (Utils.isNullOrEmpty(model.getVenue().getName())){
                    viewHolder.binding.tvDescription.setVisibility(View.GONE);
                    viewHolder.binding.tvTitleText.setText(Utils.getLangValue("select_venue_event"));
                }else {
                    viewHolder.binding.tvDescription.setVisibility(View.VISIBLE);
                    viewHolder.binding.tvTitleText.setText( model.getVenue().getName() );
                    viewHolder.binding.tvDescription.setText(model.getVenue().getAddress());
                }

            }

        } else {
            if (model.getCustomVenue() != null) {
                if (Utils.isNullOrEmpty(model.getCustomVenue().getImage())) {
                    viewHolder.binding.imgEvent.setImageResource(R.drawable.icon_empty_venue_edit);
                } else {
                    Graphics.loadImageWithFirstLetter(model.getCustomVenue().getImage(), viewHolder.binding.imgEvent, model.getCustomVenue().getName());
                }

                if (Utils.isNullOrEmpty(model.getCustomVenue().getName())) {
                    viewHolder.binding.tvDescription.setVisibility(View.GONE);
                    viewHolder.binding.tvTitleText.setText(Utils.getLangValue("select_venue_event"));
                } else {
                    viewHolder.binding.tvDescription.setVisibility(View.VISIBLE);
                    viewHolder.binding.tvTitleText.setText(model.getCustomVenue().getName());
                    viewHolder.binding.tvDescription.setText(model.getCustomVenue().getAddress());
                }
            }
        }


        viewHolder.binding.getRoot().setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            PromoterProfileManager.shared.isEventSaveToDraft = true;
            PromoterProfileManager.shared.promoterEventModel = model;
            activity.startActivity(new Intent(activity, PromoterCreateEventActivity.class));

        });


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemSaveToDraftEventDesignBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSaveToDraftEventDesignBinding.bind(itemView);
        }


    }

}