package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

import java.util.ArrayList;
import java.util.List;

public class HomeTicketsModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("description")
    @Expose
    private String description ="";
    @SerializedName("badge")
    @Expose
    private String badge ="";
    @SerializedName("avg_ratings")
    private double avg_ratings ;
    @SerializedName("tags")
    @Expose
    private List<String> tags;
    @SerializedName("city")
    @Expose
    private String city ="";
    @SerializedName("images")
    @Expose
    private List<String> images;
    @SerializedName("bookingType")
    @Expose
    private String bookingType;
    @SerializedName("isFreeCancellation")
    @Expose
    private boolean isFreeCancellation = false;
    @SerializedName("title")
    @Expose
    private String title ="";
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("startingAmount")
    @Expose
    private Integer startingAmount;

    @SerializedName("tourId")
    @Expose
    private String tourId;

    @SerializedName("contractId")
    @Expose
    private String contractId;

    @SerializedName("discount")
    @Expose
    private Integer discount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return Utils.notNullString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBadge() {
        return badge;
    }

    public double getAvg_ratings() {
        return avg_ratings;
    }
    public void setBadge(String badge) {
        this.badge = badge;
    }

    public boolean isTicketRecentlyAdded(){
        if (tags == null) return false;
        if (tags.isEmpty()) return false;
        for (String data : tags){
            if (data.equals("Recently added")){
                return true;
            }
        }
        return false;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<String> getImages() {
        return images == null ? new ArrayList<>() : images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public boolean isFreeCancellation() {
        return isFreeCancellation;
    }

    public void setFreeCancellation(boolean freeCancellation) {
        isFreeCancellation = freeCancellation;
    }

    public String getTitle() {
        return Utils.notNullString( title );
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getStartingAmount() {
        return startingAmount;
    }

    public void setStartingAmount(Integer startingAmount) {
        this.startingAmount = startingAmount;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
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
