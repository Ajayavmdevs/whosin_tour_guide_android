package com.whosin.business.service.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.BooleanResult;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.service.DataService;
import com.whosin.business.service.models.AdListModel;
import com.whosin.business.service.models.BigBusModels.BigBusOptionsItemModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.TravelDeskModels.TravelDeskOptionDataModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.models.rayna.TourOptionsModel;
import com.whosin.business.service.models.whosinTicketModel.WhosinTicketTourOptionModel;
import com.whosin.business.service.rest.RestCallback;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class RaynaTicketManager {

    @SuppressLint("StaticFieldLeak")
    @NonNull
    public static RaynaTicketManager shared = RaynaTicketManager.getInstance();

    @SuppressLint("StaticFieldLeak")
    private static RaynaTicketManager instance = null;
    public List<TourOptionsModel> selectedAddonModels;

    private Context context;

    public RaynaTicketDetailModel raynaTicketDetailModel = null;

    public TourOptionsModel tourOptionsModel = null;

    public JsonObject object = new JsonObject();

    public List<RaynaTicketDetailModel> raynaTicketList = new ArrayList<>();

    public List<Activity>  activityList = new ArrayList<>();

    public CommanCallback<String> callback = null;

    public CommanCallback<Boolean> callbackForReload = null;

    public List<TourOptionsModel> selectedTourModel = new ArrayList<>();

    public JsonArray cancellationObject = new JsonArray();

    public CommanCallback<Boolean> walletRedirectCallBack;


    // Whosin Ticket parameter

    public List<TourOptionsModel> raynaWhosinModels = new ArrayList<>();

    public JsonObject whosinTicketTypeObj = new JsonObject();

    // Travel Desk Ticket parameter

    public List<TravelDeskOptionDataModel> travelDeskOptionDataModels = new ArrayList<>();

    public List<TravelDeskOptionDataModel> selectTravelDeskOptionDataModels = new ArrayList<>();

    // Whosin Ticket parameter

    public List<WhosinTicketTourOptionModel> whosinCustomTicketTourOption = new ArrayList<>();

    public List<WhosinTicketTourOptionModel> selectedTourModelForWhosin = new ArrayList<>();

    // Big Bus Ticket parameter

    public List<BigBusOptionsItemModel> bigBusTicketTourOption = new ArrayList<>();

    public List<BigBusOptionsItemModel> selectedTourModelForBigBus = new ArrayList<>();


    // Update Cart Api Call Back

    public CommanCallback<Boolean> cartReloadCallBack;


    // --------------------------------------
    // region Singleton
    // --------------------------------------

    @androidx.annotation.NonNull
    private static synchronized RaynaTicketManager getInstance() {
        if (instance == null) {
            instance = new RaynaTicketManager();
        }
        return instance;
    }


    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public void clearManager(){
        tourOptionsModel = null;
        raynaTicketDetailModel = null;
        object = new JsonObject();
        activityList = new ArrayList<>();
        selectedTourModel = new ArrayList<>();
        cancellationObject = new JsonArray();
        selectedTourModel = new ArrayList<>();
        whosinTicketTypeObj = new JsonObject();
        travelDeskOptionDataModels = new ArrayList<>();
        selectTravelDeskOptionDataModels = new ArrayList<>();
        whosinCustomTicketTourOption = new ArrayList<>();
        selectedTourModelForWhosin = new ArrayList<>();
        bigBusTicketTourOption = new ArrayList<>();
        selectedTourModelForBigBus = new ArrayList<>();
        selectedAddonModels = new ArrayList<>();
    }


    // Finish all activities
    public void finishAllActivities() {
        for (int i = 0; i < activityList.size(); i++) {
            if (!activityList.get(i).isFinishing()) {
                activityList.get(i).finish();
            }
        }

    }


    // endregion
    // --------------------------------------
    // region Data/Services
    // --------------------------------------


    public void requestRaynaTicketFavorite(Context context,String id, BooleanResult callback) {
        this.context = context;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type","ticket");
        jsonObject.addProperty("typeId",id);
        DataService.shared(context).requestRaynaTicketFavorite(jsonObject, new RestCallback<ContainerModel<AdListModel>>(null) {
            @Override
            public void result(ContainerModel<AdListModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    callback.success(false, error);
                    return;
                }

                callback.success(true,model.message);
            }
        });
    }

    public void updateAddonsJson(TourOptionsModel option) {
        JsonArray addonsArray = new JsonArray();
        for (TourOptionsModel model : selectedAddonModels) {
            JsonObject addonObj = new JsonObject();
            addonObj.addProperty("tourId", option.get_id());
            addonObj.addProperty("optionId", model.get_id());
            addonObj.addProperty("_id", model.get_id());
            addonObj.addProperty("departureTime", "");
            addonObj.addProperty("tourDate", !TextUtils.isEmpty(model.getTourOptionSelectDate()) ? model.getTourOptionSelectDate() : model.getBookingDate());
            if (model.getRaynaTimeSlotModel() != null) {
                addonObj.addProperty("startTime", model.getRaynaTimeSlotModel().getAvailabilityTime());
                addonObj.addProperty("timeSlot", model.getRaynaTimeSlotModel().getAvailabilityTime());
                addonObj.addProperty("timeSlotId", model.getRaynaTimeSlotModel().getId());
            } else if (model.getAvailabilityType().equals("same_as_option")) {
                if (option.getAvailabilityType().equals("slot")) {
                    addonObj.addProperty("startTime", option.getAvailabilityTimeSlot().get(option.whosinTypeTicketSlotPosition).getAvailabilityTime());
                    addonObj.addProperty("timeSlot", option.getAvailabilityTimeSlot().get(option.whosinTypeTicketSlotPosition).getAvailabilityTime());
                    addonObj.addProperty("timeSlotId", option.getAvailabilityTimeSlot().get(option.whosinTypeTicketSlotPosition).getId());
                } else {
                    addonObj.addProperty("startTime", option.getAvailabilityTime());
                    addonObj.addProperty("timeSlot", option.getAvailabilityTime());
                    addonObj.addProperty("timeSlotId", "0");
                }
            }else {
                addonObj.addProperty("startTime", model.getAvailabilityTime());
                addonObj.addProperty("timeSlot", "");
                addonObj.addProperty("timeSlotId", "0");
            }
            addonObj.addProperty("addOnTitle", model.getTitle());
            addonObj.addProperty("addOnImage", model.getImages().get(0));
            addonObj.addProperty("addOndesc", model.getSortDescription());
            addonObj.addProperty("adult_title", model.getAdultTitle());
            addonObj.addProperty("child_title", model.getChildTitle());
            addonObj.addProperty("infant_title", model.getInfantTitle());
            addonObj.addProperty("adult_description", model.getAdultDescription());
            addonObj.addProperty("child_description", model.getChildDescription());
            addonObj.addProperty("infant_description", model.getInfantDescription());
            addonObj.addProperty("endTime", "");
            addonObj.addProperty("pickup", "");
            addonObj.addProperty("message", "");
            addonObj.add("transferId", null);
            addonObj.addProperty("adultRate", model.getAdultPrice());
            addonObj.addProperty("adult", model.getTmpAdultValue());
            addonObj.addProperty("childRate", model.getChildPrice());
            addonObj.addProperty("child", model.getTmpChildValue());
            addonObj.addProperty("infantRate", model.getInfantPrice());
            addonObj.addProperty("infant", model.getTmpInfantValue());
            addonObj.addProperty("whosinTotal", model.updatePrice());
            addonObj.addProperty("serviceTotal", model.updatePrice());
            addonsArray.add(addonObj);
        }
        object.add("Addons", addonsArray);
    }

    // --------------------------------------
    // region Rayna Ticket Method
    // --------------------------------------

    public String getTourId() {
        if (raynaTicketDetailModel != null
                && raynaTicketDetailModel.getTourDataModel() != null
                && !TextUtils.isEmpty(raynaTicketDetailModel.getTourDataModel().getTourId())) {
            return raynaTicketDetailModel.getTourDataModel().getTourId();
        } else if (raynaTicketDetailModel != null) {
            return raynaTicketDetailModel.getTourId();
        }
        return "";
    }


    public String getContractId() {
        if (raynaTicketDetailModel != null
                && raynaTicketDetailModel.getTourDataModel() != null
                && !TextUtils.isEmpty(raynaTicketDetailModel.getTourDataModel().getContractId())) {
            return raynaTicketDetailModel.getTourDataModel().getContractId();
        } else if (raynaTicketDetailModel != null
                && !TextUtils.isEmpty(raynaTicketDetailModel.getContractId())) {
            return raynaTicketDetailModel.getContractId();
        } else if (raynaTicketDetailModel != null && raynaTicketDetailModel.getTourDetail() != null && !TextUtils.isEmpty(raynaTicketDetailModel.getTourDetail().getContractId())) {
            return raynaTicketDetailModel.getTourDetail().getContractId();
        }

        return "";
    }

    // --------------------------------------
    // region Whosin Ticket Method
    // --------------------------------------



    // endregion
    // --------------------------------------
}
