package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

public class ActivityAvailableFeatureModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("icon")
    @Expose
    private String icon="";
    @SerializedName("feature")
    @Expose
    private String feature="";
    @SerializedName("_id")
    @Expose
    private String id="";
    @SerializedName("title")
    @Expose
    private String title="";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
