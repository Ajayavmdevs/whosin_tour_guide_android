package com.whosin.app.ui.controller;

import static com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.amplitude.api.Amplitude;
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
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.VideoCache;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.MiniVideoComponentLayoutBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.AdListModel;
import com.whosin.app.ui.activites.Promoter.ComplementaryEventDetailActivity;
import com.whosin.app.ui.activites.category.CategoryActivity;
import com.whosin.app.ui.activites.home.MainHomeActivity;
import com.whosin.app.ui.activites.home.activity.ActivityListDetail;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.offers.OfferDetailActivity;
import com.whosin.app.ui.activites.offers.VoucherDetailScreenActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaVideoFullScreenPreviewActivity;
import com.whosin.app.ui.activites.venue.VenueActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiniVideoView extends ConstraintLayout {

    private MiniVideoComponentLayoutBinding binding;
    private Context context;
    private Activity activity;
    public SimpleExoPlayer exoPlayer;

    private boolean isVisibleNow = false;
    public CommanCallback<Boolean> closeCallBack = null;

    private List<AdListModel> videos = new ArrayList<>();

    private OnMiniPlayerGestureListener gestureListener;
    private float lastY;
    private boolean isDragging = false;




    public MiniVideoView(Context context) {
        this(context, null);
    }

    public MiniVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiniVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.mini_video_component_layout, this, (view, resid, parent) -> {
            binding = MiniVideoComponentLayoutBinding.bind(view);
            if (videos != null) {
                bindMedia();
            }
            MiniVideoView.this.removeAllViews();
            MiniVideoView.this.addView(view);
        });
    }

    public void setOnMiniPlayerGestureListener(OnMiniPlayerGestureListener listener) {
        this.gestureListener = listener;
    }

    public void bindMedia() {

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
            for (AdListModel p : videos) {
                MediaItem mediaItem = MediaItem.fromUri(p.getVideo());
                ProgressiveMediaSource source = new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem);
                mediaSourceList.add(source);
            }

            if (Utils.isValidActivity(activity)) {
                activity.runOnUiThread(() -> {
                    binding.videoView.setPlayer(exoPlayer);
                    exoPlayer.addMediaSources(mediaSourceList);
                    exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
                    exoPlayer.prepare();
                    exoPlayer.setVolume(0f);
                    setListeners();
                });
            }
        });
        backgroundThread.start();

    }

    public void setupData(List<AdListModel> videos, View itemView, Activity activity) {
        this.activity = activity;
        this.videos = videos;
        setupAppLifeCycleCallback(itemView);
        if (binding == null) {
            return;
        }
        bindMedia();
    }

    public void onItemVisibilityChanged(boolean isVisible) {
        if (isVisibleNow == isVisible) {
            return;
        }
        isVisibleNow = isVisible;
        if (isVisible) {
            playPlayer();
        } else {
            pausePlayer();
        }
    }

    private void openScreen(){
        activity.runOnUiThread(() -> {


            AdListModel model = getCurrentVideoModel();
            if (model != null){
                trackEventWithModel("ad_click",model);
                switch (model.getType()) {
                    case "venue":
                        activity.startActivity(new Intent(activity, VenueActivity.class).putExtra("venueId", model.getTypeId()));
                        break;
                    case "ticket":
                        activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId", model.getTypeId()));
                        break;
                    case "event":
                        activity.startActivity(new Intent(activity, EventDetailsActivity.class).putExtra("eventId", model.getTypeId()));
                        break;
                    case "activity":
                        activity.startActivity(new Intent(activity, ActivityListDetail.class).putExtra("activityId", model.getTypeId()));
                        break;
                    case "deal":
                        activity.startActivity(new Intent(activity, VoucherDetailScreenActivity.class).putExtra("id", model.getTypeId()));
                        break;
                    case "offer":
                        activity.startActivity(new Intent(activity, OfferDetailActivity.class).putExtra("offerId", model.getTypeId()));
                        break;
                    case "category":
                        activity.startActivity(new Intent(activity, CategoryActivity.class).putExtra("categoryId", model.getTypeId()));
                        break;
                    case "promoter-event":
                        if (SessionManager.shared.getUser().isRingMember()){
                            Intent intent = new Intent(activity, ComplementaryEventDetailActivity.class);
                            intent.putExtra("eventId", model.getTypeId());
                            intent.putExtra("type","complementary");
                            activity.startActivity(intent);
                        }
                        break;
                }
            }
        });
    }

    private void setListeners() {
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                AdListModel currentModel = getCurrentVideoModel();
                if (currentModel != null) {
                    trackEventWithModel("ad_load", currentModel);
                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                binding.progressBar.setVisibility((playbackState == Player.STATE_BUFFERING || playbackState == Player.STATE_IDLE) ? View.VISIBLE : View.GONE);
            }
        });

        final float[] lastX = {0};
        final float[] lastY = {0};

        GestureDetector gestureDetector = new GestureDetector(activity, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                    if (diffX > 0) {
                        AdListModel currentModel = getCurrentVideoModel();
                        if (currentModel != null) {
                            trackEventWithModel("ad_swipe", currentModel);
                        }
                        exoPlayer.seekToPreviousMediaItem();
                    } else {
                        AdListModel currentModel = getCurrentVideoModel();
                        if (currentModel != null) {
                            trackEventWithModel("ad_swipe", currentModel);
                        }
                        exoPlayer.seekToNextMediaItem();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                openScreen();
                return true;
            }
        });


        binding.gestureZone.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX[0] = event.getRawX();
                    lastY[0] = event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getRawX() - lastX[0];
                    float deltaY = event.getRawY() - lastY[0];

                    lastX[0] = event.getRawX();
                    lastY[0] = event.getRawY();

                    if (gestureListener != null) {
                        gestureListener.onFreeDrag(deltaX, deltaY);
                    }
                    break;
            }
            return true;
        });


        binding.iconCloseVideo.setOnClickListener(v -> {
            releasePlayer();
            if (closeCallBack != null){
                AdListModel videoModel = getCurrentVideoModel();
                if (videoModel != null){
                    trackEventWithModel("ad_close",videoModel);
                }
                closeCallBack.onReceive(true);
            }
        });

        binding.iconFullScreen.setOnClickListener(v -> activity.runOnUiThread(() -> {
            AdListModel videoModel = getCurrentVideoModel();
            if (videoModel != null){
                trackEventWithModel("ad_fullScreen",videoModel);
                activity.startActivity(new Intent(activity, RaynaVideoFullScreenPreviewActivity.class).putExtra("VIDEO_URL",videoModel.getVideo()));
            }

        }));
    }

    public void playPlayer() {
        if (isVisibleNow) {
            if (exoPlayer == null) {
                return;
            }
            try {
                exoPlayer.setPlayWhenReady(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pausePlayer() {
        if (exoPlayer == null) {
            return;
        }
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


    private void trackEventWithModel(String eventName, AdListModel model) {
        try {
            Gson gson = new Gson();
            String jsonString = gson.toJson(model);
            JSONObject jsonObject = new JSONObject(jsonString);
            Amplitude.getInstance().logEvent(eventName, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("EventTracking", "Failed to convert model to JSONObject", e);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("EventTracking", "Unexpected error while tracking event", ex);
        }
    }


    private AdListModel getCurrentVideoModel() {
        if (videos == null || videos.isEmpty() || exoPlayer == null) {
            return null;
        }
        int currentVideoIndex = exoPlayer.getCurrentMediaItemIndex();
        if (currentVideoIndex < 0 || currentVideoIndex >= videos.size()) {
            return null;
        }
        return videos.get(currentVideoIndex);
    }


    private void setupAppLifeCycleCallback(View itemView) {
        ((AppDelegate) itemView.getContext().getApplicationContext()).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                if (activity instanceof MainHomeActivity) {
                    playPlayer();
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
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
            }
        });
    }

    public interface OnMiniPlayerGestureListener {
        void onFreeDrag(float deltaX, float deltaY);
    }


}

