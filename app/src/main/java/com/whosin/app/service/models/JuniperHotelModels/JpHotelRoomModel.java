package com.whosin.app.service.models.JuniperHotelModels;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

import java.util.List;

public class JpHotelRoomModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("Source")
    @Expose
    public String source = "";

    @SerializedName("Units")
    @Expose
    public String units = "";

    @SerializedName("AvailRooms")
    @Expose
    public String availRooms = "";

    @SerializedName("Name")
    @Expose
    public String name = "";

    @SerializedName("RoomCategory")
    @Expose
    public String roomCategory = "";

    @SerializedName("Type")
    @Expose
    public String type = "";

    @SerializedName("RoomOccupancy")
    @Expose
    public JPHotelRoomOccupancy roomOccupancy;

    @SerializedName("name")
    @Expose
    public String smallName = "";

    @SerializedName("category")
    @Expose
    public String category = "";

    @SerializedName("categoryType")
    @Expose
    public String categoryType = "";

    @SerializedName("features")
    @Expose
    public List<String> features;


    public String getSource() {
        return Utils.notNullString(source);
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUnits() {
        return Utils.notNullString(units);
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getAvailRooms() {
        return Utils.notNullString(availRooms);
    }

    public void setAvailRooms(String availRooms) {
        this.availRooms = availRooms;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomCategory() {
        return Utils.notNullString(roomCategory);
    }

    public void setRoomCategory(String roomCategory) {
        this.roomCategory = roomCategory;
    }

    public String getType() {
        return Utils.notNullString(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public JPHotelRoomOccupancy getRoomOccupancy() {
        return roomOccupancy;
    }

    public void setRoomOccupancy(JPHotelRoomOccupancy roomOccupancy) {
        this.roomOccupancy = roomOccupancy;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }


    public String getFullName() {
        StringBuilder boardName = new StringBuilder();

        if (!TextUtils.isEmpty(name)) {
            boardName.append(name);
        }

        if (!TextUtils.isEmpty(roomCategory)) {
            boardName.append(" | ").append(roomCategory);
        }

        return boardName.toString();
    }

    public String getFullNameForWallet() {
        StringBuilder boardName = new StringBuilder();

        if (!TextUtils.isEmpty(smallName)) {
            boardName.append(smallName);
        }

        if (!TextUtils.isEmpty(category)) {
            boardName.append(" | ").append(category);
        }

        return boardName.toString();
    }

    public String getPaxCount() {

        StringBuilder paxCount = new StringBuilder();

        if (roomOccupancy != null && !TextUtils.isEmpty(roomOccupancy.getAdults()) && !roomOccupancy.getAdults().equals("0")) {
            paxCount.append(Utils.getLangValue("adults_title")).append(" : ").append(roomOccupancy.getAdults());
        }

        if (roomOccupancy != null && !TextUtils.isEmpty(roomOccupancy.getChildren()) && !roomOccupancy.getChildren().equals("0")) {
            paxCount.append(" | ").append(Utils.getLangValue("children_title")).append(" : ").append(roomOccupancy.getChildren());
        }

        if (roomOccupancy != null && !TextUtils.isEmpty(roomOccupancy.getOccupancy()) && !roomOccupancy.getOccupancy().equals("0")) {
            paxCount.append(" | ").append(Utils.getLangValue("occupancy")).append(" : ").append(roomOccupancy.getOccupancy());
        }

        return paxCount.toString();
    }

    public String getSmallName() {
        return Utils.notNullString(smallName);
    }

    public void setSmallName(String smallName) {
        this.smallName = smallName;
    }

    public String getCategory() {
        return Utils.notNullString(category);
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryType() {
        return Utils.notNullString(categoryType);
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public List<String> getFeatures() {
        return Utils.notEmptyList(features);
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }
}
