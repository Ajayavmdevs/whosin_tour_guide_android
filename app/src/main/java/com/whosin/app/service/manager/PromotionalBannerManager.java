package com.whosin.app.service.manager;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PromotionalBannerModels.PromotionalMainModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
