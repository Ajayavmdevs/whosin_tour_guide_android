package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class PromoterVenuesModel implements DiffIdentifier, ModelProtocol {


    @SerializedName("count")
    @Expose
    private int count = 0;

    @SerializedName("list")
    @Expose
    private List<VenueObjectModel> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<VenueObjectModel> getList() {
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    public void setList(List<VenueObjectModel> list) {
        this.list = list;
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
