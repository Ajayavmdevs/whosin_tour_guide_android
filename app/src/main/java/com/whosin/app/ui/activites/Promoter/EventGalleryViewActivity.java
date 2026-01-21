package com.whosin.app.ui.activites.Promoter;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityEventGalleryViewBinding;
import com.whosin.app.databinding.ItemEventFullScreenImageDesignBinding;
import com.whosin.app.databinding.ItemEventFullScreenVideoDesignBinding;
import com.whosin.app.databinding.ItemEventImageDesignBinding;
import com.whosin.app.databinding.ItemEventVideoDesignBinding;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventGalleryViewActivity extends BaseActivity {

    private ActivityEventGalleryViewBinding binding;

    private final EventGalleryAdapter<RatingModel> eventGalleryAdapter = new EventGalleryAdapter<>();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        binding.eventGalleryView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.eventGalleryView.setAdapter(eventGalleryAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.eventGalleryView);

        int position = getIntent().getIntExtra("scrollToPosition",0);
        String eventModel = getIntent().getStringExtra( "model");
        if (!TextUtils.isEmpty(eventModel)){
            PromoterEventModel model = new Gson().fromJson( eventModel, PromoterEventModel.class );
            if (!model.getEventGallery().isEmpty()){
                List<RatingModel> list = model.getEventGallery().stream().map(RatingModel::new).collect(Collectors.toList());
                eventGalleryAdapter.updateData(list);
            }else {
                List<RatingModel> tmpList = new ArrayList<>();
                if ("custom".equals(model.getVenueType()) && model.getCustomVenue() != null) {
                    tmpList.add(new RatingModel(model.getCustomVenue().getImage()));
                } else if (model.getVenue() != null && !TextUtils.isEmpty(model.getImage())) {
                    tmpList.add(new RatingModel(model.getImage()));
                    eventGalleryAdapter.updateData(tmpList);

                }
            }

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
                    if (viewHolder instanceof EventGalleryAdapter.EventVideoHolder) {
                        View itemView = viewHolder.itemView;
                        if (isView90PercentVisibleHorizontally(recyclerView, itemView)) {
                            ((EventGalleryAdapter.EventVideoHolder) viewHolder).startVideo();
                        } else {
                            ((EventGalleryAdapter.EventVideoHolder) viewHolder).pauseVideo();
                        }
                    }
                }
            }
        });


        binding.ivClose.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            finish();
        });


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityEventGalleryViewBinding.inflate(getLayoutInflater());
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
     }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void controlVideoPlayback(boolean shouldPlay) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.eventGalleryView.getLayoutManager();
        if (layoutManager == null) return;

        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

        for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
            RecyclerView.ViewHolder viewHolder = binding.eventGalleryView.findViewHolderForAdapterPosition(i);
            if (viewHolder instanceof EventGalleryAdapter.EventVideoHolder) {
                if (shouldPlay) {
                    ((EventGalleryAdapter.EventVideoHolder) viewHolder).startVideo();
                } else {
                    ((EventGalleryAdapter.EventVideoHolder) viewHolder).pauseVideo();
                }
            }
        }
    }

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


    private class EventGalleryAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private final List<ExoPlayer> players = new ArrayList<>();

        private final List<Integer> playingPositions = new ArrayList<>();


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 1:
                    return new EventImageHolder(UiUtils.getViewBy(parent, R.layout.item_event_full_screen_image_design));
                case 2:
                    return new EventVideoHolder(UiUtils.getViewBy(parent, R.layout.item_event_full_screen_video_design));
                default:
                    return new EventImageHolder(UiUtils.getViewBy(parent, R.layout.item_event_full_screen_image_design));
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RatingModel model = (RatingModel) getItem(position);
            if (getItemViewType(position) == 1) {
                ((EventImageHolder) holder).setupData(model.getImage());
            } else if (getItemViewType(position) == 2) {
                EventVideoHolder videoHolder = (EventVideoHolder) holder;
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


        public class EventImageHolder extends RecyclerView.ViewHolder {

            private final ItemEventFullScreenImageDesignBinding binding;

            public EventImageHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemEventFullScreenImageDesignBinding.bind(itemView);
            }

            public void setupData(String imageUrl) {
                Graphics.loadImageWithFirstLetter(imageUrl, binding.eventImage, "W");
            }
        }

        public class EventVideoHolder extends RecyclerView.ViewHolder {

            private final ItemEventFullScreenVideoDesignBinding binding;

            private ExoPlayer player;

            public EventVideoHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemEventFullScreenVideoDesignBinding.bind(itemView);
                player = new ExoPlayer.Builder(itemView.getContext()).build();
                binding.eventVideoView.setPlayer(player);

                binding.iconSoundOff.setChecked(Preferences.shared.getBoolean("isMuteEventVideoFromGallery"));
                boolean isMute = Preferences.shared.getBoolean("isMuteEventVideoFromGallery");
                player.setVolume(isMute ? 0f : 1f);

                binding.eventVideoView.setOnTouchListener((view, motionEvent) -> {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (player.isPlaying()) {
                                player.pause();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            if (!player.isPlaying()) {
                                player.play();
                            }
                            break;
                    }
                    return true;
                });

                player.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        if (playbackState == Player.STATE_ENDED) {
                            player.seekTo(0);
                            player.pause();
                        }
                    }
                });


                binding.iconSoundOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (player != null) {
                        player.setVolume(!isChecked ? 1f : 0f);
                        Preferences.shared.setBoolean("isMuteEventVideoFromGallery", isChecked);
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
            }

            public void pauseVideo() {
                player.pause();
            }

        }

    }



    // --------------------------------------
    // endregion
}