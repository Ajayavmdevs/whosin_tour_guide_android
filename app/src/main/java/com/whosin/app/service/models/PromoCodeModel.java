package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

public class PromoCodeModel implements DiffIdentifier, ModelProtocol{
    @SerializedName("promoCodeInfo")
    @Expose
    private PromoCodeInfoModel promoCodeInfo;
    @SerializedName("discountAmount")
    @Expose
    private Integer discountAmount;
    @SerializedName("finalAmount")
    @Expose
    private Integer finalAmount;

    public PromoCodeInfoModel getPromoCodeInfo() {
        return promoCodeInfo;
    }

    public void setPromoCodeInfo(PromoCodeInfoModel promoCodeInfo) {
        this.promoCodeInfo = promoCodeInfo;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Integer finalAmount) {
        this.finalAmount = finalAmount;
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
