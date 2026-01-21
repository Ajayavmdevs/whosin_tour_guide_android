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
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlockUserManager {

    @NonNull
    public static BlockUserManager shared = BlockUserManager.getInstance();

    private static BlockUserManager instance = null;

    private Context context;


    // --------------------------------------
    // region Singleton
    // --------------------------------------

    @NonNull
    private static synchronized BlockUserManager getInstance() {
        if (instance == null) {
            instance = new BlockUserManager();
        }
        return instance;
    }

    // --------------------------------------
    // region Public
    // --------------------------------------


    // Add Block User in Preferences
    public static void addBlockUserId(String blockUserId) {
        List<String> idList = getBlockUserIdList();
        if (!idList.contains(blockUserId)) {
            addToBlockUserInList(idList, blockUserId);
        }
    }

    // Remove Block user form Preferences
    public static void deleteBlockUserId(String blockUserId) {
        List<String> idList = getBlockUserIdList();
        if (idList.contains(blockUserId)) {
            idList.remove(blockUserId);
            updatePreferences(idList);
        }
    }

    // Check particular use block or not
    public boolean isUserBlocked(String blockUserId) {
        List<String> idList = getBlockUserIdList();
        return !idList.isEmpty() && idList.contains(blockUserId);
    }

    // Store block user list in Preferences
    private static void addToBlockUserInList(List<String> history, String blockUserId) {
        if (history == null) {
            history = new ArrayList<>();
        }
        history.add(blockUserId);
        Preferences.shared.setString("block_user_id", new Gson().toJson(history));
    }

    // Update block user list
    private static void updatePreferences(List<String> blockUserID) {
        String updatedJson = new Gson().toJson(blockUserID);
        Preferences.shared.setString("block_user_id", updatedJson);
    }

    // Get all block user list
    public static List<String> getBlockUserIdList() {
        String json = Preferences.shared.getString("block_user_id");
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> list = new Gson().fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    // Store block user list id
    public static void storeBlockUserIdList(List<String> blockUserID) {
        List<String> existingBlockUserIds = getBlockUserIdList();

        // Filter out IDs that are already in the block list
        List<String> idsToAdd = blockUserID.stream()
                .filter(id -> id != null && !id.isEmpty())
                .filter(id -> !existingBlockUserIds.contains(id))
                .collect(Collectors.toList());

        // Add the new IDs to the block list
        existingBlockUserIds.addAll(idsToAdd);

        // Store the updated block list
        String updatedJson = new Gson().toJson(existingBlockUserIds);
        Preferences.shared.setString("block_user_id", updatedJson);
    }



    // region Data/Services
    // --------------------------------------

    public void requestBlockUserList(Context context) {
        if (!AppSettingManager.shared.callHomeCommanApi) return;
        this.context = context;
        DataService.shared(context).requestUserBlockList(new RestCallback<ContainerListModel<UserDetailModel>>(null) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    storeBlockUserIdList( model.data.stream().map(UserDetailModel::getId)
                            .filter(id -> id != null && !id.isEmpty()).collect(Collectors.toList()));

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
