package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

public class ActivityTypeModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("title")
    @Expose
    private String title = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
