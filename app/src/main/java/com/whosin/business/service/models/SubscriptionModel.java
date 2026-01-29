package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

public class SubscriptionModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("userId")
    @Expose
    private String userId = "";
    @SerializedName("validity")
    @Expose
    private String validity = "";
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";

    @SerializedName("package")
    @Expose
    private MemberShipPackageModel _package;
    @SerializedName("title")
    @Expose
    private String title = "";
    @SerializedName("subTitle")
    @Expose
    private String subTitle = "";
    @SerializedName("description")
    @Expose
    private String description ="";
    @SerializedName("image")
    @Expose
    private String image ="";
    @SerializedName("buttonText")
    @Expose
    private String buttonText ="";
    @SerializedName("backgroundType")
    @Expose
    private String backgroundType = "";
    @SerializedName("startColor")
    @Expose
    private String startColor = "";
    @SerializedName("endColor")
    @Expose
    private String endColor = "";
    @SerializedName("frequency")
    @Expose
    private int frequency;
    @SerializedName("packageId")
    @Expose
    private MemberShipPackageModel packageId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getBackgroundType() {
        return backgroundType;
    }

    public void setBackgroundType(String backgroundType) {
        this.backgroundType = backgroundType;
    }

    public String getStartColor() {
        return startColor;
    }

    public void setStartColor(String startColor) {
        this.startColor = startColor;
    }

    public String getEndColor() {
        return endColor;
    }

    public void setEndColor(String endColor) {
        this.endColor = endColor;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public MemberShipPackageModel getPackage() {
        return _package;
    }

    public void setPackage(MemberShipPackageModel _package) {
        this._package = _package;
    }
    public MemberShipPackageModel getPackageId() {
        return packageId;
    }

    public void setPackageId(MemberShipPackageModel packageId) {
        this.packageId = packageId;
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
