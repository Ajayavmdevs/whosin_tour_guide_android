package com.whosin.business.ui.controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.ColorUtils;

import com.whosin.business.R;
import com.whosin.business.comman.ui.roundcornerlayout.CornerType;
import com.whosin.business.databinding.LayoutCustomButtonBinding;
import com.whosin.business.databinding.LayoutCustomButtonTextBinding;

public class CustomButtonText extends ConstraintLayout {
    private LayoutCustomButtonTextBinding binding;
    private int bgColorRes;

    public CustomButtonText(Context context) {
        this(context,null);
    }

    public CustomButtonText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomButtonText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomButtonText, 0, 0);
        String titleText = a.getString(R.styleable.CustomButtonText_btnTxtTitle);
        float titleSize = a.getDimensionPixelSize(R.styleable.CustomButtonText_btnTxtTitleSize, 30);

        int titleColorRes = a.getColor(R.styleable.CustomButtonText_btnTxtIconColor, Color.WHITE);
        bgColorRes = a.getColor(R.styleable.CustomButtonText_btnTxtBgColor, 0);

        int iconColorRes = a.getColor(R.styleable.CustomButtonText_btnTxtIconColor, Color.WHITE);
        Drawable iconImgRes = a.getDrawable(R.styleable.CustomButtonText_btnTxtIcon);
        int cornerRadius = (int) a.getDimension(R.styleable.CustomButtonText_btnTxtCorner, 70);
        a.recycle();

        View view = LayoutInflater.from(context).inflate(R.layout.layout_custom_button_text, this);
        binding = LayoutCustomButtonTextBinding.bind(view);

        binding.mainContainer.setCornerRadius(cornerRadius, CornerType.ALL);

        if (bgColorRes != 0) {
            binding.mainContainer.setBackgroundColor(bgColorRes);
        }
        if (iconImgRes != null) {
            binding.image.setImageDrawable(iconImgRes);
        }
        binding.image.setColorFilter(iconColorRes);

        binding.titleText.setText(titleText);
        binding.titleText.setTextColor(titleColorRes);
        binding.titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize);

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
