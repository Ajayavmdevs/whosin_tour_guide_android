package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

public class YachtFeatureModel implements DiffIdentifier,ModelProtocol {
    @SerializedName("icon")
    @Expose
    private String icon = "";
    @SerializedName("feature")
    @Expose
    private String feature = "";
    @SerializedName("_id")
    @Expose
    private String id ="";
    @SerializedName("emoji")
    @Expose
    private String emoji = "";

    public String getIcon() {
        return Utils.notNullString( icon.trim() );
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getFeature() {
        return Utils.notNullString( feature.trim());
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
        return true;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
