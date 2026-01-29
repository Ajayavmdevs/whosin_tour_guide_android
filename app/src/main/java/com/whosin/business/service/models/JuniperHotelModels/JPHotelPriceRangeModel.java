package com.whosin.business.service.models.JuniperHotelModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class JPHotelPriceRangeModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("Minimum")
    @Expose
    public String minimum = "";
    @SerializedName("Maximum")
    @Expose
    public String maximum = "";
    @SerializedName("Currency")
    @Expose
    public String currency = "";

    public String getMinimum() {
        return Utils.notNullString(minimum);
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }

    public String getMaximum() {
        return Utils.notNullString(maximum);
    }

    public void setMaximum(String maximum) {
        this.maximum = maximum;
    }

    public String getCurrency() {
        return Utils.notNullString(currency);
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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
