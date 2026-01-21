package com.whosin.app.ui.activites.Promoter;

import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityPromoterVenuesBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.adapter.VenueAdapter;

import java.util.List;

public class PromoterVenuesActivity extends BaseActivity {

    private ActivityPromoterVenuesBinding binding;

    private VenueAdapter<VenueObjectModel> venueAdapter;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        binding.titleVenuesText.setText(getValue("my_venues"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("venue_list_empty"));

        venueAdapter = new VenueAdapter<>(activity, getSupportFragmentManager());
        binding.venuesRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.venuesRecyclerView.setAdapter(venueAdapter);
        requestPromoterVenues();
    }

    @Override
    protected void setListeners() {
        binding.backIcon.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityPromoterVenuesBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    public void requestPromoterVenues() {
        showProgress();
        DataService.shared(activity).requestPromoterVenues(new RestCallback<ContainerListModel<VenueObjectModel>>() {
            @Override
            public void result(ContainerListModel<VenueObjectModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    venueAdapter.updateData(model.data);
                    binding.emptyPlaceHolderView.setVisibility( View.GONE );
                }else {
                    binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
                    binding.venuesRecyclerView.setVisibility( View.GONE );
                }

            }
        });
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------


}