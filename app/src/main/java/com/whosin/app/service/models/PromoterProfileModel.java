package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.List;

public class PromoterProfileModel implements DiffIdentifier, ModelProtocol{

    @SerializedName("profile")
    @Expose
    private UserDetailModel profile;

    @SerializedName("circles")
    @Expose
    private List<PromoterCirclesModel> circles;

    @SerializedName("rings")
    @Expose
    private PromoterRingsModel rings;

    @SerializedName("review")
    @Expose
    private ReviewModel reviewModel;

    @SerializedName("venues")
    @Expose
    private PromoterVenuesModel venues;

    @SerializedName("events")
    @Expose
    private List<PromoterEventModel> events;

    @SerializedName("score")
    @Expose
    private CmpScoreModel score;


    public UserDetailModel getProfile() {
        return profile;
    }

    public void setProfile(UserDetailModel profile) {
        this.profile = profile;
    }

    public List<PromoterCirclesModel> getCircles() {
        return circles;
    }

    public void setCircles(List<PromoterCirclesModel> circles) {
        this.circles = circles;
    }

    public PromoterRingsModel getRings() {
        return rings;
    }

    public void setRings(PromoterRingsModel rings) {
        this.rings = rings;
    }

    public ReviewModel getReviewModel() {
        return reviewModel;
    }

    public void setReviewModel(ReviewModel reviewModel) {
        this.reviewModel = reviewModel;
    }

    public PromoterVenuesModel getVenues() {
        return venues;
    }

    public void setVenues(PromoterVenuesModel venues) {
        this.venues = venues;
    }

    public CmpScoreModel getScore() {
        return score;
    }

    public void setScore(CmpScoreModel score) {
        this.score = score;
    }

    public List<PromoterEventModel> getEvents() {
        return events;
    }

    public void setEvents(List<PromoterEventModel> events) {
        this.events = events;
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
