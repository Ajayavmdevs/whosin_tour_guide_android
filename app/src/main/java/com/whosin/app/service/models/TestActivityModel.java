package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.List;

public class TestActivityModel implements ModelProtocol, DiffIdentifier {


    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("galleries")
    @Expose
    private List<String> galleries;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("totalSeats")
    @Expose
    private int totalSeats;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("endDate")
    @Expose
    private String endDate;
    @SerializedName("avilableDays")
    @Expose
    private List<String> avilableDays;
    @SerializedName("activityTime")
    @Expose
    private TestActivityTimeModel activityTime;
    @SerializedName("avilableFeatures")
    @Expose
    private List<ActivityAvailableFeatureModel> avilableFeatures;
    @SerializedName("provider")
    @Expose
    private ActivityProviderModel provider;
    @SerializedName("type")
    @Expose
    private ActivityTypeModel type;
    @SerializedName("avgRating")
    @Expose
    private double avgRating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getGalleries() {
        return galleries;
    }

    public void setGalleries(List<String> galleries) {
        this.galleries = galleries;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getAvilableDays() {
        return avilableDays;
    }

    public void setAvilableDays(List<String> avilableDays) {
        this.avilableDays = avilableDays;
    }

    public TestActivityTimeModel getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(TestActivityTimeModel activityTime) {
        this.activityTime = activityTime;
    }

    public List<ActivityAvailableFeatureModel> getAvilableFeatures() {
        return avilableFeatures;
    }

    public void setAvilableFeatures(List<ActivityAvailableFeatureModel> avilableFeatures) {
        this.avilableFeatures = avilableFeatures;
    }

    public ActivityProviderModel getProvider() {
        return provider;
    }

    public void setProvider(ActivityProviderModel provider) {
        this.provider = provider;
    }

    public ActivityTypeModel getType() {
        return type;
    }

    public void setType(ActivityTypeModel type) {
        this.type = type;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }
    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return false;
    }
}
