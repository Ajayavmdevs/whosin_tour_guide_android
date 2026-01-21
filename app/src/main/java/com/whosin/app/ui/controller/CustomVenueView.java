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

public class CustomVenueView extends ConstraintLayout {

    private HomeBlockLargeOfferViewBinding binding;
    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;
    private List<CustomVenueModel> customVenueList;
    private  int screenWidth;
    private CustomVenueAdapter<CustomVenueModel> customVenueAdapter;

    public CustomVenueView(@NonNull Context context) {
        this( context, null );    }

    public CustomVenueView(Context context, @Nullable AttributeSet attrs) {
        this( context, attrs, 0 );

    }


    public CustomVenueView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super( context, attrs, 0 );
        this.context = context;
        screenWidth = Graphics.getScreenWidth(context);
        LayoutInflater.from( context ).inflate( R.layout.offer_info_view_loader, this, true );

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater( context );
        asyncLayoutInflater.inflate( R.layout.home_block_large_offer_view, this, (view, resid, parent) -> {
            binding = HomeBlockLargeOfferViewBinding.bind( view );
            setupRecycleHorizontalManager( binding.offerLargeRecycler );
            customVenueAdapter = new CustomVenueAdapter<>( activity, supportFragmentManager );
            binding.offerLargeRecycler.setAdapter( customVenueAdapter );
            if (customVenueList != null) {
                activity.runOnUiThread(() -> customVenueAdapter.updateData( customVenueList ));
            }
            CustomVenueView.this.removeAllViews();
            CustomVenueView.this.addView( view );
        } );    }

    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager( new LinearLayoutManager( context, LinearLayoutManager.HORIZONTAL, false ) );
        int spacing = getResources().getDimensionPixelSize( com.intuit.ssp.R.dimen._10ssp );
        recyclerView.addItemDecoration( new HorizontalSpaceItemDecoration( spacing ) );
//        recyclerView.setNestedScrollingEnabled( false );
        recyclerView.offsetChildrenHorizontal( 1 );
    }

    public void setupData(List<CustomVenueModel> venue, Activity activity, FragmentManager fragmentManager) {
        this.customVenueList = venue;
        this.activity = activity;
        this.supportFragmentManager = fragmentManager;
        if (customVenueList == null) {return;}
        if (binding == null) { return; }
        activity.runOnUiThread(() -> customVenueAdapter.updateData( customVenueList ));
    }

    public class CustomVenueAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private final Activity activity;
        private final FragmentManager fragmentManager;

        public CustomVenueAdapter(Activity activity, FragmentManager fragmentManager) {
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
                if (model.getVenue() != null) {
                    Graphics.loadImage(model.getVenue().getCover(), viewHolder.mBinding.imgCover);
                    viewHolder.mBinding.venueContainer.setVenueDetail(model.getVenue());
                }
            });

            viewHolder.mBinding.tvView.setOnClickListener(view -> {
                if (model.getVenue() != null) {
                    Graphics.openVenueDetail(activity, model.getVenue().getId());
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
