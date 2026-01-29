package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

public class BrunchModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id ="";
    @SerializedName("item")
    @Expose
    private String item = "";
    @SerializedName("amount")
    @Expose
    private String amount="";
    @SerializedName("qty")
    @Expose
    private Integer qty;
    @SerializedName("discount")
    @Expose
    private String discount="";
    @SerializedName("pricePerBrunch")
    @Expose
    private int pricePerBrunch;
    @SerializedName("discountedPrice")
    @Expose
    private int discountedPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(int discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getAmount() {
        return amount;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public int getPricePerBrunch() {
        return pricePerBrunch;
    }

    public void setPricePerBrunch(int pricePerBrunch) {
        this.pricePerBrunch = pricePerBrunch;
    }

    public BrunchModel(String id, String item, String amount, Integer qty, String discount) {
        this.id = id;
        this.item = item;
        this.amount = amount;
        this.qty = qty;
        this.discount = discount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDiscount() {
        return discount;
    }

    public int getDiscountValue() {
        return Utils.stringToInt(discount);
    }


    public void setDiscount(String discount) {
        this.discount = discount;
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
