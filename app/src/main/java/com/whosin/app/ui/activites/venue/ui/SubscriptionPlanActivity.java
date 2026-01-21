package com.whosin.app.ui.activites.venue.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivitySubscribationPlanBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.MemberShipModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.venue.TouristPlanActivity;

public class SubscriptionPlanActivity extends BaseActivity {

    private ActivitySubscribationPlanBinding binding;

    private MemberShipModel memberShipModel;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        /*model = AppSettingManager.shared.getSubscriptionData();*/
//        getSubscriptionData();
        requestSubscriptionDetail();

    }

    @Override
    protected void setListeners() {
//        Graphics.loadImage( String.valueOf( getResources().getDrawable( R.drawable.icon_close_btn ) ),binding.ivClose );
//        Glide.with( activity ).load( R.drawable.icon_close_btn ).into( binding.ivClose );
        binding.ivClose.setOnClickListener( view -> onBackPressed() );
        binding.tvFeature.setOnClickListener( view -> {
            startActivity( new Intent(activity, TouristPlanActivity.class)
                    .putExtra( "memberShipModel",new Gson().toJson( memberShipModel ) ) );
        } );
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivitySubscribationPlanBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void getSubscriptionData() {

        binding.tvTitle.setText( memberShipModel.getTitle() );
        binding.tvSubTitle.setText( memberShipModel.getDescription());
        binding.tvValidity.setText(memberShipModel.getValidTill() == null ? "lifetime" : memberShipModel.getValidTill());
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    public void requestSubscriptionDetail() {
        DataService.shared( activity ).requestSubscriptionDetail( new RestCallback<ContainerModel<MemberShipModel>>(this) {
            @Override
            public void result(ContainerModel<MemberShipModel> model, String error) {

                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                memberShipModel = model.getData();
                getSubscriptionData();

            }
        } );
    }


}