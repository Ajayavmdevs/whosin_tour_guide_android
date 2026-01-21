package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class ComplimentaryProfileModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("profile")
    @Expose
    private UserDetailModel profile;
    @SerializedName("rings")
    @Expose
    private PromoterRingsModel rings;
    @SerializedName("score")
    @Expose
    private CmpScoreModel score;
    @SerializedName("inEvents")
    @Expose
    private List<PromoterEventModel> inEvents;
    @SerializedName("wishlistEvents")
    @Expose
    private List<PromoterEventModel> wishlistEvents;
    @SerializedName("speciallyForMeEvents")
    @Expose
    private List<PromoterEventModel> speciallyForMeEvents;
    @SerializedName("interestedEvents")
    @Expose
    private List<PromoterEventModel> interestedEvents;

    @SerializedName("review")
    @Expose
    private ReviewModel review;
    @SerializedName("counter")
    @Expose
    private CmEventsCounterModel counter;

    @SerializedName("isAdminPromoter")
    @Expose
    private boolean isAdminPromoter = false;

    public UserDetailModel getProfile() {
        return profile == null ? new UserDetailModel() : profile;
    }

    public void setProfile(UserDetailModel profile) {
        this.profile = profile;
    }

    public PromoterRingsModel getRings() {
        return rings == null ? new PromoterRingsModel() : rings;
    }

    public void setRings(PromoterRingsModel rings) {
        this.rings = rings;
    }

    public CmpScoreModel getScore() {
        return score == null ? new CmpScoreModel() : score;
    }

    public void setScore(CmpScoreModel score) {
        this.score = score;
    }

    public List<PromoterEventModel> getInEvents() {
        return inEvents == null ? new ArrayList<>() : inEvents;
    }

    public void setInEvents(List<PromoterEventModel> inEvents) {
        this.inEvents = inEvents;
    }

    public List<PromoterEventModel> getWishlistEvents() {
        return wishlistEvents == null ? new ArrayList<>() : wishlistEvents;
    }

    public void setWishlistEvents(List<PromoterEventModel> wishlistEvents) {
        this.wishlistEvents = wishlistEvents;
    }

    public ReviewModel getReview() {
        return review == null ? new ReviewModel() : review;
    }

    public void setReview(ReviewModel review) {
        this.review = review;
    }

    public CmEventsCounterModel getCounter() {
        return counter;
    }

    public void setCounter(CmEventsCounterModel counter) {
        this.counter = counter;
    }

    public List<PromoterEventModel> getSpeciallyForMeEvents() {
        return speciallyForMeEvents;
    }

    public void setSpeciallyForMeEvents(List<PromoterEventModel> speciallyForMeEvents) {
        this.speciallyForMeEvents = speciallyForMeEvents;
    }

    public List<PromoterEventModel> getInterestedEvents() {
        return interestedEvents;
    }

    public void setInterestedEvents(List<PromoterEventModel> interestedEvents) {
        this.interestedEvents = interestedEvents;
    }

    public boolean isAdminPromoter() {
        return isAdminPromoter;
    }

    public void setAdminPromoter(boolean adminPromoter) {
        isAdminPromoter = adminPromoter;
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
