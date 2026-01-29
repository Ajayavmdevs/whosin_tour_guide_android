package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

import java.util.List;

public class PackageModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id="";
    @SerializedName("title")
    @Expose
    private String title="";
    @SerializedName("subTitle")
    @Expose
    private String subTitle="";
    @SerializedName("discountedPrice")
    @Expose
    private String discountedPrice;
    @SerializedName("discount")
    @Expose
    private String discount="";
    @SerializedName("description")
    @Expose
    private String description = "";
    @SerializedName("packageData")
    @Expose
    private CategoryPackageModel packageData;

    @SerializedName("discountValue")
    @Expose
    private String discountValue="";
    @SerializedName("actualPrice")
    @Expose
    private int actualPrice;
    @SerializedName("event_id")
    @Expose
    private String eventId="";

    @SerializedName("offerId")
    @Expose
    private String offerId="";
    @SerializedName("qty")
    @Expose
    private int qty;
    @SerializedName("remainingQty")
    @Expose
    private int remainingQty = 0;
    @SerializedName("package_key")
    @Expose
    private String packageKey="";

    @SerializedName("amount")
    @Expose
    private String amount="";
    @SerializedName("status")
    @Expose
    private String status="";
    @SerializedName("createdAt")
    @Expose
    private String createdAt="";
    @SerializedName("isAllowSale")
    @Expose
    private boolean isAllowSale;
    @SerializedName("isAllowClaim")
    @Expose
    private boolean isAllowClaim;

    @SerializedName("isFeatured")
    @Expose
    private boolean isFeatured;

    @SerializedName("leftQtyAlert")
    @Expose
    private int leftQtyAlert;
    private int pricePerBrunch;
    private double price = 0.0;
    @SerializedName("quantity")
    @Expose
    private int quantity = 0;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    private List<String > giftMessage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return Utils.notNullString(title);
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

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
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

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public String getDescription() {
        return description.trim();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount() {
        return Utils.notNullString(discount);
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public CategoryPackageModel getPackageData() {
        return packageData;
    }

    public void setPackageData(CategoryPackageModel packageData) {
        this.packageData = packageData;
    }

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }

    public int getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(int actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getPackageKey() {
        return packageKey;
    }

    public void setPackageKey(String packageKey) {
        this.packageKey = packageKey;
    }

    public String getAmount() {
        return Utils.notNullString(amount);
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public boolean isIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }


    public int getRemainingQty() {
        return remainingQty;
    }

    public void setRemainingQty(int remainingQty) {
        this.remainingQty = remainingQty;
    }

    public int getPricePerBrunch() {
        return pricePerBrunch;
    }

    public boolean isAllowSale() {
        return isAllowSale;
    }

    public void setAllowSale(boolean allowSale) {
        isAllowSale = allowSale;
    }

    public boolean isAllowClaim() {
        return isAllowClaim;
    }

    public void setAllowClaim(boolean allowClaim) {
        isAllowClaim = allowClaim;
    }

    public void setPricePerBrunch(int pricePerBrunch) {
        this.pricePerBrunch = pricePerBrunch;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getLeftQtyAlert() {
        return leftQtyAlert;
    }

    public void setLeftQtyAlert(int leftQtyAlert) {
        this.leftQtyAlert = leftQtyAlert;
    }


    public List<String> getGiftMessage() {
        return giftMessage;
    }

    public void setGiftMessage(List<String> giftMessage) {
        this.giftMessage = giftMessage;
    }

    public boolean isShowLeftQtyAlert() {
        if (remainingQty == 0) {
            return false;
        }
        return remainingQty <= leftQtyAlert;
    }

}
