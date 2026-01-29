package com.whosin.business.service.models.myCartModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;
import com.whosin.business.service.models.rayna.RaynaPassengerModel;

import java.util.List;

public class MyCartItemsModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String _id = "";

    @SerializedName("userId")
    @Expose
    private String userId = "";

    @SerializedName("bookingType")
    @Expose
    private String bookingType = "";

    @SerializedName("totalAmount")
    @Expose
    private float totalAmount;

    @SerializedName("discount")
    @Expose
    private float discount;

    @SerializedName("amount")
    @Expose
    private float amount;

    @SerializedName("currency")
    @Expose
    private String currency = "";

    @SerializedName("customTicketId")
    @Expose
    private String customTicketId = "";

    @SerializedName("departureTime")
    @Expose
    private String departureTime = "";


    @SerializedName("TourDetails")
    @Expose
    private List<MyCartTourDetailsModel> tourDetails;

    @SerializedName("passengers")
    @Expose
    private List<RaynaPassengerModel> passengerModel;

    @SerializedName("cancellationPolicy")
    @Expose
    private List<MyCartCancellationModel> cancellationPolicy;

    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";


    public String get_id() {
        return Utils.notNullString(_id);
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return Utils.notNullString(userId);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookingType() {
        return Utils.notNullString(bookingType);
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return Utils.notNullString(currency);
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCustomTicketId() {
        return Utils.notNullString(customTicketId);
    }

    public void setCustomTicketId(String customTicketId) {
        this.customTicketId = customTicketId;
    }

    public String getDepartureTime() {
        return Utils.notNullString(departureTime);
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public List<MyCartTourDetailsModel> getTourDetails() {
        return Utils.notEmptyList(tourDetails);
    }

    public void setTourDetails(List<MyCartTourDetailsModel> tourDetails) {
        this.tourDetails = tourDetails;
    }

    public List<MyCartCancellationModel> getCancellationPolicy() {
        return Utils.notEmptyList(cancellationPolicy);
    }

    public void setCancellationPolicy(List<MyCartCancellationModel> cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public String getCreatedAt() {
        return Utils.notNullString(createdAt);
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<RaynaPassengerModel> getPassengerModel() {
        return Utils.notEmptyList(passengerModel);
    }

    public void setPassengerModel(List<RaynaPassengerModel> passengerModel) {
        this.passengerModel = passengerModel;
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
