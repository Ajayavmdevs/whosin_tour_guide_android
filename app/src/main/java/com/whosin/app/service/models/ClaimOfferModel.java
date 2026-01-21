package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

public class ClaimOfferModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("response")
    @Expose
    private ClaimSpecialOfferModel response;
    @SerializedName("objToSend")
    @Expose
    private PaymentCredentialModel objToSend;

    @SerializedName("tabby")
    @Expose
    private PaymentTabbyModel tabbyModel;

    public ClaimSpecialOfferModel getResponse() {
        return response;
    }

    public void setResponse(ClaimSpecialOfferModel response) {
        this.response = response;
    }

    public PaymentCredentialModel getObjToSend() {
        return objToSend;
    }

    public void setObjToSend(PaymentCredentialModel objToSend) {
        this.objToSend = objToSend;
    }

    public PaymentTabbyModel getTabbyModel() {
        return tabbyModel;
    }

    public void setTabbyModel(PaymentTabbyModel tabbyModel) {
        this.tabbyModel = tabbyModel;
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
