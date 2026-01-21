package com.whosin.app.service.models.TravelDeskModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class TravelDeskPickUpListModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("name")
    @Expose
    private String name = "";

    @SerializedName("regionId")
    @Expose
    private int regionId;

    @SerializedName("regionName")
    @Expose
    private String regionName = "";

    @SerializedName("cityId")
    @Expose
    private int cityId;

    @SerializedName("cityName")
    @Expose
    private String cityName = "";

    @SerializedName("id")
    @Expose
    private int id;

    public TravelDeskPickUpListModel(){}

    public TravelDeskPickUpListModel(int id,String name){
        this.id = id;
        this.name = name;
    }


    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return Utils.notNullString(regionName);
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return Utils.notNullString(cityName);
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
