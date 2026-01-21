package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

public class VenueMetaDataPromoCodeModel implements DiffIdentifier, ModelProtocol{

    @SerializedName("_id")
    @Expose
    private String _id = "";

    @SerializedName("ticketId")
    @Expose
    private String ticketId = "";

    @SerializedName("dealId")
    @Expose
    private String dealId = "";

    @SerializedName("packageId")
    @Expose
    private String packageId = "";

    @SerializedName("type")
    @Expose
    private String type = "";

    @SerializedName("amount")
    @Expose
    private int amount = 0;

    @SerializedName("discountPrize")
    @Expose
    private double discountPrize = 0 ;

    @SerializedName("discount")
    @Expose
    private double discount = 0;

    @SerializedName("discountType")
    @Expose
    private String discountType = "";

    @SerializedName("isEligible")
    @Expose
    private boolean isEligible = false;

    @SerializedName("promoType")
    @Expose
    private String promoType = "";

    @SerializedName("finalDiscount")
    @Expose
    private double finalDiscount = 0;

    @SerializedName("finalAmount")
    @Expose
    private double finalAmount = 0;

    @SerializedName("finalDiscountInPercent")
    @Expose
    private double finalDiscountInPercent = 0;

    @SerializedName("promoDiscountInPercent")
    @Expose
    private double promoDiscountInPercent = 0;

    @SerializedName("qty")
    @Expose
    private int qty = 0;


    public String getDealId() {
        return Utils.notNullString(dealId);
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getPackageId() {
        return Utils.notNullString(packageId);
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getType() {
        return Utils.notNullString(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getDiscountPrize() {
        return discountPrize;
    }

    public void setDiscountPrize(double discountPrize) {
        this.discountPrize = discountPrize;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getDiscountType() {
        return Utils.notNullString(discountType);
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public boolean isEligible() {
        return isEligible;
    }

    public void setEligible(boolean eligible) {
        isEligible = eligible;
    }

    public String getPromoType() {
        return promoType;
    }

    public void setPromoType(String promoType) {
        this.promoType = promoType;
    }

    public double getFinalDiscount() {
        return finalDiscount;
    }

    public void setFinalDiscount(double finalDiscount) {
        this.finalDiscount = finalDiscount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public double getFinalDiscountInPercent() {
        return finalDiscountInPercent;
    }

    public void setFinalDiscountInPercent(double finalDiscountInPercent) {
        this.finalDiscountInPercent = finalDiscountInPercent;
    }

    public double getPromoDiscountInPercent() {
        return promoDiscountInPercent;
    }

    public void setPromoDiscountInPercent(double promoDiscountInPercent) {
        this.promoDiscountInPercent = promoDiscountInPercent;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String get_id() {
        return Utils.notNullString(_id);
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTicketId() {
        return Utils.notNullString(ticketId);
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
}
