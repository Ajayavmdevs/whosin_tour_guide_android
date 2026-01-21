package com.whosin.app.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.BooleanResult;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.HomeBlockLargeOfferViewBinding;
import com.whosin.app.databinding.HomeBlockLargeVenueViewBinding;
import com.whosin.app.databinding.ItemNewLargeOfferComponentBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.FollowUnfollowModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.offers.OfferDetailBottomSheet;
import com.whosin.app.ui.activites.venue.Bucket.BucketListBottomSheet;
import com.whosin.app.ui.activites.venue.VenueBuyNowActivity;
import com.whosin.app.ui.activites.venue.VenueShareActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeBlockLargeOfferView extends ConstraintLayout {

    private HomeBlockLargeOfferViewBinding binding;

    private Context context;
    public Activity activity;
    private FragmentManager supportFragmentManager;
    private List<OffersModel> offersList;

    private HomeBlockLargeOfferAdapter<OffersModel> offerAdapter;

    public HomeBlockLargeOfferView(Context context) {
        this( context, null );
    }

    public HomeBlockLargeOfferView(Context context, AttributeSet attrs) {
        this( context, attrs, 0 );
    }

    public HomeBlockLargeOfferView(Context context, AttributeSet attrs, int defStyleAttr) {
        super( context, attrs, 0 );
        this.context = context;
        LayoutInflater.from( context ).inflate( R.layout.offer_info_view_loader, this, true );

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater( context );
        asyncLayoutInflater.inflate( R.layout.home_block_large_offer_view, this, (view, resid, parent) -> {
            binding = HomeBlockLargeOfferViewBinding.bind( view );
            setupRecycleHorizontalManager( binding.offerLargeRecycler );
            if (activity == null){
                offerAdapter = new HomeBlockLargeOfferAdapter<>(Graphics.activity, supportFragmentManager );
            }else {
                offerAdapter = new HomeBlockLargeOfferAdapter<>( activity, supportFragmentManager );
            }
            binding.offerLargeRecycler.setAdapter( offerAdapter );
            if (offersList != null && !offersList.isEmpty()) {
                activity.runOnUiThread(() -> offerAdapter.updateData( offersList ));
            }
            HomeBlockLargeOfferView.this.removeAllViews();
            HomeBlockLargeOfferView.this.addView( view );
        } );
    }

    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager( new LinearLayoutManager( context, LinearLayoutManager.HORIZONTAL, false ) );
        int spacing = getResources().getDimensionPixelSize( com.intuit.ssp.R.dimen._10ssp );
        recyclerView.addItemDecoration( new HorizontalSpaceItemDecoration( spacing ) );
//        recyclerView.setNestedScrollingEnabled( false );
        recyclerView.offsetChildrenHorizontal( 1 );
    }


    public void setupData(List<OffersModel> venue, Activity activity, FragmentManager fragmentManager) {
        this.offersList = venue;
        this.activity = activity;
        this.supportFragmentManager = fragmentManager;
        if (offersList == null && offersList.isEmpty()) {return;}
        if (binding == null) { return; }
        activity.runOnUiThread(() -> offerAdapter.updateData( offersList ));
    }


    public static class HomeBlockLargeOfferAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        private final Activity activity;
        private final FragmentManager fragmentManager;

        public HomeBlockLargeOfferAdapter(Activity activity, FragmentManager fragmentManager) {
            this.activity = activity;
            this.fragmentManager = fragmentManager;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy( parent, R.layout.item_new_large_offer_component );
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (Graphics.getScreenWidth( activity ) * (getItemCount() > 1 ? 0.89 : 0.93));
            view.setLayoutParams( params );
            return new ViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            OffersModel model = (OffersModel) getItem( position );
            if (activity == null) return;
            if (Utils.isValidActivity(activity)) {
                activity.runOnUiThread(() -> {
                    viewHolder.mBinding.txtTitle.setText(model.getTitle());
                    viewHolder.mBinding.tvDescription.setText(model.getDescription());
                    viewHolder.mBinding.offerInfoView.setOfferDetail(model, activity, fragmentManager);
                    viewHolder.mBinding.buyNowButton.setVisibility( model.isAvailableToBuy() ? View.VISIBLE : View.GONE);
                    viewHolder.mBinding.claimButton.setVisibility(model.isSpecialOffer() ? View.VISIBLE : View.GONE);
                    viewHolder.mBinding.txtDiscountTag.setVisibility(model.getDiscountTag() != null && !model.getDiscountTag().isEmpty() ? View.VISIBLE : View.INVISIBLE);
                    viewHolder.mBinding.txtDiscountTag.setText(model.getDiscountTag());

                    if (model.getVenue() != null) {
                        viewHolder.mBinding.venueContainer.setVisibility(View.VISIBLE);
                        viewHolder.mBinding.venueContainer.setVenueDetail(model.getVenue());
                    } else {
                        viewHolder.mBinding.venueContainer.setVisibility(View.GONE);
                    }

                    viewHolder.mBinding.tvDescription.post(() -> {
                        int lineCount = viewHolder.mBinding.tvDescription.getLineCount();
                        if (lineCount > 2) {
                            Utils.makeTextViewResizable(viewHolder.mBinding.tvDescription, 2, 2, ".. See More", false);
                        }
                    });


                });
            }

            viewHolder.mBinding.buyNowButton.setOnClickListener(v->{
                activity.startActivity( new Intent( activity, VenueBuyNowActivity.class ).putExtra( "venueObjectModel", new Gson().toJson( model.getVenue() ) ).putExtra( "offerModel", new Gson().toJson( model ) ) );

            });


            viewHolder.mBinding.claimButton.setOnClickListener( view -> {
                Utils.preventDoubleClick( view );
                if (model.getSpecialOfferModel() == null) {
                    return;
                }
                if (model.getVenue() == null) {
                    return;
                }
                Utils.openClaimScreen( model.getSpecialOfferModel(), model.getVenue(), activity );
            } );

            viewHolder.mBinding.btnMenu.setOnClickListener( v -> showActionSheet( v, model ) );

            viewHolder.itemView.setOnClickListener( v -> {
                if (model == null) return;
                if (fragmentManager == null) return;
                OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
                dialog.offerId = model.getId();
                dialog.show( fragmentManager, "" );
            } );
        }

        private void showActionSheet(View v, OffersModel model) {
            Utils.preventDoubleClick( v );
            ArrayList<String> data = new ArrayList<>();
            data.add( "Add To BucketList" );
            if (model.getVenue().isIsFollowing()) {
                data.add( "UnFollow" );
            } else {
                data.add( "Follow" );
            }
            data.add( "Share Venue" );
            data.add( "Share Offer" );
            data.add( model.getVenue().isRecommendation() ? "Remove recommendation" : "Add recommendation" );
            Graphics.showActionSheet( activity, model.getVenue().getName(), data, (data1, position1) -> {
                switch (position1) {
                    case 0:
                        BucketListBottomSheet dialog = new BucketListBottomSheet();
                        dialog.offerId = model.getId();
                        dialog.show( fragmentManager, "" );
                        break;
                    case 1:
                        reqFollowUnFollow( model.getVenue(), (success, error) -> {
                            notifyDataSetChanged();
                        } );
                        break;
                    case 2:
                        Intent shareIntent = new Intent(activity, VenueShareActivity.class);
                        shareIntent.putExtra( "venue",new Gson().toJson( model.getVenue()) );
                        shareIntent.putExtra( "type","venue" );
                        activity.startActivity(shareIntent);
                        break;
                    case 3:
                        Intent intent = new Intent(activity, VenueShareActivity.class);
                        intent.putExtra( "offer",new Gson().toJson( model) );
                        intent.putExtra( "type","offer" );
                        activity.startActivity(intent);
                        break;
                    case 4:
                        reqRecommendation( model.getVenue().getId(), model.getVenue() );
                        notifyDataSetChanged();
                        break;
                }

            } );

        }

        private void reqFollowUnFollow(VenueObjectModel venueObjectModel, BooleanResult callBack) {
            DataService.shared( activity ).requestVenueFollow( venueObjectModel.getId(), new RestCallback<ContainerModel<FollowUnfollowModel>>(null) {
                @Override
                public void result(ContainerModel<FollowUnfollowModel> model, String error) {
                    if (!Utils.isNullOrEmpty( error ) || model == null) {
                        Toast.makeText( activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                        return;
                    }
                    callBack.success( !model.message.equals( "Unfollowed!" ), "" );
                    if (!model.message.equals( "Unfollowed!" )) {
                        Alerter.create( activity ).setTitle( "Thank you!" ).setText( "For following " + venueObjectModel.getName() ).setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                        venueObjectModel.setIsFollowing( true );
                    } else {
                        Alerter.create( activity ).setTitle( "Oh Snap!" ).setTitleAppearance( R.style.AlerterTitle ).setTextAppearance( R.style.AlerterText ).setText( "You have unfollowed " + venueObjectModel.getName() ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                        venueObjectModel.setIsFollowing( false );
                    }
                }
            } );
        }

        private void reqRecommendation(String id, VenueObjectModel venueModel) {
            DataService.shared( activity ).requestFeedRecommandation( id, "venue", new RestCallback<ContainerModel<UserDetailModel>>(null) {
                @Override
                public void result(ContainerModel<UserDetailModel> model, String error) {
                    if (!Utils.isNullOrEmpty( error ) || model == null) {
                        Toast.makeText( activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                        return;
                    }

                    if (!venueModel.isRecommendation()) {
                        venueModel.setRecommendation( true );
                        Alerter.create( activity ).setTitle( "Thank you!" ).setTitleAppearance( R.style.AlerterTitle ).setTextAppearance( R.style.AlerterText ).setText( "for recommended " + venueModel.getName() + "to your friends!" ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                    } else {
                        venueModel.setRecommendation( false );
                        Alerter.create( activity ).setTitle( "Oh Snap!!" ).setTitleAppearance( R.style.AlerterTitle ).setTextAppearance( R.style.AlerterText ).setText( "you have removed recommendation of " + venueModel.getName() ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                    }
                }
            } );
        }




        public static class ViewHolder extends RecyclerView.ViewHolder {
            private ItemNewLargeOfferComponentBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = ItemNewLargeOfferComponentBinding.bind( itemView );
            }
        }

    }

}
