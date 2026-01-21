package com.whosin.app.service.manager;

import android.content.Context;
import android.net.Uri;

import com.google.gson.JsonObject;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.UserDetailModel;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PromoterCmApplyManager {

    @NonNull
    public static PromoterCmApplyManager shared = PromoterCmApplyManager.getInstance();

    private static PromoterCmApplyManager instance = null;

    public List<RatingModel> imageListsAdapter = new ArrayList<>();

    private Context context;

    public UserDetailModel userDetailModel;

    public boolean isEditProfile = false;

    public boolean isPromoter = false;

    public boolean isFromNotification = false;

    public String  notificationTypeId = "";

    public JsonObject object = new JsonObject();

    public List<String> uploadImageList = new ArrayList<>();

    public Uri profileImageUri = null;

    public boolean isAvatarImage = false;

    public boolean isImageSetFormSession = false;


    // --------------------------------------
    // region Singleton
    // --------------------------------------

    @androidx.annotation.NonNull
    private static synchronized PromoterCmApplyManager getInstance() {
        if (instance == null) {
            instance = new PromoterCmApplyManager();
        }
        return instance;
    }


    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public void clearManager() {
        shared.uploadImageList = new ArrayList<>();
        shared.userDetailModel = new UserDetailModel();
        shared.object = new JsonObject();

        // Reset boolean and other primitive fields
        shared.isEditProfile = shared.isPromoter = shared.isFromNotification = shared.isAvatarImage = shared.isImageSetFormSession = false;

        // Reset string fields
        shared.notificationTypeId = "";

        // Reset Uri fields
        shared.profileImageUri = null;
    }



    // endregion
    // --------------------------------------
    // region Save To Draft Event
    // --------------------------------------





    // endregion
    // --------------------------------------


    // region Data/Services
    // --------------------------------------




    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    // endregion
    // --------------------------------------
}
