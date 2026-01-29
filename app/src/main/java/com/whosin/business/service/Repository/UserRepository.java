package com.whosin.business.service.Repository;

import android.content.Context;

import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.service.DataService;
import com.whosin.business.service.models.ContainerListModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.service.rest.RestCallback;

import java.util.List;

public class UserRepository extends RealmRepository {
    private Context context;

    private static volatile UserRepository _instance = null;

    public static UserRepository shared(Context ctx) {
        if (_instance == null) {
            synchronized (ChatRepository.class) {
                _instance = new UserRepository();
            }
        }
        _instance.context = ctx;
        return _instance;
    }

    public void addUsers(List<UserDetailModel> users, CommanCallback<Boolean> delegate) {
        getRealm().executeTransactionAsync( bgRealm -> {
            bgRealm.insertOrUpdate(users);
        }, () -> {
            getRealm().refresh();
            delegate.onReceive( true );
        } );
    }

    public UserDetailModel getUserById(String id) {
        return UserDetailModel.getUserById(getRealm(), id);
    }

    public void fetchUsers(List<String> userIds, CommanCallback<Boolean> delegate){
        DataService.shared(context).requestUsersByIds(userIds, new RestCallback<ContainerListModel<UserDetailModel>>(null) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                if (model == null) { return; }
                if (model.data == null) { return; }
                if (model.data.isEmpty()) {
                    delegate.onReceive(true);
                    return;
                }

                getRealm().executeTransactionAsync( bgRealm -> {
                    bgRealm.insertOrUpdate(model.data);
                }, () -> {
                    getRealm().refresh();
                    delegate.onReceive( true );
                } );
            }
        });
    }
}
