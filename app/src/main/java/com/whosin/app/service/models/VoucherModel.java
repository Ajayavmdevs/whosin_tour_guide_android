package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.List;

public class VoucherModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("title")
    @Expose
    private String title = "";
    @SerializedName("description")
    @Expose
    private String description="";
    @SerializedName("discountValue")
    @Expose
    private String discountValue = "";
    @SerializedName("originalPrice")
    @Expose
    private int originalPrice = 0;
    @SerializedName("discountedPrice")
    @Expose
    private int discountedPrice;
    @SerializedName("startDate")
    @Expose
    private String startDate ="";
    @SerializedName("endDate")
    @Expose
    private String endDate="";
    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("categoryId")
    @Expose
    private String categoryId="";

    @SerializedName("offerId")
    @Expose
    private String offerId="";
    @SerializedName("dimension")
    @Expose
    private String dimension="";
    @SerializedName("image")
    @Expose
    private String image="";
    @SerializedName("days")
    @Expose
    private String days="";
    @SerializedName("venueId")
    @Expose
    private String venueId="";
    @SerializedName("disclaimerTitle")
    @Expose
    private String disclaimerTitle = "";
    @SerializedName("disclaimerDescription")
    @Expose
    private String disclaimerDescription = "";
    @SerializedName("startTime")
    @Expose
    private String startTime="";
    @SerializedName("paxPerVoucher")
    @Expose
    private int paxPerVoucher;
    @SerializedName("actualPrice")
    @Expose
    private String actualPrice="";
    @SerializedName("endTime")
    @Expose
    private String endTime="";
    @SerializedName("discount")
    @Expose
    private String discount="";
    @SerializedName("allowWhosIn")
    @Expose
    private boolean allowWhosIn;
    @SerializedName("createdAt")
    @Expose
    private String createdAt="";

    @SerializedName("packages")
    @Expose
    private List<PackageModel> packages;
    @SerializedName("features")
    @Expose
    private List<ActivityAvailableFeatureModel> features;

    @SerializedName("package")
    @Expose
    private PackageModel _package;

    @SerializedName("venue")
    @Expose
    private VenueObjectModel venue;

    private int qty = 0;
    private VenueObjectModel venueObjectModel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getPaxPerVoucher() {
        return paxPerVoucher;
    }

    public void setPaxPerVoucher(int paxPerVoucher) {
        this.paxPerVoucher = paxPerVoucher;
    }

    public String getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(String actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public boolean isAllowWhosIn() {
        return allowWhosIn;
    }

    public void setAllowWhosIn(boolean allowWhosIn) {
        this.allowWhosIn = allowWhosIn;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<PackageModel> getPackages() {
        return packages;
    }

    public void setPackages(List<PackageModel> packages) {
        this.packages = packages;
    }

    public List<ActivityAvailableFeatureModel> getFeatures() {
        return features;
    }

    public void setFeatures(List<ActivityAvailableFeatureModel> features) {
        this.features = features;
    }

    public PackageModel get_package() {
        return _package;
    }

    public void set_package(PackageModel _package) {
        this._package = _package;
    }

    public VenueObjectModel getVenue() {
        return venue;
    }

    public void setVenue(VenueObjectModel venue) {
        this.venue = venue;
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

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void addQty(int qty) {
        this.qty = this.qty + qty;
    }

    public int getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(int discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public VenueObjectModel getVenueObjectModel() {
        return venueObjectModel;
    }

    public void setVenueObjectModel(VenueObjectModel venueObjectModel) {
        this.venueObjectModel = venueObjectModel;
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
}
