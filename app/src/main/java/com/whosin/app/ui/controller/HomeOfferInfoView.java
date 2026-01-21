package com.whosin.app.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.HomeOfferInfoViewBinding;
import com.whosin.app.databinding.OfferInfoViewBinding;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.ui.activites.venue.VenueTimingDialog;

public class HomeOfferInfoView extends ConstraintLayout {

    private HomeOfferInfoViewBinding binding;
    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;

    private OffersModel model;


    public HomeOfferInfoView(Context context) {
        this(context, null);
    }

    public HomeOfferInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeOfferInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.home_offer_info_view, this, (view, resid, parent) -> {
            binding = HomeOfferInfoViewBinding.bind(view);
            if (model != null) {
                setupData();
            }
            HomeOfferInfoView.this.removeAllViews();
            HomeOfferInfoView.this.addView(view);
        });
    }

    private void setupData() {
        if (model == null){return;}
        Graphics.loadImage(model.getImage300(), binding.imgOffer);
        Drawable drawableCalender = ContextCompat.getDrawable(context, R.drawable.icon_venue_calender);
        drawableCalender.setBounds(0, 0, drawableCalender.getIntrinsicWidth(), drawableCalender.getIntrinsicHeight());
        binding.txtDays.setCompoundDrawables(drawableCalender, null, null, null);
        binding.txtDays.setText(model.getDays());

        binding.txtFromDate.setText(TextUtils.isEmpty(model.getStartTime()) ? "Ongoing" : Utils.convertMainDateFormatReview(model.getStartTime()));
        binding.tillDateLayout.setVisibility(TextUtils.isEmpty(model.getStartTime()) ? View.GONE : View.VISIBLE);
        if (!TextUtils.isEmpty(model.getStartTime())) {
            binding.txtTillDate.setText(Utils.convertMainDateFormat(model.getEndTime()));
        }

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.icon_time);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        Drawable drawableRight = ContextCompat.getDrawable(context, R.drawable.icon_time_info);
//        drawableRight.setBounds(0, 0, drawableRight.getIntrinsicWidth(), drawableRight.getIntrinsicHeight());
        binding.txtOfferTime.setCompoundDrawables(drawable, null, null, null);
        binding.txtOfferTime.setText(model.getOfferTiming());
        binding.txtOfferTime.setOnClickListener(v -> {
            if (model.getVenue() == null || supportFragmentManager == null || activity == null) { return; }
            VenueTimingDialog dialog = new VenueTimingDialog(model.getVenue().getTiming(), activity);
            dialog.show(supportFragmentManager, "1");
        });
    }

    public void setOfferDetail(OffersModel model , Activity activity, FragmentManager supportFragmentManager){
        if (model == null){return;}
        this.activity = activity;
        this.supportFragmentManager = supportFragmentManager;
        this.model = model;
        if (binding == null){return;}
        setupData();
    }

}
