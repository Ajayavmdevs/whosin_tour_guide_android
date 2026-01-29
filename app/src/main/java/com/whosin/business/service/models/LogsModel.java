package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

import io.realm.RealmObject;

public class LogsModel extends RealmObject implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("type")
    @Expose
    private String type = "";
    @SerializedName("subType")
    @Expose
    private String subType = "";
    @SerializedName("typeId")
    @Expose
    private String typeId;
    @SerializedName("dateTime")
    @Expose
    private String dateTime = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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
