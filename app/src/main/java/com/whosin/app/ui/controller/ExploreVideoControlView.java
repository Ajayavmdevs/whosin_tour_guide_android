package com.whosin.app.ui.controller;

import static com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.AppDelegate;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.VideoCache;
import com.whosin.app.databinding.ExploreVideoComponentBinding;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.BannerModel;
import com.whosin.app.service.models.CategoriesModel;
import com.whosin.app.service.models.CustomComponentModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.VideoComponentModel;
import com.whosin.app.ui.activites.explore.ExploreDetailActivity;
import com.whosin.app.ui.activites.home.MainHomeActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ExploreVideoControlView extends ConstraintLayout {

    private ExploreVideoComponentBinding binding;
    private Context context;
    private Activity activity;
    public SimpleExoPlayer exoPlayer;

    private boolean isVisibleNow = false;

    private List<CustomComponentModel> videos = new ArrayList<>();


    public ExploreVideoControlView(Context context) {
        this(context, null);
    }

    public ExploreVideoControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExploreVideoControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.explore_video_component, this, (view, resid, parent) -> {
            binding = ExploreVideoComponentBinding.bind(view);
               if (videos != null && !videos.isEmpty()) {
                if (videos.get(0).isVideoUrl()) {
                    bindMedia();
                } else {
                    hideLayouts(true);
                    Graphics.loadImage(videos.get(0).getMedia(), binding.ivCustom);
                }
            }

            setupVenueInfo();
            ExploreVideoControlView.this.removeAllViews();
            ExploreVideoControlView.this.addView(view);
        });

    }

    public void bindMedia() {
        hideLayouts(false);
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
            for (CustomComponentModel p : videos) {
                MediaItem mediaItem = MediaItem.fromUri(p.getMedia());
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

    public void setupData(List<CustomComponentModel> videos, View itemView , Activity activity){
//        if (this.videos != null && !this.videos.isEmpty()) return;

        this.activity = activity;
        this.videos = videos;

        if (binding == null) return;

        if (videos != null && videos.get(0).isVideoUrl()){
            setupAppLifeCycleCallback(itemView);
            bindMedia(); // do this only once
        } else if (videos != null){
            Graphics.loadImage(videos.get(0).getMedia(), binding.ivCustom);
        }
    }


    public void onItemVisibilityChanged(boolean isVisible) {
        if (videos == null || videos.isEmpty()) return;
        CustomComponentModel model = videos.get(0);
        if (model == null) return;
        if (AppSettingManager.shared.videoPlayPauseList.contains(model.getId())) {return;}
        if (isVisibleNow == isVisible) { return; }
        isVisibleNow = isVisible;
        if (isVisible) {
            playPlayer();
        } else {
            pausePlayer();
        }
        updateVolume();
    }

    public boolean isVideo(){
        if (videos == null || videos.isEmpty()) return false;
        CustomComponentModel model = videos.get(0);
        if (model != null){
            return model.getMediaType().equals("video");
        }
        return false;
    }

    private void setupVenueInfo() {
        activity.runOnUiThread(() -> {
            if (videos == null || videos.isEmpty()) return;
            CustomComponentModel model = videos.get(0);
            if ( model != null){
                setText(binding.tvTitle,model.getTitle());
                setText(binding.tvSubTitle,model.getSubTitle());
                setText(binding.tvSubDescription,model.getDescription());

                binding.btnBookNow.setText(model.getButtonText());

                if (!TextUtils.isEmpty(model.getButtonColor())) {
                    int color = Color.parseColor(model.getButtonColor());
                    binding.btnBookNow.setBackgroundTintList(ColorStateList.valueOf(color));
                }

            }
        });
    }

    private void setListeners() {

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {

            }
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                binding.ivCustom.setVisibility(playbackState == Player.STATE_ENDED ? View.VISIBLE : View.GONE);
                CustomComponentModel model = videos.get(0);
                if (model != null){
                    Graphics.loadImage(model.getMedia(),binding.ivCustom);
                }
                if (playbackState == Player.STATE_ENDED && model != null && !AppSettingManager.shared.videoPlayPauseList.contains(model.getId())){
                    AppSettingManager.shared.videoPlayPauseList.add(model.getId());
                }
                binding.btnReplay.setVisibility(playbackState == Player.STATE_ENDED ? View.VISIBLE : View.GONE);
                if (playbackState == Player.STATE_READY){
                    if (videos != null && !videos.isEmpty()) {
                        if (model != null && model.isVideoUrl() && AppSettingManager.shared.videoPlayPauseList.contains(model.getId())) {
                            binding.btnReplay.setVisibility(View.VISIBLE);
                        } else {
                            binding.btnReplay.setVisibility(GONE);
                        }
                    }
                }

                binding.progressBar.setVisibility((playbackState == Player.STATE_BUFFERING || playbackState == Player.STATE_IDLE) ? View.VISIBLE : View.GONE);
            }
        });


        binding.btnReplay.setOnClickListener(v -> {
            if (exoPlayer != null) { exoPlayer.seekTo(0, 0); }
            playPlayer();
            CustomComponentModel model = videos.get(0);
            if (model != null) {
                AppSettingManager.shared.videoPlayPauseList.remove(model.getId());
            }
            binding.ivCustom.setVisibility(GONE);
            binding.btnReplay.setVisibility(View.GONE);
        });

        binding.iconSoundOff.setChecked(Preferences.shared.getBoolean("isMute"));

        binding.iconSoundOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (exoPlayer != null) {
                exoPlayer.setVolume(!isChecked ? 1f : 0f);
                Preferences.shared.setBoolean("isMute", isChecked);
            }
        });

        binding.btnBookNow.setOnClickListener(v -> {
            if (videos == null || videos.isEmpty()) return;
            CustomComponentModel model = videos.get(0);
            if (model != null){
                handleExploreBtnClick(model);
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


    private void updateVolume() {
        if (binding == null) { return; }
        boolean isMute = Preferences.shared.getBoolean("isMute");
        binding.iconSoundOff.setChecked(isMute);
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

    public void hideLayouts(boolean hideVideoComponents){
        if (binding == null) return;
        binding.videoView.setVisibility(hideVideoComponents ? View.GONE : View.VISIBLE);
        binding.btnReplay.setVisibility(hideVideoComponents ? View.GONE : View.VISIBLE);
        binding.btnVolume.setVisibility(hideVideoComponents ? View.GONE : View.VISIBLE);
        binding.ivCustom.setVisibility(!hideVideoComponents ? View.GONE : View.VISIBLE);

        if (videos != null && !videos.isEmpty()) {
            CustomComponentModel model = videos.get(0);
            if (model != null && model.isVideoUrl() && AppSettingManager.shared.videoPlayPauseList.contains(model.getId())) {
                binding.btnReplay.setVisibility(View.VISIBLE);
            } else {
                binding.btnReplay.setVisibility(GONE);
            }
        }


        setupVenueInfo();

        binding.btnBookNow.setOnClickListener(v -> {
            if (videos == null || videos.isEmpty()) return;
            CustomComponentModel model = videos.get(0);
            if (model != null){
                handleExploreBtnClick(model);
            }
        });
    }


    private void setText(TextView textView,String title){
        if (TextUtils.isEmpty(title)){
            textView.setVisibility(View.GONE);
        }else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(title);
        }

    }


    private void handleExploreBtnClick(CustomComponentModel model){
        if (model != null){
            switch (model.getType()) {
                case "ticket":
                    activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId", model.getTypeId()));
                    break;
                case "category":
                case "small-category":
                case "big-category":
                    if (SessionManager.shared.geExploreBlockData() != null) {
                        Optional<CategoriesModel> categoriesModel = SessionManager.shared.geExploreBlockData().getCategories().stream().filter(m -> m.getId().equals(model.getTypeId())).findFirst();
                        Intent intent = new Intent(activity, ExploreDetailActivity.class);
                        intent.putExtra("isCity", false);
                        if (categoriesModel.isPresent()) {
                            intent.putExtra("categoryModel", new Gson().toJson(categoriesModel.get()));
                        } else {
                            intent.putExtra("title", model.getTitle());
                        }
                        activity.startActivity(intent);

                    }
                    break;
                case "city":
                    Optional<BannerModel> citiModel = SessionManager.shared.geExploreBlockData().getBanners().stream().filter(m -> m.getId().equals(model.getId())).findFirst();
                    Intent intent = new Intent(activity, ExploreDetailActivity.class);
                    intent.putExtra("isCity", true);
                    citiModel.ifPresent(bannerModel -> intent.putExtra("categoryModel", new Gson().toJson(bannerModel)));
                    intent.putExtra("title", model.getTitle());
                    activity.startActivity(intent);
                    break;
            }
//                activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId",model.getTypeId()));

        }
    }

}
