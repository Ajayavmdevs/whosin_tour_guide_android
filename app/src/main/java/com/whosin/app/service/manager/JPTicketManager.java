package com.whosin.app.service.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.JuniperHotelModels.CheckTime;
import com.whosin.app.service.models.JuniperHotelModels.HotelRequestModel;
import com.whosin.app.service.models.JuniperHotelModels.JPHotelPolicyRuleModel;
import com.whosin.app.service.models.JuniperHotelModels.JPPassengerModel;
import com.whosin.app.service.models.JuniperHotelModels.JpHotelBookingRuleModel;
import com.whosin.app.service.models.JuniperHotelModels.JpHotelPriceModel;
import com.whosin.app.service.models.JuniperHotelModels.PaxModel;
import com.whosin.app.service.models.JuniperHotelModels.PaxesItemModel;
import com.whosin.app.service.models.VenuePromoCodeModel;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class JPTicketManager {
    @SuppressLint("StaticFieldLeak")
    @NonNull
    public static JPTicketManager shared = JPTicketManager.getInstance();

    @SuppressLint("StaticFieldLeak")
    private static JPTicketManager instance = null;

    private Context context;

    public List<Activity> activityList = new ArrayList<>();

    public HotelRequestModel hotelRequestModel = new HotelRequestModel();

    public JpHotelBookingRuleModel jpHotelBookingRuleModel = null;

    public List<JPPassengerModel> guestList = new ArrayList<>();

    public CheckTime checkTime = null;

    public JpHotelPriceModel priceModel = null;

    public String nonRefundable = "";


    // --------------------------------------
    // region Singleton
    // --------------------------------------

    @androidx.annotation.NonNull
    private static synchronized JPTicketManager getInstance() {
        if (instance == null) {
            instance = new JPTicketManager();
        }
        return instance;
    }


    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public void clearManager() {
        hotelRequestModel = new HotelRequestModel();
        jpHotelBookingRuleModel = null;
        guestList = new ArrayList<>();
        activityList = new ArrayList<>();
        checkTime = null;
        priceModel = null;
        nonRefundable = "";
    }


    // Finish all activities
    public void finishAllActivities() {
        for (int i = 0; i < activityList.size(); i++) {
            if (!activityList.get(i).isFinishing()) {
                activityList.get(i).finish();
            }
        }

    }

    public JsonObject getHotelRequestList() {
        if (hotelRequestModel == null) return new JsonObject();

        int globalId = 1;

        // Create a deep copy list only for JSON export
        List<PaxesItemModel> exportList = new ArrayList<>();

        for (PaxesItemModel sourceItem : hotelRequestModel.getPaxes()) {

            PaxesItemModel exportItem = new PaxesItemModel();
            exportItem.setAdultCount(sourceItem.getAdultCount());
            exportItem.setChildCount(sourceItem.getChildCount());


            List<PaxModel> newPaxList = new ArrayList<>();

            // 1️⃣ Add adults
            for (int i = 0; i < sourceItem.getAdultCount(); i++) {
                PaxModel adult = new PaxModel();
                adult.setId(globalId++);
                adult.setAge("20");
                newPaxList.add(adult);
            }

            // 2️⃣ Add children (copied, with cleaned age)
            if (sourceItem.getPax() != null) {
                for (PaxModel child : sourceItem.getPax()) {
                    PaxModel newChild = new PaxModel();
                    newChild.setId(globalId++);
                    String ageStr = child.getAge();
                    if (ageStr != null && !ageStr.isEmpty()) {
                        String numericAge = ageStr.replaceAll("\\D+", "");
                        newChild.setAge(numericAge);
                    }
                    newPaxList.add(newChild);
                }
            }

            exportItem.setPax(newPaxList);
            exportList.add(exportItem);
        }

        // Now convert exportList → JsonObject
        Gson gson = new Gson();
        JsonObject jsonObject = gson.toJsonTree(hotelRequestModel).getAsJsonObject();
        jsonObject.add("paxes", gson.toJsonTree(exportList).getAsJsonArray());

        // Remove unwanted fields
        JsonArray paxesArray = jsonObject.getAsJsonArray("paxes");
        for (int i = 0; i < paxesArray.size(); i++) {
            JsonObject paxObj = paxesArray.get(i).getAsJsonObject();
            paxObj.remove("adultCount");
            paxObj.remove("childCount");
            paxObj.remove("roomId");
        }

        return jsonObject;
    }


    public JsonObject getBookingObject(boolean isPromoCodeApply, VenuePromoCodeModel promoCodeModel) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("bookingType","juniper-hotel");
        jsonObject.addProperty("currency","AED");
        jsonObject.addProperty("comment","");
        jsonObject.addProperty("customTicketId",RaynaTicketManager.shared.raynaTicketDetailModel.getId());


        JsonArray tourDetails = new JsonArray();

        JsonObject tourDetailObject = new JsonObject();
        tourDetailObject.addProperty("optionId","");
        if (!guestList.isEmpty()){
            long adultCount = guestList.stream().filter(p -> "adult".equalsIgnoreCase(p.getPaxType())).count();
            long childCount = guestList.stream().filter(p -> "child".equalsIgnoreCase(p.getPaxType())).count();
            tourDetailObject.addProperty("adult",adultCount);
            tourDetailObject.addProperty("child",childCount);
            tourDetailObject.addProperty("infant",0);
        }



        if (!guestList.isEmpty()){
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            JsonElement passengersJson = gson.toJsonTree(guestList);
            jsonObject.add("passengers", passengersJson);
        }

        if (jpHotelBookingRuleModel != null && jpHotelBookingRuleModel.getHotelBookingRequiredFields() != null){

            tourDetailObject.addProperty("tourId",jpHotelBookingRuleModel.getHotelBookingRequiredFields().getHotelCode());
            tourDetailObject.addProperty("startDate",jpHotelBookingRuleModel.getHotelBookingRequiredFields().getStartDate());
            tourDetailObject.addProperty("endDate",jpHotelBookingRuleModel.getHotelBookingRequiredFields().getEndDate());
            tourDetailObject.addProperty("startTime",checkTime.checkIn);
            tourDetailObject.addProperty("serviceTotal",priceModel.getNett());
            tourDetailObject.addProperty("whosinTotal",priceModel.getNett());


            jsonObject.addProperty("bookingCode",jpHotelBookingRuleModel.getBookingCode());

            Gson gson = new Gson();
            JsonElement priceRangeJson = gson.toJsonTree(jpHotelBookingRuleModel.getHotelBookingRequiredFields().getPriceRange());
            jsonObject.add("priceRange", priceRangeJson);

            JsonElement relPaxesDistJson = gson.toJsonTree(jpHotelBookingRuleModel.getHotelBookingRequiredFields().getRelPaxesDist());
            jsonObject.add("relPaxesDist", relPaxesDistJson);

            JsonElement passengersJson = gson.toJsonTree(jpHotelBookingRuleModel.getCancellationPolicy().getPolicyRules());
            jsonObject.add("cancellationPolicy", passengersJson);
        }

        if (isPromoCodeApply && promoCodeModel != null) {
            jsonObject.addProperty("amount", Utils.roundDoubleValueToDouble(promoCodeModel.getMetaData().get(0).getFinalAmount()));
            jsonObject.add("promoCodeData",new Gson().toJsonTree(promoCodeModel.getMetaData()).getAsJsonArray());
        } else {
            jsonObject.addProperty("amount",priceModel.getNett());
        }


        jsonObject.addProperty("totalAmount",priceModel.getNett());

        tourDetails.add(tourDetailObject);
        jsonObject.add("TourDetails", tourDetails);

        return jsonObject;
    }



    // endregion
    // --------------------------------------
    // region Data/Services
    // --------------------------------------




    // endregion
    // --------------------------------------
}
