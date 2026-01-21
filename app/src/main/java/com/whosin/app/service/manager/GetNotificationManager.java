package com.whosin.app.service.manager;

import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.whosin.app.comman.Utils;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.CommanMsgModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.MainNotificationModel;
import com.whosin.app.service.models.UpdateStatusModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.controller.NavBarWithBack;

import org.greenrobot.eventbus.EventBus;

public class GetNotificationManager {

    @NonNull
    public static GetNotificationManager shared = GetNotificationManager.getInstance();

    private static GetNotificationManager instance = null;
    private Context context;

    private NavBarWithBack navbar;

    public UpdateStatusModel statusModel;

    // --------------------------------------
    // region Singleton
    // --------------------------------------

    @NonNull
    private static synchronized GetNotificationManager getInstance() {
        if (instance == null) {
            instance = new GetNotificationManager();
        }
        return instance;
    }

    public void requestCount(NavBarWithBack navbar, Context context) {
        this.context = context;
        this.navbar = navbar;
        requestUnreadCount(navbar);
    }

    // region Data/Services
    // --------------------------------------

    public void requestUnreadCount(NavBarWithBack navbar) {
        DataService.shared( context ).requestUserNotificationUnreadCount(new RestCallback<ContainerModel<MainNotificationModel>>(null) {
            @Override
            public void result(ContainerModel<MainNotificationModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( context, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.getData() != null) {
                    if (model.getData().getCount() > 99) {
                        navbar.setCount("99+");
                    } else {
                        navbar.setCount(String.valueOf(model.getData().getCount()));
                    }
                    navbar.setCountVisible(model.data.getCount() == 0 ? false : true);
                }
            }
        } );
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    public void requestActivityUpdatesCount() {
        DataService.shared(context).requestUpdatesStatus(new RestCallback<ContainerModel<UpdateStatusModel>>(null) {
            @Override
            public void result(ContainerModel<UpdateStatusModel> model, String error) {
                if (model == null) { return; }
                if (model.getData() != null) {
                    statusModel = model.data;
                    EventBus.getDefault().post(model.data);
                }
            }
        });
    }

    public void requestUpdatesCount(String type) {

        DataService.shared(context).requestUpdatesRead(type, new RestCallback<ContainerModel<CommanMsgModel>>(null) {
            @Override
            public void result(ContainerModel<CommanMsgModel> model, String error) {
                if (model == null) { return; }
                if (statusModel == null) { return; }
                if (model.status == 1) {
                    switch (type) {
                        case "bucket":
                            statusModel.setBucket(false);
                            break;
                        case "outing":
                            statusModel.setOuting(false);
                            break;
                        case "wallet":
                            statusModel.setWallet(false);
                            break;
                        case "event":
                            statusModel.setEvent(false);
                            break;
                    }
                    EventBus.getDefault().post(statusModel);
                }
            }
        });
    }

    // endregion
    // --------------------------------------

}
