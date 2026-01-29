package com.whosin.business.service.models.TravelDeskModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

import java.util.List;

public class TravelDeskSrcSetModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("type")
    @Expose
    private String type = "";

    @SerializedName("sizes")
    @Expose
    private List<TravelDeskSizesModel> sizes;

    public String getType() {
        return Utils.notNullString(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TravelDeskSizesModel> getSizes() {
        return Utils.notEmptyList(sizes);
    }

    public void setSizes(List<TravelDeskSizesModel> sizes) {
        this.sizes = sizes;
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

