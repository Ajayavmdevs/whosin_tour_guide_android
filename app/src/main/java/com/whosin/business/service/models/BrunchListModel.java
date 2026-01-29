package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

import java.util.ArrayList;
import java.util.List;

public class BrunchListModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("dimension")
    @Expose
    private String dimension;
    @SerializedName("days")
    @Expose
    private String days;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("endTime")
    @Expose
    private String endTime;
    @SerializedName("allowWhosIn")
    @Expose
    private Boolean allowWhosIn;
    @SerializedName("packages")
    @Expose
    private List<BrunchPackageModel> packages;

    @SerializedName("venueId")
    @Expose
    private String venueId;

    private boolean isChecked;
    public boolean isChecked(){
        return isChecked;}

    public void setIsChecked(boolean checked){
        isChecked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Boolean getAllowWhosIn() {
        return allowWhosIn;
    }

    public void setAllowWhosIn(Boolean allowWhosIn) {
        this.allowWhosIn = allowWhosIn;
    }

    public List<BrunchPackageModel> getPackages() {
        return packages == null ? new ArrayList<>() : packages;
    }

    public void setPackages(List<BrunchPackageModel> packages) {
        this.packages = packages;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public String getVenueId() {
        return Utils.notNullString(venueId);
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }
}
