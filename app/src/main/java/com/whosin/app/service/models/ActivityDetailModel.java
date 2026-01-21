package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

import java.util.ArrayList;
import java.util.List;

public class ActivityDetailModel implements DiffIdentifier,ModelProtocol {


    @SerializedName("_id")
    @Expose
    private String id="";
    @SerializedName("name")
    @Expose
    private String name="";
    @SerializedName("galleries")
    @Expose
    private List<String> galleries;
    @SerializedName("description")
    @Expose
    private String description="";
    @SerializedName("providerId")
    @Expose
    private String providerId="";
    @SerializedName("typeId")
    @Expose
    private String typeId="";
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("discount")
    @Expose
    private String discount="";
    @SerializedName("totalSeats")
    @Expose
    private int totalSeats;
    @SerializedName("startDate")
    @Expose
    private String startDate="";
    @SerializedName("reservationEnd")
    @Expose
    private String reservationEnd="";
    @SerializedName("reservationStart")
    @Expose
    private String reservationStart="";
    @SerializedName("endDate")
    @Expose
    private String endDate="";
    @SerializedName("avilableDays")
    @Expose
    private List<String> avilableDays;
    @SerializedName("activityTime")
    @Expose
    private TestActivityTimeModel activityTime;
    @SerializedName("status")
    @Expose
    private String status="";
    @SerializedName("avilableFeatures")
    @Expose
    private List<ActivityAvailableFeatureModel> avilableFeatures;
    @SerializedName("createdAt")
    @Expose
    private String createdAt="";
    @SerializedName("__v")
    @Expose
    private int v;
    @SerializedName("activityType")
    @Expose
    private ActivityTypeModel activityType;
    @SerializedName("avg_ratings")
    @Expose
    private float avgRating;
    @SerializedName("myRating")
    @Expose
    private Object myRating;

    @SerializedName("provider")
    @Expose
    private ActivityProviderModel provider;

    @SerializedName("disclaimerTitle")
    @Expose
    private String disclaimerTitle = "";
    @SerializedName("disclaimerDescription")
    @Expose
    private String disclaimerDescription = "";

    @SerializedName("currentUserReview")
    @Expose
    private CurrentUserRatingModel currentUserRating;

    @SerializedName("reviews")
    @Expose
    private List<CurrentUserRatingModel> reviews;
    @SerializedName("users")
    @Expose
    private List<UserDetailModel> users;

    @SerializedName("coverImage")
    @Expose
    private String coverImage = "";

    @SerializedName("isRecommendation")
    @Expose
    private boolean isRecommendation ;

    public String getReservationEnd() {
        return reservationEnd;
    }

    public void setReservationEnd(String reservationEnd) {
        this.reservationEnd = reservationEnd;
    }

    public String getReservationStart() {
        return reservationStart;
    }

    public void setReservationStart(String reservationStart) {
        this.reservationStart = reservationStart;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getGalleries() {
        if (galleries == null) {
            return new ArrayList<>();
        }
        return galleries;
    }

    public void setGalleries(List<String> galleries) {
        this.galleries = galleries;
    }

    public String getDescription() {
        return Utils.notNullString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
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
        if (avilableDays == null){
            return new ArrayList<>();
        }
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ActivityAvailableFeatureModel> getAvilableFeatures() {
        if (avilableFeatures == null){
            return new ArrayList<>();
        }
        return avilableFeatures;
    }

    public void setAvilableFeatures(List<ActivityAvailableFeatureModel> avilableFeatures) {
        this.avilableFeatures = avilableFeatures;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public ActivityTypeModel getActivityType() {
        if (activityType == null){
            return  new ActivityTypeModel();
        }
        return activityType;
    }

    public void setActivityType(ActivityTypeModel activityType) {
        this.activityType = activityType;
    }
    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public Object getMyRating() {
        return myRating;
    }

    public void setMyRating(Object myRating) {
        this.myRating = myRating;
    }

    public ActivityProviderModel getProvider() {
        return provider;
    }

    public void setProvider(ActivityProviderModel provider) {
        this.provider = provider;
    }

    public CurrentUserRatingModel getCurrentUserRating() {
        return currentUserRating == null ? new CurrentUserRatingModel() : currentUserRating;
    }

    public void setCurrentUserRating(CurrentUserRatingModel currentUserRating) {
        this.currentUserRating = currentUserRating;
    }

    public List<CurrentUserRatingModel> getReviews() {
        if (reviews == null){
            return new ArrayList<>();
        }
        return reviews;
    }

    public void setReviews(List<CurrentUserRatingModel> reviews) {
        this.reviews = reviews;
    }

    public List<UserDetailModel> getUsers() {
        if (users == null){
            return new ArrayList<>();
        }
        return users;
    }

    public void setUsers(List<UserDetailModel> users) {
        this.users = users;
    }


    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public String getDisclaimerTitle() {
        return disclaimerTitle;
    }

    public void setDisclaimerTitle(String disclaimerTitle) {
        this.disclaimerTitle = disclaimerTitle;
    }

    public String getDisclaimerDescription() {
        return disclaimerDescription;
    }

    public void setDisclaimerDescription(String disclaimerDescription) {
        this.disclaimerDescription = disclaimerDescription;
    }

    public boolean isRecommendation() {
        return isRecommendation;
    }

    public boolean getRecommendation() {
        return isRecommendation;
    }

    public void setRecommendation(boolean recommendation) {
        isRecommendation = recommendation;
    }
}
