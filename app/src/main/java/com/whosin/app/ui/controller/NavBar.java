package com.whosin.app.ui.controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Objects;

import com.whosin.app.R;
import com.whosin.app.databinding.LayoutNavBarBinding;

public class NavBar extends ConstraintLayout {

    private LayoutNavBarBinding binding;

    public NavBar(Context context) {
        this(context,null);
    }

    public NavBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NavBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NavBar, 0, 0);
        String titleText = a.getString(R.styleable.NavBar_navTitle);
        String rightBtnTitle = a.getString(R.styleable.NavBar_rightBtnTitle);
        String leftBtnTitle = a.getString(R.styleable.NavBar_leftBtnTitle);
        int titleColorRes = a.getColor(R.styleable.NavBar_navTitleColor, Color.BLACK);
        int bgColorRes = a.getColor(R.styleable.NavBar_navBgColor, Color.WHITE);
        Drawable rightImgRes = a.getDrawable(R.styleable.NavBar_rightImage);
        Drawable leftImgRes = a.getDrawable(R.styleable.NavBar_leftImage);
        Boolean isShowBackBtn = a.getBoolean(R.styleable.NavBar_showBackButton, false);
        Boolean isShowRightBtn = a.getBoolean(R.styleable.NavBar_showRightButton, false);
        Boolean isShowCenterBtn = a.getBoolean(R.styleable.NavBar_showCenterButton, false);
        Boolean isShowSaperator = a.getBoolean(R.styleable.NavBar_showSaperator, false);
        a.recycle();

        View view = LayoutInflater.from(context).inflate(R.layout.layout_nav_bar, this);
        binding = LayoutNavBarBinding.bind(view);
        binding.getRoot().setBackgroundColor(bgColorRes);

        binding.txtTitle.setText(titleText);
        binding.txtTitle.setTextColor(titleColorRes);
        binding.txtRightBtn.setText(rightBtnTitle);
        binding.txtLeftBtn.setText(leftBtnTitle);

        if (rightImgRes != null) {
            binding.imageRight.setImageDrawable(rightImgRes);
        } else if (leftImgRes != null){
            binding.imageBack.setImageDrawable(leftImgRes);
        }

        binding.imageBack.setVisibility(Boolean.TRUE.equals(isShowBackBtn) ? VISIBLE : GONE);
        binding.imageRight.setVisibility(Boolean.TRUE.equals(isShowRightBtn) ? VISIBLE : GONE);
        binding.imgCenter.setVisibility(Boolean.TRUE.equals(isShowCenterBtn) ? VISIBLE : GONE);
        binding.txtRightBtn.setVisibility(TextUtils.isEmpty(rightBtnTitle) ? GONE : VISIBLE);
        binding.txtLeftBtn.setVisibility(TextUtils.isEmpty(leftBtnTitle) ? GONE : VISIBLE);
        binding.saperator.setVisibility(Boolean.TRUE.equals(isShowSaperator) ? VISIBLE : GONE);

    }

    public ImageView getBackBtn() {
        return binding.imageBack;
    }


    public ImageView getRightBtn() {
        return binding.imageRight;
    }

    public TextView getTextRightBtn() {
        return binding.txtRightBtn;
    }
    public TextView getTextLeftBtn() {
        return binding.txtLeftBtn;
    }

    public void setTxtTitle(String title) {
        binding.txtTitle.setText(title);
    }

    public void setNotificationCount(String title) {
        if (Objects.equals(title, "0")) {
            binding.txtNotifCount.setVisibility(GONE);
        } else {
            binding.txtNotifCount.setVisibility(VISIBLE);
        }
        binding.txtNotifCount.setText(title);
    }

    public void showNotificationIcon() {
        binding.notifContainer.setVisibility(VISIBLE);
    }

    public ConstraintLayout getNotificationView() {
        return binding.notifContainer;
    }

}
