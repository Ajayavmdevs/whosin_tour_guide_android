package com.whosin.app.ui.activites.raynaTicket;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.whosin.app.R;
import com.whosin.app.comman.Preferences;
import com.whosin.app.databinding.ActivityRaynaVideoFullScreenPreviewBinding;
import com.whosin.app.ui.activites.comman.BaseActivity;

public class RaynaVideoFullScreenPreviewActivity extends BaseActivity {


    private ActivityRaynaVideoFullScreenPreviewBinding binding;

    private ExoPlayer player;

    private final Handler handler = new Handler(Looper.getMainLooper());

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

        player = new ExoPlayer.Builder(activity).build();
        binding.eventVideoView.setPlayer(player);

        binding.iconSoundOff.setChecked(Preferences.shared.getBoolean("isMuteRaynaFullScreenVideo"));
        boolean isMute = Preferences.shared.getBoolean("isMuteRaynaFullScreenVideo");
        player.setVolume(isMute ? 0f : 1f);

        String videoUrl = getIntent().getStringExtra("VIDEO_URL");
        setupData(videoUrl);


    }

    @Override
    protected void setListeners() {

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    player.seekTo(0);
                    player.play();
                }
            }
        });


        binding.iconClose.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (player != null) {
                finish();
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

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityRaynaVideoFullScreenPreviewBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.play();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setupData(String videoUrl) {
        player.setMediaItem(MediaItem.fromUri(videoUrl));
        player.prepare();
        player.play();
    }

    private void startVideo() {
        player.play();

        binding.btnPlayAndPause.setImageResource(R.drawable.icon_play_video);
        binding.btnPlayAndPause.setVisibility(View.VISIBLE);

        handler.postDelayed(() -> binding.btnPlayAndPause.setVisibility(View.GONE), 1000);
    }

    private void pauseVideo() {
        player.pause();
        showPauseButton();
    }

    private void showPauseButton() {
        binding.btnPlayAndPause.setImageResource(R.drawable.icon_pause_video);
        binding.btnPlayAndPause.setVisibility(View.VISIBLE);
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------



    // endregion
    // --------------------------------------


}