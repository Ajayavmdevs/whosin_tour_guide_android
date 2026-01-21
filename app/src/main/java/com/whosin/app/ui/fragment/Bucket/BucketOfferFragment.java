package com.whosin.app.ui.fragment.Bucket;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentBucketOfferBinding;
import com.whosin.app.databinding.IteamBucketListDetailsBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.offers.OfferDetailBottomSheet;
import com.whosin.app.ui.activites.venue.Bucket.BucketListBottomSheet;
import com.whosin.app.ui.activites.venue.Bucket.BucketListDetailActivity;
import com.whosin.app.ui.activites.venue.VenueShareActivity;
import com.whosin.app.ui.adapter.OfferPackagesAdapter;
import com.whosin.app.ui.fragment.Profile.RemoveBucketDialog;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.util.ArrayList;


public class BucketOfferFragment extends BaseFragment {

    private FragmentBucketOfferBinding binding;
    private final BucketDetailAdapter<OffersModel> bucketDetailAdapter = new BucketDetailAdapter<>();
    private CreateBucketListModel bucketListModel;
    private final OfferPackagesAdapter<PackageModel> packageAdapter = new OfferPackagesAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------
    public BucketOfferFragment(CreateBucketListModel bucketListModel) {
        this.bucketListModel = bucketListModel;
    }

    public BucketOfferFragment() {
    }

    @Override
    public void initUi(View view) {
        binding = FragmentBucketOfferBinding.bind( view );
        setBucketDetailAdapter();
        binding.offerListRecycler.setNestedScrollingEnabled( false );
    }

    @Override
    public void setListeners() {

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_bucket_offer;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setBucketDetailAdapter() {
        binding.offerListRecycler.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.VERTICAL, false ) );
        binding.offerListRecycler.setAdapter( bucketDetailAdapter );
        if (bucketListModel.getOffers() != null && !bucketListModel.getOffers().isEmpty()) {
            bucketDetailAdapter.updateData( bucketListModel.getOffers() );
            binding.emptyPlaceHolderView.setVisibility( View.GONE );
            binding.offerListRecycler.setVisibility( View.VISIBLE );
        } else {
            binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
            binding.offerListRecycler.setVisibility( View.VISIBLE );
        }
    }

    private void reloadData() {
        BucketListDetailActivity parentActivity = (BucketListDetailActivity) getActivity();
        if (parentActivity != null) {
            parentActivity.requestBucketDetail( bucketListModel.getId() );
        }
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void reqRecommendation(String id, VenueObjectModel venueModel) {
        showProgress();
        DataService.shared( context ).requestFeedRecommandation( id, "venue", new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( context, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }

                if (!venueModel.isRecommendation()) {
                    Alerter.create( getActivity() ).setTitle( "Thank you!" ).setTitleAppearance( R.style.AlerterTitle ).setTextAppearance( R.style.AlerterText ).setText( "for recommended " + venueModel.getName() + "to your friends!" ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                } else {
                    Alerter.create( getActivity() ).setTitle( "Oh Snap!!" ).setTitleAppearance( R.style.AlerterTitle ).setTextAppearance( R.style.AlerterText ).setText( "you have removed recommendation of " + venueModel.getName() ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                }
                reloadData();
            }
        } );
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class BucketDetailAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.iteam_bucket_list_details ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            OffersModel model = (OffersModel) getItem( position );
            boolean isLastItem = position == getItemCount() - 1;

            viewHolder.binding.offerInfoView.setOfferDetail( model, getActivity(), getChildFragmentManager() );
            if (model.getVenue() != null) {
                viewHolder.binding.venueDetalis.setVenueDetail( model.getVenue() );
            }


            viewHolder.binding.txtTitle.setText( model.getTitle() );
            viewHolder.binding.tvDescription.setText( model.getDescription() );

            viewHolder.binding.btnBucketList.setOnClickListener( view -> {
                Utils.preventDoubleClick( view );
                ArrayList<String> data = new ArrayList<>();
                data.add( "Remove" );
                data.add( "Move to another bucket" );
                data.add( "Share Venue" );
                data.add( "Share Offer" );

                data.add( model.getVenue().isRecommendation() ? "Remove recommendation" : "Add recommendation" );
                Graphics.showActionSheet( getContext(), model.getVenue().getName(), data, (data1, position1) -> {
                    switch (position1) {
                        case 0:
                            RemoveBucketDialog dialog = new RemoveBucketDialog( bucketListModel.getId() );
                            dialog.offerId = model.getId();
                            dialog.callback = data2 -> {
                                reloadData();
                            };
                            dialog.show( getChildFragmentManager(), "" );
                            break;
                        case 1:
                            BucketListBottomSheet bucketDialog = new BucketListBottomSheet();
                            bucketDialog.offerId = model.getId();
                            bucketDialog.isBucketRemove = true;
                            bucketDialog.bucketId = bucketListModel.getId();
                            bucketDialog.callBack = data2 -> {
                                if (data2) {
                                    reloadData();
                                }
                            };

                            bucketDialog.show( getChildFragmentManager(), "" );
                            break;
                        case 2:
                            startActivity( new Intent( requireActivity(), VenueShareActivity.class ).putExtra( "venue", new Gson().toJson( model.getVenue() ) )
                                    .putExtra( "type", "venue" ) );
                            break;
                        case 3:
                            startActivity( new Intent( requireActivity(), VenueShareActivity.class ).putExtra( "offer", new Gson().toJson( model ) )
                                    .putExtra( "type", "offer" ) );
                            break;
                        case 4:
                            reqRecommendation( model.getVenue().getId(), model.getVenue() );
                            break;
                    }
                } );

            } );

            viewHolder.binding.packageRecycler.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.VERTICAL, false ) );
            viewHolder.binding.packageRecycler.setAdapter( packageAdapter );
            if (model.getPackages() != null) {
                packageAdapter.updateData( model.getPackages() );
            }


            viewHolder.binding.getRoot().setOnClickListener( v -> {
                OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
                dialog.offerId = model.getId();
                dialog.show( getChildFragmentManager(), "" );
            } );

//            viewHolder.binding.btnLetsGo.setOnClickListener( v -> {
//                Utils.preventDoubleClick( v );
//
//                InviteFriendBottomSheet inviteFriendDialog = new InviteFriendBottomSheet();
//                inviteFriendDialog.venueObjectModel = model.getVenue();
//                inviteFriendDialog.offersModel = model;
//                inviteFriendDialog.defaultUserList = bucketListModel.getSharedWith();
//                inviteFriendDialog.setShareListener( data -> {
//                    AppExecutors.get().mainThread().execute( () -> {
//                    } );
//                } );
//                inviteFriendDialog.show( getChildFragmentManager(), "1" );
//            } );

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom( holder.itemView.getContext(), 0.10f );
                Utils.setBottomMargin( holder.itemView, marginBottom );
            } else {
                Utils.setBottomMargin( holder.itemView, 0 );
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final IteamBucketListDetailsBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = IteamBucketListDetailsBinding.bind( itemView );
            }
        }
    }

    // endregion
    // --------------------------------------


}