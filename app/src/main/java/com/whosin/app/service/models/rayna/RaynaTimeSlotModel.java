package com.whosin.app.service.models.rayna;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class RaynaTimeSlotModel implements DiffIdentifier, ModelProtocol {


    @SerializedName("tourOptionId")
    @Expose
    private int tourOptionId = 0;

    @SerializedName("timeSlotId")
    @Expose
    private String timeSlotId = "";

    @SerializedName("timeSlot")
    @Expose
    private String timeSlot = "";

    @SerializedName("slotId")
    @Expose
    private String slotId = "";

    @SerializedName("available")
    @Expose
    private int available = 0;

    @SerializedName("adultPrice")
    @Expose
    private double adultPrice = 0;

    @SerializedName("childPrice")
    @Expose
    private double childPrice = 0;

    @SerializedName("isDynamicPrice")
    @Expose
    private boolean isDynamicPrice = false;

    @SerializedName("availabilityTime")
    @Expose
    private String availabilityTime = "";

    @SerializedName("totalSeats")
    @Expose
    private int totalSeats = 0;

    @SerializedName("_id")
    @Expose
    private String id = "";

    public RaynaTimeSlotModel() {
    }

    public RaynaTimeSlotModel(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public RaynaTimeSlotModel(int timeSlotId,String timeSlot) {
        this.timeSlot = timeSlot;
        this.timeSlotId = String.valueOf(timeSlotId);
    }

    public RaynaTimeSlotModel(String timeSlot, int available ) {
        this.timeSlot = timeSlot;
        this.available = available;
    }

    public RaynaTimeSlotModel(String _id , String slot){
        this.id = _id;
        this.availabilityTime = slot;
    }

    public RaynaTimeSlotModel(String _id , String slot,boolean isSlot){
        this.slotId = _id;
        this.timeSlot = slot;
    }


    public String getAvailabilityTime() {
        return availabilityTime;
    }

    public void setAvailabilityTime(String availabilityTime) {
        this.availabilityTime = availabilityTime;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTourOptionId() {
        return tourOptionId;
    }

    public void setTourOptionId(int tourOptionId) {
        this.tourOptionId = tourOptionId;
    }

    public String getTimeSlotId() {
        return Utils.notNullString(timeSlotId);
    }

    public void setTimeSlotId(String timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public String getTimeSlot() {
        return Utils.notNullString(timeSlot);
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public double getAdultPrice() {
        return adultPrice;
    }

    public void setAdultPrice(double adultPrice) {
        this.adultPrice = adultPrice;
    }

    public double getChildPrice() {
        return childPrice;
    }

    public void setChildPrice(double childPrice) {
        this.childPrice = childPrice;
    }

    public boolean isDynamicPrice() {
        return isDynamicPrice;
    }

    public void setDynamicPrice(boolean dynamicPrice) {
        isDynamicPrice = dynamicPrice;
    }

    public String getSlotId() {
        return Utils.notNullString(slotId);
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
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
