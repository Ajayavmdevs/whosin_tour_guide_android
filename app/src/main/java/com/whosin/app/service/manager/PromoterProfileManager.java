package com.whosin.app.service.manager;

import static com.whosin.app.comman.AppDelegate.activity;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.CartModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.PromoterInvitedUserModel;
import com.whosin.app.service.models.PromoterPaidPassModel;
import com.whosin.app.service.models.PromoterProfileModel;
import com.whosin.app.service.models.TimeSlotModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.units.qual.A;
import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class PromoterProfileManager {

    @NonNull
    public static PromoterProfileManager shared = PromoterProfileManager.getInstance();

    private static PromoterProfileManager instance = null;

    public CommanCallback<Boolean> setProfileCallBack;

    private Context context;

    public PromoterProfileModel promoterProfileModel;

    public TimeSlotModel timeSlotModel = null;

    public JsonObject promoterEventObject = new JsonObject();

    public boolean isEventEdit = false;

    public boolean isEventSaveToDraft = false;

    public boolean isEventRepost = false;

    public PromoterEventModel promoterEventModel =  null;

    public CommanCallback<UserDetailModel> callbackForHeader;

    public List<String> invitedUserList = new ArrayList<>();

    public List<VenueObjectModel> venueList = new ArrayList<>();

    public List<UserDetailModel> subAdminList = new ArrayList<>();

    public List<PromoterPaidPassModel> promoterPaidPassList = new ArrayList<>();

    public List<String> eventCustomCategory = new ArrayList<>();



    // --------------------------------------
    // region Singleton
    // --------------------------------------

    @androidx.annotation.NonNull
    private static synchronized PromoterProfileManager getInstance() {
        if (instance == null) {
            instance = new PromoterProfileManager();
        }
        return instance;
    }


    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Save To Draft Event
    // --------------------------------------


    public static void addEventIntoDraft(PromoterEventModel model) {
        List<PromoterEventModel> list = getSaveToDraftEventList();
        if (list.isEmpty()) {
            list = new ArrayList<>();
        }
        list.add(model);
        Preferences.shared.setString("promoterEventSaveDraft", new Gson().toJson(list));
    }

    public static void updatePreferences(List<PromoterEventModel> list) {
        String updatedJson = new Gson().toJson(list);
        Preferences.shared.setString("promoterEventSaveDraft", updatedJson);
        EventBus.getDefault().post(new PromoterCirclesModel());
    }

    public static void clearSaveDraftEvent() {
        List<PromoterEventModel> emptyList = new ArrayList<>();
        Preferences.shared.setString("promoterEventSaveDraft", new Gson().toJson(emptyList));
    }

    public static void updateEventIntoDraft(String id, PromoterEventModel model) {
        List<PromoterEventModel> tmpList = getSaveToDraftEventList();
        if (Utils.isNullOrEmpty(id)){return;}
        if (!tmpList.isEmpty()) {
            for (int i = 0; i < tmpList.size(); i++) {
                PromoterEventModel existingModel = tmpList.get(i);
                if (existingModel.getSaveToDraftId().equals(id)) {
                    tmpList.set(i, model);
                    updatePreferences(tmpList);
                    break;
                }
            }

        }
    }


    public static void removeEventIntoDraft(String id) {
        if (Utils.isNullOrEmpty(id)) {
            return;
        }
        List<PromoterEventModel> tmpList = getSaveToDraftEventList();
        if (!tmpList.isEmpty()) {
            boolean removed = tmpList.removeIf(event -> id.equals(event.getSaveToDraftId()));
            if (removed) {
                updatePreferences(tmpList); // Persist updated list only if an item was removed
            }
        }
    }


//    public static void removeEventIntoDraft(String id) {
//        List<PromoterEventModel> tmpList = getSaveToDraftEventList();
//        if (Utils.isNullOrEmpty(id)) {
//            return;
//        }
//        if (!tmpList.isEmpty()) {
//            Iterator<PromoterEventModel> iterator = tmpList.iterator();
//            while (iterator.hasNext()) {
//                PromoterEventModel existingModel = iterator.next();
//                if (existingModel.getSaveToDraftId().equals(id)) {
//                    iterator.remove();
//                    updatePreferences(tmpList);
//                    break;
//                }
//            }
//        }
//    }


    public static List<PromoterEventModel> getSaveToDraftEventList() {
        String json = Preferences.shared.getString("promoterEventSaveDraft");
        Type type = new TypeToken<List<PromoterEventModel>>() {
        }.getType();
        List<PromoterEventModel> list = new Gson().fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public boolean isFormPromoterVenue(String venueId) {
        if (venueList == null || venueList.isEmpty()) {
            return false;
        }
        return venueList.stream().anyMatch(p -> p.getId().equals(venueId));
    }

    public UserDetailModel getSubAdmin(String id) {
        if (id == null || subAdminList == null || subAdminList.isEmpty()) return null;

        return subAdminList.stream()
                .filter(model -> id.equals(model.getId()))
                .findFirst()
                .orElse(null);
    }


    public String getPaidPassId(String passTitle){
        if (promoterPaidPassList == null ||  promoterPaidPassList.isEmpty()) return "";
        Optional<PromoterPaidPassModel> passObject = promoterPaidPassList.stream().filter(p -> p.getTitle().equalsIgnoreCase(passTitle)).findFirst();
        if (passObject.isPresent()){
            return passObject.get().getId();
        }
        return  "";
    }

    public String getPaidPassString(String id){
        if (promoterPaidPassList == null ||  promoterPaidPassList.isEmpty()) return "";
        Optional<PromoterPaidPassModel> passObject = promoterPaidPassList.stream().filter(p -> p.getId().equalsIgnoreCase(id)).findFirst();
        if (passObject.isPresent()){
            return passObject.get().getTitle();
        }
        return  "";
    }



    // endregion
    // --------------------------------------


    // region Data/Services
    // --------------------------------------


    public void requestPromoterEventInviteUser(String eventId, Activity activity) {
        if (TextUtils.isEmpty(eventId)) return;
        if (invitedUserList != null && !invitedUserList.isEmpty()) invitedUserList.clear();
        Log.d("TAG", "requestPromoterEventInviteUser: " + eventId);
        if (activity == null) return;
        DataService.shared(activity).requestPromoterEventInviteUser(eventId,new RestCallback<ContainerModel<PromoterInvitedUserModel>>(null) {
            @Override
            public void result(ContainerModel<PromoterInvitedUserModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }


                if (model.data != null) {
                    if (model.data.getInvitedUsers() != null && !model.data.getInvitedUsers().isEmpty()) {
                        invitedUserList = model.data.getInvitedUsers();
                    }
                }

            }
        });
    }


    public void requestPromoterVenues(Activity activity) {
        if (activity == null) return;
        DataService.shared(activity).requestPromoterVenues(new RestCallback<ContainerListModel<VenueObjectModel>>(null) {
            @Override
            public void result(ContainerListModel<VenueObjectModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null) {
                    if (!venueList.isEmpty()) venueList.clear();
                    venueList.addAll(model.data);
                }

            }
        });
    }
    public void requestSubAdminList(Activity activity){
        if (activity == null) return;
        DataService.shared(activity).requestPromoterSubAdminList(new RestCallback<ContainerListModel<UserDetailModel>>(null) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null) {
                    if (!subAdminList.isEmpty()) subAdminList.clear();
                    subAdminList.addAll(model.data);
                }

            }
        });
    }


    public void requestPromoterPaidPass(Activity activity){
        DataService.shared(activity).requestPromoterPaidPassList(new RestCallback<ContainerListModel<PromoterPaidPassModel>>(null) {
            @Override
            public void result(ContainerListModel<PromoterPaidPassModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }


                if (model.data != null && !model.data.isEmpty()) {
                    if (!promoterPaidPassList.isEmpty()) promoterPaidPassList.clear();
                    promoterPaidPassList = model.data;
                }else {
                    promoterPaidPassList = new ArrayList<>();
                }


            }
        });
    }


    public void requestPromoterEventGetCustomCategory(Activity activity) {
        if (activity == null) return;
        DataService.shared(activity).requestPromoterEventGetCustomCategory(new RestCallback<ContainerModel<List<String>>>(null) {
            @Override
            public void result(ContainerModel<List<String>> model, String error) {
                eventCustomCategory.clear();
                if (model.data != null && !model.data.isEmpty()){
                    eventCustomCategory.addAll(model.data);
                }
            }
        });
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    // endregion
    // --------------------------------------

}
