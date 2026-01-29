package com.whosin.business.service.models.JuniperHotelModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class JPHotelRoomOccupancy implements DiffIdentifier, ModelProtocol {

    @SerializedName("Occupancy")
    @Expose
    public String occupancy = "";

    @SerializedName("MaxOccupancy")
    @Expose
    public String maxOccupancy = "";

    @SerializedName("Adults")
    @Expose
    public String adults = "";

    @SerializedName("Children")
    @Expose
    public String children = "";

    public String getOccupancy() {
        return Utils.notNullString(occupancy);
    }

    public void setOccupancy(String occupancy) {
        this.occupancy = occupancy;
    }

    public String getMaxOccupancy() {
        return Utils.notNullString(maxOccupancy);
    }

    public void setMaxOccupancy(String maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    public String getAdults() {
        return Utils.notNullString(adults);
    }

    public void setAdults(String adults) {
        this.adults = adults;
    }

    public String getChildren() {
        return Utils.notNullString(children);
    }

    public void setChildren(String children) {
        this.children = children;
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
