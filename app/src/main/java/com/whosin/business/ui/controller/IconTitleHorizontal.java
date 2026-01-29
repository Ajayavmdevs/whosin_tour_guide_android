package com.whosin.business.ui.controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.whosin.business.R;
import com.whosin.business.databinding.LayoutIconTitleHorizontalBinding;

public class IconTitleHorizontal extends ConstraintLayout {

    private LayoutIconTitleHorizontalBinding binding;

    public IconTitleHorizontal(Context context) {
        this(context,null);
    }

    public IconTitleHorizontal(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IconTitleHorizontal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconTitleHorizontal, 0, 0);
        String titleText = a.getString(R.styleable.IconTitleHorizontal_ith_title);
        int titleColorRes = a.getColor(R.styleable.IconTitleHorizontal_ith_titleColor, Color.BLACK);
        float titleSize = a.getDimensionPixelSize(R.styleable.IconTitleHorizontal_ith_titleSize, 30);

        int iconColorRes = a.getColor(R.styleable.IconTitleHorizontal_ith_iconColor, Color.WHITE);
        Drawable iconImgRes = a.getDrawable(R.styleable.IconTitleHorizontal_ith_icon);
        int size = (int) a.getDimension(R.styleable.IconTitleHorizontal_ith_iconSize, 60);
        a.recycle();

        View view = LayoutInflater.from(context).inflate(R.layout.layout_icon_title_horizontal, this);
        binding = LayoutIconTitleHorizontalBinding.bind(view);

        binding.titleText.setText(titleText);
        binding.titleText.setTextColor(titleColorRes);
        binding.titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize);

        if (iconImgRes != null) {
            binding.iconImageView.setImageDrawable(iconImgRes);
        }
        binding.iconImageView.setColorFilter(iconColorRes);

        setIconSize(size);
    }

    public void setTitleText(String title) {
        binding.titleText.setText(title);
    }

    public void setIcon(Drawable iconRes) {
        binding.iconImageView.setImageDrawable(iconRes);
    }

    public void setIconSize(int size) {
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(size,size);
        binding.iconImageView.setLayoutParams(parms);
    }

    public void setTextSize(float titleSize) {
        binding.titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize);
    }
}
