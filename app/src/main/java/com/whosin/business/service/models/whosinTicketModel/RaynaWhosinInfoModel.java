package com.whosin.business.service.models.whosinTicketModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.service.models.ModelProtocol;

public class RaynaWhosinInfoModel  implements DiffIdentifier, ModelProtocol {

    @SerializedName("key")
    @Expose
    private String key = "";

    @SerializedName("value")
    @Expose
    private Object value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
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
