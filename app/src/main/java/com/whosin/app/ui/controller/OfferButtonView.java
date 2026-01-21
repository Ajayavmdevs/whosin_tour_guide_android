package com.whosin.app.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.LayoutOfferButtonBinding;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.ui.activites.venue.VenueBuyNowActivity;

public class OfferButtonView extends LinearLayout {

    private LayoutOfferButtonBinding binding;

    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;

    private OffersModel offersModel;

    public OfferButtonView(Context context) {
        this(context, null);
    }

    public OfferButtonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OfferButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(Graphics.context);
        asyncLayoutInflater.inflate(R.layout.layout_offer_button, this, (view, resid, parent) -> {
            binding = LayoutOfferButtonBinding.bind(view);
            if (offersModel != null) {
                setupData();
            }
            OfferButtonView.this.removeAllViews();
            OfferButtonView.this.addView(view);
        });
    }

    private void setupData() {
        if (binding == null) { return; }
        if (offersModel == null) return;
        Utils.setupOfferButtons(offersModel, binding.buttonOne, binding.buttonTwo, binding.buttonThree);

        View.OnClickListener buttonClick = v -> {
            Utils.preventDoubleClick(v);
            if (activity == null) { return; }
            if (offersModel.getVenue() == null) { return; }
            TextView button = (TextView) v;
            String buttonText = button.getText().toString();
            if(buttonText.equalsIgnoreCase(Utils.getLangValue("buy_now"))) {
                activity.startActivity(new Intent(activity, VenueBuyNowActivity.class).putExtra("venueObjectModel", new Gson().toJson(offersModel.getVenue())).putExtra("offerModel", new Gson().toJson(offersModel)));
            } else if(buttonText.equalsIgnoreCase(Utils.getLangValue("claim_discount"))) {
                Utils.openClaimScreen(offersModel.getSpecialOfferModel(), offersModel.getVenue(), activity);
            }
//            else {
//                Utils.openInviteButtonSheet(offersModel, offersModel.getVenue(), supportFragmentManager);
//            }
        };

        binding.buttonOne.setOnClickListener(buttonClick);
        binding.buttonTwo.setOnClickListener(buttonClick);
        binding.buttonThree.setOnClickListener(buttonClick);
    }

    public void setupButtons(OffersModel offersModel, Activity activity, FragmentManager supportFragmentManager) {
        if (offersModel == null) return;
        this.offersModel = offersModel;
        this.activity = activity;
        this.supportFragmentManager = supportFragmentManager;
        if (binding == null) { return; }
        setupData();
    }

}
