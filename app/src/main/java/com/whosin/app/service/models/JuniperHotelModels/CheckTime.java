package com.whosin.app.service.models.JuniperHotelModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class CheckTime implements DiffIdentifier, ModelProtocol {

    @SerializedName("CheckIn")
    @Expose
    public String checkIn = "";

    @SerializedName("CheckOut")
    @Expose
    public String checkOut = "";


    public String getCheckIn() {
        return Utils.notNullString(checkIn);
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return Utils.notNullString(checkOut);
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
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
