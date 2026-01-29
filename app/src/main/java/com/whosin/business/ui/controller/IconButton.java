package com.whosin.business.ui.controller;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.whosin.business.R;
import com.whosin.business.comman.ui.roundcornerlayout.CornerType;
import com.whosin.business.comman.ui.roundcornerlayout.RoundCornerLinearLayout;


public class IconButton extends ConstraintLayout {

    private ImageView iconImage;
    private TextView txtTitle;
    private RoundCornerLinearLayout mContainer;
    private ProgressBar progressBar;

    private int progressColor = Color.BLACK;
    public IconButton(Context context) {
        this(context,null);
    }

    public IconButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IconButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconButton, 0, 0);
        String titleText = a.getString(R.styleable.IconButton_buttonTitle);

        Drawable bgRec = a.getDrawable(R.styleable.IconButton_buttonBg);
        if (bgRec == null) {
            bgRec = AppCompatResources.getDrawable(context,R.drawable.border_black);
        }
        int colorRes = a.getColor(R.styleable.IconButton_buttonTintColor, Color.BLACK);
        int bgColorRes = a.getColor(R.styleable.IconButton_buttonBgColor, 0);

        int cornerRadius = (int) a.getDimension(R.styleable.IconButton_buttonCorner, 50);

        Drawable iconRec = a.getDrawable(R.styleable.IconButton_buttonIcon);

        a.recycle();

        LayoutInflater.from(context).inflate(R.layout.layout_icon_button, this);

        mContainer = findViewById(R.id.mainContainer);
        mContainer.setCornerRadius(cornerRadius, CornerType.ALL);
        if (bgRec != null) {
            mContainer.setBackground(bgRec);
        }

        if (bgColorRes != 0) {
            mContainer.setBackgroundColor(bgColorRes);
        }

        iconImage = findViewById(R.id.imgIcon);
        iconImage.setImageDrawable(iconRec);
        iconImage.setColorFilter(colorRes);

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

    public void setTintColor(@ColorInt int colorRes){
        txtTitle.setTextColor(colorRes);
        iconImage.setColorFilter(colorRes);
    }

    public void setIconImage(int resId){
        iconImage.setImageResource(resId);
    }

    public void startProgress() {
        setEnabled(false);
        progressBar.setVisibility(VISIBLE);
        iconImage.setVisibility(INVISIBLE);
        txtTitle.setVisibility(INVISIBLE);
    }

    public void stopProgress() {
        setEnabled(true);
        progressBar.setVisibility(INVISIBLE);
        iconImage.setVisibility(VISIBLE);
        txtTitle.setVisibility(VISIBLE);
    }
    public void setProgressColor(@ColorInt int color) {
        this.progressColor = color;
        progressBar.getIndeterminateDrawable().setColorFilter(progressColor, PorterDuff.Mode.SRC_IN);
    }

    public void setTitleTextSize(float textSize){
        txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    public void setPadding(int padding){
        mContainer.setPadding(padding, mContainer.getPaddingTop(), padding, mContainer.getPaddingBottom());
    }
}