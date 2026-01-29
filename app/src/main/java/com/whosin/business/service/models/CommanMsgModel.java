package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

public class CommanMsgModel implements DiffIdentifier,ModelProtocol {


    @SerializedName("status")
    @Expose
    public int status = 0;

    @SerializedName("message")
    @Expose
    public String message = "";
    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public CommanMsgModel(String message) {
        this.message = message;
    }
    public CommanMsgModel() {}
}