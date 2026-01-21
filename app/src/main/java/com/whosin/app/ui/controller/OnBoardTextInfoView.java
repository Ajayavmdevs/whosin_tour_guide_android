package com.whosin.app.ui.controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.whosin.app.R;
import com.whosin.app.databinding.LayoutOnboardTextInfoBinding;

public class OnBoardTextInfoView extends ConstraintLayout {

    private LayoutOnboardTextInfoBinding binding;

    public OnBoardTextInfoView(Context context) {
        this(context, null);
    }

    public OnBoardTextInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OnBoardTextInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OnBoardTextInfoView, 0, 0);
        String titleText = a.getString(R.styleable.OnBoardTextInfoView_mainTitle);
        String subTitleText = a.getString(R.styleable.OnBoardTextInfoView_subTitle);
        int titleColorRes = a.getColor(R.styleable.OnBoardTextInfoView_mainTitleColor, context.getColor(R.color.white_85));
        a.recycle();

        View view = LayoutInflater.from(context).inflate(R.layout.layout_onboard_text_info, this);
        binding = LayoutOnboardTextInfoBinding.bind(view);

        binding.titleText.setText(titleText);
        binding.titleText.setTextColor(titleColorRes);
        binding.subTitleText.setText(subTitleText);
    }

    public void setTitleColor(@ColorInt int titleColor) {
        binding.titleText.setTextColor(titleColor);
    }

    public void setMainTitle(String title) {
        binding.titleText.setText(title);
    }

    public void setSubTitleText(String title){
        binding.subTitleText.setText(title);
    }

    public void setAlertTitleText(String title){
        binding.alertTitleText.setText(title);
        binding.alertTitleText.setVisibility(View.VISIBLE);
    }

}

