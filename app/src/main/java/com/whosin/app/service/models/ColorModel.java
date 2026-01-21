package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

public class ColorModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("startColor")
    @Expose
    private String startColor="";
    @SerializedName("endColor")
    @Expose
    private String endColor="";

    public String getStartColor() {
        return startColor;
    }
    public void setStartColor(String startColor) {
        this.startColor = startColor;
    }

    public String getEndColor() {
        return endColor;
    }

    public void setEndColor(String endColor) {
        this.endColor = endColor;
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
