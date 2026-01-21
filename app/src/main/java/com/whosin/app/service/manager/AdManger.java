package com.whosin.app.service.manager;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.ActionDoneCallback;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.AdListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class AdManger {

    @NonNull
    public static AdManger shared = AdManger.getInstance();

    private static AdManger instance = null;

    private Context context;


    // --------------------------------------
    // region Singleton
    // --------------------------------------

    @NonNull
    private static synchronized AdManger getInstance() {
        if (instance == null) {
            instance = new AdManger();
        }
        return instance;
    }

    // --------------------------------------
    // region Public
    // --------------------------------------


    // --------------------------------------
    // region Data/Services
    // --------------------------------------

    public void requestAdList(Context context, ActionDoneCallback<List<AdListModel>> callback) {
        this.context = context;
        DataService.shared(context).requestAdList(new RestCallback<ContainerListModel<AdListModel>>(null) {
            @Override
            public void result(ContainerListModel<AdListModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    callback.onDone(null,0);
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    model.data.sort((a, b) -> {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date d1 = sdf.parse(a.getCreatedAt());
                            Date d2 = sdf.parse(b.getCreatedAt());
                            assert d2 != null;
                            return d2.compareTo(d1);
                        } catch (Exception e) {
                            return 0;
                        }
                    });
                    callback.onDone(model.data,1);
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
}
