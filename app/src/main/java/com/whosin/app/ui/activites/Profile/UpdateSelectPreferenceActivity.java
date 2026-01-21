package com.whosin.app.ui.activites.Profile;

import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityUpdateSelectPrefernceBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.models.AppSettingTitelCommonModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.GetPrefrenceModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.adapter.ActivitiesAdapter;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class UpdateSelectPreferenceActivity extends BaseActivity {


    private ActivityUpdateSelectPrefernceBinding binding;

    private ActivitiesAdapter<AppSettingTitelCommonModel> adapterCuisine, adapterMusic, adapterFeature;

    private List<String> selectedCuisine, selectedMusic, selectedFeature;


    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    @Override
    protected void initUi() {
        selectedCuisine = new ArrayList<>();
        selectedMusic = new ArrayList<>();
        selectedFeature = new ArrayList<>();


        getSelectedList();

        adapterCuisine = new ActivitiesAdapter<>( activity );
        adapterMusic = new ActivitiesAdapter<>( activity );
        adapterFeature = new ActivitiesAdapter<>( activity );


        FlexboxLayoutManager layoutManagerCuisines = new FlexboxLayoutManager( activity );
        layoutManagerCuisines.setFlexDirection( FlexDirection.ROW );
        layoutManagerCuisines.setJustifyContent( JustifyContent.CENTER );

        FlexboxLayoutManager layoutManagerMusic = new FlexboxLayoutManager( activity );
        layoutManagerMusic.setFlexDirection( FlexDirection.ROW );
        layoutManagerMusic.setJustifyContent( JustifyContent.CENTER );

        FlexboxLayoutManager layoutManagerFeatures = new FlexboxLayoutManager( activity );
        layoutManagerFeatures.setFlexDirection( FlexDirection.ROW );
        layoutManagerFeatures.setJustifyContent( JustifyContent.CENTER );


        binding.tabRecyclerCuisines.setLayoutManager( layoutManagerCuisines );
        binding.tabRecyclerMusic.setLayoutManager( layoutManagerMusic );
        binding.tabRecyclerFeatures.setLayoutManager( layoutManagerFeatures );


        binding.tabRecyclerCuisines.setAdapter( adapterCuisine );
        binding.tabRecyclerMusic.setAdapter( adapterMusic );
        binding.tabRecyclerFeatures.setAdapter( adapterFeature );

        if (AppSettingManager.shared.getAppSettingData() != null) {
            adapterCuisine.updateData( AppSettingManager.shared.getAppSettingData().getCuisine() );
            adapterMusic.updateData( AppSettingManager.shared.getAppSettingData().getMusic() );
            adapterFeature.updateData( AppSettingManager.shared.getAppSettingData().getFeature() );

        }

        Glide.with( activity ).load( R.drawable.icon_close_btn ).into( binding.iconClose );


    }

    @Override
    protected void setListeners() {

        binding.updateBtn.setOnClickListener( view -> {
            selectedCuisine.clear();
            selectedFeature.clear();
            selectedMusic.clear();

            selectedId( selectedCuisine, adapterCuisine );
            selectedId( selectedFeature, adapterFeature );
            selectedId( selectedMusic, adapterMusic );

            setSelectedList();

        } );

        binding.iconClose.setOnClickListener( view -> {
            onBackPressed();
        } );
    }


    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityUpdateSelectPrefernceBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void selectedId(List<String> list, ActivitiesAdapter<AppSettingTitelCommonModel> adapter) {
        for (AppSettingTitelCommonModel model : adapter.getData()) {
            if (model.isSelected()) {
                list.add( model.getId() );
            }

        }

    }

    private void addItem(GetPrefrenceModel data) {
        for (String cuisine : data.getCuisine()) {
            selectedCuisine.add( cuisine );
        }
        for (String music : data.getMusic()) {
            selectedMusic.add( music );
        }
        for (String feature : data.getFeatures()) {
            selectedFeature.add( feature );
        }


    }


    private void updateItemSelectionState(List<AppSettingTitelCommonModel> adapterData, List<String> selectedIds) {
        for (AppSettingTitelCommonModel model : adapterData) {
            if (selectedIds.contains( model.getId() )) {
                model.setSelected( true );

            } else {
                model.setSelected( false );
            }
        }
    }
    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void setSelectedList() {
        JsonArray musicArray = new JsonArray();
        for (String music : selectedMusic) {
            musicArray.add( music );
        }

        JsonArray cuisineArray = new JsonArray();
        for (String cuisine : selectedCuisine) {
            cuisineArray.add( cuisine );
        }

        JsonArray featureArray = new JsonArray();
        for (String feature : selectedFeature) {
            featureArray.add( feature );
        }

        JsonObject object = new JsonObject();
        object.add( "music", musicArray );
        object.add( "cuisine", cuisineArray );
        object.add( "feature", featureArray );

        DataService.shared( activity ).requestSelectedPreference( object, new RestCallback<ContainerModel<GetPrefrenceModel>>(this) {
            @Override
            public void result(ContainerModel<GetPrefrenceModel> model, String error) {

                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Toast.makeText( activity, model.message, Toast.LENGTH_SHORT ).show();
            }
        } );

    }

    private void getSelectedList() {

        DataService.shared( activity ).requestGetPreference( new RestCallback<ContainerModel<GetPrefrenceModel>>(this) {
            @Override
            public void result(ContainerModel<GetPrefrenceModel> model, String error) {

                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }

                model.getData();
                addItem( model.getData() );

                updateItemSelectionState( adapterCuisine.getData(), model.getData().getCuisine() );
                updateItemSelectionState( adapterMusic.getData(), model.getData().getMusic() );
                updateItemSelectionState( adapterFeature.getData(), model.getData().getFeatures() );
                adapterCuisine.notifyDataSetChanged();
                adapterFeature.notifyDataSetChanged();
                adapterMusic.notifyDataSetChanged();
            }
        } );

    }

}