package com.whosin.app.ui.controller.yacht;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.VerticalSpaceItemDecoration;
import com.whosin.app.databinding.ItemYachtsFeaturesBinding;
import com.whosin.app.databinding.YachtFeatureViewBinding;
import com.whosin.app.service.models.YachtFeatureModel;

import java.util.List;

public class YachtFeatureView extends ConstraintLayout {
    private YachtFeatureViewBinding binding;
    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;
    private List<YachtFeatureModel> yachtFeatureModels;
    private YachtFeatureAdapter<YachtFeatureModel> yachtFeatureAdapter;

    public YachtFeatureView(Context context) {
        this( context, null );
    }

    public YachtFeatureView(Context context, AttributeSet attrs) {
        this( context, attrs, 0 );
    }

    public YachtFeatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super( context, attrs, 0 );
        this.context = context;
        View view = LayoutInflater.from( context ).inflate( R.layout.yacht_feature_view, this );
        binding = YachtFeatureViewBinding.bind( view );
    }

    public void setupData(String title, List<YachtFeatureModel> yacht, Activity activity, FragmentManager fragmentManager, int orientation, int spanCount) {
        this.yachtFeatureModels = yacht;
        this.activity = activity;
        this.supportFragmentManager = fragmentManager;
        if (yachtFeatureModels == null) {
            return;
        }
        if (binding == null) {
            return;
        }
        if (title != null && !title.isEmpty()) {
            binding.tvTitle.setVisibility( VISIBLE );
            binding.tvTitle.setText( title );
        }

        yachtFeatureAdapter = new YachtFeatureAdapter<>( activity );

        binding.featureRecycler.setAdapter( yachtFeatureAdapter );

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager( spanCount, orientation );
        binding.featureRecycler.setLayoutManager( layoutManager );
        binding.featureRecycler.setNestedScrollingEnabled( true );
        int spacing = getResources().getDimensionPixelSize( com.intuit.ssp.R.dimen._4ssp );
        binding.featureRecycler.addItemDecoration( new VerticalSpaceItemDecoration( spacing ) );

        if(yachtFeatureModels != null && !yachtFeatureModels.isEmpty()){
            yachtFeatureAdapter.updateData( yachtFeatureModels );

        }


    }

    public class YachtFeatureAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        private final Activity activity;

        public YachtFeatureAdapter(Activity activity) {
            this.activity = activity;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_yachts_features ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            YachtFeatureModel model = (YachtFeatureModel) getItem( position );

            if (model != null) {
                viewHolder.binding.iconText.setText(model.getFeature());

                if (model.getIcon() != null && !model.getIcon().isEmpty()){
                    viewHolder.binding.ivFeatures.setVisibility(View.VISIBLE);
                    Graphics.loadImage(model.getIcon(), viewHolder.binding.ivFeatures);
                }else {
                    viewHolder.binding.ivFeatures.setVisibility(View.GONE);
                    viewHolder.binding.iconText.setText(String.format("%s %s", model.getEmoji(), model.getFeature()));

                }

//                if (model.getIcon() != null && !model.getIcon().isEmpty()) {
//                    viewHolder.binding.ivFeatures.setVisibility(VISIBLE);
//                    Graphics.loadImage( model.getIcon(),viewHolder.binding.ivFeatures );
//                } else if (model.getEmoji() != null && !model.getEmoji().isEmpty()) {
//                    viewHolder.binding.ivFeatures.setVisibility(VISIBLE);
//                    Graphics.loadImage( model.getEmoji(),viewHolder.binding.ivFeatures );
//                } else {
//                    viewHolder.binding.ivFeatures.setVisibility(GONE);
//                }
            }


        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemYachtsFeaturesBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemYachtsFeaturesBinding.bind( itemView );
            }
        }
    }
}
