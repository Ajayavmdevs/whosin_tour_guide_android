package com.whosin.app.service.models;

import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.List;

public class YachtAvailableSlotsModel implements DiffIdentifier,ModelProtocol{

    @SerializedName("date")
    private String date;

    @SerializedName("slots")
    private List<ActivitySlotModel> slotList;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ActivitySlotModel> getSlotList() {
        return slotList;
    }

    public void setSlotList(List<ActivitySlotModel> slotList) {
        this.slotList = slotList;
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
