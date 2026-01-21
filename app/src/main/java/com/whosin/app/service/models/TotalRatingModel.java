package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

public class TotalRatingModel implements DiffIdentifier,ModelProtocol {


    @SerializedName("totalRating")
    @Expose
    private int totalRating;
    @SerializedName("avgRating")
    @Expose
    private String avgRating;
    @SerializedName("summary")
    @Expose
    private RatingSummaryModel summary;

    public int getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(int totalRating) {
        this.totalRating = totalRating;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public RatingSummaryModel getSummary() {
        return summary;
    }

    public void setSummary(RatingSummaryModel summary) {
        this.summary = summary;
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
