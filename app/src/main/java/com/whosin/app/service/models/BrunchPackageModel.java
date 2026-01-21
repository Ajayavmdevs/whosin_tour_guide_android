package com.whosin.app.service.models;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

public class BrunchPackageModel implements DiffIdentifier, ModelProtocol{

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("offerId")
    @Expose
    private String offerId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("pricePerBrunch")
    @Expose
    private int pricePerBrunch;
    @SerializedName("isFeatured")
    @Expose
    private Boolean isFeatured;
    @SerializedName("isAllowClaim")
    @Expose
    private Boolean isAllowClaim;
    @SerializedName("qty")
    @Expose
    private Integer qty;

   //quantity is only use for calculation.. not coming from API
    @SerializedName("quantity")
    @Expose
    private int quantity = 0;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPricePerBrunch() {
        return pricePerBrunch;
    }

    private boolean isDiscount50 = false;

    public boolean isDiscount50() {
        return isDiscount50;
    }

    public void setDiscount50(boolean discount50) {
        isDiscount50 = discount50;
    }

    public void setPricePerBrunch(int pricePerBrunch) {
        this.pricePerBrunch = pricePerBrunch;
    }

    public Boolean getFeatured() {
        return isFeatured;
    }

    public void setFeatured(Boolean featured) {
        isFeatured = featured;
    }

    @SerializedName("discountedPrice")
    @Expose
    private int discountedPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiscount() {
        return TextUtils.isEmpty(discount) ? "0" : discount;
    }



    public int getDiscountValue() {
        return Integer.parseInt(getDiscount());
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getAmount() {
        return amount;
    }

    public int getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(int discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    public Boolean getAllowClaim() {
        return isAllowClaim;
    }

    public void setAllowClaim(Boolean allowClaim) {
        isAllowClaim = allowClaim;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }
}
