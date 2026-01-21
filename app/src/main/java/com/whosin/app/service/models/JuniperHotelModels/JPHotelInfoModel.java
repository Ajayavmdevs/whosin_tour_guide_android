package com.whosin.app.service.models.JuniperHotelModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

import java.util.List;

public class JPHotelInfoModel implements DiffIdentifier, ModelProtocol {


    @SerializedName("Code")
    @Expose
    public String code = "";

    @SerializedName("JPCode")
    @Expose
    public String jPCode = "";

    @SerializedName("JPDCode")
    @Expose
    public String jPDCode = "";

    @SerializedName("BestDeal")
    @Expose
    public String bestDeal = "";

    @SerializedName("Type")
    @Expose
    public String type = "";

    @SerializedName("DestinationZone")
    @Expose
    public String destinationZone = "";

    @SerializedName("Name")
    @Expose
    public String name = "";

    @SerializedName("Description")
    @Expose
    public String description = "";

    @SerializedName("HotelChain")
    @Expose
    public String hotelChain = "";

    @SerializedName("Latitude")
    @Expose
    public String latitude = "";

    @SerializedName("Longitude")
    @Expose
    public String longitude = "";

    @SerializedName("Address")
    @Expose
    public String address = "";

    @SerializedName("HotelCategory")
    @Expose
    public String hotelCategory = "";

    @SerializedName("HotelCategoryType")
    @Expose
    public String hotelCategoryType = "";

    @SerializedName("HotelType")
    @Expose
    public String hotelType = "";

    @SerializedName("Label")
    @Expose
    public List<String> label;

    @SerializedName("Images")
    @Expose
    public List<String> images;

    @SerializedName("CheckTime")
    @Expose
    public CheckTime checkTime;


    public String getCode() {
        return Utils.notNullString(code);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getjPCode() {
        return Utils.notNullString(jPCode);
    }

    public void setjPCode(String jPCode) {
        this.jPCode = jPCode;
    }

    public String getjPDCode() {
        return Utils.notNullString(jPDCode);
    }

    public void setjPDCode(String jPDCode) {
        this.jPDCode = jPDCode;
    }

    public String getBestDeal() {
        return Utils.notNullString(bestDeal);
    }

    public void setBestDeal(String bestDeal) {
        this.bestDeal = bestDeal;
    }

    public String getType() {
        return Utils.notNullString(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDestinationZone() {
        return Utils.notNullString(destinationZone);
    }

    public void setDestinationZone(String destinationZone) {
        this.destinationZone = destinationZone;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return Utils.notNullString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHotelChain() {
        return Utils.notNullString(hotelChain);
    }

    public void setHotelChain(String hotelChain) {
        this.hotelChain = hotelChain;
    }

    public String getLatitude() {
        return Utils.notNullString(latitude);
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return Utils.notNullString(longitude);
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return Utils.notNullString(address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHotelCategory() {
        return Utils.notNullString(hotelCategory);
    }

    public void setHotelCategory(String hotelCategory) {
        this.hotelCategory = hotelCategory;
    }

    public String getHotelCategoryType() {
        return Utils.notNullString(hotelCategoryType);
    }

    public void setHotelCategoryType(String hotelCategoryType) {
        this.hotelCategoryType = hotelCategoryType;
    }

    public String getHotelType() {
        return Utils.notNullString(hotelType);
    }

    public void setHotelType(String hotelType) {
        this.hotelType = hotelType;
    }

    public List<String> getLabel() {
        return Utils.notEmptyList(label);
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }

    public List<String> getImages() {
        return Utils.notEmptyList(images);
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public CheckTime getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(CheckTime checkTime) {
        this.checkTime = checkTime;
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
