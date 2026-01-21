package com.whosin.app.ui.activites.Profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityItemFeedBinding;
import com.whosin.app.databinding.ActivityOtherUserProfileBinding;
import com.whosin.app.databinding.EventItemOfferFeedBinding;
import com.whosin.app.databinding.ItemFriendsUpdateFeedBinding;
import com.whosin.app.databinding.ItemVenueOfferFeedsBinding;
import com.whosin.app.databinding.UserImageListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.CheckUserSession;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.AppSettingTitelCommonModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.FollowUnfollowModel;
import com.whosin.app.service.models.FollowUpdateEventModel;
import com.whosin.app.service.models.MyUserFeedModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.CmPublicProfileActivity;
import com.whosin.app.ui.activites.PromoterPublic.PromoterPublicProfileActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.activites.home.activity.ActivityListDetail;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.home.event.MutualFriendFragment;
import com.whosin.app.ui.activites.offers.OfferDetailBottomSheet;
import com.whosin.app.ui.activites.venue.Bucket.BucketListBottomSheet;
import com.whosin.app.ui.activites.venue.VenueBuyNowActivity;
import com.whosin.app.ui.activites.venue.VenueShareActivity;
import com.whosin.app.ui.activites.venue.VenueTimingDialog;
import com.whosin.app.ui.adapter.OfferPackagesAdapter;

import org.greenrobot.eventbus.EventBus;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherUserProfileActivity extends BaseActivity {

    private ActivityOtherUserProfileBinding binding;
    private final MutualFriendsAdapter<ContactListModel> userAdapter = new MutualFriendsAdapter<>();
    private final FeedAdapter<MyUserFeedModel> feedAdapter = new FeedAdapter<>();

    private List<MyUserFeedModel> otherUserFeedData = new ArrayList<>();
    private UserDetailModel userDetailModel;
    private String userName = "";

    private int page = 1;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @SuppressLint("SetTextI18n")
    @Override
    protected void initUi() {

        applyTranslations();

        binding.feedRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.feedRecyclerView.setAdapter(feedAdapter);
        binding.feedRecyclerView.setNestedScrollingEnabled(false);
        Utils.hideViews(binding.nestedScrollView);

        String friendId = getIntent().getStringExtra("friendId");
        if (!TextUtils.isEmpty(friendId)) {
            Log.d("TAG", "initUi: "+friendId);
            Log.d("TAG", "initUi: "+SessionManager.shared.getToken());
            CheckUserSession.checkSessionAndProceed(activity, () -> requestUserProfile(friendId));
        }
        Graphics.applyBlurEffect(activity, binding.blurView);

    }

    @Override
    protected void setListeners() {
        binding.closeBtn.setOnClickListener(v -> onBackPressed());

        binding.followingLayout.setOnClickListener(view -> startActivity(new Intent(activity, FollowingActivity.class).putExtra("id", getIntent().getStringExtra("friendId"))));
        binding.followersLayout.setOnClickListener(view -> startActivity(new Intent(activity, FollowresActivity.class).putExtra("id", getIntent().getStringExtra("friendId"))));

//        binding.btnFollowing.setOnClickListener(view -> {
//            if (!binding.btnFollowing.getText().equals("Pending")) {
//                reqFollowUnFollow();
//            }
//        });

        binding.btnFollowingButton.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            binding.btnFollowingButton.requestFollowUnfollow(userDetailModel,activity);
        });


        binding.btnProfileShare.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            Graphics.openShareDialog(activity, null, userDetailModel, null, "user");
        });

        binding.imageProfile.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (userDetailModel != null || TextUtils.isEmpty(userDetailModel.getImage())) {
                openLightbox(userDetailModel.getImage());
            }
        });

        binding.ivSharedUser.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (userDetailModel == null) { return; }
            MutualFriendFragment mutualFriendFragment = new MutualFriendFragment();
            mutualFriendFragment.mutualFriendsList = userDetailModel;
            mutualFriendFragment.show(getSupportFragmentManager(), "");
        });

        binding.btnMessage.setOnClickListener(view -> {
            if (userDetailModel == null) { return; }
            ChatModel chatModel = new ChatModel(userDetailModel);
            Intent intent = new Intent(activity, ChatMessageActivity.class);
            intent.putExtra("chatModel", new Gson().toJson(chatModel));
            startActivity(intent);
        });
        binding.feedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.feedRecyclerView.getLayoutManager();
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == feedAdapter.getData().size() - 1 && (feedAdapter.getData().size() % 30 == 0 && (!feedAdapter.getData().isEmpty()))) {
                    page++;
                    requestMyUserFeed(userDetailModel.getId(), true);
                }
            }
        });

        binding.tvApprove.setOnClickListener( view -> {

            reqFollowRequestAction("approved",userDetailModel);
        } );

        binding.tvReject.setOnClickListener( view -> {
            reqFollowRequestAction("rejected",userDetailModel);

        } );

        binding.profileswitchBtn.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            if (binding.tvSwitchBtn.getText().equals(getValue("switch_to_complimentary"))){
                startActivity(new Intent(activity, CmPublicProfileActivity.class)
                        .putExtra("isFromOtherUserProfile",true)
                        .putExtra("promoterUserId", userDetailModel.getId()));

            }else {
                startActivity( new Intent(activity, PromoterPublicProfileActivity.class)
                        .putExtra("isFromOtherUserProfile",true)
                        .putExtra("isPromoterProfilePublic", true).putExtra( "id",userDetailModel.getId() ) );
            }
            finish();
        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityOtherUserProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvApprove, "confirm");
        map.put(binding.tvReject, "delete");
        map.put(binding.tvFollowersTitle, "followers");
        map.put(binding.tvFollowingTitle, "following");
        map.put(binding.tvUserBio1, "following");
        map.put(binding.txtUserTitle, "suggested_friends");
        map.put(binding.tvMainTitle, "profile");
        map.put(binding.tvSwitchBtn, "switch_to_promoter");

        binding.btnMessage.setText(getValue("messages"));

        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void openLightbox(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Intent intent = new Intent(activity, ProfileFullScreenImageActivity.class);
            intent.putExtra(ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, imageUrl);
            startActivity(intent);
        }
    }

    private void filterData() {
        if (otherUserFeedData == null && otherUserFeedData.isEmpty()) {
            return;
        }
        List<MyUserFeedModel> otherUserFeed = otherUserFeedData.stream()
                .filter(model -> model.getType().equals("friend_updates")
                        || model.getType().equals("venue_updates")
                        || model.getType().equals("event_checkin")
                        || model.getType().equals("activity_recommendation")
                        || model.getType().equals("venue_recommendation")
                        || model.getType().equals("offer_recommendation")
                )
                .collect(Collectors.toList());

        feedAdapter.updateData(otherUserFeed);
    }

    private void setFriendDetails(UserDetailModel userDetailModel) {
        if (userDetailModel != null) {
            userName = userDetailModel.getFullName();
            binding.tvName.setText(userDetailModel.getFullName());
            Graphics.loadImageWithFirstLetter(userDetailModel.getImage(), binding.imageProfile, Utils.notNullString(userDetailModel.getFullName()));

            if (userDetailModel.getBio() != null && !userDetailModel.getBio().isEmpty()) {
                binding.tvUserBio.setVisibility(View.VISIBLE);
                binding.tvUserBio.setText(Utils.notNullString(userDetailModel.getBio()));
                binding.tvUserBio.post(() -> {
                    int lineCount = binding.tvUserBio.getLineCount();
                    if (lineCount > 2) {
                        Utils.makeTextViewResizable(binding.tvUserBio, 2, 2, ".. " + getValue("see_more"), true);
                    }
                });
            } else {
                binding.tvUserBio.setVisibility(View.GONE);

            }

            binding.followRequestContainer.setVisibility( userDetailModel.isRequestPending() ? View.VISIBLE : View.GONE );
            binding.tvUserName.setText(setValue("want_to_follow_you",userDetailModel.getFirstName()) );
            binding.tvFollowerCount.setText(String.valueOf(userDetailModel.getFollower()));
            binding.tvFollowingCount.setText(String.valueOf(userDetailModel.getFollowing()));

            binding.btnFollowingButton.setUserRequestStatus(userDetailModel);

            binding.mutualFriendsRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            binding.mutualFriendsRecycler.setAdapter(userAdapter);
            if (userDetailModel.getMutualFriends() != null && !userDetailModel.getMutualFriends().isEmpty()) {
                if (userDetailModel.getMutualFriends().size() > 4) {
                    List<ContactListModel> filteredList = userDetailModel.getMutualFriends().subList(0, 4);
                    userAdapter.updateData(filteredList);
                    String shareText = userDetailModel.getMutualFriends().subList(0, 4).stream().map(ContactListModel::getFirstName).collect(Collectors.joining(", "));
                    binding.tvShareUserName.setText(setValue("followed_by_message",shareText,String.valueOf(userDetailModel.getMutualFriends().size() - 4)));
                } else {
                    userAdapter.updateData(userDetailModel.getMutualFriends());
                    String shareText = userDetailModel.getMutualFriends().stream().map(ContactListModel::getFirstName).collect(Collectors.joining(", "));
                    binding.tvShareUserName.setText(getValue("followed_by") + shareText);
                }
            } else {
                binding.mutualFriendsRecycler.setVisibility(View.GONE);
                binding.tvShareUserName.setVisibility(View.GONE);
            }



            if (SessionManager.shared.getUser().isRingMember() && userDetailModel.isPromoter()){
                binding.profileswitchBtn.setVisibility(View.VISIBLE);
            } else if (SessionManager.shared.getUser().isPromoter() && userDetailModel.isRingMember()) {
                binding.profileswitchBtn.setVisibility(View.VISIBLE);
                binding.tvSwitchBtn.setText(getValue("switch_to_complimentary"));
            }else {
                binding.tvSwitchBtn.setVisibility(View.GONE);
            }

        }


    }

    private void setTitleFromList(List<String> categoryIds, List<AppSettingTitelCommonModel> commonModels, String type, TextView textView) {
        if (categoryIds == null && categoryIds.isEmpty()) {
            textView.setVisibility(View.GONE);
            return;
        }

        if (categoryIds != null && !categoryIds.isEmpty() && commonModels != null) {
            String cuisineText = commonModels.stream().filter(p -> categoryIds.contains(p.getId()))
                    .map(AppSettingTitelCommonModel::getTitle).collect(Collectors.joining(", "));
            if (!cuisineText.isEmpty()) {
                textView.setVisibility(View.VISIBLE);
                SpannableString spannableString = new SpannableString(type + cuisineText);
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.WHITE);
                spannableString.setSpan(colorSpan, 0, type.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannableString);
            } else {
                textView.setVisibility(View.GONE);
            }
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void setUserImage(UserDetailModel model, CircleImageView ivForFollow) {
        if (model == null) {
            return;
        }
        if (model.getImage() == null) {
            return;
        }
        if (model.getImage() != null && !model.getImage().isEmpty()) {
            Graphics.loadRoundImage(model.getImage(), ivForFollow);
        } else {
            Graphics.loadImageWithFirstLetter(model.getImage(), ivForFollow, model.getFirstName());
        }
    }

    private void updateVenueFollowStatus(boolean followStatus, String venueId) {
        feedAdapter.getData().forEach(p -> {
            if (p.getVenue() != null) {
                if (p.getVenue().getId().equals(venueId)) {
                    p.getVenue().setFollowing(followStatus);
                }
            }
        });
        feedAdapter.notifyDataSetChanged();
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    public void requestUserProfile(String id) {
        binding.progress.setVisibility(View.VISIBLE);
        DataService.shared(activity).requestUserProfile(id, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.getData() != null) {
                    Utils.showViews(binding.nestedScrollView);
                    userDetailModel = model.data;
                    setFriendDetails(model.data);
                    binding.linearHeader.setVisibility(View.VISIBLE);
                    binding.layoutBio.setVisibility(View.VISIBLE);
                    binding.buttonLayout.setVisibility(View.VISIBLE);
                    binding.linear.setVisibility(View.VISIBLE);
                    requestMyUserFeed(id, false);
                    requestSuggestedUser(id);
                }
            }
        });
    }

    private void requestMyUserFeed(String friendId, boolean isShowPaggationLoader) {
        if (isShowPaggationLoader) {
            binding.feedRecyclerView.setVisibility(View.VISIBLE);
            binding.pagginationProgressBar.setVisibility(View.VISIBLE);
        }
        binding.feedRecyclerView.setVisibility(View.GONE);
        DataService.shared(activity).requestFriendFeed(page, 30, friendId, new RestCallback<ContainerListModel<MyUserFeedModel>>(this) {
            @Override
            public void result(ContainerListModel<MyUserFeedModel> model, String error) {
                binding.progress.setVisibility(View.GONE);
                binding.pagginationProgressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    otherUserFeedData.addAll(model.data);
                    binding.feedRecyclerView.setVisibility(View.VISIBLE);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    filterData();
                } else {
                    if (otherUserFeedData.isEmpty()) {
                        binding.feedRecyclerView.setVisibility(View.GONE);
                        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                        if (userDetailModel != null) {
                            binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(setValue("other_user_feed_empty_message",userDetailModel.getFullName()));
                        }
                    }
                }

                binding.feedRecyclerView.setVisibility(otherUserFeedData.isEmpty() ? View.GONE : View.VISIBLE);

            }
        });
    }



    private void requestSuggestedUser(String id) {
        DataService.shared( activity ).requestSuggestedUser( id, new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data.isEmpty()) {
                    binding.suggestedUserView.setVisibility(View.GONE);
                    binding.txtUserTitle.setVisibility(View.GONE);
                } else {
                    binding.txtUserTitle.setVisibility(View.VISIBLE);
                    binding.suggestedUserView.setVisibility(View.VISIBLE);
                    binding.suggestedUserView.setSuggestedUser(model.data, OtherUserProfileActivity.this, getSupportFragmentManager(), (success, error1) -> {
                    });
                }
            }
        } );
    }

    private void reqFollowRequestAction(String status,UserDetailModel userDetailModel) {
        showProgress();
        DataService.shared(activity).requestUserFollowAction(userDetailModel.getId(),status, new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                binding.followRequestContainer.setVisibility( View.GONE );

            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class MutualFriendsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.user_image_list);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._minus10sdp);
            if (viewType == 0) {
                layoutParams.width = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._48sdp);
            }
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem(position);
            if (position < 3) {
                Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.vBinding.civPlayers, Utils.notNullString(model.getFirstName()));
            }

            viewHolder.itemView.setOnClickListener(view -> {
                Utils.preventDoubleClick(view);
                if (userDetailModel == null) {
                    return;
                }
                MutualFriendFragment mutualFriendFragment = new MutualFriendFragment();
                mutualFriendFragment.mutualFriendsList = userDetailModel;
                mutualFriendFragment.show(getSupportFragmentManager(), "");
            });
        }

        @Override
        public int getItemViewType(int position) {
            if (getItemCount() == position + 1) {
                return 0;
            } else {
                return 1;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final UserImageListBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = UserImageListBinding.bind(itemView);
            }
        }
    }

    public class FeedAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            switch (AppConstants.UserFeedType.valueOf(viewType)) {
                case FRIENDS_UPDATE:
                case VENUE_RECOMMENDATION:
                    return new FriendUpdatesHolder(UiUtils.getViewBy(parent, R.layout.item_friends_update_feed));
                case VENUE_UPDATE:
                case OFFER_RECOMMENDATION:
                    return new VenueOfferHolder(UiUtils.getViewBy(parent, R.layout.item_venue_offer_feeds));
                case EVENY_UPDATE:
                    return new EventViewHolder(UiUtils.getViewBy(parent, R.layout.event_item_offer_feed));
                case ACTIVITY_RECOMMENDATION:
                    return new ActivitytViewHolder(UiUtils.getViewBy(parent, R.layout.activity_item_feed));

            }
            return new EventViewHolder(UiUtils.getViewBy(parent, R.layout.event_item_offer_feed));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MyUserFeedModel model = (MyUserFeedModel) getItem(position);

            if (model.getBlockType() == AppConstants.UserFeedType.FRIENDS_UPDATE) {
                FriendUpdatesHolder viewHolder = (FriendUpdatesHolder) holder;
                viewHolder.setData(model);
            } else if (model.getBlockType() == AppConstants.UserFeedType.VENUE_UPDATE) {
                VenueOfferHolder venueOfferHolder = (VenueOfferHolder) holder;
                venueOfferHolder.setData(model);
            } else if (model.getBlockType() == AppConstants.UserFeedType.EVENY_UPDATE) {
                EventViewHolder viewHolder = (EventViewHolder) holder;
                viewHolder.setupData(model);
            } else if (model.getBlockType() == AppConstants.UserFeedType.ACTIVITY_RECOMMENDATION) {
                ActivitytViewHolder viewHolder = (ActivitytViewHolder) holder;
                viewHolder.setupData(model);
            } else if (model.getBlockType() == AppConstants.UserFeedType.VENUE_RECOMMENDATION) {
                FriendUpdatesHolder viewHolder = (FriendUpdatesHolder) holder;
                viewHolder.setData(model);
            } else if (model.getBlockType() == AppConstants.UserFeedType.OFFER_RECOMMENDATION) {
                VenueOfferHolder venueOfferHolder = (VenueOfferHolder) holder;
                venueOfferHolder.setData(model);
            }
        }

        @Override
        public int getItemViewType(int position) {
            MyUserFeedModel model = (MyUserFeedModel) getItem(position);
            return model.getBlockType().getValue();
        }

        public class FriendUpdatesHolder extends RecyclerView.ViewHolder {
            private final ItemFriendsUpdateFeedBinding mBinding;

            public FriendUpdatesHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemFriendsUpdateFeedBinding.bind(itemView);
            }

            public void setData(MyUserFeedModel model) {
                if (model == null) {
                    return;
                }

                setUserImage(userDetailModel, mBinding.ivForFollow);
                String tmpTitle = model.getType().equals("venue_recommendation") ? getValue("venue_recommendation") : getValue("venue_followed");
                if (!TextUtils.isEmpty(userName)) {
                    mBinding.tvUserName.setText(Html.fromHtml(userName + tmpTitle));
                }

                mBinding.tvTime.setText(Utils.getTimeAgo(model.getCreatedAt(), activity));

                if (model.getVenue() != null) {
                    mBinding.venueContainer.setVenueDetail(model.getVenue());
                    Graphics.loadImage(model.getVenue().getCover(), mBinding.img);
                    mBinding.btnFollowButton.setVenueRequestStatus(model.getVenue());

                    mBinding.tvDressCode.setText(MessageFormat.format("{0}{1}", getValue("dress_code"), model.getVenue().getDressCode()));
                    mBinding.tvDressCode.setVisibility(TextUtils.isEmpty(model.getVenue().getDressCode()) ? View.GONE : View.VISIBLE);

                    if (model.getType().equals("venue_recommendation")) {
                        setVisibilityAndText(model.getVenue().getFeature(), getValue("features"), mBinding.tvFeature);
                        setVisibilityAndText(model.getVenue().getCuisine(), getValue("cuisine"), mBinding.tvCuisine);
                        setVisibilityAndText(model.getVenue().getMusic(),  getValue("music"), mBinding.tvMusic);
                    } else {
                        setTitleFromList(model.getVenue().getCuisine(), AppSettingManager.shared.getAppSettingData().getCuisine(), getValue("cuisine"), mBinding.tvCuisine);
                        setTitleFromList(model.getVenue().getMusic(), AppSettingManager.shared.getAppSettingData().getMusic(),  getValue("music"), mBinding.tvMusic);
                        setTitleFromList(model.getVenue().getFeature(), AppSettingManager.shared.getAppSettingData().getFeature(), getValue("features"), mBinding.tvFeature);

                    }
                }

                mBinding.getRoot().setOnClickListener(view -> {
                    if (model.getVenue() != null) {
                        Graphics.openVenueDetail(activity, model.getVenue().getId());
                    }
                });

                mBinding.btnFollowButton.setOnClickListener(view -> {
                    if (model.getVenue() != null) {
                         mBinding.btnFollowButton.requestFollowUnfollowVenue( model.getVenue(),activity, (success, message) -> {
                             if (message.equals("Unfollowed!")) {
                                 model.getVenue().setIsFollowing(false);
                                 updateVenueFollowStatus(false, model.getVenue().getId());
                                 Alerter.create(activity).setTitle(getValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(setValue("unfollow_toast",model.getVenue().getName())).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                             } else {
                                 model.getVenue().setIsFollowing(true);
                                 updateVenueFollowStatus(true, model.getVenue().getId());
                                 Alerter.create(activity).setTitle(getValue("thank_you")).setText(setValue("following_toast",model.getVenue().getName())).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                             }
                             EventBus.getDefault().post(model.getVenue());
                        });
                    }
                });
            }



            private void setVisibilityAndText(List<String> list, String header, TextView textView) {
                if (list == null || list.isEmpty()) {
                    textView.setVisibility(View.GONE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    String featureHeader = header;
                    String featureText = TextUtils.join(", ", list);
                    SpannableString spannableString = new SpannableString(featureHeader + featureText);
                    ForegroundColorSpan whiteColorSpan = new ForegroundColorSpan(Color.WHITE);
                    spannableString.setSpan(whiteColorSpan, 0, featureHeader.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(spannableString);
                }
            }

        }

        public class VenueOfferHolder extends RecyclerView.ViewHolder {

            private final ItemVenueOfferFeedsBinding vbinding;

            private final OfferPackagesAdapter<PackageModel> adapter = new OfferPackagesAdapter<>();

            public VenueOfferHolder(@NonNull View itemView) {
                super(itemView);
                vbinding = ItemVenueOfferFeedsBinding.bind(itemView);

                vbinding.dateTitleFrom.setText(getValue("and_from"));
                vbinding.tvTillDateTitle.setText(getValue("and_till"));

                vbinding.buttonOne.setText(getValue("invite_your_friends"));
                vbinding.buttonTwo.setText(getValue("claim_discount"));
                vbinding.buttonThree.setText(getValue("invite_your_friends"));

                vbinding.venueSubRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                vbinding.venueSubRecycler.setAdapter(adapter);
            }

            public void setData(MyUserFeedModel model) {
                if (model.getOffer() == null) {
                    return;
                }
                setUserImage(userDetailModel, vbinding.ivForFollow);

                Graphics.loadImage(model.getOffer().getImage(), vbinding.img);
                vbinding.txtTitle.setText(model.getOffer().getTitle());
                vbinding.tvDescription.setText(model.getOffer().getDescription());
                vbinding.txtDays.setText(model.getOffer().getDays());

                vbinding.tvTime.setText(Utils.getTimeAgo(model.getCreatedAt(), activity));

                vbinding.tvUserName.setText(Html.fromHtml(userName + (model.getType().equals("offer_recommendation") ? getValue("venue_recommendation") : getValue("venue_followed"))));

                if (TextUtils.isEmpty(model.getOffer().getStartTime())) {
                    vbinding.startDate.setText(getValue("ongoing"));
                    vbinding.layoutEndDate.setVisibility(View.GONE);

                } else {
                    vbinding.layoutEndDate.setVisibility(View.VISIBLE);
                    vbinding.startDate.setText(Utils.convertMainDateFormat(model.getOffer().getStartTime()));
                    vbinding.endDate.setText(Utils.convertMainDateFormat(model.getOffer().getEndTime()));
                }
                vbinding.txtOfferTime.setText(model.getOffer().getOfferTiming());
                vbinding.btnTimeInfo.setVisibility(model.getOffer().isShowTimeInfo() ? View.GONE : View.VISIBLE);
                vbinding.layoutTimeInfo.setOnClickListener(v -> {
                    if (!model.getOffer().isShowTimeInfo()) {
                        if (model.getOffer() == null) {
                            return;
                        }
                        if (model.getOffer().getVenue() == null) {
                            return;
                        }
                        VenueTimingDialog dialog = new VenueTimingDialog(model.getOffer().getVenue().getTiming(), activity);
                        dialog.show(getSupportFragmentManager(), "1");
                    }
                });

                Utils.setupOfferButtons(model.getOffer(), vbinding.buttonOne, vbinding.buttonTwo, vbinding.buttonThree);

                View.OnClickListener buttonClick = v -> {
                    Utils.preventDoubleClick(v);
                    if (model.getOffer() == null) { return; }
                    if (model.getOffer().getVenue() == null) { return; }
                    TextView button = (TextView) v;
                    String buttonText = button.getText().toString();
                    if (buttonText.equalsIgnoreCase(getValue("buy_now"))) {
                        startActivity(new Intent(activity, VenueBuyNowActivity.class).putExtra("venueObjectModel", new Gson().toJson(model.getOffer().getVenue())).putExtra("offerModel", new Gson().toJson(model.getOffer())));
                    } else if (buttonText.equalsIgnoreCase(getValue("claim_discount"))) {
                        Utils.openClaimScreen(model.getOffer().getSpecialOfferModel(), model.getOffer().getVenue(), activity);
                    }
//                    else {
//                        Utils.openInviteButtonSheet(model.getOffer(), model.getOffer().getVenue(), getSupportFragmentManager());
//                    }
                };

                vbinding.buttonOne.setOnClickListener(buttonClick);
                vbinding.buttonTwo.setOnClickListener(buttonClick);
                vbinding.buttonThree.setOnClickListener(buttonClick);

                if (!model.getOffer().getPackages().isEmpty()) {
                    adapter.updateData(model.getOffer().getPackages());
                    vbinding.venueSubRecycler.setVisibility(View.VISIBLE);
                }
                setListeners(model);
            }

            private void setListeners(MyUserFeedModel model) {

                vbinding.iconMenu.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    ArrayList<String> data = new ArrayList<>();
                    data.add(getValue("share_venue"));
                    data.add(getValue("share_offer"));
                    Graphics.showActionSheet(activity, model.getOffer().getTitle(), data, (data1, position1) -> {
                        switch (position1) {
                            case 0:
                                BucketListBottomSheet dialog = new BucketListBottomSheet();
                                dialog.offerId = model.getOffer().getId();
                                dialog.show(getSupportFragmentManager(), "");
                                break;
                            case 1:
                                startActivity( new Intent(activity, VenueShareActivity.class )
                                        .putExtra( "venue", new Gson().toJson( model.getOffer().getVenue() ) )
                                        .putExtra( "type", "venue" ) );
                                break;
                            case 2:
                                startActivity( new Intent( activity, VenueShareActivity.class )
                                        .putExtra( "offer", new Gson().toJson( model.getOffer() ) )
                                        .putExtra( "type", "offer" ) );
                                break;
                        }
                    });

                });


                vbinding.getRoot().setOnClickListener(view -> {
                    OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
                    dialog.offerId = model.getOffer().getId();
                    dialog.show(getSupportFragmentManager(), "");
                    // Utils.requestvenueOfferDetail( activity, model.getOffer().getId() );
                });

            }
        }

        public class EventViewHolder extends RecyclerView.ViewHolder {

            private final EventItemOfferFeedBinding binding;

            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = EventItemOfferFeedBinding.bind(itemView);
            }

            public void setupData(MyUserFeedModel model) {


                if (userDetailModel != null) {
                    binding.tvUserName.setText(Html.fromHtml(userDetailModel.getFirstName() + " " + userDetailModel.getLastName() + getValue("checked_in_bold")));
                    setUserImage(userDetailModel, binding.ivForFollow);
                }

                binding.tvTime.setText(Utils.getTimeAgo(model.getCreatedAt(), activity));
                Graphics.loadImage(model.getEvent().getImage(), binding.img);

                if (!model.getEvent().getEventOrg().isEmpty()) {
                    binding.tvEventEmail.setText(model.getEvent().getEventOrg().get(0).getEmail());
                    binding.tvTitle.setText(model.getEvent().getEventOrg().get(0).getName());
                    Graphics.loadRoundImage(model.getEvent().getEventOrg().get(0).getLogo(), binding.imgEventOrg);
                }


                if (model.getEvent().getVenue() != null) {
                    binding.venueDetails.setVisibility(View.VISIBLE);
                    binding.txtUserName.setText(model.getEvent().getVenue().getName());
                    binding.tvAddress.setText(model.getEvent().getVenue().getAddress());
                    Graphics.loadRoundImage(model.getEvent().getVenue().getLogo(), binding.imgUserLogo);
                }
                else {
                    binding.venueDetails.setVisibility(View.GONE);
                }

                binding.txtDate.setText(Utils.convertMainDateFormat(model.getEvent().getEventTime()));
                binding.txtTime.setText(Utils.convertMainTimeFormat(model.getEvent().getReservationTime()) + " - " + Utils.convertMainTimeFormat(model.getEvent().getEventTime()));

                binding.tvEventTitle.setText(model.getEvent().getTitle());
                binding.tvEventDes.setText(model.getEvent().getDescription());

                binding.tvTime.setText(Utils.getTimeAgo(model.getCreatedAt(), activity));

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(activity, EventDetailsActivity.class);
                    intent.putExtra("eventId", model.getEvent().getId());
                    if (model.getEvent().getEventOrg() != null) {
                        intent.putExtra("name", model.getEvent().getEventOrg().get(0).getName());
                        intent.putExtra("address", model.getEvent().getEventOrg().get(0).getWebsite());
                        intent.putExtra("image", model.getEvent().getEventOrg().get(0).getLogo());
                    }
                    if (model.getVenue() != null) {
                        intent.putExtra("venueId", model.getVenue().getId());
                        intent.putExtra("venueModel", new Gson().toJson(model.getVenue()));
                    }
                    startActivity(intent);
                });

            }

        }

        public class ActivitytViewHolder extends RecyclerView.ViewHolder {

            private final ActivityItemFeedBinding binding;

            public ActivitytViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ActivityItemFeedBinding.bind(itemView);
            }

            public void setupData(MyUserFeedModel model) {

                if (model == null) {
                    return;
                }

                setUserImage(model.getUser(), binding.ivForFollow);

                binding.tvUserName.setText(Html.fromHtml(userName + "<b> Recommended </b>"));

                binding.tvName.setText(model.getActivityDetailModel().getName());
                binding.tvDescription.setText(model.getActivityDetailModel().getDescription());
                Graphics.loadImage(model.getActivityDetailModel().getCoverImage(), binding.img);

                binding.startDate.setText(Utils.convertMainDateFormat(model.getActivityDetailModel().getStartDate()));
                binding.endDate.setText(Utils.convertMainDateFormat(model.getActivityDetailModel().getEndDate()));


                binding.tvTime.setText(Utils.getTimeAgo(model.getCreatedAt(), activity));

                if (model.getActivityDetailModel().getProvider() != null) {
                    Graphics.loadRoundImage(model.getActivityDetailModel().getProvider().getLogo(), binding.iconImg);
                    binding.tvTitle.setText(model.getActivityDetailModel().getProvider().getName());
                    binding.tvAddress.setText(model.getActivityDetailModel().getProvider().getAddress());
                }


                binding.getRoot().setOnClickListener(view -> {
                    if (model.getActivityDetailModel().getProvider() == null) {
                        return;
                    }
                    Intent intent = new Intent(activity, ActivityListDetail.class);
                    intent.putExtra("activityId", model.getActivityDetailModel().getId()).putExtra("name", model.getActivityDetailModel().getName());
                    intent.putExtra("image", model.getActivityDetailModel().getProvider().getLogo());
                    intent.putExtra("title", model.getActivityDetailModel().getProvider().getName());
                    intent.putExtra("address", model.getActivityDetailModel().getProvider().getAddress());
                    startActivity(intent);
                });

            }
        }


    }


    // endregion
    // --------------------------------------

}