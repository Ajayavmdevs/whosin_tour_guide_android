package com.whosin.app.service.models.BigBusModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class BigBusAccentFont implements DiffIdentifier, ModelProtocol {

    @SerializedName("normalTtfUrl")
    @Expose
    private String normalTtfUrl = "";

    @SerializedName("italicTtfUrl")
    @Expose
    private String italicTtfUrl = "";

    @SerializedName("name")
    @Expose
    private String name = "";

    @SerializedName("id")
    @Expose
    private String id = "";

    @SerializedName("boldItalicTtfUrl")
    @Expose
    private String boldItalicTtfUrl = "";

    @SerializedName("boldTtfUrl")
    @Expose
    private String boldTtfUrl = "";

    public String getNormalTtfUrl() {
        return Utils.notNullString(normalTtfUrl);
    }

    public void setNormalTtfUrl(String normalTtfUrl) {
        this.normalTtfUrl = normalTtfUrl;
    }

    public String getItalicTtfUrl() {
        return Utils.notNullString(italicTtfUrl);
    }

    public void setItalicTtfUrl(String italicTtfUrl) {
        this.italicTtfUrl = italicTtfUrl;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return Utils.notNullString(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBoldItalicTtfUrl() {
        return Utils.notNullString(boldItalicTtfUrl);
    }

    public void setBoldItalicTtfUrl(String boldItalicTtfUrl) {
        this.boldItalicTtfUrl = boldItalicTtfUrl;
    }

    public String getBoldTtfUrl() {
        return Utils.notNullString(boldTtfUrl);
    }

    public void setBoldTtfUrl(String boldTtfUrl) {
        this.boldTtfUrl = boldTtfUrl;
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