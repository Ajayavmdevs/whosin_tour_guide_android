package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

import io.realm.RealmObject;

public class ReplyToModel extends RealmObject implements DiffIdentifier,ModelProtocol{

    @SerializedName("type")
    @Expose
    private String type ;

    @SerializedName("data")
    @Expose
    private String data ;

    @SerializedName("id")
    @Expose
    private String id ;

    public ReplyToModel() {
    }

    public ReplyToModel(String type, String data,String id) {
        this.type = type;
        this.data = data;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
