package com.whosin.app.ui.activites.venue;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityTouristPlanBinding;
import com.whosin.app.databinding.ItemSubscriptionDeatailRecyclerBinding;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.models.FeatureModel;
import com.whosin.app.service.models.MemberShipModel;
import com.whosin.app.service.models.MemberShipPackageModel;
import com.whosin.app.service.models.SubscriptionModel;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.HomeMenuActivity;
import com.whosin.app.ui.activites.venue.ui.PaymentActivity;
import com.whosin.app.ui.fragment.home.SubscriptionPlanBottomSheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class TouristPlanActivity extends BaseActivity {

    private ActivityTouristPlanBinding binding;
    private MemberShipModel memberShipModel;
    private FeatureDetailsAdapter<FeatureModel> adapter = new FeatureDetailsAdapter();
    private SubscriptionModel model;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        applyTranslations();

        String memberShip = getIntent().getStringExtra( "memberShipModel" );
        memberShipModel = new Gson().fromJson( memberShip, MemberShipModel.class );

        model = AppSettingManager.shared.getSubscriptionData();

        if (memberShipModel != null) {
            binding.tvBundleName.setText(memberShipModel.getTitle());
            binding.tvValidDate.setText(memberShipModel.getValidTill() == null ? "Validity : lifetime" : memberShipModel.getValidTill());
            binding.tvDescription.setText(memberShipModel.getDescription());
            binding.featureRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
            binding.featureRecycler.setAdapter( adapter );
            adapter.updateData(memberShipModel.getFeatures());
        }

    }

    @Override
    protected void setListeners() {

        binding.cancleNowBtn.setOnClickListener( view -> {});

        binding.iconClose.setOnClickListener( v -> {
            if (memberShipModel != null) {
                onBackPressed();
            }
            else {
                startActivity(new Intent(activity, HomeMenuActivity.class));
                activity.finish();
            }
        });
        Glide.with( activity ).load( R.drawable.icon_left_back_arrow ).into( binding.iconClose );
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityTouristPlanBinding.inflate( getLayoutInflater());
        return binding.getRoot();
    }



    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvTitle, "my_subscriptions");
        map.put(binding.cancleNowBtn, "cancel_your_bundle");
        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class FeatureDetailsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_subscription_deatail_recycler ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            FeatureModel model = (FeatureModel) getItem( position );
            Graphics.loadImage(model.getIcon(),viewHolder.binding.imgFeature);
            viewHolder.binding.tvTitle.setText( model.getFeature() );

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemSubscriptionDeatailRecyclerBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemSubscriptionDeatailRecyclerBinding.bind( itemView );
            }
        }
    }


    // endregion
    // --------------------------------------

}