package com.whosin.business.service.models.BigBusModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class BigBusItineraryItemModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("travelTime")
    @Expose
    private String travelTime = "";

    @SerializedName("address")
    @Expose
    private String address = "";

    @SerializedName("googlePlaceId")
    @Expose
    private String googlePlaceId = "";

    @SerializedName("latitude")
    @Expose
    private float latitude;

    @SerializedName("description")
    @Expose
    private String description = "";

    @SerializedName("type")
    @Expose
    private String type = "";

    @SerializedName("duration")
    @Expose
    private String duration = "";

    @SerializedName("travelTimeAmount")
    @Expose
    private String travelTimeAmount = "";

    @SerializedName("name")
    @Expose
    private String name = "";

    @SerializedName("travelTimeUnit")
    @Expose
    private String travelTimeUnit = "";

    @SerializedName("durationUnit")
    @Expose
    private String durationUnit = "";

    @SerializedName("durationAmount")
    @Expose
    private int durationAmount;

    @SerializedName("longitude")
    @Expose
    private float longitude;

    public String getTravelTime() {
        return Utils.notNullString(travelTime);
    }

    public void setTravelTime(String travelTime) {
        this.travelTime = travelTime;
    }

    public String getAddress() {
        return Utils.notNullString(address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGooglePlaceId() {
        return Utils.notNullString(googlePlaceId);
    }

    public void setGooglePlaceId(String googlePlaceId) {
        this.googlePlaceId = googlePlaceId;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getDescription() {
        return Utils.notNullString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return Utils.notNullString(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuration() {
        return Utils.notNullString(duration);
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTravelTimeAmount() {
        return Utils.notNullString(travelTimeAmount);
    }

    public void setTravelTimeAmount(String travelTimeAmount) {
        this.travelTimeAmount = travelTimeAmount;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTravelTimeUnit() {
        return Utils.notNullString(travelTimeUnit);
    }

    public void setTravelTimeUnit(String travelTimeUnit) {
        this.travelTimeUnit = travelTimeUnit;
    }

    public String getDurationUnit() {
        return Utils.notNullString(durationUnit);
    }

    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }

    public int getDurationAmount() {
        return durationAmount;
    }

    public void setDurationAmount(int durationAmount) {
        this.durationAmount = durationAmount;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
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