package com.whosin.business.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.business.R;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.databinding.LayoutUserFollowOptionBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.Repository.ContactRepository;
import com.whosin.business.service.models.ChatModel;
import com.whosin.business.service.models.CommonModel;
import com.whosin.business.service.models.ContactListModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.FollowUnfollowModel;
import com.whosin.business.service.models.FollowUpdateEventModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.home.Chat.ChatMessageActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Objects;

public class UserOptionView extends LinearLayout {
    private UserDetailModel userDetailModel;
    private ContactListModel contactListModel;
    private LayoutUserFollowOptionBinding binding;

    private Activity activity;
    private CommanCallback<Boolean> callback;

    // endregion
    // --------------------------------------
    // region Lifecycle
    // --------------------------------------

    public UserOptionView(Context context) {
        this(context,null);
    }

    public UserOptionView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UserOptionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_user_follow_option, this);
        binding = LayoutUserFollowOptionBinding.bind(view);
    }


//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        EventBus.getDefault().unregister(this);
//    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public void setupView(UserDetailModel userDetailModel, Activity activity, CommanCallback<Boolean> callback) {
        this.activity = activity;
        this.userDetailModel = userDetailModel;
        this.callback = callback;
        if (binding == null) { return; }
        setupData();
    }

    public void setupView(ContactListModel contactListModel, Activity activity, CommanCallback<Boolean> callback) {
        this.activity = activity;
        this.contactListModel = contactListModel;
        this.callback = callback;
        if (binding == null) { return; }
        setupData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FollowUpdateEventModel event) {

        if (binding == null) { return; }
        if (event == null) { return; }
        if (userDetailModel != null && (Objects.equals(userDetailModel.getId(), event.id))) {
            userDetailModel.setFollow(event.status);
            setUserRequestStatus(userDetailModel.getFollow());
        } else if (contactListModel != null && (Objects.equals(contactListModel.getId(), event.id))) {
            contactListModel.setFollow(event.status);
            Log.d("UserOptionView", "onMessageEvent: " + event.status + " user : " + contactListModel.getFullName());
            setUserRequestStatus(event.status);
        }
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setupData() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (userDetailModel != null) {
            setUserRequestStatus(userDetailModel.getFollow());
        } else if (contactListModel != null) {
            setUserRequestStatus(contactListModel.getFollow());
        } else {
            binding.followButton.setVisibility(View.GONE);
            binding.btnMenu.setVisibility(View.GONE);
            binding.btnChat.setVisibility(View.GONE);
        }

        binding.followButton.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            followUnfollow();
        });

        binding.btnChat.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            openChat();
        });
        binding.btnMenu.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            openMenu();
        });
    }
    private void followUnfollow() {
        if (userDetailModel != null) {
            reqFollowUnFollow(userDetailModel.getId());
        } else if (contactListModel != null) {
            reqFollowUnFollow(contactListModel.getId());
        }
    }

    private void setUserRequestStatus(String status) {
        if (!TextUtils.isEmpty(status)) {
            binding.txtFollowButton.setText(Utils.followButtonTitle(status));
            if (status.equals("approved")) {
                binding.followButton.setVisibility(View.GONE);
                binding.btnMenu.setVisibility(View.VISIBLE);
                binding.btnChat.setVisibility(View.VISIBLE);
            } else {
                binding.followButton.setVisibility(View.VISIBLE);
                binding.btnMenu.setVisibility(View.GONE);
                binding.btnChat.setVisibility(View.GONE);
            }
        } else {
            binding.followButton.setVisibility(View.VISIBLE);
            binding.btnMenu.setVisibility(View.GONE);
            binding.btnChat.setVisibility(View.GONE);
        }
    }

    private void openChat() {
        ChatModel chatModel;
        if (userDetailModel != null) {
            chatModel = new ChatModel(userDetailModel);
        } else {
            chatModel = new ChatModel(contactListModel);
        }
        Intent intent = new Intent(activity, ChatMessageActivity.class);
        intent.putExtra("chatModel", new Gson().toJson(chatModel));
        intent.putExtra("isChatId", true);
        intent.putExtra("type", "friend");
        activity.startActivity(intent);
    }

    private void openMenu() {
        ArrayList<String> data = new ArrayList<>();
        data.add(Utils.getLangValue("unfollow"));
        data.add(Utils.getLangValue("block"));
        Graphics.showActionSheet(activity, activity.getString(R.string.app_name), data, (data1, position1) -> {
            switch (position1) {
                case 0:
                    followUnfollow();
                    break;
                case 1:
                    blockUser();
                    break;
            }
        });
    }

    private void blockUser() {
        String userId;
        String userName;
        if (userDetailModel != null) {
            userId = userDetailModel.getId();
            userName = userDetailModel.getFullName();
        } else if (contactListModel != null) {
            userId = contactListModel.getId();
            userName = contactListModel.getFullName();
        } else {
            userId = "";
            userName = "";
        }
        Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), Utils.setLangValue("block_user_alert",userName), aBoolean -> {
            if (aBoolean) {
                requestBlockUserAdd(userId, userName);
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void startProgress() {
        binding.followButton.setVisibility(View.VISIBLE);
        binding.progressView.setVisibility(View.VISIBLE);
        binding.txtFollowButton.setVisibility(View.GONE);
        binding.btnMenu.setVisibility(View.GONE);
        binding.btnChat.setVisibility(View.GONE);
        binding.followButton.setEnabled(false);
    }

    private void stopProgress() {
        binding.progressView.setVisibility(View.GONE);
        binding.txtFollowButton.setVisibility(View.VISIBLE);
        binding.followButton.setEnabled(true);
    }

    private void reqFollowUnFollow(String id) {
        startProgress();
        DataService.shared(activity).requestUserFollowUnFollow(id, new RestCallback<ContainerModel<FollowUnfollowModel>>(null) {
            @Override
            public void result(ContainerModel<FollowUnfollowModel> model, String error) {
                stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null || model.getData() == null) {
                    if (Utils.isValidActivity(activity)) {
                        Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                setUserRequestStatus(model.getData().getStatus());
                String userName = "";
                if (userDetailModel != null) {
                    userDetailModel.setFollow(model.getData().getStatus());
                    userName = userDetailModel.getFullName();
                } else if (contactListModel != null) {
                    userName = contactListModel.getFullName();
                    contactListModel.setFollow(model.getData().getStatus());
                }


                switch (model.getData().getStatus()) {
                    case "unfollowed":
                        Alerter.create(activity).setTitle(Utils.getLangValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(Utils.setLangValue("unfollow_toast",userName)).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "approved":
                        Alerter.create(activity).setTitle(Utils.getLangValue("thank_you")).setText(Utils.setLangValue("follow_venue",userName)).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "pending":
                        Alerter.create(activity).setTitle(Utils.getLangValue("thank_you")).setText(Utils.setLangValue("requested_for_follow",userName)).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "cancelled":
                        Alerter.create(activity).setTitle(Utils.getLangValue("oh_snap")).setText(Utils.setLangValue("requested_cancel_for_follow",userName)).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                }
                if (callback != null) {
                    callback.onReceive(true);
                }
                ContactRepository.shared(activity).updateUserFollowStatus(id, model.getData().getStatus());
                EventBus.getDefault().post(new FollowUpdateEventModel(id, model.getData().getStatus()));
            }
        });
    }

    private void requestBlockUserAdd(String id, String name) {
        Graphics.showProgress(activity);
        DataService.shared(activity).requestBlockUser(id, new RestCallback<ContainerModel<CommonModel>>(null) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                Graphics.hideProgress(activity);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Alerter.create(activity).setTitle(Utils.getLangValue("oh_snap")).setText(Utils.getLangValue("you_have_blocked") + " " + name).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                if (callback != null) {
                    callback.onReceive(true);
                }
            }
        });
    }

    // endregion
    // --------------------------------------
}
