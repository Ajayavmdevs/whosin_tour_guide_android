package com.whosin.business.ui.controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.whosin.business.R;
import com.whosin.business.databinding.LayoutBadgeViewBinding;

public class BadgeView extends ConstraintLayout {

    private LayoutBadgeViewBinding binding;

    public BadgeView(Context context) {
        this(context, null);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BadgeView, 0, 0);
        String titleText = a.getString(R.styleable.BadgeView_badgeTitle);
        float titleSize = a.getDimensionPixelSize(R.styleable.BadgeView_badgeTextSize, 30);
        int bgColorRes = a.getColor(R.styleable.BadgeView_badgeBgColor, Color.WHITE);

        Drawable iconRec = a.getDrawable(R.styleable.BadgeView_badgeIcon);
        int size = (int) a.getDimension(R.styleable.BadgeView_badgeIconSize, 50);
        a.recycle();

        View view = LayoutInflater.from(context).inflate(R.layout.layout_badge_view, this);
        binding = LayoutBadgeViewBinding.bind(view);

        binding.cardView.setCardBackgroundColor(bgColorRes);
        binding.iconTitleView.setTitleText(titleText);
        binding.iconTitleView.setIcon(iconRec);
        binding.iconTitleView.setIconSize(size);
        binding.iconTitleView.setTextSize(titleSize);
    }

    public void setTitleText(String title) {
        binding.iconTitleView.setTitleText(title);
    }
    public void setIcon(Drawable iconRes) {
        binding.iconTitleView.setIcon(iconRes);
    }
    public void setCardBackgroundColor(int bgColorRes) {
        binding.cardView.setCardBackgroundColor(bgColorRes);
    }
}

