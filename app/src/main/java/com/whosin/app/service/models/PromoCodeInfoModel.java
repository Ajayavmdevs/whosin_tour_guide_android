package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.List;

public class PromoCodeInfoModel implements DiffIdentifier, ModelProtocol{

    @SerializedName("dealIds")
    @Expose
    private List<String> dealIds;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("bannerImage")
    @Expose
    private String bannerImage;
    @SerializedName("promoCode")
    @Expose
    private String promoCode;
    @SerializedName("discountPercentage")
    @Expose
    private Integer discountPercentage;
    @SerializedName("minimumPurchaseAmount")
    @Expose
    private Integer minimumPurchaseAmount;
    @SerializedName("maximumDiscountAmount")
    @Expose
    private Integer maximumDiscountAmount;
    @SerializedName("targetTypes")
    @Expose
    private List<Object> targetTypes;
    @SerializedName("usageLimitation")
    @Expose
    private Integer usageLimitation;
    @SerializedName("startDate")
    @Expose
    private Object startDate;
    @SerializedName("endDate")
    @Expose
    private Object endDate;
    @SerializedName("activityIds")
    @Expose
    private List<Object> activityIds;
    @SerializedName("offerPackageIds")
    @Expose
    private List<Object> offerPackageIds;
    @SerializedName("discountIds")
    @Expose
    private List<Object> discountIds;
    @SerializedName("membershipPackageIds")
    @Expose
    private List<Object> membershipPackageIds;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("isDeleted")
    @Expose
    private Boolean isDeleted;

    public List<String> getDealIds() {
        return dealIds;
    }

    public void setDealIds(List<String> dealIds) {
        this.dealIds = dealIds;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Integer getMinimumPurchaseAmount() {
        return minimumPurchaseAmount;
    }

    public void setMinimumPurchaseAmount(Integer minimumPurchaseAmount) {
        this.minimumPurchaseAmount = minimumPurchaseAmount;
    }

    public Integer getMaximumDiscountAmount() {
        return maximumDiscountAmount;
    }

    public void setMaximumDiscountAmount(Integer maximumDiscountAmount) {
        this.maximumDiscountAmount = maximumDiscountAmount;
    }

    public List<Object> getTargetTypes() {
        return targetTypes;
    }

    public void setTargetTypes(List<Object> targetTypes) {
        this.targetTypes = targetTypes;
    }

    public Integer getUsageLimitation() {
        return usageLimitation;
    }

    public void setUsageLimitation(Integer usageLimitation) {
        this.usageLimitation = usageLimitation;
    }

    public Object getStartDate() {
        return startDate;
    }

    public void setStartDate(Object startDate) {
        this.startDate = startDate;
    }

    public Object getEndDate() {
        return endDate;
    }

    public void setEndDate(Object endDate) {
        this.endDate = endDate;
    }

    public List<Object> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<Object> activityIds) {
        this.activityIds = activityIds;
    }

    public List<Object> getOfferPackageIds() {
        return offerPackageIds;
    }

    public void setOfferPackageIds(List<Object> offerPackageIds) {
        this.offerPackageIds = offerPackageIds;
    }

    public List<Object> getDiscountIds() {
        return discountIds;
    }

    public void setDiscountIds(List<Object> discountIds) {
        this.discountIds = discountIds;
    }

    public List<Object> getMembershipPackageIds() {
        return membershipPackageIds;
    }

    public void setMembershipPackageIds(List<Object> membershipPackageIds) {
        this.membershipPackageIds = membershipPackageIds;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
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
