package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

import java.util.List;

public class VenuePromoCodeModel implements DiffIdentifier, ModelProtocol{


    @SerializedName("totalDiscount")
    @Expose
    private double totalDiscount = 0;

    @SerializedName("totalAmount")
    @Expose
    private double totalAmount = 0;

    @SerializedName("amount")
    @Expose
    private String amount = "";

    @SerializedName("promoDiscountType")
    @Expose
    private String promoDiscountType = "";

    @SerializedName("promoDiscount")
    @Expose
    private double promoDiscount = 0;

    @SerializedName("itemsDiscount")
    @Expose
    private double itemsDiscount = 0;

    @SerializedName("metadata")
    @Expose
    private List<VenueMetaDataPromoCodeModel> metaData;

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public double getTotalDiscount() {
        return Utils.roundDoubleValueToDouble(totalDiscount);
    }

    public void setTotalDiscount(int totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public List<VenueMetaDataPromoCodeModel> getMetaData() {
        return Utils.notEmptyList(metaData);
    }

    public void setMetaData(List<VenueMetaDataPromoCodeModel> metaData) {
        this.metaData = metaData;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public double getPromoDiscount() {
        return Utils.roundDoubleValueToDouble(promoDiscount);
    }

    public void setPromoDiscount(double promoDiscount) {
        this.promoDiscount = promoDiscount;
    }

    public double getItemsDiscount() {
        return itemsDiscount;
    }

    public void setItemsDiscount(double itemsDiscount) {
        this.itemsDiscount = itemsDiscount;
    }

    public String getPromoDiscountType() {
        return promoDiscountType;
    }

    public void setPromoDiscountType(String promoDiscountType) {
        this.promoDiscountType = promoDiscountType;
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
