package com.whosin.app.ui.controller.promoter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UserInfoView;
import com.whosin.app.databinding.LayoutPromoterProfileHeaderViewBinding;
import com.whosin.app.service.manager.ComplementaryProfileManager;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.StorySeenEventModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.ui.activites.CmProfile.CmProfileActivity;
import com.whosin.app.ui.activites.Promoter.PromoterMyProfile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PromoterProfileHeaderView extends ConstraintLayout {

    private LayoutPromoterProfileHeaderViewBinding binding;

    public Activity activity;

    public boolean isFromCM = false;

    public UserDetailModel userDetailModel;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public PromoterProfileHeaderView(Context context) {
        this(context, null);
    }

    public PromoterProfileHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PromoterProfileHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_promoter_profile_header_view, this);
        binding = LayoutPromoterProfileHeaderViewBinding.bind(view);


        binding.ivClose.setOnClickListener(view1 -> {
            Activity activity = getActivity();
            if (isFromCM){
                if (activity instanceof CmProfileActivity) {
                    CmProfileActivity cmProfileActivity = (CmProfileActivity) activity;
                    if (cmProfileActivity.callback != null) {
                        cmProfileActivity.callback.onReceive(true);
                    }
                }
            }else {
                PromoterProfileManager.shared.setProfileCallBack.onReceive(true);
            }

        });




    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------


    public void setUpData(Activity activity ,UserDetailModel model) {

//
//        if(!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }


        if (binding == null) {
            return;
        }

        if (model == null){return;}
        this.userDetailModel = model;
        Graphics.applyBlurEffect(activity, binding.headerBlurView);
        String name = model.getFirstName() + " " + model.getLastName();
        binding.headertitle.setText(name);
        Graphics.loadImageWithFirstLetter(model.getImage(), binding.headerIv, name);

        binding.ivShare.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            if (userDetailModel == null) {
                return;
            }
            Utils.generateDynamicLinks(activity, userDetailModel);
        });

    }



    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(UserDetailModel model) {
//        if (model == null) { return; }
//        if (binding == null) { return; }
//
////        Graphics.applyBlurEffect(activity, binding.headerBlurView);
////        String name = model.getFirstName() + " " + model.getLastName();
////        binding.headertitle.setText(name);
////        Graphics.loadImageWithFirstLetter(model.getImage(), binding.headerIv, name);
//    }

//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        EventBus.getDefault().unregister(this);
//    }

    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
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
