package com.whosin.app.ui.controller.yacht;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.whosin.app.R;
import com.whosin.app.databinding.YachtMessageViewBinding;


public class YachtImportantMessageView extends ConstraintLayout {


    private YachtMessageViewBinding binding;
    private int bgColorRes ,titleColorRes;
    private Context context;
    private Activity activity;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public YachtImportantMessageView(Context context) {
        this(context, null);
    }

    public YachtImportantMessageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public YachtImportantMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MessageView, 0, 0);
        bgColorRes = a.getColor(R.styleable.MessageView_subTitleBgColor, 0);
        titleColorRes = a.getColor(R.styleable.MessageView_titleTxtBgColor, Color.WHITE);
        int titleTextStyle = a.getResourceId(R.styleable.MessageView_messageTitleTextStyle, 0);

        boolean isItalic = a.getBoolean(R.styleable.MessageView_messageTitleTextStyleItalic, false);
        boolean isSemiBold = a.getBoolean(R.styleable.MessageView_messageTitleTextStyleSemiBold, false);
        boolean isBold = a.getBoolean(R.styleable.MessageView_messageTitleTextStyleBold, false);

        a.recycle();
        View view = LayoutInflater.from(context).inflate(R.layout.yacht_message_view, this, true);
        binding = YachtMessageViewBinding.bind(view);

        if (bgColorRes != 0) {
            binding.messageSubTitleBackground.setBackgroundColor(bgColorRes);
        }

        if (isItalic) {
            binding.tvMessageTitle.setTypeface(null, Typeface.ITALIC);
        }
        if (isSemiBold) {
            binding.tvMessageTitle.setTypeface(null, Typeface.BOLD);
        }
        if (isBold) {
            binding.tvMessageTitle.setTypeface(null, Typeface.BOLD);
        }
        if (titleTextStyle != 0) {
            binding.tvMessageTitle.setTextAppearance(context, titleTextStyle);
        }
        binding.tvMessageTitle.setTextColor(titleColorRes);
    }

    public void setupData(Activity activity, String title, String subTitle) {
        this.activity = activity;
        if (subTitle == null || subTitle.isEmpty() || binding == null) {
            return;
        }

        activity.runOnUiThread(() -> {
            binding.tvMessageTitle.setText(title);
            binding.tvMessageSubTitle.setText(subTitle);

            binding.messageTitleView.setOnClickListener(v -> {
                if (binding.messageSubTitleBackground.getVisibility() == View.VISIBLE) {
                    binding.messageSubTitleBackground.setVisibility(View.GONE);
                    binding.expandedArrow.setRotation(0);
                } else {
                    binding.messageSubTitleBackground.setVisibility(View.VISIBLE);
                    binding.expandedArrow.setRotation(180);
                }
            });

        });
    }

    public void setTxtTitle(String title) {
        binding.tvMessageTitle.setText(title);
    }

    public void subTitleTxtBgColor(@ColorInt int titleColor) {
        binding.tvMessageTitle.setTextColor(titleColor);
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // --------------------------------------
    // endregion
}

