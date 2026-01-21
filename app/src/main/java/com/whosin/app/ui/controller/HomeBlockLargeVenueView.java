package com.whosin.app.ui.controller;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whosin.app.R;
import com.whosin.app.comman.AppDelegate;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.HomeBlockLargeVenueViewBinding;
import com.whosin.app.databinding.ItemLargeVenueComponentBinding;
import com.whosin.app.service.models.VenueObjectModel;

import java.util.List;

public class HomeBlockLargeVenueView extends ConstraintLayout {

    private HomeBlockLargeVenueViewBinding binding;
    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;
    private List<VenueObjectModel> venueObjectModelList;

    private HomeBlockLargeVenueAdapter<VenueObjectModel> venueSuggestionAdapter;

    public HomeBlockLargeVenueView(Context context) {
        this( context, null );
    }

    public HomeBlockLargeVenueView(Context context, AttributeSet attrs) {
        this( context, attrs, 0 );
    }

    public HomeBlockLargeVenueView(Context context, AttributeSet attrs, int defStyleAttr) {
        super( context, attrs, 0 );
        this.context = context;
        LayoutInflater.from( context ).inflate( R.layout.offer_info_view_loader, this, true );

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater( context );
        asyncLayoutInflater.inflate( R.layout.home_block_large_venue_view, this, (view, resid, parent) -> {
            binding = HomeBlockLargeVenueViewBinding.bind( view );
            setupRecycleHorizontalManager( binding.venueLargeRecycler );
            venueSuggestionAdapter = new HomeBlockLargeVenueAdapter<>(supportFragmentManager);
            binding.venueLargeRecycler.setAdapter( venueSuggestionAdapter );
            if (venueObjectModelList != null) {
                activity.runOnUiThread(() -> venueSuggestionAdapter.updateData(venueObjectModelList));
            } else {
                Log.d("HomeBlockLargeVenueView", "onBindViewHolder: null venue list" );
            }
            HomeBlockLargeVenueView.this.removeAllViews();
            HomeBlockLargeVenueView.this.addView( view );
        } );
    }

    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager( new LinearLayoutManager( context, LinearLayoutManager.HORIZONTAL, false ) );
        int spacing = getResources().getDimensionPixelSize( com.intuit.ssp.R.dimen._10ssp );
        recyclerView.addItemDecoration( new HorizontalSpaceItemDecoration( spacing ) );
    }

    public void setupData(List<VenueObjectModel> venue, Activity activity, FragmentManager fragmentManager) {
        this.venueObjectModelList = venue;
        this.activity = activity;
        this.supportFragmentManager = fragmentManager;
        if (venueObjectModelList == null) {return;}
        if (binding == null){return;}
        activity.runOnUiThread(() -> venueSuggestionAdapter.updateData(venueObjectModelList));
    }

    public static class HomeBlockLargeVenueAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private final FragmentManager fragmentManager;

        public HomeBlockLargeVenueAdapter(FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy( parent, R.layout.item_large_venue_component );
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (Graphics.getScreenWidth(AppDelegate.activity) * (getItemCount() > 1 ? 0.89 : 0.93));
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (AppDelegate.activity == null) {
                Log.d("HomeBlockLargeVenueView", "onBindViewHolder: empty activity" );
                return;
            }
            ViewHolder viewHolder = (ViewHolder) holder;
            VenueObjectModel model = (VenueObjectModel) getItem( position );
            if (model != null) {
                AppDelegate.activity.runOnUiThread( () -> {
                    viewHolder.mBinding.venueContainer.setVenueDetail( model );
                    Graphics.loadImage( model.getCover(), viewHolder.mBinding.cover );
                    viewHolder.mBinding.tvDiscountText.setVisibility( !TextUtils.isEmpty( model.getDiscountText() ) ? View.VISIBLE : View.GONE );
                    viewHolder.mBinding.tvDiscountText.setText( model.getDiscountText() != null ? "\uD83D\uDD25 " + model.getDiscountText() : "" );
                } );
//                viewHolder.mBinding.txtInviteFriend.setOnClickListener( v -> {
//                    Utils.preventDoubleClick( v );
//                    InviteFriendBottomSheet inviteFriendDialog = new InviteFriendBottomSheet();
//                    inviteFriendDialog.venueObjectModel = model;
//                    inviteFriendDialog.setShareListener( data -> {
//
//                    } );
//                    inviteFriendDialog.show( fragmentManager, "1" );
//                } );
                viewHolder.itemView.setOnClickListener( v -> Graphics.openVenueDetail( AppDelegate.activity, model.getId() ) );

                viewHolder.mBinding.shareBtn.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    Graphics.openShareDialog(AppDelegate.activity, model,null, null, "venue");
                });
            }
            else {
                Log.d("HomeBlockLargeVenueView", "onBindViewHolder: empty model" );
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            ItemLargeVenueComponentBinding mBinding;
            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = ItemLargeVenueComponentBinding.bind( itemView );
            }
        }
    }

}