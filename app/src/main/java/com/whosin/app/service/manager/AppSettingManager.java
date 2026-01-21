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
import com.whosin.app.service.models.HomeObjectModel;
import com.whosin.app.service.models.InAppNotificationModel;
import com.whosin.app.service.models.SearchHistoryModel;
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
    public static SubscriptionModel homePostersubscriptionModel;
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

    /*public void requestSubscriptionDetail(Context context) {
        this.context = context;
        DataService.shared( context ).requestSubscriptionDetail( new RestCallback<ContainerModel<MemberShipModel>>() {
            @Override
            public void result(ContainerModel<MemberShipModel> model, String error) {

                if (!Utils.isNullOrEmpty(error)) { return;}
                if (model == null) {
                    return;
                }

                subscriptionModel  = model.getData();
                if (subscriptionModel != null) {
                    saveSubscriptionData(model.getData());
                }

            }
        } );
    }*/

    public void requestSubscriptionCustomPlan(Context context) {

        this.context = context;
        DataService.shared( context ).requestSubscriptionPlan(new RestCallback<ContainerModel<SubscriptionModel>>(null) {
            @Override
            public void result(ContainerModel<SubscriptionModel> model, String error) {

                if (!Utils.isNullOrEmpty(error)) {
                    return;
                }
                if (model == null) {
                    return;
                }
                homePostersubscriptionModel = model.getData();
            }
        });

    }

    public void requestVenueAllFilters(Context context) {
        this.context = context;
        DataService.shared( context ).requestVenueAllFilters(new RestCallback<ContainerModel<VenueFiltersModel>>(null) {
            @Override
            public void result(ContainerModel<VenueFiltersModel> model, String error) {

                if (!Utils.isNullOrEmpty(error)) {
                    return;
                }
                if (model == null) {
                    return;
                }

                venueFilterModel = model.getData();
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
