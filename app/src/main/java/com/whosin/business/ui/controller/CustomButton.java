package com.whosin.business.ui.controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.ColorUtils;

import com.whosin.business.R;
import com.whosin.business.comman.ui.roundcornerlayout.CornerType;
import com.whosin.business.databinding.LayoutCustomButtonBinding;

public class CustomButton extends ConstraintLayout {

    private LayoutCustomButtonBinding binding;
    private int bgColorRes;

    public CustomButton(Context context) {
        this(context,null);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomButton, 0, 0);
        String titleText = a.getString(R.styleable.CustomButton_btnTitle);
        int titleColorRes = a.getColor(R.styleable.CustomButton_btnTitleColor, Color.WHITE);
        bgColorRes = a.getColor(R.styleable.CustomButton_btnBgColor, 0);
        int cornerRadius = (int) a.getDimension(R.styleable.CustomButton_btnCorner, 50);
        a.recycle();

        View view = LayoutInflater.from(context).inflate(R.layout.layout_custom_button, this);
        binding = LayoutCustomButtonBinding.bind(view);

        binding.mainContainer.setCornerRadius(cornerRadius, CornerType.ALL);

        if (bgColorRes != 0) {
            binding.mainContainer.setBackgroundColor(bgColorRes);
        }

        binding.titleText.setText(titleText);
        binding.titleText.setTextColor(titleColorRes);

        binding.progressView.setVisibility(GONE);
    }

    public void setEnable(boolean enable) {
        if (enable) {
            binding.mainContainer.setBackgroundColor(bgColorRes);
        } else {
            int alphColor = ColorUtils.setAlphaComponent(bgColorRes, 40);
            binding.mainContainer.setBackgroundColor(alphColor);
        }
    }

    public void setTxtTitle(String title) {
        binding.titleText.setText(title);
    }

    public void startProgress() {
        setEnabled(false);
        binding.progressView.setVisibility(VISIBLE);
        binding.titleText.setVisibility(INVISIBLE);
    }

    public void stopProgress() {
        setEnabled(true);
        binding.progressView.setVisibility(INVISIBLE);
        binding.titleText.setVisibility(VISIBLE);
    }
}
