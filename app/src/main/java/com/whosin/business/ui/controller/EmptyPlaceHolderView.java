package com.whosin.business.ui.controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.whosin.business.R;
import com.whosin.business.databinding.LayoutEmptyPlaceHolderViewBinding;


public class EmptyPlaceHolderView extends LinearLayout {
    private LayoutEmptyPlaceHolderViewBinding binding;

    public EmptyPlaceHolderView(Context context) {
        super(context);
        init(null, 0, context);
    }

    public EmptyPlaceHolderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, context);
    }

    public EmptyPlaceHolderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle,context);
    }

    private void init(AttributeSet attrs, int defStyle, Context context) {
         final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.EmptyPlaceHolderView, defStyle, 0);

         String titleText = a.getString(R.styleable.EmptyPlaceHolderView_emptyPlaceTxtTitle);
        float titleSize = a.getDimensionPixelSize(R.styleable.EmptyPlaceHolderView_emptyPlaceTxtTitleSize, 30);

        String subTitleText = a.getString(R.styleable.EmptyPlaceHolderView_emptyPlaceTxtSubTitle);

        int titleColorRes = a.getColor(R.styleable.EmptyPlaceHolderView_emptyPlaceTxtTitleColor,context.getColor(R.color.white_85));

        int marginTop = a.getDimensionPixelSize(R.styleable.EmptyPlaceHolderView_emptyPlaceTxtMarginTop, 0);

        Drawable iconImgRes = a.getDrawable(R.styleable.EmptyPlaceHolderView_emptyPlaceTxtIcon);
        a.recycle();

        View view = LayoutInflater.from(context).inflate(R.layout.layout_empty_place_holder_view, this);
        binding = LayoutEmptyPlaceHolderViewBinding.bind(view);

        if (iconImgRes != null) {
            binding.image.setImageDrawable(iconImgRes);
        }

        binding.titleText.setTextAppearance(context, R.style.txt14Regular);
        binding.titleText.setText(titleText);
        binding.titleText.setTextColor(titleColorRes);
        binding.titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize);

        LayoutParams params = (LayoutParams)  binding.titleText.getLayoutParams();
        params.topMargin = marginTop;
        binding.titleText.setLayoutParams(params);

        if (!TextUtils.isEmpty(subTitleText)) {
            binding.subTitleText.setText(subTitleText);
            binding.subTitleText.setVisibility(VISIBLE);
        } else {
            binding.subTitleText.setVisibility(GONE);
        }
    }

    public void setEmptyPlaceTxtTitle(String title) {
        binding.titleText.setText(title);
    }

    public void setActionButton(String text, OnClickListener listener) {
        binding.actionButton.setVisibility(VISIBLE);
        binding.actionButton.setText(text);
        binding.actionButton.setOnClickListener(listener);
    }

    public void hideActionButton() {
        binding.actionButton.setVisibility(GONE);
    }
}