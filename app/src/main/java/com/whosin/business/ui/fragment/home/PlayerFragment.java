package com.whosin.business.ui.fragment.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.gson.Gson;
import com.whosin.business.R;
import com.whosin.business.comman.Preferences;
import com.whosin.business.comman.VideoCache;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.databinding.FragmentPlayerBinding;
import com.whosin.business.ui.fragment.comman.BaseFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerFragment extends BaseFragment {

    private static final String TAG = "PlayerFragment";
    private FragmentPlayerBinding binding;
    private ExoPlayer exoPlayer;
    private String videoUrl;
    private List<String> videoUrls = new ArrayList<>();
    private CommanCallback<Integer> callback;

    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    public static PlayerFragment newInstance(String videoUrl) {
        PlayerFragment fragmentFirst = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString("video_url", videoUrl);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public static PlayerFragment newInstance(List<String> videoUrls) {
        PlayerFragment fragmentFirst = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString("video_urls", new Gson().toJson(videoUrls));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void initUi(View view) {
        binding = FragmentPlayerBinding.bind(view);
        context = view.getContext();
        videoUrl = getArguments().getString("video_url");
        String jsonString = getArguments().getString("video_urls");
        if (!TextUtils.isEmpty(jsonString)) {
            videoUrls = Arrays.asList(new Gson().fromJson(jsonString, String[].class));
        }
        setupUI();

    }

    @Override
    public void setListeners() {

    }

    public void toggleSound() {
        if (exoPlayer != null) {
            boolean isMuted = (exoPlayer.getVolume() == 0f);
            exoPlayer.setVolume(isMuted ? 1f : 0f);
        }
    }

    public boolean isMute() {
        return (exoPlayer.getVolume() == 0f);
    }

    public void muteSound() {
        if (exoPlayer != null) {
            exoPlayer.setVolume(0f);
        }
    }

    public void unMuteSound() {
        if (exoPlayer != null) {
            exoPlayer.setVolume(1f);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        play();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
         }
    }

    @Override
    public void populateData(boolean getDataFromServer) {
        // empty
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_player;
    }

    public void setCallback(CommanCallback<Integer> callback) {
        this.callback = callback;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setupUI() {
        initializePlayer();
        if (!TextUtils.isEmpty(videoUrl)) {
            VideoPreCaching.shared(context).removeItem(videoUrl);
        } 
        else if (!videoUrls.isEmpty()) {
            videoUrls.forEach( p -> {
                VideoPreCaching.shared(context).removeItem(p);
            });
        }
    }


    private void initializePlayer() {
        DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true);
        DefaultDataSource.Factory defaultDataSourceFactory = new DefaultDataSource.Factory(context, httpDataSourceFactory);
        CacheDataSource.Factory cacheDataSourceFactory = new CacheDataSource.Factory()
                .setCache(VideoCache.shared(context))
                .setUpstreamDataSourceFactory(defaultDataSourceFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

        DefaultDataSource.Factory mediaDataSourceFactory = new DefaultDataSource.Factory(context);
        MediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(mediaDataSourceFactory);

        exoPlayer = new ExoPlayer.Builder(context).setMediaSourceFactory(mediaSourceFactory).build();
        binding.videoView.setPlayer(exoPlayer);
        binding.videoView.hideController();
        binding.videoView.setUseController(false);
        if (!videoUrls.isEmpty()) {
            videoUrls.forEach( p -> {
                MediaItem mediaItem = MediaItem.fromUri(p);
                ProgressiveMediaSource source = new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem);
                exoPlayer.addMediaSource(source);
            });

        } else if (!TextUtils.isEmpty(videoUrl)) {
            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            exoPlayer.addMediaSource(new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem));
        }
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        exoPlayer.prepare();

        boolean isMute = Preferences.shared.getBoolean("isMute");
        if (isMute) {
            muteSound();
        } else {
            unMuteSound();
        }
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);

                if (callback != null) {
                    callback.onReceive(playbackState);
                }
                if (playbackState == Player.STATE_BUFFERING) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);
                Log.d(TAG, "onPlayerError: " + error.getLocalizedMessage());
            }

            @Override
            public void onRenderedFirstFrame() {
                Log.d(TAG, "onRenderedFirstFrame: ");
                Player.Listener.super.onRenderedFirstFrame();
            }
        });
    }

    public void play() {
        if (exoPlayer != null) {
            if (exoPlayer.getPlaybackState() == Player.STATE_IDLE) {
                exoPlayer.prepare();
            }
            exoPlayer.seekTo(0);
            exoPlayer.play();
        } else {
            initializePlayer();
            exoPlayer.play();
        }
    }

    private void stopPlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
        }
    }

    public void replay() {
        if (exoPlayer != null) {
            exoPlayer.seekTo(0,0);
            if (exoPlayer.getPlaybackState() == Player.STATE_ENDED) {
                exoPlayer.prepare();
                exoPlayer.setPlayWhenReady(true);
            } else {
                exoPlayer.play();
            }
        } else {
            initializePlayer();
            exoPlayer.play();
        }
    }

    // endregion
    // --------------------------------------
    // region public
    // --------------------------------------

    public  void next() {
        if (exoPlayer != null) {
            if (exoPlayer.hasNextMediaItem()) {
                exoPlayer.seekToNextMediaItem();
            } else {
                exoPlayer.seekTo(exoPlayer.getDuration());
            }
        }
    }

    public  void previous() {
        if (exoPlayer != null) {
            if (exoPlayer.hasPreviousMediaItem()) {
                exoPlayer.seekToPreviousMediaItem();
            }
            else {
                exoPlayer.seekTo(0,0);
            }
        }
    }

    public void seekToIndex(int index) {
        if (exoPlayer != null) {
            exoPlayer.seekTo(index, 0);
        }
    }

    public int getCurrentIndex() {
        if (exoPlayer != null) {
            exoPlayer.getCurrentMediaItemIndex();
        }
        return 0;
    }

    public  void pause() {
        if (exoPlayer != null) {
            exoPlayer.pause();
        }
    }

    public void resume() {
        if (exoPlayer != null) {
            exoPlayer.play();
        }
    }
    // endregion
    // --------------------------------------
}
