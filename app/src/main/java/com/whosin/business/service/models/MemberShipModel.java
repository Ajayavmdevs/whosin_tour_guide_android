package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

import java.util.List;

public class MemberShipModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("package_key")
    @Expose
    private String packageKey;
    @SerializedName("subTitle")
    @Expose
    private String subTitle;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("actualPrice")
    @Expose
    private Integer actualPrice;
    @SerializedName("discountType")
    @Expose
    private String discountType;
    @SerializedName("discount")
    @Expose
    private Integer discount;
    @SerializedName("discountText")
    @Expose
    private String discountText;
    @SerializedName("features")
    @Expose
    private List<FeatureModel> features;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("isPopular")
    @Expose
    private Boolean isPopular;
    @SerializedName("isDeleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("termsAndCondition")
    @Expose
    private String termsAndCondition;
    @SerializedName("additionalValidity")
    @Expose
    private Integer additionalValidity;

    @SerializedName("membershipPackageId")
    @Expose
    private String membershipPackageId;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("amount")
    @Expose
    private int amount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("validTill")
    @Expose
    private String validTill;
    @SerializedName("orderStatus")
    @Expose
    private String orderStatus;
    @SerializedName("membershipStatus")
    @Expose
    private String membershipStatus;
    @SerializedName("paymentStatus")
    @Expose
    private String paymentStatus;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;

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

    public String getPackageKey() {
        return packageKey;
    }

    public void setPackageKey(String packageKey) {
        this.packageKey = packageKey;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(Integer actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getDiscountText() {
        return discountText;
    }

    public void setDiscountText(String discountText) {
        this.discountText = discountText;
    }

    public List<FeatureModel> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeatureModel> features) {
        this.features = features;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getIsPopular() {
        return isPopular;
    }

    public void setIsPopular(Boolean isPopular) {
        this.isPopular = isPopular;
    }

    public Boolean getPopular() {
        return isPopular;
    }

    public void setPopular(Boolean popular) {
        isPopular = popular;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTermsAndCondition() {
        return termsAndCondition;
    }

    public void setTermsAndCondition(String termsAndCondition) {
        this.termsAndCondition = termsAndCondition;
    }

    public Integer getAdditionalValidity() {
        return additionalValidity;
    }

    public void setAdditionalValidity(Integer additionalValidity) {
        this.additionalValidity = additionalValidity;
    }
    public String getMembershipPackageId() {
        return membershipPackageId;
    }

    public void setMembershipPackageId(String membershipPackageId) {
        this.membershipPackageId = membershipPackageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getValidTill() {
        return validTill;
    }

    public void setValidTill(String validTill) {
        this.validTill = validTill;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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
