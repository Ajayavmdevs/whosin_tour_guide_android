package com.whosin.app.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import com.whosin.app.R;
import com.whosin.app.service.models.YachtDetailModel;
import java.util.List;

public class HomeBlockYachtView extends ConstraintLayout {
    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;
    private List<YachtDetailModel> yachtDetailModelList;

    private boolean isYachtOffer;

    public HomeBlockYachtView(Context context) {
        this(context, null);
    }

    public HomeBlockYachtView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeBlockYachtView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        this.context = context;

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
    }

    public void setupData(List<YachtDetailModel> yacht, Activity activity, FragmentManager fragmentManager, boolean isYacht) {
        this.yachtDetailModelList = yacht;
        this.activity = activity;
        this.supportFragmentManager = fragmentManager;
        this.isYachtOffer = isYacht;
    }
}
