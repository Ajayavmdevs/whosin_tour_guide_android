package com.whosin.business.service.models.rayna;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.service.models.ModelProtocol;

public class TourImageModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("tourId")
    @Expose
    private Integer tourId;
    @SerializedName("imagePath")
    @Expose
    private String imagePath;
    @SerializedName("imageCaptionName")
    @Expose
    private String imageCaptionName;
    @SerializedName("isFrontImage")
    @Expose
    private Integer isFrontImage;
    @SerializedName("isBannerImage")
    @Expose
    private Integer isBannerImage;
    @SerializedName("isBannerRotateImage")
    @Expose
    private Integer isBannerRotateImage;

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
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

    public Integer getIsFrontImage() {
        return isFrontImage;
    }

    public void setIsFrontImage(Integer isFrontImage) {
        this.isFrontImage = isFrontImage;
    }

    public Integer getIsBannerImage() {
        return isBannerImage;
    }

    public void setIsBannerImage(Integer isBannerImage) {
        this.isBannerImage = isBannerImage;
    }

    public Integer getIsBannerRotateImage() {
        return isBannerRotateImage;
    }

    public void setIsBannerRotateImage(Integer isBannerRotateImage) {
        this.isBannerRotateImage = isBannerRotateImage;
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
