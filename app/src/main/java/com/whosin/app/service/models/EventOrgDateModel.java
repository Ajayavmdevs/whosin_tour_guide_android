package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.List;

public class EventOrgDateModel implements DiffIdentifier,ModelProtocol {
    @SerializedName("_id")
    @Expose
    private String id="";
    @SerializedName("name")
    @Expose
    private String name="";
    @SerializedName("logo")
    @Expose
    private String logo="";
    @SerializedName("cover")
    @Expose
    private String cover="";
    @SerializedName("password")
    @Expose
    private String password="";
    @SerializedName("status")
    @Expose
    private String status="";
    @SerializedName("createdAt")
    @Expose
    private String createdAt="";
    @SerializedName("phone")
    @Expose
    private String phone="";
    @SerializedName("email")
    @Expose
    private String email="";
    @SerializedName("website")
    @Expose
    private String website="";

    @SerializedName("description")
    @Expose
    private String description="";
    @SerializedName("events")
    @Expose
    private List<BucketEventListModel> events;
    @SerializedName("galleries")
    @Expose
    private List<String> galleries;
    @SerializedName("ratings")
    @Expose
    private List<CurrentUserRatingModel> ratings;
    @SerializedName("avg_ratings")
    @Expose
    private float avgRatings;
    @SerializedName("currentUserReview")
    @Expose
    private CurrentUserRatingModel currentUserRating;
    @SerializedName("isFollowing")
    @Expose
    private boolean isFollowing;

    @SerializedName("reviews")
    @Expose
    private List<CurrentUserRatingModel> reviews;

    @SerializedName("users")
    @Expose
    private List<ContactListModel> users;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<BucketEventListModel> getEvents() {
        return events;
    }

    public void setEvents(List<BucketEventListModel> events) {
        this.events = events;
    }

    public List<String> getGalleries() {
        return galleries;
    }

    public void setGalleries(List<String> galleries) {
        this.galleries = galleries;
    }

    public List<CurrentUserRatingModel> getRatings() {
        return ratings;
    }

    public void setRatings(List<CurrentUserRatingModel> ratings) {
        this.ratings = ratings;
    }

    public float getAvgRatings() {
        return avgRatings;
    }

    public void setAvgRatings(float avgRatings) {
        this.avgRatings = avgRatings;
    }

    public CurrentUserRatingModel getCurrentUserRating() {
        return currentUserRating == null ? new CurrentUserRatingModel() : currentUserRating;
    }

    public void setCurrentUserRating(CurrentUserRatingModel currentUserRating) {
        this.currentUserRating = currentUserRating;
    }

    public boolean isIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

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


    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }
}
