package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

import io.realm.RealmObject;

public class BucketItemModel extends RealmObject implements DiffIdentifier,ModelProtocol {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("ref")
    @Expose
    private String ref;
    @SerializedName("_id")
    @Expose
    private String isd;

    @SerializedName("type")
    @Expose
    private String  type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getIds() {
        return id;
    }

    public void setIds(String id) {
        this.id = id;
    }


    public String getIsd() {
        return isd;
    }

    public void setIsd(String isd) {
        this.isd = isd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
