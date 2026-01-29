package com.whosin.business.service.models.JuniperHotelModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class JpHotelPriceModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("Currency")
    @Expose
    public String currency = "";

    @SerializedName("Recommended")
    @Expose
    public Object recommended;

    @SerializedName("Gross")
    @Expose
    public String gross = "";

    @SerializedName("Nett")
    @Expose
    public String nett = "";

    @SerializedName("Amount")
    @Expose
    public String amount = "";

    @SerializedName("ServiceTaxes")
    @Expose
    public JpHotelServiceTaxes serviceTaxes;

    public String getCurrency() {
        return Utils.notNullString(currency);
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Object getRecommended() {
        return recommended;
    }

    public void setRecommended(Object recommended) {
        this.recommended = recommended;
    }

    public String getGross() {
        return Utils.notNullString(gross);
    }

    public void setGross(String gross) {
        this.gross = gross;
    }

    public String getNett() {
        return Utils.notNullString(nett);
    }

    public void setNett(String nett) {
        this.nett = nett;
    }

    public String getAmount() {
        return Utils.notNullString(amount);
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public JpHotelServiceTaxes getServiceTaxes() {
        return serviceTaxes;
    }

    public void setServiceTaxes(JpHotelServiceTaxes serviceTaxes) {
        this.serviceTaxes = serviceTaxes;
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
