package com.whosin.business.service.models.JuniperHotelModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class JpHotelServiceTaxes implements DiffIdentifier, ModelProtocol {

    @SerializedName("Included")
    @Expose
    public String included = "";

    @SerializedName("Amount")
    @Expose
    public String amount = "";

    public String getIncluded() {
        return Utils.notNullString(included);
    }

    public void setIncluded(String included) {
        this.included = included;
    }

    public String getAmount() {
        return Utils.notNullString(amount);
    }

    public void setAmount(String amount) {
        this.amount = amount;
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
