package com.whosin.app.ui.activites.auth;

import android.content.Intent;
import android.view.View;

import android.widget.Toast;


import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivitySelectPrefrenceBinding;
import com.whosin.app.service.DataService;

import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.models.AppSettingTitelCommonModel;


import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.GetPrefrenceModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.home.MainHomeActivity;

import com.whosin.app.ui.adapter.ActivitiesAdapter;

import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.ArrayList;
import java.util.List;


public class SelectPrefrenceActivity extends BaseActivity {

    private ActivitySelectPrefrenceBinding binding;
    private ActivitiesAdapter<AppSettingTitelCommonModel> adapterCuisine, adapterMusic, adapterFeature;
    private List<String> selectedCuisine, selectedMusic, selectedFeature;


    // --------------------------------------
    // region Life Cycle
    // --------------------------------------
    @Override
    protected void initUi() {

        adapterCuisine = new ActivitiesAdapter<>(activity);
        adapterMusic = new ActivitiesAdapter<>(activity);
        adapterFeature = new ActivitiesAdapter<>(activity);

        selectedCuisine = new ArrayList<>();
        selectedMusic = new ArrayList<>();
        selectedFeature = new ArrayList<>();

        FlexboxLayoutManager layoutManagerCuisines = new FlexboxLayoutManager(activity);
        layoutManagerCuisines.setFlexDirection(FlexDirection.ROW);
        layoutManagerCuisines.setJustifyContent(JustifyContent.FLEX_START);

        FlexboxLayoutManager layoutManagerMusic = new FlexboxLayoutManager(activity);
        layoutManagerMusic.setFlexDirection(FlexDirection.ROW);
        layoutManagerMusic.setJustifyContent(JustifyContent.FLEX_START);

        FlexboxLayoutManager layoutManagerFeatures = new FlexboxLayoutManager(activity);
        layoutManagerFeatures.setFlexDirection(FlexDirection.ROW);
        layoutManagerFeatures.setJustifyContent(JustifyContent.FLEX_START);


        binding.tabRecyclerCuisines.setLayoutManager(layoutManagerCuisines);
        binding.tabRecyclerMusic.setLayoutManager(layoutManagerMusic);
        binding.tabRecyclerFeatures.setLayoutManager(layoutManagerFeatures);


        binding.tabRecyclerCuisines.setAdapter(adapterCuisine);
        binding.tabRecyclerMusic.setAdapter(adapterMusic);
        binding.tabRecyclerFeatures.setAdapter(adapterFeature);

        if (AppSettingManager.shared.getAppSettingData() != null) {
            adapterCuisine.updateData(AppSettingManager.shared.getAppSettingData().getCuisine());
            adapterMusic.updateData(AppSettingManager.shared.getAppSettingData().getMusic());
            adapterFeature.updateData(AppSettingManager.shared.getAppSettingData().getFeature());
        }

    }

    @Override
    protected void setListeners() {
        binding.navbar.getBackBtn().setOnClickListener(view -> {
            onBackPressed();
        });
        binding.imageNext.setOnClickListener(view -> {
            selectedCuisine.clear();
            selectedFeature.clear();
            selectedMusic.clear();

            selectedId(selectedCuisine, adapterCuisine);
            selectedId(selectedFeature, adapterFeature);
            selectedId(selectedMusic, adapterMusic);

            setSelectedList();
        });
    }

    // region Private
    // --------------------------------------
    private void selectedId(List<String> list, ActivitiesAdapter<AppSettingTitelCommonModel> adapter) {
        for (AppSettingTitelCommonModel model : adapter.getData()) {
            if (model.isSelected()) {
                list.add(model.getId());
            }
        }

    }

    private void setSelectedList() {

        JsonArray musicArray = new JsonArray();
        for (String music : selectedMusic) {
            musicArray.add(music);
        }

        JsonArray cuisineArray = new JsonArray();
        for (String cuisine : selectedCuisine) {
            cuisineArray.add(cuisine);
        }

        JsonArray featureArray = new JsonArray();
        for (String feature : selectedFeature) {
            featureArray.add(feature);
        }

        JsonObject object = new JsonObject();
        object.add( "music", musicArray  );
        object.add( "cuisine", cuisineArray);
        object.add( "feature", featureArray );
        DataService.shared(activity).requestSelectedPreference(object, new RestCallback<ContainerModel<GetPrefrenceModel>>(this) {
            @Override
            public void result(ContainerModel<GetPrefrenceModel> model, String error) {

                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    startActivity(new Intent(SelectPrefrenceActivity.this, MainHomeActivity.class));
                }
            }
        });

    }

    // endregion
    // --------------------------------------
    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivitySelectPrefrenceBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    // endregion
    // --------------------------------------

}