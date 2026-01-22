package com.whosin.app.ui.controller.promotionalView;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.whosin.app.R;
import com.whosin.app.comman.AppDelegate;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.HorizontalSpacingItemDecoration;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemPromotionalImageViewBinding;
import com.whosin.app.databinding.ItemPromotionalVideoViewBinding;
import com.whosin.app.databinding.PromotionalBannerViewBinding;
import com.whosin.app.service.manager.PromotionalBannerManager;
import com.whosin.app.service.models.BannerModel;
import com.whosin.app.service.models.PromotionalBannerModels.PromotionalListModel;
import com.whosin.app.service.models.PromotionalBannerModels.PromotionalMainModel;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PromotionalBannerView extends ConstraintLayout {

    private PromotionalBannerViewBinding binding;

    public final PromotionalListAdapter<BannerModel>  promotionalListAdapter = new PromotionalListAdapter<>();

    private Context context;

    public Activity activity;

    private PagerSnapHelper snapHelper = new PagerSnapHelper ();

    private int bannerPosition = -1;

    public boolean isNotSetSpace = false;

    public PromotionalBannerView(Context context) {
        this(context, null);
    }

    public PromotionalBannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PromotionalBannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PromotionalBanner, 0, 0);
        isNotSetSpace = a.getBoolean(R.styleable.PromotionalBanner_isAddSpace,false);

        this.context = context;

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.promotional_banner_view, this, (view, resid, parent) -> {
            binding = PromotionalBannerViewBinding.bind(view);
            if (isNotSetSpace){
               setupRecycleHorizontalManager();
            }else {
                setupRecycleHorizontalManager(binding.promotionalRecycleView);
            }

            binding.promotionalRecycleView.setAdapter(promotionalListAdapter);
            snapHelper.attachToRecyclerView(binding.promotionalRecycleView);

            if (PromotionalBannerManager.shared.getIsPromotionalBanner()) {
                PromotionalMainModel mainModel = PromotionalBannerManager.shared.getPromotionalMainModel();

                if (mainModel != null) {

                    List<PromotionalListModel> list = mainModel.getList();
                    if (list != null && !list.isEmpty()) {
                        if (bannerPosition == -1) {
                            bannerPosition = new Random().nextInt(list.size());
                        }

                        PromotionalListModel listModel = list.get(bannerPosition);
                        if (listModel != null && !listModel.getBanners().isEmpty()) {

                            binding.promotionalRecycleView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    binding.promotionalRecycleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    setRatio(listModel);
                                    promotionalListAdapter.updateData(listModel.getBanners());
                                    ViewGroup parent = (ViewGroup) binding.promotionalRecycleView.getParent();
                                    if (parent != null) parent.requestLayout();
                                }
                            });
                        }
                    }
                }
            }


            binding.promotionalRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                }


                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager == null) return;

                    int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                    int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

                    // Manage video playback for visible items
                    for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                        if (viewHolder instanceof PromotionalListAdapter.RaynaVideoHolder) {
                            View itemView = viewHolder.itemView;
                            if (UiUtils.isView90PercentVisibleHorizontally(recyclerView, itemView)) {
                                ((PromotionalListAdapter.RaynaVideoHolder) viewHolder).startVideo();
                            } else {
                                ((PromotionalListAdapter.RaynaVideoHolder) viewHolder).pauseVideo();
                            }
                        }
                    }
                }
            });

            PromotionalBannerView.this.removeAllViews();
            PromotionalBannerView.this.addView(view);
        });
    }


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Activity Or Fragment Lifecycle
    // --------------------------------------

    private void setupFragmentLifecycleCallback(Fragment fragment) {
        fragment.getViewLifecycleOwner().getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                if (promotionalListAdapter != null) {
                    promotionalListAdapter.resumeAllVideos();
                }
            }

            @Override
            public void onPause(@NonNull LifecycleOwner owner) {
                if (promotionalListAdapter != null) {
                    promotionalListAdapter.pauseAllVideos();
                }
            }

            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                if (promotionalListAdapter != null) {
                    promotionalListAdapter.releaseAllPlayers();
                }
            }
        });
    }

    public void setupAppLifeCycleCallback(View itemView, Class<? extends Activity> targetActivityClass) {
        ((AppDelegate) itemView.getContext().getApplicationContext())
                .registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

                    @Override
                    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

                    @Override
                    public void onActivityStarted(@NonNull Activity activity) {}

                    @Override
                    public void onActivityResumed(@NonNull Activity activity) {
                        if (targetActivityClass.isInstance(activity)) {
                            promotionalListAdapter.resumeAllVideos();
                        }
                    }

                    @Override
                    public void onActivityPaused(@NonNull Activity activity) {
                        if (targetActivityClass.isInstance(activity)) {
                            promotionalListAdapter.pauseAllVideos();
                        }
                    }

                    @Override
                    public void onActivityStopped(@NonNull Activity activity) {}

                    @Override
                    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

                    @Override
                    public void onActivityDestroyed(@NonNull Activity activity) {
                        if (targetActivityClass.isInstance(activity)) {
                            promotionalListAdapter.releaseAllPlayers();
                        }
                    }
                });
    }



    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.offsetChildrenHorizontal(1);
    }

    public void setupRecycleHorizontalManager() {
        if (binding == null) return;
        binding.promotionalRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
        binding.promotionalRecycleView.addItemDecoration(new HorizontalSpacingItemDecoration(spacing));
        binding.promotionalRecycleView.setNestedScrollingEnabled(false);
        binding.promotionalRecycleView.setHasFixedSize(true);
        binding.promotionalRecycleView.offsetChildrenHorizontal(1);
    }



    private void setRatio(PromotionalListModel model) {
        String ratio = "16:9";
        if (model.getSize() != null && model.getSize().getRatio() != null) {
            ratio = model.getSize().getRatio();
        }

        if (!ratio.contains(":")) {
            ratio = "16:9";
        }

        try {
            String[] parts = ratio.split(":");
            float widthRatio = Float.parseFloat(parts[0]);
            float heightRatio = Float.parseFloat(parts[1]);

            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            int calculatedHeight = (int) (screenWidth * (heightRatio / widthRatio));

            ViewGroup.LayoutParams params = binding.promotionalRecycleView.getLayoutParams();
            params.height = calculatedHeight;
            binding.promotionalRecycleView.setLayoutParams(params);

            // 🆕 Force request layout on parent
            ViewGroup parent = (ViewGroup) binding.promotionalRecycleView.getParent();
            if (parent != null) {
                parent.requestLayout();
                parent.invalidate();
            }

            binding.promotionalRecycleView.requestLayout();
            binding.promotionalRecycleView.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int getLightColor(String hexColor) {
        int base = Color.parseColor(hexColor);
        int alpha = (int) (255 * 0.10f); // 10% opacity
        return Color.argb(alpha, Color.red(base), Color.green(base), Color.blue(base));

    }

    private void openView(BannerModel view) {
        if (view == null || view.getType().isEmpty()) return;
        Intent intent = null;
        switch (view.getType()) {
            case "link":
                try {
                    Uri webpage = Uri.parse(view.getTypeId());
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (webIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(webIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            case "ticket":
                intent = new Intent(context, RaynaTicketDetailActivity.class);
                intent.putExtra("ticketId", view.getTypeId());
                break;
            default:
                break;
        }

        if (intent != null) {
            activity.startActivity(intent);
        }

    }

    private int getCurrentSnappedPosition(RecyclerView recyclerView, PagerSnapHelper snapHelper) {
        if (recyclerView == null || snapHelper == null || recyclerView.getLayoutManager() == null) {
            return RecyclerView.NO_POSITION;
        }

        View snappedView = snapHelper.findSnapView(recyclerView.getLayoutManager());
        if (snappedView != null) {
            return recyclerView.getLayoutManager().getPosition(snappedView);
        }

        return RecyclerView.NO_POSITION;
    }



    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------


    public void seUpData(Activity activity, Fragment fragment) {
        if (binding == null) {
            return;
        }

        setupFragmentLifecycleCallback(fragment);

        if (PromotionalBannerManager.shared.getIsPromotionalBanner()) {
            PromotionalMainModel mainModel = PromotionalBannerManager.shared.getPromotionalMainModel();

            if (mainModel != null) {
                if (promotionalListAdapter == null) {
                    if (isNotSetSpace) {
                        setupRecycleHorizontalManager();
                    } else {
                        setupRecycleHorizontalManager(binding.promotionalRecycleView);
                    }
                    binding.promotionalRecycleView.setAdapter(promotionalListAdapter);
                }

                List<PromotionalListModel> list = mainModel.getList();
                if (list != null && !list.isEmpty()) {
                    if (bannerPosition == -1) {
                        bannerPosition = new Random().nextInt(list.size());
                    }

                    PromotionalListModel listModel = list.get(bannerPosition);
                    if (listModel != null && !listModel.getBanners().isEmpty()) {

                        binding.promotionalRecycleView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                // Remove the listener to prevent multiple calls
                                binding.promotionalRecycleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                // Set ratio and update data
                                setRatio(listModel);
                                promotionalListAdapter.updateData(listModel.getBanners());

                                // Force parent layout just in case
                                ViewGroup parent = (ViewGroup) binding.promotionalRecycleView.getParent();
                                if (parent != null) parent.requestLayout();
                            }
                        });
                    }
                }
            }
        }
    }

    public void seUpData(Activity activity) {
        if (binding == null) {
            return;
        }
//        setupAppLifeCycleCallback(activity);
        if (PromotionalBannerManager.shared.getIsPromotionalBanner()) {
            PromotionalMainModel mainModel = PromotionalBannerManager.shared.getPromotionalMainModel();
            if (mainModel != null){
                if (promotionalListAdapter == null){
                    if (isNotSetSpace){
                        setupRecycleHorizontalManager();
                    }else {
                        setupRecycleHorizontalManager(binding.promotionalRecycleView);
                    }
                    binding.promotionalRecycleView.setAdapter(promotionalListAdapter);
                }


                activity.runOnUiThread(() -> {
                    List<PromotionalListModel> list = mainModel.getList();
                    if (list != null && !list.isEmpty()) {
                        if (bannerPosition == -1) {
                            bannerPosition = new Random().nextInt(list.size());
                        }

                        PromotionalListModel listModel = list.get(bannerPosition);
                        if (listModel != null && !listModel.getBanners().isEmpty()) {
                            setRatio(listModel);
                            promotionalListAdapter.updateData(listModel.getBanners());
                        }
                    }
                });

            }
        }
    }

    public void onItemVisibilityChanged(boolean isVisible) {
        if (binding == null || binding.promotionalRecycleView == null) {
            Log.e("PromotionalBannerView", "Binding or RecyclerView is null. Skipping visibility handling.");
            return;
        }

        int position = getCurrentSnappedPosition(binding.promotionalRecycleView, snapHelper);
        if (isVisible) {
            RecyclerView.ViewHolder holder = binding.promotionalRecycleView.findViewHolderForAdapterPosition(position);
            if (holder instanceof PromotionalListAdapter.RaynaVideoHolder) {
                ((PromotionalListAdapter.RaynaVideoHolder) holder).startVideo();
            }
        } else {
            RecyclerView.ViewHolder holder = binding.promotionalRecycleView.findViewHolderForAdapterPosition(position);
            if (holder instanceof PromotionalListAdapter.RaynaVideoHolder) {
                ((PromotionalListAdapter.RaynaVideoHolder) holder).pauseVideo();
            }
        }

    }


    public void relaseAllplayer() {
        if (promotionalListAdapter != null && binding != null) {
            promotionalListAdapter.releaseAllPlayers();
            promotionalListAdapter.updateData(new ArrayList<>());

            binding.promotionalRecycleView.getRecycledViewPool().clear();
            binding.promotionalRecycleView.setAdapter(null);
            binding.promotionalRecycleView.setAdapter(promotionalListAdapter);

            Preferences.shared.setBoolean("isMute", true);
        }
    }




    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class PromotionalListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private final List<ExoPlayer> players = new ArrayList<>();

        private final List<Integer> playingPositions = new ArrayList<>();

        private final Handler handler = new Handler(Looper.getMainLooper());

        private boolean isPlayerInitialized = false;


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 1:
                    view = UiUtils.getViewBy(parent, R.layout.item_promotional_image_view);
                    break;
                case 2:
                    view = UiUtils.getViewBy(parent, R.layout.item_promotional_video_view);
                    break;
                default:
                    view = UiUtils.getViewBy(parent, R.layout.item_promotional_image_view);
                    break;
            }

            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (Graphics.getScreenWidth(activity) * (getItemCount() > 1 ? 0.89 : 0.93));
            view.setLayoutParams(params);

            if (viewType == 2) {
                return new RaynaVideoHolder(view);
            } else {
                return new RaynaImageHolder(view);
            }
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            BannerModel model = (BannerModel) getItem(position);
            if (getItemViewType(position) == 1) {
                ((RaynaImageHolder) holder).setupData(model);
            } else if (getItemViewType(position) == 2) {
                RaynaVideoHolder videoHolder = (RaynaVideoHolder) holder;
                videoHolder.setDetail(model);
                videoHolder.setupData(model.getMediaUrls().get(0));
                if (!players.contains(videoHolder.player)) {
                    players.add(videoHolder.player);
                }

            }
        }

        public int getItemViewType(int position) {
            BannerModel model = (BannerModel) getItem(position);
            if (isVideo(model.getMediaUrls().get(0))) {
                return 2;
            } else {
                return 1;
            }
        }

        public void pauseAllVideos() {
            playingPositions.clear();
            for (int i = 0; i < players.size(); i++) {
                ExoPlayer player = players.get(i);
                if (player.isPlaying()) {
                    playingPositions.add(i);
                    player.pause();
                }
            }
        }

        public void resumeAllVideos() {
            for (int position : playingPositions) {
                if (position >= 0 && position < players.size()) {
                    players.get(position).play();
                }
            }
            playingPositions.clear();
        }

        public void releaseAllPlayers() {
            for (ExoPlayer player : players) {
                player.release();
            }
            players.clear();
            playingPositions.clear();
        }

        private boolean isVideo(String url) {
            return url.endsWith(".mp4") || url.endsWith(".avi") || url.endsWith(".mov");
        }

        public class RaynaImageHolder extends RecyclerView.ViewHolder {

            private final ItemPromotionalImageViewBinding binding;

            public RaynaImageHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemPromotionalImageViewBinding.bind(itemView);

            }

            public void setupData(BannerModel model) {

                Graphics.loadImage(activity,model.getMediaUrls().get(0),binding.imageView);

                if (!TextUtils.isEmpty(model.getTitle())) {
                    binding.tvTitle.setText(model.getTitle());
                    binding.tvTitle.setVisibility(View.VISIBLE);
                } else {
                    binding.tvTitle.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(model.getDescription())) {
                    binding.tvSubTitle.setText(model.getDescription());
                    binding.tvSubTitle.setVisibility(View.VISIBLE);
                } else {
                    binding.tvSubTitle.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(model.getButtonText())) {
                    binding.btnBookNow.setText(model.getButtonText());
                    binding.btnBookNow.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(model.getButtonTint())){
                        String apiColor = model.getButtonTint();
                        binding.btnBookNow.setTextColor(Color.parseColor(apiColor));
                        binding.btnRoundBg.setBackgroundColor(getLightColor(apiColor));
                    }
                } else {
                    binding.btnBookNow.setVisibility(View.GONE);
                }

                binding.getRoot().setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    openView(model);
                });

                binding.btnRoundBg.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    openView(model);
                });
            }

        }

        public class RaynaVideoHolder extends RecyclerView.ViewHolder {

            private final ItemPromotionalVideoViewBinding binding;

            private ExoPlayer player;

            public RaynaVideoHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemPromotionalVideoViewBinding.bind(itemView);
                player = new ExoPlayer.Builder(itemView.getContext()).build();
                binding.eventVideoView.setPlayer(player);

                binding.iconSoundOff.setChecked(Preferences.shared.getBoolean("isMute"));
                boolean isMute = Preferences.shared.getBoolean("isMute");
                player.setVolume(isMute ? 0f : 1f);


                player.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        if (playbackState == Player.STATE_ENDED) {
                            player.seekTo(0);
                            player.play();

                        }
                    }
                });

                binding.iconSoundOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (player != null) {
                        player.setVolume(!isChecked ? 1f : 0f);
                        Preferences.shared.setBoolean("isMute", isChecked);
                    }
                });

                binding.eventVideoView.setOnClickListener(v -> {
                    if (player.isPlaying()) {
                        pauseVideo();
                    } else {
                        startVideo();
                    }
                });



            }

            public void setupData(String videoUrl) {
//                player.setMediaItem(MediaItem.fromUri(videoUrl));
//                player.prepare();
//                player.pause();
                if (!isPlayerInitialized) {
                    player.setMediaItem(MediaItem.fromUri(videoUrl));
                    player.prepare();
                    isPlayerInitialized = true;
                }
                // Just pause (if needed) to keep it ready
                player.pause();
            }

            public void setDetail(BannerModel model){
                if (!TextUtils.isEmpty(model.getTitle())) {
                    binding.tvTitle.setText(model.getTitle());
                    binding.tvTitle.setVisibility(View.VISIBLE);
                } else {
                    binding.tvTitle.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(model.getDescription())) {
                    binding.tvSubTitle.setText(model.getDescription());
                    binding.tvSubTitle.setVisibility(View.VISIBLE);
                } else {
                    binding.tvSubTitle.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(model.getButtonText())) {
                    binding.btnBookNow.setText(model.getButtonText());
                    binding.btnBookNow.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(model.getButtonTint())){
                        String apiColor = model.getButtonTint();
                        binding.btnBookNow.setTextColor(Color.parseColor(apiColor));
                        binding.btnRoundBg.setBackgroundColor(getLightColor(apiColor));
                    }
                } else {
                    binding.btnBookNow.setVisibility(View.GONE);
                }

                binding.getRoot().setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    openView(model);
                });

                binding.btnRoundBg.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    openView(model);
                });
            }

            public void startVideo() {
                if (!player.isPlaying()) {
                    player.play();
                    binding.btnPlayAndPause.setImageResource(R.drawable.icon_play_video);
                    binding.btnPlayAndPause.setVisibility(View.VISIBLE);
                    handler.postDelayed(() -> binding.btnPlayAndPause.setVisibility(View.GONE), 1000);
                }
//                player.play();
//
//                binding.btnPlayAndPause.setImageResource(R.drawable.icon_play_video);
//                binding.btnPlayAndPause.setVisibility(View.VISIBLE);
//
//                handler.postDelayed(() -> binding.btnPlayAndPause.setVisibility(View.GONE), 1000);
            }

            public void pauseVideo() {
                player.pause();
                showPauseButton();
            }

            private void showPauseButton() {
                binding.btnPlayAndPause.setImageResource(R.drawable.icon_pause_video);
                binding.btnPlayAndPause.setVisibility(View.VISIBLE);
            }

        }
    }



    // endregion
    // --------------------------------------


}