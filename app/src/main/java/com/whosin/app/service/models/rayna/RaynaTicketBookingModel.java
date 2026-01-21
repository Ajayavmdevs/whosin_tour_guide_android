package com.whosin.app.service.models.rayna;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.HomeTicketsModel;
import com.whosin.app.service.models.JuniperHotelModels.JPPassengerModel;
import com.whosin.app.service.models.ModelProtocol;

import java.util.List;

public class RaynaTicketBookingModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("uniqueNo")
    @Expose
    private String uniqueNo;

    @SerializedName("details")
    @Expose
    private List<RaynaTicketDownloadModel> details;

    @SerializedName("bookingType")
    @Expose
    private String bookingType;

    @SerializedName("referenceNo")
    @Expose
    private String referenceNo;

    @SerializedName("bookingCode")
    @Expose
    private String bookingCode;

    @SerializedName("paymentStatus")
    @Expose
    private String paymentStatus;

    @SerializedName("bookingStatus")
    @Expose
    private String bookingStatus;

    @SerializedName("cancelledTime")
    @Expose
    private String cancelledTime;

    @SerializedName("cancellationPolicy")
    @Expose
    private List<TourOptionsModel> cancellationPolicy;

    @SerializedName("TourDetails")
    @Expose
    private List<RaynaTourDetailModel> TourDetails;

    @SerializedName("passengers")
    @Expose
    private List<JPPassengerModel> passengers;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("totalAmount")
    @Expose
    private float totalAmount = 0;

    @SerializedName("discount")
    @Expose
    private float discount = 0;

    @SerializedName("amount")
    @Expose
    private float amount = 0;

    @SerializedName("departureTime")
    @Expose
    private String departureTime;

    @SerializedName("downloadTicket")
    @Expose
    private String downloadTicket;

    @SerializedName("customData")
    @Expose
    private HomeTicketsModel customData;

    public String getBookingCode() {
        return Utils.notNullString(bookingCode);
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUniqueNo() {
        return uniqueNo;
    }

    public void setUniqueNo(String uniqueNo) {
        this.uniqueNo = uniqueNo;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getPaymentStatus() {
        return Utils.notNullString(paymentStatus);
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getBookingStatus() {
        return Utils.notNullString(bookingStatus);
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getCancelledTime() {
        return cancelledTime;
    }

    public void setCancelledTime(String cancelledTime) {
        this.cancelledTime = cancelledTime;
    }

    public List<TourOptionsModel> getCancellationPolicy() {
        return Utils.notEmptyList(cancellationPolicy);
    }

    public void setCancellationPolicy(List<TourOptionsModel> cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public List<RaynaTourDetailModel> getTourDetails() {
        return Utils.notEmptyList(TourDetails);
    }

    public void setTourDetails(List<RaynaTourDetailModel> tourDetails) {
        TourDetails = tourDetails;
    }

    public List<JPPassengerModel> getPassengers() {
        return Utils.notEmptyList(passengers);
    }

    public void setPassengers(List<JPPassengerModel> passengers) {
        this.passengers = passengers;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }


    public float getTotalAmount() {
//        return Utils.roundFloatToFloat(totalAmount);
        return Utils.convertIntoCurrenctCurrency(totalAmount);
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public float getDiscount() {
        return Utils.roundFloatToFloat(discount);
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getAmount() {
//        return Utils.roundFloatToFloat(amount);
        return Utils.convertIntoCurrenctCurrency(amount);
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public List<RaynaTicketDownloadModel> getDetails() {
        return Utils.notEmptyList(details);
    }

    public void setDetails(List<RaynaTicketDownloadModel> details) {
        this.details = details;
    }

    public String getDepartureTime() {
        return Utils.notNullString(departureTime);
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getDownloadTicket() {
        return Utils.notNullString(downloadTicket);
    }

    public void setDownloadTicket(String downloadTicket) {
        this.downloadTicket = downloadTicket;
    }

    public HomeTicketsModel getCustomData() {
        return customData;
    }

    public void setCustomData(HomeTicketsModel customData) {
        this.customData = customData;
    }
}
