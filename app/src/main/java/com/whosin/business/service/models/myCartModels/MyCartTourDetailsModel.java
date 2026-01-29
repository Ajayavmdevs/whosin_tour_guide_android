package com.whosin.business.service.models.myCartModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

import java.util.List;

public class MyCartTourDetailsModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("tourId")
    @Expose
    private int tourId = 0;

    @SerializedName("optionId")
    @Expose
    private String optionId = "";

    @SerializedName("adult")
    @Expose
    private int adult = 0;

    @SerializedName("child")
    @Expose
    private int child = 0;

    @SerializedName("infant")
    @Expose
    private int infant = 0;

    @SerializedName("tourDate")
    @Expose
    private String tourDate = "";

    @SerializedName("timeSlot")
    @Expose
    private String timeSlot = "";

    @SerializedName("timeSlotId")
    @Expose
    private Object timeSlotId = "";

    @SerializedName("startTime")
    @Expose
    private String startTime = "";

    @SerializedName("endTime")
    @Expose
    private String endTime = "";

    @SerializedName("transferId")
    @Expose
    private int transferId = 0;

    @SerializedName("pickup")
    @Expose
    private String pickup = "";

    @SerializedName("adultRate")
    @Expose
    private float adultRate;

    @SerializedName("childRate")
    @Expose
    private float childRate;

    @SerializedName("serviceTotal")
    @Expose
    private float serviceTotal;

    @SerializedName("whosinTotal")
    @Expose
    private float whosinTotal;

    @SerializedName("_id")
    @Expose
    private String _id = "";

    @SerializedName("hotelId")
    @Expose
    private int hotelId ;


    @SerializedName("adult_title")
    @Expose
    public String adultTitle = "";
    @SerializedName("child_title")
    @Expose
    public String childTitle = "";
    @SerializedName("infant_title")
    @Expose
    public String infantTitle = "";
    @SerializedName("adult_description")
    @Expose
    public String adultDescription = "";
    @SerializedName("child_description")
    @Expose
    public String childDescription = "";
    @SerializedName("infant_description")
    @Expose
    public String infantDescription = "";
    @SerializedName("Addons")
    @Expose
    private List<MyCartTourDetailsModel> addons;
    @SerializedName("addOnTitle")
    @Expose
    public String addOnTitle = "";
    @SerializedName("addOndesc")
    @Expose
    public String addOndesc = "";
    @SerializedName("addOnImage")
    @Expose
    public String addOnImage = "";

    public List<MyCartTourDetailsModel> getAddons() {
        return addons;
    }

    public void setAddons(List<MyCartTourDetailsModel> addons) {
        this.addons = addons;
    }

    public String getAddOnTitle() {
        return addOnTitle;
    }

    public void setAddOnTitle(String addOnTitle) {
        this.addOnTitle = addOnTitle;
    }

    public String getAddOndesc() {
        return addOndesc;
    }
    public String getAddOnImage() {
        return addOnImage;
    }

    public void setAddOndesc(String addOndesc) {
        this.addOndesc = addOndesc;
    }

    public String getAdultTitle() {
        return Utils.isNullOrEmpty(adultTitle) ? "Adult" : adultTitle;
    }
    public String getChildTitle() {
        return Utils.isNullOrEmpty(childTitle) ? "Child" : childTitle;
    }
    public String getInfantTitle() {
        return Utils.isNullOrEmpty(infantTitle) ? "Infant" : infantTitle;
    }

    public String getAdultDescription() {
        return Utils.notNullString(adultDescription);
    }
    public String getChildDescription() {
        return Utils.notNullString(childDescription);
    }
    public String getInfantDescription() {
        return Utils.notNullString(infantDescription);
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getTimeSlot() {
        return Utils.notNullString(timeSlot);
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public String getOptionId() {
        return Utils.notNullString(optionId);
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public int getAdult() {
        return adult;
    }

    public void setAdult(int adult) {
        this.adult = adult;
    }

    public int getChild() {
        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }

    public int getInfant() {
        return infant;
    }

    public void setInfant(int infant) {
        this.infant = infant;
    }

    public String getTourDate() {
        return Utils.notNullString(tourDate);
    }

    public void setTourDate(String tourDate) {
        this.tourDate = tourDate;
    }

    public Object getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(int timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public String getStartTime() {
        return Utils.notNullString(startTime);
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return Utils.notNullString(endTime);
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public String getPickup() {
        return Utils.notNullString(pickup);
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public float getAdultRate() {
        return adultRate;
    }

    public void setAdultRate(float adultRate) {
        this.adultRate = adultRate;
    }

    public float getChildRate() {
        return childRate;
    }

    public void setChildRate(float childRate) {
        this.childRate = childRate;
    }

    public float getServiceTotal() {
        return serviceTotal;
    }

    public void setServiceTotal(float serviceTotal) {
        this.serviceTotal = serviceTotal;
    }

    public float getWhosinTotal() {
        return whosinTotal;
    }

    public void setWhosinTotal(float whosinTotal) {
        this.whosinTotal = whosinTotal;
    }

    public String get_id() {
        return Utils.notNullString(_id);
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }
}
