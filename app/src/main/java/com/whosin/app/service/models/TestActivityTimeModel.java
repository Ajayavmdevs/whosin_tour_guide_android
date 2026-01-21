package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.List;

public class TestActivityTimeModel implements DiffIdentifier, ModelProtocol{

    @SerializedName("type")
    @Expose
    private String type = "";
    @SerializedName("slot")
    @Expose
    private List<ActivitySlotModel> slot;
    @SerializedName("start")
    @Expose
    private String start = "";
    @SerializedName("end")
    @Expose
    private String end ="";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ActivitySlotModel> getSlot() {
        return slot;
    }

    public void setSlot(List<ActivitySlotModel> slot) {
        this.slot = slot;
    }

    public Object getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end ;
    }

    public void setEnd(String end) {
        this.end = end;
    }
    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return false;
    }
}
