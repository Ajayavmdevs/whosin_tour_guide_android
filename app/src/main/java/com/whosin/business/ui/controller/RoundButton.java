package com.whosin.business.ui.controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.whosin.business.R;
import com.whosin.business.databinding.LayoutRoundBtnBinding;

public class RoundButton extends ConstraintLayout {


    private LayoutRoundBtnBinding binding;
    private Context context;
    private Drawable leftImgRes;

    public RoundButton(Context context) {
        this(context, null);
    }

    public RoundButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundButton, 0, 0);
        Drawable leftImgRes = a.getDrawable(R.styleable.RoundButton_centerImg);
        int margin = (int) a.getDimension(R.styleable.RoundButton_imgMargin, 50);
        boolean isDisable = a.getBoolean(R.styleable.RoundButton_btnDisabled, false);
        a.recycle();

        View view = LayoutInflater.from(context).inflate(R.layout.layout_round_btn, this);
        binding = LayoutRoundBtnBinding.bind(view);
        this.leftImgRes = leftImgRes;
        binding.imageView.setImageDrawable(leftImgRes);
        if (isDisable) {
            binding.imageView.setColorFilter(ContextCompat.getColor(context, R.color.disable_gray));
        }

        ViewGroup.MarginLayoutParams s = (ViewGroup.MarginLayoutParams) binding.imageView.getLayoutParams();
        s.setMargins(margin, margin, margin, margin);
        binding.imageView.setLayoutParams(s);
    }

    public void setImage(@DrawableRes int imgRes) {
        binding.imageView.setImageResource(imgRes);
    }

    public void setEnable(boolean enabled) {
        if (!enabled) {
            binding.imageView.setColorFilter(ContextCompat.getColor(context, R.color.disable_gray));
        } else {
            binding.imageView.clearColorFilter();
            binding.imageView.setImageDrawable(leftImgRes);
        }
    }
}
