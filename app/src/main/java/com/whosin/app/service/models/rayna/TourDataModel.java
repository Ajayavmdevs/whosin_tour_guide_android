package com.whosin.app.service.models.rayna;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

import java.util.List;

public class TourDataModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("reviewCount")
    @Expose
    private String reviewCount;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("isSlot")
    @Expose
    private Boolean isSlot;
    @SerializedName("onlyChild")
    @Expose
    private Boolean onlyChild;
    @SerializedName("recommended")
    @Expose
    private Boolean recommended;
    @SerializedName("isPrivate")
    @Expose
    private Boolean isPrivate;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("tourId")
    @Expose
    private String tourId;
    @SerializedName("countryId")
    @Expose
    private String countryId;
    @SerializedName("countryName")
    @Expose
    private String countryName;
    @SerializedName("cityId")
    @Expose
    private String cityId;
    @SerializedName("cityName")
    @Expose
    private String cityName;
    @SerializedName("tourName")
    @Expose
    private String tourName;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("imagePath")
    @Expose
    private String imagePath;
    @SerializedName("imageCaptionName")
    @Expose
    private String imageCaptionName;
    @SerializedName("cityTourTypeId")
    @Expose
    private String cityTourTypeId;
    @SerializedName("cityTourType")
    @Expose
    private String cityTourType;
    @SerializedName("tourShortDescription")
    @Expose
    private String tourShortDescription;
    @SerializedName("cancellationPolicyName")
    @Expose
    private String cancellationPolicyName;
    @SerializedName("contractId")
    @Expose
    private String contractId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("tourOptionData")
    @Expose
    private List<TourOptionDetailModel> tourOptionData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Boolean getIsSlot() {
        return isSlot;
    }

    public void setIsSlot(Boolean isSlot) {
        this.isSlot = isSlot;
    }

    public Boolean getOnlyChild() {
        return onlyChild;
    }

    public void setOnlyChild(Boolean onlyChild) {
        this.onlyChild = onlyChild;
    }

    public Boolean getRecommended() {
        return recommended;
    }

    public void setRecommended(Boolean recommended) {
        this.recommended = recommended;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return Utils.notNullString(cityName);
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public String getDuration() {
        return Utils.notNullString(duration);
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageCaptionName() {
        return imageCaptionName;
    }

    public void setImageCaptionName(String imageCaptionName) {
        this.imageCaptionName = imageCaptionName;
    }

    public String getCityTourTypeId() {
        return cityTourTypeId;
    }

    public void setCityTourTypeId(String cityTourTypeId) {
        this.cityTourTypeId = cityTourTypeId;
    }

    public String getCityTourType() {
        return Utils.notNullString(cityTourType);
    }

    public void setCityTourType(String cityTourType) {
        this.cityTourType = cityTourType;
    }

    public String getTourShortDescription() {
        return tourShortDescription;
    }

    public void setTourShortDescription(String tourShortDescription) {
        this.tourShortDescription = tourShortDescription;
    }

    public String getCancellationPolicyName() {
        return cancellationPolicyName;
    }

    public void setCancellationPolicyName(String cancellationPolicyName) {
        this.cancellationPolicyName = cancellationPolicyName;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TourOptionDetailModel> getTourOptionData() {
        return Utils.notEmptyList(tourOptionData);
    }

    public void setTourOptionData(List<TourOptionDetailModel> tourOptionData) {
        this.tourOptionData = tourOptionData;
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
