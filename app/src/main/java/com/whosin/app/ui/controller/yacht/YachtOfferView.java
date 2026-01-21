package com.whosin.app.ui.controller.yacht;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemYachtComponentBinding;
import com.whosin.app.databinding.ItemYachtSpecificationsBinding;
import com.whosin.app.databinding.PagerItemBinding;
import com.whosin.app.databinding.YachtFeatureViewBinding;
import com.whosin.app.databinding.YachtOfferViewBinding;
import com.whosin.app.service.models.YachtClubModel;
import com.whosin.app.service.models.YachtDetailModel;
import com.whosin.app.service.models.YachtFeatureModel;
import com.whosin.app.service.models.YachtsOfferModel;
import com.whosin.app.ui.activites.venue.VenueShareActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class YachtOfferView extends ConstraintLayout {
    private YachtOfferViewBinding binding;
    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;
    public YachtOfferAdapter<YachtsOfferModel> offerAdapter;
    private Handler handler = new Handler();

    private YachtClubModel yachtClubModel;

    public YachtOfferView(Context context) {
        this(context, null);
    }

    public YachtOfferView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YachtOfferView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.yacht_offer_view, this, (view, resid, parent) -> {
            binding = YachtOfferViewBinding.bind(view);
            setupRecycleHorizontalManager(binding.yachtRecycler);
            offerAdapter = new YachtOfferAdapter<>(activity, supportFragmentManager);
            binding.yachtRecycler.setAdapter(offerAdapter);
            if (yachtClubModel != null) {
                activity.runOnUiThread(() -> offerAdapter.updateData(yachtClubModel.getOffers()));
            }
            YachtOfferView.this.removeAllViews();
            YachtOfferView.this.addView(view);
        });
    }

    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        //int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
       // recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
    }

    public void setupData(YachtClubModel yachtClubModel, Activity activity, FragmentManager fragmentManager) {
        this.yachtClubModel = yachtClubModel;
        this.activity = activity;
        this.supportFragmentManager = fragmentManager;
        if (yachtClubModel == null) {
            return;
        }
        if (binding == null) {
            return;
        }
        activity.runOnUiThread(() -> {
            offerAdapter.updateData(yachtClubModel.getOffers());
        });
    }


    public class YachtOfferAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        private final Activity activity;
        public YachtOfferAdapter(Activity activity, FragmentManager fragmentManager) {
            this.activity = activity;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new YachtOfferAdapter.ViewHolder( UiUtils.getViewBy( parent, R.layout.item_yacht_offer ) );

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            YachtsOfferModel model = (YachtsOfferModel) getItem( position );
            if (model != null) {
                viewHolder.binding.tvName.setText( model.getTitle() );
                viewHolder.binding.tvDescription.setText( model.getDescription() );
                viewHolder.binding.tvDescription.post( () -> {
                    int lineCount = viewHolder.binding.tvDescription.getLineCount();
                    if (lineCount > 2) {
                        Utils.makeTextViewResizable( viewHolder.binding.tvDescription, 3, 3, ".. See More", true );
                    }
                } );
                viewHolder.setupData( yachtClubModel.getOffers());

                Optional<YachtDetailModel> model1 = yachtClubModel.getYachts().stream().filter(p -> p.getId().equals( model.getYachtId() ) ).findFirst();
                if(model1.isPresent()){
                    viewHolder.binding.featureRecycler.setupData("", model1.get().getFeatures(), activity,
                            supportFragmentManager, StaggeredGridLayoutManager.HORIZONTAL, 2);
                }


            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            public com.whosin.app.databinding.ItemYachtOfferBinding binding;
            public YachtSpecificationsAdapter yachtsOfferItemAdapter = new YachtSpecificationsAdapter();

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = com.whosin.app.databinding.ItemYachtOfferBinding.bind( itemView );
                binding.yachtsRecyler.setLayoutManager( new GridLayoutManager( activity, 3, LinearLayoutManager.VERTICAL, false ) );
                binding.yachtsRecyler.setAdapter( yachtsOfferItemAdapter );
            }

            public void setupData(List<YachtsOfferModel> bannerModels) {
                MyPagerAdapter adapter = new MyPagerAdapter( activity, bannerModels );
                binding.viewPager.setAdapter( adapter );
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int nextPage = binding.viewPager.getCurrentItem() + 1;
                            if (nextPage == adapter.getCount()) {
                                nextPage = 0;
                            }
                            binding.viewPager.setCurrentItem( nextPage, true );
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            handler.postDelayed( this, 4000 );
                        }
                    }
                };
                handler.postDelayed( runnable, 4000 );

                binding.viewPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        handler.removeCallbacks( runnable );
                        handler.postDelayed( runnable, 4000 );
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                } );
                binding.dotsIndicator.attachTo( binding.viewPager );
            }
        }
    }

    public static class YachtSpecificationsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_yacht_specifications));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            YachtFeatureModel model = (YachtFeatureModel)getItem( position );
            ViewHolder viewHolder = (ViewHolder) holder;

        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            ItemYachtSpecificationsBinding binding;
            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemYachtSpecificationsBinding.bind( itemView );
            }
        }
    }

    public class MyPagerAdapter extends PagerAdapter {
        private Context context;
        private List<YachtsOfferModel> bannerModels;
        public MyPagerAdapter(Context context, List<YachtsOfferModel> bannerModels) {
            this.context = context;
            this.bannerModels = bannerModels;
        }

        @Override
        public int getCount() {
            return bannerModels.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from( activity );
            PagerItemBinding binding = PagerItemBinding.inflate( inflater, container, false );

            YachtsOfferModel bannerModel = bannerModels.get( position );

            List<String> images = bannerModel.getImages();
            if (!images.isEmpty()) {
                String imageUrl = images.get(0);
                Glide.with(activity).load(imageUrl).into(binding.imageView);
            }


            Optional<YachtDetailModel> model = yachtClubModel.getYachts().stream().filter(p -> p.getId().equals( bannerModel.getYachtId() ) ).findFirst();
            if (model.isPresent()){
                binding.tvTitle.setText( model.get().getName() );
                binding.tvAddress.setText( model.get().getAbout() );
            }
            binding.ivMenu.setOnClickListener( view -> {
                Utils.preventDoubleClick( view );
                ArrayList<String> data = new ArrayList<>();
                data.add("Share");
                Graphics.showActionSheet(activity, "WhosIn", data, (data1, position1) -> {
                    switch (position1) {
                        case 0:
                            Intent intent = new Intent(activity, VenueShareActivity.class);
                            intent.putExtra( "yacht",new Gson().toJson( model) );
                            intent.putExtra( "type","yacht" );
                            activity.startActivity(intent);
                            break;

                    }
                });
            } );

            container.addView( binding.getRoot() );
            return binding.getRoot();

        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView( (View) object );
        }
    }

}
