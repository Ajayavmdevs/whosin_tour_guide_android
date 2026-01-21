package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.List;

public class ReviewModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("reviews")
    @Expose
    private List<CurrentUserRatingModel> reviews;
    @SerializedName("users")
    @Expose
    private List<ContactListModel> users;

    @SerializedName("avgRating")
    @Expose
    private float avgRating = 0;

    @SerializedName("currentUserReview")
    @Expose
    private CurrentUserRatingModel currentUserRating;

    public List<CurrentUserRatingModel> getReviews() {
        return reviews;
    }

    public void setReviews(List<CurrentUserRatingModel> reviews) {
        this.reviews = reviews;
    }

    public List<ContactListModel> getUsers() {
        return users;
    }

    public void setUsers(List<ContactListModel> users) {
        this.users = users;
    }

    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public CurrentUserRatingModel getCurrentUserRating() {
        return currentUserRating;
    }

    public void setCurrentUserRating(CurrentUserRatingModel currentUserRating) {
        this.currentUserRating = currentUserRating;
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
