package com.whosin.app.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.HomeBlockLargeOfferViewBinding;
import com.whosin.app.databinding.ItemLargeVenueBinding;
import com.whosin.app.service.models.CustomVenueModel;

import java.util.List;

public class CustomOfferView extends ConstraintLayout {

    private HomeBlockLargeOfferViewBinding binding;

    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;
    private List<CustomVenueModel> customOfferModelList;

    private CustomOfferAdapter<CustomVenueModel> customOfferAdapter;

    public CustomOfferView(Context context) {
        this( context, null );    }

    public CustomOfferView(Context context,AttributeSet attrs) {
        this( context, attrs, 0 );    }

    public CustomOfferView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super( context, attrs, 0 );

        this.context = context;
        LayoutInflater.from( context ).inflate( R.layout.offer_info_view_loader, this, true );

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater( context );
        asyncLayoutInflater.inflate( R.layout.home_block_large_offer_view, this, (view, resid, parent) -> {
            binding = HomeBlockLargeOfferViewBinding.bind( view );
            setupRecycleHorizontalManager( binding.offerLargeRecycler );
            customOfferAdapter = new CustomOfferAdapter<>( activity, supportFragmentManager );
            binding.offerLargeRecycler.setAdapter( customOfferAdapter );
            if (customOfferModelList != null) {
                activity.runOnUiThread(() -> customOfferAdapter.updateData( customOfferModelList ));
            }
            CustomOfferView.this.removeAllViews();
            CustomOfferView.this.addView( view );
        } );}

    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager( new LinearLayoutManager( context, LinearLayoutManager.HORIZONTAL, false ) );
        int spacing = getResources().getDimensionPixelSize( com.intuit.ssp.R.dimen._10ssp );
        recyclerView.addItemDecoration( new HorizontalSpaceItemDecoration( spacing ) );
//        recyclerView.setNestedScrollingEnabled( false );
        recyclerView.offsetChildrenHorizontal( 1 );
    }

    public void setupData(List<CustomVenueModel> venue, Activity activity, FragmentManager fragmentManager) {
        this.customOfferModelList = venue;
        this.activity = activity;
        this.supportFragmentManager = fragmentManager;
        if (customOfferModelList == null) {return;}
        if (binding == null) { return; }
        activity.runOnUiThread(() -> customOfferAdapter.updateData( customOfferModelList ));
    }

    public class CustomOfferAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {


        private final Activity activity;
        private final FragmentManager fragmentManager;
        private  final int screenWidth = Graphics.getScreenWidth(context);


        public CustomOfferAdapter(Activity activity, FragmentManager fragmentManager) {
            this.activity = activity;
            this.fragmentManager = fragmentManager;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_large_venue);
            setItemWidth(getItemCount(), view);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CustomVenueModel model = (CustomVenueModel) getItem(position);
            activity.runOnUiThread(() -> {
                viewHolder.mBinding.tvPrice.setText( Utils.addPercentage(model.getBadge()));
                viewHolder.mBinding.textTitleSmall.setText(model.getTitle());
                viewHolder.mBinding.textTitle.setText(model.getSubTitle());

                if (model.getOffer() != null) {
                    Graphics.loadImage(model.getOffer().getImage(), viewHolder.mBinding.imgCover);
                    if (model.getOffer().getVenue() != null) {
                        viewHolder.mBinding.venueContainer.setVenueDetail(model.getOffer().getVenue());
                        viewHolder.mBinding.venueHoldLayout.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.mBinding.venueHoldLayout.setVisibility(View.GONE);
                    }
                }
            });

            viewHolder.mBinding.tvView.setOnClickListener(view -> {
                if (model.getOffer().getVenue() != null) {
                    Graphics.openVenueDetail(activity, model.getOffer().getVenue().getId());
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemLargeVenueBinding mBinding;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemLargeVenueBinding.bind(itemView);
            }
        }
        private void setItemWidth(int itemCount, View view) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (screenWidth * (itemCount > 1 ? 0.89 : 0.93));
            view.setLayoutParams(params);
        }
    }
}
