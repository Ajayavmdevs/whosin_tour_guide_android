package com.whosin.business.ui.activites.Profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.ActivityOtherUserProfileBinding;
import com.whosin.business.databinding.UserImageListBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.CheckUserSession;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.ChatModel;
import com.whosin.business.service.models.CommonModel;
import com.whosin.business.service.models.ContactListModel;
import com.whosin.business.service.models.ContainerListModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.MyUserFeedModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.comman.BaseActivity;
import com.whosin.business.ui.activites.home.Chat.ChatMessageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OtherUserProfileActivity extends BaseActivity {

    private ActivityOtherUserProfileBinding binding;
    private final MutualFriendsAdapter<ContactListModel> userAdapter = new MutualFriendsAdapter<>();

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
            }
        });

        binding.tvApprove.setOnClickListener( view -> {

            reqFollowRequestAction("approved",userDetailModel);
        } );

        binding.tvReject.setOnClickListener( view -> {
            reqFollowRequestAction("rejected",userDetailModel);

        } );

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

        }


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


    // endregion
    // --------------------------------------

}