package com.whosin.app.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.BooleanResult;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.roundcornerlayout.CornerType;
import com.whosin.app.comman.ui.roundcornerlayout.RoundCornerLinearLayout;
import com.whosin.app.service.DataService;
import com.whosin.app.service.Repository.ContactRepository;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.FollowUnfollowModel;
import com.whosin.app.service.models.FollowUpdateEventModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;

import org.greenrobot.eventbus.EventBus;

public class VenueFollowButton extends ConstraintLayout {

    private TextView txtTitle;
    private RoundCornerLinearLayout mContainer;
    private ProgressBar progressBar;

    private VenueObjectModel venueObjectModel;
    private Context context;
    private BooleanResult callBack;
    private Activity activity;

    public VenueFollowButton(Context context) {
        this(context,null);
    }

    public VenueFollowButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VenueFollowButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VenueFollowButton, 0, 0);
        String titleText = a.getString(R.styleable.VenueFollowButton_venueFollowButtonTitle);

        Drawable bgRec = a.getDrawable(R.styleable.VenueFollowButton_venueFollowButtonBg);
        if (bgRec == null) {
            bgRec = AppCompatResources.getDrawable(context,R.drawable.button_border);
        }
        int colorRes = a.getColor(R.styleable.VenueFollowButton_venueFollowButtonTintColor, Color.BLACK);
        int bgColorRes = a.getColor(R.styleable.VenueFollowButton_venueFollowButtonBgColor, 0);

        int cornerRadius = (int) a.getDimension(R.styleable.VenueFollowButton_venueFollowButtonCorner, 50);

        a.recycle();

        LayoutInflater.from(context).inflate(R.layout.layout_venue_follow_button, this);

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

    public void requestFollowUnfollowVenue(VenueObjectModel venueObjectModel, Activity activity, BooleanResult callBack) {
        this.callBack = callBack;
        this.venueObjectModel = venueObjectModel;
        this.activity=activity;
        reqVenueFollowUnFollow();
    }

    public void setVenueRequestStatus(VenueObjectModel venueObjectModel) {
        if (venueObjectModel != null) {
            setTxtTitle(venueObjectModel.isIsFollowing() ? Utils.getLangValue("following") : Utils.getLangValue("follow"));
        }
    }

    private void reqVenueFollowUnFollow() {
        startProgress();
        DataService.shared(activity).requestVenueFollow(venueObjectModel.getId(), new RestCallback<ContainerModel<FollowUnfollowModel>>(null) {
            @Override
            public void result(ContainerModel<FollowUnfollowModel> model, String error) {
                stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                callBack.success(true,model.message);

            }
        });
    }

}
