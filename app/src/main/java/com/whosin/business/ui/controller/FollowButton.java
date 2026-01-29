package com.whosin.business.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tapadoo.alerter.Alerter;
import com.whosin.business.R;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.roundcornerlayout.CornerType;
import com.whosin.business.comman.ui.roundcornerlayout.RoundCornerLinearLayout;
import com.whosin.business.service.DataService;
import com.whosin.business.service.Repository.ContactRepository;
import com.whosin.business.service.models.ContactListModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.FollowUnfollowModel;
import com.whosin.business.service.models.FollowUpdateEventModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.service.rest.RestCallback;

import org.greenrobot.eventbus.EventBus;

public class FollowButton extends ConstraintLayout {

    private TextView txtTitle;
    private RoundCornerLinearLayout mContainer;
    private ProgressBar progressBar;

    private UserDetailModel userDetailModel;
    private ContactListModel contactListModel;
    private Context context;

    private Activity activity;

    public FollowButton(Context context) {
        this(context,null);
    }

    public FollowButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FollowButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FollowButton, 0, 0);
        String titleText = a.getString(R.styleable.FollowButton_followButtonTitle);

        Drawable bgRec = a.getDrawable(R.styleable.FollowButton_followButtonBg);
        if (bgRec == null) {
            bgRec = AppCompatResources.getDrawable(context,R.drawable.button_border);
        }
        int colorRes = a.getColor(R.styleable.FollowButton_followButtonTintColor, Color.BLACK);
        int bgColorRes = a.getColor(R.styleable.FollowButton_followButtonBgColor, 0);

        int cornerRadius = (int) a.getDimension(R.styleable.FollowButton_followButtonCorner, 50);

        a.recycle();

        LayoutInflater.from(context).inflate(R.layout.layout_follow_button, this);

        mContainer = findViewById(R.id.mainContainer);
        mContainer.setCornerRadius(cornerRadius, CornerType.ALL);
        if (bgRec != null) {
            mContainer.setBackground(bgRec);
        }

        if (bgColorRes != 0) {
            mContainer.setBackgroundColor(bgColorRes);
        }

        txtTitle = findViewById(R.id.titleText);
        txtTitle.setText(titleText);
        txtTitle.setTextColor(colorRes);

        progressBar = findViewById(R.id.progressView);
        progressBar.setVisibility(GONE);
    }

    public void setTxtTitle(String title) {
        txtTitle.setText(title);
    }

    public String getTxtTitle() {
        return txtTitle.getText().toString();
    }

    public void setBg(Drawable bgRec){
        mContainer.setBackgroundColor(Color.TRANSPARENT);
        mContainer.setBackground(bgRec);
    }

    public void setBgColor(int bgColorRes) {
        mContainer.setBackgroundColor(bgColorRes);
    }

    public void setTintColor(@ColorInt int colorRes) {
        txtTitle.setTextColor(colorRes);

    }


    public void startProgress() {
        setEnabled(false);
        progressBar.setVisibility(VISIBLE);
        txtTitle.setVisibility(INVISIBLE);
    }

    public void stopProgress() {
        setEnabled(true);
        progressBar.setVisibility(INVISIBLE);
        txtTitle.setVisibility(VISIBLE);
    }

    public void requestFollowUnfollow(UserDetailModel userDetailModel,Activity activity) {
        this.userDetailModel = userDetailModel;
        this.activity=activity;
        reqFollowUnFollow();
    }

    public void requestFollowUnfollow(ContactListModel contactListModel, Activity activity) {
        this.contactListModel = contactListModel;
        this.activity=activity;
        reqFollowUnFollow();
    }

    public void setUserRequestStatus(UserDetailModel userDetailModel) {
        if (!userDetailModel.getFollow().isEmpty()) {
            if (userDetailModel.getFollow().equals("approved")) {
                setTxtTitle(Utils.getLangValue("following"));
            } else if (userDetailModel.getFollow().equals("na") || userDetailModel.getFollow().equals("none")) {
                setTxtTitle(Utils.getLangValue("follow"));
            } else if (userDetailModel.getFollow().equals(Utils.getLangValue("pending"))) {
                setTxtTitle(Utils.getLangValue("requested"));
            } else {
                setTxtTitle(Utils.getLangValue("follow"));
            }
        }
    }

    private void reqFollowUnFollow() {
        startProgress();
        DataService.shared(activity).requestUserFollowUnFollow(userDetailModel.getId(), new RestCallback<ContainerModel<FollowUnfollowModel>>() {
            @Override
            public void result(ContainerModel<FollowUnfollowModel> model, String error) {
                stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null || model.getData() == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                userDetailModel.setFollow(model.getData().getStatus());
                setTxtTitle(Utils.followButtonTitle(userDetailModel.getFollow()));
                switch (model.getData().getStatus()) {
                    case "unfollowed":
                        Alerter.create(activity).setTitle(Utils.getLangValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(Utils.setLangValue("unfollow_toast",userDetailModel.getFullName())).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "approved":
                        Alerter.create(activity).setTitle(Utils.getLangValue("thank_you")).setText(Utils.setLangValue("follow_venue",userDetailModel.getFullName())).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "pending":
                        Alerter.create(activity).setTitle(Utils.getLangValue("thank_you")).setText(Utils.setLangValue("requested_for_follow",userDetailModel.getFullName())).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "cancelled":
                        Alerter.create(activity).setTitle(Utils.getLangValue("oh_snap")).setText(Utils.setLangValue("requested_cancel_for_follow",userDetailModel.getFullName())).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                }
                ContactRepository.shared(activity).updateUserFollowStatus(userDetailModel.getId(), model.getData().getStatus());
                EventBus.getDefault().post(new FollowUpdateEventModel(userDetailModel.getId(), model.getData().getStatus()));
                EventBus.getDefault().post(userDetailModel);
            }
        });
    }



}
