package com.whosin.app.ui.controller.ComplementryProfileViews;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.whosin.app.R;
import com.whosin.app.databinding.LayoutCmProfileScoreBinding;
import com.whosin.app.service.models.CmpScoreModel;

public class ComplementryProfileScoreView extends ConstraintLayout {
    private LayoutCmProfileScoreBinding binding;

    private Double profilescore = 0.0;

    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public ComplementryProfileScoreView(Context context) {
        this(context, null);
    }

    public ComplementryProfileScoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComplementryProfileScoreView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.ComplementryProfileScoreView, 0, 0 );
        String titleText = a.getString( R.styleable.ComplementryProfileScoreView_scoreTitleText );

        a.recycle();
        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.layout_cm_profile_score, this, (view, resid, parent) -> {
            binding = LayoutCmProfileScoreBinding.bind( view );

            if (titleText != null) {
                binding.scoreDescription.setText( titleText );
            }

            binding.tvScoreCount.setText( String.valueOf( profilescore ) );


            ComplementryProfileScoreView.this.removeAllViews();
            ComplementryProfileScoreView.this.addView( view );
        });
    }




    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    public void setUpData(Double score, Activity activity, FragmentManager supportFragmentManager) {
        this.activity = activity;
        this.supportFragmentManager = supportFragmentManager;
        this.profilescore = score;

        if (binding == null) {
            return;
        }

        binding.tvScoreCount.setText( String.valueOf( score ) );
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
}
