package com.whosin.business.ui.activites.raynaTicket;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.gson.Gson;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Preferences;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.ActivityRaynaGalleryViewBinding;
import com.whosin.business.databinding.ItemEventFullScreenImageDesignBinding;
import com.whosin.business.databinding.ItemRaynaFullScreenVideoDesignBinding;
import com.whosin.business.service.models.RatingModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.ui.activites.comman.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RaynaGalleryViewActivity extends BaseActivity {

    private ActivityRaynaGalleryViewBinding binding;

    private final RaynaGalleryAdapter<RatingModel> eventGalleryAdapter = new RaynaGalleryAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        WindowInsetsController controller = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            controller = getWindow().getInsetsController();
        }
        if (controller != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                controller.hide(WindowInsets.Type.statusBars());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        }


        binding.eventGalleryView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.eventGalleryView.setAdapter(eventGalleryAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.eventGalleryView);

        int position = getIntent().getIntExtra("scrollToPosition", 0);
        String raynaModel = getIntent().getStringExtra("model");
        if (!TextUtils.isEmpty(raynaModel)) {
            RaynaTicketDetailModel raynaTicketDetailModel = new Gson().fromJson(raynaModel, RaynaTicketDetailModel.class);
            eventGalleryAdapter.updateData(raynaTicketDetailModel.getImages().stream().map(RatingModel::new).collect(Collectors.toList()));
            binding.eventGalleryView.smoothScrollToPosition(position);
        }

    }

    @Override
    protected void setListeners() {

        binding.eventGalleryView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

                // Video visibility logic
                for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                    if (viewHolder instanceof RaynaGalleryAdapter.RaynaVideoHolder) {
                        View itemView = viewHolder.itemView;
                        if (isView90PercentVisibleHorizontally(recyclerView, itemView)) {
                            ((RaynaGalleryAdapter.RaynaVideoHolder) viewHolder).startVideo();
                        } else {
                            ((RaynaGalleryAdapter.RaynaVideoHolder) viewHolder).pauseVideo();
                        }
                    }
                }
            }
        });


        binding.ivClose.setOnCheckedChangeListener((buttonView, isChecked) -> {
            finish();
        });


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityRaynaGalleryViewBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventGalleryAdapter != null) {
            eventGalleryAdapter.releaseAllPlayers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eventGalleryAdapter != null) {
            eventGalleryAdapter.pauseAllVideos();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (eventGalleryAdapter != null) {
            eventGalleryAdapter.resumeAllVideos();
        }
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private boolean isView90PercentVisibleHorizontally(RecyclerView recyclerView, View view) {
        int[] viewLocation = new int[2];
        int[] recyclerViewLocation = new int[2];

        view.getLocationOnScreen(viewLocation);
        recyclerView.getLocationOnScreen(recyclerViewLocation);

        int viewStart = viewLocation[0];
        int viewEnd = viewStart + view.getWidth();

        int recyclerViewStart = recyclerViewLocation[0];
        int recyclerViewEnd = recyclerViewStart + recyclerView.getWidth();

        int visibleStart = Math.max(viewStart, recyclerViewStart);
        int visibleEnd = Math.min(viewEnd, recyclerViewEnd);

        int visibleWidth = Math.max(0, visibleEnd - visibleStart);
        float visibilityPercentage = (visibleWidth / (float) view.getWidth()) * 100;

        return visibilityPercentage >= 95;
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class RaynaGalleryAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private final List<ExoPlayer> players = new ArrayList<>();

        private final List<Integer> playingPositions = new ArrayList<>();


        private final Handler handler = new Handler(Looper.getMainLooper());


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 1:
                    return new RaynaImageHolder(UiUtils.getViewBy(parent, R.layout.item_event_full_screen_image_design));
                case 2:
                    return new RaynaVideoHolder(UiUtils.getViewBy(parent, R.layout.item_rayna_full_screen_video_design));
                default:
                    return new RaynaImageHolder(UiUtils.getViewBy(parent, R.layout.item_event_image_design));
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RatingModel model = (RatingModel) getItem(position);
            if (getItemViewType(position) == 1) {
                ((RaynaImageHolder) holder).setupData(model.getImage());
            } else if (getItemViewType(position) == 2) {
                RaynaVideoHolder videoHolder = (RaynaVideoHolder) holder;
                videoHolder.setupData(model.getImage());
                if (!players.contains(videoHolder.player)) {
                    players.add(videoHolder.player);
                }
            }
        }

        public int getItemViewType(int position) {
            RatingModel model = (RatingModel) getItem(position);
            if (isVideo(model.getImage())) {
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

            private final ItemEventFullScreenImageDesignBinding binding;

            public RaynaImageHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemEventFullScreenImageDesignBinding.bind(itemView);

            }

            public void setupData(String imageUrl) {
                Graphics.loadImageWithFirstLetter(imageUrl, binding.eventImage, "W");
            }
        }

        public class RaynaVideoHolder extends RecyclerView.ViewHolder {

            private final ItemRaynaFullScreenVideoDesignBinding binding;

            private ExoPlayer player;

            public RaynaVideoHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemRaynaFullScreenVideoDesignBinding.bind(itemView);
                player = new ExoPlayer.Builder(itemView.getContext()).build();
                binding.eventVideoView.setPlayer(player);


                binding.iconSoundOff.setChecked(Preferences.shared.getBoolean("isMuteRaynaFullScreenVideo"));
                boolean isMute = Preferences.shared.getBoolean("isMuteRaynaFullScreenVideo");
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
                        Preferences.shared.setBoolean("isMuteRaynaFullScreenVideo", isChecked);
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
                player.setMediaItem(MediaItem.fromUri(videoUrl));
                player.prepare();
                player.pause();
            }

            public void startVideo() {
                player.play();

                binding.btnPlayAndPause.setImageResource(R.drawable.icon_play_video);
                binding.btnPlayAndPause.setVisibility(View.VISIBLE);

                handler.postDelayed(() -> binding.btnPlayAndPause.setVisibility(View.GONE), 1000);
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