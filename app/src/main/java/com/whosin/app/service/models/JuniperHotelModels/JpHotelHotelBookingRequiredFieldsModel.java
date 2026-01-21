package com.whosin.app.service.models.JuniperHotelModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

import java.util.List;

public class JpHotelHotelBookingRequiredFieldsModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("bookingCode")
    @Expose
    public String bookingCode;
    @SerializedName("hotelCode")
    @Expose
    public String hotelCode;
    @SerializedName("startDate")
    @Expose
    public String startDate;
    @SerializedName("endDate")
    @Expose
    public String endDate;
    @SerializedName("priceRange")
    @Expose
    public JPHotelPriceRangeModel priceRange;
    @SerializedName("relPaxesDist")
    @Expose
    public List<JpHotelRelPaxesDistModel> relPaxesDist;

    public String getBookingCode() {
        return Utils.notNullString(bookingCode);
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getHotelCode() {
        return Utils.notNullString(hotelCode);
    }

    public void setHotelCode(String hotelCode) {
        this.hotelCode = hotelCode;
    }

    public String getStartDate() {
        return Utils.notNullString(startDate);
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return Utils.notNullString(endDate);
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public JPHotelPriceRangeModel getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(JPHotelPriceRangeModel priceRange) {
        this.priceRange = priceRange;
    }

    public List<JpHotelRelPaxesDistModel> getRelPaxesDist() {
        return Utils.notEmptyList(relPaxesDist);
    }

    public void setRelPaxesDist(List<JpHotelRelPaxesDistModel> relPaxesDist) {
        this.relPaxesDist = relPaxesDist;
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
