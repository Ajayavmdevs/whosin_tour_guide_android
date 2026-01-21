package com.whosin.app.ui.activites.venue;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.king.image.imageviewer.ImageViewer;
import com.king.image.imageviewer.loader.GlideImageLoader;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.GridSpacingItemDecoration;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityVenueGallaryBinding;
import com.whosin.app.databinding.ItemGalleryGridBinding;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class VenueGalleryActivity extends BaseActivity {

    private VenueGalleryAdapter adapter;
    private ActivityVenueGallaryBinding binding;
    List<String> imageList = new ArrayList<>();
    private boolean isSelectCoverImage = false;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        binding.tvTitle.setText(getValue("gallery"));

        String json = getIntent().getStringExtra("galleries");

        isSelectCoverImage = getIntent().getBooleanExtra("isSelectCoverImage",false);


        Type type = new TypeToken<List<String>>() {
        }.getType();
        imageList = new Gson().fromJson(json, type);
        setGalleryImageAdapter();
        adapter.updateData(imageList);

        adapter.notifyDataSetChanged();
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

    }

    @Override
    protected void setListeners() {
        Glide.with(activity).load(R.drawable.icon_close_btn).into(binding.ivClose);
        binding.ivClose.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityVenueGallaryBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setGalleryImageAdapter() {
        if (adapter == null) {
            adapter = new VenueGalleryAdapter(imageList, activity);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, 3);
            binding.galleryRecycler.setLayoutManager(gridLayoutManager);
            binding.galleryRecycler.setAdapter(adapter);

            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.tab_padding);
            binding.galleryRecycler.addItemDecoration(new GridSpacingItemDecoration(activity, 3, spacingInPixels, false));
        }

    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class VenueGalleryAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {


        private List<String> gallery;
        private Activity context;

        public VenueGalleryAdapter(List<String> gallery, Activity context) {

            this.gallery = gallery;
            this.context = context;
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_gallery_grid));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            String image = imageList.get(position);

            viewHolder.binding.imgGallery.getRootView().setOnClickListener(view -> {
                if (isSelectCoverImage){
                    Intent intent = new Intent();
                    intent.putExtra("venueCoverImage", image);
                    setResult(RESULT_OK, intent);
                    finish();
                }else {
                    ImageViewer.load(imageList).selection(position).imageLoader(new GlideImageLoader()).indicator(true).start(VenueGalleryActivity.this);
                }

            });


            Graphics.loadImage(image, viewHolder.binding.imgGallery);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ItemGalleryGridBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemGalleryGridBinding.bind(itemView);
            }
        }
    }


    // --------------------------------------
    // endregion
}