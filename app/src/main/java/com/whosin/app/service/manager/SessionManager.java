package com.whosin.app.service.manager;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.onesignal.OneSignal;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.IPInfoTask;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.SaveJsonInBackground;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.BooleanResult;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.service.DataService;
import com.whosin.app.service.Repository.ChatRepository;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;

import com.whosin.app.service.models.HomeObjectModel;
import com.whosin.app.service.models.MyUserFeedModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.PromoterProfileModel;
import com.whosin.app.service.models.RoleAcessModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.UserTokenModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.VenueRecommendedModel;
import com.whosin.app.service.models.VoucherModel;
import com.whosin.app.service.models.myCartModels.MyCartMainModel;
import com.whosin.app.service.models.newExploreModels.ExploreObjectModel;
import com.whosin.app.service.rest.RestCallback;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SessionManager {

    @NonNull
    public static SessionManager shared = SessionManager.getInstance();

    @Nullable
    private static volatile SessionManager instance = null;

    private Context context;
    private UserDetailModel userObjectModel;
    private UserTokenModel userTokenModel;
    private ComplimentaryProfileModel complimentaryProfileModel;
    private ContactListModel contactListModel;
    private List<ContactListModel> followingList = new ArrayList<>();

    public JsonObject jsonObject = new JsonObject();

    private HomeObjectModel homeObjectModel;

    private MyCartMainModel myCartMainModel;

    private HomeObjectModel searchHomeObjectModel;

    private ExploreObjectModel exploreObjectModel;

    private VenueObjectModel venueModel;

    private PromoterProfileModel promoterProfileModel;


    // --------------------------------------
    // region Singleton
    // --------------------------------------
    private SessionManager() {
    }

    @NonNull
    private static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

//    public void saveUserData(@NonNull UserDetailModel model) {
//        if (context != null) {
//            userObjectModel = model;
//            String json = new Gson().toJson( userObjectModel );
//            Preferences.shared.setString( "LoginUserDetail", json );
//        }
//    }

    public void saveUserData(@NonNull UserDetailModel model) {
        if (context == null) return;

        userObjectModel = model;
        Gson gson = new Gson();
        String json = gson.toJson(model);

//        if (!model.getRoleAcessModelList().isEmpty() && "promoter-subadmin".equals(model.getRoleAcessModelList().get(0).getType())) {
//            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
//            jsonObject.addProperty("isPromoter", true);
//            json = gson.toJson(jsonObject);
//        }

        if (model.getRoleAcessModelList() != null && !model.getRoleAcessModelList().isEmpty()) {
            RoleAcessModel roleAccess = model.getRoleAcessModelList().get(0);
            assert roleAccess != null;
            if ("promoter-subadmin".equals(roleAccess.getType())) {
                JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
                jsonObject.addProperty("isPromoter", true);
                json = gson.toJson(jsonObject);
            }
        }

        Preferences.shared.setString("LoginUserDetail", json);
    }

    public void saveCmUserData(@NonNull ComplimentaryProfileModel model) {
        if (context != null) {
            complimentaryProfileModel = model;
            String json = new Gson().toJson( userObjectModel );
            Preferences.shared.setString( "ComplimentaryUserDetail", json );
        }
    }

    public void saveSubAdminUserData(@NonNull UserTokenModel model,Context context) {
        if (context != null) {
            userTokenModel = model;
            String json = new Gson().toJson( userTokenModel );
            Preferences.shared.setString( "LoginSubAdminDetail", json );
        }
    }

    public UserTokenModel getSubAdminUser() {
        if (userTokenModel == null) {
            String json = Preferences.shared.getString( "LoginSubAdminDetail" );
            if (!TextUtils.isEmpty( json )) {
                userTokenModel = new Gson().fromJson( json, UserTokenModel.class );
            }
        }
        if (userTokenModel == null) {
            return null;
        }
        return userTokenModel;
    }

    public boolean isSubAdmin(){
        return getSubAdminUser() != null && userTokenModel != null && userTokenModel.getLoginType().equals("sub-admin");
    }

    public boolean isPromoterSubAdmin(){
        return getUser() != null && !getUser().getRoleAcessModelList().isEmpty() && getUser().getRoleAcessModelList().get(0).getType().equals("promoter-subadmin");
    }

    public String getPromoterId() {
        return (getUser() != null && !getUser().getRoleAcessModelList().isEmpty()
                && "promoter-subadmin".equals(getUser().getRoleAcessModelList().get(0).getType()))
                ? getUser().getRoleAcessModelList().get(0).getTypeId()
                : "";
    }


    public ComplimentaryProfileModel getCmUserProfile() {
        if (complimentaryProfileModel == null) {
            String json = Preferences.shared.getString( "ComplimentaryUserDetail" );
            if (!TextUtils.isEmpty( json )) {
                complimentaryProfileModel = new Gson().fromJson( json, ComplimentaryProfileModel.class );
            }
        }
        if (complimentaryProfileModel == null) {
            return new ComplimentaryProfileModel();
        }
        return complimentaryProfileModel;
    }

    public String getToken() {
        return Preferences.shared.getString( "token" );
    }

    public UserDetailModel getUser() {
        if (userObjectModel == null) {
            String json = Preferences.shared.getString( "LoginUserDetail" );
            if (!TextUtils.isEmpty( json )) {
                userObjectModel = new Gson().fromJson( json, UserDetailModel.class );
            }
        }
        if (userObjectModel == null) {
            return new UserDetailModel();
        }
        return userObjectModel;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context _context) {
        if (_context != null)
            this.context = _context;
    }

    public void clearSessionData(Context context) {
        if (context != null) {
            userObjectModel = null;
            ChatRepository.shared( context ).clearDb();
            Preferences.shared.setContext( context );
            Preferences.shared.clearData();
            TranslationManager.shared.clearManager();
        }
    }

    public void saveHomeBlockData(@NonNull HomeObjectModel model) {
        if (context != null) {
            homeObjectModel = model;
            Thread backgroundThread = new Thread( () -> {
                String json = new Gson().toJson( model );
                Preferences.shared.setString( "HomeBlockResponse", json );
            } );
            backgroundThread.start();
        }
    }

    public HomeObjectModel geHomeBlockData() {
        if (homeObjectModel == null) {
            String json = Preferences.shared.getString( "HomeBlockResponse" );
            if (!TextUtils.isEmpty( json )) {
                try {
                    homeObjectModel = new Gson().fromJson( json, HomeObjectModel.class );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return homeObjectModel;
    }

    public void saveTicketCartData(@NonNull MyCartMainModel mainModel) {
        if (context != null) {
            myCartMainModel = mainModel;
            Thread backgroundThread = new Thread( () -> {
                String json = new Gson().toJson( mainModel );
                Preferences.shared.setString( "MyCartTicketResponse", json );
            } );
            backgroundThread.start();
        }
    }

    public void clearTicketCartData(){
        Preferences.shared.setString( "MyCartTicketResponse", "");
        myCartMainModel = null;
    }

    public MyCartMainModel geMyCartTicketData() {
        if (myCartMainModel == null) {
            String json = Preferences.shared.getString( "MyCartTicketResponse" );
            if (!TextUtils.isEmpty( json )) {
                try {
                    myCartMainModel = new Gson().fromJson( json, MyCartMainModel.class );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return myCartMainModel;
    }

    public void saveSearchBlockData(@NonNull HomeObjectModel model) {
        if (context != null) {
            searchHomeObjectModel = model;
            Thread backgroundThread = new Thread( () -> {
                String json = new Gson().toJson( model );
                Preferences.shared.setString( "SearchBlockResponse", json );
            } );
            backgroundThread.start();
        }
    }

    public HomeObjectModel getSearchBlockData() {
        if (searchHomeObjectModel == null) {
            String json = Preferences.shared.getString( "SearchBlockResponse" );
            if (!TextUtils.isEmpty( json )) {
                try {
                    searchHomeObjectModel = new Gson().fromJson( json, HomeObjectModel.class );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return searchHomeObjectModel;
    }

    public void saveExploreBlockData(@NonNull ExploreObjectModel model) {
        if (context != null) {
            exploreObjectModel = model;
            Thread backgroundThread = new Thread( () -> {
                String json = new Gson().toJson( model );
                Preferences.shared.setString( "ExploreBlockResponse", json );
            } );
            backgroundThread.start();
        }
    }

    public ExploreObjectModel geExploreBlockData() {
        if (exploreObjectModel == null) {
            String json = Preferences.shared.getString( "ExploreBlockResponse" );
            if (!TextUtils.isEmpty( json )) {
                try {
                    exploreObjectModel = new Gson().fromJson( json, ExploreObjectModel.class );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return exploreObjectModel;
    }

    public void saveFollowingData(@NonNull List<ContactListModel> list) {
        if (context != null) {
            if (SessionManager.shared.getUser() != null && !list.isEmpty()) {
                list.removeIf(p -> p.getId().equals(SessionManager.shared.getUser().getId()));
            }
            String followingJson = new Gson().toJson( list );
            Preferences.shared.setString( "FollowingList", followingJson );
        }
    }

    public List<ContactListModel> getFollowingData() {
        String json = Preferences.shared.getString( "FollowingList" );
        List<ContactListModel> contactListModel = new ArrayList<>();
        if (!TextUtils.isEmpty( json )) {
            Type listType = new TypeToken<List<ContactListModel>>() {
            }.getType();
            contactListModel = new Gson().fromJson( json, listType );
        }
        return contactListModel;
    }

    public void saveFollowersData(@NonNull List<ContactListModel> list) {
        if (context != null) {
            String followingJson = new Gson().toJson( list );
            Preferences.shared.setString( "FollowersList", followingJson );
        }
    }

    public List<ContactListModel> getFollowersData() {
        String json = Preferences.shared.getString( "FollowersList" );
        List<ContactListModel> contactListModel = new ArrayList<>();
        if (!TextUtils.isEmpty( json )) {
            Type listType = new TypeToken<List<ContactListModel>>() {
            }.getType();
            contactListModel = new Gson().fromJson( json, listType );
        }
        return contactListModel;
    }

    public void saveSearchRecommended(@NonNull List<VenueRecommendedModel> list) {
        if (context != null) {
            String venueRecommended = new Gson().toJson( list );
            Preferences.shared.setString( "venueRecommendedList", venueRecommended );
        }
    }

    public List<VenueRecommendedModel> getVenueRecommended() {
        String json = Preferences.shared.getString( "venueRecommendedList" );
        List<VenueRecommendedModel> venueRecommendedModels = new ArrayList<>();
        if (!TextUtils.isEmpty( json )) {
            Type list = new TypeToken<List<VenueRecommendedModel>>() {
            }.getType();
            venueRecommendedModels = new Gson().fromJson( json, list );
        }
        return venueRecommendedModels;
    }

    public void saveProfileFeed(@NonNull List<MyUserFeedModel> list) {
        if (context != null) {
            Map.Entry<String, List<MyUserFeedModel>> entry = new AbstractMap.SimpleEntry<>( "profileFeedList", list );
            SaveJsonInBackground<List<MyUserFeedModel>> backgroundTask = new SaveJsonInBackground<>();
            backgroundTask.execute( entry );
        }
    }

    public List<MyUserFeedModel> getProfileFeed() {
        String json = Preferences.shared.getString( "profileFeedList" );
        List<MyUserFeedModel> feedList = new ArrayList<>();
        if (!TextUtils.isEmpty( json )) {
            Type list = new TypeToken<List<MyUserFeedModel>>() {
            }.getType();
            feedList = new Gson().fromJson( json, list );
        }
        return feedList;
    }

    public void saveShareVenue(@NonNull String shareString) {
        if (context != null) {
            Preferences.shared.setString("shareList", shareString);
        }
    }

    public String getVenueShareJson() {
        return Preferences.shared.getString("shareList");
    }

    public void setNormalUserAsSubAdmin(){
        String json = Preferences.shared.getString( "LoginUserDetail" );
        if (!TextUtils.isEmpty(json)){
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            jsonObject.addProperty("isPromoter", true);
            String updatedJsonString = gson.toJson(jsonObject);
            Preferences.shared.setString( "LoginUserDetail",updatedJsonString);
        }
    }

    public void savePromoterUserData(@NonNull PromoterProfileModel model,Context context) {
        if (context != null) {
            promoterProfileModel = model;
            String json = new Gson().toJson( promoterProfileModel );
            Preferences.shared.setString( "PromoterUserDetail", json );
        }
    }

    public PromoterProfileModel getPromoterProfileUser() {
        if (promoterProfileModel == null) {
            String json = Preferences.shared.getString( "PromoterUserDetail" );
            if (!TextUtils.isEmpty( json )) {
                promoterProfileModel = new Gson().fromJson( json, PromoterProfileModel.class );
            }
        }
        if (promoterProfileModel == null) {
            return null;
        }
        return promoterProfileModel;
    }

    public String isUserOrSubAdmin(){
        return isPromoterSubAdmin() ? getPromoterId() : getUser().getId();
    }


    public void saveLangData(Map<String, Map<String, String>> map) {
        if (context != null) {
            Thread backgroundThread = new Thread(() -> {
                String json = new Gson().toJson(map);
                Preferences.shared.setString("LangData", json);
            });
            backgroundThread.start();
        }
    }

    public Map<String, Map<String, String>> getLangData() {
        String json = Preferences.shared.getString("LangData");
        if (!TextUtils.isEmpty(json)) {
            try {
                Type type = new TypeToken<Map<String, Map<String, String>>>(){}.getType();
                return new Gson().fromJson(json, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }


    // endregion
    // --------------------------------------
    // region Data/Services
    // --------------------------------------

    public void requestGetToken(Context context, @NonNull RestCallback<String> callback) {
        DataService.shared( context ).requestGetToken(new RestCallback<ContainerModel<String>>(null) {
            @Override
            public void result(ContainerModel<String> model, String error) {
                if (!Utils.isNullOrEmpty( error )) {
                    callback.result( null, error );
                    return;
                }
                if (model == null) {
                    callback.result( null, SessionManager.this.context.getString( R.string.service_message_something_wrong ) );
                    return;
                }

                callback.result( model.getData(),  null );

            }
        } );
    }


    public void requestCheckSession(Activity activity,@NonNull BooleanResult callbackForUseSessionExpire) {
        DataService.shared(activity).requestCheckUserSession(new RestCallback<ContainerModel<UserDetailModel>>(null) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                if (!Utils.isNullOrEmpty(error)) {
                    if (error.equals("session expired!")){
                        callbackForUseSessionExpire.success(true,"Session expired, please login again!");
                    }else {
                        callbackForUseSessionExpire.success(false,error);
                    }
                    return;
                }

                callbackForUseSessionExpire.success(false, "");
//                if (model.getData() != null) {
//                    if (model.getData().getAccountStatus().equals("permanent-ban")) {
//                        callbackForUseSessionExpire.success(true, "Your account has been permanently banned. You will now be logged out.");
//                    } else if (model.getData().getAccountStatus().equals("temporary-ban")) {
//                        callbackForUseSessionExpire.success(true, "Your account has been temporarily banned. Please try again later.");
//                    } else {
//                        callbackForUseSessionExpire.success(false, "");
//                    }
//                }else {
//                    callbackForUseSessionExpire.success(false, "");
//                }
            }
        });
    }


    public void loginWithGoogle(String token, Context context, @NonNull BooleanResult callback) {
        setContext( context );
        JsonObject params = new JsonObject();
        params.addProperty( "token", token );


        DataService.shared( context ).requestLoginWithGoogle( params, new RestCallback<ContainerModel<UserTokenModel>>(null) {
            @Override
            public void result(ContainerModel<UserTokenModel> model, String error) {
                if (!Utils.isNullOrEmpty( error )) {
                    callback.success( false, error );
                    return;
                }
                if (model == null) {
                    callback.success( false, SessionManager.this.context.getString( R.string.service_message_something_wrong ) );
                    return;
                }
                Preferences.shared.setString( "token", model.getData().getToken() );
                saveUserData( model.getData().getUserDetail() );
                callback.success( true, null );

            }
        } );
    }

    public void guestLogin(Context context, @NonNull BooleanResult callback) {
        setContext(context);

        DataService.shared(context).requestGuestLogin(new RestCallback<ContainerModel<UserTokenModel>>(null) {
            @Override
            public void result(ContainerModel<UserTokenModel> model, String error) {
                if (!Utils.isNullOrEmpty(error)) {
                    callback.success(false, error);
                    return;
                }
                if (model == null) {
                    callback.success(false, SessionManager.this.context.getString(R.string.service_message_something_wrong));
                    return;
                }
                Preferences.shared.setString("token", model.getData().getToken());
                saveUserData(model.getData().getUserDetail());
                callback.success(true, null);
            }
        });
    }



    public void verifyOtp(boolean isManagePromoter ,String userId, String otp, Context context, @NonNull RestCallback<UserTokenModel> callback) {
        setContext( context );

        new IPInfoTask(data -> {
            JsonObject params = new JsonObject();
            params.addProperty( "userId", userId );
            params.addProperty( "otp", otp );

            if (isManagePromoter){
                params.addProperty( "isManagePromoter", isManagePromoter );
            }

            JsonObject metaData = new JsonObject();
            metaData.addProperty("device_id", Utils.getDeviceUniqueId(context));
            metaData.addProperty("device_name", Build.DEVICE);
            metaData.addProperty("device_model", Build.MODEL);
            metaData.addProperty("device_location", data);
            params.add("metadata", metaData);



            DataService.shared( context ).requestVerifyOtp( params, new RestCallback<ContainerModel<UserTokenModel>>( null ) {
                @Override
                public void result(ContainerModel<UserTokenModel> model, String error) {
                    if (!Utils.isNullOrEmpty( error )) {
                        callback.result( null, error );
                        return;
                    }
                    if (model == null) {
                        callback.result( null, SessionManager.this.context.getString( R.string.service_message_something_wrong ) );
                        return;
                    }
                    ChatManager.shared.connect();
                    if (model.getData() != null && !model.getData().isAuthenticationPending()) {
                        Preferences.shared.setString("token", model.getData().getToken());
                    }

                    if (model.getData() != null) {
                        UserDetailModel userDetail = model.getData().getUserDetail();
                        if (userDetail != null) {
                            saveUserData(userDetail);
                        } else {
                            Log.e("Error", "Unexpected data type for UserDetail: " + userDetail.getClass().getSimpleName());
                        }
                    }


//                    if (model.getData() != null && model.getData().getUserDetail() != null) saveUserData( model.getData().getUserDetail() );

                    AppSettingManager.shared.requestAppSetting( context );
                    if (model.getData() != null && model.getData().getUserDetail() != null && !model.getData().getUserDetail().getRoleAcessModelList().isEmpty()){
                        if (model.getData().getUserDetail().getRoleAcessModelList().get(0).getType().equals("promoter-subadmin")){
                            setNormalUserAsSubAdmin();
                        }
                    }
                    callback.result( model.data, null );

                }
            } );
        }).execute();




    }

    public void verifyPassword(String userId, String password, Context context, @NonNull BooleanResult callback) {
        setContext( context );
        JsonObject params = new JsonObject();
        params.addProperty( "userId", userId );
        params.addProperty( "password", password );
        DataService.shared( context ).requestVerifyPassword( params, new RestCallback<ContainerModel<UserTokenModel>>( null ) {
            @Override
            public void result(ContainerModel<UserTokenModel> model, String error) {
                if (!Utils.isNullOrEmpty( error )) {
                    callback.success( false, error );
                    return;
                }
                if (model == null) {
                    callback.success( false, SessionManager.this.context.getString( R.string.service_message_something_wrong ) );
                    return;
                }

                Preferences.shared.setString( "token", model.getData().getToken() );
                saveUserData( model.getData().getUserDetail() );
                callback.success( true, null );

            }
        } );
    }

    public void updateProfile(Context context, JsonObject object, @NonNull BooleanResult callback) {
        DataService.shared( context ).requestUpdateProfile( object, new RestCallback<ContainerModel<UserDetailModel>>(null) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {

                if (!Utils.isNullOrEmpty( error )) {
                    callback.success( false, error );
                    return;
                }
                if (model == null) {
                    callback.success( false, SessionManager.this.context.getString( R.string.service_message_something_wrong ) );
                    return;
                }

                if (!TextUtils.isEmpty( model.getData().getToken() )) {
                    Preferences.shared.setString( "token", model.getData().getToken() );
                }

                saveUserData( model.getData() );
                callback.success( true, null );
            }
        } );
    }

    public void logout(Context context, BooleanResult callback) {
        JsonObject object = new JsonObject();
        object.addProperty("deviceId", Utils.getDeviceUniqueId(context));
        DataService.shared(context).requestLogout(object, new RestCallback<ContainerModel<UserDetailModel>>(null) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                if (!Utils.isNullOrEmpty( error )) {
                    callback.success( false, error );
                    return;
                }
                if (model == null) {
                    callback.success( false, SessionManager.this.context.getString( R.string.service_message_something_wrong ) );
                    return;
                }
                OneSignal.logout();
                int isUserGoogleLogin =  Preferences.shared.getInt("isUserGoogleLogin");
                if (isUserGoogleLogin == 1){
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();
                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, gso);
                    googleSignInClient.signOut().addOnCompleteListener(task -> {

                    });
                }
                clearSessionData(context);
                callback.success(true, "");
            }
        });
    }

    public void getCurrentUserProfile(Activity activity, BooleanResult callback) {
//        context = activity;
        DataService.shared( context ).requestUserProfile( getUser().getId(), new RestCallback<ContainerModel<UserDetailModel>>(null) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
//                    callback.success( false, error );
                    return;
                }
                if (model.getData() != null) {
                    saveUserData( model.getData() );
                    callback.success( true, "" );
                } else {
                    callback.success( false, "something went wrong" );
                }
            }
        } );
    }

    public void getCurrentUserProfile(BooleanResult callback) {
        DataService.shared(context).requestUserProfile(getUser().getId(), new RestCallback<ContainerModel<UserDetailModel>>(null) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    return;
                }
                if (model.getData() != null) {
                    saveUserData(model.getData());
                    if (model.getData().isRingMember()){
                        callback.success(true,"");
                    }else {
                        callback.success(false,"");
                    }
                }
            }
        });
    }


    // endregion
    // --------------------------------------

}
