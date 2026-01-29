package com.whosin.business.service.models.BigBusModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

import java.util.List;

public class OctaTourAvailabilityModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("id")
    @Expose
    private String id = "";

    @SerializedName("localDateTimeStart")
    @Expose
    private String localDateTimeStart = "";

    @SerializedName("localDateTimeEnd")
    @Expose
    private String localDateTimeEnd = "";

    @SerializedName("paxCount")
    @Expose
    private int paxCount = 0;

    @SerializedName("limitPaxCount")
    @Expose
    private int limitPaxCount = 0;

    @SerializedName("totalPaxCount")
    @Expose
    private int totalPaxCount = 0;

    @SerializedName("totalNoShows")
    @Expose
    private int totalNoShows = 0;

    @SerializedName("noShows")
    @Expose
    private int noShows = 0;

    @SerializedName("openingHours")
    @Expose
    private List<BigBusOpeningHoursModel> openingHours;

    @SerializedName("pickupAvailable")
    @Expose
    private Boolean pickupAvailable = false;

    @SerializedName("pickupRequired")
    @Expose
    private Boolean pickupRequired = false;

    @SerializedName("unitPricing")
    @Expose
    private List<BigBusPricingFromItemModel> unitPricing;


    public List<BigBusPricingFromItemModel> getUnitPricing() {
        return Utils.notEmptyList(unitPricing);
    }

    public void setUnitPricing(List<BigBusPricingFromItemModel> unitPricing) {
        this.unitPricing = unitPricing;
    }

    public String getId() {
        return Utils.notNullString(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocalDateTimeStart() {
        return Utils.notNullString(localDateTimeStart);
    }

    public void setLocalDateTimeStart(String localDateTimeStart) {
        this.localDateTimeStart = localDateTimeStart;
    }

    public String getLocalDateTimeEnd() {
        return Utils.notNullString(localDateTimeEnd);
    }

    public void setLocalDateTimeEnd(String localDateTimeEnd) {
        this.localDateTimeEnd = localDateTimeEnd;
    }

    public int getPaxCount() {
        return paxCount;
    }

    public void setPaxCount(int paxCount) {
        this.paxCount = paxCount;
    }

    public int getLimitPaxCount() {
        return limitPaxCount;
    }

    public void setLimitPaxCount(int limitPaxCount) {
        this.limitPaxCount = limitPaxCount;
    }

    public int getTotalPaxCount() {
        return totalPaxCount;
    }

    public void setTotalPaxCount(int totalPaxCount) {
        this.totalPaxCount = totalPaxCount;
    }

    public int getTotalNoShows() {
        return totalNoShows;
    }

    public void setTotalNoShows(int totalNoShows) {
        this.totalNoShows = totalNoShows;
    }

    public int getNoShows() {
        return noShows;
    }

    public void setNoShows(int noShows) {
        this.noShows = noShows;
    }

    public List<BigBusOpeningHoursModel> getOpeningHours() {
        return Utils.notEmptyList(openingHours);
    }

    public void setOpeningHours(List<BigBusOpeningHoursModel> openingHours) {
        this.openingHours = openingHours;
    }

    public Boolean getPickupAvailable() {
        return pickupAvailable;
    }

    public void setPickupAvailable(Boolean pickupAvailable) {
        this.pickupAvailable = pickupAvailable;
    }

    public Boolean getPickupRequired() {
        return pickupRequired;
    }

    public void setPickupRequired(Boolean pickupRequired) {
        this.pickupRequired = pickupRequired;
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
