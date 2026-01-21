package com.whosin.app.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.BooleanResult;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.SuggestedUserListItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.FollowUnfollowModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.CmPublicProfileActivity;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.PromoterPublic.PromoterPublicProfileActivity;

public class SuggestedUserAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    public Activity activity;

    private final String follow = Utils.getLangValue("follow");
    private final String following = Utils.getLangValue("following");
    private final String requested = Utils.getLangValue("requested");
    private final String thank_you = Utils.getLangValue("thank_you");
    private final String oh_snap = Utils.getLangValue("oh_snap");

    public SuggestedUserAdapter(Activity activity, FragmentManager fragmentManager) {
        this.activity = activity;
    }

    public SuggestedUserAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = UiUtils.getViewBy(parent, R.layout.suggested_user_list_item);
        view.getLayoutParams().width = (int) (Graphics.getScreenWidth(activity) * 0.35);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        UserDetailModel model = (UserDetailModel) getItem(position);
        viewHolder.mBinding.tvName.setText(model.getFullName());
        Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.mBinding.image, model.getFullName());

        viewHolder.mBinding.btnFollow.setOnClickListener(v -> viewHolder.reqFollowUnFollow(model, (success, message) -> {
            if (success) {
                if (message.equals(follow)) {
                    viewHolder.mBinding.btnFollow.setBackgroundResource(R.drawable.follow_button_bg);
                } else {
                    viewHolder.mBinding.btnFollow.setBackgroundResource(R.drawable.contact_follow_btn_bg);
                }
            }
        }));
        viewHolder.setFollowUnFollowStatus(model);
        viewHolder.mBinding.ivClose.setOnClickListener(v -> viewHolder.requestUserRemoveSuggestion(model, (success, error1) -> {
            if (success) {
                removeItem(position);
                notifyDataSetChanged();
            }
        }));
        viewHolder.mBinding.getRoot().setOnClickListener(v -> {
            Intent intent;
//            activity.startActivity(new Intent(activity, OtherUserProfileActivity.class).putExtra("friendId", model.getId()));
            if (SessionManager.shared.getUser().isRingMember() && model.isPromoter()) {
                intent = new Intent(activity, PromoterPublicProfileActivity.class);
                intent.putExtra("isFromOtherUserProfile", true);
                intent.putExtra("isPromoterProfilePublic", true);
                intent.putExtra("id", model.getId());
            } else if (SessionManager.shared.getUser().isPromoter() && model.isRingMember()) {
                intent = new Intent(activity, CmPublicProfileActivity.class);
                intent.putExtra("isFromOtherUserProfile", true);
                intent.putExtra("promoterUserId", model.getId());
            } else {
                intent = new Intent(activity, OtherUserProfileActivity.class);
                intent.putExtra("friendId", model.getId());
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final SuggestedUserListItemBinding mBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = SuggestedUserListItemBinding.bind(itemView);
        }

        private void setFollowUnFollowStatus(UserDetailModel model) {
            if (model == null) {
                return;
            }
            mBinding.btnFollow.setText(Utils.followButtonTitle(model.getFollow()));
        }

        private void reqFollowUnFollow(UserDetailModel userDetailModel, BooleanResult callBack) {
            Graphics.showProgress(activity);
            DataService.shared(activity).requestUserFollowUnFollow(userDetailModel.getId(), new RestCallback<ContainerModel<FollowUnfollowModel>>() {
                @Override
                public void result(ContainerModel<FollowUnfollowModel> model, String error) {
                    if (!Utils.isValidActivity(activity)) {
                        return;
                    }
                    Graphics.hideProgress(activity);
                    if (!Utils.isNullOrEmpty(error) || model == null || model.getData() == null) {
                        Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    userDetailModel.setFollow(model.getData().getStatus());
                    switch (model.getData().getStatus()) {
                        case "unfollowed":
                            mBinding.btnFollow.setText(follow);
                            callBack.success(true, follow);
                            Alerter.create(activity).setTitle(oh_snap).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(Utils.setLangValue("unfollow_toast",userDetailModel.getFullName())).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                            break;
                        case "approved":
                            mBinding.btnFollow.setText(following);
                            callBack.success(true, following);
                            Alerter.create(activity).setTitle(thank_you).setText(Utils.setLangValue("follow_venue",userDetailModel.getFullName())).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                            break;
                        case "pending":
                            callBack.success(true, requested);
                            mBinding.btnFollow.setText(requested);
                            Alerter.create(activity).setTitle(thank_you).setText(Utils.setLangValue("requested_for_follow",userDetailModel.getFullName())).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                            break;
                        case "cancelled":
                            callBack.success(true, follow);
                            mBinding.btnFollow.setText(follow);
                            Alerter.create(activity).setTitle(oh_snap).setText(Utils.setLangValue("requested_cancel_for_follow",userDetailModel.getFullName())).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                            break;
                    }

                }
            });
        }

        private void requestUserRemoveSuggestion(UserDetailModel user, BooleanResult callBack) {
            DataService.shared(activity).requestUserRemoveSuggestion("user", user.getId(), new RestCallback<ContainerModel<CommonModel>>() {
                @Override
                public void result(ContainerModel<CommonModel> model, String error) {
                    if (!Utils.isNullOrEmpty(error) || model == null) {
                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Alerter.create(activity).setTitle(thank_you).setText(Utils.setLangValue("remove_suggested_user",user.getName())).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                    callBack.success(true, "");
                }
            });
        }

    }

}
