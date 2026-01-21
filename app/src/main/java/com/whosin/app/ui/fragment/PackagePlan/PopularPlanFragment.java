package com.whosin.app.ui.fragment.PackagePlan;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentPopluarPlanBinding;
import com.whosin.app.databinding.ItemMembershipDeatailRecyclerBinding;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.models.MemberShipPackageModel;
import com.whosin.app.ui.activites.venue.TouristPlanActivity;
import com.whosin.app.ui.activites.venue.ui.PaymentActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;


public class PopularPlanFragment extends BaseFragment {

    private FragmentPopluarPlanBinding binding;

    private PackageDetailsAdapter<MemberShipPackageModel> adapter = new PackageDetailsAdapter();

    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentPopluarPlanBinding.bind( view );
        setPackageAdapter();

    }

    @Override
    public void setListeners() {

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_popluar_plan;
    }



    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void setPackageAdapter() {
        binding.packageDetailRecycler.setLayoutManager( new LinearLayoutManager( requireContext(), LinearLayoutManager.VERTICAL, false ) );
        binding.packageDetailRecycler.setAdapter( adapter );

        adapter.updateData(AppSettingManager.shared.getAppSettingData().getMembershipPackage() );


    }



    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class PackageDetailsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_membership_deatail_recycler ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            MemberShipPackageModel model = (MemberShipPackageModel) getItem( position );


            viewHolder.binding.tvTitle.setText( model.getTitle() );
            viewHolder.binding.tvSubTitle.setText( model.getFeature() );
            viewHolder.binding.tvPrice.setText( "1MONTH FREE \n then" + model.getActualPrice() + "/" + "monthly" );
//            viewHolder.binding.getNowBtn.setText( "Get it Now! " + "(" + model.getActualPrice() + "AED" + ")" );
            String image = model.getBackgroundImage();
/*
            Picasso.get().load( image ).into( new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    viewHolder.binding.mainLinear.setBackground( new BitmapDrawable( activity.getResources(), bitmap ) );
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            } );
*/

            viewHolder.binding.getNowBtn.setOnClickListener( view -> {
                startActivity(new Intent(requireContext(), PaymentActivity.class).putExtra("packageModel", new Gson().toJson(model)));
            } );

            viewHolder.binding.tvFeature.setOnClickListener( view -> {
                startActivity( new Intent(requireActivity(), TouristPlanActivity.class).putExtra( "packageModel", new Gson().toJson( model ) ));
            } );

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemMembershipDeatailRecyclerBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemMembershipDeatailRecyclerBinding.bind( itemView );
            }
        }
    }


    // endregion
    // --------------------------------------
}