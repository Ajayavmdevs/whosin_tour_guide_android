package com.whosin.app.service.models.TravelDeskModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class TravelDeskPriceModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("date")
    @Expose
    private String date = "";

    @SerializedName("dateStart")
    @Expose
    private String dateStart = "";

    @SerializedName("dateEnd")
    @Expose
    private String dateEnd = "";

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("offerId")
    @Expose
    private int offerId;

    @SerializedName("currency")
    @Expose
    private String currency = "";

    @SerializedName("recPricePerAdult")
    @Expose
    private double recPricePerAdult;

    @SerializedName("recPricePerAdultUsd")
    @Expose
    private double recPricePerAdultUsd;

    @SerializedName("recPricePerChild")
    @Expose
    private double recPricePerChild;

    @SerializedName("recPricePerChildUsd")
    @Expose
    private double recPricePerChildUsd;

    @SerializedName("recPricePerTrip")
    @Expose
    private double recPricePerTrip;

    @SerializedName("recPricePerTripUsd")
    @Expose
    private double recPricePerTripUsd;

    @SerializedName("pricePerAdult")
    @Expose
    private Float pricePerAdult;

    @SerializedName("pricePerChild")
    @Expose
    private Float pricePerChild;

    @SerializedName("pricePerInfant")
    @Expose
    private Float pricePerInfant;

    @SerializedName("pricePerTrip")
    @Expose
    private Float pricePerTrip;

    @SerializedName("pricePerAdultUsd")
    @Expose
    private double pricePerAdultUsd;

    @SerializedName("pricePerChildUsd")
    @Expose
    private double pricePerChildUsd;

    @SerializedName("pricePerTripUsd")
    @Expose
    private double pricePerTripUsd;

    @SerializedName("discountPercent")
    @Expose
    private double discountPercent;

    @SerializedName("pricePerAdultBeforeDiscountUsd")
    @Expose
    private double pricePerAdultBeforeDiscountUsd;

    @SerializedName("pricePerChildBeforeDiscountUsd")
    @Expose
    private double pricePerChildBeforeDiscountUsd;

    @SerializedName("pricePerTripBeforeDiscountUsd")
    @Expose
    private double pricePerTripBeforeDiscountUsd;

    @SerializedName("pricePerAdultBeforeDiscount")
    @Expose
    private Float pricePerAdultBeforeDiscount;

    @SerializedName("pricePerChildBeforeDiscount")
    @Expose
    private Float pricePerChildBeforeDiscount;

    @SerializedName("pricePerInfantBeforeDiscount")
    @Expose
    private Float pricePerInfantBeforeDiscount;

    @SerializedName("pricePerTripBeforeDiscount")
    @Expose
    private double pricePerTripBeforeDiscount;

    @SerializedName("isBookable")
    @Expose
    private boolean isBookable;

    @SerializedName("pricePerAdultTravelDesk")
    @Expose
    private Float pricePerAdultTravelDesk;

    @SerializedName("pricePerChildTravelDesk")
    @Expose
    private Float pricePerChildTravelDesk;

    @SerializedName("pricePerTripTravelDesk")
    @Expose
    private Float pricePerTripTravelDesk;


    public Float getPricePerAdultTravelDesk() {
        return pricePerAdultTravelDesk;
    }

    public void setPricePerAdultTravelDesk(Float pricePerAdultTravelDesk) {
        this.pricePerAdultTravelDesk = pricePerAdultTravelDesk;
    }

    public Float getPricePerChildTravelDesk() {
        return pricePerChildTravelDesk;
    }

    public void setPricePerChildTravelDesk(Float pricePerChildTravelDesk) {
        this.pricePerChildTravelDesk = pricePerChildTravelDesk;
    }

    public Float getPricePerTripTravelDesk() {
        return pricePerTripTravelDesk;
    }

    public void setPricePerTripTravelDesk(Float pricePerTripTravelDesk) {
        this.pricePerTripTravelDesk = pricePerTripTravelDesk;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getRecPricePerAdult() {
        return recPricePerAdult;
    }

    public void setRecPricePerAdult(double recPricePerAdult) {
        this.recPricePerAdult = recPricePerAdult;
    }

    public double getRecPricePerAdultUsd() {
        return recPricePerAdultUsd;
    }

    public void setRecPricePerAdultUsd(double recPricePerAdultUsd) {
        this.recPricePerAdultUsd = recPricePerAdultUsd;
    }

    public double getRecPricePerChild() {
        return recPricePerChild;
    }

    public void setRecPricePerChild(double recPricePerChild) {
        this.recPricePerChild = recPricePerChild;
    }

    public double getRecPricePerChildUsd() {
        return recPricePerChildUsd;
    }

    public void setRecPricePerChildUsd(double recPricePerChildUsd) {
        this.recPricePerChildUsd = recPricePerChildUsd;
    }

    public double getRecPricePerTrip() {
        return recPricePerTrip;
    }

    public void setRecPricePerTrip(double recPricePerTrip) {
        this.recPricePerTrip = recPricePerTrip;
    }

    public double getRecPricePerTripUsd() {
        return recPricePerTripUsd;
    }

    public void setRecPricePerTripUsd(double recPricePerTripUsd) {
        this.recPricePerTripUsd = recPricePerTripUsd;
    }

    public Float getPricePerAdult() {
        return pricePerAdult;
    }

    public void setPricePerAdult(Float pricePerAdult) {
        this.pricePerAdult = pricePerAdult;
    }

    public Float getPricePerChild() {
        return pricePerChild;
    }

    public void setPricePerChild(Float pricePerChild) {
        this.pricePerChild = pricePerChild;
    }

    public Float getPricePerInfant() {
        return pricePerInfant;
    }

    public void setPricePerInfant(Float pricePerInfant) {
        this.pricePerInfant = pricePerInfant;
    }

    public Float getPricePerTrip() {
        return pricePerTrip;
    }

    public void setPricePerTrip(Float pricePerTrip) {
        this.pricePerTrip = pricePerTrip;
    }

    public double getPricePerAdultUsd() {
        return pricePerAdultUsd;
    }

    public void setPricePerAdultUsd(double pricePerAdultUsd) {
        this.pricePerAdultUsd = pricePerAdultUsd;
    }

    public double getPricePerChildUsd() {
        return pricePerChildUsd;
    }

    public void setPricePerChildUsd(double pricePerChildUsd) {
        this.pricePerChildUsd = pricePerChildUsd;
    }

    public double getPricePerTripUsd() {
        return pricePerTripUsd;
    }

    public void setPricePerTripUsd(double pricePerTripUsd) {
        this.pricePerTripUsd = pricePerTripUsd;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getPricePerAdultBeforeDiscountUsd() {
        return pricePerAdultBeforeDiscountUsd;
    }

    public void setPricePerAdultBeforeDiscountUsd(double pricePerAdultBeforeDiscountUsd) {
        this.pricePerAdultBeforeDiscountUsd = pricePerAdultBeforeDiscountUsd;
    }

    public double getPricePerChildBeforeDiscountUsd() {
        return pricePerChildBeforeDiscountUsd;
    }

    public void setPricePerChildBeforeDiscountUsd(double pricePerChildBeforeDiscountUsd) {
        this.pricePerChildBeforeDiscountUsd = pricePerChildBeforeDiscountUsd;
    }

    public double getPricePerTripBeforeDiscountUsd() {
        return pricePerTripBeforeDiscountUsd;
    }

    public void setPricePerTripBeforeDiscountUsd(double pricePerTripBeforeDiscountUsd) {
        this.pricePerTripBeforeDiscountUsd = pricePerTripBeforeDiscountUsd;
    }

    public Float getPricePerAdultBeforeDiscount() {
        return pricePerAdultBeforeDiscount;
    }

    public void setPricePerAdultBeforeDiscount(Float pricePerAdultBeforeDiscount) {
        this.pricePerAdultBeforeDiscount = pricePerAdultBeforeDiscount;
    }

    public Float getPricePerChildBeforeDiscount() {
        return pricePerChildBeforeDiscount;
    }

    public void setPricePerChildBeforeDiscount(Float pricePerChildBeforeDiscount) {
        this.pricePerChildBeforeDiscount = pricePerChildBeforeDiscount;
    }

    public Float getPricePerInfantBeforeDiscount() {
        return pricePerInfantBeforeDiscount;
    }

    public void setPricePerInfantBeforeDiscount(Float pricePerInfantBeforeDiscount) {
        this.pricePerInfantBeforeDiscount = pricePerInfantBeforeDiscount;
    }

    public double getPricePerTripBeforeDiscount() {
        return pricePerTripBeforeDiscount;
    }

    public void setPricePerTripBeforeDiscount(double pricePerTripBeforeDiscount) {
        this.pricePerTripBeforeDiscount = pricePerTripBeforeDiscount;
    }

    public boolean isBookable() {
        return isBookable;
    }

    public void setBookable(boolean bookable) {
        isBookable = bookable;
    }

    public String getDateStart() {
        return Utils.notNullString(dateStart);
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return Utils.notNullString(dateEnd);
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
