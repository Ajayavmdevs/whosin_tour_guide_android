package com.whosin.business.service.models.JuniperHotelModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

import java.util.List;

public class JpHotelBookingRuleModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("status")
    @Expose
    public String status = "";
    @SerializedName("BookingCode")
    @Expose
    public String bookingCode = "";
    @SerializedName("ExpirationDate")
    @Expose
    public String expirationDate = "";
    @SerializedName("hotelBookingRequiredFields")
    @Expose
    public JpHotelHotelBookingRequiredFieldsModel hotelBookingRequiredFields;
    @SerializedName("cancellationPolicy")
    @Expose
    public JPHotelCancellationPolicyModel cancellationPolicy;
    @SerializedName("priceInformation")
    @Expose
    public List<JpHotelPriceInformationModel> priceInformation;


    public String getStatus() {
        return Utils.notNullString(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookingCode() {
        return Utils.notNullString(bookingCode);
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getExpirationDate() {
        return Utils.notNullString(expirationDate);
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public JpHotelHotelBookingRequiredFieldsModel getHotelBookingRequiredFields() {
        return hotelBookingRequiredFields;
    }

    public void setHotelBookingRequiredFields(JpHotelHotelBookingRequiredFieldsModel hotelBookingRequiredFields) {
        this.hotelBookingRequiredFields = hotelBookingRequiredFields;
    }

    public JPHotelCancellationPolicyModel getCancellationPolicy() {
        return cancellationPolicy;
    }

    public void setCancellationPolicy(JPHotelCancellationPolicyModel cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public List<JpHotelPriceInformationModel> getPriceInformation() {
        return Utils.notEmptyList(priceInformation);
    }

    public void setPriceInformation(List<JpHotelPriceInformationModel> priceInformation) {
        this.priceInformation = priceInformation;
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
