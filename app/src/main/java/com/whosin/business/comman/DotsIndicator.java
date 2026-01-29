package com.whosin.business.comman;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.whosin.business.R;

public class DotsIndicator extends LinearLayout {

    private int selection = 0;
    private int dotsCount = 2;
    private int dotHeight = dpToPx(7);
    private int dotWidth = dpToPx(7);
    private int firstDotHeight = dpToPx(14);
    private int firstDotWidth = dpToPx(14);
    private int marginsBetweenDots = dpToPx(17);
    private float selectedDotScaleFactor = 1.4f;
    private int selectedDotResource = R.drawable.icon_selected_dot;
    private int unselectedDotResource = R.drawable.icon_unselected_dot;

    private OnSelectListener onSelectListener;

    public DotsIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public DotsIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER);

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DotsIndicator);
            dotsCount = ta.getInt(R.styleable.DotsIndicator_dots_count, 3);
            selectedDotScaleFactor = ta.getFloat(R.styleable.DotsIndicator_selected_dot_scale_factor, 1.4f);
            selectedDotResource = ta.getResourceId(R.styleable.DotsIndicator_selected_dot_resource, selectedDotResource);
            unselectedDotResource = ta.getResourceId(R.styleable.DotsIndicator_unselected_dot_resource, unselectedDotResource);

            dotHeight = ta.getDimensionPixelSize(R.styleable.DotsIndicator_dot_height, dotHeight);
            dotWidth = ta.getDimensionPixelSize(R.styleable.DotsIndicator_dot_width, dotWidth);
            firstDotHeight = ta.getDimensionPixelSize(R.styleable.DotsIndicator_first_dot_height, firstDotHeight);
            firstDotWidth = ta.getDimensionPixelSize(R.styleable.DotsIndicator_first_dot_width, firstDotWidth);
            marginsBetweenDots = ta.getDimensionPixelSize(R.styleable.DotsIndicator_margins_between_dots, marginsBetweenDots);
            ta.recycle();
        }

        initDots(dotsCount);
    }

    public void initDots(int dotsCount) {
        removeAllViews();
        for (int i = 0; i < dotsCount; i++) {
            ImageView dot = new ImageView(getContext());
            dot.setId(i);
            dot.setTag(i);
            LayoutParams layoutParams = new LayoutParams(
                    selection == i ? (int) (dotWidth * selectedDotScaleFactor) : dotWidth,
                    dotHeight
            );
            layoutParams.setMarginEnd(marginsBetweenDots);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            dot.setLayoutParams(layoutParams);
//            dot.setScaleType(ImageView.ScaleType.FIT_XY);

            dot.setImageResource((selection == i) ? selectedDotResource : unselectedDotResource);


            final int position = i;
            dot.setOnClickListener(v -> {
                if (onSelectListener != null) {
                    onSelectListener.onSelect(position);
                }
                setDotSelection(position);
            });

            addView(dot);
        }
        setDotSelection(selection);
    }

    public void setDotSelection(int position) {
        if (position == selection)
            return;

        ImageView newSelection = findViewById(position);
        ImageView selectedDot = findViewWithTag(selection);

        if (newSelection == null || selectedDot == null) {
             return;
        }

        ValueAnimator increaseAnimator = ValueAnimator.ofFloat(1f, selectedDotScaleFactor);
        increaseAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
//            newSelection.getLayoutParams().width = (int) (dotWidth * value);

            ViewGroup.LayoutParams layoutParams = newSelection.getLayoutParams();
            layoutParams.width = (int) (dotWidth * value);
            newSelection.setLayoutParams(layoutParams);
        });

        ValueAnimator decreaseAnimator = ValueAnimator.ofFloat(selectedDotScaleFactor, 1f);
        decreaseAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
//            selectedDot.getLayoutParams().width = (int) (dotWidth * value);

            ViewGroup.LayoutParams layoutParams = selectedDot.getLayoutParams();
            layoutParams.width = (int) (dotWidth * value);
            selectedDot.setLayoutParams(layoutParams);
        });

        increaseAnimator.start();
        decreaseAnimator.start();

        Animator.AnimatorListener animationListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationRepeat(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                newSelection.getLayoutParams().width = (int) (dotWidth * selectedDotScaleFactor);
                ViewGroup.LayoutParams layoutParams = newSelection.getLayoutParams();
                layoutParams.width = (int) (dotWidth * selectedDotScaleFactor);
                newSelection.setLayoutParams(layoutParams);

//                selectedDot.getLayoutParams().width = (int) (dotWidth);
                ViewGroup.LayoutParams layoutParams1 = selectedDot.getLayoutParams();
                layoutParams1.width = (int) (dotWidth);
                selectedDot.setLayoutParams(layoutParams1);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationStart(Animator animation) {}
        };

        increaseAnimator.addListener(animationListener);
        decreaseAnimator.addListener(animationListener);

        newSelection.setImageResource(selectedDotResource);
        selectedDot.setImageResource(unselectedDotResource);
        selection = (int) newSelection.getTag();
    }

    public interface OnSelectListener {
        void onSelect(int position);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
