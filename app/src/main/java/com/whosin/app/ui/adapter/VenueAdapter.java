package com.whosin.app.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.BooleanResult;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemMyVenuesBinding;
import com.whosin.app.databinding.ItemTestBinding;
import com.whosin.app.databinding.ItemVenueBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.AppSettingTitelCommonModel;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PromoterProfileModel;
import com.whosin.app.service.models.PromoterVenueModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.venue.VenueActivity;

import java.util.ArrayList;
import java.util.List;

public class VenueAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    private Activity activity;
    private FragmentManager supportFragmentManager;
    private FragmentManager fragmentManager;

    private List<PromoterVenueModel> promoterVenueModelList = new ArrayList<>();

    public VenueAdapter(Activity activity, FragmentManager fragmentManager) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_my_venues));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        VenueObjectModel model = (VenueObjectModel) getItem(position);
        viewHolder.mBinding.titleText.setText(model.getName());
        viewHolder.mBinding.subTitleText.setText(model.getAddress());
        Graphics.loadImage(model.getLogo(), viewHolder.mBinding.image);
        viewHolder.mBinding.editIcon.setOnClickListener(view -> {
            ArrayList<String> data = new ArrayList<>();
            data.add(Utils.getLangValue("remove_venue"));

            Graphics.showActionSheet(activity, model.getName(), data, (data1, position1) -> {
                viewHolder.requestPromoterVenueRemove(model.getId());

            });
        });
        viewHolder.mBinding.getRoot().setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            activity.startActivity(new Intent(activity, VenueActivity.class).putExtra("venueId", model.getId()));
        });

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemMyVenuesBinding mBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemMyVenuesBinding.bind(itemView);
        }

        private void requestPromoterVenueRemove(String id) {

            DataService.shared(activity).requestPromoterVenueRemove(id, new RestCallback<ContainerModel<PromoterVenueModel>>() {
                @Override
                public void result(ContainerModel<PromoterVenueModel> model, String error) {

                    if (!Utils.isNullOrEmpty(error) || model == null) {
                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String name = "";
                    Alerter.create(activity).setTitle("Oh Snap!!").setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("you have delete " + name).hideIcon().show();
                    removeItemById(id);
                    notifyDataSetChanged();
                }
            });
        }

        private void removeItemById(String id) {
            for (int i = 0; i < promoterVenueModelList.size(); i++) {
                if ((promoterVenueModelList.get(i)).getId().equals(id)) {
                    promoterVenueModelList.remove(i);
                    notifyItemRemoved(i);
                    notifyItemRangeChanged(i, promoterVenueModelList.size());
                    break;
                }
            }
        }


    }


}
