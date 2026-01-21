package com.whosin.app.service.models.rayna;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.service.models.ModelProtocol;

public class TourReviewModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("tourId")
    @Expose
    private Integer tourId;
    @SerializedName("reviewId")
    @Expose
    private Integer reviewId;
    @SerializedName("reviewTitle")
    @Expose
    private String reviewTitle;
    @SerializedName("reviewContent")
    @Expose
    private String reviewContent;
    @SerializedName("visitMonth")
    @Expose
    private String visitMonth;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("imagePath")
    @Expose
    private String imagePath;
    @SerializedName("guestName")
    @Expose
    private String guestName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getVisitMonth() {
        return visitMonth;
    }

    public void setVisitMonth(String visitMonth) {
        this.visitMonth = visitMonth;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
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
