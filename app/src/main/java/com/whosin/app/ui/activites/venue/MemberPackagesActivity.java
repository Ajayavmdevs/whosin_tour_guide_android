package com.whosin.app.ui.activites.venue;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityMemberPakagesBinding;
import com.whosin.app.databinding.PakagerOfferItemRecylerBinding;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.models.MemberShipPackageModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.ArrayList;
import java.util.List;


public class MemberPackagesActivity extends BaseActivity {

    private ActivityMemberPakagesBinding binding;

    private PackageAdapter  adapter;

    private List<VenueObjectModel> list = new ArrayList<>();
    private OffersModel model = new OffersModel();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {
        setPackageAdapter();

        String packageModel = Utils.notNullString(getIntent().getStringExtra("packageModel"));
        model = new Gson().fromJson(packageModel, OffersModel.class);

        binding.packageRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
        adapter = new PackageAdapter();
        binding.packageRecycler.setAdapter( adapter );
        if (model != null){
            adapter.updateData(model.getPackages());
        }



    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener( view -> {
            onBackPressed();
        } );
        Glide.with( activity ).load( R.drawable.icon_close_btn ).into( binding.ivClose );
        binding.getBtn.setOnClickListener( v -> {
            startActivity( new Intent( activity, MemberShipDetailsActivity.class ) );
        } );
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityMemberPakagesBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setPackageAdapter() {



    }
    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class PackageAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.pakager_offer_item_recyler ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PackageModel model = (PackageModel) getItem( position );
            viewHolder.binding.tvTitle.setText( model.getTitle() );
            viewHolder.binding.tvDiscount.setText( Utils.addPercentage(model.getDiscount() ) );
            viewHolder.binding.tvDescription.setText( model.getDescription() );
            viewHolder.binding.tvAED.setText( "AED " + model.getDiscountedPrice() );


            if (AppSettingManager.shared.getSubscriptionData().getUserId() != null) {
//                if (!AppSettingManager.shared.getSubscriptionData().getUserId().equals( SessionManager.shared.getUser().getId() )) {
//                    viewHolder.binding.tvDiscount.setBackground( getResources().getDrawable( R.drawable.gray_button_bg ) );
//
//                } else {
//                    viewHolder.binding.tvDiscount.setBackground( getResources().getDrawable( R.drawable.pink_button_bg ) );
//                    binding.nameControl.setMainTitle( "Our Package" );
//                    binding.subTitle.setVisibility( View.GONE );
//                    binding.getBtn.setVisibility( View.GONE );
//                }
            }else {
                viewHolder.binding.tvDiscount.setBackground( getResources().getDrawable( R.drawable.gray_button_bg ) );

            }




        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            PakagerOfferItemRecylerBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = PakagerOfferItemRecylerBinding.bind( itemView );
            }
        }
    }
}