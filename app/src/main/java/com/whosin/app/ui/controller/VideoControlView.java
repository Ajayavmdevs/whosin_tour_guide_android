package com.whosin.app.ui.controller;

import static com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.mediacodec.MediaCodecInfo;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.util.MimeTypes;
import com.whosin.app.R;
import com.whosin.app.comman.AppDelegate;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.VideoCache;
import com.whosin.app.comman.ui.roundcornerlayout.RoundCornerConstraintLayout;
import com.whosin.app.databinding.LayoutVideoComponentBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.VideoComponentModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.ui.activites.home.MainHomeActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class VideoControlView extends ConstraintLayout {

    private LayoutVideoComponentBinding binding;
    private Context context;
    private Activity activity;
    public SimpleExoPlayer exoPlayer;
    public String title = "";
    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    private Runnable progressUpdateRunnable;
    private final Handler handler = new Handler();
    private boolean isVisibleNow = false;

    private List<VideoComponentModel> videos = new ArrayList<>();


    public VideoControlView(Context context) {
        this(context, null);
    }

    public VideoControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.layout_video_component, this, (view, resid, parent) -> {
            binding = LayoutVideoComponentBinding.bind(view);
            binding.tvView.setText(Utils.getLangValue("view"));
            if (videos != null) {
                bindMedia();
            }
            VideoControlView.this.removeAllViews();
            VideoControlView.this.addView(view);
        });
    }

    public void bindMedia() {
        loadThumbImg();
        new Handler().postDelayed(() -> binding.storyProgressBar.setNumProgressBars(videos.size()), 500);

        DefaultRenderersFactory rf = new DefaultRenderersFactory(context.getApplicationContext()).setExtensionRendererMode(EXTENSION_RENDERER_MODE_PREFER).setMediaCodecSelector(
                        (mimeType, requiresSecureDecoder, requiresTunnelingDecoder) -> {
                            List<MediaCodecInfo> decoderInfos = MediaCodecSelector.DEFAULT
                                    .getDecoderInfos(mimeType, requiresSecureDecoder, requiresTunnelingDecoder);
                            if (MimeTypes.VIDEO_H264.equals(mimeType)) {
                                decoderInfos = new ArrayList<>(decoderInfos);
                                Collections.reverse(decoderInfos);
                            }
                            return decoderInfos;
                        });


        exoPlayer = new SimpleExoPlayer.Builder(context, rf).build();
        Thread backgroundThread = new Thread(() -> {
            DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true);
            DefaultDataSource.Factory defaultDataSourceFactory = new DefaultDataSource.Factory(context, httpDataSourceFactory);
            CacheDataSource.Factory cacheDataSourceFactory = new CacheDataSource.Factory().setCache(VideoCache.shared(context)).setUpstreamDataSourceFactory(defaultDataSourceFactory).setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
            List<MediaSource> mediaSourceList = new ArrayList<>();
            for (VideoComponentModel p : videos) {
                MediaItem mediaItem = MediaItem.fromUri(p.getVideoUrl());
                ProgressiveMediaSource source = new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem);
                mediaSourceList.add(source);
            }

            if (Utils.isValidActivity(activity)) {
                activity.runOnUiThread(() -> {
                    binding.videoView.setPlayer(exoPlayer);
                    exoPlayer.addMediaSources(mediaSourceList);
                    exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                    exoPlayer.prepare();
                    boolean isMute = Preferences.shared.getBoolean("isMute");
                    exoPlayer.setVolume(isMute ? 0f : 1f);
                    setupVenueInfo();
                    setListeners();
                });
            }
        });
        backgroundThread.start();

    }

    public void setupData(List<VideoComponentModel> videos, View itemView , Activity activity){
        this.activity = activity;
        this.videos = videos;
        setupAppLifeCycleCallback(itemView);
        if (binding == null){return;}
        bindMedia();
        if (!TextUtils.isEmpty(title)) binding.txtTitle.setText(title);
    }

    public void onItemVisibilityChanged(boolean isVisible) {
        if (isVisibleNow == isVisible) { return; }
        isVisibleNow = isVisible;
        if (isVisible) {
            startUpdatingProgress();
            playPlayer();
        } else {
            stopUpdatingProgress();
            pausePlayer();
        }
        updateVolume();
    }

    private void setupVenueInfo() {
        activity.runOnUiThread(() -> {
            if (!TextUtils.isEmpty(title)) binding.txtTitle.setText(title);


            int lastItemIndex = videos.size() - 1;
            int currentVideoIndex = exoPlayer.getCurrentMediaItemIndex();
            if (currentVideoIndex > lastItemIndex || currentVideoIndex < 0) { return; }
            VideoComponentModel videoModel = videos.get(currentVideoIndex);

            if (!TextUtils.isEmpty(videoModel.getTicketId())) {
                if (videoModel.getTicketDetailModel() != null) {
                    binding.venueContainer.setTicketDetail(videoModel.getTicketDetailModel());
                } else if (SessionManager.shared.geHomeBlockData() != null) {
                    Optional<RaynaTicketDetailModel> ticketModel = SessionManager.shared.geHomeBlockData().getTickets().stream().filter(p -> p.getId().equals(videoModel.getTicketId())).findFirst();
                    ticketModel.ifPresent(raynaTicketDetailModel -> binding.venueContainer.setTicketDetail(raynaTicketDetailModel));
                }

            } else {
                if (videoModel.getVenue() != null) {
                    binding.venueContainer.setVenueDetail(videoModel.getVenue());
                } else if (videoModel.getVenueId() != null && SessionManager.shared.geHomeBlockData() != null) {
                    Optional<VenueObjectModel> venueObjectModel1 = SessionManager.shared.geHomeBlockData().getVenues().stream().filter(p -> p.getId().equals(videoModel.getVenueId())).findFirst();
                    venueObjectModel1.ifPresent(binding.venueContainer::setVenueDetail);
                }
            }

        });
    }

    private void setListeners() {
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                setupVenueInfo();
                binding.storyProgressBar.setAllProgressBarsProgress(0);
            }
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                binding.btnReplay.setVisibility(playbackState == Player.STATE_ENDED ? View.VISIBLE : View.GONE);
                if (playbackState == Player.STATE_BUFFERING || playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED){
                    loadThumbImg();
                }
                binding.progressBar.setVisibility((playbackState == Player.STATE_BUFFERING || playbackState == Player.STATE_IDLE) ? View.VISIBLE : View.GONE);
                if (playbackState == Player.STATE_READY){
                    exoPlayer.setPlayWhenReady(true);
                    binding.ivCustom.setVisibility(GONE);
                   startUpdatingProgress();
                }
            }
        });


        binding.tvView.setOnClickListener(v -> {
            if (activity == null) { return; }
            int lastItemIndex = videos.size() - 1;
            int currentVideoIndex = exoPlayer.getCurrentMediaItemIndex();
            if (currentVideoIndex > lastItemIndex || currentVideoIndex < 0) { return; }
            VideoComponentModel videoModel = videos.get(currentVideoIndex);
            if (!TextUtils.isEmpty(videoModel.getTicketId())){
                activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId",videoModel.getTicketId()));
            }

        });

        binding.btnReplay.setOnClickListener(v -> {
            if (exoPlayer != null) { exoPlayer.seekTo(0, 0); }
            playPlayer();
            binding.btnReplay.setVisibility(View.GONE);
            binding.ivCustom.setVisibility(GONE);
        });

        Runnable mLongPressed = this::pausePlayer;

        View.OnTouchListener touchListener = (v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                handler.postDelayed(mLongPressed, 300);
            }
            else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                handler.removeCallbacks(mLongPressed);
                if (event.getEventTime() - event.getDownTime() > 300) {
                    playPlayer();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                handler.removeCallbacks(mLongPressed);
                if (event.getEventTime() - event.getDownTime() > 400) {
                    playPlayer();
                } else {
                    if (v.getId() == binding.skip.getId()) {
                        exoPlayer.seekToNextMediaItem();
                    } else {
                        exoPlayer.seekToPreviousMediaItem();
                    }
                }
            }
            return false;
        };

        binding.skip.setOnTouchListener(touchListener);
        binding.reverse.setOnTouchListener(touchListener);

        binding.iconSoundOff.setChecked(Preferences.shared.getBoolean("isMute"));
        binding.iconSoundOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (exoPlayer != null) {
                exoPlayer.setVolume(!isChecked ? 1f : 0f);
                Preferences.shared.setBoolean("isMute", isChecked);
            }
        });
    }

    public void playPlayer() {
        if (isVisibleNow) {
            if (exoPlayer == null) { return; }
            try {
                exoPlayer.setPlayWhenReady(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pausePlayer() {
        if (exoPlayer == null) { return; }
        try {
            exoPlayer.setPlayWhenReady(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.clearMediaItems();
            exoPlayer.release();
        }
    }

    public void startUpdatingProgress() {
        progressUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateSeekBar();
                progressHandler.postDelayed(this, 500); // Update every second (adjust as needed)
            }
        };
        progressHandler.post(progressUpdateRunnable);
    }

    public void stopUpdatingProgress() {
        progressHandler.removeCallbacks(progressUpdateRunnable);
    }

    private void updateSeekBar() {
        if (activity == null || exoPlayer == null) { return; }
        activity.runOnUiThread(() -> {
            int currentIndex = exoPlayer.getCurrentMediaItemIndex();
            long duration = exoPlayer.getDuration();
            long position = exoPlayer.getCurrentPosition();
            int progressPercentage = (int) (position * 100 / duration);
            binding.storyProgressBar.setProgressBarProgress(currentIndex, progressPercentage);
        });
    }

    private void updateVolume() {
        if (binding == null) { return; }
        boolean isMute = Preferences.shared.getBoolean("isMute");
        binding.iconSoundOff.setChecked(isMute);
    }

    private void loadThumbImg() {
        if (binding == null) return;
        binding.ivCustom.setVisibility(View.VISIBLE);

        if (exoPlayer == null || videos == null || videos.isEmpty()) {
            binding.ivCustom.setVisibility(View.GONE);
            return;
        }
        int currentVideoIndex;
        try {
            currentVideoIndex = exoPlayer.getCurrentMediaItemIndex();
        } catch (Exception e) {
            e.printStackTrace();
            binding.ivCustom.setVisibility(View.GONE);
            return;
        }

        int lastItemIndex = videos.size() - 1;
        if (currentVideoIndex > lastItemIndex || currentVideoIndex < 0) {
            binding.ivCustom.setVisibility(View.GONE);
            return;
        }

        VideoComponentModel videoModel = videos.get(currentVideoIndex);
        if (videoModel != null && !TextUtils.isEmpty(videoModel.getThumb())) {
            Graphics.loadImage(videoModel.getThumb(), binding.ivCustom);
        } else {
            binding.ivCustom.setVisibility(View.GONE);
        }
    }


    private void setupAppLifeCycleCallback(View itemView) {
        ((AppDelegate) itemView.getContext().getApplicationContext()).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}
            @Override
            public void onActivityStarted(@NonNull Activity activity) {}
            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                if (activity instanceof MainHomeActivity) {
                    playPlayer();
                    updateVolume();
                }
            }
            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                if (activity instanceof MainHomeActivity) {
                    pausePlayer();
                }
            }
            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                if (activity instanceof MainHomeActivity) {
                    pausePlayer();
                }
            }
            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) { }
            @Override
            public void onActivityDestroyed(@NonNull Activity activity) { }
        });
    }
}
