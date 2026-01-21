package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class PromoterRingsModel  implements DiffIdentifier, ModelProtocol{

    @SerializedName("count")
    @Expose
    private int count = 0;

    @SerializedName("maleCount")
    @Expose
    private int maleCount = 0;

    @SerializedName("femaleCount")
    @Expose
    private int femaleCount = 0;

    @SerializedName("preferNotToSay")
    @Expose
    private int preferNotToSay = 0;

    @SerializedName("list")
    @Expose
    private List<UserDetailModel> list ;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMaleCount() {
        return maleCount;
    }

    public void setMaleCount(int maleCount) {
        this.maleCount = maleCount;
    }

    public int getFemaleCount() {
        return femaleCount;
    }

    public void setFemaleCount(int femaleCount) {
        this.femaleCount = femaleCount;
    }

    public int getPreferNotToSay() {
        return preferNotToSay;
    }

    public void setPreferNotToSay(int preferNotToSay) {
        this.preferNotToSay = preferNotToSay;
    }

    public List<UserDetailModel> getList() {
        if (list == null){
            return new ArrayList<>();
        }
        return list;
    }

    public void setList(List<UserDetailModel> list) {
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
