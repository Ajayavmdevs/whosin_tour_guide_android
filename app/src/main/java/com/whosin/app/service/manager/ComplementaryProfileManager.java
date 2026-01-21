package com.whosin.app.service.manager;

import static com.whosin.app.comman.AppDelegate.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.ItemComplementryProfileBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComplementaryProfileManager {

    @NonNull
    public static ComplementaryProfileManager shared = ComplementaryProfileManager.getInstance();

    private static ComplementaryProfileManager instance = null;

    private Context context;

    public CommanCallback<Boolean> setProfileCallBack;

    public ComplimentaryProfileModel complimentaryProfileModel = null;

    public List<PromoterEventModel> eventList = new ArrayList<>();

    public CommanCallback<UserDetailModel> callbackForHeader;

    public List<PromoterEventModel> inEventDateAndTimeList = new ArrayList<>();

    // --------------------------------------
    // region Singleton
    // --------------------------------------

    @androidx.annotation.NonNull
    private static synchronized ComplementaryProfileManager getInstance() {
        if (instance == null) {
            instance = new ComplementaryProfileManager();
        }
        return instance;
    }


    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public static boolean checkEventInDateTime(String date, String time) {
        if (!shared.inEventDateAndTimeList.isEmpty()) {
            return shared.inEventDateAndTimeList.stream().anyMatch(p -> p.getDate().equals(date) && p.getStartTime().equals(time));
        }
        return false;
    }


    // region Data/Services
    // --------------------------------------

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void requestPromoterUpdateInviteStatus(Context context) {
        DataService.shared(context).requestPromoterUserInEvent(new RestCallback<ContainerListModel<PromoterEventModel>>(null) {
            @Override
            public void result(ContainerListModel<PromoterEventModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()){
                    inEventDateAndTimeList.clear();
                    inEventDateAndTimeList.addAll(model.data);
                }
            }
        });
    }


    // endregion
    // --------------------------------------
}
