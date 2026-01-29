package com.whosin.business.ui.adapter.raynaTicketAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Preferences;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.ItemEventImageDesignBinding;
import com.whosin.business.databinding.ItemEventVideoDesignBinding;
import com.whosin.business.service.models.RatingModel;
import com.whosin.business.ui.activites.raynaTicket.RaynaVideoFullScreenPreviewActivity;

import java.util.ArrayList;
import java.util.List;


public class RaynaGalleryAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    private final List<ExoPlayer> players = new ArrayList<>();

    private final List<Integer> playingPositions = new ArrayList<>();

    private Activity activity;

    private CommanCallback<Integer> callback;

    private final Handler handler = new Handler(Looper.getMainLooper());

    public RaynaGalleryAdapter(Activity activity , CommanCallback<Integer> callback){
        this.callback = callback;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View view = UiUtils.getViewBy(parent, R.layout.item_event_image_design);
                view.getLayoutParams().width = (int) (getItemCount() > 1 ? Graphics.getScreenWidth(activity) * 0.93 : Graphics.getScreenWidth(activity) * 0.95);
                return new RaynaImageHolder(view);
            case 2:
                View view2 = UiUtils.getViewBy(parent, R.layout.item_event_video_design);
                view2.getLayoutParams().width = (int) (getItemCount() > 1 ? Graphics.getScreenWidth(activity) * 0.93 : Graphics.getScreenWidth(activity) * 0.95);
                return new RaynaVideoHolder(view2);
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


            videoHolder.binding.iconFullScreen.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                activity.startActivity(new Intent(activity, RaynaVideoFullScreenPreviewActivity.class).putExtra("VIDEO_URL",model.getImage()));
            });


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

        private final ItemEventImageDesignBinding binding;

        public RaynaImageHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemEventImageDesignBinding.bind(itemView);

        }

        public void setupData(String imageUrl) {

            Graphics.loadImage(activity,imageUrl,binding.eventImage);

            binding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                if (callback != null) callback.onReceive(getAbsoluteAdapterPosition());
            });
        }
    }

    public class RaynaVideoHolder extends RecyclerView.ViewHolder {

        private final ItemEventVideoDesignBinding binding;

        private ExoPlayer player;

        public RaynaVideoHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemEventVideoDesignBinding.bind(itemView);
            player = new ExoPlayer.Builder(itemView.getContext()).build();
            binding.eventVideoView.setPlayer(player);

            binding.iconFullScreen.setVisibility(View.VISIBLE);

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
