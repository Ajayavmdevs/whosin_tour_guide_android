package com.whosin.business.service.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.whosin.business.service.models.PromotionalBannerModels.PromotionalMainModel;

public class PromotionalBannerManager {

    @NonNull
    public static PromotionalBannerManager shared = PromotionalBannerManager.getInstance();

    private static PromotionalBannerManager instance = null;

    private Context context;

    private PromotionalMainModel promotionalMainModel;

    private boolean isPromotionalBanner = false;


    // --------------------------------------
    // region Singleton
    // --------------------------------------

    @NonNull
    private static synchronized PromotionalBannerManager getInstance() {
        if (instance == null) {
            instance = new PromotionalBannerManager();
        }
        return instance;
    }

    // --------------------------------------
    // region Public
    // --------------------------------------

    public PromotionalMainModel getPromotionalMainModel() {
        return promotionalMainModel;
    }

    public boolean getIsPromotionalBanner() {
        return isPromotionalBanner;
    }


    // region Data/Services
    // --------------------------------------

    // endregion
    // --------------------------------------


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
