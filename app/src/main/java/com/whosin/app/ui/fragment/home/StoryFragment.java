package com.whosin.app.ui.fragment.home;

import android.content.Intent;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.Player;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.FragmentStoryBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.StoryObjectModel;
import com.whosin.app.service.models.StorySeenEventModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.storyViewer.StoriesProgressView;
import com.whosin.app.ui.activites.Story.OnSwipeTouchListener;
import com.whosin.app.ui.activites.offers.OfferDetailBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.app.ui.activites.venue.VenueActivity;
import com.whosin.app.ui.activites.venue.VenueShareActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StoryFragment extends BaseFragment {
    private FragmentStoryBinding binding;
    private VenueObjectModel model;
    private VenueObjectModel sendStoryModel;
    private int currentPosition = 0;
    private ViewPager2 viewPager;
    private PlayerFragment playerView;
    private final Handler handler = new Handler();

    private StoryObjectModel storyModel;

    private String offerID = "";
    private String ticketId = "";
    private boolean isVisibled = false;
    private boolean isPausedByLongPress = false;
    private String type = "";
    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    public StoryFragment() {
    }

    public StoryFragment(VenueObjectModel model, ViewPager2 viewPager) {
        this.model = model;
        this.viewPager = viewPager;
    }


    @Override
    public void initUi(View view) {
        binding = FragmentStoryBinding.bind(view);
        if (model != null) {
            binding.venueContainer.setVenueDetail(model);
            binding.venueContainer.isOpenStory = false;
        }
        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.storyClose);

        view.setOnTouchListener(new OnSwipeTouchListener(requireActivity()) {
            @Override
            public boolean onSwipeBottom() {
                getActivity().onBackPressed();
                return true;
            }
        });

        boolean isMute = Preferences.shared.getBoolean("isMute");
        binding.iconSoundOff.setChecked(isMute);

        if (getActivity() != null) {
            Graphics.applyBlurEffect(getActivity(), binding.blurViewShare);
            Graphics.applyBlurEffect(getActivity(), binding.blurViewTicketShare);
        }
    }

    @Override
    public void onResume() {
        super.onResume();


        if (!isVisibled && isVisible()) {
            currentPosition = 0;
            if (model == null) { return; }
            if (model.getStories() == null) { return; }
            if (model.getStories().isEmpty()) { return; }
            binding.storiesProgress.setStoriesCount(model.getStories().size());
            storyModel = model.getStories().get(currentPosition);

            if (storyModel.getMediaType().equalsIgnoreCase("photo")) {
                binding.storiesProgress.setStoryDuration(5000);
            } else {
                binding.storiesProgress.setStoryDuration(storyModel.getDuration());
            }
            binding.storiesProgress.startStories();
            loadStory();
        }
    }

    public void restartStory() {
        Log.d("StoryFragment", "restartStory: " + viewPager.getCurrentItem());
        if (isPausedByLongPress) {
            isPausedByLongPress = false;
            binding.storiesProgress.resume();
            if (playerView != null) {
                playerView.resume();
            }
        }
    }

    public void onFragmentVisible() {
//        Log.d("StoryFragment", "onFragmentVisible: " + viewPager.getCurrentItem());
        isVisibled = true;
        currentPosition = 0;
        if (model == null) { return; }
        if (model.getStories() == null) { return; }
        binding.storiesProgress.setStoriesCount(model.getStories().size());
        storyModel = model.getStories().get(currentPosition);
        if (storyModel.getMediaType().equalsIgnoreCase("photo")) {
            binding.storiesProgress.setStoryDuration(5000);
        } else {
            binding.storiesProgress.setStoryDuration(storyModel.getDuration());
        }
        binding.storiesProgress.startStories();
        loadStory();
    }

    public void onFragmentInvisible() {
        if (playerView != null) {
            playerView.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (playerView != null) {
            playerView.pause();
            playerView.muteSound();
            binding.storiesProgress.pause();
        }
    }

    @Override
    public void setListeners() {

        binding.blurViewShare.setOnClickListener(v -> {
            sendStoryModel = model;
            sendStoryModel.getStories().clear();
            List<StoryObjectModel> storyObjectModels  = new ArrayList<>();
            storyObjectModels.add(0,storyModel);
            sendStoryModel.setStories(storyObjectModels);
            if (getActivity() == null) { return; }
            startActivity( new Intent(getActivity(), VenueShareActivity.class).putExtra(  "venue",new Gson().toJson( sendStoryModel)).putExtra("type","story"));
        });

        binding.blurViewTicketShare.setOnClickListener(v -> {
            sendStoryModel = model;
            sendStoryModel.getStories().clear();
            List<StoryObjectModel> storyObjectModels  = new ArrayList<>();
            storyObjectModels.add(0,storyModel);
            sendStoryModel.setStories(storyObjectModels);
            if (getActivity() == null) { return; }
            startActivity( new Intent(getActivity(), VenueShareActivity.class).putExtra(  "venue",new Gson().toJson( sendStoryModel)).putExtra("type","story"));
        });

        binding.iconSoundOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (playerView != null) {
                playerView.toggleSound();
                Preferences.shared.setBoolean("isMute", playerView.isMute());
            }
        });

        binding.constrain.setOnClickListener(v -> {
            if (model == null) { return; }
            Intent intent = new Intent(context, VenueActivity.class);
            intent.putExtra("venueId", model.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        binding.storiesProgress.setStoriesListener(new StoriesProgressView.StoriesListener() {
            @Override
            public void onNext() {
                currentPosition++;
                loadStory();
            }

            @Override
            public void onPrev() {
                currentPosition--;
                loadStory();
            }

            @Override
            public void onComplete() {
                if (viewPager == null) { return; }
                int currentItem = viewPager.getCurrentItem();
                int totalViews = viewPager.getAdapter().getItemCount();
                currentItem++;
                if (totalViews > currentItem) {
                    viewPager.setCurrentItem(currentItem, true);
                } else {
                    if (getActivity() == null) { return; }
                    getActivity().finish();
                }
            }
        });

        binding.storyClose.setOnClickListener(view -> {
            getActivity().finish();
            EventBus.getDefault().post(new MessageEvent());
        });


        Runnable mLongPressed = () -> {
            isPausedByLongPress = true;
            binding.storiesProgress.pause();
            if (playerView != null) {
                playerView.pause();
            }
        };

        View.OnTouchListener touchListener = (v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                handler.postDelayed(mLongPressed, 300);
            }
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                isPausedByLongPress = false;
                handler.removeCallbacks(mLongPressed);
                if (event.getEventTime() - event.getDownTime() > 300) {
                    binding.storiesProgress.resume();
                    if (playerView != null) {
                        playerView.resume();
                    }
                } else {
                    if (v.getId() == binding.skip.getId()) {
                        binding.storiesProgress.skip();
                    } else {
                        binding.storiesProgress.reverse();
                    }
                }
            }
            return false;
        };

        binding.skip.setOnTouchListener(touchListener);
        binding.reverse.setOnTouchListener(touchListener);

        binding.ivOfferNext.setOnClickListener(v -> {
            if (TextUtils.isEmpty(offerID)) { return; }
            OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
            dialog.offerId = offerID;
            dialog.show(getChildFragmentManager(), "");
        });


        binding.offerLayout.setOnClickListener(v -> {
           if (TextUtils.isEmpty(offerID)) { return; }
            OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
            dialog.offerId = offerID;
            dialog.show(getChildFragmentManager(), "");
        });


        binding.ticketLayout.setOnClickListener(v -> {
            if (TextUtils.isEmpty(ticketId)) { return; }
            startActivity(new Intent(requireActivity(), RaynaTicketDetailActivity.class).putExtra("ticketId",ticketId));
        });
    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_story;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void loadStory() {
        if (model == null) { return; }
        if (model.getStories() == null) { return; }
        playerView = null;
        if (currentPosition < 0) {
            currentPosition = 0;
        }

        if (currentPosition >= 0 && currentPosition < model.getStories().size()) {
            storyModel = model.getStories().get(currentPosition);
        }

        if (storyModel.getContentType() != null &&  !storyModel.getContentType().isEmpty() && storyModel.getContentType().equals("offer")){
            if (storyModel.getOfferId() != null && !storyModel.getOfferId().isEmpty()){
                setOfferDetail(storyModel.getOfferId());
            }
        } else if (storyModel.getContentType() != null &&  !storyModel.getContentType().isEmpty() && storyModel.getContentType().equals("ticket")) {
            if (storyModel.getTicketId() != null && !storyModel.getTicketId().isEmpty()){
                setTicketDetail(storyModel.getTicketId());
            }
        }

        if (currentPosition == model.getStories().size() - 1) {
            Preferences.shared.addListItem("story_seen", model.getId());
        }

        if (storyModel.getMediaType().equalsIgnoreCase("photo")) {
            binding.layoutVolume.setVisibility(View.GONE);
            binding.storiesProgress.setStoryDuration(5000);
            Graphics.loadImage(storyModel.getMediaUrl(), binding.imgStory);
            binding.playerContainer.setVisibility(View.GONE);
            binding.imgStory.setVisibility(View.VISIBLE);
        } else {
            binding.layoutVolume.setVisibility(View.VISIBLE);
            binding.storiesProgress.setStoryDuration(storyModel.getDuration());
            binding.playerContainer.setVisibility(View.VISIBLE);
            binding.imgStory.setVisibility(View.GONE);
            playerView = PlayerFragment.newInstance(storyModel.getMediaUrl());
            playerView.setCallback(data -> {
                if (data == Player.STATE_BUFFERING || data == Player.STATE_IDLE) {
                    binding.storiesProgress.pause();
                } else if (data == Player.STATE_READY) {
                    binding.storiesProgress.resume();
                } else if (data == Player.STATE_ENDED) {
                    loadStory();
                }
            });
            Graphics.replaceFragment(this, binding.playerContainer.getId(), playerView);
        }
    }


    private void setOfferDetail(String offerId){

        Optional<OffersModel> matchingOffer = SessionManager.shared.geHomeBlockData().getOffers().stream()
                .filter(offer -> offer.getId().equals(offerId))
                .findFirst();
        if (matchingOffer.isPresent()){
            offerID = matchingOffer.get().getId();
            binding.offerLayout.setVisibility(View.VISIBLE);
            Graphics.applyBlurEffect(requireActivity(),binding.blurView);
            Graphics.loadImage(matchingOffer.get().getImage(),binding.ivOffer);
            binding.tvOfferTitle.setText(matchingOffer.get().getTitle());
            binding.tvOfferDescription.setText(matchingOffer.get().getDescription());
            binding.txtOfferTime.setText(matchingOffer.get().getOfferTiming());
            String startDate = Utils.convertDateFormat(matchingOffer.get().getStartTime(),"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","dd-MM-yyyy");
            String endDate = Utils.convertDateFormat(matchingOffer.get().getEndTime(),"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","dd-MM-yyyy");
            binding.txtOfferDays.setText(String.format("%s - %s", startDate, endDate));
            binding.txtOfferDays.setText(String.format("%s - %s, %s", startDate, endDate, matchingOffer.get().getDays()));
        }else {
            binding.offerLayout.setVisibility(View.GONE);
        }

    }

    private void setTicketDetail(String id){
        binding.shareBtn.setVisibility(View.GONE);
        Optional<RaynaTicketDetailModel> ticketModel = SessionManager.shared.geHomeBlockData().getTickets().stream()
                .filter(ticketDetail -> ticketDetail.getId().equals(id))
                .findFirst();
        if (ticketModel.isPresent()) {
            ticketId = ticketModel.get().getId();
            binding.ticketLayout.setVisibility(View.VISIBLE);
            Graphics.applyBlurEffect(requireActivity(), binding.ticketblurView);

            binding.tvTicketTitle.setText(ticketModel.get().getTitle());

            String description = ticketModel.get().getDescription();
            String text = description != null ? description.replaceAll("<[^>]*>", "").trim() : "";

            if (!TextUtils.isEmpty(text)) {
                binding.tvTicketDescription.setVisibility(View.VISIBLE);
                binding.tvTicketDescription.setText(text);
            } else {
                binding.tvTicketDescription.setVisibility(View.GONE);
            }

            binding.txtLocationime.setText(ticketModel.get().getCity());

            String startingAmount = ticketModel.get().getStartingAmount() != null ? String.valueOf(ticketModel.get().getStartingAmount()) : "N/A";

            SpannableString styledPrice = Utils.getStyledText(requireActivity(), startingAmount);
            SpannableStringBuilder fullText = new SpannableStringBuilder()
                    .append("Starting from :  ")
                    .append(styledPrice);

            binding.txtTicketStartingAmount.setText(fullText);


            String tmpImage = "";

            if (ticketModel.get().getImages() != null && !ticketModel.get().getImages().isEmpty()) {
                for (String image : ticketModel.get().getImages()) {
                    if (!Utils.isVideo(image)) {
                        tmpImage = image;
                        Graphics.loadImage(image, binding.ivTicket);
                        break;
                    }
                }
            }

        } else {
            binding.ticketLayout.setVisibility(View.GONE);
        }

    }





    // endregion
    // --------------------------------------
}