package com.whosin.app.service.models.whosinTicketModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.service.models.ModelProtocol;

public class WhosinAvailabilityModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("available")
    @Expose
    private boolean available = false;


    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
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
