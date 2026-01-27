package com.whosin.app.service.manager;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.AppSettingModel;
import com.whosin.app.service.models.CartModel;
import com.whosin.app.service.models.CategoriesModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.SubscriptionModel;
import com.whosin.app.service.models.VenueFiltersModel;
import com.whosin.app.service.rest.RestCallback;

import java.util.ArrayList;
import java.util.List;

public class AppSettingManager {
    @NonNull
    public static AppSettingManager shared = AppSettingManager.getInstance();

     private static AppSettingModel appSettingModel;
    private static SubscriptionModel subscriptionModel;
     private static AppSettingManager instance = null;
    private Context context;
    public CommanCallback<Boolean> reloadHomeFragment;

    public List<CartModel> tmpCartList = new ArrayList<>();

    public List<String> videoPlayPauseList = new ArrayList<>();

    public String authRequestId = "";

    public VenueFiltersModel venueFilterModel;

    public CommanCallback<Boolean> venueReloadCallBack;

    public List<CategoriesModel> filterList = new ArrayList<>();

    public boolean isAlreadyOpenReviewSheet = false;

    public CommanCallback<Boolean> deleteNotificationCallBack;

    public CommanCallback<Boolean> walletHistoryCallBack;

    public boolean callHomeCommanApi = true;

    // --------------------------------------
    // region Singleton
    // --------------------------------------

    @NonNull
    private static synchronized AppSettingManager getInstance() {
        if (instance == null) {
            instance = new AppSettingManager();
        }
        return instance;
    }
    // region Data/Services
    // --------------------------------------

    public void requestAppSetting(Context context) {
        TranslationManager.shared.reequestCommanLang();
        this.context = context;
        DataService.shared(context).requestAppSetting(new RestCallback<ContainerModel<AppSettingModel>>(null) {
            @Override
            public void result(ContainerModel<AppSettingModel> model, String error) {
                if (!Utils.isNullOrEmpty(error)) {
                    return;
                }
                if (model == null) {
                    return;
                }
                AppSettingModel data = model.getData();
                if (data != null) {
                    saveAppSettingData(data);
                }
            }
        });


    }



    // endregion
    // --------------------------------------
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private void saveAppSettingData(@NonNull AppSettingModel model) {
        appSettingModel = model;
        String json = new Gson().toJson(model);
        Preferences.shared.setString("AppSettingDetail", json);
    }

    public AppSettingModel getAppSettingData() {
        if (appSettingModel == null) {
            String json = Preferences.shared.getString("AppSettingDetail");
            if (!TextUtils.isEmpty(json)) {
                appSettingModel = new Gson().fromJson(json, AppSettingModel.class);
            }
        }
        if (appSettingModel == null) {
            return new AppSettingModel();
        }
        return appSettingModel;
    }


    public SubscriptionModel getSubscriptionData() {
        if (subscriptionModel == null) {
            String json = Preferences.shared.getString( "SubscriptionDetail" );
            if (!TextUtils.isEmpty( json )) {
                subscriptionModel = new Gson().fromJson( json, SubscriptionModel.class );
            }
        }
        return subscriptionModel;
    }


    // endregion
    // --------------------------------------
}
