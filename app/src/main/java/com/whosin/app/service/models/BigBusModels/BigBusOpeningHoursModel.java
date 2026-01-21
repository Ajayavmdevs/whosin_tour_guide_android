package com.whosin.app.service.models.BigBusModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class BigBusOpeningHoursModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("from")
    @Expose
    private String from = "";

    @SerializedName("to")
    @Expose
    private String to = "";


    public String getFrom() {
        return Utils.notNullString(from);
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return Utils.notNullString(to);
    }

    public void setTo(String to) {
        this.to = to;
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
