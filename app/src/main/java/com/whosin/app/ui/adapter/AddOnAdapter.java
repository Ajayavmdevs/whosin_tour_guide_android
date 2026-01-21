package com.whosin.app.ui.adapter;

import static com.whosin.app.comman.Graphics.context;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemAddOnBinding;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.models.rayna.TourOptionsModel;

import java.util.List;


import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.AddOnGuestTimeSheet;

public class AddOnAdapter <T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    public TourOptionsModel whosinTicketTourOptionModel ;
    public boolean isReadOnly = false;
    // Callback invoked when an add-on item is added/updated (passes the updated model)
    public CommanCallback<TourOptionsModel> onAddonUpdated;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_add_on));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof ViewHolder viewHolder)) return;
        TourOptionsModel itemModel = (TourOptionsModel) getItem(position);
        if (itemModel == null) return;

        if (isReadOnly) {
            viewHolder.binding.btnAddOn.setVisibility(View.GONE);
        } else {
            viewHolder.binding.btnAddOn.setVisibility(View.VISIBLE);
        }

        viewHolder.itemView.setClickable(!isReadOnly);
        viewHolder.itemView.setFocusable(!isReadOnly);

        viewHolder.binding.btnAddOn.setEnabled(false);
        viewHolder.binding.btnAddOn.setClickable(false);

        TourOptionsModel model = itemModel;
        boolean isSelected = false;
        for (TourOptionsModel m : RaynaTicketManager.shared.selectedAddonModels) {
            if (m.get_id().equals(itemModel.get_id())) {
                model = m;
                isSelected = true;
                break;
            }
        }
        int totalPax = model.getTmpAdultValue() + model.getTmpChildValue() + model.getTmpInfantValue();
        float totalAmount = (model.getTmpAdultValue() * (model.getAdultPrice() != null ? model.getAdultPrice() : 0))
                + (model.getTmpChildValue() * (model.getChildPrice() != null ? model.getChildPrice() : 0))
                + (model.getTmpInfantValue() * (model.getInfantPrice() != null ? model.getInfantPrice() : 0));

        viewHolder.binding.addOnTitle.setText(model.getTitle());
        Utils.setTextOrHide(viewHolder.binding.addOndDescription, model.getSortDescription());
        if (model.getAdultPrice() != null) {
            Utils.setStyledText(viewHolder.itemView.getContext(), viewHolder.binding.addOnPrice, Utils.roundFloatValue(totalAmount == 0 ? model.getAdultPrice() : totalAmount));
        }

        if (isSelected) {
            viewHolder.binding.viewSeparator.setVisibility(View.VISIBLE);
            viewHolder.binding.llDetailsContainer.setVisibility(View.VISIBLE);
            viewHolder.binding.tvGuestDetails.setVisibility(View.VISIBLE);
            viewHolder.binding.tvTime.setVisibility(View.VISIBLE);
            viewHolder.binding.btnAddOn.setText("ADD MORE");

            if (model.getTmpAdultValue() > 0 || model.getTmpChildValue() > 0) {
                StringBuilder guestBuilder = new StringBuilder();

                if (model.getTmpAdultValue() > 0) {
                    guestBuilder.append(model.getTmpAdultValue())
                            .append("x ")
                            .append(model.getAdultTitle());
                }

                if (model.getTmpChildValue() > 0) {
                    if (guestBuilder.length() > 0) guestBuilder.append(", ");
                    guestBuilder.append(model.getTmpChildValue())
                            .append("x ")
                            .append(model.getChildTitle());
                }

                if (model.getTmpInfantValue() > 0) {
                    if (guestBuilder.length() > 0) guestBuilder.append(", ");
                    guestBuilder.append(model.getTmpInfantValue())
                            .append("x ")
                            .append(model.getInfantTitle());
                }

                viewHolder.binding.tvGuestDetails.setText(guestBuilder.toString());
            } else {
                String guestName = Utils.setLangValue("numberOfPax", String.valueOf(1), model.getAdultTitle(), String.valueOf(1), model.getChildTitle(), String.valueOf(0), model.getInfantTitle());
                viewHolder.binding.tvGuestDetails.setText(guestName);
            }

            String time = "";
            if (model.getRaynaTimeSlotModel() != null) {
                String at = model.getRaynaTimeSlotModel().getAvailabilityTime();
                time = !TextUtils.isEmpty(at) ? at : model.getRaynaTimeSlotModel().getTimeSlot();
            } else if (!TextUtils.isEmpty(model.getAvailabilityTime())) {
                time = model.getAvailabilityTime();
            }
            Utils.setTextOrHide(viewHolder.binding.tvTime, time);
        } else {
            viewHolder.binding.btnAddOn.setText("ADD");
            viewHolder.binding.viewSeparator.setVisibility(View.GONE);
            viewHolder.binding.llDetailsContainer.setVisibility(View.GONE);
            viewHolder.binding.tvGuestDetails.setVisibility(View.GONE);
            viewHolder.binding.tvTime.setVisibility(View.GONE);
        }

        if (model.getImages() != null && !model.getImages().isEmpty() && !model.getImages().get(0).isEmpty()) {
            Glide.with(context).load(model.getImages().get(0)).placeholder(R.drawable.days_background).into(viewHolder.binding.ivAddOnImage);
        } else {
            viewHolder.binding.ivAddOnImage.setImageResource(R.drawable.days_background);
        }

        final TourOptionsModel finalModel = model;
        viewHolder.itemView.setOnClickListener(v -> {
            if (isReadOnly) {
                return;
            }
            if (v.getContext() instanceof AppCompatActivity activity) {
                if (RaynaTicketManager.shared.selectedTourModel.isEmpty() ||
                        TextUtils.isEmpty(RaynaTicketManager.shared.selectedTourModel.get(0).getTourOptionSelectDate())) {
                    Graphics.showAlertDialogWithOkButton(activity, activity.getString(R.string.app_name), "Please choose your ticket first.");
                    return;
                }

                AddOnGuestTimeSheet sheet = new AddOnGuestTimeSheet();
                sheet.tourOptionsModel = finalModel;
                sheet.whosinTicketTourOptionModel = whosinTicketTourOptionModel;
                sheet.callback = cbModel -> {
                    List<TourOptionsModel> selectedList = RaynaTicketManager.shared.selectedAddonModels;

                    if (cbModel == null) {

                        for (int i = 0; i < selectedList.size(); i++) {
                            if (selectedList.get(i).get_id()
                                    .equals(finalModel.get_id())) {
                                selectedList.remove(i);
                                break;
                            }
                        }

                        RaynaTicketManager.shared
                                .updateAddonsJson(whosinTicketTourOptionModel);

                        notifyItemChanged(position);

                        if (onAddonUpdated != null) {
                            onAddonUpdated.onReceive(null);
                        }
                        return;
                    }

                    boolean found = false;
                    for (int i = 0; i < selectedList.size(); i++) {
                        if (selectedList.get(i).get_id().equals(cbModel.get_id())) {
                            selectedList.set(i, cbModel);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        selectedList.add(cbModel);
                    }
                    RaynaTicketManager.shared.updateAddonsJson(whosinTicketTourOptionModel);
                    notifyItemChanged(position);
                    // notify outer listeners about the addon update
                    if (onAddonUpdated != null) {
                        onAddonUpdated.onReceive(cbModel);
                    }
                };
                sheet.show(activity.getSupportFragmentManager(), "AddOnGuestTimeSheet");
            }
        });

        if (isSelected) {
            viewHolder.binding.addOnView.setBackgroundResource(R.drawable.selected_tour_option_people_stock_bg);
        } else {
            viewHolder.binding.addOnView.setBackgroundResource(R.drawable.tour_option_spinner_stock_bg);
        }
        if (isReadOnly) {
            viewHolder.binding.addOnView.setBackgroundResource(R.drawable.tour_option_spinner_stock_bg);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAddOnBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemAddOnBinding.bind(itemView);
        }

    }
}
